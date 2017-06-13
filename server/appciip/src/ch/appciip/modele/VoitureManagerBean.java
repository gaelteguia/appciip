package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Collaborateur;
import beans.Horaire;
import beans.Voiture;

public class VoitureManagerBean implements VoitureManager {

	private static final String SQL_SELECT_PAR_NOPLAQUES = "SELECT * FROM voiture WHERE no_plaques =?";
	private static final String SQL_SELECT = "SELECT * FROM voiture";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM voiture WHERE id_voiture = ?";
	private static final String SQL_INSERT = "INSERT INTO voiture (no_plaques,id_collaborateur) VALUES(UPPER(?),?)";
	private static final String SQL_UPDATE = "UPDATE voiture SET no_plaques=UPPER(?),id_collaborateur=? WHERE id_voiture =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM voiture WHERE id_voiture = ?";

	private Factory factory;

	public VoitureManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Voiture retrouver(Voiture voiture) throws DatabaseException {
		if (voiture.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, voiture.getId());
		return retrouver(SQL_SELECT_PAR_NOPLAQUES, voiture.getNoPlaques());
	}

	private Voiture retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Voiture voiture = null;

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
				voiture = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return voiture;
	}

	@Override
	public void supprimer(Voiture voiture) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, voiture.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de l'voiture, aucune ligne supprim�e de la table.");
			} else {
				voiture.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface VoitureManager
	 */
	@Override
	public List<Voiture> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Voiture> clients = new ArrayList<Voiture>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Voiture voiture = map(resultSet);
				voiture = consulterHorairesVoiture(voiture);
				clients.add(voiture);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return clients;
	}

	@Override
	public void modifier(Voiture voiture) throws DatabaseException {

		Long idCollaborateur = null;

		if (voiture.getCollaborateur() != null)
			idCollaborateur = voiture.getCollaborateur().getId();

		modifier(SQL_UPDATE, voiture.getNoPlaques(), idCollaborateur, voiture.getId());
	}

	@Override
	public Voiture creer(Voiture voiture) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idCollaborateur = null;

		if (voiture.getCollaborateur() != null)
			idCollaborateur = voiture.getCollaborateur().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, voiture.getNoPlaques(),
					idCollaborateur);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				voiture = retrouver(new Voiture((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation de l'voiture en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return voiture;
	}

	private Voiture map(ResultSet resultSet) throws SQLException {
		Voiture voiture = new Voiture();
		voiture.setId(resultSet.getLong("id_voiture"));

		voiture.setNoPlaques(resultSet.getString("no_plaques"));

		CollaborateurManager utilisateur = factory.getCollaborateurManager();
		voiture.setCollaborateur(utilisateur.retrouver(new Utilisateur(resultSet.getLong("id_collaborateur"))));

		return voiture;
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

	private Voiture consulterHorairesVoiture(Voiture voiture) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Horaire> uneListe = new ArrayList<Horaire>();
		String sql = "SELECT DISTINCT * FROM horaire A WHERE id_horaire IN (SELECT id_horaire FROM voiture_horaire WHERE voiture_horaire.id_voiture=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, voiture.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Horaire(resultSet.getString("nom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		voiture.setHoraires(uneListe);
		return voiture;
	}

}