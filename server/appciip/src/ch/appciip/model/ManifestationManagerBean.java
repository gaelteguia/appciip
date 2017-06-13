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

import ch.appciip.bean.Address;
import ch.appciip.bean.Institution;
import ch.appciip.bean.Manifestation;

public class ManifestationManagerBean implements ManifestationManager {

	private static final String SQL_SELECT = "SELECT * FROM manifestation";
	private static final String SQL_SELECT_PER_ID = "SELECT * FROM manifestation WHERE id_manifestation = ?";
	private static final String SQL_SELECT_PER_TITLE = "SELECT * FROM manifestation WHERE title =?";
	private static final String SQL_INSERT = "INSERT INTO manifestation (title,description,date,id_adresse,id_user) VALUES(UPPER(?),?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE manifestation SET title=UPPER(?),description=?,date=?,id_adresse=?,id_user=? WHERE id_manifestation =?";
	private static final String SQL_DELETE_PER_ID = "DELETE FROM Manifestation WHERE id_manifestation = ?";
	private Factory factory;

	ManifestationManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * ManifestationManager
	 */
	@Override
	public Manifestation read(Manifestation manifestation) throws DatabaseException {

		if (manifestation.getId() != null)
			return read(SQL_SELECT_PER_ID, manifestation.getId());
		
		return read(SQL_SELECT_PER_TITLE, manifestation.getTitle());

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * ManifestationManager
	 */

	@Override
	public Manifestation create(Manifestation manifestation) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		boolean b = false;

		Timestamp date = null;
		if (manifestation.getStartTime() != null)
			date = new Timestamp(manifestation.getStartTime().getMillis());

		Long idInstitution = null;
		if (manifestation.getInstitution() != null) {
			idInstitution = manifestation.getInstitution().getId();
			InstitutionManager i = factory.getInstitutionManager();
			Institution ut = i.read(manifestation.getInstitution());

			Iterator<Manifestation> it = ut.getManifestations().iterator();
			while (it.hasNext()) {
				Manifestation o = (Manifestation) it.next();
				if (o.getTitle().equals(manifestation.getTitle()))
					b = true;
			}

		}

		Long idAddress = null;
		if (manifestation.getAddress() != null) {
			idAddress = manifestation.getAddress().getId();
			AddressManager u = factory.getAddressManager();
			Address ut = u.read(manifestation.getAddress());
			if (ut != null) {
				Iterator<Manifestation> it = ut.getManifestations().iterator();
				while (it.hasNext()) {
					Manifestation o = (Manifestation) it.next();
					if (o.getTitle().equals(manifestation.getTitle()))
						b = true;
				}
			}

		}

		if (!b) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, manifestation.getTitle(),
						manifestation.getDescription(),

						date, idAddress, idInstitution);
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException(
							"�chec de la cr�ation de la manifestation, aucune ligne ajout�e dans la table.");
				}
				valeursAutoGenerees = preparedStatement.getGeneratedKeys();
				if (valeursAutoGenerees.next()) {

					manifestation = read(new Manifestation((valeursAutoGenerees.getLong(1))));

				} else {
					throw new DatabaseException(
							"�chec de la cr�ation de la manifestation en base, aucun ID auto-g�n�r� retourn�.");
				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		} else {
			throw new DatabaseException(
					"�chec de la cr�ation de la manifestation en base, aucun ID auto-g�n�r� retourn�.");
		}

		return manifestation;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un manifestation depuis
	 * la base de donn�es, correspondant � la requ�te SQL donn�e prenant
	 * en param�tres les objets pass�s en argument.
	 */

	private Manifestation read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Manifestation manifestation = null;

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
				manifestation = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		/*
		 * if (manifestation == null) throw new DatabaseException(
		 * "�chec de la recherche de la manifestation en base, aucune manifestation retourn�e."
		 * );
		 */
		return manifestation;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * ManifestationManager
	 */
	@Override
	public void delete(Manifestation manifestation) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PER_ID, true,
					manifestation.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du manifestation, aucune ligne supprim�e de la table.");
			} else {
				manifestation.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * ManifestationManager
	 */
	@Override
	public List<Manifestation> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Manifestation> manifestations = new ArrayList<Manifestation>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				manifestations.add(map(resultSet));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return manifestations;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des manifestations (un
	 * ResultSet) et un bean Manifestation.
	 */
	private Manifestation map(ResultSet resultSet) throws SQLException {
		Manifestation manifestation = new Manifestation();
		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			manifestation.setStartTime(new DateTime(date));
		manifestation.setId(Long.toString(resultSet.getLong("id_manifestation")));
		manifestation.setTitle(resultSet.getString("title"));
		manifestation.setDescription(resultSet.getString("description"));

		AddressManager adresse = factory.getAddressManager();
		manifestation.setAddress(adresse.read(new Address(resultSet.getLong("id_adresse"))));

		InstitutionManager user = factory.getInstitutionManager();
		manifestation.setInstitution(user.read(new Institution(resultSet.getLong("id_user"))));

		return manifestation;
	}

	@Override
	public void update(Manifestation manifestation) throws DatabaseException {
		Timestamp date = null;
		if (manifestation.getStartTime() != null)
			date = new Timestamp(manifestation.getStartTime().getMillis());
		Long idInstitution = null;
		if (manifestation.getInstitution() != null)
			idInstitution = manifestation.getInstitution().getId();

		Long idAddress = null;
		if (manifestation.getAddress() != null)
			idAddress = manifestation.getAddress().getId();

		update(SQL_UPDATE, manifestation.getTitle(), manifestation.getDescription(),

				date, idAddress, idInstitution, manifestation.getId());
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
