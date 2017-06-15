package ch.appciip.model;

import static ch.appciip.model.Utility.fermeturesSilencieuses;
import static ch.appciip.model.Utility.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.appciip.bean.Address;
import ch.appciip.bean.Institution;
import ch.appciip.bean.User;

public class InstitutionManagerBean implements InstitutionManager {

	private static final String SQL_SELECT_PAR_ACRONYME = "SELECT * FROM institution WHERE acronyme =?";
	private static final String SQL_SELECT = "SELECT * FROM institution";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM institution WHERE id_institution = ?";
	private static final String SQL_INSERT = "INSERT INTO institution (nom,acronyme,id_departement,id_supervisor) VALUES(UPPER(?),UPPER(?),?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM institution WHERE id_institution = ?";
	private static final String SQL_UPDATE = "UPDATE institution SET nom=UPPER(?),acronyme=UPPER(?),id_departement=?,id_supervisor=? WHERE id_institution =?";

	private Factory factory;

	InstitutionManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * InstitutionManager
	 */
	@Override
	public Institution read(Institution institution) throws DatabaseException {
		if (institution.getId() != null)
			return read(SQL_SELECT_PAR_ID, institution.getId());
		return read(SQL_SELECT_PAR_ACRONYME, institution.getAcronyme());
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * InstitutionManager
	 */
	@Override
	public Institution create(Institution institution) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		Long idDepartment = null;
		Long idSupervisor = null;
		if (institution.getDepartment() != null)
			idDepartment = institution.getDepartment().getId();

		if (institution.getSupervisor() != null)
			idSupervisor = institution.getSupervisor().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, institution.getName(),
					institution.getAcronyme(), idDepartment, idSupervisor);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation du institution, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				institution = read(new Institution((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du institution en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return institution;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un user depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en
	 * param�tres les objets pass�s en argument.
	 */
	private Institution read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Institution institution = null;

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en
			 * arguments et ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, objets);
			resultSet = preparedStatement.executeQuery();
			/*
			 * Parcours de la ligne de donn�es retourn�e dans le ResultSet
			 */
			if (resultSet.next()) {
				institution = map(resultSet);
				institution = consulterAdressesInstitution(institution);
				// institution = consulterFonctionsInstitution(institution);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return institution;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * InstitutionManager
	 */
	@Override
	public void delete(Institution institution) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		Long idDepartment = institution.getDepartment().getId();
		String sql = "UPDATE institution SET id_departement =? WHERE id_institution = ?";
		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, institution.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du institution, aucune ligne supprim�e de la table.");
			} else {
				List<Institution> listeInstitutions = list(institution);

				for (Institution s : listeInstitutions) {
					update(sql, idDepartment, s.getId());

				}
				institution.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * InstitutionManager
	 */
	@Override
	public List<Institution> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Institution> institutions = new ArrayList<Institution>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Institution institution = map(resultSet);
				institution = consulterAdressesInstitution(institution);
				// institution = consulterFonctionsInstitution(institution);
				institutions.add(institution);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return institutions;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * InstitutionManager
	 */
	@Override
	public List<Institution> list(Institution institution) throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Institution> institutions = new ArrayList<Institution>();
		String sql = "SELECT * FROM institution s WHERE id_departement = " + institution.getId();
		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Institution s = map(resultSet);
				s = consulterAdressesInstitution(s);
				// s = consulterFonctionsInstitution(s);
				institutions.add(institution);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return institutions;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des users (un ResultSet) et un
	 * bean Institution.
	 */
	private Institution map(ResultSet resultSet) throws SQLException {
		Institution institution = new Institution();
		institution.setId(resultSet.getLong("id_institution"));
		institution.setName(resultSet.getString("nom"));
		institution.setAcronyme(resultSet.getString("acronyme"));

		Long id = resultSet.getLong("id_departement");
		if (id != resultSet.getLong("id_institution")) {
			Institution departement = read(new Institution(id));
			institution.setDepartment(departement);
		}

		institution.setSupervisor(supervisor(resultSet.getLong("id_supervisor")));

		return institution;
	}

	/*
	 * Methode speciale permettant d'eviter un traitement recursif(en appellant
	 * la methode read(Collaborateur) de CollaborateurManager), voire une boucle
	 * infinie, dans le cas par exemple o� on cherche un user qui est
	 * supervisor de son propre institution.
	 */
	private User supervisor(Long idCollaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		User user = new User(idCollaborateur);
		String sql = "SELECT nom, prenom FROM user WHERE id_user =?";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en
			 * arguments et ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, idCollaborateur);
			resultSet = preparedStatement.executeQuery();
			/*
			 * Parcours de la ligne de donn�es retourn�e dans le ResultSet
			 */
			if (resultSet.next()) {
				user = new User(resultSet.getString("nom"), resultSet.getString("prenom"));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		return user;

	}

	private void update(String sql, Object... objets) throws DatabaseException {
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

	private Institution consulterAdressesInstitution(Institution institution) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Address> uneListe = new ArrayList<Address>();
		String sql = "SELECT * FROM adresse L WHERE id_adresse IN (SELECT id_adresse FROM adresse_institution WHERE adresse_institution.id_institution=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en
			 * arguments et ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, institution.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				uneListe.add(new Address(resultSet.getString("nom"), resultSet.getString("ville"),
						resultSet.getInt("npa"), resultSet.getString("rue"), resultSet.getInt("no_rue"),
						resultSet.getString("pays"), resultSet.getString("telephone")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		institution.setAdresses(uneListe);
		return institution;
	}

	@Override
	public void update(Institution institution) throws DatabaseException {

		Long idDepartment = null;
		Long idSupervisor = null;
		if (institution.getDepartment() != null)
			idDepartment = institution.getDepartment().getId();

		if (institution.getSupervisor() != null)
			idSupervisor = institution.getSupervisor().getId();

		update(SQL_UPDATE, institution.getName(), institution.getAcronyme(), idDepartment, idSupervisor,
				institution.getId());
	}

}
