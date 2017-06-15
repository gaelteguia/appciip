package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Collaborateur;
import beans.Horaire;
import beans.Voiture;

public class HoraireManagerBean implements HoraireManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM horaire WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM horaire";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM horaire WHERE id_horaire = ?";
	private static final String SQL_INSERT = "INSERT INTO horaire (nom) VALUES(UPPER(?))";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM horaire WHERE id_horaire = ?";
	private static final String SQL_UPDATE = "UPDATE horaire SET nom=UPPER(?) WHERE id_horaire =?";

	private Factory factory;

	public HoraireManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface HoraireManager */
	@Override
	public Horaire retrouver(Horaire horaire) throws DatabaseException {

		if (horaire.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, horaire.getId());

		return retrouver(SQL_SELECT_PAR_NOM, horaire.getNom());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Horaire retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Horaire horaire = null;

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
				horaire = map(resultSet);
				horaire = consulterVoituresHoraire(horaire);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return horaire;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface HoraireManager */
	@Override
	public void supprimer(Horaire horaire) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, horaire.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de la horaire, aucune ligne supprim�e de la table.");
			} else {

				horaire.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface HoraireManager */
	@Override
	public List<Horaire> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Horaire> horaires = new ArrayList<Horaire>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Horaire horaire = map(resultSet);
				horaire = consulterVoituresHoraire(horaire);
				horaires.add(horaire);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return horaires;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des horaires (un ResultSet) et
	 * un bean Horaire.
	 */
	private Horaire map(ResultSet resultSet) throws SQLException {
		Horaire horaire = new Horaire();
		horaire.setId(resultSet.getLong("id_horaire"));
		horaire.setNom(resultSet.getString("nom"));

		return horaire;
	}

	@Override
	public void modifier(Horaire horaire) throws DatabaseException {
		modifier(SQL_UPDATE, horaire.getNom(), horaire.getId());

	}

	@Override
	public Horaire creer(Horaire horaire) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, horaire.getNom());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				horaire = retrouver(new Horaire((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException("�chec de la cr�ation du horaire en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return horaire;
	}

	@Override
	public Horaire supprimerVoiture(Horaire horaire, Voiture voiture) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		String sql = "DELETE FROM voiture_horaire WHERE id_horaire = ? AND id_voiture = ?";

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, sql, true, horaire.getId(), voiture.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du lien entre la horaire et le voiture, aucune ligne supprim�e de la table.");
			} else {
				horaire = retrouver(horaire);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
		return horaire;
	}

	@Override
	public Horaire supprimerCollaborateur(Horaire horaire, Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		String sql = "DELETE FROM collaborateur_horaire WHERE id_horaire = ? AND id_collaborateur = ?";

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, sql, true, horaire.getId(),
					collaborateur.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression du lien, aucune ligne supprim�e de la table.");
			} else {
				horaire = retrouver(horaire);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
		return horaire;
	}

	@Override
	public Horaire associerVoiture(Horaire horaire, Voiture voiture) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO voiture_horaire (id_horaire,id_voiture) VALUES(?,?)";
		if (horaire != null && voiture != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, horaire.getId(),
						voiture.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					horaire = consulterVoituresHoraire(horaire);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return horaire;
	}

	@Override
	public Horaire associerCollaborateur(Horaire horaire, Utilisateur collaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO collaborateur_horaire (id_horaire,id_collaborateur) VALUES(?,?)";
		if (horaire != null && collaborateur != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, horaire.getId(),
						collaborateur.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					horaire = consulterVoituresHoraire(horaire);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return horaire;
	}

	private Horaire consulterVoituresHoraire(Horaire horaire) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Voiture> uneListe = new ArrayList<Voiture>();
		String sql = "SELECT DISTINCT * FROM voiture S WHERE id_voiture IN (SELECT id_voiture FROM voiture_horaire WHERE voiture_horaire.id_horaire=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, horaire.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Voiture(resultSet.getString("no_plaques")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		horaire.setVoitures(uneListe);
		return horaire;
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