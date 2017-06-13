package forms;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Adresse;
import beans.Comite;
import beans.Salle;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.SalleManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class SalleForm extends Form {

	public SalleForm(SalleManager sa) {
		this.sa = sa;
	}

	public SalleForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l, SalleManager sa,
			EvenementManager n, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.l = l;
		this.sa = sa;
		this.n = n;
		this.p = p;
		this.s = s;
	}

	public Salle retrouverSalle(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_SALLE);
		String id = getValeurChamp(request, CHAMP_ID);

		Salle salle = new Salle();
		traiterId(salle, id);

		salle.setNom(nom);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				salle = sa.retrouver(salle);
				setResultat("Succ�s de la recherche du profil m�tier.");
			} else

				setResultat("�chec de la recherche du profil m�tier. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la recherche.");
			setResultat("�chec de la recherche.");
		}

		return salle;
	}

	public Salle creerSalle(HttpServletRequest request) {

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
			 * existant et de r�cup�rer l'objet Salle cr��.
			 */
			AdresseForm adresseForm = new AdresseForm(l);
			adresse = adresseForm.creerAdresse(request);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier SalleForm dans la map
			 * d'erreurs courante, actuellement vide.
			 */
			erreurs = adresseForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un salle.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_SALLE);
		String capacite = getValeurChamp(request, CHAMP_DESCRIPTION);
		Boolean valide = Boolean.parseBoolean(getValeurChamp(request, CHAMP_VALIDE));

		String description = getValeurChamp(request, CHAMP_VALIDEUR);
		String dateValidation = getValeurChamp(request, CHAMP_DATE_VALIDATION);
		String commentaires = getValeurChamp(request, CHAMP_COMMENTAIRES);

		Salle salle = new Salle();

		try {

			traiterAdresse(salle, adresse);

			traiterNom(salle, nom);
			// traiterCapacite(salle, capacite);
			// traiterValide(salle, valide);
			// traiterDescription(salle, description);
			// traiterDateValidation(salle, dateValidation);

			// traiterCommentaires(salle, commentaires);

			if (getErreurs().isEmpty()) {

				if (sa.retrouver(salle) == null) {
					salle = sa.creer(salle);
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

		return salle;
	}

	public Salle associerComite(HttpServletRequest request) {
		/* R�cup�ration de l'id du client choisi */
		String idSalle = getValeurChamp(request, CHAMP_LISTE_SALLES);
		Long idMet = null;
		String idComite = getValeurChamp(request, CHAMP_LISTE_COMITES);
		Long idPro = null;
		try {
			idMet = Long.parseLong(idSalle);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_SALLE, "Comite m�tier inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
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
		Salle salle = ((Map<Long, Salle>) session.getAttribute(SESSION_SALLES)).get(idMet);
		Localisation profil = ((Map<Long, Localisation>) session.getAttribute(SESSION_COMITES)).get(idPro);

		try {

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {

					// salle = m.associerComite(salle, profil);
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

		return salle;
	}

	public Salle consulterSalle(HttpServletRequest request) {
		Salle salle = new Salle();
		/* R�cup�ration de l'id du client choisi */
		String idSalle = getValeurChamp(request, CHAMP_LISTE_SALLES);
		Long id = null;
		try {
			id = Long.parseLong(idSalle);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_SALLE, "Salle inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/* R�cup�ration de l'objet client correspondant dans la session */
			HttpSession session = request.getSession();
			salle = ((Map<Long, Salle>) session.getAttribute(SESSION_SALLES)).get(id);
		}

		return salle;
	}

	public Salle modifierSalle(HttpServletRequest request) {

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
		 * sp�cifiques � un salle.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_SALLE);
		String capacite = getValeurChamp(request, CHAMP_DESCRIPTION);
		Boolean valide = Boolean.parseBoolean(getValeurChamp(request, CHAMP_VALIDE));

		String description = getValeurChamp(request, CHAMP_VALIDEUR);
		String dateValidation = getValeurChamp(request, CHAMP_DATE_VALIDATION);
		String commentaires = getValeurChamp(request, CHAMP_COMMENTAIRES);

		String id = getValeurChamp(request, CHAMP_ID);

		Salle salle = new Salle();
		if (!id.trim().isEmpty())
			traiterId(salle, id);

		try {

			traiterAdresse(salle, adresse);

			traiterNom(salle, nom);
			// traiterCapacite(salle, capacite);
			// traiterValide(salle, valide);
			// traiterDescription(salle, description);
			// traiterDateValidation(salle, dateValidation);

			// traiterCommentaires(salle, commentaires);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					sa.modifier(salle);
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

		return salle;
	}

	public Salle creerSalle(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];
		String capacite = tokens[1];
		// Boolean valide = Boolean.parseBoolean(liste.get(2));

		String description = tokens[2];
		// String dateValidation = liste.get(4);
		// String commentaires = liste.get(5);

		Adresse adresse = new Adresse(tokens[3]);
		adresse = l.retrouver(adresse);
		Salle salle = new Salle();

		if (!nom.trim().isEmpty()) {
			try {
				traiterNom(salle, nom);
				traiterCapacite(salle, capacite);
				traiterDescription(salle, description);
				// traiterCommentaires(salle, commentaires);

				// traiterValide(salle, valide);

				// traiterDateValidation(salle, dateValidation);
				traiterAdresse(salle, adresse);

				if (sa.retrouver(salle) == null) {
					salle = sa.creer(salle);
					setResultat("Succ�s de la cr�ation du profil m�tier.");

				} else {
					salle = sa.retrouver(salle);

					traiterCapacite(salle, capacite);
					traiterDescription(salle, description);
					// traiterValide(salle, valide);

					// traiterDateValidation(salle, dateValidation);

					// traiterCommentaires(salle, commentaires);

					traiterAdresse(salle, adresse);

					sa.modifier(salle);
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

		return salle;

	}

	public Salle associerComite(ArrayList<String> liste) {
		Salle salle = new Salle(liste.get(0));
		salle = sa.retrouver(salle);
		Localisation profil = new Localisation(liste.get(1));
		profil = p.retrouver(profil);

		// salle = m.associerComite(salle, profil);

		return salle;

	}

	public Salle supprimerComite(HttpServletRequest request) {
		Salle salle = retrouverSalle(request);
		ComiteForm profilForm = new ComiteForm(p);
		Localisation profil = profilForm.consulterComite(request);
		// salle = m.supprimerComite(salle, profil);
		return salle;
	}

}
