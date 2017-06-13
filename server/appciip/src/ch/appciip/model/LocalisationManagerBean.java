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
	 * Implémentation de la méthode définie dans l'interface LocalisationManager
	 */
	@Override
	public Localisation read(Localisation localisation) throws DatabaseException {

		if (localisation.getId() != null)
			return read(SQL_SELECT_PER_ID, localisation.getId());

		return read(SQL_SELECT_PER_CONTENT, localisation.getContent());
	}

	/*
	 * Méthode générique utilisée pour retourner un utilisateur depuis la base
	 * de données, correspondant à la requête SQL donnée prenant en paramètres
	 * les objets passés en argument.
	 */
	private Localisation read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Localisation localisation = null;

		try {
			/* Récupération d'une connexion depuis la Factory */
			connexion = factory.getConnection();

			/*
			 * Préparation de la requête avec les objets passés en arguments et
			 * exécution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, objets);
			resultSet = preparedStatement.executeQuery();

			/* Parcours de la ligne de données retournée dans le ResultSet */
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
	 * Implémentation de la méthode définie dans l'interface LocalisationManager
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
						"Échec de la suppression de la localisation, aucune ligne supprimée de la table.");
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
	 * Implémentation de la méthode définie dans l'interface LocalisationManager
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
	 * Simple méthode utilitaire permettant de faire la correspondance (le
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
						"Échec de la création de la localisation, aucune ligne ajoutée dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				localisation = read(new Localisation((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"Échec de la création du localisation en base, aucun ID auto-généré retourné.");
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
