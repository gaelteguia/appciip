package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Salle;
import beans.Adresse;
import beans.Collaborateur;
import beans.Comite;

public class SalleManagerBean implements SalleManager {

	private static final String SQL_SELECT_PAR_LIBELLE = "SELECT * FROM salle WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM salle";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM salle WHERE id_salle = ?";
	private static final String SQL_INSERT = "INSERT INTO salle (nom,capacite,description,id_adresse) VALUES(UPPER(?),?,?,?)";
	private static final String SQL_UPDATE = "UPDATE salle SET nom=UPPER(?),capacite=?,description=?,id_adresse=? WHERE id_salle =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Droit WHERE id_salle = ?";
	private Factory factory;

	public SalleManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Salle retrouver(Salle salle) throws DatabaseException {
		if (salle.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, salle.getId());
		return retrouver(SQL_SELECT_PAR_LIBELLE, salle.getNom());
	}

	private Salle retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Salle salle = null;

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
				salle = map(resultSet);
				// salle = consulterComitesDroit(salle);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return salle;
	}

	@Override
	public void supprimer(Salle salle) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, salle.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du salle d'acc�s, aucune ligne supprim�e de la table.");
			} else {
				salle.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/* Impl�mentation de la m�thode d�finie dans l'interface DroitManager */
	@Override
	public List<Salle> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Salle> clients = new ArrayList<Salle>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Salle salle = map(resultSet);
				// salle = consulterComitesDroit(salle);
				clients.add(salle);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return clients;
	}

	@Override
	public void modifier(Salle salle) throws DatabaseException {
		Long idAdresse = null;

		if (salle.getAdresse() != null)
			idAdresse = salle.getAdresse().getId();
		modifier(SQL_UPDATE, salle.getNom(), salle.getCapacite(), salle.getDescription(), idAdresse, salle.getId());
	}

	@Override
	public Salle creer(Salle salle) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		Long idAdresse = null;

		if (salle.getAdresse() != null)
			idAdresse = salle.getAdresse().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, salle.getNom(),
					salle.getCapacite(), salle.getDescription(), idAdresse);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation du salle d'acc�s, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				salle = retrouver(new Salle((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du salle d'acc�s en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return salle;
	}

	private Salle map(ResultSet resultSet) throws SQLException {
		Salle salle = new Salle();
		salle.setId(resultSet.getLong("id_salle"));
		salle.setNom(resultSet.getString("nom"));
		salle.setCapacite(resultSet.getInt("capacite"));
		salle.setDescription(resultSet.getString("description"));

		AdresseManager adresse = factory.getAdresseManager();

		salle.setAdresse(adresse.retrouver(new Adresse(resultSet.getLong("id_adresse"))));

		return salle;
	}
	/*
	 * private Salle consulterComitesDroit(Salle salle) throws DatabaseException
	 * { Connection connexion = null; PreparedStatement preparedStatement =
	 * null; ResultSet resultSet = null;
	 * 
	 * ArrayList<Comite> uneListe = new ArrayList<Comite>(); String sql =
	 * "SELECT * FROM profil P WHERE id_profil IN (SELECT id_profil FROM profil_salle WHERE profil_salle.id_salle=?)"
	 * ;
	 * 
	 * try {
	 * 
	 * connexion = factory.getConnection();
	 * 
	 * preparedStatement = initialisationRequetePreparee(connexion, sql, false,
	 * salle.getId()); resultSet = preparedStatement.executeQuery();
	 * 
	 * while (resultSet.next()) { uneListe.add(new
	 * Comite(resultSet.getString("identifiant"))); } } catch (SQLException e) {
	 * throw new DatabaseException(e); } finally {
	 * fermeturesSilencieuses(resultSet, preparedStatement, connexion); } //
	 * salle.setComites(uneListe); return salle; }
	 * 
	 */

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
