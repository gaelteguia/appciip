package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Collaborateur;
import beans.Horaire;
import beans.Voiture;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.HoraireManager;
import modele.SeanceManager;
import modele.ServiceManager;
import modele.VoitureManager;

@SuppressWarnings("unchecked")
public class HoraireForm extends Form {

	public HoraireForm(CollaborateurManager u, ApplicationManager a, HoraireManager h, SeanceManager d,
			AdresseManager l, FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s,
			VoitureManager v) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.l = l;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;
		this.v = v;
		this.h = h;
	}

	public HoraireForm(HoraireManager h) {
		this.h = h;
	}

	public Horaire consulterHoraire(HttpServletRequest request) {
		Horaire horaire = new Horaire();
		/* R�cup�ration de l'id de l'horaire choisie */
		String idHoraire = getValeurChamp(request, CHAMP_LISTE_APPLICATIONS);
		Long id = null;
		try {
			id = Long.parseLong(idHoraire);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_APPLICATION, "Horaire inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet horaire correspondant dans la session
			 */
			HttpSession session = request.getSession();

			horaire = ((Map<Long, Horaire>) session.getAttribute(SESSION_APPLICATIONS)).get(id);
		}

		return horaire;
	}

	public Horaire retrouverHoraire(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_HORAIRE);
		String id = getValeurChamp(request, CHAMP_ID);

		Horaire horaire = new Horaire();

		traiterId(horaire, id);

		horaire.setNom(nom);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				horaire = h.retrouver(horaire);

				setResultat("Succ�s de la recherche de l'horaire.");
				return horaire;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier le nom de l'horaire.");
			setResultat("�chec de la recherche.");

		}

		return horaire;
	}

	public Horaire creerHoraire(HttpServletRequest request) {
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
		 * sp�cifiques � une horaire.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_HORAIRE);

		Horaire horaire = new Horaire();

		try {

			traiterNom(horaire, nom);

			if (getErreurs().isEmpty()) {

				if (h.retrouver(horaire) == null) {
					horaire = h.creer(horaire);
					setResultat("Succ�s de la cr�ation de l'horaire.");
				} else {
					setResultat("Horaire d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation de l'horaire.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de l'horaire : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return horaire;
	}

	public Horaire modifierHoraire(HttpServletRequest request) {
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
		 * sp�cifiques � une horaire.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_HORAIRE);

		String id = getValeurChamp(request, CHAMP_ID);

		Horaire horaire = new Horaire();
		if (!id.trim().isEmpty()) {
			traiterId(horaire, id);
		}

		try {

			traiterNom(horaire, nom);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					h.modifier(horaire);
					setResultat("Succ�s de la modification de l'horaire.");
				} else {
					setResultat("�chec de la modification de l'horaire.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modifiation.");
			setResultat(
					"�chec de la modification de l'horaire : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return horaire;

	}

	public Horaire creerHoraire(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];

		Horaire horaire = new Horaire();

		if (!nom.trim().isEmpty()) {
			try {
				traiterNom(horaire, nom);

				if (h.retrouver(horaire) == null) {
					horaire = h.creer(horaire);
					setResultat("Succès de la création.");
				} else {

					horaire = h.retrouver(horaire);

					h.modifier(horaire);
					setResultat("Horaire déjà existante.");
				}

			} catch (DatabaseException e) {
				setErreur("imprévu", "Erreur imprévue lors de la création.");
				setResultat(
						"Échec de la création de l'horaire : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("Échec de la création de l'horaire.");
		}

		return horaire;

	}

	public Horaire associerVoiture(String[] tokens) {
		Horaire horaire = new Horaire(tokens[1]);
		horaire = h.retrouver(horaire);
		Voiture voiture = new Voiture(tokens[0]);
		voiture = v.retrouver(voiture);

		horaire = h.associerVoiture(horaire, voiture);

		return horaire;

	}

	public Horaire associerCollaborateur(String[] tokens) {
		Horaire horaire = new Horaire(tokens[1]);
		horaire = h.retrouver(horaire);
		Utilisateur collaborateur = new Utilisateur(tokens[0]);
		collaborateur = u.retrouver(collaborateur);

		horaire = h.associerCollaborateur(horaire, collaborateur);

		return horaire;

	}
}
