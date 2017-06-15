package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Collaborateur;
import beans.Distribution;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.DistributionManager;
import modele.SeanceManager;
import modele.ServiceManager;
import modele.VoitureManager;

@SuppressWarnings("unchecked")
public class DistributionForm extends Form {

	public DistributionForm(CollaborateurManager u, ApplicationManager a, DistributionManager di, SeanceManager d,
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
		this.di = di;
	}

	public DistributionForm(DistributionManager di) {
		this.di = di;
	}

	public Distribution consulterDistribution(HttpServletRequest request) {
		Distribution distribution = new Distribution();
		/* R�cup�ration de l'id de l'distribution choisie */
		String idDistribution = getValeurChamp(request, CHAMP_LISTE_APPLICATIONS);
		Long id = null;
		try {
			id = Long.parseLong(idDistribution);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_APPLICATION,
					"Distribution inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet distribution correspondant dans la
			 * session
			 */
			HttpSession session = request.getSession();

			distribution = ((Map<Long, Distribution>) session.getAttribute(SESSION_APPLICATIONS)).get(id);
		}

		return distribution;
	}

	public Distribution retrouverDistribution(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_HORAIRE);
		String id = getValeurChamp(request, CHAMP_ID);

		Distribution distribution = new Distribution();

		traiterId(distribution, id);

		distribution.setNom(nom);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				distribution = di.retrouver(distribution);

				setResultat("Succ�s de la recherche de l'distribution.");
				return distribution;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier le nom de l'distribution.");
			setResultat("�chec de la recherche.");

		}

		return distribution;
	}

	public Distribution creerDistribution(HttpServletRequest request) {
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
		 * sp�cifiques � une distribution.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_HORAIRE);

		Distribution distribution = new Distribution();

		try {

			traiterNom(distribution, nom);

			if (getErreurs().isEmpty()) {

				if (di.retrouver(distribution) == null) {
					distribution = di.creer(distribution);
					setResultat("Succ�s de la cr�ation de l'distribution.");
				} else {
					setResultat("Distribution d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation de l'distribution.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de l'distribution : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return distribution;
	}

	public Distribution modifierDistribution(HttpServletRequest request) {
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
		 * sp�cifiques � une distribution.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_HORAIRE);

		String id = getValeurChamp(request, CHAMP_ID);

		Distribution distribution = new Distribution();
		if (!id.trim().isEmpty()) {
			traiterId(distribution, id);
		}

		try {

			traiterNom(distribution, nom);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					di.modifier(distribution);
					setResultat("Succ�s de la modification de l'distribution.");
				} else {
					setResultat("�chec de la modification de l'distribution.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modifiation.");
			setResultat(
					"�chec de la modification de l'distribution : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return distribution;

	}

	public Distribution creerDistribution(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];

		Distribution distribution = new Distribution();

		if (!nom.trim().isEmpty()) {
			try {
				traiterNom(distribution, nom);

				if (di.retrouver(distribution) == null) {
					distribution = di.creer(distribution);
					setResultat("Succès de la création.");
				} else {

					distribution = di.retrouver(distribution);

					di.modifier(distribution);
					setResultat("Distribution déjà existante.");
				}

			} catch (DatabaseException e) {
				setErreur("imprévu", "Erreur imprévue lors de la création.");
				setResultat(
						"Échec de la création de l'distribution : une erreur imprévue est survenue, merci de réessayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("Échec de la création de l'distribution.");
		}

		return distribution;

	}
}
