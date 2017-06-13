package forms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Adresse;
import beans.Service;
import modele.AdresseManager;
import modele.ApplicationManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.DatabaseException;
import modele.EvenementManager;
import modele.FonctionManager;
import modele.SeanceManager;
import modele.ServiceManager;

@SuppressWarnings("unchecked")
public class AdresseForm extends Form {

	public AdresseForm(AdresseManager l) {
		this.l = l;
	}

	public AdresseForm(CollaborateurManager u, ApplicationManager a, SeanceManager d, AdresseManager l,
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

	public Adresse retrouverAdresse(HttpServletRequest request) {
		/* R�cup�ration des champs du formulaire */
		String nom = getValeurChamp(request, CHAMP_NOM_ADRESSE);
		String id = getValeurChamp(request, CHAMP_ID);

		Adresse adresse = new Adresse();

		traiterId(adresse, id);
		adresse.setNom(nom);
		try {
			/* Initialisation du r�sultat global de la validation. */
			if (getErreurs().isEmpty()) {
				adresse = l.retrouver(adresse);

				setResultat("Succ�s de la recherche de la adresse.");
				return adresse;
			} else

				setResultat("�chec de la recherche. Informations invalides.");
		} catch (DatabaseException e) {
			setErreur(CHAMP_ID, "Erreur lors de la recherche. V�rifier votre login.");
			setResultat("�chec de la recherche.");
		}

		return adresse;
	}

	public Adresse associerService(HttpServletRequest request) {
		/* R�cup�ration de l'id de la adresse choisie */
		String idAdresse = getValeurChamp(request, CHAMP_LISTE_ADRESSES);
		Long idLoc = null;
		String idService = getValeurChamp(request, CHAMP_LISTE_SERVICES);
		Long idSer = null;
		try {
			idLoc = Long.parseLong(idAdresse);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_ADRESSE, "Adresse inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			idLoc = 0L;
		}
		try {
			idSer = Long.parseLong(idService);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_SERVICE, "Service inconnu, merci d'utiliser le formulaire pr�vu � cet effet.");
			idSer = 0L;
		}
		/* R�cup�ration de l'objet adresse correspondant dans la session */
		HttpSession session = request.getSession();
		Adresse adresse = ((Map<Long, Adresse>) session.getAttribute(SESSION_ADRESSES)).get(idLoc);
		Service service = ((Map<Long, Service>) session.getAttribute(SESSION_SERVICES)).get(idSer);
		try {

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					adresse = l.associerService(adresse, service);
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
		return adresse;
	}

	public Adresse supprimerService(HttpServletRequest request) {
		Adresse adresse = retrouverAdresse(request);

		ServiceForm serviceForm = new ServiceForm(s);
		Service service = serviceForm.consulterService(request);
		adresse = l.supprimerService(adresse, service);
		return adresse;
	}

	public Adresse consulterAdresse(HttpServletRequest request) {
		Adresse adresse = new Adresse();
		/* R�cup�ration de l'id de la adresse choisie */
		String idAdresse = getValeurChamp(request, CHAMP_LISTE_ADRESSES);
		Long id = null;
		try {
			id = Long.parseLong(idAdresse);
		} catch (NumberFormatException e) {
			setErreur(CHAMP_CHOIX_ADRESSE, "Adresse inconnue, merci d'utiliser le formulaire pr�vu � cet effet.");
			id = 0L;
		}
		if (id != 0L) {
			/*
			 * R�cup�ration de l'objet adresse correspondant dans la session
			 */
			HttpSession session = request.getSession();
			adresse = ((Map<Long, Adresse>) session.getAttribute(SESSION_ADRESSES)).get(id);
		}

		return adresse;
	}

	public Adresse creerAdresse(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une adresse.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_ADRESSE);
		String ville = getValeurChamp(request, CHAMP_VILLE);
		String pays = getValeurChamp(request, CHAMP_PAYS);
		String rue = getValeurChamp(request, CHAMP_RUE);
		String telephone = getValeurChamp(request, CHAMP_TYPE);

		String npa = getValeurChamp(request, CHAMP_NPA);
		String noRue = getValeurChamp(request, CHAMP_NORUE);

		Adresse adresse = new Adresse();
		if (npa != null && !npa.trim().isEmpty())
			traiterNpa(adresse, Integer.parseInt(npa));
		if (noRue != null && !noRue.trim().isEmpty())
			traiterNoRue(adresse, Integer.parseInt(noRue));

		try {
			traiterNom(adresse, nom);

			traiterVille(adresse, ville);
			traiterPays(adresse, pays);
			traiterRue(adresse, rue);
			traiterTelephone(adresse, telephone);

			if (getErreurs().isEmpty()) {

				if (l.retrouver(adresse) == null) {
					adresse = l.creer(adresse);
					setResultat("Succ�s de la cr�ation de la adresse.");
				} else {
					setResultat("Adresse d�j� existante.");
				}
			} else {
				setResultat("�chec de la cr�ation de la adresse.");

			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la cr�ation.");
			setResultat(
					"�chec de la cr�ation de la adresse : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return adresse;
	}

	public Adresse modifierAdresse(HttpServletRequest request) {

		/*
		 * Ensuite, il suffit de proc�der normalement avec le reste des champs
		 * sp�cifiques � une adresse.
		 */

		String nom = getValeurChamp(request, CHAMP_NOM_ADRESSE);
		String ville = getValeurChamp(request, CHAMP_VILLE);
		String pays = getValeurChamp(request, CHAMP_PAYS);
		String rue = getValeurChamp(request, CHAMP_RUE);
		String telephone = getValeurChamp(request, CHAMP_TYPE);

		Integer npa = Integer.parseInt(getValeurChamp(request, CHAMP_NPA));
		Integer noRue = Integer.parseInt(getValeurChamp(request, CHAMP_NORUE));

		String id = getValeurChamp(request, CHAMP_ID);

		Adresse adresse = new Adresse();
		if (!id.trim().isEmpty())
			traiterId(adresse, id);

		try {
			traiterNom(adresse, nom);

			traiterVille(adresse, ville);
			traiterPays(adresse, pays);
			traiterRue(adresse, rue);
			traiterTelephone(adresse, telephone);

			traiterNpa(adresse, npa);

			traiterNoRue(adresse, noRue);

			if (getErreurs().isEmpty()) {

				if (getErreurs().isEmpty()) {
					l.modifier(adresse);
					setResultat("Succ�s de la modification de la adresse.");
				} else {
					setResultat("�chec de la modification de la adresse.");
				}
			}
		} catch (DatabaseException e) {
			setErreur("impr�vu", "Erreur impr�vue lors de la modification.");
			setResultat(
					"�chec de la modification de la adresse : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
			e.printStackTrace();
		}

		return adresse;
	}

	public Adresse creerAdresse(String[] tokens) {
		if (tokens.length < 2) {
			System.err.println("Not enough arguments received.");
			return null;
		}
		String nom = tokens[0];
		String ville = tokens[1];

		String npa = tokens[2];
		String rue = tokens[3];
		String noRue = tokens[4];
		String pays = tokens[5];

		String telephone = tokens[6];

		String fax = tokens[7];

		String canton = tokens[8];
		String type = tokens[9];
		String etage = tokens[10];

		String cp = tokens[11];

		Adresse adresse = new Adresse();

		if (!nom.trim().isEmpty()) {
			try {

				traiterNom(adresse, nom);
				traiterVille(adresse, ville);
				traiterNpa(adresse, npa);

				traiterRue(adresse, rue);
				traiterNoRue(adresse, noRue);
				traiterPays(adresse, pays);

				traiterTelephone(adresse, telephone);

				traiterFax(adresse, fax);

				traiterCanton(adresse, canton);
				traiterType(adresse, type);
				traiterEtage(adresse, etage);

				traiterCp(adresse, cp);

				if (l.retrouver(adresse) == null) {
					adresse = l.creer(adresse);
					setResultat("Succ�s de la creation de la adresse.");

				} else {

					adresse = l.retrouver(adresse);
					traiterVille(adresse, ville);
					traiterNpa(adresse, npa);

					traiterRue(adresse, rue);
					traiterNoRue(adresse, noRue);
					traiterPays(adresse, pays);

					traiterTelephone(adresse, telephone);

					traiterFax(adresse, fax);

					traiterCanton(adresse, canton);
					traiterType(adresse, type);
					traiterEtage(adresse, etage);

					traiterCp(adresse, cp);

					l.modifier(adresse);
					setResultat("Adresse d�j� existante.");

				}
			} catch (DatabaseException e) {

				setErreur("impr�vu", "Erreur impr�vue lors de la creation.");
				setResultat(
						"�chec de la creation de la adresse : une erreur impr�vue est survenue, merci de r�essayer dans quelques instants.");
				e.printStackTrace();
			}
		} else {
			setResultat("�chec de la creation de la adresse.");

		}
		return adresse;

	}

	public Adresse associerService(String[] tokens) {
		Adresse adresse = new Adresse(tokens[0]);
		adresse = l.retrouver(adresse);
		Service service = new Service(tokens[1]);
		service = s.retrouver(service);

		adresse = l.associerService(adresse, service);

		return adresse;

	}

}
