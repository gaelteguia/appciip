package forms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Adresse;
import beans.Collaborateur;
import beans.Horaire;
import beans.Service;
import beans.Voiture;
import beans.Voiture;
import modele.VoitureManager;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DAOException;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.SeanceManager;
import modele.ServiceManager;

public final class VoitureForm extends Form {

	private String resultat;
	private Map<String, String> erreurs = new HashMap<String, String>();
	private VoitureManager clientDao;

	public VoitureForm(VoitureManager clientDao) {
		this.clientDao = clientDao;
	}

	public VoitureForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
			FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s, VoitureManager v) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.l = l;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;
		this.v = v;
	}

	public Map<String, String> getErreurs() {
		return erreurs;
	}

	public String getResultat() {
		return resultat;
	}

	public Voiture creerVoiture(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String noPlaques = tokens[0];

		Utilisateur collaborateur = new Utilisateur(tokens[1]);

		collaborateur = u.retrouver(collaborateur);

		Voiture voiture = new Voiture();

		if (!noPlaques.trim().isEmpty()) {
			try {
				traiterNoPlaques(voiture, noPlaques);
				traiterCollaborateur(voiture, collaborateur);

				if (v.retrouver(voiture) == null) {
					voiture = v.creer(voiture);
					setResultat("Succès de la création.");
				} else {

					voiture = v.retrouver(voiture);
					traiterCollaborateur(voiture, collaborateur);

					v.modifier(voiture);
					setResultat("Voiture déjà existante.");
				}

			} catch (DatabaseException e) {
				setErreur("imprévu", "Erreur imprévue lors de la création.");
				setResultat(
						"Échec de la création de l'application : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("Échec de la création de l'application.");
		}

		return voiture;

	}

	public Voiture consulterVoiture(HttpServletRequest request) {
		Voiture telephone = new Voiture();
		/* R�cup�ration de l'id de l'telephone choisie */
		String idVoiture = getValeurChamp(request, CHAMP_LISTE_VOITURES);
		Long id = null;
		try {
			id = Long.parseLong(idVoiture);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_VOITURE, "Voiture inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet telephone correspondant dans la session
			 */
			HttpSession session = request.getSession();

			telephone = ((Map<Long, Voiture>) session.getAttribute(SESSION_VOITURES)).get(id);
		}

		return telephone;
	}

}