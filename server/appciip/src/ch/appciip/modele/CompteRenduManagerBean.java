package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import beans.Collaborateur;
import beans.CompteRendu;
import beans.Service;

public class CompteRenduManagerBean implements CompteRenduManager {
	private static final String SQL_SELECT_PAR_TITRE = "SELECT * FROM compte_rendu WHERE titre =?";
	private static final String SQL_SELECT = "SELECT * FROM compte_rendu";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM compte_rendu WHERE id_compte_rendu = ?";
	private static final String SQL_INSERT = "INSERT INTO compte_rendu (titre,type,description,lien,date,id_collaborateur,id_service) VALUES(UPPER(?),?,?,?,?,?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM compte_rendu WHERE id_compte_rendu = ?";
	private static final String SQL_UPDATE = "UPDATE compte_rendu SET titre=UPPER(?),type=?,description=?,lien=?,date=?,id_collaborateur=?,id_service=? WHERE id_compte_rendu =?";

	private Factory factory;

	public CompteRenduManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface CompteRenduManager
	 */
	@Override
	public CompteRendu retrouver(CompteRendu compteRendu) throws DatabaseException {

		if (compteRendu.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, compteRendu.getId());

		return retrouver(SQL_SELECT_PAR_TITRE, compteRendu.getTitre());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private CompteRendu retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		CompteRendu compteRendu = null;

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();

			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, objets);
			resultSet = preparedStatement.executeQuery();

			/* Parcours de la ligne de donn�es retourn�e dans le ResultSet */
			if (resultSet.next()) {
				compteRendu = map(resultSet);
				// compteRendu = consulterServicesCompteRendu(compteRendu);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return compteRendu;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface CompteRenduManager
	 */
	@Override
	public void supprimer(CompteRendu compteRendu) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, compteRendu.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de la compteRendu, aucune ligne supprim�e de la table.");
			} else {

				compteRendu.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface CompteRenduManager
	 */
	@Override
	public List<CompteRendu> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<CompteRendu> compteRendus = new ArrayList<CompteRendu>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				CompteRendu compteRendu = map(resultSet);
				// compteRendu = consulterServicesCompteRendu(compteRendu);
				compteRendus.add(compteRendu);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return compteRendus;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des compteRendus (un
	 * ResultSet) et un bean CompteRendu.
	 */
	private CompteRendu map(ResultSet resultSet) throws SQLException {
		CompteRendu compteRendu = new CompteRendu();

		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			compteRendu.setDate(new DateTime(date));

		compteRendu.setId(resultSet.getLong("id_compte_rendu"));
		compteRendu.setTitre(resultSet.getString("titre"));
		compteRendu.setType(resultSet.getString("type"));
		compteRendu.setDescription(resultSet.getString("description"));
		compteRendu.setLien(resultSet.getString("lien"));

		CollaborateurManager collaborateur = factory.getCollaborateurManager();

		compteRendu.setCollaborateur(collaborateur.retrouver(new Utilisateur(resultSet.getLong("id_collaborateur"))));

		ServiceManager service = factory.getServiceManager();

		compteRendu.setService(service.retrouver(new Service(resultSet.getLong("id_service"))));
		return compteRendu;
	}

	@Override
	public void modifier(CompteRendu compteRendu) throws DatabaseException {

		Long idCollaborateur = null;

		if (compteRendu.getCollaborateur() != null)
			idCollaborateur = compteRendu.getCollaborateur().getId();

		Long idService = null;
		if (compteRendu.getService() != null)
			idService = compteRendu.getService().getId();

		Timestamp date = null;
		if (compteRendu.getDate() != null)
			date = new Timestamp(compteRendu.getDate().getMillis());

		modifier(SQL_UPDATE, compteRendu.getTitre(), compteRendu.getType(), compteRendu.getDescription(),
				compteRendu.getLien(), date, idCollaborateur, idService,

				compteRendu.getId());

	}

	@Override
	public CompteRendu creer(CompteRendu compteRendu) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idCollaborateur = null;

		if (compteRendu.getCollaborateur() != null)
			idCollaborateur = compteRendu.getCollaborateur().getId();

		Long idService = null;
		if (compteRendu.getService() != null)
			idService = compteRendu.getService().getId();

		Timestamp date = null;
		if (compteRendu.getDate() != null)
			date = new Timestamp(compteRendu.getDate().getMillis());

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, compteRendu.getTitre(),
					compteRendu.getType(), compteRendu.getDescription(), compteRendu.getLien(), date, idCollaborateur,
					idService);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de la compteRendu, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				compteRendu = retrouver(new CompteRendu((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du compteRendu en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return compteRendu;
	}

	private void modifier(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, objets);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

	}

}
