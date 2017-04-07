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
import beans.Evenement;

public class EvenementManagerBean implements EvenementManager {

	private static final String SQL_SELECT = "SELECT * FROM evenement WHERE date > CURRENT_DATE()";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM evenement WHERE id_evenement = ?";
	private static final String SQL_SELECT_PAR_TITRE = "SELECT * FROM evenement WHERE titre =?";
	private static final String SQL_INSERT = "INSERT INTO evenement (titre,description,date,id_adresse,id_collaborateur) VALUES(UPPER(?),?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE evenement SET titre=UPPER(?),description=?,date=?,id_adresse=?,id_collaborateur=? WHERE id_evenement =?";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Evenement WHERE id_evenement = ?";
	private Factory factory;

	EvenementManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface EvenementManager */
	@Override
	public Evenement retrouver(Evenement evenement) throws DatabaseException {

		if (evenement.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, evenement.getId());
		return retrouver(SQL_SELECT_PAR_TITRE, evenement.getTitre());

	}

	/* Impl�mentation de la m�thode d�finie dans l'interface EvenementManager */

	@Override
	public Evenement creer(Evenement evenement) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		boolean b = false;

		Timestamp date = null;
		if (evenement.getDate() != null)
			date = new Timestamp(evenement.getDate().getMillis());

		Long idCollaborateur = null;
		if (evenement.getCollaborateur() != null) {
			idCollaborateur = evenement.getCollaborateur().getId();
			CollaborateurManager u = factory.getCollaborateurManager();
			Utilisateur ut = u.retrouver(evenement.getCollaborateur());

			Iterator<Evenement> it = ut.getEvenements().iterator();
			while (it.hasNext()) {
				Evenement o = (Evenement) it.next();
				if (o.getTitre().equals(evenement.getTitre()))
					b = true;
			}

		}

		Long idAdresse = null;
		if (evenement.getAdresse() != null) {
			idAdresse = evenement.getAdresse().getId();
			AdresseManager u = factory.getAdresseManager();
			Adresse ut = u.retrouver(evenement.getAdresse());
			if (ut != null) {
				Iterator<Evenement> it = ut.getEvenements().iterator();
				while (it.hasNext()) {
					Evenement o = (Evenement) it.next();
					if (o.getTitre().equals(evenement.getTitre()))
						b = true;
				}
			}

		}

		if (!b) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, evenement.getTitre(),
						evenement.getDescription(),

						date, idAdresse, idCollaborateur);
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException(
							"�chec de la cr�ation de la evenement, aucune ligne ajout�e dans la table.");
				}
				valeursAutoGenerees = preparedStatement.getGeneratedKeys();
				if (valeursAutoGenerees.next()) {

					evenement = retrouver(new Evenement((valeursAutoGenerees.getLong(1))));

				} else {
					throw new DatabaseException(
							"�chec de la cr�ation de la evenement en base, aucun ID auto-g�n�r� retourn�.");
				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		} else {
			throw new DatabaseException("�chec de la cr�ation de la evenement en base, aucun ID auto-g�n�r� retourn�.");
		}

		return evenement;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un evenement depuis la base de
	 * donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres les
	 * objets pass�s en argument.
	 */

	private Evenement retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Evenement evenement = null;

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
				evenement = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		/*
		 * if (evenement == null) throw new DatabaseException(
		 * "�chec de la recherche de la evenement en base, aucune evenement retourn�e."
		 * );
		 */
		return evenement;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface EvenementManager */
	@Override
	public void supprimer(Evenement evenement) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, evenement.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression du client, aucune ligne supprim�e de la table.");
			} else {
				evenement.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface EvenementManager */
	@Override
	public List<Evenement> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Evenement> clients = new ArrayList<Evenement>();

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
	 * mapping) entre une ligne issue de la table des evenements (un ResultSet)
	 * et un bean Evenement.
	 */
	private Evenement map(ResultSet resultSet) throws SQLException {
		Evenement evenement = new Evenement();
		Timestamp date = resultSet.getTimestamp("date");
		if (date != null)
			evenement.setDate(new DateTime(date));
		evenement.setId(resultSet.getLong("id_evenement"));
		evenement.setTitre(resultSet.getString("titre"));
		evenement.setDescription(resultSet.getString("description"));

		AdresseManager adresse = factory.getAdresseManager();
		evenement.setAdresse(adresse.retrouver(new Adresse(resultSet.getLong("id_adresse"))));

		CollaborateurManager collaborateur = factory.getCollaborateurManager();
		evenement.setCollaborateur(collaborateur.retrouver(new Utilisateur(resultSet.getLong("id_collaborateur"))));

		return evenement;
	}

	@Override
	public void modifier(Evenement evenement) throws DatabaseException {
		Timestamp date = null;
		if (evenement.getDate() != null)
			date = new Timestamp(evenement.getDate().getMillis());
		Long idCollaborateur = null;
		if (evenement.getCollaborateur() != null)
			idCollaborateur = evenement.getCollaborateur().getId();

		Long idAdresse = null;
		if (evenement.getAdresse() != null)
			idAdresse = evenement.getAdresse().getId();

		modifier(SQL_UPDATE, evenement.getTitre(), evenement.getDescription(),

				date, idAdresse, idCollaborateur, evenement.getId());
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
