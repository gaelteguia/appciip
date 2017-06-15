package modele;

import java.util.List;

import beans.Distribution;

public interface DistributionManager {
	Distribution retrouver(Distribution distribution) throws DatabaseException;

	void supprimer(Distribution distribution) throws DatabaseException;

	List<Distribution> lister() throws DatabaseException;

	void modifier(Distribution distribution) throws DatabaseException;

	Distribution creer(Distribution distribution) throws DatabaseException;

}
