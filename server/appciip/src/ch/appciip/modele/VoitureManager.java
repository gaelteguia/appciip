package modele;

import java.util.List;

import beans.Voiture;

public interface VoitureManager {
	Voiture creer(Voiture voiture) throws DatabaseException;

	Voiture retrouver(Voiture voiture) throws DatabaseException;

	List<Voiture> lister() throws DAOException;

	void supprimer(Voiture client) throws DAOException;

	void modifier(Voiture voiture) throws DatabaseException;
}