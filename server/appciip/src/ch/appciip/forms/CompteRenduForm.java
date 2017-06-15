package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Collaborateur;
import beans.CompteRendu;
import beans.Service;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.CompteRenduManager;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class CompteRenduForm extends Form {

	public CompteRenduForm(CompteRenduManager cr) {
		this.cr = cr;
	}

	public CompteRenduForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, CompteRenduManager cr,
			FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.cr = cr;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;

	}

	public CompteRendu retrouverCompteRendu(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String id = getValeurChamp(request, CHAMP_ID);

		CompteRendu compteRendu = new CompteRendu();

		traiterId(compteRendu, id);
		compteRendu.setTitre(titre);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				compteRendu = cr.retrouver(compteRendu);

				setResultat("Succ�s de la recherche de la compteRendu.");
				return compteRendu;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre login.");
			setResultat("�chec de la recherche.");
		}

		return compteRendu;
	}

	public CompteRendu consulterCompteRendu(HttpServletRequest request) {
		CompteRendu compteRendu = new CompteRendu();
		/* R�cup�ration de l'id de la compteRendu choisie */
		String idCompteRendu = getValeurChamp(request, CHAMP_LISTE_DIRECTIVES);
		Long id = null;
		try {
			id = Long.parseLong(idCompteRendu);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_DIRECTIVE, "CompteRendu inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet compteRendu correspondant dans la session
			 */
			HttpSession session = request.getSession();
			compteRendu = ((Map<Long, CompteRendu>) session.getAttribute(SESSION_DIRECTIVES)).get(id);
		}

		return compteRendu;
	}

	public CompteRendu creerCompteRendu(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une compteRendu.
		 */

		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String type = getValeurChamp(request, CHAMP_THEME);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		String lien = getValeurChamp(request, CHAMP_LIEN);

		CompteRendu compteRendu = new CompteRendu();

		try {
			traiterTitre(compteRendu, titre);

			traiterType(compteRendu, type);
			traiterDescription(compteRendu, description);
			traiterLien(compteRendu, lien);

			if (getErreurs().isEmpty()) {

				if (cr.retrouver(compteRendu) == null) {
					compteRendu = cr.creer(compteRendu);
					setResultat("Succ�s de la cr�ation de la compteRendu.");
				} else {
					setResultat("CompteRendu d�j� existante.");
				}
			} else {
				setResultat("�chec de la cr�ation de la compteRendu.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de la compteRendu : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return compteRendu;
	}

	public CompteRendu modifierCompteRendu(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une compteRendu.
		 */

		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String type = getValeurChamp(request, CHAMP_THEME);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		String lien = getValeurChamp(request, CHAMP_LIEN);

		String id = getValeurChamp(request, CHAMP_ID);

		CompteRendu compteRendu = new CompteRendu();
		if (!id.trim().isEmpty())
			traiterId(compteRendu, id);

		try {
			traiterTitre(compteRendu, titre);

			traiterType(compteRendu, type);
			traiterDescription(compteRendu, description);
			traiterLien(compteRendu, lien);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					cr.modifier(compteRendu);
					setResultat("Succ�s de la modification de la compteRendu.");
				} else {
					setResultat("�chec de la modification de la compteRendu.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification de la compteRendu : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return compteRendu;
	}

	public CompteRendu creerCompteRendu(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String titre = tokens[0];
		String type = tokens[1];
		String description = tokens[2];
		String lien = tokens[3];
		String date = tokens[4];

		Utilisateur collaborateur = new Utilisateur(tokens[5]);
		collaborateur = u.retrouver(collaborateur);

		Service service = new Service(tokens[6]);
		service = s.retrouver(service);

		CompteRendu compteRendu = new CompteRendu();

		if (!titre.trim().isEmpty()) {
			try {

				traiterTitre(compteRendu, titre);
				traiterType(compteRendu, type);

				traiterLien(compteRendu, lien);
				traiterDate(compteRendu, date);
				traiterDescription(compteRendu, description);
				traiterCollaborateur(compteRendu, collaborateur);
				traiterService(compteRendu, service);

				if (cr.retrouver(compteRendu) == null) {
					
					compteRendu = cr.creer(compteRendu);
					setResultat("Succ�s de la creation de la compteRendu.");

				} else {

					compteRendu = cr.retrouver(compteRendu);
					traiterType(compteRendu, type);

					traiterLien(compteRendu, lien);
					traiterDate(compteRendu, date);
					traiterDescription(compteRendu, description);
					traiterCollaborateur(compteRendu, collaborateur);
					traiterService(compteRendu, service);

					cr.modifier(compteRendu);
					setResultat("CompteRendu d�j� existante.");

				}
			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la creation.");
				setResultat(
						"�chec de la creation de la compteRendu : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la creation de la compteRendu.");

		}
		return compteRendu;

	}

}
