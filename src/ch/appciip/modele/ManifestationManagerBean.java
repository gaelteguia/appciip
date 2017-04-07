package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import beans.Adresse;
import beans.Collaborateur;
import beans.Manifestation;

public class ManifestationManagerBean implements ManifestationManager {

	private static final String SQL_SELECT = "SELECT * FROM manifestation";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM manifestation WHERE id_manifestation = ?";
	private static final String SQL_SELECT_PAR_TITRE = "SELECT * FROM manifestation WHERE titre =?";
	private static final String SQL_INSERT = "INSERT INTO manifestation (titre,description,date,id_adresse,id_collaborateur) VALUES(UPPER(?),?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE manifestation SET titre=UPPER(?),description=?,date=?,id_adresse=?,id_collaborateur=? WHERE id_manifestation =?";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Manifestation WHERE id_manifestation = ?";
	private Factory factory;

	ManifestationManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * ManifestationManager
	 */
	@Override
	public Manifestation retrouver(Manifestation manifestation) throws DatabaseException {

		if (manifestation.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, manifestation.getId());
		return retrouver(SQL_SELECT_PAR_TITRE, manifestation.getTitre());

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * ManifestationManager
	 */

	@Override
	public Manifestation creer(Manifestation manifestation) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		boolean b = false;

		Timestamp date = null;
		if (manifestation.getDate() != null)
			date = new Timestamp(manifestation.getDate().getMillis());

		Long idCollaborateur = null;
		if (manifestation.getCollaborateur() != null) {
			idCollaborateur = manifestation.getCollaborateur().getId();
			CollaborateurManager u = factory.getCollaborateurManager();
			Utilisateur ut = u.retrouver(manifestation.getCollaborateur());

			Iterator<Manifestation> it = ut.getManifestations().iterator();
			while (it.hasNext()) {
				Manifestation o = (Manifestation) it.next();
				if (o.getTitre().equals(manifestation.getTitre()))
					b = true;
			}

		}

		Long idAdresse = null;
		if (manifestation.getAdresse() != null) {
			idAdresse = manifestation.getAdresse().getId();
			AdresseManager u = factory.getAdresseManager();
			Adresse ut = u.retrouver(manifestation.getAdresse());
			if (ut != null) {
				Iterator<Manifestation> it = ut.getManifestations().iterator();
				while (it.hasNext()) {
					Manifestation o = (Manifestation) it.next();
					if (o.getTitre().equals(manifestation.getTitre()))
						b = true;
				}
			}

		}

		if (!b) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, manifestation.getTitre(),
						manifestation.getDescription(),

						date, idAdresse, idCollaborateur);
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException(
							"�chec de la cr�ation de la manifestation, aucune ligne ajout�e dans la table.");
				}
				valeursAutoGenerees = preparedStatement.getGeneratedKeys();
				if (valeursAutoGenerees.next()) {

					manifestation = retrouver(new Manifestation((valeursAutoGenerees.getLong(1))));

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
	 * M�thode g�n�rique utilis�e pour retourner un manifestation depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */

	private Manifestation retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Manifestation manifestation = null;

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
	public void supprimer(Manifestation manifestation) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true,
					manifestation.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression du client, aucune ligne supprim�e de la table.");
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
	public List<Manifestation> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Manifestation> clients = new ArrayList<Manifestation>();

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
	 * mapping) entre une ligne issue de la table des manifestations (un
	 * ResultSet) et un bean Manifestation.
	 */
	private Manifestation map(ResultSet resultSet) throws SQLException {
		Manifestation manifestation = new Manifestation();
		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			manifestation.setDate(new DateTime(date));
		manifestation.setId(resultSet.getLong("id_manifestation"));
		manifestation.setTitre(resultSet.getString("titre"));
		manifestation.setDescription(resultSet.getString("description"));

		AdresseManager adresse = factory.getAdresseManager();
		manifestation.setAdresse(adresse.retrouver(new Adresse(resultSet.getLong("id_adresse"))));

		CollaborateurManager collaborateur = factory.getCollaborateurManager();
		manifestation
				.setCollaborateur(collaborateur.retrouver(new Utilisateur(resultSet.getLong("id_collaborateur"))));

		return manifestation;
	}

	@Override
	public void modifier(Manifestation manifestation) throws DatabaseException {
		Timestamp date = null;
		if (manifestation.getDate() != null)
			date = new Timestamp(manifestation.getDate().getMillis());
		Long idCollaborateur = null;
		if (manifestation.getCollaborateur() != null)
			idCollaborateur = manifestation.getCollaborateur().getId();

		Long idAdresse = null;
		if (manifestation.getAdresse() != null)
			idAdresse = manifestation.getAdresse().getId();

		modifier(SQL_UPDATE, manifestation.getTitre(), manifestation.getDescription(),

				date, idAdresse, idCollaborateur, manifestation.getId());
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
