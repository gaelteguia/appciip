package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Partenaire;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.PartenaireManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class PartenaireForm extends Form {

	public PartenaireForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
			FonctionManager m, EvenementManager n, PartenaireManager pa, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.l = l;
		this.m = m;
		this.n = n;
		this.pa = pa;
		this.s = s;
	}

	public PartenaireForm(PartenaireManager pa) {
		this.pa = pa;
	}

	public Partenaire consulterPartenaire(HttpServletRequest request) {
		Partenaire partenaire = new Partenaire();
		/* R�cup�ration de l'id du partenaire choisi */
		String idPartenaire = getValeurChamp(request, CHAMP_LISTE_COMITES);
		Long id = null;
		try {
			id = Long.parseLong(idPartenaire);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_COMITE,
					"Partenaire d'acc�s inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet partenaire correspondant dans la session
			 */
			HttpSession session = request.getSession();
			partenaire = ((Map<Long, Partenaire>) session.getAttribute(SESSION_COMITES)).get(id);
		}

		return partenaire;
	}

	public Partenaire creerPartenaire(HttpServletRequest request) {

		/*
		 * Si l'utilisateur choisit une parent d�j� existante, pas de validation
		 * � effectuer
		 */
		String choixNouvellePartenaire = getValeurChamp(request, CHAMP_CHOIX_COMITE);
		if (ANCIEN_COMITE.equals(choixNouvellePartenaire)) {
			/* R�cup�ration de l'id de l'parent choisie */
			String idAnciennePartenaire = getValeurChamp(request, CHAMP_LISTE_COMITES);
			Long id = null;
			try {
				id = Long.parseLong(idAnciennePartenaire);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_COMITE, "Partenaire inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet parent correspondant dans la session
			 */
			HttpSession session = request.getSession();

		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'une parent existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Partenaire cr��.
			 */
			PartenaireForm parentForm = new PartenaireForm(pa);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier PartenaireForm dans
			 * la map d'erreurs courante, actuellement vide.
			 */
			erreurs = parentForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un partenaire.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM);

		Partenaire partenaire = new Partenaire();

		try {
			//

			//

			if (getErreurs().isEmpty()) {

				if (pa.retrouver(partenaire) == null) {
					partenaire = pa.creer(partenaire);
					setResultat("Succ�s de la cr�ation du partenaire d'acc�s.");
				} else {
					setResultat("Partenaire d'acc�s d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation du partenaire d'acc�s.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation du partenaire d'acc�s : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return partenaire;
	}

	public Partenaire modifierPartenaire(HttpServletRequest request) {

		/*
		 * Si l'utilisateur choisit une parent d�j� existante, pas de validation
		 * � effectuer
		 */
		String choixNouvellePartenaire = getValeurChamp(request, CHAMP_CHOIX_COMITE);
		if (ANCIEN_COMITE.equals(choixNouvellePartenaire)) {
			/* R�cup�ration de l'id d'parent choisie */
			String idAnciennePartenaire = getValeurChamp(request, CHAMP_LISTE_COMITES);
			Long id = null;
			try {
				id = Long.parseLong(idAnciennePartenaire);
			} catch (NumberFormatException e) {
				setErreur(CHAMP_CHOIX_COMITE, "Partenaire inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
				id = 0L;
			}
			/*
			 * R�cup�ration de l'objet parent correspondant dans la session
			 */
			HttpSession session = request.getSession();

		} else {
			/*
			 * Sinon on garde l'ancien mode, pour la validation des champs.
			 * 
			 * L'objet m�tier pour valider la cr�ation d'une parent existe d�j�,
			 * il est donc d�conseill� de dupliquer ici son contenu ! � la
			 * place, il suffit de passer la requ�te courante � l'objet m�tier
			 * existant et de r�cup�rer l'objet Partenaire cr��.
			 */
			PartenaireForm parentForm = new PartenaireForm(pa);

			/*
			 * Et tr�s important, il ne faut pas oublier de r�cup�rer le contenu
			 * de la map d'erreur cr��e par l'objet m�tier PartenaireForm dans
			 * la map d'erreurs courante, actuellement vide.
			 */
			erreurs = parentForm.getErreurs();
		}

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un partenaire.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM);
		String id = getValeurChamp(request, CHAMP_ID);

		Partenaire partenaire = new Partenaire();
		if (!id.trim().isEmpty())
			traiterId(partenaire, id);

		try {

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					pa.modifier(partenaire);
					setResultat("Succ�s de la modification du partenaire d'acc�s.");
				} else {
					setResultat("�chec de la modification du partenaire d'acc�s.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification du partenaire d'acc�s : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return partenaire;
	}

	public Partenaire retrouverPartenaire(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM);
		String id = getValeurChamp(request, CHAMP_ID);

		Partenaire partenaire = new Partenaire();
		traiterId(partenaire, id);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				partenaire = pa.retrouver(partenaire);

				setResultat("Succ�s de la recherche du partenaire d'acc�s.");
				return partenaire;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre nom.");
			setResultat("�chec de la recherche.");
		}

		return partenaire;
	}

	public Partenaire creerPartenaire(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}

		String nom = tokens[0];
		String type = tokens[1];
		String description = tokens[2];
		String website = tokens[3];

		Partenaire partenaire = new Partenaire();

		if (!nom.trim().isEmpty()) {
			try {

				traiterNom(partenaire, nom);
				traiterType(partenaire, type);
				traiterDescription(partenaire, description);
				traiterWebsite(partenaire, website);

				if (pa.retrouver(partenaire) == null) {
					partenaire = pa.creer(partenaire);
					setResultat("Succ�s de la cr�ation.");

				} else {
					partenaire = pa.retrouver(partenaire);

					traiterNom(partenaire, nom);
					traiterType(partenaire, type);
					traiterDescription(partenaire, description);
					traiterWebsite(partenaire, website);

					pa.modifier(partenaire);
					setResultat("Partenaire d�j� existant.");
				}
			}

			catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation du partenaire d'acc�s : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation du partenaire d'acc�s.");

		}

		return partenaire;

	}

}
