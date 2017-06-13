package ch.appciip.model;

import static ch.appciip.model.Utility.fermeturesSilencieuses;
import static ch.appciip.model.Utility.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.appciip.bean.Localisation;

public class LocalisationManagerBean implements LocalisationManager {
	private static final String SQL_SELECT_PER_CONTENT = "SELECT * FROM localisation WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM localisation";
	private static final String SQL_SELECT_PER_ID = "SELECT * FROM localisation WHERE id_localisation = ?";
	private static final String SQL_INSERT = "INSERT INTO localisation (nom,nom_court,no_rue,npa,pays,rue,ville) VALUES(UPPER(?),?,?,?,?,?,?)";
	private static final String SQL_DELETE_PER_ID = "DELETE FROM localisation WHERE id_localisation = ?";
	private static final String SQL_UPDATE = "UPDATE localisation SET nom=UPPER(?),nom_court=?,no_rue=?,npa=?,pays=?,rue=?,ville=? WHERE id_localisation =?";

	private Factory factory;

	public LocalisationManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface LocalisationManager
	 */
	@Override
	public Localisation read(Localisation localisation) throws DatabaseException {

		if (localisation.getId() != null)
			return read(SQL_SELECT_PER_ID, localisation.getId());

		return read(SQL_SELECT_PER_CONTENT, localisation.getContent());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Localisation read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Localisation localisation = null;

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
				localisation = map(resultSet);
				// localisation = consulterServicesLocalisation(localisation);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return localisation;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface LocalisationManager
	 */
	@Override
	public void delete(Localisation localisation) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PER_ID, true, localisation.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de la localisation, aucune ligne supprim�e de la table.");
			} else {

				localisation.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface LocalisationManager
	 */
	@Override
	public List<Localisation> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Localisation> localisations = new ArrayList<Localisation>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Localisation localisation = map(resultSet);
				// localisation = consulterServicesLocalisation(localisation);
				localisations.add(localisation);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return localisations;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des localisations (un
	 * ResultSet) et un bean Localisation.
	 */
	private Localisation map(ResultSet resultSet) throws SQLException {
		Localisation localisation = new Localisation();
		localisation.setId(resultSet.getLong("id_localisation"));
		localisation.setContent(resultSet.getString("nom"));

		return localisation;
	}

	@Override
	public void update(Localisation localisation) throws DatabaseException {
		update(SQL_UPDATE, localisation.getContent(), localisation.getId());

	}

	@Override
	public Localisation create(Localisation localisation) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, localisation.getContent());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de la localisation, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				localisation = read(new Localisation((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du localisation en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return localisation;
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
