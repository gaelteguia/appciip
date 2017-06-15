package forms;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Collaborateur;
import beans.Comite;
import beans.Seance;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.SeanceManager;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class ComiteForm extends Form {

	public ComiteForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
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

	public ComiteForm(ComiteManager p) {
		this.p = p;
	}

	public Localisation associerSeance(HttpServletRequest request) {
		/* R�cup�ration de l'id du comite choisi */
		String idComite = getValeurChamp(request, CHAMP_LISTE_COMITES);
		Long idPro = null;
		String idSeance = getValeurChamp(request, CHAMP_LISTE_DROITS);
		Long idDro = null;
		try {
			idPro = Long.parseLong(idComite);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_COMITE, "Comite inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			idPro = 0L;
		}
		try {
			idDro = Long.parseLong(idSeance);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_DROIT, "Seance inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			idDro = 0L;
		}
		/* R�cup�ration de l'objet comite correspondant dans la session */
		HttpSession session = request.getSession();
		Localisation comite = ((Map<Long, Localisation>) session.getAttribute(SESSION_COMITES)).get(idPro);
		Seance droit = ((Map<Long, Seance>) session.getAttribute(SESSION_DROITS)).get(idDro);
		try {

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					comite = p.associerSeance(comite, droit);
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
		return comite;
	}

	public Localisation consulterComite(HttpServletRequest request) {
		Localisation comite = new Localisation();
		/* R�cup�ration de l'id du comite choisi */
		String idComite = getValeurChamp(request, CHAMP_LISTE_COMITES);
		Long id = null;
		try {
			id = Long.parseLong(idComite);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_COMITE, "Comite d'acc�s inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/* R�cup�ration de l'objet comite correspondant dans la session */
			HttpSession session = request.getSession();
			comite = ((Map<Long, Localisation>) session.getAttribute(SESSION_COMITES)).get(id);
		}

		return comite;
	}

	public Localisation creerComite(HttpServletRequest request) {
		Localisation parent;

		/*
		 * Si l'utilisateur choisit une parent d�j� existante, pas de validation
		 * � effectuer
		 */
		String choixNouvelleComite = getValeurChamp(request, CHAMP_CHOIX_COMITE);
		if (ANCIEN_COMITE.equals(choixNouvelleComite)) {
			/* R�cup�ration de l'id de l'parent choisie */
			String idAncienneComite = getValeurChamp(request, CHAMP_LISTE_COMITES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienneComite);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_COMITE, "Comite inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet parent correspondant dans la session
			 */
			HttpSession session = request.getSession();
			parent = ((Map<Long, Localisation>) session.getAttribute(SESSION_COMITES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'une parent existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Comite cr��.
			 */
			ComiteForm parentForm = new ComiteForm(p);
			parent = parentForm.creerComite(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier ComiteForm dans la
			 * map d'erreurs courante, actuellement vide.
			 */
			erreurs = parentForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un comite.
		 */

		String acronyme = getValeurChamp(request, CHAMP_ACRONYME);

		Localisation comite = new Localisation();

		try {
			traiterParent(comite, parent);

			traiterAcronyme(comite, acronyme);

			if (getErreurs().isEmpty()) {

				if (p.retrouver(comite) == null) {
					comite = p.creer(comite);
					setResultat("Succ�s de la cr�ation du comite d'acc�s.");
				} else {
					setResultat("Comite d'acc�s d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation du comite d'acc�s.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation du comite d'acc�s : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return comite;
	}

	public Localisation modifierComite(HttpServletRequest request) {
		Localisation parent;

		/*
		 * Si l'utilisateur choisit une parent d�j� existante, pas de validation
		 * � effectuer
		 */
		String choixNouvelleComite = getValeurChamp(request, CHAMP_CHOIX_COMITE);
		if (ANCIEN_COMITE.equals(choixNouvelleComite)) {
			/* R�cup�ration de l'id d'parent choisie */
			String idAncienneComite = getValeurChamp(request, CHAMP_LISTE_COMITES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienneComite);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_COMITE, "Comite inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet parent correspondant dans la session
			 */
			HttpSession session = request.getSession();
			parent = ((Map<Long, Localisation>) session.getAttribute(SESSION_COMITES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'une parent existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Comite cr��.
			 */
			ComiteForm parentForm = new ComiteForm(p);
			parent = parentForm.creerComite(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier ComiteForm dans la
			 * map d'erreurs courante, actuellement vide.
			 */
			erreurs = parentForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un comite.
		 */

		String acronyme = getValeurChamp(request, CHAMP_ACRONYME);
		String id = getValeurChamp(request, CHAMP_ID);

		Localisation comite = new Localisation();
		if (!id.trim().isEmpty())
			traiterId(comite, id);

		try {
			traiterParent(comite, parent);

			traiterAcronyme(comite, acronyme);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					p.modifier(comite);
					setResultat("Succ�s de la modification du comite d'acc�s.");
				} else {
					setResultat("�chec de la modification du comite d'acc�s.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification du comite d'acc�s : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return comite;
	}

	public Localisation retrouverComite(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String acronyme = getValeurChamp(request, CHAMP_ACRONYME);
		String id = getValeurChamp(request, CHAMP_ID);

		Localisation comite = new Localisation();
		traiterId(comite, id);
		comite.setAcronyme(acronyme);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				comite = p.retrouver(comite);

				setResultat("Succ�s de la recherche du comite d'acc�s.");
				return comite;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre acronyme.");
			setResultat("�chec de la recherche.");
		}

		return comite;
	}

	public Localisation creerComite(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];
		String acronyme = tokens[1];

		String statut = tokens[2];
		String officiel = tokens[3];
		String description = tokens[4];
		String website = tokens[5];

		Utilisateur responsable = new Utilisateur(tokens[6]);
		Localisation parent = new Localisation(tokens[7]);

		parent = p.retrouver(parent);
		Localisation comite = new Localisation();

		if (!acronyme.trim().isEmpty()) {
			try {
				traiterAcronyme(comite, acronyme);
				traiterNom(comite, nom);
				traiterStatut(comite, statut);
				traiterOfficiel(comite, officiel);
				traiterDescription(comite, description);
				traiterWebsite(comite, website);

				traiterResponsable(comite, responsable);

				traiterParent(comite, parent);

				if (p.retrouver(comite) == null) {
					comite = p.creer(comite);
					setResultat("Succ�s de la cr�ation.");

				} else {
					comite = p.retrouver(comite);

					traiterAcronyme(comite, acronyme);
					traiterNom(comite, nom);
					traiterStatut(comite, statut);
					traiterOfficiel(comite, officiel);
					traiterDescription(comite, description);
					traiterWebsite(comite, website);

					traiterResponsable(comite, responsable);

					traiterParent(comite, parent);
					p.modifier(comite);
					setResultat("Comite d�j� existant.");
				}
			}

			catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation du comite d'acc�s : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation du comite d'acc�s.");

		}

		return comite;

	}

	public Localisation associerSeance(ArrayList<String> liste) {
		Seance droit = new Seance(liste.get(1));
		droit = d.retrouver(droit);
		Localisation comite = new Localisation(liste.get(0));
		comite = p.retrouver(comite);

		comite = p.associerSeance(comite, droit);

		return comite;

	}

	public Localisation supprimerSeance(HttpServletRequest request) {
		Localisation comite = retrouverComite(request);
		SeanceForm droitForm = new SeanceForm(d);
		Seance droit = droitForm.consulterSeance(request);
		comite = p.supprimerSeance(comite, droit);
		return comite;
	}

}
