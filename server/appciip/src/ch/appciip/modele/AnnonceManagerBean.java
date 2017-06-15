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

import beans.Collaborateur;
import beans.Annonce;

public class AnnonceManagerBean implements AnnonceManager {

	private static final String SQL_SELECT = "SELECT * FROM annonce WHERE echeance > CURRENT_DATE()";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM annonce WHERE id_annonce = ?";
	private static final String SQL_SELECT_PAR_TITRE = "SELECT * FROM annonce WHERE titre =?";
	private static final String SQL_INSERT = "INSERT INTO annonce (titre,contenu,date,echeance,id_auteur) VALUES(UPPER(?),?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE annonce SET titre=UPPER(?),contenu=?,date=?,echeance=?,id_auteur=? WHERE id_annonce =?";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Annonce WHERE id_annonce = ?";
	private Factory factory;

	AnnonceManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface AnnonceManager */
	@Override
	public Annonce retrouver(Annonce annonce) throws DatabaseException {

		if (annonce.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, annonce.getId());
		return retrouver(SQL_SELECT_PAR_TITRE, annonce.getTitre());

	}

	/* Impl�mentation de la m�thode d�finie dans l'interface AnnonceManager */

	@Override
	public Annonce creer(Annonce annonce) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		boolean b = false;

		Timestamp date = null;
		if (annonce.getDate() != null)
			date = new Timestamp(annonce.getDate().getMillis());

		Timestamp echeance = null;
		if (annonce.getEcheance() != null)
			echeance = new Timestamp(annonce.getEcheance().getMillis());

		Long idAuteur = null;
		if (annonce.getAuteur() != null) {
			idAuteur = annonce.getAuteur().getId();
			CollaborateurManager u = factory.getCollaborateurManager();
			Utilisateur ut = u.retrouver(annonce.getAuteur());

			Iterator<Annonce> it = ut.getAnnonces().iterator();
			while (it.hasNext()) {
				Annonce o = (Annonce) it.next();
				if (o.getTitre().equals(annonce.getTitre()))
					b = true;
			}

		}

		if (!b) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, annonce.getTitre(),
						annonce.getContenu(),

						date, echeance, idAuteur);
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException(
							"�chec de la cr�ation de la annonce, aucune ligne ajout�e dans la table.");
				}
				valeursAutoGenerees = preparedStatement.getGeneratedKeys();
				if (valeursAutoGenerees.next()) {

					annonce = retrouver(new Annonce((valeursAutoGenerees.getLong(1))));

				} else {
					throw new DatabaseException(
							"�chec de la cr�ation de la annonce en base, aucun ID auto-g�n�r� retourn�.");
				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		} else {
			throw new DatabaseException("�chec de la cr�ation de la annonce en base, aucun ID auto-g�n�r� retourn�.");
		}

		return annonce;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un annonce depuis la base de
	 * donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres les
	 * objets pass�s en argument.
	 */

	private Annonce retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Annonce annonce = null;

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
				annonce = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		/*
		 * if (annonce == null) throw new DatabaseException(
		 * "�chec de la recherche de la annonce en base, aucune annonce retourn�e."
		 * );
		 */
		return annonce;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface AnnonceManager */
	@Override
	public void supprimer(Annonce annonce) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, annonce.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression du client, aucune ligne supprim�e de la table.");
			} else {
				annonce.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface AnnonceManager */
	@Override
	public List<Annonce> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Annonce> clients = new ArrayList<Annonce>();

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
	 * mapping) entre une ligne issue de la table des annonces (un ResultSet) et
	 * un bean Annonce.
	 */
	private Annonce map(ResultSet resultSet) throws SQLException {
		Annonce annonce = new Annonce();
		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			annonce.setDate(new DateTime(date));

		Timestamp echeance = resultSet.getTimestamp("echeance");
		if (date != null)
			annonce.setEcheance(new DateTime(echeance));

		annonce.setId(resultSet.getLong("id_annonce"));
		annonce.setTitre(resultSet.getString("titre"));
		annonce.setContenu(resultSet.getString("contenu"));

		CollaborateurManager auteur = factory.getCollaborateurManager();
		annonce.setAuteur(auteur.retrouver(new Utilisateur(resultSet.getLong("id_auteur"))));

		return annonce;
	}

	@Override
	public void modifier(Annonce annonce) throws DatabaseException {
		Timestamp date = null;
		if (annonce.getDate() != null)
			date = new Timestamp(annonce.getDate().getMillis());
		Long idAuteur = null;

		if (annonce.getAuteur() != null) {
			idAuteur = annonce.getAuteur().getId();

		}

		Timestamp echeance = null;
		if (annonce.getEcheance() != null)
			echeance = new Timestamp(annonce.getEcheance().getMillis());

		modifier(SQL_UPDATE, annonce.getTitre(), annonce.getContenu(),

				date, echeance, idAuteur, annonce.getId());
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
