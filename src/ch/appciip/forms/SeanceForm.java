package forms;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import modele.ApplicationManager;
import modele.DatabaseException;
import modele.SeanceManager;
import modele.AdresseManager;
import modele.FonctionManager;
import modele.EvenementManager;
import modele.ComiteManager;
import modele.ServiceManager;
import modele.CollaborateurManager;
import beans.Adresse;
import beans.Collaborateur;
import beans.Comite;
import beans.Seance;

@SuppressWarnings("unchecked")
public class SeanceForm extends Form {

	public SeanceForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
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

	public SeanceForm(SeanceManager d) {
		this.d = d;
	}

	public Seance retrouverSeance(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String libelle = getValeurChamp(request, CHAMP_LIBELLE);
		String id = getValeurChamp(request, CHAMP_ID);

		Seance seance = new Seance();

		traiterId(seance, id);

		seance.setLibelle(libelle);

		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				seance = d.retrouver(seance);

				setResultat("Succ�s de la recherche du seance d'acc�s.");
				return seance;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre libelle.");
			setResultat("�chec de la recherche.");

		}

		return seance;
	}

	public Seance consulterSeance(HttpServletRequest request) {
		Seance seance = new Seance();
		/* R�cup�ration de l'id du seance d'acc�s choisi */
		String idSeance = getValeurChamp(request, CHAMP_LISTE_SEANCES);
		Long id = null;
		try {
			id = Long.parseLong(idSeance);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_SEANCE, "Seance inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/* R�cup�ration de l'objet seance correspondant dans la session */
			HttpSession session = request.getSession();
			seance = ((Map<Long, Seance>) session.getAttribute(SESSION_SEANCES)).get(id);
		}

		return seance;
	}

	public Seance creerSeance(HttpServletRequest request) {
		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un seance d'acc�s.
		 */

		String libelle = getValeurChamp(request, CHAMP_LIBELLE);

		Seance seance = new Seance();

		try {
			traiterLibelle(seance, libelle);

			if (getErreurs().isEmpty()) {

				if (d.retrouver(seance) == null) {
					seance = d.creer(seance);
					setResultat("Succ�s de la cr�ation du seance d'acc�s.");
				} else {
					setResultat("Seance d'acc�s d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation du seance d'acc�s.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation du seance d'acc�s : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return seance;
	}

	public Seance modifierSeance(HttpServletRequest request) {
		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � un seance d'acc�s.
		 */

		String libelle = getValeurChamp(request, CHAMP_LIBELLE);

		String id = getValeurChamp(request, CHAMP_ID);

		Seance seance = new Seance();
		if (!id.trim().isEmpty())
			traiterId(seance, id);

		try {
			traiterLibelle(seance, libelle);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					d.modifier(seance);
					setResultat("Succ�s de la modification du seance d'acc�s.");
				} else {
					setResultat("�chec de la modification du seance d'acc�s.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification du seance d'acc�s : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return seance;
	}

	public Seance creerSeance(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String libelle = tokens[0];
		String date = tokens[2];
		String description = tokens[1];
		Adresse adresse = new Adresse(tokens[3]);
		Localisation comite = new Localisation(tokens[4]);
		Utilisateur responsable = new Utilisateur(tokens[5]);
		Seance seance = new Seance();

		comite = p.retrouver(comite);
		adresse = l.retrouver(adresse);

		if (!libelle.trim().isEmpty()) {

			try {
				traiterLibelle(seance, libelle);
				traiterDate(seance, date);
				traiterDescription(seance, description);
				traiterAdresse(seance, adresse);
				traiterComite(seance, comite);
				traiterResponsable(seance, responsable);

				if (d.retrouver(seance) == null) {

					seance = d.creer(seance);
					setResultat("Succ�s de la cr�ation du seance.");
				} else {

					seance = d.retrouver(seance);
					traiterDate(seance, date);
					traiterDescription(seance, description);
					traiterAdresse(seance, adresse);
					traiterComite(seance, comite);
					traiterResponsable(seance, responsable);

					d.modifier(seance);
					setResultat("Service d�j� existant.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation du seance : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation du seance.");
		}

		return seance;

	}

}
