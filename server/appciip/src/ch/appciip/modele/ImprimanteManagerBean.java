package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Imprimante;
import beans.Collaborateur;

public class ImprimanteManagerBean implements ImprimanteManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM imprimante WHERE numero =?";
	private static final String SQL_SELECT = "SELECT * FROM imprimante";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM imprimante WHERE id_imprimante = ?";
	private static final String SQL_INSERT = "INSERT INTO imprimante (numero,prix,id_contact_interne,id_contact_fonctionnel) VALUES(UPPER(?),?,?,?)";
	private static final String SQL_UPDATE = "UPDATE imprimante SET numero=UPPER(?),prix=?,id_contact_interne=?,id_contact_fonctionnel=? WHERE id_imprimante =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Imprimante WHERE id_imprimante = ?";

	private Factory factory;

	public ImprimanteManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Imprimante retrouver(Imprimante imprimante) throws DatabaseException {
		if (imprimante.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, imprimante.getId());
		return retrouver(SQL_SELECT_PAR_NOM, imprimante.getNumero());
	}

	private Imprimante retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Imprimante imprimante = null;

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
				imprimante = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return imprimante;
	}

	@Override
	public void supprimer(Imprimante imprimante) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, imprimante.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de l'imprimante, aucune ligne supprim�e de la table.");
			} else {
				imprimante.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface ImprimanteManager
	 */
	@Override
	public List<Imprimante> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Imprimante> clients = new ArrayList<Imprimante>();

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
	public void modifier(Imprimante imprimante) throws DatabaseException {

		Long idContactInterne = null;
		Long idContactFonctionnel = null;

		if (imprimante.getContactInterne() != null)
			idContactInterne = imprimante.getContactInterne().getId();

		modifier(SQL_UPDATE, imprimante.getNumero(), imprimante.getPrix(), idContactInterne, idContactFonctionnel,
				imprimante.getId());
	}

	@Override
	public Imprimante creer(Imprimante imprimante) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idContactInterne = null;
		Long idContactFonctionnel = null;

		if (imprimante.getContactInterne() != null)
			idContactInterne = imprimante.getContactInterne().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, imprimante.getNumero(),
					imprimante.getPrix(), idContactInterne, idContactFonctionnel);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de l'imprimante, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				imprimante = retrouver(new Imprimante((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation de l'imprimante en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return imprimante;
	}

	private Imprimante map(ResultSet resultSet) throws SQLException {
		Imprimante imprimante = new Imprimante();
		imprimante.setId(resultSet.getLong("id_imprimante"));

		imprimante.setNumero(resultSet.getString("numero"));
		imprimante.setPrix(resultSet.getBigDecimal("prix"));

		CollaborateurManager utilisateur = factory.getCollaborateurManager();
		imprimante.setContactInterne(utilisateur.retrouver(new Utilisateur(resultSet.getLong("id_contact_interne"))));

		return imprimante;
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
