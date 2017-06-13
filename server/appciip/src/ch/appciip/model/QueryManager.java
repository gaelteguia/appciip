package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.Query;

public interface QueryManager {
	Query read(Query query) throws DatabaseException;

	void delete(Query query) throws DatabaseException;

	List<Query> list() throws DatabaseException;

	void update(Query query) throws DatabaseException;

	Query create(Query query) throws DatabaseException;

}
