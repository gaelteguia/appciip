package ch.appciip.model;

import static ch.appciip.model.Utility.fermeturesSilencieuses;
import static ch.appciip.model.Utility.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.appciip.bean.Canton;

public class CantonManagerBean implements CantonManager {
	private static final String SQL_SELECT_PAR_ACRONYME = "SELECT * FROM canton WHERE acronym =?";
	private static final String SQL_SELECT = "SELECT * FROM canton";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM canton WHERE id_canton = ?";
	private static final String SQL_INSERT = "INSERT INTO canton (acronym,name,description,website,statut,officiel,id_parent,id_responsable) VALUES(UPPER(?),?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE canton SET acronym=UPPER(?),name=?,description=?,website=?,statut=?,officiel=?,id_parent=?,id_responsable=? WHERE id_canton =?";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Comite WHERE id_canton = ?";
	private Factory factory;

	public CantonManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Canton read(Canton canton) throws DatabaseException {
		if (canton.getId() != null)
			return read(SQL_SELECT_PAR_ID, canton.getId());
		return read(SQL_SELECT_PAR_ACRONYME, canton.getAcronym());
	}

	private Canton read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Canton canton = null;

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en
			 * arguments et ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, objets);
			resultSet = preparedStatement.executeQuery();
			/*
			 * Parcours de la ligne de donn�es retourn�e dans le ResultSet
			 */
			if (resultSet.next()) {
				canton = map(resultSet);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return canton;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface ComiteManager
	 */
	@Override
	public List<Canton> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Canton> clients = new ArrayList<Canton>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Canton canton = map(resultSet);
				// canton = consulterSeancesComite(canton);
				// canton = consulterCollaborateursComite(canton);
				clients.add(canton);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return clients;
	}

	@Override
	public void update(Canton canton) throws DatabaseException {

		update(SQL_UPDATE, canton.getAcronym(), canton.getName(), canton.getDescription(), canton.getWebsite(),
				canton.getId());

	}

	@Override
	public Canton create(Canton canton) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, canton.getAcronym(),
					canton.getName(), canton.getDescription(), canton.getWebsite());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation du canton d'acc�s, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				canton = read(new Canton((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du canton d'acc�s en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return canton;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des cantons (un ResultSet) et
	 * un bean Comite.
	 */
	private Canton map(ResultSet resultSet) throws SQLException {
		Canton canton = new Canton();
		canton.setId(resultSet.getLong("id_canton"));
		canton.setAcronym(resultSet.getString("acronym"));
		canton.setName(resultSet.getString("name"));
		canton.setDescription(resultSet.getString("description"));
		canton.setWebsite(resultSet.getString("website"));

		return canton;
	}

	@Override
	public void delete(Canton canton) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, canton.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du canton d'acc�s, aucune ligne supprim�e de la table.");
			} else {
				canton.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	private void update(String sql, Object... objets) throws DatabaseException {
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
