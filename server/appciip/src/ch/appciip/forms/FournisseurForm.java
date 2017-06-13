package forms;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Adresse;
import beans.Comite;
import beans.Fournisseur;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FournisseurManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class FournisseurForm extends Form {

	public FournisseurForm(FournisseurManager f) {
		this.f = f;
	}

	public FournisseurForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
			FournisseurManager f, EvenementManager n, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.l = l;
		this.f = f;
		this.n = n;
		this.p = p;
		this.s = s;
	}

	public Fournisseur retrouverFournisseur(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_METIER);
		String id = getValeurChamp(request, CHAMP_ID);

		Fournisseur fournisseur = new Fournisseur();
		traiterId(fournisseur, id);

		fournisseur.setNom(nom);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				fournisseur = f.retrouver(fournisseur);
				setResultat("Succ�s de la recherche du profil m�tier.");
			} else

				setResultat("�chec de la recherche du profil m�tier. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la recherche.");
			setResultat("�chec de la recherche.");
		}

		return fournisseur;
	}

	public Fournisseur creerFournisseur(HttpServletRequest request) {

		Adresse adresse;

		/*
		 * Si l'utilisateur choisit un adresse d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauAdresse = getValeurChamp(request, CHAMP_CHOIX_SERVICE);
		if (ANCIEN_SERVICE.equals(choixNouveauAdresse)) {
			/* R�cup�ration de l'id du adresse choisi */
			String idAncienAdresse = getValeurChamp(request, CHAMP_LISTE_SERVICES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienAdresse);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_SERVICE, "Adresse inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/* R�cup�ration de l'objet adresse correspondant dans la session */
			HttpSession session = request.getSession();
			adresse = ((Map<Long, Adresse>) session.getAttribute(SESSION_SERVICES)).get(id);
		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'un adresse existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Fournisseur cr��.
			 */
			AdresseForm adresseForm = new AdresseForm(l);
			adresse = adresseForm.creerAdresse(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier FournisseurForm dans
			 * la map d'erreurs courante, actuellement vide.
			 */
			erreurs = adresseForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un fournisseur.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_METIER);
		String email = getValeurChamp(request, CHAMP_DESCRIPTION);
		Boolean valide = Boolean.parseBoolean(getValeurChamp(request, CHAMP_VALIDE));

		String identifiant = getValeurChamp(request, CHAMP_VALIDEUR);
		String dateValidation = getValeurChamp(request, CHAMP_DATE_VALIDATION);
		String commentaires = getValeurChamp(request, CHAMP_COMMENTAIRES);

		Fournisseur fournisseur = new Fournisseur();

		try {

			traiterAdresse(fournisseur, adresse);

			traiterNom(fournisseur, nom);
			traiterEmail(fournisseur, email);
			// traiterValide(fournisseur, valide);
			// traiterIdentifiant(fournisseur, identifiant);
			// traiterDateValidation(fournisseur, dateValidation);

			// traiterCommentaires(fournisseur, commentaires);

			if (getErreurs().isEmpty()) {

				if (f.retrouver(fournisseur) == null) {
					fournisseur = f.creer(fournisseur);
					setResultat("Succ�s de la cr�ation du profil m�tier.");
				} else {
					setResultat("Comite m�tier d�j� existant.");
				}

			} else {
				setResultat("�chec de la cr�ation du profil m�tier.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation du profil m�tier : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return fournisseur;
	}

	public Fournisseur associerComite(HttpServletRequest request) {
		/* R�cup�ration de l'id du client choisi */
		String idFournisseur = getValeurChamp(request, CHAMP_LISTE_METIERS);
		Long idMet = null;
		String idComite = getValeurChamp(request, CHAMP_LISTE_COMITES);
		Long idPro = null;
		try {
			idMet = Long.parseLong(idFournisseur);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_METIER, "Comite m�tier inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			idMet = 0L;
		}
		try {
			idPro = Long.parseLong(idComite);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_COMITE, "Comite d'acc�s inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			idPro = 0L;
		}
		/* R�cup�ration de l'objet client correspondant dans la session */
		HttpSession session = request.getSession();
		Fournisseur fournisseur = ((Map<Long, Fournisseur>) session.getAttribute(SESSION_METIERS)).get(idMet);
		Localisation profil = ((Map<Long, Localisation>) session.getAttribute(SESSION_COMITES)).get(idPro);

		try {

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {

					// fournisseur = m.associerComite(fournisseur, profil);
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

		return fournisseur;
	}

	public Fournisseur consulterFournisseur(HttpServletRequest request) {
		Fournisseur fournisseur = new Fournisseur();
		/* R�cup�ration de l'id du client choisi */
		String idFournisseur = getValeurChamp(request, CHAMP_LISTE_METIERS);
		Long id = null;
		try {
			id = Long.parseLong(idFournisseur);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_METIER, "Comite m�tier inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/* R�cup�ration de l'objet client correspondant dans la session */
			HttpSession session = request.getSession();
			fournisseur = ((Map<Long, Fournisseur>) session.getAttribute(SESSION_METIERS)).get(id);
		}

		return fournisseur;
	}

	public Fournisseur modifierFournisseur(HttpServletRequest request) {

		Adresse adresse;

		/*
		 * Si l'utilisateur choisit un adresse d�j� existant, pas de validation
		 * � effectuer
		 */
		String choixNouveauAdresse = getValeurChamp(request, CHAMP_CHOIX_SERVICE);
		if (ANCIEN_SERVICE.equals(choixNouveauAdresse)) {
			/* R�cup�ration de l'id du adresse choisi */
			String idAncienAdresse = getValeurChamp(request, CHAMP_LISTE_SERVICES);
			Long id = null;
			try {
				id = Long.parseLong(idAncienAdresse);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_SERVICE, "Adresse inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/* R�cup�ration de l'objet adresse correspondant dans la session */
			HttpSession session = request.getSession();
			adresse = ((Map<Long, Adresse>) session.getAttribute(SESSION_SERVICES)).get(id);
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
			 * de la map d'erreur cr��e par l'objet m�tier AdresseForm dans la
			 * map d'erreurs courante, actuellement vide.
			 */
			erreurs = adresseForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un fournisseur.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_METIER);
		String email = getValeurChamp(request, CHAMP_DESCRIPTION);
		Boolean valide = Boolean.parseBoolean(getValeurChamp(request, CHAMP_VALIDE));

		String identifiant = getValeurChamp(request, CHAMP_VALIDEUR);
		String dateValidation = getValeurChamp(request, CHAMP_DATE_VALIDATION);
		String commentaires = getValeurChamp(request, CHAMP_COMMENTAIRES);

		String id = getValeurChamp(request, CHAMP_ID);

		Fournisseur fournisseur = new Fournisseur();
		if (!id.trim().isEmpty())
			traiterId(fournisseur, id);

		try {

			traiterAdresse(fournisseur, adresse);

			traiterNom(fournisseur, nom);
			traiterEmail(fournisseur, email);
			// traiterValide(fournisseur, valide);
			// traiterIdentifiant(fournisseur, identifiant);
			// traiterDateValidation(fournisseur, dateValidation);

			// traiterCommentaires(fournisseur, commentaires);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					f.modifier(fournisseur);
					setResultat("Succ�s de la modification du profil m�tier.");
				} else {
					setResultat("�chec de la modification du profil m�tier.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification du profil m�tier : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return fournisseur;
	}

	public Fournisseur creerFournisseur(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];
		String email = tokens[1];
		// Boolean valide = Boolean.parseBoolean(liste.get(2));

		String identifiant = tokens[2];
		// String dateValidation = liste.get(4);
		// String commentaires = liste.get(5);

		Adresse adresse = new Adresse(tokens[3]);
		adresse = l.retrouver(adresse);
		Fournisseur fournisseur = new Fournisseur();

		if (!nom.trim().isEmpty()) {
			try {
				traiterNom(fournisseur, nom);

				// traiterCommentaires(fournisseur, commentaires);
				traiterEmail(fournisseur, email);
				// traiterValide(fournisseur, valide);
				// traiterIdentifiant(fournisseur, identifiant);
				// traiterDateValidation(fournisseur, dateValidation);
				traiterAdresse(fournisseur, adresse);

				if (f.retrouver(fournisseur) == null) {
					fournisseur = f.creer(fournisseur);
					setResultat("Succ�s de la cr�ation du profil m�tier.");

				} else {
					fournisseur = f.retrouver(fournisseur);

					traiterEmail(fournisseur, email);
					// traiterValide(fournisseur, valide);
					// traiterIdentifiant(fournisseur, identifiant);
					// traiterDateValidation(fournisseur, dateValidation);

					// traiterCommentaires(fournisseur, commentaires);

					traiterAdresse(fournisseur, adresse);

					f.modifier(fournisseur);
					setResultat("Comite m�tier d�j� existant.");
				}
			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation du profil m�tier : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation du profil m�tier.");
		}

		return fournisseur;

	}

	public Fournisseur associerComite(ArrayList<String> liste) {
		Fournisseur fournisseur = new Fournisseur(liste.get(0));
		fournisseur = f.retrouver(fournisseur);
		Localisation profil = new Localisation(liste.get(1));
		profil = p.retrouver(profil);

		// fournisseur = m.associerComite(fournisseur, profil);

		return fournisseur;

	}

	public Fournisseur supprimerComite(HttpServletRequest request) {
		Fournisseur fournisseur = retrouverFournisseur(request);
		ComiteForm profilForm = new ComiteForm(p);
		Localisation profil = profilForm.consulterComite(request);
		// fournisseur = m.supprimerComite(fournisseur, profil);
		return fournisseur;
	}

}
