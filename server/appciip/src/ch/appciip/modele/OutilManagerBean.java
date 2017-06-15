package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Outil;
import beans.Service;

public class OutilManagerBean implements OutilManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM outil WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM outil";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM outil WHERE id_outil = ?";
	private static final String SQL_INSERT = "INSERT INTO outil (nom,type,description,lien,id_service) VALUES(UPPER(?),?,?,?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM outil WHERE id_outil = ?";
	private static final String SQL_UPDATE = "UPDATE outil SET nom=UPPER(?),type=?,description=?,id_service=? WHERE id_outil =?";

	private Factory factory;

	public OutilManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface OutilManager */
	@Override
	public Outil retrouver(Outil outil) throws DatabaseException {

		if (outil.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, outil.getId());

		return retrouver(SQL_SELECT_PAR_NOM, outil.getNom());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Outil retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Outil outil = null;

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
				outil = map(resultSet);
				// outil = consulterServicesOutil(outil);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return outil;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface OutilManager */
	@Override
	public void supprimer(Outil outil) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, outil.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression de la outil, aucune ligne supprim�e de la table.");
			} else {

				outil.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface OutilManager */
	@Override
	public List<Outil> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Outil> outils = new ArrayList<Outil>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Outil outil = map(resultSet);
				// outil = consulterServicesOutil(outil);
				outils.add(outil);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return outils;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des outils (un ResultSet) et
	 * un bean Outil.
	 */
	private Outil map(ResultSet resultSet) throws SQLException {
		Outil outil = new Outil();
		outil.setId(resultSet.getLong("id_outil"));
		outil.setNom(resultSet.getString("nom"));
		outil.setType(resultSet.getString("type"));
		outil.setDescription(resultSet.getString("description"));
		outil.setLien(resultSet.getString("lien"));
		ServiceManager service = factory.getServiceManager();

		outil.setService(service.retrouver(new Service(resultSet.getLong("id_service"))));
		return outil;
	}

	@Override
	public void modifier(Outil outil) throws DatabaseException {

		Long idService = null;
		if (outil.getService() != null)
			idService = outil.getService().getId();
		modifier(SQL_UPDATE, outil.getNom(), outil.getType(), outil.getDescription(), outil.getLien(), idService,

				outil.getId());

	}

	@Override
	public Outil creer(Outil outil) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idService = null;
		if (outil.getService() != null)
			idService = outil.getService().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, outil.getNom(),
					outil.getType(), outil.getDescription(), outil.getLien(), idService);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation de la outil, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				outil = retrouver(new Outil((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException("�chec de la cr�ation du outil en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return outil;
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
