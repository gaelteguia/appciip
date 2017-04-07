package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Telephone;
import beans.Collaborateur;
import beans.Fournisseur;
import beans.Salle;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.BeamerManager;
import modele.TelephoneManager;
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
public class TelephoneForm extends Form {

	public TelephoneForm(CollaborateurManager u, ApplicationManager a, BeamerManager b, SeanceManager d,
			AdresseManager l, FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s,
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

	public TelephoneForm(TelephoneManager t) {
		this.t = t;
	}

	public Telephone consulterTelephone(HttpServletRequest request) {
		Telephone telephone = new Telephone();
		/* R�cup�ration de l'id de l'telephone choisie */
		String idTelephone = getValeurChamp(request, CHAMP_LISTE_BEAMERS);
		Long id = null;
		try {
			id = Long.parseLong(idTelephone);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_BEAMER, "Telephone inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet telephone correspondant dans la session
			 */
			HttpSession session = request.getSession();

			telephone = ((Map<Long, Telephone>) session.getAttribute(SESSION_BEAMERS)).get(id);
		}

		return telephone;
	}

	public Telephone retrouverTelephone(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String id = getValeurChamp(request, CHAMP_ID);

		Telephone telephone = new Telephone();

		traiterId(telephone, id);

		telephone.setNumero(numero);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				telephone = t.retrouver(telephone);

				setResultat("Succ�s de la recherche de l'telephone.");
				return telephone;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier le numero de l'telephone.");
			setResultat("�chec de la recherche.");

		}

		return telephone;
	}

	public Telephone creerTelephone(HttpServletRequest request) {
		Utilisateur collaborateur;

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
		String choixNouveauCollaborateur = getValeurChamp(request, CHAMP_CHOIX_CONTACT_FONCTIONNEL);
		if (ANCIEN_CONTACT_FONCTIONNEL.equals(choixNouveauCollaborateur)) {
			/* R�cup�ration de l'id du contact choisi */
			String idAncienCollaborateur = getValeurChamp(request, CHAMP_LISTE_CONTACT_FONCTIONNELS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienCollaborateur);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_FONCTIONNEL,
						"Contact fonctionnel inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			collaborateur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			collaborateur = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une telephone.
		 */

		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String prix = getValeurChamp(request, CHAMP_DESCRIPTION);

		Telephone telephone = new Telephone();

		try {

			traiterCollaborateur(telephone, collaborateur);

			traiterNumero(telephone, numero);
			traiterPrix(telephone, prix);

			if (getErreurs().isEmpty()) {

				if (t.retrouver(telephone) == null) {
					telephone = t.creer(telephone);
					setResultat("Succ�s de la cr�ation de l'telephone.");
				} else {
					setResultat("Telephone d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation de l'telephone.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de l'telephone : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return telephone;
	}

	public Telephone modifierTelephone(HttpServletRequest request) {
		Utilisateur collaborateur;

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
		String choixNouveauCollaborateur = getValeurChamp(request, CHAMP_CHOIX_CONTACT_FONCTIONNEL);
		if (ANCIEN_CONTACT_FONCTIONNEL.equals(choixNouveauCollaborateur)) {
			/* R�cup�ration de l'id du contact choisi */
			String idAncienCollaborateur = getValeurChamp(request, CHAMP_LISTE_CONTACT_FONCTIONNELS);
			Long id = null;
			try {
				id = Long.parseLong(idAncienCollaborateur);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_CONTACT_FONCTIONNEL,
						"Contact fonctionnel inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet utilisateur correspondant dans la session
			 */
			HttpSession session = request.getSession();
			collaborateur = ((Map<Long, Utilisateur>) session.getAttribute(SESSION_UTILISATEURS)).get(id);
		} else {

			collaborateur = new Utilisateur();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une telephone.
		 */

		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String prix = getValeurChamp(request, CHAMP_DESCRIPTION);

		String id = getValeurChamp(request, CHAMP_ID);

		Telephone telephone = new Telephone();
		if (!id.trim().isEmpty()) {
			traiterId(telephone, id);
		}

		try {

			traiterCollaborateur(telephone, collaborateur);

			traiterNumero(telephone, numero);
			traiterPrix(telephone, prix);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					t.modifier(telephone);
					setResultat("Succ�s de la modification de l'telephone.");
				} else {
					setResultat("�chec de la modification de l'telephone.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modifiation.");
			setResultat(
					"�chec de la modification de l'telephone : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return telephone;

	}

	public Telephone creerTelephone(String[] tokens) {
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

		Utilisateur collaborateur = new Utilisateur(tokens[7], tokens[8]);
		Fournisseur fournisseur = new Fournisseur(tokens[9]);
		Salle salle = new Salle(tokens[10]);

		collaborateur = u.retrouver(collaborateur);
		fournisseur = f.retrouver(fournisseur);

		salle = sa.retrouver(salle);

		Telephone telephone = new Telephone();

		if (!numero.trim().isEmpty()) {
			try {
				traiterNumero(telephone, numero);
				traiterPrix(telephone, prix);
				traiterMarque(telephone, marque);
				traiterAmortissement(telephone, amortissement);
				traiterType(telephone, type);
				traiterDateAchat(telephone, dateAchat);
				traiterDatePeremption(telephone, datePeremption);

				traiterCollaborateur(telephone, collaborateur);

				traiterFournisseur(telephone, fournisseur);

				traiterSalle(telephone, salle);

				if (t.retrouver(telephone) == null) {
					telephone = t.creer(telephone);
					setResultat("Succ�s de la cr�ation de l'telephone.");
				} else {

					telephone = t.retrouver(telephone);
					traiterPrix(telephone, prix);
					traiterMarque(telephone, marque);
					traiterAmortissement(telephone, amortissement);
					traiterType(telephone, type);
					traiterDateAchat(telephone, dateAchat);
					traiterDatePeremption(telephone, datePeremption);

					traiterCollaborateur(telephone, collaborateur);

					traiterFournisseur(telephone, fournisseur);

					traiterSalle(telephone, salle);

					t.modifier(telephone);
					setResultat("Telephone d�j� existante.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation de l'telephone : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation de l'telephone.");
		}

		return telephone;

	}
}
