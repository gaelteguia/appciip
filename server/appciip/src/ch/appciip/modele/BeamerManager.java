package modele;

import java.util.List;

import beans.Beamer;

public interface BeamerManager {
	Beamer retrouver(Beamer application) throws DatabaseException;

	void supprimer(Beamer application) throws DatabaseException;

	List<Beamer> lister() throws DatabaseException;

	Beamer creer(Beamer application) throws DatabaseException;

	void modifier(Beamer application) throws DatabaseException;

}
