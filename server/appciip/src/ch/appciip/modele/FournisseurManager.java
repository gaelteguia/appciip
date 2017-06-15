package modele;

import java.util.List;

import beans.Fournisseur;
import beans.Comite;

public interface FournisseurManager {
	Fournisseur retrouver(Fournisseur fournisseur) throws DatabaseException;

	void supprimer(Fournisseur fournisseur) throws DatabaseException;

	List<Fournisseur> lister() throws DatabaseException;

	void modifier(Fournisseur fournisseur) throws DatabaseException;

	Fournisseur creer(Fournisseur fournisseur) throws DatabaseException;

	Fournisseur associerComite(Fournisseur fournisseur, Localisation profil) throws DatabaseException;

	Fournisseur supprimerComite(Fournisseur fournisseur, Localisation profil) throws DatabaseException;

}
