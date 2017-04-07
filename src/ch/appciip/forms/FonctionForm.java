package forms;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import modele.ApplicationManager;
import modele.DatabaseException;
import modele.SeanceManager;
import modele.AdresseManager;
import modele.FonctionManager;
import modele.EvenementManager;
import modele.ComiteManager;
import modele.ServiceManager;
import modele.CollaborateurManager;
import beans.Fonction;
import beans.Comite;
import beans.Service;

@SuppressWarnings("unchecked")
public class FonctionForm extends Form {

	public FonctionForm(FonctionManager m) {
		this.m = m;
	}

	public FonctionForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
			FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.l = l;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;
	}

	public Fonction retrouverFonction(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_METIER);
		String id = getValeurChamp(request, CHAMP_ID);

		Fonction fonction = new Fonction();
		traiterId(fonction, id);

		fonction.setNom(nom);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				fonction = m.retrouver(fonction);
				setResultat("Succ�s de la recherche du profil m�tier.");
			} else

				setResultat("�chec de la recherche du profil m�tier. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la recherche.");
			setResultat("�chec de la recherche.");
		}

		return fonction;
	}

	public Fonction creerFonction(HttpServletRequest request) {

		Service service;

		/*
		 * Si l'utilisateur choisit un service d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauService = getValeurChamp(request, CHAMP_CHOIX_SERVICE);
		if (ANCIEN_SERVICE.equals(choixNouveauService)) {
			/* R�cup�ration de l'id du service choisi */
			String idAncienService = getValeurChamp(request, CHAMP_LISTE_SERVICES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienService);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_SERVICE, "Service inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/* R�cup�ration de l'objet service correspondant dans la session */
			HttpSession session = request.getSession();
			service = ((Map<Long, Service>) session.getAttribute(SESSION_SERVICES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'un service existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Fonction cr��.
			 */
			ServiceForm serviceForm = new ServiceForm(s);
			service = serviceForm.creerService(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier FonctionForm dans la
			 * map d'erreurs courante, actuellement vide.
			 */
			erreurs = serviceForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un fonction.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_METIER);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		Boolean valide = Boolean.parseBoolean(getValeurChamp(request, CHAMP_VALIDE));

		String identifiant = getValeurChamp(request, CHAMP_VALIDEUR);
		String dateValidation = getValeurChamp(request, CHAMP_DATE_VALIDATION);
		String commentaires = getValeurChamp(request, CHAMP_COMMENTAIRES);

		Fonction fonction = new Fonction();

		try {

			traiterService(fonction, service);

			traiterNom(fonction, nom);
			traiterDescription(fonction, description);
			traiterValide(fonction, valide);
			traiterIdentifiant(fonction, identifiant);
			traiterDateValidation(fonction, dateValidation);

			traiterCommentaires(fonction, commentaires);

			if (getErreurs().isEmpty()) {

				if (m.retrouver(fonction) == null) {
					fonction = m.creer(fonction);
					setResultat("Succ�s de la cr�ation du profil m�tier.");
				} else {
					setResultat("Comite m�tier d�j� existant.");
				}

			} else {
				setResultat("�chec de la cr�ation du profil m�tier.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation du profil m�tier : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return fonction;
	}

	public Fonction associerComite(HttpServletRequest request) {
		/* R�cup�ration de l'id du client choisi */
		String idFonction = getValeurChamp(request, CHAMP_LISTE_METIERS);
		Long idMet = null;
		String idComite = getValeurChamp(request, CHAMP_LISTE_COMITES);
		Long idPro = null;
		try {
			idMet = Long.parseLong(idFonction);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_METIER, "Comite m�tier inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			idMet = 0L;
		}
		try {
			idPro = Long.parseLong(idComite);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_COMITE, "Comite d'acc�s inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			idPro = 0L;
		}
		/* R�cup�ration de l'objet client correspondant dans la session */
		HttpSession session = request.getSession();
		Fonction fonction = ((Map<Long, Fonction>) session.getAttribute(SESSION_METIERS)).get(idMet);
		Localisation profil = ((Map<Long, Localisation>) session.getAttribute(SESSION_COMITES)).get(idPro);

		try {

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {

					fonction = m.associerComite(fonction, profil);
					setResultat("Succ�s de l'association.");
				} else {
					setResultat("�chec de l'association.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de l'association.");
			setResultat(
					"�chec de l'association : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return fonction;
	}

	public Fonction consulterFonction(HttpServletRequest request) {
		Fonction fonction = new Fonction();
		/* R�cup�ration de l'id du client choisi */
		String idFonction = getValeurChamp(request, CHAMP_LISTE_METIERS);
		Long id = null;
		try {
			id = Long.parseLong(idFonction);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_METIER, "Comite m�tier inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/* R�cup�ration de l'objet client correspondant dans la session */
			HttpSession session = request.getSession();
			fonction = ((Map<Long, Fonction>) session.getAttribute(SESSION_METIERS)).get(id);
		}

		return fonction;
	}

	public Fonction modifierFonction(HttpServletRequest request) {

		Service service;

		/*
		 * Si l'utilisateur choisit un service d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauService = getValeurChamp(request, CHAMP_CHOIX_SERVICE);
		if (ANCIEN_SERVICE.equals(choixNouveauService)) {
			/* R�cup�ration de l'id du service choisi */
			String idAncienService = getValeurChamp(request, CHAMP_LISTE_SERVICES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienService);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_SERVICE, "Service inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/* R�cup�ration de l'objet service correspondant dans la session */
			HttpSession session = request.getSession();
			service = ((Map<Long, Service>) session.getAttribute(SESSION_SERVICES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'un service existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Service cr��.
			 */
			ServiceForm serviceForm = new ServiceForm(s);
			service = serviceForm.creerService(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier ServiceForm dans la
			 * map d'erreurs courante, actuellement vide.
			 */
			erreurs = serviceForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un fonction.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_METIER);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		Boolean valide = Boolean.parseBoolean(getValeurChamp(request, CHAMP_VALIDE));

		String identifiant = getValeurChamp(request, CHAMP_VALIDEUR);
		String dateValidation = getValeurChamp(request, CHAMP_DATE_VALIDATION);
		String commentaires = getValeurChamp(request, CHAMP_COMMENTAIRES);

		String id = getValeurChamp(request, CHAMP_ID);

		Fonction fonction = new Fonction();
		if (!id.trim().isEmpty())
			traiterId(fonction, id);

		try {

			traiterService(fonction, service);

			traiterNom(fonction, nom);
			traiterDescription(fonction, description);
			traiterValide(fonction, valide);
			traiterIdentifiant(fonction, identifiant);
			traiterDateValidation(fonction, dateValidation);

			traiterCommentaires(fonction, commentaires);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					m.modifier(fonction);
					setResultat("Succ�s de la modification du profil m�tier.");
				} else {
					setResultat("�chec de la modification du profil m�tier.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification du profil m�tier : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return fonction;
	}

	public Fonction creerFonction(String[] tokens) {

		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];
		String description = tokens[1];
		// Boolean valide = Boolean.parseBoolean(liste.get(2));

		String identifiant = tokens[2];
		// String dateValidation = liste.get(4);
		// String commentaires = liste.get(5);

		Service service = new Service(tokens[3]);
		service = s.retrouver(service);
		Fonction fonction = new Fonction();

		if (!nom.trim().isEmpty()) {
			try {
				traiterNom(fonction, nom);

				// traiterCommentaires(fonction, commentaires);
				traiterDescription(fonction, description);
				// traiterValide(fonction, valide);
				traiterIdentifiant(fonction, identifiant);
				// traiterDateValidation(fonction, dateValidation);
				traiterService(fonction, service);

				if (m.retrouver(fonction) == null) {
					fonction = m.creer(fonction);
					setResultat("Succ�s de la cr�ation du profil m�tier.");

				} else {
					fonction = m.retrouver(fonction);

					traiterDescription(fonction, description);
					// traiterValide(fonction, valide);
					traiterIdentifiant(fonction, identifiant);
					// traiterDateValidation(fonction, dateValidation);

					// traiterCommentaires(fonction, commentaires);

					traiterService(fonction, service);

					m.modifier(fonction);
					setResultat("Comite m�tier d�j� existant.");
				}
			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation du profil m�tier : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation du profil m�tier.");
		}

		return fonction;

	}

	public Fonction associerComite(ArrayList<String> liste) {
		Fonction fonction = new Fonction(liste.get(0));
		fonction = m.retrouver(fonction);
		Localisation profil = new Localisation(liste.get(1));
		profil = p.retrouver(profil);

		fonction = m.associerComite(fonction, profil);

		return fonction;

	}

	public Fonction supprimerComite(HttpServletRequest request) {
		Fonction fonction = retrouverFonction(request);
		ComiteForm profilForm = new ComiteForm(p);
		Localisation profil = profilForm.consulterComite(request);
		fonction = m.supprimerComite(fonction, profil);
		return fonction;
	}

}
