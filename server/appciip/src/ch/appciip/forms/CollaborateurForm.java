package forms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import beans.Adresse;
import beans.Application;
import beans.Beamer;
import beans.Fonction;
import beans.Horaire;
import beans.Imprimante;
import beans.Seance;
import beans.Evenement;
import beans.Service;
import beans.Statut;
import beans.Voiture;
import beans.Collaborateur;
import beans.Comite;
import beans.Distribution;

/**
 * @author TTG
 * 
 */
@SuppressWarnings("unchecked")
public final class CollaborateurForm extends Form {

	public CollaborateurForm(CollaborateurManager u) {
		this.u = u;
	}

	public CollaborateurForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
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

	public Utilisateur connecterCollaborateur(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String initiales = getValeurChamp(request, CHAMP_INITIALES);
		String motDePasse = getValeurChamp(request, CHAMP_PASS);

		Utilisateur collaborateur = new Utilisateur();

		traiterInitiales(collaborateur, initiales);

		traiterMdp(collaborateur, motDePasse);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				collaborateur = u.connecter(collaborateur);
				if (collaborateur == null) {
					setResultat("�chec de la connexion.");
					setErreur(CHAMP_INITIALES, "�chec de la connexion. Informations invalides.");
				} else {
					collaborateur.setSubalternes(u.lister(collaborateur));

					setResultat("Succ�s de la connexion de l'collaborateur.");
				}
			} else

