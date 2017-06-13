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

import beans.Station;
import beans.Collaborateur;
import beans.Fournisseur;
import beans.Salle;

public class StationManagerBean implements StationManager {
	private static final String SQL_SELECT_PAR_NUMERO = "SELECT * FROM station WHERE numero =?";
	private static final String SQL_SELECT = "SELECT * FROM station";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM station WHERE id_station = ?";
	private static final String SQL_INSERT = "INSERT INTO station (numero,prix,marque,modele,os,amortissement,type,date_achat,date_peremption,id_collaborateur,id_fournisseur,id_salle) VALUES(UPPER(?),?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE station SET numero=UPPER(?),prix=?,marque=?,modele=?,os=?,amortissement=?,type=?,date_achat=?,date_peremption=?,id_collaborateur=?,id_fournisseur=?,id_salle=? WHERE id_station =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Station WHERE id_station = ?";

	private Factory factory;

	public StationManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Station retrouver(Station station) throws DatabaseException {
		if (station.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, station.getId());
		return retrouver(SQL_SELECT_PAR_NUMERO, station.getNumero());
	}

	private Station retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Station station = null;

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
				station = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return station;
	}

	@Override
	public void supprimer(Station station) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, station.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de l'station, aucune ligne supprim�e de la table.");
			} else {
				station.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface StationManager
	 */
	@Override
	public List<Station> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Station> clients = new ArrayList<Station>();

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
	public void modifier(Station station) throws DatabaseException {

		Long idCollaborateur = null;
		Long idFournisseur = null;
		Long idSalle = null;

		if (station.getCollaborateur() != null)
			idCollaborateur = station.getCollaborateur().getId();

		if (station.getFournisseur() != null)
			idFournisseur = station.getFournisseur().getId();

		if (station.getSalle() != null)
			idSalle = station.getSalle().getId();

		modifier(SQL_UPDATE, station.getNumero(), station.getPrix(), station.getMarque(), station.getModele(),
				station.getOs(), station.getAmortissement(), station.getType(), station.getDateAchat(),
				station.getDatePeremption(), idCollaborateur, idFournisseur, idSalle, station.getId());
	}

	@Override
	public Station creer(Station station) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idCollaborateur = null;
		Long idFournisseur = null;
		Long idSalle = null;

		if (station.getCollaborateur() != null)
			idCollaborateur = station.getCollaborateur().getId();

		if (station.getFournisseur() != null)
			idFournisseur = station.getFournisseur().getId();

		if (station.getSalle() != null)
			idSalle = station.getSalle().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, station.getNumero(),
					station.getPrix(), station.getMarque(), station.getModele(), station.getOs(),
					station.getAmortissement(), station.getType(), station.getDateAchat(), station.getDatePeremption(),
					idCollaborateur, idFournisseur, idSalle);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation de l'station, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				station = retrouver(new Station((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation de l'station en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return station;
	}

	private Station map(ResultSet resultSet) throws SQLException {
		Station station = new Station();
		Timestamp dateAchat = resultSet.getTimestamp("date_achat");
		if (dateAchat != null)
			station.setDateAchat(new DateTime(dateAchat));

		Timestamp datePeremption = resultSet.getTimestamp("date_peremption");
		if (datePeremption != null)
			station.setDatePeremption(new DateTime(datePeremption));

		station.setId(resultSet.getLong("id_station"));

		station.setNumero(resultSet.getString("numero"));
		station.setPrix(resultSet.getBigDecimal("prix"));
		station.setMarque(resultSet.getString("marque"));
		station.setModele(resultSet.getString("modele"));
		station.setOs(resultSet.getString("os"));
		station.setAmortissement(resultSet.getFloat("amortissement"));
		station.setType(resultSet.getString("type"));

		CollaborateurManager collaborateur = factory.getCollaborateurManager();
		station.setCollaborateur(collaborateur.retrouver(new Utilisateur(resultSet.getLong("id_collaborateur"))));

		FournisseurManager fournisseur = factory.getFournisseurManager();
		station.setFournisseur(fournisseur.retrouver(new Fournisseur(resultSet.getLong("id_fournisseur"))));

		SalleManager salle = factory.getSalleManager();
		station.setSalle(salle.retrouver(new Salle(resultSet.getLong("id_salle"))));

		return station;
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
