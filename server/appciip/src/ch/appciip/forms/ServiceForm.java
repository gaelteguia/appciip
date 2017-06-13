package forms;

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
import beans.Service;
import beans.Collaborateur;

@SuppressWarnings("unchecked")
public class ServiceForm extends Form {

	public ServiceForm(ServiceManager s) {

		this.s = s;
	}

	public ServiceForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
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

	public Service retrouverService(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_SERVICE);
		String id = getValeurChamp(request, CHAMP_ID);

		Service service = new Service();
		traiterId(service, id);

		service.setNom(nom);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				service = s.retrouver(service);
				setResultat("Succ�s de la recherche du service.");
			} else

				setResultat("�chec de la recherche du service. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la recherche.");
			setResultat("�chec de la recherche.");
		}

		return service;
	}

	public Service creerService(HttpServletRequest request) {
		Utilisateur superviseur;
		Service departement;

		/*
		 * Si l'utilisateur choisit un utilisateur d�j� existant, pas de
		 * validation � effectuer
		 */
		String choixNouvelCollaborateur = getValeurChamp(request, CHAMP_CHOIX_UTILISATEUR);
		if (ANCIEN_UTILISATEUR.equals(choixNouvelCollaborateur)) {
			/* R�cup�ration de l'id de l'utilisateur choisi */
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
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			superviseur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			superviseur = new Utilisateur();
		}

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
			departement = ((Map<Long, Service>) session.getAttribute(SESSION_SERVICES)).get(id);
		} else {

			departement = new Service();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un service.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_SERVICE);
		String acronyme = getValeurChamp(request, CHAMP_ACRONYME);

		Service service = new Service();

		try {
			traiterSuperviseur(service, superviseur);
			traiterDepartement(service, departement);

			traiterNom(service, nom);
			traiterAcronyme(service, acronyme);

			if (getErreurs().isEmpty()) {

				if (s.retrouver(service) == null) {
					// if (getErreurs().isEmpty()) {
					service = s.creer(service);
					setResultat("Succ�s de la cr�ation du service.");
				} else {

					setResultat("Service d�j� existant.");
				}
			}

			else {
				setResultat("�chec de la cr�ation du service.");
			}

		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation du service : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return service;
	}

	public Service modifierService(HttpServletRequest request) {
		Utilisateur superviseur;
		Service departement;

		/*
		 * Si l'utilisateur choisit un utilisateur d�j� existant, pas de
		 * validation � effectuer
		 */
		String choixNouvelCollaborateur = getValeurChamp(request, CHAMP_CHOIX_UTILISATEUR);
		if (ANCIEN_UTILISATEUR.equals(choixNouvelCollaborateur)) {
			/* R�cup�ration de l'id du utilisateur choisi */
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
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			superviseur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			superviseur = new Utilisateur();
		}

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
			departement = ((Map<Long, Service>) session.getAttribute(SESSION_SERVICES)).get(id);
		} else {

			departement = new Service();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un service.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_SERVICE);
		String acronyme = getValeurChamp(request, CHAMP_ACRONYME);
		String id = getValeurChamp(request, CHAMP_ID);

		Service service = new Service();
		if (!id.trim().isEmpty())
			traiterId(service, id);

		try {
			traiterSuperviseur(service, superviseur);
			traiterDepartement(service, departement);

			traiterNom(service, nom);
			traiterAcronyme(service, acronyme);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					s.modifier(service);
					setResultat("Succ�s de la modification du service.");
				} else {
					setResultat("�chec de la modification du service.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification du service : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return service;
	}

	public Service consulterService(HttpServletRequest request) {
		Service service = new Service();
		/* R�cup�ration de l'id du service choisi */
		String idService = getValeurChamp(request, CHAMP_LISTE_SERVICES);
		Long id = null;
		try {
			id = Long.parseLong(idService);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_SERVICE, "Service inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/* R�cup�ration de l'objet service correspondant dans la session */
			HttpSession session = request.getSession();
			service = ((Map<Long, Service>) session.getAttribute(SESSION_SERVICES)).get(id);
		}
		return service;
	}

	public Service creerService(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];

		String acronyme = tokens[1];

		Service departement = new Service(tokens[2]);
		departement = s.retrouver(departement);

		Utilisateur superviseur = new Utilisateur(tokens[3]);
		superviseur = u.retrouver(superviseur);

		Service service = new Service();

		if (!nom.trim().isEmpty()) {

			try {
				traiterNom(service, nom);
				traiterSuperviseur(service, superviseur);
				traiterDepartement(service, departement);

				traiterAcronyme(service, acronyme);

				if (s.retrouver(service) == null) {

					service = s.creer(service);
					setResultat("Succ�s de la cr�ation du service.");
				} else {

					service = s.retrouver(service);
					traiterNom(service, nom);
					traiterSuperviseur(service, superviseur);
					traiterDepartement(service, departement);

					traiterAcronyme(service, acronyme);

					s.modifier(service);
					setResultat("Service d�j� existant.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation du service : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation du service.");
		}

		return service;

	}

}
