package ch.appciip.model;

import static ch.appciip.model.Utility.fermeturesSilencieuses;
import static ch.appciip.model.Utility.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import ch.appciip.bean.Query;
import ch.appciip.bean.User;

public class QueryManagerBean implements QueryManager {

	private static final String SQL_SELECT = "SELECT * FROM query WHERE echeance > CURRENT_DATE()";
	private static final String SQL_SELECT_PER_ID = "SELECT * FROM query WHERE id_query = ?";
	private static final String SQL_SELECT_PER_CONTENT = "SELECT * FROM query WHERE titre =?";
	private static final String SQL_INSERT = "INSERT INTO query (titre,contenu,date,echeance,id_auteur) VALUES(UPPER(?),?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE query SET titre=UPPER(?),contenu=?,date=?,echeance=?,id_auteur=? WHERE id_query =?";
	private static final String SQL_DELETE_PER_ID = "DELETE FROM Query WHERE id_query = ?";
	private Factory factory;

	QueryManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface QueryManager
	 */
	@Override
	public Query read(Query query) throws DatabaseException {

		if (query.getId() != null)
			return read(SQL_SELECT_PER_ID, query.getId());
		return read(SQL_SELECT_PER_CONTENT, query.getContent());

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface QueryManager
	 */

	@Override
	public Query create(Query query) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		boolean b = false;

		Timestamp date = null;
		if (query.getDate() != null)
			date = new Timestamp(query.getDate().getMillis());

		Timestamp echeance = null;
		if (query.getEcheance() != null)
			echeance = new Timestamp(query.getEcheance().getMillis());

		Long idAuteur = null;
		if (query.getAuteur() != null) {
			idAuteur = query.getAuteur().getId();
			UserManager u = factory.getUserManager();
			User ut = u.read(query.getAuteur());

			Iterator<Query> it = ut.getQueries().iterator();
			while (it.hasNext()) {
				Query o = (Query) it.next();
				if (o.getContent().equals(query.getContent()))
					b = true;
			}

		}

		if (!b) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, query.getContent(),
						query.getContenu(),

						date, echeance, idAuteur);
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException(
							"�chec de la cr�ation de la query, aucune ligne ajout�e dans la table.");
				}
				valeursAutoGenerees = preparedStatement.getGeneratedKeys();
				if (valeursAutoGenerees.next()) {

					query = read(new Query((valeursAutoGenerees.getLong(1))));

				} else {
					throw new DatabaseException(
							"�chec de la cr�ation de la query en base, aucun ID auto-g�n�r� retourn�.");
				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		} else {
			throw new DatabaseException(
					"�chec de la cr�ation de la query en base, aucun ID auto-g�n�r� retourn�.");
		}

		return query;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un query depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en
	 * param�tres les objets pass�s en argument.
	 */

	private Query read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Query query = null;

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
				query = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		/*
		 * if (query == null) throw new DatabaseException(
		 * "�chec de la recherche de la query en base, aucune query retourn�e."
		 * );
		 */
		return query;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface QueryManager
	 */
	@Override
	public void delete(Query query) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PER_ID, true, query.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du client, aucune ligne supprim�e de la table.");
			} else {
				query.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface QueryManager
	 */
	@Override
	public List<Query> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Query> clients = new ArrayList<Query>();

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
	 * mapping) entre une ligne issue de la table des querys (un ResultSet) et
	 * un bean Query.
	 */
	private Query map(ResultSet resultSet) throws SQLException {
		Query query = new Query();
		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			query.setDate(new DateTime(date));

		Timestamp echeance = resultSet.getTimestamp("echeance");
		if (date != null)
			query.setEcheance(new DateTime(echeance));

		query.setId(resultSet.getLong("id_query"));
		query.setContent(resultSet.getString("titre"));
		query.setContenu(resultSet.getString("contenu"));

		UserManager auteur = factory.getUserManager();
		query.setAuteur(auteur.read(new User(resultSet.getLong("id_auteur"))));

		return query;
	}

	@Override
	public void update(Query query) throws DatabaseException {
		Timestamp date = null;
		if (query.getDate() != null)
			date = new Timestamp(query.getDate().getMillis());
		Long idAuteur = null;

		if (query.getAuteur() != null) {
			idAuteur = query.getAuteur().getId();

		}

		Timestamp echeance = null;
		if (query.getEcheance() != null)
			echeance = new Timestamp(query.getEcheance().getMillis());

		update(SQL_UPDATE, query.getContent(), query.getContenu(),

				date, echeance, idAuteur, query.getId());
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
