package modele;

import java.util.List;

import beans.Adresse;
import beans.Service;

public interface AdresseManager {
	Adresse retrouver(Adresse localisation) throws DatabaseException;

	void supprimer(Adresse localisation) throws DatabaseException;

	List<Adresse> lister() throws DatabaseException;

	void modifier(Adresse localisation) throws DatabaseException;

	Adresse creer(Adresse localisation) throws DatabaseException;

	Adresse associerService(Adresse localisation, Service service) throws DatabaseException;

	Adresse supprimerService(Adresse localisation, Service service) throws DatabaseException;

}
