package modele;

import java.util.List;

import beans.Fonction;
import beans.Comite;

public interface FonctionManager {
	Fonction retrouver(Fonction fonction) throws DatabaseException;

	void supprimer(Fonction fonction) throws DatabaseException;

	List<Fonction> lister() throws DatabaseException;

	void modifier(Fonction fonction) throws DatabaseException;

	Fonction creer(Fonction fonction) throws DatabaseException;

	Fonction associerComite(Fonction fonction, Localisation profil) throws DatabaseException;

	Fonction supprimerComite(Fonction fonction, Localisation profil) throws DatabaseException;

}
