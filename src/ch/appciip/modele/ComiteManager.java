package modele;

import java.util.List;

import beans.Seance;
import beans.Comite;

public interface ComiteManager {
	Localisation retrouver(Localisation profil) throws DatabaseException;

	List<Localisation> lister() throws DatabaseException;

	void modifier(Localisation profil) throws DatabaseException;

	Localisation creer(Localisation profil) throws DatabaseException;

	Localisation associerSeance(Localisation profil, Seance droit) throws DatabaseException;

	void supprimer(Localisation profil) throws DatabaseException;

	Localisation supprimerSeance(Localisation profil, Seance droit) throws DatabaseException;

}
