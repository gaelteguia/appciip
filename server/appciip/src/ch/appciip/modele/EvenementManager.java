package modele;

import java.util.List;

import beans.Evenement;

public interface EvenementManager {
	Evenement retrouver(Evenement evenement) throws DatabaseException;

	void supprimer(Evenement evenement) throws DatabaseException;

	List<Evenement> lister() throws DatabaseException;

	void modifier(Evenement evenement) throws DatabaseException;

	Evenement creer(Evenement evenement) throws DatabaseException;

}
