package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.Canton;

public interface CantonManager {
	Canton read(Canton canton) throws DatabaseException;

	List<Canton> list() throws DatabaseException;

	void update(Canton canton) throws DatabaseException;

	Canton create(Canton canton) throws DatabaseException;

	void delete(Canton canton) throws DatabaseException;

}