				setResultat("�chec de la connexion. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la connexion.");
			setResultat("�chec de la connexion.");
		}

		return collaborateur;
	}

	public Utilisateur consulterCollaborateur(HttpServletRequest request) {
		Utilisateur collaborateur = new Utilisateur();
		/* R�cup�ration de l'id de l'collaborateur choisi */
		String idAncienCollaborateur = getValeurChamp(request, CHAMP_LISTE_UTILISATEURS);
		Long id = null;
		try {
			id = Long.parseLong(idAncienCollaborateur);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_UTILISATEUR,
					"Collaborateur inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet collaborateur correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();
			collaborateur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		}

		return collaborateur;
	}

	public Utilisateur creerCollaborateur(HttpServletRequest request, String chemin) {

		Utilisateur superviseur;
		Service service;
		Fonction fonction;
		Adresse adresse;
		/*
		 * Si l'collaborateur choisit un collaborateur d�j� existant, pas de
		 * validation � effectuer
		 */
		String choixNouvelCollaborateur = getValeurChamp(request, CHAMP_CHOIX_UTILISATEUR);
		if (ANCIEN_UTILISATEUR.equals(choixNouvelCollaborateur)) {
			/* R�cup�ration de l'id de l'collaborateur choisi */
			String idAncienCollaborateur = getValeurChamp(request, CHAMP_LISTE_UTILISATEURS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienCollaborateur);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_UTILISATEUR,
						"Collaborateur inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet collaborateur correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();
			superviseur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			superviseur = new Utilisateur();
		}

		/*
		 * Si l'collaborateur choisit un service d�j� existant, pas de
		 * validation � effectuer
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
			 * de la map d'erreur cr��e par l'objet m�tier CollaborateurForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			erreurs = serviceForm.getErreurs();
		}

		/*
		 * Si l'collaborateur choisit un fonction d�j� existant, pas de
		 * validation � effectuer
		 */
		String choixNouveauFonction = getValeurChamp(request, CHAMP_CHOIX_METIER);
		if (ANCIEN_METIER.equals(choixNouveauFonction)) {
			/* R�cup�ration de l'id du fonction choisi */
			String idAncienFonction = getValeurChamp(request, CHAMP_LISTE_METIERS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienFonction);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_METIER, "Fonction inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/* R�cup�ration de l'objet fonction correspondant dans la session */
			HttpSession session = request.getSession();
			fonction = ((Map<Long, Fonction>) session.getAttribute(SESSION_METIERS)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'un fonction existe
			 * d�j�, il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Fonction cr��.
			 */
			FonctionForm fonctionForm = new FonctionForm(m);
			fonction = fonctionForm.creerFonction(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier CollaborateurForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			erreurs = fonctionForm.getErreurs();
		}

		/*
		 * Si l'collaborateur choisit une adresse d�j� existante, pas de
		 * validation � effectuer
		 */
		String choixNouvelleAdresse = getValeurChamp(request, CHAMP_CHOIX_ADRESSE);
		if (ANCIENNE_ADRESSE.equals(choixNouvelleAdresse)) {
			/* R�cup�ration de l'id de la adresse choisie */
			String idAncienneAdresse = getValeurChamp(request, CHAMP_LISTE_ADRESSES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienneAdresse);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_ADRESSE, "Adresse inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet adresse correspondant dans la session
			 */
			HttpSession session = request.getSession();
			adresse = ((Map<Long, Adresse>) session.getAttribute(SESSION_ADRESSES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'un adresse existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Adresse cr��.
			 */
			AdresseForm adresseForm = new AdresseForm(l);
			adresse = adresseForm.creerAdresse(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier CollaborateurForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			erreurs = adresseForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un collaborateur.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM);
		String prenom = getValeurChamp(request, CHAMP_PRENOM);
		String telInterne = getValeurChamp(request, CHAMP_TELINTERNE);
		String email = getValeurChamp(request, CHAMP_EMAIL);
		String sexe = getValeurChamp(request, CHAMP_SEXE);
		String initiales = getValeurChamp(request, CHAMP_INITIALES);
		String mdp = getValeurChamp(request, CHAMP_PASS);
		String confirmation = getValeurChamp(request, CHAMP_CONF);

		String image = getValeurChamp(request, CHAMP_IMAGE);

		Statut statut = Statut.fromString(getValeurChamp(request, CHAMP_STATUT));
		Boolean admin = Boolean.parseBoolean(getValeurChamp(request, CHAMP_ADMIN));

		String dateDebutService = getValeurChamp(request, CHAMP_DATE_DEBUT_SERVICE);
		String dateFinEffective = getValeurChamp(request, CHAMP_DATE_FIN_EFFECTIVE);
		String dateFinService = getValeurChamp(request, CHAMP_DATE_FIN_SERVICE);

		Utilisateur collaborateur = new Utilisateur();

		try {
			traiterSuperviseur(collaborateur, superviseur);
			traiterService(collaborateur, service);
			traiterAdresse(collaborateur, adresse);
			traiterFonction(collaborateur, fonction);

			traiterSexe(collaborateur, sexe);
			traiterInitiales(collaborateur, initiales);
			traiterMdp(collaborateur, mdp);
			traiterConfirmation(collaborateur, confirmation);
			traiterImage(collaborateur, request, image);

			traiterAdmin(collaborateur, admin);
			traiterNom(collaborateur, nom);
			traiterPrenom(collaborateur, prenom);
			traiterTelInterne(collaborateur, telInterne);
			traiterEmail(collaborateur, email);
			traiterStatut(collaborateur, statut);
			traiterImage(collaborateur, request, chemin);
			traiterDateDebutService(collaborateur, dateDebutService);
			traiterDateFinService(collaborateur, dateFinService);
			traiterDateFinEffective(collaborateur, dateFinEffective);

			if (getErreurs().isEmpty()) {
				if (u.retrouver(collaborateur) == null) {
					collaborateur = u.creer(collaborateur);
					setResultat("Succ�s de la cr�ation de l'collaborateur.");
				} else {
					setResultat("Collaborateur d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation de l'collaborateur.");
			}

		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de l'collaborateur : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return collaborateur;
	}

	public Utilisateur challengerCollaborateur(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String initiales = getValeurChamp(request, CHAMP_INITIALES);
		String reponse = getValeurChamp(request, CHAMP_REPONSE);

		Utilisateur collaborateur = new Utilisateur();

		traiterInitiales(collaborateur, initiales);
		traiterReponse(collaborateur, reponse);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				collaborateur = u.challenger(collaborateur);
				if (collaborateur == null) {
					setResultat("�chec de la connexion.");
					setErreur(CHAMP_INITIALES, "�chec de la connexion. Informations invalides.");
				} else {
					collaborateur.setSubalternes(u.lister(collaborateur));

					setResultat("Succ�s de la connexion de l'collaborateur.");
				}

			} else

				setResultat("�chec de la connexion. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la connexion.");
			setResultat("�chec de la connexion.");
		}

		return collaborateur;
	}

	public Utilisateur retrouverCollaborateur(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String initiales = getValeurChamp(request, CHAMP_INITIALES);
		String id = getValeurChamp(request, CHAMP_ID);

		Utilisateur collaborateur = new Utilisateur();
		traiterId(collaborateur, id);
		collaborateur.setInitiales(initiales);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				collaborateur = u.retrouver(collaborateur);
				setResultat("Succ�s de la recherche de l'collaborateur.");
				return collaborateur;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre initiales.");
			setResultat("�chec de la recherche.");
		}

		return collaborateur;
	}

	public Utilisateur modifierMdpCollaborateur(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String mdp = getValeurChamp(request, CHAMP_PASS);
		String confirmation = getValeurChamp(request, CHAMP_CONF);
		String id = getValeurChamp(request, CHAMP_ID);

		Utilisateur collaborateur = new Utilisateur();
		traiterMdp(collaborateur, mdp);
		traiterConfirmation(collaborateur, confirmation);
		traiterId(collaborateur, id);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				u.modifierMdp(collaborateur);
				setResultat("Succ�s de la modification de l'collaborateur.");
			} else

				setResultat("�chec de la modification. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_PASS, "Erreur lors de la modification.");
			setResultat("�chec de la modification.");
		}
		return collaborateur;

	}

	public Utilisateur modifierChallengeCollaborateur(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String question = getValeurChamp(request, CHAMP_QUESTION);
		String reponse = getValeurChamp(request, CHAMP_REPONSE);
		String id = getValeurChamp(request, CHAMP_ID);

		Utilisateur collaborateur = new Utilisateur();
		traiterQuestion(collaborateur, question);
		traiterReponse(collaborateur, reponse);
		traiterId(collaborateur, id);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				u.modifierChallenge(collaborateur);
				setResultat("Succ�s de la modification de l'collaborateur.");
			} else

				setResultat("�chec de la modification. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_QUESTION, "Erreur lors de la modification.");
			setResultat("�chec de la modification.");
		}
		return collaborateur;

	}

	public Utilisateur modifierActifCollaborateur(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		Boolean actif = Boolean.parseBoolean(getValeurChamp(request, CHAMP_ACTIF));
		String id = getValeurChamp(request, CHAMP_ID);

		Utilisateur collaborateur = new Utilisateur();
		traiterActif(collaborateur, actif);
		traiterId(collaborateur, id);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				u.modifierActif(collaborateur);
				setResultat("Succ�s de la modification de l'collaborateur.");
			} else

				setResultat("�chec de la modification. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ACTIF, "Erreur lors de la modification.");
			setResultat("�chec de la modification.");
		}
		return collaborateur;
	}

	public Utilisateur initialiserCollaborateur(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String mdp = getValeurChamp(request, CHAMP_PASS);
		String confirmation = getValeurChamp(request, CHAMP_CONF);

		String question = getValeurChamp(request, CHAMP_QUESTION);
		String reponse = getValeurChamp(request, CHAMP_REPONSE);
		String id = getValeurChamp(request, CHAMP_ID);

		Utilisateur collaborateur = new Utilisateur();

		traiterId(collaborateur, id);

		traiterMdp(collaborateur, mdp);
		traiterConfirmation(collaborateur, confirmation);
		traiterQuestion(collaborateur, question);
		traiterReponse(collaborateur, reponse);
		collaborateur.setActif(true);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				u.modifierMdp(collaborateur);
				u.modifierChallenge(collaborateur);
				u.modifierActif(collaborateur);
				setResultat("Succ�s de la modification de l'collaborateur.");
			} else

				setResultat("�chec de la modification. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_INITIALES, "Erreur lors de la modification.");
			setResultat("�chec de la modification.");
		}

		return collaborateur;

	}

	public Utilisateur modifierCollaborateur(HttpServletRequest request, String chemin) {
		Utilisateur superviseur;
		Service service;
		Fonction fonction;
		Adresse adresse;
		/*
		 * Si l'collaborateur choisit un collaborateur d�j� existant, pas de
		 * validation � effectuer
		 */
		String choixNouvelCollaborateur = getValeurChamp(request, CHAMP_CHOIX_UTILISATEUR);
		if (ANCIEN_UTILISATEUR.equals(choixNouvelCollaborateur)) {
			/* R�cup�ration de l'id de l'collaborateur choisi */
			String idAncienCollaborateur = getValeurChamp(request, CHAMP_LISTE_UTILISATEURS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienCollaborateur);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_UTILISATEUR,
						"Collaborateur inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet collaborateur correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();
			superviseur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			superviseur = new Utilisateur();
		}

		/*
		 * Si l'collaborateur choisit un service d�j� existant, pas de
		 * validation � effectuer
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
			 * de la map d'erreur cr��e par l'objet m�tier CollaborateurForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			erreurs = serviceForm.getErreurs();
		}

		/*
		 * Si l'collaborateur choisit un fonction d�j� existant, pas de
		 * validation � effectuer
		 */
		String choixNouveauFonction = getValeurChamp(request, CHAMP_CHOIX_METIER);
		if (ANCIEN_METIER.equals(choixNouveauFonction)) {
			/* R�cup�ration de l'id du fonction choisi */
			String idAncienFonction = getValeurChamp(request, CHAMP_LISTE_METIERS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienFonction);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_METIER, "Fonction inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/* R�cup�ration de l'objet fonction correspondant dans la session */
			HttpSession session = request.getSession();
			fonction = ((Map<Long, Fonction>) session.getAttribute(SESSION_METIERS)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'un fonction existe
			 * d�j�, il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Fonction cr��.
			 */
			FonctionForm fonctionForm = new FonctionForm(m);
			fonction = fonctionForm.creerFonction(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier CollaborateurForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			erreurs = fonctionForm.getErreurs();
		}

		/*
		 * Si l'collaborateur choisit une adresse d�j� existante, pas de
		 * validation � effectuer
		 */
		String choixNouvelleAdresse = getValeurChamp(request, CHAMP_CHOIX_ADRESSE);
		if (ANCIENNE_ADRESSE.equals(choixNouvelleAdresse)) {
			/* R�cup�ration de l'id de la adresse choisie */
			String idAncienneAdresse = getValeurChamp(request, CHAMP_LISTE_ADRESSES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienneAdresse);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_ADRESSE, "Adresse inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet adresse correspondant dans la session
			 */
			HttpSession session = request.getSession();
			adresse = ((Map<Long, Adresse>) session.getAttribute(SESSION_ADRESSES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'un adresse existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Adresse cr��.
			 */

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier CollaborateurForm
			 * dans la map d'erreurs courante, actuellement vide.
			 */
			adresse = new Adresse();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un collaborateur.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM);
		String prenom = getValeurChamp(request, CHAMP_PRENOM);
		String telInterne = getValeurChamp(request, CHAMP_TELINTERNE);
		String email = getValeurChamp(request, CHAMP_EMAIL);
		String sexe = getValeurChamp(request, CHAMP_SEXE);
		String initiales = getValeurChamp(request, CHAMP_INITIALES);

		String image = getValeurChamp(request, CHAMP_IMAGE);

		Statut statut = Statut.fromString(getValeurChamp(request, CHAMP_STATUT));
		Boolean admin = Boolean.parseBoolean(getValeurChamp(request, CHAMP_ADMIN));

		String dateDebutService = getValeurChamp(request, CHAMP_DATE_DEBUT_SERVICE);
		String dateFinEffective = getValeurChamp(request, CHAMP_DATE_FIN_EFFECTIVE);
		String dateFinService = getValeurChamp(request, CHAMP_DATE_FIN_SERVICE);

		String id = getValeurChamp(request, CHAMP_ID);

		Utilisateur collaborateur = new Utilisateur();

		try {
			traiterId(collaborateur, id);

			traiterSuperviseur(collaborateur, superviseur);
			traiterService(collaborateur, service);
			traiterAdresse(collaborateur, adresse);
			traiterFonction(collaborateur, fonction);

			traiterSexe(collaborateur, sexe);
			traiterInitiales(collaborateur, initiales);
			traiterImage(collaborateur, request, image);

			traiterAdmin(collaborateur, admin);
			traiterNom(collaborateur, nom);
			traiterPrenom(collaborateur, prenom);
			traiterTelInterne(collaborateur, telInterne);
			traiterEmail(collaborateur, email);
			traiterStatut(collaborateur, statut);
			traiterImage(collaborateur, request, chemin);
			traiterDateDebutService(collaborateur, dateDebutService);
			traiterDateFinService(collaborateur, dateFinService);
			traiterDateFinEffective(collaborateur, dateFinEffective);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					u.modifier(collaborateur);

					setResultat("Succ�s de la modification de l'collaborateur.");
				} else {
					setResultat("�chec de la modification de l'collaborateur.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification de l'collaborateur : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return collaborateur;
	}

	public Utilisateur creerCollaborateur(ArrayList<String> liste) {

		String nom = liste.get(0);
		String prenom = liste.get(1);
		String initiales = liste.get(2);
		String mdp = liste.get(3);
		String confirmation = liste.get(3);
		String sexe = liste.get(4);
		Statut statut = Statut.fromString(liste.get(5));
		String email = liste.get(6);

		String telInterne = liste.get(7);

		String image = liste.get(8);

		Boolean admin = Boolean.parseBoolean(liste.get(9));

		String dateDebutService = liste.get(10);
		String dateFinService = liste.get(11);
		String dateFinEffective = liste.get(12);

		Utilisateur superviseur = new Utilisateur(liste.get(13), liste.get(14));
		Adresse adresse = new Adresse(liste.get(15));
		Fonction fonction = new Fonction(liste.get(16));

		Utilisateur collaborateur = new Utilisateur();
		superviseur = u.retrouver(superviseur);
		adresse = l.retrouver(adresse);
		fonction = m.retrouver(fonction);

		if (!initiales.trim().isEmpty() || !nom.trim().isEmpty()) {
			try {
				traiterInitiales(collaborateur, initiales);

				traiterSexe(collaborateur, sexe);

				traiterMdp(collaborateur, mdp);
				traiterConfirmation(collaborateur, confirmation);
				traiterImage(collaborateur, image);

				traiterAdmin(collaborateur, admin);
				traiterNom(collaborateur, nom);
				traiterPrenom(collaborateur, prenom);
				traiterTelInterne(collaborateur, telInterne);
				traiterEmail(collaborateur, email);
				traiterStatut(collaborateur, statut);
				traiterDateDebutService(collaborateur, dateDebutService);
				traiterDateFinService(collaborateur, dateFinService);
				traiterDateFinEffective(collaborateur, dateFinEffective);

				traiterSuperviseur(collaborateur, superviseur);
				traiterAdresse(collaborateur, adresse);
				traiterFonction(collaborateur, fonction);

				if (u.retrouver(collaborateur) == null) {
					collaborateur = u.creer(collaborateur);
					setResultat("Succ�s de la cr�ation de l'collaborateur.");
				} else {

					collaborateur = u.retrouver(collaborateur);

					traiterSexe(collaborateur, sexe);
					traiterInitiales(collaborateur, initiales);
					traiterMdp(collaborateur, mdp);
					traiterConfirmation(collaborateur, confirmation);
					traiterAdmin(collaborateur, admin);
					traiterNom(collaborateur, nom);
					traiterPrenom(collaborateur, prenom);
					traiterTelInterne(collaborateur, telInterne);
					traiterEmail(collaborateur, email);
					traiterStatut(collaborateur, statut);
					traiterDateDebutService(collaborateur, dateDebutService);
					traiterDateFinService(collaborateur, dateFinService);
					traiterDateFinEffective(collaborateur, dateFinEffective);
					traiterSuperviseur(collaborateur, superviseur);
					traiterAdresse(collaborateur, adresse);
					traiterFonction(collaborateur, fonction);

					u.modifier(collaborateur);

					setResultat("Collaborateur d�j� existant.");

				}

			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat("�chec de la cr�ation de l'collaborateur : " + collaborateur.getNom()
						+ ": une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}

		} else {
			setResultat("�chec de la cr�ation de l'collaborateur.");
		}
		return collaborateur;
	}

	public Utilisateur creerCollaborateur(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];
		String prenom = tokens[1];
		String initiales = tokens[2];
		String mdp = tokens[3];
		String confirmation = tokens[3];
		String sexe = tokens[4];
		Statut statut = Statut.fromString(tokens[5]);
		String email = tokens[6];

		String telInterne = tokens[7];
		String telPrive = tokens[8];

		String image = tokens[9];

		Boolean admin = Boolean.parseBoolean(tokens[10]);

		String dateAnniversaire = tokens[11];
		String dateDebutService = tokens[12];
		String dateFinService = tokens[13];
		String dateFinEffective = tokens[14];

		Utilisateur superviseur = new Utilisateur(tokens[15]);
		Adresse adresse = new Adresse(tokens[16]);
		Fonction fonction = new Fonction(tokens[17]);
		String activites = tokens[18];
		String taux = tokens[19];

		Utilisateur collaborateur = new Utilisateur();
		superviseur = u.retrouver(superviseur);
		adresse = l.retrouver(adresse);
		fonction = m.retrouver(fonction);

		if (!initiales.trim().isEmpty() || !nom.trim().isEmpty()) {
			try {
				traiterInitiales(collaborateur, initiales);

				traiterSexe(collaborateur, sexe);

				traiterMdp(collaborateur, mdp);
				traiterConfirmation(collaborateur, confirmation);
				traiterImage(collaborateur, image);

				traiterAdmin(collaborateur, admin);
				traiterNom(collaborateur, nom);
				traiterPrenom(collaborateur, prenom);

				traiterTelInterne(collaborateur, telInterne);
				traiterTelPrive(collaborateur, telPrive);

				traiterEmail(collaborateur, email);
				traiterStatut(collaborateur, statut);
				traiterDateAnniversaire(collaborateur, dateAnniversaire);
				traiterDateDebutService(collaborateur, dateDebutService);
				traiterDateFinService(collaborateur, dateFinService);
				traiterDateFinEffective(collaborateur, dateFinEffective);

				traiterSuperviseur(collaborateur, superviseur);
				traiterAdresse(collaborateur, adresse);
				traiterFonction(collaborateur, fonction);

				traiterActivites(collaborateur, activites);

				traiterTaux(collaborateur, taux);

				if (u.retrouver(collaborateur) == null) {
					collaborateur = u.creer(collaborateur);
					setResultat("Succ�s de la cr�ation de l'collaborateur.");
				} else {

					collaborateur = u.retrouver(collaborateur);

					traiterSexe(collaborateur, sexe);
					traiterInitiales(collaborateur, initiales);
					traiterMdp(collaborateur, mdp);
					traiterConfirmation(collaborateur, confirmation);
					traiterAdmin(collaborateur, admin);
					traiterNom(collaborateur, nom);
					traiterPrenom(collaborateur, prenom);
					traiterTelInterne(collaborateur, telInterne);
					traiterTelPrive(collaborateur, telPrive);
					traiterEmail(collaborateur, email);
					traiterStatut(collaborateur, statut);
					traiterDateAnniversaire(collaborateur, dateAnniversaire);
					traiterDateDebutService(collaborateur, dateDebutService);
					traiterDateFinService(collaborateur, dateFinService);
					traiterDateFinEffective(collaborateur, dateFinEffective);
					traiterSuperviseur(collaborateur, superviseur);
					traiterAdresse(collaborateur, adresse);
					traiterFonction(collaborateur, fonction);
					traiterActivites(collaborateur, activites);
					traiterTaux(collaborateur, taux);

					// System.err.println(collaborateur.getTelInterne());
					u.modifier(collaborateur);

					setResultat("Collaborateur d�j� existant.");

				}

			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat("�chec de la cr�ation de l'collaborateur : " + collaborateur.getNom()
						+ ": une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}

		} else {
			setResultat("�chec de la cr�ation de l'collaborateur.");
		}
		return collaborateur;

	}

	public Utilisateur lireAD(Utilisateur ut) {

		Utilisateur collaborateur = new Utilisateur();
		if (!ut.getInitiales().trim().isEmpty()) {
			try {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

				Date today = Calendar.getInstance().getTime();
				traiterInitiales(collaborateur, ut.getInitiales());
				traiterNom(collaborateur, ut.getNom());
				traiterPrenom(collaborateur, ut.getPrenom());
				traiterEmail(collaborateur, ut.getEmail());
				traiterTelInterne(collaborateur, ut.getTelInterne());

				traiterDateDebutService(collaborateur, df.format(today));
				traiterDateFinService(collaborateur, "");
				traiterDateFinEffective(collaborateur, "");
				traiterMdp(collaborateur, "1234");

				if (u.retrouver(collaborateur) == null) {

					if (collaborateur.getEmail() != null && collaborateur.getInitiales().length() >= 3) {

						collaborateur = u.creer(collaborateur);
						setResultat("Succ�s de la cr�ation de l'collaborateur.");
					}
				} else {

					collaborateur = u.retrouver(collaborateur);
					traiterInitiales(collaborateur, ut.getInitiales());
					traiterNom(collaborateur, ut.getNom());
					traiterPrenom(collaborateur, ut.getPrenom());
					traiterEmail(collaborateur, ut.getEmail());
					traiterTelInterne(collaborateur, ut.getTelInterne());

					traiterDateDebutService(collaborateur, "");
					traiterDateFinService(collaborateur, "");
					traiterDateFinEffective(collaborateur, "");

					u.modifier(collaborateur);

					setResultat("Collaborateur d�j� existant.");

				}

			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation de l'collaborateur : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}

		} else {
			setResultat("�chec de la cr�ation de l'collaborateur.");
		}
		return collaborateur;
	}

	public void supprimerAD(Utilisateur ut) {
		ut = u.retrouver(ut);
		if (ut != null) {
			try {

				Evenement note = new Evenement();
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

				Date today = Calendar.getInstance().getTime();

				traiterCollaborateur(note, ut.getSuperviseur());

				traiterTitre(note, "Anomalie d'collaborateur dans l'Active Directory");
				traiterDescription(note, "L'collaborateur " + ut.getPrenom() + " " + ut.getNom()
						+ " n'est plus present dans l'Active Directory, alors il a �t� supprimer de l'application.");
				traiterDate(note, df.format(today));

				note = n.creer(note);
				/* Alors suppression de l'collaborateur de la BDD */
				// u.supprimer(ut);
				/* Puis suppression de l'collaborateur de la Map */
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}

	public void notifierOLY(Utilisateur ut) {

		ut = u.retrouver(ut);
		Evenement note = new Evenement();
		if (ut != null) {
			traiterCollaborateur(note, ut.getSuperviseur());
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			Date today = Calendar.getInstance().getTime();

			traiterTitre(note, "Anomalie d'collaborateur dans OLYMPIC");
			traiterDescription(note, "L'collaborateur " + ut.getPrenom() + " " + ut.getNom()
					+ " ne s'est pas connect� sur l'application OLYMPIC depuis plus de 90 jours.");
			traiterDate(note, df.format(today));

			note = n.creer(note);
		}

	}

	public Utilisateur associerApplication(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Application application = new Application(tokens[1]);
		application = a.retrouver(application);

		collaborateur = u.associerApplication(collaborateur, application);

		return collaborateur;

	}

	public Utilisateur associerBeamer(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Beamer beamer = new Beamer(tokens[1]);
		beamer = b.retrouver(beamer);

		collaborateur = u.associerBeamer(collaborateur, beamer);

		return collaborateur;

	}

	public Utilisateur associerComite(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Localisation comite = new Localisation(tokens[1]);
		comite = p.retrouver(comite);

		collaborateur = u.associerComite(collaborateur, comite);

		return collaborateur;

	}

	public Utilisateur associerDistribution(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Distribution distribution = new Distribution(tokens[1]);
		distribution = di.retrouver(distribution);

		collaborateur = u.associerDistribution(collaborateur, distribution);

		return collaborateur;

	}

	public Utilisateur associerImprimante(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Imprimante imprimante = new Imprimante(tokens[1]);
		imprimante = i.retrouver(imprimante);

		collaborateur = u.associerImprimante(collaborateur, imprimante);

		return collaborateur;

	}

	public Utilisateur associerSeance(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Seance seance = new Seance(tokens[1]);
		seance = d.retrouver(seance);

		collaborateur = u.associerSeance(collaborateur, seance);

		return collaborateur;

	}

	public Utilisateur associerService(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Service service = new Service(tokens[1]);
		service = s.retrouver(service);

		collaborateur = u.associerService(collaborateur, service);

		return collaborateur;

	}

	public Utilisateur associerSuperieur(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Utilisateur superieur = new Utilisateur(tokens[1]);
		superieur = u.retrouver(superieur);

		collaborateur = u.associerSuperieur(collaborateur, superieur);

		return collaborateur;

	}

	public Utilisateur associerSuppleant(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Utilisateur suppleant = new Utilisateur(tokens[1]);
		suppleant = u.retrouver(suppleant);

		collaborateur = u.associerSuppleant(collaborateur, suppleant);

		return collaborateur;

	}

	public Utilisateur associerSupplee(String[] tokens) {
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);
		Utilisateur supplee = new Utilisateur(tokens[1]);
		supplee = u.retrouver(supplee);

		collaborateur = u.associerSupplee(collaborateur, supplee);

		return collaborateur;

	}

}