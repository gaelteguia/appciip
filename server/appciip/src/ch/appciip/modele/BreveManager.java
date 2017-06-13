package modele;

import java.util.List;

import beans.Breve;

public interface BreveManager {
	Breve retrouver(Breve outil) throws DatabaseException;

	void supprimer(Breve outil) throws DatabaseException;

	List<Breve> lister() throws DatabaseException;

	void modifier(Breve outil) throws DatabaseException;

	Breve creer(Breve outil) throws DatabaseException;

}
