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

import beans.Adresse;
import beans.Collaborateur;
import beans.Comite;
import beans.Fournisseur;

public class FournisseurManagerBean implements FournisseurManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM fournisseur WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM fournisseur";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM fournisseur WHERE id_fournisseur = ?";
	private static final String SQL_INSERT = "INSERT INTO fournisseur (nom,email,valide,valideur,date_validation,commentaires,id_adresse) VALUES(UPPER(?),?,?,?,?,?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM fournisseur WHERE id_fournisseur = ?";
	private static final String SQL_UPDATE = "UPDATE fournisseur SET nom=UPPER(?),email=?,valide=?,valideur=?,date_validation=?,commentaires=?,id_adresse=? WHERE id_fournisseur =?";

	private Factory factory;

	public FournisseurManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface FournisseurManager
	 */
	@Override
	public Fournisseur retrouver(Fournisseur fournisseur) throws DatabaseException {
		if (fournisseur.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, fournisseur.getId());
		return retrouver(SQL_SELECT_PAR_NOM, fournisseur.getNom());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Fournisseur retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Fournisseur fournisseur = null;

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
				fournisseur = map(resultSet);
				fournisseur = consulterComitesFournisseur(fournisseur);
				fournisseur = consulterCollaborateursFournisseur(fournisseur);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return fournisseur;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface FournisseurManager
	 */
	@Override
	public void supprimer(Fournisseur fournisseur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, fournisseur.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du fournisseur, aucune ligne supprim�e de la table.");
			} else {

				fournisseur.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface FournisseurManager
	 */
	@Override
	public List<Fournisseur> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Fournisseur> fournisseurs = new ArrayList<Fournisseur>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Fournisseur fournisseur = map(resultSet);
				fournisseur = consulterComitesFournisseur(fournisseur);
				fournisseur = consulterCollaborateursFournisseur(fournisseur);
				fournisseurs.add(fournisseur);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return fournisseurs;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des utilisateurs (un
	 * ResultSet) et un bean Fournisseur.
	 */
	private Fournisseur map(ResultSet resultSet) throws SQLException {
		Fournisseur fournisseur = new Fournisseur();
		// Timestamp dateValidation = resultSet.getTimestamp("date_validation");
		// if (dateValidation != null)
		// fournisseur.setDateValidation(new DateTime(dateValidation));
		fournisseur.setId(resultSet.getLong("id_fournisseur"));
		fournisseur.setNom(resultSet.getString("nom"));
		fournisseur.setEmail(resultSet.getString("email"));
		// fournisseur.setValide(resultSet.getBoolean("valide"));
		// fournisseur.setValideur(resultSet.getString("valideur"));

		// fournisseur.setCommentaires(resultSet.getString("commentaires"));

		AdresseManager adresse = factory.getAdresseManager();

		fournisseur.setAdresse(adresse.retrouver(new Adresse(resultSet.getLong("id_adresse"))));

		return fournisseur;
	}

	@Override
	public void modifier(Fournisseur fournisseur) throws DatabaseException {

		Timestamp dateValidation = null;
		// if (fournisseur.getDateValidation() != null)
		// dateValidation = new
		// Timestamp(fournisseur.getDateValidation().getMillis());

		Long idAdresse = null;
		if (fournisseur.getAdresse() != null)
			idAdresse = fournisseur.getAdresse().getId();
		modifier(SQL_UPDATE, fournisseur.getNom(), fournisseur.getEmail(), dateValidation, idAdresse,
				fournisseur.getId());

	}

	@Override
	public Fournisseur creer(Fournisseur fournisseur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		Timestamp dateValidation = null;
		// if (fournisseur.getDateValidation() != null)
		// dateValidation = new
		// Timestamp(fournisseur.getDateValidation().getMillis());

		Long idAdresse = null;
		if (fournisseur.getAdresse() != null)
			idAdresse = fournisseur.getAdresse().getId();
		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, fournisseur.getNom(),
					fournisseur.getEmail(), dateValidation, idAdresse);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation du fournisseur, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				fournisseur = retrouver(new Fournisseur((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du fournisseur en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return fournisseur;
	}

	@Override
	public Fournisseur supprimerComite(Fournisseur fournisseur, Localisation profil) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		String sql = "DELETE FROM fournisseur_profil WHERE id_fournisseur = ? AND id_profil = ?";

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, sql, true, fournisseur.getId(),
					profil.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du lien entre le profil m�tier et le profil d'acc�s, aucune ligne supprim�e de la table.");
			} else {
				fournisseur = retrouver(fournisseur);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
		return fournisseur;
	}

	@Override
	public Fournisseur associerComite(Fournisseur fournisseur, Localisation profil) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO fournisseur_profil (id_fournisseur,id_profil) VALUES(?,?)";
		if (fournisseur != null && profil != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, fournisseur.getId(),
						profil.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					fournisseur = consulterComitesFournisseur(fournisseur);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return fournisseur;
	}

	private Fournisseur consulterComitesFournisseur(Fournisseur fournisseur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Localisation> uneListe = new ArrayList<Localisation>();
		String sql = "SELECT * FROM profil P WHERE id_profil IN (SELECT id_profil FROM fournisseur_profil WHERE fournisseur_profil.id_fournisseur=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, fournisseur.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Localisation(resultSet.getString("identifiant")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		// fournisseur.setComites(uneListe);
		return fournisseur;
	}

	private Fournisseur consulterCollaborateursFournisseur(Fournisseur fournisseur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Utilisateur> uneListe = new ArrayList<Utilisateur>();
		String sql = "SELECT * FROM utilisateur U WHERE id_fournisseur=?";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, fournisseur.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				uneListe.add(new Utilisateur(resultSet.getLong("id_utilisateur"), resultSet.getString("login"),
						resultSet.getString("nom"), resultSet.getString("prenom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		// fournisseur.setCollaborateurs(uneListe);
		return fournisseur;
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
