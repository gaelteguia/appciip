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

import beans.Breve;
import beans.Service;

public class BreveManagerBean implements BreveManager {
	private static final String SQL_SELECT_PAR_TITRE = "SELECT * FROM breve WHERE titre =?";
	private static final String SQL_SELECT = "SELECT * FROM breve";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM breve WHERE id_breve = ?";
	private static final String SQL_INSERT = "INSERT INTO breve (titre,type,description,lien,date,id_service) VALUES(UPPER(?),?,?,?,?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM breve WHERE id_breve = ?";
	private static final String SQL_UPDATE = "UPDATE breve SET titre=UPPER(?),type=?,description=?,lien=?,date=?,id_service=? WHERE id_breve =?";

	private Factory factory;

	public BreveManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface BreveManager */
	@Override
	public Breve retrouver(Breve breve) throws DatabaseException {

		if (breve.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, breve.getId());

		return retrouver(SQL_SELECT_PAR_TITRE, breve.getTitre());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Breve retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Breve breve = null;

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
				breve = map(resultSet);
				// breve = consulterServicesBreve(breve);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return breve;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface BreveManager */
	@Override
	public void supprimer(Breve breve) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, breve.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression de la breve, aucune ligne supprim�e de la table.");
			} else {

				breve.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface BreveManager */
	@Override
	public List<Breve> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Breve> breves = new ArrayList<Breve>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Breve breve = map(resultSet);
				// breve = consulterServicesBreve(breve);
				breves.add(breve);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return breves;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des breves (un ResultSet) et
	 * un bean Breve.
	 */
	private Breve map(ResultSet resultSet) throws SQLException {
		Breve breve = new Breve();

		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			breve.setDate(new DateTime(date));

		breve.setId(resultSet.getLong("id_breve"));
		breve.setTitre(resultSet.getString("titre"));
		breve.setType(resultSet.getString("type"));
		breve.setDescription(resultSet.getString("description"));
		breve.setLien(resultSet.getString("lien"));
		ServiceManager service = factory.getServiceManager();

		breve.setService(service.retrouver(new Service(resultSet.getLong("id_service"))));
		return breve;
	}

	@Override
	public void modifier(Breve breve) throws DatabaseException {

		Long idService = null;
		if (breve.getService() != null)
			idService = breve.getService().getId();

		Timestamp date = null;
		if (breve.getDate() != null)
			date = new Timestamp(breve.getDate().getMillis());

		modifier(SQL_UPDATE, breve.getTitre(), breve.getType(), breve.getDescription(), breve.getLien(), date,
				idService,

				breve.getId());

	}

	@Override
	public Breve creer(Breve breve) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idService = null;
		if (breve.getService() != null)
			idService = breve.getService().getId();

		Timestamp date = null;
		if (breve.getDate() != null)
			date = new Timestamp(breve.getDate().getMillis());

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, breve.getTitre(),
					breve.getType(), breve.getDescription(), breve.getLien(), date, idService);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation de la breve, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				breve = retrouver(new Breve((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException("�chec de la cr�ation du breve en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return breve;
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
