package modele;

import java.util.List;

import beans.Outil;

public interface OutilManager {
	Outil retrouver(Outil outil) throws DatabaseException;

	void supprimer(Outil outil) throws DatabaseException;

	List<Outil> lister() throws DatabaseException;

	void modifier(Outil outil) throws DatabaseException;

	Outil creer(Outil outil) throws DatabaseException;

}
