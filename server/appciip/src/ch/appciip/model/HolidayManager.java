package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.Holiday;

public interface HolidayManager {
	Holiday read(Holiday manifestation) throws DatabaseException;

	void delete(Holiday manifestation) throws DatabaseException;

	List<Holiday> list() throws DatabaseException;

	void update(Holiday manifestation) throws DatabaseException;

	Holiday create(Holiday manifestation) throws DatabaseException;

}
