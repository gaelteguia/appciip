package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.Institution;

public interface InstitutionManager {
	Institution read(Institution institution) throws DatabaseException;

	void delete(Institution institution) throws DatabaseException;

	List<Institution> list() throws DatabaseException;

	void update(Institution institution) throws DatabaseException;

	Institution create(Institution institution) throws DatabaseException;

	List<Institution> list(Institution institution) throws DatabaseException;

}
