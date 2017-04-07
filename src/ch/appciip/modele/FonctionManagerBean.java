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

import beans.Fonction;
import beans.Comite;
import beans.Service;
import beans.Collaborateur;

public class FonctionManagerBean implements FonctionManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM fonction WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM fonction";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM fonction WHERE id_fonction = ?";
	private static final String SQL_INSERT = "INSERT INTO fonction (nom,description,identifiant,id_service) VALUES(UPPER(?),?,?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM fonction WHERE id_fonction = ?";
	private static final String SQL_UPDATE = "UPDATE fonction SET nom=UPPER(?),description=?,identifiant=?,id_service=? WHERE id_fonction =?";

	private Factory factory;

	public FonctionManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface FonctionManager */
	@Override
	public Fonction retrouver(Fonction fonction) throws DatabaseException {
		if (fonction.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, fonction.getId());
		return retrouver(SQL_SELECT_PAR_NOM, fonction.getNom());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un collaborateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Fonction retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Fonction fonction = null;

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
				fonction = map(resultSet);
				// fonction = consulterComitesFonction(fonction);
				fonction = consulterCollaborateursFonction(fonction);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return fonction;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface FonctionManager */
	@Override
	public void supprimer(Fonction fonction) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, fonction.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression du fonction, aucune ligne supprim�e de la table.");
			} else {

				fonction.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface FonctionManager */
	@Override
	public List<Fonction> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Fonction> fonctions = new ArrayList<Fonction>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Fonction fonction = map(resultSet);
				// fonction = consulterComitesFonction(fonction);
				fonction = consulterCollaborateursFonction(fonction);
				fonctions.add(fonction);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return fonctions;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des collaborateurs (un
	 * ResultSet) et un bean Fonction.
	 */
	private Fonction map(ResultSet resultSet) throws SQLException {
		Fonction fonction = new Fonction();
		// Timestamp dateValidation = resultSet.getTimestamp("date_validation");
		// if (dateValidation != null)
		// fonction.setDateValidation(new DateTime(dateValidation));
		fonction.setId(resultSet.getLong("id_fonction"));
		fonction.setNom(resultSet.getString("nom"));
		fonction.setDescription(resultSet.getString("description"));
		// fonction.setValide(resultSet.getBoolean("valide"));
		fonction.setIdentifiant(resultSet.getString("identifiant"));

		// fonction.setCommentaires(resultSet.getString("commentaires"));

		ServiceManager service = factory.getServiceManager();

		fonction.setService(service.retrouver(new Service(resultSet.getLong("id_service"))));

		return fonction;
	}

	@Override
	public void modifier(Fonction fonction) throws DatabaseException {
		/*
		 * Timestamp dateValidation = null; if (fonction.getDateValidation() !=
		 * null) dateValidation = new
		 * Timestamp(fonction.getDateValidation().getMillis());
		 */
		Long idService = null;
		if (fonction.getService() != null)
			idService = fonction.getService().getId();
		modifier(SQL_UPDATE, fonction.getNom(), fonction.getDescription(), fonction.getIdentifiant(), idService,
				fonction.getId());

	}

	@Override
	public Fonction creer(Fonction fonction) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		/*
		 * Timestamp dateValidation = null; if (fonction.getDateValidation() !=
		 * null) dateValidation = new
		 * Timestamp(fonction.getDateValidation().getMillis());
		 */
		Long idService = null;
		if (fonction.getService() != null)
			idService = fonction.getService().getId();
		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, fonction.getNom(),
					fonction.getDescription(), fonction.getIdentifiant(), idService);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation du fonction, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				fonction = retrouver(new Fonction((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException("�chec de la cr�ation du fonction en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return fonction;
	}

	@Override
	public Fonction supprimerComite(Fonction fonction, Localisation profil) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		String sql = "DELETE FROM fonction_profil WHERE id_fonction = ? AND id_profil = ?";

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, sql, true, fonction.getId(), profil.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du lien entre le profil m�tier et le profil d'acc�s, aucune ligne supprim�e de la table.");
			} else {
				fonction = retrouver(fonction);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
		return fonction;
	}

	@Override
	public Fonction associerComite(Fonction fonction, Localisation profil) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO fonction_profil (id_fonction,id_profil) VALUES(?,?)";
		if (fonction != null && profil != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, fonction.getId(),
						profil.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					// fonction = consulterComitesFonction(fonction);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return fonction;
	}

	/*
	 * private Fonction consulterComitesFonction(Fonction fonction) throws
	 * DatabaseException { Connection connexion = null; PreparedStatement
	 * preparedStatement = null; ResultSet resultSet = null;
	 * 
	 * ArrayList<Comite> uneListe = new ArrayList<Comite>(); String sql =
	 * "SELECT * FROM profil P WHERE id_profil IN (SELECT id_profil FROM fonction_profil WHERE fonction_profil.id_fonction=?)"
	 * ;
	 * 
	 * try {
	 * 
	 * connexion = factory.getConnection();
	 * 
	 * 
	 * preparedStatement = initialisationRequetePreparee(connexion, sql, false,
	 * fonction.getId()); resultSet = preparedStatement.executeQuery(); while
	 * (resultSet.next()) { uneListe.add(new
	 * Comite(resultSet.getString("identifiant"))); } } catch (SQLException e) {
	 * throw new DatabaseException(e); } finally {
	 * fermeturesSilencieuses(resultSet, preparedStatement, connexion); }
	 * fonction.setComites(uneListe); return fonction; }
	 */

	private Fonction consulterCollaborateursFonction(Fonction fonction) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Utilisateur> uneListe = new ArrayList<Utilisateur>();
		String sql = "SELECT * FROM collaborateur U WHERE id_fonction=?";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, fonction.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				uneListe.add(new Utilisateur(resultSet.getLong("id_collaborateur"), resultSet.getString("initiales"),
						resultSet.getString("nom"), resultSet.getString("prenom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		fonction.setCollaborateurs(uneListe);
		return fonction;
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
