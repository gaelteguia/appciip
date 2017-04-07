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
import beans.Service;

public class AdresseManagerBean implements AdresseManager {
	private static final String SQL_SELECT_PAR_NOM = "SELECT * FROM adresse WHERE nom =?";
	private static final String SQL_SELECT = "SELECT * FROM adresse";
	private static final String SQL_SELECT_PAR_ID = "SELECT * FROM adresse WHERE id_adresse = ?";
	private static final String SQL_INSERT = "INSERT INTO adresse (nom,telephone,fax,canton,type,etage,cp,no_rue,npa,pays,rue,ville) VALUES(UPPER(?),?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_DELETE_PAR_ID = "DELETE FROM adresse WHERE id_adresse = ?";
	private static final String SQL_UPDATE = "UPDATE adresse SET nom=UPPER(?),telephone=?,fax=?,canton=?,type=?,etage=?,cp=?,no_rue=?,npa=?,pays=?,rue=?,ville=? WHERE id_adresse =?";

	private Factory factory;

	public AdresseManagerBean(Factory factory) {
		this.factory = factory;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface AdresseManager */
	@Override
	public Adresse retrouver(Adresse adresse) throws DatabaseException {

		if (adresse.getId() != null)
			return retrouver(SQL_SELECT_PAR_ID, adresse.getId());

		return retrouver(SQL_SELECT_PAR_NOM, adresse.getNom());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis la base
	 * de donn�es, correspondant � la requ�te SQL donn�e prenant en param�tres
	 * les objets pass�s en argument.
	 */
	private Adresse retrouver(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Adresse adresse = null;

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
				adresse = map(resultSet);
				adresse = consulterServicesAdresse(adresse);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return adresse;
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface AdresseManager */
	@Override
	public void supprimer(Adresse adresse) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PAR_ID, true, adresse.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de la adresse, aucune ligne supprim�e de la table.");
			} else {

				adresse.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/* Impl�mentation de la m�thode d�finie dans l'interface AdresseManager */
	@Override
	public List<Adresse> lister() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Adresse> adresses = new ArrayList<Adresse>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Adresse adresse = map(resultSet);
				adresse = consulterServicesAdresse(adresse);
				adresses.add(adresse);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return adresses;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des adresses (un ResultSet) et
	 * un bean Adresse.
	 */
	private Adresse map(ResultSet resultSet) throws SQLException {
		Adresse adresse = new Adresse();
		adresse.setId(resultSet.getLong("id_adresse"));
		adresse.setNom(resultSet.getString("nom"));
		adresse.setTelephone(resultSet.getString("telephone"));
		adresse.setFax(resultSet.getString("fax"));
		adresse.setCanton(resultSet.getString("canton"));
		adresse.setType(resultSet.getString("type"));
		adresse.setEtage(resultSet.getInt("etage"));
		adresse.setTelephone(resultSet.getString("cp"));
		adresse.setNoRue(resultSet.getInt("no_rue"));
		adresse.setNpa(resultSet.getInt("npa"));
		adresse.setPays(resultSet.getString("pays"));
		adresse.setRue(resultSet.getString("rue"));
		adresse.setVille(resultSet.getString("ville"));

		return adresse;
	}

	@Override
	public void modifier(Adresse adresse) throws DatabaseException {
		modifier(SQL_UPDATE, adresse.getNom(), adresse.getTelephone(), adresse.getFax(), adresse.getCanton(),
				adresse.getType(), adresse.getEtage(), adresse.getCp(), adresse.getNoRue(), adresse.getNpa(),
				adresse.getPays(), adresse.getRue(), adresse.getVille(), adresse.getId());

	}

	@Override
	public Adresse creer(Adresse adresse) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, adresse.getNom(),
					adresse.getTelephone(), adresse.getFax(), adresse.getCanton(), adresse.getType(),
					adresse.getEtage(), adresse.getCp(), adresse.getNoRue(), adresse.getNpa(), adresse.getPays(),
					adresse.getRue(), adresse.getVille());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException("�chec de la cr�ation de la adresse, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				adresse = retrouver(new Adresse((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException("�chec de la cr�ation du adresse en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return adresse;
	}

	@Override
	public Adresse supprimerService(Adresse adresse, Service service) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		String sql = "DELETE FROM adresse_service WHERE id_adresse = ? AND id_service = ?";

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, sql, true, adresse.getId(), service.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression du lien entre la adresse et le service, aucune ligne supprim�e de la table.");
			} else {
				adresse = retrouver(adresse);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
		return adresse;
	}

	@Override
	public Adresse associerService(Adresse adresse, Service service) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;
		String sql = "INSERT INTO adresse_service (id_adresse,id_service) VALUES(?,?)";
		if (adresse != null && service != null) {
			try {
				connexion = factory.getConnection();
				preparedStatement = initialisationRequetePreparee(connexion, sql, true, adresse.getId(),
						service.getId());
				int statut = preparedStatement.executeUpdate();
				if (statut == 0) {
					throw new DatabaseException("�chec de la cr�ation du lien, aucune ligne ajout�e dans la table.");
				}

				else {
					adresse = consulterServicesAdresse(adresse);

				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
			}
		}
		return adresse;
	}

	private Adresse consulterServicesAdresse(Adresse adresse) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<Service> uneListe = new ArrayList<Service>();
		String sql = "SELECT DISTINCT * FROM service S WHERE id_service IN (SELECT id_service FROM adresse_service WHERE adresse_service.id_adresse=?)";

		try {
			/* R�cup�ration d'une connexion depuis la Factory */
			connexion = factory.getConnection();
			/*
			 * Pr�paration de la requ�te avec les objets pass�s en arguments et
			 * ex�cution.
			 */
			preparedStatement = initialisationRequetePreparee(connexion, sql, false, adresse.getId());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				uneListe.add(new Service(resultSet.getString("acronyme")));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}
		adresse.setServices(uneListe);
		return adresse;
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
