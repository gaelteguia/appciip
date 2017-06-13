package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Directive;

public class DirectiveManagerBean implements DirectiveManager {
	private static final String SQL_SELECT_PAR_TITRE = "SELECT * FROM directive WHERE titre =?";
	private static final String SQL_SELECT = "SELECT * FROM directive";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM directive WHERE id_directive = ?";
	private static final String SQL_INSERT = "INSERT INTO directive (titre,theme,description,lien) VALUES(UPPER(?),?,?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM directive WHERE id_directive = ?";
	private static final String SQL_UPDATE = "UPDATE directive SET titre=UPPER(?),theme=?,description=?,lien=? WHERE id_directive =?";

	private Factory factory;

	public DirectiveManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface DirectiveManager */
	@Override
	public Directive retrouver(Directive directive) throws DatabaseException {

		if (directive.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, directive.getId());

		return retrouver(SQL_SELECT_PAR_TITRE, directive.getTitre());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Directive retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Directive directive = null;

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
				directive = map(resultSet);
				// directive = consulterServicesDirective(directive);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return directive;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface DirectiveManager */
	@Override
	public void supprimer(Directive directive) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, directive.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de la directive, aucune ligne supprim�e de la table.");
			} else {

				directive.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface DirectiveManager */
	@Override
	public List<Directive> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Directive> directives = new ArrayList<Directive>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Directive directive = map(resultSet);
				// directive = consulterServicesDirective(directive);
				directives.add(directive);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return directives;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des directives (un ResultSet)
	 * et un bean Directive.
	 */
	private Directive map(ResultSet resultSet) throws SQLException {
		Directive directive = new Directive();
		directive.setId(resultSet.getLong("id_directive"));
		directive.setTitre(resultSet.getString("titre"));
		directive.setTheme(resultSet.getString("theme"));
		directive.setDescription(resultSet.getString("description"));
		directive.setLien(resultSet.getString("lien"));

		return directive;
	}

	@Override
	public void modifier(Directive directive) throws DatabaseException {
		modifier(SQL_UPDATE, directive.getTitre(), directive.getTheme(), directive.getDescription(),
				directive.getLien(),

				directive.getId());

	}

	@Override
	public Directive creer(Directive directive) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, directive.getTitre(),
					directive.getTheme(), directive.getDescription(), directive.getLien());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de la directive, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				directive = retrouver(new Directive((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du directive en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return directive;
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
