package modele;

import java.util.List;

import beans.Salle;

public interface SalleManager {
	Salle retrouver(Salle salle) throws DatabaseException;

	void supprimer(Salle salle) throws DatabaseException;

	List<Salle> lister() throws DatabaseException;

	void modifier(Salle salle) throws DatabaseException;

	Salle creer(Salle salle) throws DatabaseException;

}
