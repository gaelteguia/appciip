package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Telephone;
import beans.Collaborateur;

public class TelephoneManagerBean implements TelephoneManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM telephone WHERE numero =?";
	private static final String SQL_SELECT = "SELECT * FROM telephone";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM telephone WHERE id_telephone = ?";
	private static final String SQL_INSERT = "INSERT INTO telephone (numero,prix,id_collaborateur,id_contact_fonctionnel) VALUES(UPPER(?),?,?,?)";
	private static final String SQL_UPDATE = "UPDATE telephone SET numero=UPPER(?),prix=?,id_collaborateur=?,id_contact_fonctionnel=? WHERE id_telephone =?";

	private static final String SQL_DELETE_PAR_ID = "DELETE FROM Telephone WHERE id_telephone = ?";

	private Factory factory;

	public TelephoneManagerBean(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Telephone retrouver(Telephone telephone) throws DatabaseException {
		if (telephone.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, telephone.getId());
		return retrouver(SQL_SELECT_PAR_NOM, telephone.getNumero());
	}

	private Telephone retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Telephone telephone = null;

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
				telephone = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return telephone;
	}

	@Override
	public void supprimer(Telephone telephone) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, telephone.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de l'telephone, aucune ligne supprim�e de la table.");
			} else {
				telephone.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}

	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface TelephoneManager
	 */
	@Override
	public List<Telephone> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Telephone> clients = new ArrayList<Telephone>();

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
	public void modifier(Telephone telephone) throws DatabaseException {

		Long idCollaborateur = null;
		Long idContactFonctionnel = null;

		if (telephone.getCollaborateur() != null)
			idCollaborateur = telephone.getCollaborateur().getId();

		modifier(SQL_UPDATE, telephone.getNumero(), telephone.getPrix(), idCollaborateur, idContactFonctionnel,
				telephone.getId());
	}

	@Override
	public Telephone creer(Telephone telephone) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		Long idCollaborateur = null;
		Long idContactFonctionnel = null;

		if (telephone.getCollaborateur() != null)
			idCollaborateur = telephone.getCollaborateur().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, telephone.getNumero(),
					telephone.getPrix(), idCollaborateur, idContactFonctionnel);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation de l'telephone, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				telephone = retrouver(new Telephone((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation de l'telephone en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return telephone;
	}

	private Telephone map(ResultSet resultSet) throws SQLException {
		Telephone telephone = new Telephone();
		telephone.setId(resultSet.getLong("id_telephone"));

		telephone.setNumero(resultSet.getString("numero"));
		telephone.setPrix(resultSet.getBigDecimal("prix"));

		CollaborateurManager collaborateur = factory.getCollaborateurManager();
		telephone.setCollaborateur(collaborateur.retrouver(new Utilisateur(resultSet.getLong("id_collaborateur"))));

		return telephone;
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
