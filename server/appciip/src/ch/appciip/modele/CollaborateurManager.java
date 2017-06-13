package modele;

import java.util.ArrayList;
import java.util.List;

import beans.Application;
import beans.Beamer;
import beans.Collaborateur;
import beans.Comite;
import beans.Distribution;
import beans.Imprimante;
import beans.Seance;
import beans.Service;

public interface CollaborateurManager {

	Utilisateur retrouver(Utilisateur utilisateur) throws DatabaseException;

	Utilisateur connecter(Utilisateur utilisateur) throws DatabaseException;

	Utilisateur challenger(Utilisateur utilisateur) throws DatabaseException;

	void supprimer(Utilisateur utilisateur) throws DatabaseException;

	List<Utilisateur> lister() throws DatabaseException;

	List<Utilisateur> anniversaire() throws DatabaseException;

	List<Utilisateur> prochainAnniversaire() throws DatabaseException;

	void modifierMdp(Utilisateur utilisateur) throws DatabaseException;

	void modifierChallenge(Utilisateur utilisateur) throws DatabaseException;

	Utilisateur creer(Utilisateur utilisateur) throws DatabaseException;

	void modifier(Utilisateur utilisateur) throws DatabaseException;

	void modifierActif(Utilisateur utilisateur) throws DatabaseException;

	List<Utilisateur> lister(Utilisateur utilisateur) throws DatabaseException;

	ArrayList<Utilisateur> lectureOLY(String string);

	Utilisateur associerApplication(Utilisateur collaborateur, Application application) throws DatabaseException;

	Utilisateur associerBeamer(Utilisateur collaborateur, Beamer beamer) throws DatabaseException;

	Utilisateur associerComite(Utilisateur collaborateur, Localisation comite) throws DatabaseException;

	Utilisateur associerDistribution(Utilisateur collaborateur, Distribution distribution) throws DatabaseException;

	Utilisateur associerImprimante(Utilisateur collaborateur, Imprimante imprimante) throws DatabaseException;

	Utilisateur associerSeance(Utilisateur collaborateur, Seance seance) throws DatabaseException;

	Utilisateur associerService(Utilisateur collaborateur, Service service) throws DatabaseException;

	Utilisateur associerSuperieur(Utilisateur collaborateur, Utilisateur superieur) throws DatabaseException;

	Utilisateur associerSuppleant(Utilisateur collaborateur, Utilisateur suppleant) throws DatabaseException;

	Utilisateur associerSupplee(Utilisateur collaborateur, Utilisateur supplee) throws DatabaseException;
}
