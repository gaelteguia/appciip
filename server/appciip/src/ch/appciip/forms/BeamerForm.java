package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Beamer;
import beans.Collaborateur;
import beans.Fournisseur;
import beans.Salle;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.BeamerManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.FournisseurManager;
import modele.SalleManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class BeamerForm extends Form {

	public BeamerForm(CollaborateurManager u, ApplicationManager a, BeamerManager b, SeanceManager d, AdresseManager l,
			FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s, FournisseurManager f,
			SalleManager sa) {
		this.u = u;
		this.b = b;
		this.d = d;
		this.l = l;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;
		this.f = f;
		this.sa = sa;
	}

	public BeamerForm(BeamerManager b) {
		this.b = b;
	}

	public Beamer consulterBeamer(HttpServletRequest request) {
		Beamer beamer = new Beamer();
		/* R�cup�ration de l'id de l'beamer choisie */
		String idBeamer = getValeurChamp(request, CHAMP_LISTE_BEAMERS);
		Long id = null;
		try {
			id = Long.parseLong(idBeamer);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_BEAMER, "Beamer inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet beamer correspondant dans la session
			 */
			HttpSession session = request.getSession();

			beamer = ((Map<Long, Beamer>) session.getAttribute(SESSION_BEAMERS)).get(id);
		}

		return beamer;
	}

	public Beamer retrouverBeamer(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String id = getValeurChamp(request, CHAMP_ID);

		Beamer beamer = new Beamer();

		traiterId(beamer, id);

		beamer.setNumero(numero);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				beamer = b.retrouver(beamer);

				setResultat("Succ�s de la recherche de l'beamer.");
				return beamer;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier le numero de l'beamer.");
			setResultat("�chec de la recherche.");

		}

		return beamer;
	}

	public Beamer creerBeamer(HttpServletRequest request) {
		Utilisateur contactInterne;

		/*
		 * Si l'utilisateur choisit un contact d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauContactTechnique = getValeurChamp(request, CHAMP_CHOIX_CONTACT_TECHNIQUE);

		if (ANCIEN_CONTACT_TECHNIQUE.equals(choixNouveauContactTechnique)) {
			try {
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_TECHNIQUE,
						"Contact technique inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			}
		} else {
		}
		/*
		 * Si l'utilisateur choisit un contact d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauContactInterne = getValeurChamp(request, CHAMP_CHOIX_CONTACT_FONCTIONNEL);
		if (ANCIEN_CONTACT_FONCTIONNEL.equals(choixNouveauContactInterne)) {
			/* R�cup�ration de l'id du contact choisi */
			String idAncienContactInterne = getValeurChamp(request, CHAMP_LISTE_CONTACT_FONCTIONNELS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienContactInterne);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_FONCTIONNEL,
						"Contact fonctionnel inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			contactInterne = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			contactInterne = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une beamer.
		 */

		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String prix = getValeurChamp(request, CHAMP_DESCRIPTION);

		Beamer beamer = new Beamer();

		try {

			traiterContactInterne(beamer, contactInterne);

			traiterNumero(beamer, numero);
			traiterPrix(beamer, prix);

			if (getErreurs().isEmpty()) {

				if (b.retrouver(beamer) == null) {
					beamer = b.creer(beamer);
					setResultat("Succ�s de la cr�ation de l'beamer.");
				} else {
					setResultat("Beamer d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation de l'beamer.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de l'beamer : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return beamer;
	}

	public Beamer modifierBeamer(HttpServletRequest request) {
		Utilisateur contactInterne;

		/*
		 * Si l'utilisateur choisit un contact d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauContactTechnique = getValeurChamp(request, CHAMP_CHOIX_CONTACT_TECHNIQUE);

		if (ANCIEN_CONTACT_TECHNIQUE.equals(choixNouveauContactTechnique)) {
			try {
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_TECHNIQUE,
						"Contact technique inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			}
		} else {
		}
		/*
		 * Si l'utilisateur choisit un contact d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauContactInterne = getValeurChamp(request, CHAMP_CHOIX_CONTACT_FONCTIONNEL);
		if (ANCIEN_CONTACT_FONCTIONNEL.equals(choixNouveauContactInterne)) {
			/* R�cup�ration de l'id du contact choisi */
			String idAncienContactInterne = getValeurChamp(request, CHAMP_LISTE_CONTACT_FONCTIONNELS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienContactInterne);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_FONCTIONNEL,
						"Contact fonctionnel inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			contactInterne = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			contactInterne = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une beamer.
		 */

		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String prix = getValeurChamp(request, CHAMP_DESCRIPTION);

		String id = getValeurChamp(request, CHAMP_ID);

		Beamer beamer = new Beamer();
		if (!id.trim().isEmpty()) {
			traiterId(beamer, id);
		}

		try {

			traiterContactInterne(beamer, contactInterne);

			traiterNumero(beamer, numero);
			traiterPrix(beamer, prix);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					b.modifier(beamer);
					setResultat("Succ�s de la modification de l'beamer.");
				} else {
					setResultat("�chec de la modification de l'beamer.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modifiation.");
			setResultat(
					"�chec de la modification de l'beamer : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return beamer;

	}

	public Beamer creerBeamer(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String numero = tokens[0];
		String prix = tokens[1];
		String marque = tokens[2];
		String amortissement = tokens[3];
		String type = tokens[4];
		String dateAchat = tokens[5];
		String datePeremption = tokens[6];

		Utilisateur contactInterne = new Utilisateur(tokens[7]);
		Fournisseur fournisseur = new Fournisseur(tokens[8]);
		Salle salle = new Salle(tokens[9]);

		contactInterne = u.retrouver(contactInterne);
		fournisseur = f.retrouver(fournisseur);

		salle = sa.retrouver(salle);

		Beamer beamer = new Beamer();

		if (!numero.trim().isEmpty()) {
			try {
				traiterNumero(beamer, numero);
				traiterPrix(beamer, prix);
				traiterMarque(beamer, marque);
				traiterAmortissement(beamer, amortissement);
				traiterType(beamer, type);
				traiterDateAchat(beamer, dateAchat);
				traiterDatePeremption(beamer, datePeremption);

				traiterContactInterne(beamer, contactInterne);

				traiterFournisseur(beamer, fournisseur);

				traiterSalle(beamer, salle);

				if (b.retrouver(beamer) == null) {
					beamer = b.creer(beamer);
					setResultat("Succ�s de la cr�ation de l'beamer.");
				} else {

					beamer = b.retrouver(beamer);
					traiterPrix(beamer, prix);
					traiterMarque(beamer, marque);
					traiterAmortissement(beamer, amortissement);
					traiterType(beamer, type);
					traiterDateAchat(beamer, dateAchat);
					traiterDatePeremption(beamer, datePeremption);

					traiterContactInterne(beamer, contactInterne);

					traiterFournisseur(beamer, fournisseur);

					traiterSalle(beamer, salle);

					b.modifier(beamer);
					setResultat("Beamer d�j� existante.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation de l'beamer : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation de l'beamer.");
		}

		return beamer;

	}
}
