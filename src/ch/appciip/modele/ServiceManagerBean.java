package modele;

import static modele.Utilitaire.fermeturesSilencieuses;
import static modele.Utilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Adresse;
import beans.Fonction;
import beans.Service;
import beans.Collaborateur;

public class ServiceManagerBean implements ServiceManager {

	private static final String SQL_SELECT_PAR_ACRONYME = "SELECT * FROM service WHERE acronyme =?";
	private static final String SQL_SELECT = "SELECT * FROM service";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM service WHERE id_service = ?";
	private static final String SQL_INSERT = "INSERT INTO service (nom,acronyme,id_departement,id_superviseur) VALUES(UPPER(?),UPPER(?),?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM service WHERE id_service = ?";
	private static final String SQL_UPDATE = "UPDATE service SET nom=UPPER(?),acronyme=UPPER(?),id_departement=?,id_superviseur=? WHERE id_service =?";

	private Factory factory;

	ServiceManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface ServiceManager */
	@Override
	public Service retrouver(Service service) throws DatabaseException {
		if (service.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, service.getId());
		return retrouver(SQL_SELECT_PAR_ACRONYME, service.getAcronyme());
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface ServiceManager */
	@Override
	public Service creer(Service service) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		Long idDepartement = null;
		Long idSuperviseur = null;
		if (service.getDepartement() != null)
			idDepartement = service.getDepartement().getId();

		if (service.getSuperviseur() != null)
			idSuperviseur = service.getSuperviseur().getId();

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, service.getNom(),
					service.getAcronyme(), idDepartement, idSuperviseur);
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation du service, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				service = retrouver(new Service((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException("�chec de la cr�ation du service en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return service;
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un collaborateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Service retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Service service = null;

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
				service = map(resultSet);
				service = consulterAdressesService(service);
				service = consulterFonctionsService(service);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return service;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface ServiceManager */
	@Override
	public void supprimer(Service service) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		Long idDepartement = service.getDepartement().getId();
		String sql = "UPDATE service SET id_departement =? WHERE id_service = ?";
		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, service.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la suppression du service, aucune ligne supprim�e de la table.");
			} else {
				List<Service> listeServices = lister(service);

				for (Service s : listeServices) {
					modifier(sql, idDepartement, s.getId());

				}
				service.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface ServiceManager */
	@Override
	public List<Service> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Service> services = new ArrayList<Service>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Service service = map(resultSet);
				service = consulterAdressesService(service);
				service = consulterFonctionsService(service);
				services.add(service);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return services;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface ServiceManager */
	@Override
	public List<Service> lister(Service service) throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Service> services = new ArrayList<Service>();
		String sql = "SELECT * FROM service s WHERE id_departement = " + service.getId();
		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Service s = map(resultSet);
				s = consulterAdressesService(s);
				s = consulterFonctionsService(s);
				services.add(service);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return services;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des collaborateurs (un
	 * ResultSet) et un bean Service.
	 */
	private Service map(ResultSet resultSet) throws SQLException {
		Service service = new Service();
		service.setId(resultSet.getLong("id_service"));
		service.setNom(resultSet.getString("nom"));
		service.setAcronyme(resultSet.getString("acronyme"));

		Long id = resultSet.getLong("id_departement");
		if (id != resultSet.getLong("id_service")) {
			Service departement = retrouver(new Service(id));
			service.setDepartement(departement);
		}

		service.setSuperviseur(superviseur(resultSet.getLong("id_superviseur")));

		return service;
	}

	/*
	 * Methode speciale permettant d'eviter un traitement recursif(en appellant
	 * la methode retrouver(Collaborateur) de CollaborateurManager), voire une
	 * boucle infinie, dans le cas par exemple o� on cherche un collaborateur
	 * qui est superviseur de son propre service.
	 */
	private Utilisateur superviseur(Long idCollaborateur) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Utilisateur collaborateur = new Utilisateur(idCollaborateur);
		String sql = "SELECT nom, prenom FROM collaborateur WHERE id_collaborateur =?";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, idCollaborateur);
			resultSet = preparedStatement.executeQuery();
			/* Parcours de la ligne de donn�es retourn�e dans le ResultSet */
			if (resultSet.next()) {
				collaborateur = new Utilisateur(resultSet.getString("nom"), resultSet.getString("prenom"));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		return collaborateur;

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

	private Service consulterAdressesService(Service service) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Adresse> uneListe = new ArrayList<Adresse>();
		String sql = "SELECT * FROM adresse L WHERE id_adresse IN (SELECT id_adresse FROM adresse_service WHERE adresse_service.id_service=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, service.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				uneListe.add(new Adresse(resultSet.getString("nom"), resultSet.getString("ville"),
						resultSet.getInt("npa"), resultSet.getString("rue"), resultSet.getInt("no_rue"),
						resultSet.getString("pays"), resultSet.getString("telephone")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		service.setAdresses(uneListe);
		return service;
	}

	private Service consulterFonctionsService(Service service) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Fonction> uneListe = new ArrayList<Fonction>();
		String sql = "SELECT * FROM fonction INNER JOIN service" + " ON fonction.id_service = service.id_service"
				+ " WHERE service.id_service=?";
		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, service.getId());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				uneListe.add(new Fonction(resultSet.getString("nom")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		service.setFonctions(uneListe);
		return service;
	}

	@Override
	public void modifier(Service service) throws DatabaseException {

		Long idDepartement = null;
		Long idSuperviseur = null;
		if (service.getDepartement() != null)
			idDepartement = service.getDepartement().getId();

		if (service.getSuperviseur() != null)
			idSuperviseur = service.getSuperviseur().getId();

		modifier(SQL_UPDATE, service.getNom(), service.getAcronyme(), idDepartement, idSuperviseur, service.getId());
	}

}
