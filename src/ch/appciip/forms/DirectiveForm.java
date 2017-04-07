package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Directive;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.DirectiveManager;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class DirectiveForm extends Form {

	public DirectiveForm(DirectiveManager dir) {
		this.dir = dir;
	}

	public DirectiveForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, DirectiveManager dir,
			FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.dir = dir;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;

	}

	public Directive retrouverDirective(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String id = getValeurChamp(request, CHAMP_ID);

		Directive directive = new Directive();

		traiterId(directive, id);
		directive.setTitre(titre);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				directive = dir.retrouver(directive);

				setResultat("Succ�s de la recherche de la directive.");
				return directive;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre login.");
			setResultat("�chec de la recherche.");
		}

		return directive;
	}

	public Directive consulterDirective(HttpServletRequest request) {
		Directive directive = new Directive();
		/* R�cup�ration de l'id de la directive choisie */
		String idDirective = getValeurChamp(request, CHAMP_LISTE_DIRECTIVES);
		Long id = null;
		try {
			id = Long.parseLong(idDirective);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_DIRECTIVE, "Directive inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet directive correspondant dans la session
			 */
			HttpSession session = request.getSession();
			directive = ((Map<Long, Directive>) session.getAttribute(SESSION_DIRECTIVES)).get(id);
		}

		return directive;
	}

	public Directive creerDirective(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une directive.
		 */

		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String theme = getValeurChamp(request, CHAMP_THEME);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		String lien = getValeurChamp(request, CHAMP_LIEN);

		Directive directive = new Directive();

		try {
			traiterTitre(directive, titre);

			traiterTheme(directive, theme);
			traiterDescription(directive, description);
			traiterLien(directive, lien);

			if (getErreurs().isEmpty()) {

				if (dir.retrouver(directive) == null) {
					directive = dir.creer(directive);
					setResultat("Succ�s de la cr�ation de la directive.");
				} else {
					setResultat("Directive d�j� existante.");
				}
			} else {
				setResultat("�chec de la cr�ation de la directive.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de la directive : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return directive;
	}

	public Directive modifierDirective(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une directive.
		 */

		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String theme = getValeurChamp(request, CHAMP_THEME);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		String lien = getValeurChamp(request, CHAMP_LIEN);

		String id = getValeurChamp(request, CHAMP_ID);

		Directive directive = new Directive();
		if (!id.trim().isEmpty())
			traiterId(directive, id);

		try {
			traiterTitre(directive, titre);

			traiterTheme(directive, theme);
			traiterDescription(directive, description);
			traiterLien(directive, lien);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					dir.modifier(directive);
					setResultat("Succ�s de la modification de la directive.");
				} else {
					setResultat("�chec de la modification de la directive.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification de la directive : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return directive;
	}

	public Directive creerDirective(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String titre = tokens[0];
		String theme = tokens[1];
		String description = tokens[2];
		String lien = tokens[3];

		Directive directive = new Directive();

		if (!titre.trim().isEmpty()) {
			try {

				traiterTitre(directive, titre);
				traiterTheme(directive, theme);

				traiterLien(directive, lien);

				traiterDescription(directive, description);

				if (dir.retrouver(directive) == null) {
					directive = dir.creer(directive);
					setResultat("Succ�s de la creation de la directive.");

				} else {

					directive = dir.retrouver(directive);
					traiterTheme(directive, theme);

					traiterLien(directive, lien);

					traiterDescription(directive, description);

					dir.modifier(directive);
					setResultat("Directive d�j� existante.");

				}
			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la creation.");
				setResultat(
						"�chec de la creation de la directive : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la creation de la directive.");

		}
		return directive;

	}

}
