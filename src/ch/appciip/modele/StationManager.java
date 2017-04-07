package modele;

import java.util.List;

import beans.Station;

public interface StationManager {
	Station retrouver(Station application) throws DatabaseException;

	void supprimer(Station application) throws DatabaseException;

	List<Station> lister() throws DatabaseException;

	Station creer(Station application) throws DatabaseException;

	void modifier(Station application) throws DatabaseException;

}
