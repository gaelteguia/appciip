package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Adresse;
import beans.Collaborateur;
import beans.Manifestation;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.DatabaseException;
import modele.SeanceManager;
import modele.ManifestationManager;
import modele.FonctionManager;
import modele.ComiteManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class ManifestationForm extends Form {

	public ManifestationForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
			FonctionManager m, ManifestationManager ma, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.l = l;
		this.m = m;
		this.ma = ma;
		this.p = p;
		this.s = s;
	}

	public Manifestation consulterManifestation(HttpServletRequest request) {
		Manifestation manifestation = new Manifestation();
		/* R�cup�ration de l'id de la manifestation choisie */
		String idManifestation = getValeurChamp(request, CHAMP_LISTE_MANIFESTATIONS);
		Long id = null;
		try {
			id = Long.parseLong(idManifestation);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_MANIFESTATION,
					"Manifestation inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet manifestation correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();
			manifestation = ((Map<Long, Manifestation>) session.getAttribute(SESSION_MANIFESTATIONS)).get(id);
		}

		return manifestation;
	}

	public Manifestation creerManifestation(HttpServletRequest request) {
		Utilisateur utilisateur;

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
			utilisateur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			utilisateur = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une manifestatioma.
		 */

		String titre = getValeurChamp(request, CHAMP_TITRE);
		String contenu = getValeurChamp(request, CHAMP_CONTENU);
		String date = getValeurChamp(request, CHAMP_DATE);

		Manifestation manifestation = new Manifestation();

		try {
			traiterCollaborateur(manifestation, utilisateur);

			traiterTitre(manifestation, titre);
			traiterDescription(manifestation, contenu);
			traiterDate(manifestation, date);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					manifestation = ma.creer(manifestation);
					setResultat("Succ�s de la cr�ation de la manifestatioma.");
				} else {
					setResultat("�chec de la cr�ation de la manifestatioma.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�atioma.");
			setResultat(
					"�chec de la cr�ation de la manifestation : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return manifestation;
	}

	public Manifestation creerManifestation(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String titre = tokens[0];

		String description = tokens[1];

		String date = tokens[2];

		// Adresse adresse = new Adresse(tokens[3]);

		// adresse.setVille(tokens[6]);
		// adresse.setPays(tokens[7]);

		// Collaborateur collaborateur = new Collaborateur(tokens[8],
		// tokens[9]);

		// collaborateur = u.retrouver(collaborateur);

		Manifestation manifestation = new Manifestation();

		if (!titre.trim().isEmpty()) {
			try {
				traiterTitre(manifestation, titre);
				traiterDate(manifestation, date);

				// traiterNoRue(adresse, tokens[4]);
				// traiterNpa(adresse, tokens[5]);

				// traiterAdresse(manifestation, adresse);
				// traiterCollaborateur(manifestation, collaborateur);

				traiterDescription(manifestation, description);

				if (ma.retrouver(manifestation) == null) {
					manifestation = ma.creer(manifestation);
					setResultat("Succ�s de la cr�ation de l'manifestatioma.");
				} else {

					manifestation = ma.retrouver(manifestation);
					// traiterCollaborateur(manifestation, collaborateur);
					traiterDate(manifestation, date);
					// traiterAdresse(manifestation, adresse);
					traiterDescription(manifestation, description);
					ma.modifier(manifestation);
					setResultat("Manifestation déjà existant.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�atioma.");
				setResultat(
						"�chec de la cr�ation de l'manifestation : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation de l'manifestatioma.");
		}

		return manifestation;

	}

	public Manifestation retrouverManifestation(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String id = getValeurChamp(request, CHAMP_ID);

		Manifestation manifestation = new Manifestation();
		traiterId(manifestation, id);

		try {
			/* Initialisation du r�sultat global de la validatioma. */
			if (getErreurs().isEmpty()) {
				manifestation = ma.retrouver(manifestation);
				setResultat("Succ�s de la recherche de la manifestatioma.");
			} else

				setResultat("�chec de la recherche de la manifestatioma. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la recherche.");
			setResultat("�chec de la recherche.");
		}

		return manifestation;
	}

	public Manifestation modifierManifestation(HttpServletRequest request) {
		Utilisateur utilisateur;

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
			utilisateur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			utilisateur = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une manifestatioma.
		 */

		String titre = getValeurChamp(request, CHAMP_TITRE);
		String contenu = getValeurChamp(request, CHAMP_CONTENU);
		String date = getValeurChamp(request, CHAMP_DATE);

		String id = getValeurChamp(request, CHAMP_ID);

		Manifestation manifestation = new Manifestation();
		if (!id.trim().isEmpty())
			traiterId(manifestation, id);

		try {
			traiterCollaborateur(manifestation, utilisateur);

			traiterTitre(manifestation, titre);
			traiterDescription(manifestation, contenu);
			traiterDate(manifestation, date);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					ma.modifier(manifestation);
					setResultat("Succ�s de la modification de la manifestatioma.");
				} else {
					setResultat("�chec de la modification de la manifestatioma.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modificatioma.");
			setResultat(
					"�chec de la modification de la manifestation : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return manifestation;
	}

}
