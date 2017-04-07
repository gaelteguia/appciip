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

import beans.Beamer;
import beans.Collaborateur;
import beans.Fournisseur;
import beans.Salle;

public class BeamerManagerBean implements BeamerManager {
	private static final String SQL_SELECT_PAR_NUMERO = "SELECT * FROM beamer WHERE numero =?";
	private static final String SQL_SELECT = "SELECT * FROM beamer";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM beamer WHERE id_beamer = ?";
	private static final String SQL_INSERT = "INSERT INTO beamer (numero,prix,marque,amortissement,type,date_achat,date_peremption,id_contact_interne,id_fournisseur,id_salle) VALUES(UPPER(?),?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE beamer SET numero=UPPER(?),prix=?,marque=?,amortissement=?,type=?,date_achat=?,date_peremption=?,id_contact_interne=?,id_fournisseur=?,id_salle=? WHERE id_beamer =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM beamer WHERE id_beamer = ?";

	private Factory factory;

	public BeamerManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Beamer retrouver(Beamer beamer) throws DatabaseException {
		if (beamer.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, beamer.getId());
		return retrouver(SQL_SELECT_PAR_NUMERO, beamer.getNumero());
	}

	private Beamer retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Beamer beamer = null;

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
				beamer = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return beamer;
	}

	@Override
	public void supprimer(Beamer beamer) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, beamer.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression de l'beamer, aucune ligne supprim�e de la table.");
			} else {
				beamer.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface BeamerManager
	 */
	@Override
	public List<Beamer> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Beamer> clients = new ArrayList<Beamer>();

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
	public void modifier(Beamer beamer) throws DatabaseException {

		Long idContactInterne = null;
		Long idFournisseur = null;
		Long idSalle = null;

		if (beamer.getContactInterne() != null)
			idContactInterne = beamer.getContactInterne().getId();

		if (beamer.getFournisseur() != null)
			idFournisseur = beamer.getFournisseur().getId();

		if (beamer.getSalle() != null)
			idSalle = beamer.getSalle().getId();

		modifier(SQL_UPDATE, beamer.getNumero(), beamer.getPrix(), beamer.getMarque(), beamer.getAmortissement(),
				beamer.getType(), beamer.getDateAchat(), beamer.getDatePeremption(), idContactInterne, idFournisseur,
				idSalle, beamer.getId());
	}

	@Override
	public Beamer creer(Beamer beamer) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idContactInterne = null;
		Long idFournisseur = null;
		Long idSalle = null;

		if (beamer.getContactInterne() != null)
			idContactInterne = beamer.getContactInterne().getId();

		if (beamer.getFournisseur() != null)
			idFournisseur = beamer.getFournisseur().getId();

		if (beamer.getSalle() != null)
			idSalle = beamer.getSalle().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, beamer.getNumero(),
					beamer.getPrix(), beamer.getMarque(), beamer.getAmortissement(), beamer.getType(),
					beamer.getDateAchat(), beamer.getDatePeremption(), idContactInterne, idFournisseur, idSalle);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation de l'beamer, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				beamer = retrouver(new Beamer((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException("�chec de la cr�ation de l'beamer en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return beamer;
	}

	private Beamer map(ResultSet resultSet) throws SQLException {
		Beamer beamer = new Beamer();

		Timestamp dateAchat = resultSet.getTimestamp("date_achat");
		if (dateAchat != null)
			beamer.setDateAchat(new DateTime(dateAchat));

		Timestamp datePeremption = resultSet.getTimestamp("date_peremption");
		if (datePeremption != null)
			beamer.setDatePeremption(new DateTime(datePeremption));

		beamer.setId(resultSet.getLong("id_beamer"));

		beamer.setNumero(resultSet.getString("numero"));
		beamer.setPrix(resultSet.getBigDecimal("prix"));
		beamer.setMarque(resultSet.getString("marque"));
		beamer.setAmortissement(resultSet.getFloat("amortissement"));
		beamer.setType(resultSet.getString("type"));

		CollaborateurManager collaborateur = factory.getCollaborateurManager();
		beamer.setContactInterne(collaborateur.retrouver(new Utilisateur(resultSet.getLong("id_contact_interne"))));

		FournisseurManager fournisseur = factory.getFournisseurManager();
		beamer.setFournisseur(fournisseur.retrouver(new Fournisseur(resultSet.getLong("id_fournisseur"))));

		SalleManager salle = factory.getSalleManager();
		beamer.setSalle(salle.retrouver(new Salle(resultSet.getLong("id_salle"))));

		return beamer;
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
