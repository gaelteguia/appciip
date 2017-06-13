package ch.appciip.model;

import static ch.appciip.model.Utility.fermeturesSilencieuses;
import static ch.appciip.model.Utility.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import ch.appciip.bean.Holiday;

public class HolidayManagerBean implements HolidayManager {

	private static final String SQL_SELECT = "SELECT * FROM holiday";
	private static final String SQL_SELECT_PER_ID = "SELECT * FROM holiday WHERE id_holiday = ?";
	private static final String SQL_SELECT_PER_TITRE = "SELECT * FROM holiday WHERE titre =?";
	private static final String SQL_INSERT = "INSERT INTO holiday (titre,description,date,id_adresse,id_user) VALUES(UPPER(?),?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE holiday SET titre=UPPER(?),description=?,date=?,id_adresse=?,id_user=? WHERE id_holiday =?";
	private static final String SQL_DELETE_PER_ID = "DELETE FROM Holiday WHERE id_holiday = ?";
	private Factory factory;

	HolidayManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * HolidayManager
	 */
	@Override
	public Holiday read(Holiday holiday) throws DatabaseException {

		if (holiday.getId() != null)
			return read(SQL_SELECT_PER_ID, holiday.getId());
		return read(SQL_SELECT_PER_TITRE, holiday.getTitle());

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * HolidayManager
	 */

	@Override
	public Holiday create(Holiday holiday) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		boolean b = false;

		Timestamp date = null;
		if (holiday.getStartTime() != null)
			date = new Timestamp(holiday.getStartTime().getMillis());

		if (!b) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, holiday.getTitle(),
						holiday.getDescription(),

						date);
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException(
							"�chec de la cr�ation de la holiday, aucune ligne ajout�e dans la table.");
				}
				valeursAutoGenerees = preparedStatement.getGeneratedKeys();
				if (valeursAutoGenerees.next()) {

					holiday = read(new Holiday((valeursAutoGenerees.getLong(1))));

				} else {
					throw new DatabaseException(
							"�chec de la cr�ation de la holiday en base, aucun ID auto-g�n�r� retourn�.");
				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		} else {
			throw new DatabaseException(
					"�chec de la cr�ation de la holiday en base, aucun ID auto-g�n�r� retourn�.");
		}

		return holiday;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un holiday depuis la
	 * base de donn�es, correspondant � la requ�te SQL donn�e prenant en
	 * param�tres les objets pass�s en argument.
	 */

	private Holiday read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Holiday holiday = null;

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
				holiday = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		/*
		 * if (holiday == null) throw new DatabaseException(
		 * "�chec de la recherche de la holiday en base, aucune holiday retourn�e."
		 * );
		 */
		return holiday;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * HolidayManager
	 */
	@Override
	public void delete(Holiday holiday) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PER_ID, true, holiday.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du client, aucune ligne supprim�e de la table.");
			} else {
				holiday.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * HolidayManager
	 */
	@Override
	public List<Holiday> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Holiday> clients = new ArrayList<Holiday>();

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

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des holidays (un ResultSet) et
	 * un bean Holiday.
	 */
	private Holiday map(ResultSet resultSet) throws SQLException {
		Holiday holiday = new Holiday();
		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			holiday.setStartTime(new DateTime(date));
		holiday.setId(resultSet.getLong("id_holiday"));
		holiday.setTitle(resultSet.getString("titre"));
		holiday.setDescription(resultSet.getString("description"));

		return holiday;
	}

	@Override
	public void update(Holiday holiday) throws DatabaseException {
		Timestamp date = null;
		if (holiday.getStartTime() != null)
			date = new Timestamp(holiday.getStartTime().getMillis());

		update(SQL_UPDATE, holiday.getTitle(), holiday.getDescription(),

				date, holiday.getId());
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
