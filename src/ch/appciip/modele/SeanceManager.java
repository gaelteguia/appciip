package modele;

import java.util.List;

import beans.Seance;

public interface SeanceManager {
	Seance retrouver(Seance droit) throws DatabaseException;

	void supprimer(Seance droit) throws DatabaseException;

	List<Seance> lister() throws DatabaseException;

	void modifier(Seance droit) throws DatabaseException;

	Seance creer(Seance droit) throws DatabaseException;

}
