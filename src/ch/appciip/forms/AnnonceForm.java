package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Annonce;
import beans.Collaborateur;
import modele.AnnonceManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.FonctionManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class AnnonceForm extends Form {

	public AnnonceForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, FonctionManager m,
			AnnonceManager an, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;

		this.m = m;
		this.an = an;
		this.p = p;
		this.s = s;
	}

	public Annonce consulterAnnonce(HttpServletRequest request) {
		Annonce annonce = new Annonce();
		/* R�cup�ration de l'id de la annonce choisie */
		String idAnnonce = getValeurChamp(request, CHAMP_LISTE_ANNONCES);
		Long id = null;
		try {
			id = Long.parseLong(idAnnonce);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_ANNONCE, "Annonce inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet annonce correspondant dans la session
			 */
			HttpSession session = request.getSession();
			annonce = ((Map<Long, Annonce>) session.getAttribute(SESSION_ANNONCES)).get(id);
		}

		return annonce;
	}

	public Annonce creerAnnonce(HttpServletRequest request) {
		Utilisateur utilisateur;

		/*
		 * Si l'utilisateur choisit un utilisateur d�j� existant, pas de
		 * validation � effectuer
		 */
		String choixNouvelAuteur = getValeurChamp(request, CHAMP_CHOIX_UTILISATEUR);
		if (ANCIEN_UTILISATEUR.equals(choixNouvelAuteur)) {
			/* R�cup�ration de l'id de l'utilisateur choisi */
			String idAncienAuteur = getValeurChamp(request, CHAMP_LISTE_UTILISATEURS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienAuteur);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_UTILISATEUR, "Auteur inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			utilisateur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			utilisateur = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une annonce.
		 */

		String titre = getValeurChamp(request, CHAMP_TITRE);
		String contenu = getValeurChamp(request, CHAMP_CONTENU);
		String date = getValeurChamp(request, CHAMP_DATE);

		Annonce annonce = new Annonce();

		try {
			traiterAuteur(annonce, utilisateur);

			traiterTitre(annonce, titre);
			traiterContenu(annonce, contenu);
			traiterDate(annonce, date);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					annonce = an.creer(annonce);
					setResultat("Succ�s de la cr�ation de la annonce.");
				} else {
					setResultat("�chec de la cr�ation de la annonce.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de la annonce : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return annonce;
	}

	public Annonce creerAnnonce(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String titre = tokens[0];

		String contenu = tokens[1];

		String date = tokens[2];

		String echeance = tokens[3];

		Utilisateur auteur = new Utilisateur(tokens[4]);

		auteur = u.retrouver(auteur);

		Annonce annonce = new Annonce();

		if (!titre.trim().isEmpty()) {
			try {
				traiterTitre(annonce, titre);
				traiterDate(annonce, date);

				traiterEcheance(annonce, echeance);
				traiterAuteur(annonce, auteur);

				traiterContenu(annonce, contenu);

				if (an.retrouver(annonce) == null) {
					annonce = an.creer(annonce);
					setResultat("Succ�s de la cr�ation de l'annonce.");
				} else {

					annonce = an.retrouver(annonce);
					traiterAuteur(annonce, auteur);
					traiterDate(annonce, date);
					traiterEcheance(annonce, echeance);
					traiterContenu(annonce, contenu);
					an.modifier(annonce);
					setResultat("Annonce déjà existant.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation de l'annonce : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation de l'annonce.");
		}

		return annonce;

	}

	public Annonce retrouverAnnonce(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String id = getValeurChamp(request, CHAMP_ID);

		Annonce annonce = new Annonce();
		traiterId(annonce, id);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				annonce = an.retrouver(annonce);
				setResultat("Succ�s de la recherche de la annonce.");
			} else

				setResultat("�chec de la recherche de la annonce. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la recherche.");
			setResultat("�chec de la recherche.");
		}

		return annonce;
	}

	public Annonce modifierAnnonce(HttpServletRequest request) {
		Utilisateur utilisateur;

		/*
		 * Si l'utilisateur choisit un utilisateur d�j� existant, pas de
		 * validation � effectuer
		 */
		String choixNouvelAuteur = getValeurChamp(request, CHAMP_CHOIX_UTILISATEUR);
		if (ANCIEN_UTILISATEUR.equals(choixNouvelAuteur)) {
			/* R�cup�ration de l'id de l'utilisateur choisi */
			String idAncienAuteur = getValeurChamp(request, CHAMP_LISTE_UTILISATEURS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienAuteur);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_UTILISATEUR, "Auteur inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			utilisateur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			utilisateur = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une annonce.
		 */

		String titre = getValeurChamp(request, CHAMP_TITRE);
		String contenu = getValeurChamp(request, CHAMP_CONTENU);
		String date = getValeurChamp(request, CHAMP_DATE);

		String id = getValeurChamp(request, CHAMP_ID);

		Annonce annonce = new Annonce();
		if (!id.trim().isEmpty())
			traiterId(annonce, id);

		try {
			traiterAuteur(annonce, utilisateur);

			traiterTitre(annonce, titre);
			traiterContenu(annonce, contenu);
			traiterDate(annonce, date);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					an.modifier(annonce);
					setResultat("Succ�s de la modification de la annonce.");
				} else {
					setResultat("�chec de la modification de la annonce.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification de la annonce : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return annonce;
	}

}
