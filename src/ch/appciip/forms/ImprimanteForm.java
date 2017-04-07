package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Adresse;
import beans.Collaborateur;
import beans.Fournisseur;
import beans.Imprimante;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.BeamerManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.FournisseurManager;
import modele.ImprimanteManager;
import modele.SalleManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class ImprimanteForm extends Form {

	public ImprimanteForm(CollaborateurManager u, ApplicationManager a, BeamerManager b, ImprimanteManager i,
			SeanceManager d, AdresseManager l, FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s,
			FournisseurManager f, SalleManager sa) {
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

	public ImprimanteForm(ImprimanteManager i) {
		this.i = i;
	}

	public Imprimante consulterImprimante(HttpServletRequest request) {
		Imprimante imprimante = new Imprimante();
		/* R�cup�ration de l'id de l'imprimante choisie */
		String idImprimante = getValeurChamp(request, CHAMP_LISTE_BEAMERS);
		Long id = null;
		try {
			id = Long.parseLong(idImprimante);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_BEAMER, "Imprimante inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet imprimante correspondant dans la session
			 */
			HttpSession session = request.getSession();

			imprimante = ((Map<Long, Imprimante>) session.getAttribute(SESSION_BEAMERS)).get(id);
		}

		return imprimante;
	}

	public Imprimante retrouverImprimante(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String id = getValeurChamp(request, CHAMP_ID);

		Imprimante imprimante = new Imprimante();

		traiterId(imprimante, id);

		imprimante.setNumero(numero);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				imprimante = i.retrouver(imprimante);

				setResultat("Succ�s de la recherche de l'imprimante.");
				return imprimante;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier le numero de l'imprimante.");
			setResultat("�chec de la recherche.");

		}

		return imprimante;
	}

	public Imprimante creerImprimante(HttpServletRequest request) {
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
		 * sp�cifiques � une imprimante.
		 */

		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String prix = getValeurChamp(request, CHAMP_DESCRIPTION);

		Imprimante imprimante = new Imprimante();

		try {

			traiterContactInterne(imprimante, contactInterne);

			traiterNumero(imprimante, numero);
			traiterPrix(imprimante, prix);

			if (getErreurs().isEmpty()) {

				if (i.retrouver(imprimante) == null) {
					imprimante = i.creer(imprimante);
					setResultat("Succ�s de la cr�ation de l'imprimante.");
				} else {
					setResultat("Imprimante d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation de l'imprimante.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de l'imprimante : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return imprimante;
	}

	public Imprimante modifierImprimante(HttpServletRequest request) {
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
		 * sp�cifiques � une imprimante.
		 */

		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String prix = getValeurChamp(request, CHAMP_DESCRIPTION);

		String id = getValeurChamp(request, CHAMP_ID);

		Imprimante imprimante = new Imprimante();
		if (!id.trim().isEmpty()) {
			traiterId(imprimante, id);
		}

		try {

			traiterContactInterne(imprimante, contactInterne);

			traiterNumero(imprimante, numero);
			traiterPrix(imprimante, prix);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					i.modifier(imprimante);
					setResultat("Succ�s de la modification de l'imprimante.");
				} else {
					setResultat("�chec de la modification de l'imprimante.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modifiation.");
			setResultat(
					"�chec de la modification de l'imprimante : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return imprimante;

	}

	public Imprimante creerImprimante(String[] tokens) {
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

		Utilisateur contactInterne = new Utilisateur(tokens[7], tokens[8]);
		Fournisseur fournisseur = new Fournisseur(tokens[9]);
		Adresse adresse = new Adresse(tokens[10]);

		contactInterne = u.retrouver(contactInterne);
		fournisseur = f.retrouver(fournisseur);

		adresse = l.retrouver(adresse);

		Imprimante imprimante = new Imprimante();

		if (!numero.trim().isEmpty()) {
			try {
				traiterNumero(imprimante, numero);
				traiterPrix(imprimante, prix);
				traiterMarque(imprimante, marque);
				traiterAmortissement(imprimante, amortissement);
				traiterType(imprimante, type);
				traiterDateAchat(imprimante, dateAchat);
				traiterDatePeremption(imprimante, datePeremption);

				traiterContactInterne(imprimante, contactInterne);

				traiterFournisseur(imprimante, fournisseur);

				traiterAdresse(imprimante, adresse);

				if (i.retrouver(imprimante) == null) {
					imprimante = i.creer(imprimante);
					setResultat("Succ�s de la cr�ation de l'imprimante.");
				} else {

					imprimante = i.retrouver(imprimante);
					traiterPrix(imprimante, prix);
					traiterMarque(imprimante, marque);
					traiterAmortissement(imprimante, amortissement);
					traiterType(imprimante, type);
					traiterDateAchat(imprimante, dateAchat);
					traiterDatePeremption(imprimante, datePeremption);

					traiterContactInterne(imprimante, contactInterne);

					traiterFournisseur(imprimante, fournisseur);

					traiterAdresse(imprimante, adresse);

					i.modifier(imprimante);
					setResultat("Imprimante d�j� existante.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation de l'imprimante : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation de l'imprimante.");
		}

		return imprimante;

	}
}
