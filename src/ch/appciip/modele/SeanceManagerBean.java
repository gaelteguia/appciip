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

import beans.Seance;
import beans.Adresse;
import beans.Collaborateur;
import beans.Comite;

public class SeanceManagerBean implements SeanceManager {

	private static final String SQL_SELECT_PAR_LIBELLE = "SELECT * FROM seance WHERE libelle =?";
	private static final String SQL_SELECT = "SELECT * FROM seance";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM seance WHERE id_seance = ?";
	private static final String SQL_INSERT = "INSERT INTO seance (libelle,description,date,id_adresse,id_comite,id_responsable) VALUES(UPPER(?),?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE seance SET libelle=UPPER(?),description=?,date=?,id_adresse=?,id_comite=?,id_responsable=? WHERE id_seance =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Seance WHERE id_seance = ?";
	private Factory factory;

	public SeanceManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Seance retrouver(Seance seance) throws DatabaseException {
		if (seance.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, seance.getId());
		return retrouver(SQL_SELECT_PAR_LIBELLE, seance.getLibelle());
	}

	private Seance retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Seance seance = null;

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
				seance = map(resultSet);
				// seance = consulterComitesSeance(seance);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return seance;
	}

	@Override
	public void supprimer(Seance seance) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, seance.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du seance d'acc�s, aucune ligne supprim�e de la table.");
			} else {
				seance.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/* Impl�mentation de la m�thode d�finie dans l'interface SeanceManager */
	@Override
	public List<Seance> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Seance> clients = new ArrayList<Seance>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Seance seance = map(resultSet);
				seance = consulterComitesSeance(seance);
				clients.add(seance);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return clients;
	}

	@Override
	public void modifier(Seance seance) throws DatabaseException {

		Timestamp date = null;
		Long idAdresse = null;
		Long idComite = null;
		Long idResponsable = null;

		if (seance.getDate() != null)
			date = new Timestamp(seance.getDate().getMillis());

		if (seance.getAdresse() != null)
			idAdresse = seance.getAdresse().getId();

		if (seance.getComite() != null)
			idComite = seance.getComite().getId();

		if (seance.getResponsable() != null)
			idResponsable = seance.getResponsable().getId();
		modifier(SQL_UPDATE, seance.getLibelle(), seance.getDescription(), date, idAdresse, idComite, idResponsable,
				seance.getId());
	}

	@Override
	public Seance creer(Seance seance) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		Timestamp date = null;
		Long idAdresse = null;
		Long idComite = null;
		Long idResponsable = null;

		if (seance.getDate() != null)
			date = new Timestamp(seance.getDate().getMillis());

		if (seance.getAdresse() != null)
			idAdresse = seance.getAdresse().getId();

		if (seance.getComite() != null)
			idComite = seance.getComite().getId();

		if (seance.getResponsable() != null)
			idResponsable = seance.getResponsable().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, seance.getLibelle(),
					seance.getDescription(), date, idAdresse, idComite, idResponsable);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation du seance d'acc�s, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				seance = retrouver(new Seance((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du seance d'acc�s en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return seance;
	}

	private Seance map(ResultSet resultSet) throws SQLException {
		Seance seance = new Seance();

		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			seance.setDate(new DateTime(date));
		seance.setId(resultSet.getLong("id_seance"));
		seance.setLibelle(resultSet.getString("libelle"));

		seance.setDescription(resultSet.getString("description"));

		AdresseManager adresse = factory.getAdresseManager();
		seance.setAdresse(adresse.retrouver(new Adresse(resultSet.getLong("id_adresse"))));

		CollaborateurManager collaborateur = factory.getCollaborateurManager();
		seance.setResponsable(collaborateur.retrouver(new Utilisateur(resultSet.getLong("id_responsable"))));

		ComiteManager comite = factory.getComiteManager();
		seance.setComite(comite.retrouver(new Localisation(resultSet.getLong("id_comite"))));

		return seance;
	}

	private Seance consulterComitesSeance(Seance seance) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Localisation> uneListe = new ArrayList<Localisation>();
		String sql = "SELECT * FROM comite P WHERE id_comite IN (SELECT id_comite FROM comite_seance WHERE comite_seance.id_seance=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, seance.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				uneListe.add(new Localisation(resultSet.getString("identifiant")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		seance.setComites(uneListe);
		return seance;
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
