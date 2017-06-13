package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Partenaire;
import beans.Service;

public class PartenaireManagerBean implements PartenaireManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM partenaire WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM partenaire";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM partenaire WHERE id_partenaire = ?";
	private static final String SQL_INSERT = "INSERT INTO partenaire (nom,type,description,website) VALUES(UPPER(?),?,?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM partenaire WHERE id_partenaire = ?";
	private static final String SQL_UPDATE = "UPDATE partenaire SET nom=UPPER(?),type=?,description=?,website=? WHERE id_partenaire =?";

	private Factory factory;

	public PartenaireManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface PartenaireManager
	 */
	@Override
	public Partenaire retrouver(Partenaire partenaire) throws DatabaseException {

		if (partenaire.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, partenaire.getId());

		return retrouver(SQL_SELECT_PAR_NOM, partenaire.getNom());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Partenaire retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Partenaire partenaire = null;

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
				partenaire = map(resultSet);
				// partenaire = consulterServicesPartenaire(partenaire);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return partenaire;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface PartenaireManager
	 */
	@Override
	public void supprimer(Partenaire partenaire) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, partenaire.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de la partenaire, aucune ligne supprim�e de la table.");
			} else {

				partenaire.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface PartenaireManager
	 */
	@Override
	public List<Partenaire> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Partenaire> partenaires = new ArrayList<Partenaire>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Partenaire partenaire = map(resultSet);
				// partenaire = consulterServicesPartenaire(partenaire);
				partenaires.add(partenaire);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return partenaires;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des partenaires (un ResultSet)
	 * et un bean Partenaire.
	 */
	private Partenaire map(ResultSet resultSet) throws SQLException {
		Partenaire partenaire = new Partenaire();
		partenaire.setId(resultSet.getLong("id_partenaire"));
		partenaire.setNom(resultSet.getString("nom"));
		partenaire.setType(resultSet.getString("type"));
		partenaire.setDescription(resultSet.getString("description"));
		partenaire.setWebsite(resultSet.getString("website"));

		return partenaire;
	}

	@Override
	public void modifier(Partenaire partenaire) throws DatabaseException {

		modifier(SQL_UPDATE, partenaire.getNom(), partenaire.getType(), partenaire.getDescription(),
				partenaire.getWebsite(),

				partenaire.getId());

	}

	@Override
	public Partenaire creer(Partenaire partenaire) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, partenaire.getNom(),
					partenaire.getType(), partenaire.getDescription(), partenaire.getWebsite());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de la partenaire, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				partenaire = retrouver(new Partenaire((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du partenaire en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return partenaire;
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
