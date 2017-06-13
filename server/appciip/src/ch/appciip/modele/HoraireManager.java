package modele;

import java.util.List;

import beans.Collaborateur;
import beans.Horaire;
import beans.Voiture;

public interface HoraireManager {
	Horaire retrouver(Horaire horaire) throws DatabaseException;

	void supprimer(Horaire horaire) throws DatabaseException;

	List<Horaire> lister() throws DatabaseException;

	void modifier(Horaire horaire) throws DatabaseException;

	Horaire creer(Horaire horaire) throws DatabaseException;

	Horaire associerVoiture(Horaire horaire, Voiture voiture) throws DatabaseException;

	Horaire supprimerVoiture(Horaire horaire, Voiture voiture) throws DatabaseException;

	Horaire associerCollaborateur(Horaire horaire, Utilisateur collaborateur) throws DatabaseException;

	Horaire supprimerCollaborateur(Horaire horaire, Utilisateur collaborateur) throws DatabaseException;
}