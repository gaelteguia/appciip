package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Adresse;
import beans.Collaborateur;
import beans.Evenement;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.DatabaseException;
import modele.SeanceManager;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.ComiteManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class EvenementForm extends Form {

	public EvenementForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
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

	public Evenement consulterEvenement(HttpServletRequest request) {
		Evenement evenement = new Evenement();
		/* R�cup�ration de l'id de la evenement choisie */
		String idEvenement = getValeurChamp(request, CHAMP_LISTE_EVENEMENTS);
		Long id = null;
		try {
			id = Long.parseLong(idEvenement);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_EVENEMENT, "Evenement inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet evenement correspondant dans la session
			 */
			HttpSession session = request.getSession();
			evenement = ((Map<Long, Evenement>) session.getAttribute(SESSION_EVENEMENTS)).get(id);
		}

		return evenement;
	}

	public Evenement creerEvenement(HttpServletRequest request) {
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
		 * sp�cifiques � une evenement.
		 */

		String titre = getValeurChamp(request, CHAMP_TITRE);
		String contenu = getValeurChamp(request, CHAMP_CONTENU);
		String date = getValeurChamp(request, CHAMP_DATE);

		Evenement evenement = new Evenement();

		try {
			traiterCollaborateur(evenement, utilisateur);

			traiterTitre(evenement, titre);
			traiterDescription(evenement, contenu);
			traiterDate(evenement, date);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					evenement = n.creer(evenement);
					setResultat("Succ�s de la cr�ation de la evenement.");
				} else {
					setResultat("�chec de la cr�ation de la evenement.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de la evenement : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return evenement;
	}

	public Evenement creerEvenement(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String titre = tokens[0];

		String description = tokens[1];

		String date = tokens[2];

		Adresse adresse = new Adresse(tokens[3]);

		adresse.setVille(tokens[6]);
		adresse.setPays(tokens[7]);

		Utilisateur collaborateur = new Utilisateur(tokens[8], tokens[9]);

		collaborateur = u.retrouver(collaborateur);

		Evenement evenement = new Evenement();

		if (!titre.trim().isEmpty()) {
			try {
				traiterTitre(evenement, titre);
				traiterDate(evenement, date);

				traiterNoRue(adresse, tokens[4]);
				traiterNpa(adresse, tokens[5]);

				traiterAdresse(evenement, adresse);
				traiterCollaborateur(evenement, collaborateur);

				traiterDescription(evenement, description);

				if (n.retrouver(evenement) == null) {
					evenement = n.creer(evenement);
					setResultat("Succ�s de la cr�ation de l'evenement.");
				} else {

					evenement = n.retrouver(evenement);
					traiterCollaborateur(evenement, collaborateur);
					traiterDate(evenement, date);
					traiterAdresse(evenement, adresse);
					traiterDescription(evenement, description);
					n.modifier(evenement);
					setResultat("Evenement déjà existant.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation de l'evenement : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation de l'evenement.");
		}

		return evenement;

	}

	public Evenement retrouverEvenement(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String id = getValeurChamp(request, CHAMP_ID);

		Evenement evenement = new Evenement();
		traiterId(evenement, id);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				evenement = n.retrouver(evenement);
				setResultat("Succ�s de la recherche de la evenement.");
			} else

				setResultat("�chec de la recherche de la evenement. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la recherche.");
			setResultat("�chec de la recherche.");
		}

		return evenement;
	}

	public Evenement modifierEvenement(HttpServletRequest request) {
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
		 * sp�cifiques � une evenement.
		 */

		String titre = getValeurChamp(request, CHAMP_TITRE);
		String contenu = getValeurChamp(request, CHAMP_CONTENU);
		String date = getValeurChamp(request, CHAMP_DATE);

		String id = getValeurChamp(request, CHAMP_ID);

		Evenement evenement = new Evenement();
		if (!id.trim().isEmpty())
			traiterId(evenement, id);

		try {
			traiterCollaborateur(evenement, utilisateur);

			traiterTitre(evenement, titre);
			traiterDescription(evenement, contenu);
			traiterDate(evenement, date);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					n.modifier(evenement);
					setResultat("Succ�s de la modification de la evenement.");
				} else {
					setResultat("�chec de la modification de la evenement.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification de la evenement : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return evenement;
	}

}
