package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Application;
import beans.Collaborateur;
import beans.Fournisseur;

public class ApplicationManagerBean implements ApplicationManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM application WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM application";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM application WHERE id_application = ?";
	private static final String SQL_INSERT = "INSERT INTO application (nom,description,id_contact_technique,id_contact_fonctionnel,id_fournisseur) VALUES(UPPER(?),?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE application SET nom=UPPER(?),description=?,id_contact_technique=?,id_contact_fonctionnel=?,id_fournisseur=? WHERE id_application =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Application WHERE id_application = ?";

	private Factory factory;

	public ApplicationManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Application retrouver(Application application) throws DatabaseException {
		if (application.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, application.getId());
		return retrouver(SQL_SELECT_PAR_NOM, application.getNom());
	}

	private Application retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Application application = null;

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
				application = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return application;
	}

	@Override
	public void supprimer(Application application) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, application.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de l'application, aucune ligne supprim�e de la table.");
			} else {
				application.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface ApplicationManager
	 */
	@Override
	public List<Application> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Application> clients = new ArrayList<Application>();

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

	@Override
	public void modifier(Application application) throws DatabaseException {

		Long idContactTechnique = null;
		Long idContactFonctionnel = null;
		Long idFournisseur = null;

		if (application.getContactTechnique() != null)
			idContactTechnique = application.getContactTechnique().getId();

		if (application.getContactFonctionnel() != null)
			idContactFonctionnel = application.getContactFonctionnel().getId();

		if (application.getFournisseur() != null)
			idContactFonctionnel = application.getFournisseur().getId();

		modifier(SQL_UPDATE, application.getNom(), application.getDescription(), idContactTechnique,
				idContactFonctionnel, idFournisseur, application.getId());
	}

	@Override
	public Application creer(Application application) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idContactTechnique = null;
		Long idContactFonctionnel = null;
		Long idFournisseur = null;

		if (application.getContactTechnique() != null)
			idContactTechnique = application.getContactTechnique().getId();

		if (application.getContactFonctionnel() != null)
			idContactFonctionnel = application.getContactFonctionnel().getId();

		if (application.getFournisseur() != null)
			idContactFonctionnel = application.getFournisseur().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, application.getNom(),
					application.getDescription(), idContactTechnique, idContactFonctionnel, idFournisseur);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de l'application, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				application = retrouver(new Application((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation de l'application en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return application;
	}

	private Application map(ResultSet resultSet) throws SQLException {
		Application application = new Application();
		application.setId(resultSet.getLong("id_application"));

		application.setNom(resultSet.getString("nom"));
		application.setDescription(resultSet.getString("description"));

		CollaborateurManager utilisateur = factory.getCollaborateurManager();
		FournisseurManager fournisseur = factory.getFournisseurManager();

		application.setContactTechnique(
				utilisateur.retrouver(new Utilisateur(resultSet.getLong("id_contact_technique"))));

		application.setContactFonctionnel(
				utilisateur.retrouver(new Utilisateur(resultSet.getLong("id_contact_fonctionnel"))));

		application.setFournisseur(fournisseur.retrouver(new Fournisseur(resultSet.getLong("id_fournisseur"))));

		return application;
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
