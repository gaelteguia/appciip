package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.Localisation;

public interface LocalisationManager {
	Localisation read(Localisation localisation) throws DatabaseException;

	void delete(Localisation localisation) throws DatabaseException;

	List<Localisation> list() throws DatabaseException;

	void update(Localisation localisation) throws DatabaseException;

	Localisation create(Localisation localisation) throws DatabaseException;

}
