package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Application;
import beans.Collaborateur;
import beans.Fournisseur;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.FournisseurManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class ApplicationForm extends Form {

	public ApplicationForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
			FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s, FournisseurManager f) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.l = l;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;
		this.f = f;
	}

	public ApplicationForm(ApplicationManager a) {
		this.a = a;
	}

	public Application consulterApplication(HttpServletRequest request) {
		Application application = new Application();
		/* R�cup�ration de l'id de l'application choisie */
		String idApplication = getValeurChamp(request, CHAMP_LISTE_APPLICATIONS);
		Long id = null;
		try {
			id = Long.parseLong(idApplication);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_APPLICATION,
					"Application inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet application correspondant dans la session
			 */
			HttpSession session = request.getSession();

			application = ((Map<Long, Application>) session.getAttribute(SESSION_APPLICATIONS)).get(id);
		}

		return application;
	}

	public Application retrouverApplication(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_APPLICATION);
		String id = getValeurChamp(request, CHAMP_ID);

		Application application = new Application();

		traiterId(application, id);

		application.setNom(nom);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				application = a.retrouver(application);

				setResultat("Succ�s de la recherche de l'application.");
				return application;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier le nom de l'application.");
			setResultat("�chec de la recherche.");

		}

		return application;
	}

	public Application creerApplication(HttpServletRequest request) {
		Utilisateur contactTechnique;
		Utilisateur contactFonctionnel;

		/*
		 * Si l'utilisateur choisit un contact d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauContactTechnique = getValeurChamp(request, CHAMP_CHOIX_CONTACT_TECHNIQUE);

		if (ANCIEN_CONTACT_TECHNIQUE.equals(choixNouveauContactTechnique)) {
			/* R�cup�ration de l'id du contact choisi */
			String idAncienContactTechnique = getValeurChamp(request, CHAMP_LISTE_CONTACT_TECHNIQUES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienContactTechnique);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_TECHNIQUE,
						"Contact technique inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			contactTechnique = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			contactTechnique = new Utilisateur();
		}
		/*
		 * Si l'utilisateur choisit un contact d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauContactFonctionnel = getValeurChamp(request, CHAMP_CHOIX_CONTACT_FONCTIONNEL);
		if (ANCIEN_CONTACT_FONCTIONNEL.equals(choixNouveauContactFonctionnel)) {
			/* R�cup�ration de l'id du contact choisi */
			String idAncienContactFonctionnel = getValeurChamp(request, CHAMP_LISTE_CONTACT_FONCTIONNELS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienContactFonctionnel);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_FONCTIONNEL,
						"Contact fonctionnel inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			contactFonctionnel = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			contactFonctionnel = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une application.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_APPLICATION);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);

		Application application = new Application();

		try {
			traiterContactTechnique(application, contactTechnique);
			traiterContactFonctionnel(application, contactFonctionnel);

			traiterNom(application, nom);
			traiterDescription(application, description);

			if (getErreurs().isEmpty()) {

				if (a.retrouver(application) == null) {
					application = a.creer(application);
					setResultat("Succ�s de la cr�ation de l'application.");
				} else {
					setResultat("Application d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation de l'application.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de l'application : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return application;
	}

	public Application modifierApplication(HttpServletRequest request) {
		Utilisateur contactTechnique;
		Utilisateur contactFonctionnel;

		/*
		 * Si l'utilisateur choisit un contact d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauContactTechnique = getValeurChamp(request, CHAMP_CHOIX_CONTACT_TECHNIQUE);

		if (ANCIEN_CONTACT_TECHNIQUE.equals(choixNouveauContactTechnique)) {
			/* R�cup�ration de l'id du contact choisi */
			String idAncienContactTechnique = getValeurChamp(request, CHAMP_LISTE_CONTACT_TECHNIQUES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienContactTechnique);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_TECHNIQUE,
						"Contact technique inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			contactTechnique = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			contactTechnique = new Utilisateur();
		}
		/*
		 * Si l'utilisateur choisit un contact d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauContactFonctionnel = getValeurChamp(request, CHAMP_CHOIX_CONTACT_FONCTIONNEL);
		if (ANCIEN_CONTACT_FONCTIONNEL.equals(choixNouveauContactFonctionnel)) {
			/* R�cup�ration de l'id du contact choisi */
			String idAncienContactFonctionnel = getValeurChamp(request, CHAMP_LISTE_CONTACT_FONCTIONNELS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienContactFonctionnel);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_FONCTIONNEL,
						"Contact fonctionnel inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			contactFonctionnel = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			contactFonctionnel = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une application.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_APPLICATION);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);

		String id = getValeurChamp(request, CHAMP_ID);

		Application application = new Application();
		if (!id.trim().isEmpty()) {
			traiterId(application, id);
		}

		try {
			traiterContactTechnique(application, contactTechnique);
			traiterContactFonctionnel(application, contactFonctionnel);

			traiterNom(application, nom);
			traiterDescription(application, description);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					a.modifier(application);
					setResultat("Succ�s de la modification de l'application.");
				} else {
					setResultat("�chec de la modification de l'application.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modifiation.");
			setResultat(
					"�chec de la modification de l'application : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return application;

	}

	public Application creerApplication(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];

		String description = tokens[1];
		Utilisateur contactTechnique = new Utilisateur(tokens[2]);
		Utilisateur contactFonctionnel = new Utilisateur(tokens[3]);

		Fournisseur fournisseur = new Fournisseur(tokens[4]);

		contactTechnique = u.retrouver(contactTechnique);
		contactFonctionnel = u.retrouver(contactFonctionnel);

		fournisseur = f.retrouver(fournisseur);

		Application application = new Application();

		if (!nom.trim().isEmpty()) {
			try {
				traiterNom(application, nom);
				traiterDescription(application, description);

				traiterContactTechnique(application, contactTechnique);
				traiterContactFonctionnel(application, contactFonctionnel);

				traiterFournisseur(application, fournisseur);

				if (a.retrouver(application) == null) {
					application = a.creer(application);
					setResultat("Succ�s de la cr�ation de l'application.");
				} else {

					application = a.retrouver(application);
					traiterDescription(application, description);
					traiterContactTechnique(application, contactTechnique);
					traiterContactFonctionnel(application, contactFonctionnel);
					traiterFournisseur(application, fournisseur);

					a.modifier(application);
					setResultat("Application d�j� existante.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation de l'application : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation de l'application.");
		}

		return application;

	}
}
