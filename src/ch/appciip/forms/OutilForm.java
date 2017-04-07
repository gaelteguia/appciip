package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Outil;
import beans.Service;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.OutilManager;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class OutilForm extends Form {

	public OutilForm(OutilManager o) {
		this.o = o;
	}

	public OutilForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, OutilManager o, FonctionManager m,
			EvenementManager n, ComiteManager p, ServiceManager s) {
		this.u = u;
		this.a = a;
		this.d = d;
		this.o = o;
		this.m = m;
		this.n = n;
		this.p = p;
		this.s = s;

	}

	public Outil retrouverOutil(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String id = getValeurChamp(request, CHAMP_ID);

		Outil outil = new Outil();

		traiterId(outil, id);
		outil.setNom(nom);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				outil = o.retrouver(outil);

				setResultat("Succ�s de la recherche de la outil.");
				return outil;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre login.");
			setResultat("�chec de la recherche.");
		}

		return outil;
	}

	public Outil consulterOutil(HttpServletRequest request) {
		Outil outil = new Outil();
		/* R�cup�ration de l'id de la outil choisie */
		String idOutil = getValeurChamp(request, CHAMP_LISTE_DIRECTIVES);
		Long id = null;
		try {
			id = Long.parseLong(idOutil);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_DIRECTIVE, "Outil inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet outil correspondant dans la session
			 */
			HttpSession session = request.getSession();
			outil = ((Map<Long, Outil>) session.getAttribute(SESSION_DIRECTIVES)).get(id);
		}

		return outil;
	}

	public Outil creerOutil(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une outil.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String type = getValeurChamp(request, CHAMP_THEME);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		String lien = getValeurChamp(request, CHAMP_LIEN);

		Outil outil = new Outil();

		try {
			traiterNom(outil, nom);

			traiterType(outil, type);
			traiterDescription(outil, description);
			traiterLien(outil, lien);

			if (getErreurs().isEmpty()) {

				if (o.retrouver(outil) == null) {
					outil = o.creer(outil);
					setResultat("Succ�s de la cr�ation de la outil.");
				} else {
					setResultat("Outil d�j� existante.");
				}
			} else {
				setResultat("�chec de la cr�ation de la outil.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de la outil : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return outil;
	}

	public Outil modifierOutil(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une outil.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_DIRECTIVE);
		String type = getValeurChamp(request, CHAMP_THEME);
		String description = getValeurChamp(request, CHAMP_DESCRIPTION);
		String lien = getValeurChamp(request, CHAMP_LIEN);

		String id = getValeurChamp(request, CHAMP_ID);

		Outil outil = new Outil();
		if (!id.trim().isEmpty())
			traiterId(outil, id);

		try {
			traiterNom(outil, nom);

			traiterType(outil, type);
			traiterDescription(outil, description);
			traiterLien(outil, lien);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					o.modifier(outil);
					setResultat("Succ�s de la modification de la outil.");
				} else {
					setResultat("�chec de la modification de la outil.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification de la outil : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return outil;
	}

	public Outil creerOutil(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];
		String type = tokens[1];
		String description = tokens[2];
		String lien = tokens[3];

		Service service = new Service(tokens[4]);
		service = s.retrouver(service);

		Outil outil = new Outil();

		if (!nom.trim().isEmpty()) {
			try {

				traiterNom(outil, nom);
				traiterType(outil, type);

				traiterLien(outil, lien);

				traiterDescription(outil, description);
				traiterService(outil, service);

				if (o.retrouver(outil) == null) {
					outil = o.creer(outil);
					setResultat("Succ�s de la creation de la outil.");

				} else {

					outil = o.retrouver(outil);
					traiterType(outil, type);

					traiterLien(outil, lien);

					traiterDescription(outil, description);
					traiterService(outil, service);

					o.modifier(outil);
					setResultat("Outil d�j� existante.");

				}
			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la creation.");
				setResultat(
						"�chec de la creation de la outil : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la creation de la outil.");

		}
		return outil;

	}

}
