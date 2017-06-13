package ch.appciip.model;

import java.util.List;

import ch.appciip.bean.Weather;

public interface WeatherManager {
	Weather read(Weather weather) throws DatabaseException;

	void delete(Weather weather) throws DatabaseException;

	List<Weather> list() throws DatabaseException;

	Weather create(Weather weather) throws DatabaseException;

	void update(Weather weather) throws DatabaseException;

}
