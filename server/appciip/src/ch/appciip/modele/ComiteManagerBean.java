package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Seance;
import beans.Collaborateur;
import beans.Comite;

public class ComiteManagerBean implements ComiteManager {
	private static final String SQL_SELECT_PAR_ACRONYME = "SELECT * FROM comite WHERE acronyme =?";
	private static final String SQL_SELECT = "SELECT * FROM comite";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM comite WHERE id_comite = ?";
	private static final String SQL_INSERT = "INSERT INTO comite (acronyme,nom,description,website,statut,officiel,id_parent,id_responsable) VALUES(UPPER(?),?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE comite SET acronyme=UPPER(?),nom=?,description=?,website=?,statut=?,officiel=?,id_parent=?,id_responsable=? WHERE id_comite =?";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Comite WHERE id_comite = ?";
	private Factory factory;

	public ComiteManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Localisation retrouver(Localisation comite) throws DatabaseException {
		if (comite.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, comite.getId());
		return retrouver(SQL_SELECT_PAR_ACRONYME, comite.getAcronyme());
	}

	private Localisation retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Localisation comite = null;

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
				comite = map(resultSet);
				comite = consulterSeancesComite(comite);
				comite = consulterCollaborateursComite(comite);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return comite;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface ComiteManager */
	@Override
	public List<Localisation> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Localisation> clients = new ArrayList<Localisation>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Localisation comite = map(resultSet);
				comite = consulterSeancesComite(comite);
				comite = consulterCollaborateursComite(comite);
				clients.add(comite);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return clients;
	}

	private Localisation consulterCollaborateursComite(Localisation comite) {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Utilisateur> uneListe = new ArrayList<Utilisateur>();
		String sql = "SELECT * FROM collaborateur D WHERE id_collaborateur IN (SELECT id_collaborateur FROM collaborateur_comite WHERE collaborateur_comite.id_comite=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, comite.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				uneListe.add(new Utilisateur(resultSet.getString("initiales")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		comite.setParticipants(uneListe);
		return comite;
	}

	@Override
	public void modifier(Localisation comite) throws DatabaseException {
		Long idResponsable = null;
		if (comite.getResponsable() != null)
			idResponsable = comite.getResponsable().getId();

		Long idParent = null;
		if (comite.getParent() != null)
			idParent = comite.getParent().getId();

		modifier(SQL_UPDATE, comite.getAcronyme(), comite.getNom(), comite.getDescription(), comite.getWebsite(),
				comite.isStatut(), comite.isOfficiel(), idParent, idResponsable, comite.getId());

	}

	@Override
	public Localisation creer(Localisation comite) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idResponsable = null;
		if (comite.getResponsable() != null)
			idResponsable = comite.getResponsable().getId();

		Long idParent = null;
		if (comite.getParent() != null)
			idParent = comite.getParent().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, comite.getAcronyme(),
					comite.getNom(), comite.getDescription(), comite.getWebsite(), comite.isStatut(),
					comite.isOfficiel(), idParent, idResponsable);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation du comite d'acc�s, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				comite = retrouver(new Localisation((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du comite d'acc�s en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return comite;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des comites (un ResultSet) et
	 * un bean Comite.
	 */
	private Localisation map(ResultSet resultSet) throws SQLException {
		Localisation comite = new Localisation();
		comite.setId(resultSet.getLong("id_comite"));
		comite.setAcronyme(resultSet.getString("acronyme"));
		comite.setNom(resultSet.getString("nom"));
		comite.setDescription(resultSet.getString("description"));
		comite.setWebsite(resultSet.getString("website"));
		comite.setStatut(resultSet.getBoolean("statut"));
		comite.setOfficiel(resultSet.getBoolean("officiel"));

		CollaborateurManager responsable = factory.getCollaborateurManager();
		comite.setResponsable(responsable.retrouver(new Utilisateur(resultSet.getLong("id_responsable"))));

		ComiteManager parent = factory.getComiteManager();
		comite.setParent(parent.retrouver(new Localisation(resultSet.getLong("id_parent"))));

		return comite;
	}

	@Override
	public Localisation supprimerSeance(Localisation comite, Seance seance) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		String sql = "DELETE FROM comite_seance WHERE id_comite = ? AND id_seance = ?";

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, sql, true, comite.getId(), seance.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du lien entre le comite d'acc�s et le seance d'acc�s, aucune ligne supprim�e de la table.");
			} else {
				comite = retrouver(comite);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
		return comite;
	}

	@Override
	public Localisation associerSeance(Localisation comite, Seance seance) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO comite_seance (id_comite,id_seance) VALUES(?,?)";
		if (comite != null && seance != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, comite.getId(), seance.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					comite = consulterSeancesComite(comite);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return comite;
	}

	private Localisation consulterSeancesComite(Localisation comite) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Seance> uneListe = new ArrayList<Seance>();
		String sql = "SELECT * FROM seance D WHERE id_seance IN (SELECT id_seance FROM comite_seance WHERE comite_seance.id_comite=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, comite.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Seance(resultSet.getString("libelle")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		comite.setSeances(uneListe);
		return comite;
	}

	@Override
	public void supprimer(Localisation comite) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, comite.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du comite d'acc�s, aucune ligne supprim�e de la table.");
			} else {
				comite.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

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
