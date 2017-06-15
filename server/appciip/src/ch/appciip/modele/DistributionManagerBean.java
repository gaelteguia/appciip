package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Distribution;
import beans.Collaborateur;

public class DistributionManagerBean implements DistributionManager {

	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM distribution WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM distribution";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM distribution WHERE id_distribution = ?";
	private static final String SQL_INSERT = "INSERT INTO distribution (nom) VALUES(UPPER(?))";
	private static final String SQL_UPDATE = "UPDATE distribution SET nom=UPPER(?) WHERE id_distribution =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Distribution WHERE id_distribution = ?";
	private Factory factory;

	public DistributionManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Distribution retrouver(Distribution distribution) throws DatabaseException {
		if (distribution.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, distribution.getId());
		return retrouver(SQL_SELECT_PAR_NOM, distribution.getNom());
	}

	private Distribution retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Distribution distribution = null;

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
				distribution = map(resultSet);
				distribution = consulterCollaborateursDistribution(distribution);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return distribution;
	}

	@Override
	public void supprimer(Distribution distribution) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, distribution.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du distribution d'acc�s, aucune ligne supprim�e de la table.");
			} else {
				distribution.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface DistributionManager
	 */
	@Override
	public List<Distribution> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Distribution> clients = new ArrayList<Distribution>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Distribution distribution = map(resultSet);
				distribution = consulterCollaborateursDistribution(distribution);
				clients.add(distribution);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return clients;
	}

	@Override
	public void modifier(Distribution distribution) throws DatabaseException {
		modifier(SQL_UPDATE, distribution.getNom(), distribution.getId());
	}

	@Override
	public Distribution creer(Distribution distribution) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, distribution.getNom());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation du distribution d'acc�s, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				distribution = retrouver(new Distribution((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du distribution d'acc�s en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return distribution;
	}

	private Distribution map(ResultSet resultSet) throws SQLException {
		Distribution distribution = new Distribution();
		distribution.setId(resultSet.getLong("id_distribution"));
		distribution.setNom(resultSet.getString("nom"));

		return distribution;
	}

	private Distribution consulterCollaborateursDistribution(Distribution distribution) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Utilisateur> uneListe = new ArrayList<Utilisateur>();
		String sql = "SELECT * FROM collaborateur P WHERE id_collaborateur IN (SELECT id_collaborateur FROM collaborateur_distribution WHERE collaborateur_distribution.id_distribution=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, distribution.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				uneListe.add(new Utilisateur(resultSet.getString("initiales")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		distribution.setCollaborateurs(uneListe);
		return distribution;
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
