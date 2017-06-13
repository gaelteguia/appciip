package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.Manifestation;

public interface ManifestationManager {
	Manifestation read(Manifestation manifestation) throws DatabaseException;

	void delete(Manifestation manifestation) throws DatabaseException;

	List<Manifestation> list() throws DatabaseException;

	void update(Manifestation manifestation) throws DatabaseException;

	Manifestation create(Manifestation manifestation) throws DatabaseException;

}
