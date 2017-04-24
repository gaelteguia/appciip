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

public class AddressManagerBean implements AddressManager {
	private static final String SQL_SELECT_PER_NAME = "SELECT * FROM address WHERE name =?";
	private static final String SQL_SELECT = "SELECT * FROM address";
	private static final String SQL_SELECT_PER_ID = "SELECT * FROM address WHERE id_address = ?";
	private static final String SQL_INSERT = "INSERT INTO address (name,telephone,fax,canton,type,etage,cp,number,npa,country,rue,city) VALUES(UPPER(?),?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_DELETE_PER_ID = "DELETE FROM address WHERE id_address = ?";
	private static final String SQL_UPDATE = "UPDATE address SET name=UPPER(?),telephone=?,fax=?,canton=?,type=?,etage=?,cp=?,number=?,npa=?,country=?,rue=?,city=? WHERE id_address =?";

	private Factory factory;

	public AddressManagerBean(Factory factory) {
		this.factory = factory;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * AdresseManager
	 */
	@Override
	public Address read(Address address) throws DatabaseException {

		if (address.getId() != null)
			return read(SQL_SELECT_PER_ID, address.getId());

		return read(SQL_SELECT_PER_NAME, address.getName());
	}

	/*
	 * M�thode g�n�rique utilis�e pour retourner un utilisateur depuis
	 * la base de donn�es, correspondant � la requ�te SQL donn�e prenant
	 * en param�tres les objets pass�s en argument.
	 */
	private Address read(String sql, Object... objets) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Address address = null;

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
				address = map(resultSet);

			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connexion);
		}

		return address;
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * AdresseManager
	 */
	@Override
	public void delete(Address address) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_DELETE_PER_ID, true, address.getId());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la suppression de la address, aucune ligne supprim�e de la table.");
			} else {

				address.setId(null);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(preparedStatement, connexion);
		}
	}

	/*
	 * Impl�mentation de la m�thode d�finie dans l'interface
	 * AdresseManager
	 */
	@Override
	public List<Address> list() throws DatabaseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Address> addresss = new ArrayList<Address>();

		try {
			connection = factory.getConnection();
			preparedStatement = connection.prepareStatement(SQL_SELECT);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Address address = map(resultSet);

				addresss.add(address);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(resultSet, preparedStatement, connection);
		}

		return addresss;
	}

	/*
	 * Simple m�thode utilitaire permettant de faire la correspondance (le
	 * mapping) entre une ligne issue de la table des addresss (un ResultSet) et
	 * un bean Adresse.
	 */
	private Address map(ResultSet resultSet) throws SQLException {
		Address address = new Address();
		address.setId(resultSet.getLong("id_address"));
		address.setName(resultSet.getString("name"));
		address.setTelephone(resultSet.getString("telephone"));
		address.setFax(resultSet.getString("fax"));
		address.setCanton(resultSet.getString("canton"));
		address.setType(resultSet.getString("type"));
		address.setEtage(resultSet.getInt("etage"));
		address.setTelephone(resultSet.getString("cp"));
		address.setNumber(resultSet.getInt("number"));
		address.setNpa(resultSet.getInt("npa"));
		address.setCountry(resultSet.getString("country"));
		address.setRue(resultSet.getString("rue"));
		address.setCity(resultSet.getString("city"));

		return address;
	}

	@Override
	public void update(Address address) throws DatabaseException {
		update(SQL_UPDATE, address.getName(), address.getTelephone(), address.getFax(), address.getCanton(),
				address.getType(), address.getEtage(), address.getCp(), address.getNumber(), address.getNpa(),
				address.getCountry(), address.getRue(), address.getCity(), address.getId());

	}

	@Override
	public Address create(Address address) throws DatabaseException {
		Connection connexion = null;
		PreparedStatement preparedStatement = null;
		ResultSet valeursAutoGenerees = null;

		try {
			connexion = factory.getConnection();
			preparedStatement = initialisationRequetePreparee(connexion, SQL_INSERT, true, address.getName(),
					address.getTelephone(), address.getFax(), address.getCanton(), address.getType(),
					address.getEtage(), address.getCp(), address.getNumber(), address.getNpa(), address.getCountry(),
					address.getRue(), address.getCity());
			int statut = preparedStatement.executeUpdate();
			if (statut == 0) {
				throw new DatabaseException(
						"�chec de la cr�ation de la address, aucune ligne ajout�e dans la table.");
			}
			valeursAutoGenerees = preparedStatement.getGeneratedKeys();
			if (valeursAutoGenerees.next()) {

				address = read(new Address((valeursAutoGenerees.getLong(1))));

			} else {
				throw new DatabaseException(
						"�chec de la cr�ation du address en base, aucun ID auto-g�n�r� retourn�.");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			fermeturesSilencieuses(valeursAutoGenerees, preparedStatement, connexion);
		}

		return address;
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

}
