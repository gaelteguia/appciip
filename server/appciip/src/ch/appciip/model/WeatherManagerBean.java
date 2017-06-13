package ch.appciip.model;

import static ch.appciip.model.Utility.fermeturesSilencieuses;
import static ch.appciip.model.Utility.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.appciip.bean.Weather;

public class WeatherManagerBean implements WeatherManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM weather WHERE content =?";
	private static final String SQL_SELECT = "SELECT * FROM weather";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM weather WHERE id_weather = ?";
	private static final String SQL_INSERT = "INSERT INTO weather (content,description,id_contact_technique,id_contact_fonctionnel) VALUES(UPPER(?),?,?,?)";
	private static final String SQL_UPDATE = "UPDATE weather SET content=UPPER(?),description=?,id_contact_technique=?,id_contact_fonctionnel=? WHERE id_weather =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Weather WHERE id_weather = ?";

	private Factory factory;

	public WeatherManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Weather read(Weather weather) throws DatabaseException {
		if (weather.getId() != null)
			return read(SQL_SELECT_PAR_ID, weather.getId());
		return read(SQL_SELECT_PAR_NOM, weather.getContent());
	}

	private Weather read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Weather weather = null;

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
				weather = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return weather;
	}

	@Override
	public void delete(Weather weather) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, weather.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de l'weather, aucune ligne supprim�e de la table.");
			} else {
				weather.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface WeatherManager
	 */
	@Override
	public List<Weather> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Weather> clients = new ArrayList<Weather>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				clients.add(map(resultSet));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return clients;
	}

	@Override
	public void update(Weather weather) throws DatabaseException {

		update(SQL_UPDATE, weather.getContent(), weather.getId());
	}

	@Override
	public Weather create(Weather weather) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, weather.getContent());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation de l'weather, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				weather = read(new Weather((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation de l'weather en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return weather;
	}

	private Weather map(ResultSet resultSet) throws SQLException {
		Weather weather = new Weather();
		weather.setId(resultSet.getLong("id_weather"));

		weather.setContent(resultSet.getString("content"));

		return weather;
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
