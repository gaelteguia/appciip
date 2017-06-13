package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Breve;
import beans.Service;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.BreveManager;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class BreveForm extends Form {

	public BreveForm(BreveManager br) {
		this.br = br;
	}

	public BreveForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, BreveManager br, FonctionManager m,
			EvenementManager n, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.br = br;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;

	}

	public Breve retrouverBreve(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String id = getValeurChamp(request, CHAMP_ID);

		Breve breve = new Breve();

		traiterId(breve, id);
		breve.setTitre(titre);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				breve = br.retrouver(breve);

				setResultat("Succ�s de la recherche de la breve.");
				return breve;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre login.");
			setResultat("�chec de la recherche.");
		}

		return breve;
	}

	public Breve consulterBreve(HttpServletRequest request) {
		Breve breve = new Breve();
		/* R�cup�ration de l'id de la breve choisie */
		String idBreve = getValeurChamp(request, CHAMP_LISTE_DIRECTIVES);
		Long id = null;
		try {
			id = Long.parseLong(idBreve);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_DIRECTIVE, "Breve inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet breve correspondant dans la session
			 */
			HttpSession session = request.getSession();
			breve = ((Map<Long, Breve>) session.getAttribute(SESSION_DIRECTIVES)).get(id);
		}

		return breve;
	}

	public Breve creerBreve(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une breve.
		 */

		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String type = getValeurChamp(request, CHAMP_THEME);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		String lien = getValeurChamp(request, CHAMP_LIEN);

		Breve breve = new Breve();

		try {
			traiterTitre(breve, titre);

			traiterType(breve, type);
			traiterDescription(breve, description);
			traiterLien(breve, lien);

			if (getErreurs().isEmpty()) {

				if (br.retrouver(breve) == null) {
					breve = br.creer(breve);
					setResultat("Succ�s de la cr�ation de la breve.");
				} else {
					setResultat("Breve d�j� existante.");
				}
			} else {
				setResultat("�chec de la cr�ation de la breve.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de la breve : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return breve;
	}

	public Breve modifierBreve(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une breve.
		 */

		String titre = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String type = getValeurChamp(request, CHAMP_THEME);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		String lien = getValeurChamp(request, CHAMP_LIEN);

		String id = getValeurChamp(request, CHAMP_ID);

		Breve breve = new Breve();
		if (!id.trim().isEmpty())
			traiterId(breve, id);

		try {
			traiterTitre(breve, titre);

			traiterType(breve, type);
			traiterDescription(breve, description);
			traiterLien(breve, lien);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					br.modifier(breve);
					setResultat("Succ�s de la modification de la breve.");
				} else {
					setResultat("�chec de la modification de la breve.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification de la breve : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return breve;
	}

	public Breve creerBreve(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String titre = tokens[0];
		String type = tokens[1];
		String description = tokens[2];
		String lien = tokens[3];
		String date = tokens[4];
		Service service = new Service(tokens[5]);
		service = s.retrouver(service);

		Breve breve = new Breve();

		if (!titre.trim().isEmpty()) {
			try {

				traiterTitre(breve, titre);
				traiterType(breve, type);

				traiterLien(breve, lien);
				traiterDate(breve, date);
				traiterDescription(breve, description);
				traiterService(breve, service);

				if (br.retrouver(breve) == null) {
					breve = br.creer(breve);
					setResultat("Succ�s de la creation de la breve.");

				} else {

					breve = br.retrouver(breve);
					traiterType(breve, type);

					traiterLien(breve, lien);
					traiterDate(breve, date);
					traiterDescription(breve, description);
					traiterService(breve, service);

					br.modifier(breve);
					setResultat("Breve d�j� existante.");

				}
			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la creation.");
				setResultat(
						"�chec de la creation de la breve : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la creation de la breve.");

		}
		return breve;

	}

}
