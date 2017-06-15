package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.Address;

public interface AddressManager {
	Address read(Address address) throws DatabaseException;

	void delete(Address address) throws DatabaseException;

	List<Address> list() throws DatabaseException;

	void update(Address address) throws DatabaseException;

	Address create(Address address) throws DatabaseException;

}
