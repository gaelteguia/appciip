package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Station;
import beans.Collaborateur;
import beans.Fournisseur;
import beans.Salle;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.BeamerManager;
import modele.StationManager;
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
public class StationForm extends Form {

	public StationForm(CollaborateurManager u, ApplicationManager a, BeamerManager b, StationManager st,
			SeanceManager d, AdresseManager l, FonctionManager m, EvenementManager n, ComiteManager p, ServiceManager s,
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
		this.st = st;
	}

	public StationForm(StationManager st) {
		this.st = st;
	}

	public Station consulterStation(HttpServletRequest request) {
		Station station = new Station();
		/* R�cup�ration de l'id de l'station choisie */
		String idStation = getValeurChamp(request, CHAMP_LISTE_BEAMERS);
		Long id = null;
		try {
			id = Long.parseLong(idStation);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_BEAMER, "Station inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet station correspondant dans la session
			 */
			HttpSession session = request.getSession();

			station = ((Map<Long, Station>) session.getAttribute(SESSION_BEAMERS)).get(id);
		}

		return station;
	}

	public Station retrouverStation(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String id = getValeurChamp(request, CHAMP_ID);

		Station station = new Station();

		traiterId(station, id);

		station.setNumero(numero);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				station = st.retrouver(station);

				setResultat("Succ�s de la recherche de l'station.");
				return station;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier le numero de l'station.");
			setResultat("�chec de la recherche.");

		}

		return station;
	}

	public Station creerStation(HttpServletRequest request) {
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
		 * sp�cifiques � une station.
		 */

		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String prix = getValeurChamp(request, CHAMP_DESCRIPTION);

		Station station = new Station();

		try {

			traiterCollaborateur(station, collaborateur);

			traiterNumero(station, numero);
			traiterPrix(station, prix);

			if (getErreurs().isEmpty()) {

				if (st.retrouver(station) == null) {
					station = st.creer(station);
					setResultat("Succ�s de la cr�ation de l'station.");
				} else {
					setResultat("Station d�j� existant.");
				}
			} else {
				setResultat("�chec de la cr�ation de l'station.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de l'station : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}
		return station;
	}

	public Station modifierStation(HttpServletRequest request) {
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
		 * sp�cifiques � une station.
		 */

		String numero = getValeurChamp(request, CHAMP_NOM_BEAMER);
		String prix = getValeurChamp(request, CHAMP_DESCRIPTION);

		String id = getValeurChamp(request, CHAMP_ID);

		Station station = new Station();
		if (!id.trim().isEmpty()) {
			traiterId(station, id);
		}

		try {

			traiterCollaborateur(station, collaborateur);

			traiterNumero(station, numero);
			traiterPrix(station, prix);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					st.modifier(station);
					setResultat("Succ�s de la modification de l'station.");
				} else {
					setResultat("�chec de la modification de l'station.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modifiation.");
			setResultat(
					"�chec de la modification de l'station : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return station;

	}

	public Station creerStation(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String numero = tokens[0];
		String prix = tokens[1];
		String marque = tokens[2];
		String modele = tokens[3];
		String os = tokens[4];
		String amortissement = tokens[5];
		String type = tokens[6];
		String dateAchat = tokens[7];
		String datePeremption = tokens[8];

		Utilisateur collaborateur = new Utilisateur(tokens[9]);
		Fournisseur fournisseur = new Fournisseur(tokens[10]);
		Salle salle = new Salle(tokens[11]);

		collaborateur = u.retrouver(collaborateur);
		fournisseur = f.retrouver(fournisseur);

		salle = sa.retrouver(salle);

		Station station = new Station();

		if (!numero.trim().isEmpty()) {
			try {
				traiterNumero(station, numero);
				traiterPrix(station, prix);
				traiterMarque(station, marque);
				traiterModele(station, modele);
				traiterOs(station, os);
				traiterAmortissement(station, amortissement);
				traiterType(station, type);
				traiterDateAchat(station, dateAchat);
				traiterDatePeremption(station, datePeremption);

				traiterCollaborateur(station, collaborateur);

				traiterFournisseur(station, fournisseur);

				traiterSalle(station, salle);

				if (st.retrouver(station) == null) {
					station = st.creer(station);
					setResultat("Succ�s de la cr�ation de l'station.");
				} else {

					station = st.retrouver(station);
					traiterPrix(station, prix);
					traiterMarque(station, marque);
					traiterModele(station, modele);
					traiterOs(station, os);
					traiterAmortissement(station, amortissement);
					traiterType(station, type);
					traiterDateAchat(station, dateAchat);
					traiterDatePeremption(station, datePeremption);

					traiterCollaborateur(station, collaborateur);

					traiterFournisseur(station, fournisseur);

					traiterSalle(station, salle);

					st.modifier(station);
					setResultat("Station d�j� existante.");
				}

			} catch (DatabaseException e) {
				setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
				setResultat(
						"�chec de la cr�ation de l'station : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la cr�ation de l'station.");
		}

		return station;

	}
}
