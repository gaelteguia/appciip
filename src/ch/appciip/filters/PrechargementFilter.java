package filters;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTimeComparator;

import beans.Adresse;
import beans.Annonce;
import beans.Application;
import beans.Beamer;
import beans.Breve;
import beans.Collaborateur;
import beans.Comite;
import beans.CompteRendu;
import beans.Directive;
import beans.Distribution;
import beans.Evenement;
import beans.Fonction;
import beans.Fournisseur;
import beans.Horaire;
import beans.Imprimante;
import beans.Manifestation;
import beans.Outil;
import beans.Partenaire;
import beans.Salle;
import beans.Seance;
import beans.Service;
import beans.Station;
import beans.Telephone;
import beans.Voiture;
import modele.AdresseManager;
import modele.AnnonceManager;
import modele.ApplicationManager;
import modele.BeamerManager;
import modele.BreveManager;
import modele.CollaborateurManager;
import modele.ComiteManager;
import modele.CompteRenduManager;
import modele.DirectiveManager;
import modele.DistributionManager;
import modele.EvenementManager;
import modele.Factory;
import modele.FonctionManager;
import modele.FournisseurManager;
import modele.HoraireManager;
import modele.ImprimanteManager;
import modele.ManifestationManager;
import modele.OutilManager;
import modele.PartenaireManager;
import modele.SalleManager;
import modele.SeanceManager;
import modele.ServiceManager;
import modele.StationManager;
import modele.TelephoneManager;
import modele.VoitureManager;

/**
 * Servlet Filter implementation class PrechargementFilter
 */
public class PrechargementFilter implements Serializable, Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CONF_FACTORY = "factory";
	public static final String ATT_SESSION_COLLABORATEURS = "collaborateurs";
	public static final String ATT_SESSION_ANNIVERSAIRES = "anniversaires";
	public static final String ATT_SESSION_PROCHAINS_ANNIVERSAIRES = "prochainsanniversaires";
	public static final String ATT_SESSION_APPLICATIONS = "applications";
	public static final String ATT_SESSION_BEAMERS = "beamers";
	public static final String ATT_SESSION_SEANCES = "seances";
	public static final String ATT_SESSION_ADRESSES = "adresses";
	public static final String ATT_SESSION_FONCTIONS = "fonctions";
	public static final String ATT_SESSION_EVENEMENTS = "evenements";
	public static final String ATT_SESSION_MANIFESTATIONS = "manifestations";
	public static final String ATT_SESSION_COMITES = "comites";
	public static final String ATT_SESSION_SERVICES = "services";
	public static final String ATT_SESSION_IMAGES = "images";
	public static final String ATT_SESSION_DISTRIBUTIONS = "distributions";
	public static final String ATT_SESSION_FOURNISSEURS = "fournisseurs";
	public static final String ATT_SESSION_HORAIRES = "horaires";
	public static final String ATT_SESSION_IMPRIMANTES = "imprimantes";
	public static final String ATT_SESSION_SALLES = "salles";
	public static final String ATT_SESSION_STATIONS = "stations";
	public static final String ATT_SESSION_TELEPHONES = "telephones";
	public static final String ATT_SESSION_VOITURES = "voitures";
	public static final String ATT_SESSION_DIRECTIVES = "directives";
	public static final String ATT_SESSION_OUTILS = "outils";
	public static final String ATT_SESSION_BREVES = "breves";
	public static final String ATT_SESSION_COMPTES_RENDUS = "comptesRendus";
	public static final String ATT_SESSION_PARTENAIRES = "partenaires";
	public static final String ATT_SESSION_ANNONCES = "annonces";
	public static final String ATT_SESSION_USER = "sessionCollaborateur";

	public static final String URL_REDIRECTION = "/ciip/home";

	private CollaborateurManager u;
	private ApplicationManager a;
	private SeanceManager d;
	private AdresseManager l;
	private FonctionManager m;
	private EvenementManager n;
	private ComiteManager p;
	private ServiceManager s;
	private BeamerManager b;

	private DistributionManager di;
	private FournisseurManager f;
	private HoraireManager h;
	private ImprimanteManager i;
	private SalleManager sa;
	private StationManager st;
	private TelephoneManager t;
	private VoitureManager v;

	private DirectiveManager dir;

	private OutilManager o;

	private BreveManager br;
	private CompteRenduManager cr;
	private PartenaireManager pa;

	private AnnonceManager an;
	private ManifestationManager ma;

	public void init(FilterConfig config) throws ServletException {
		/* R�cup�ration d'instances de nos Manager */
		this.u = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getCollaborateurManager();
		this.a = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getApplicationManager();
		this.d = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getSeanceManager();
		this.l = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getAdresseManager();
		this.m = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getFonctionManager();
		this.n = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getEvenementManager();
		this.p = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getComiteManager();
		this.s = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getServiceManager();
		this.b = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getBeamerManager();

		this.di = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getDistributionManager();
		this.f = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getFournisseurManager();
		this.h = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getHoraireManager();
		this.i = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getImprimanteManager();
		this.sa = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getSalleManager();
		this.st = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getStationManager();
		this.t = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getTelephoneManager();
		this.v = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getVoitureManager();

		this.dir = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getDirectiveManager();
		this.o = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getOutilManager();

		this.br = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getBreveManager();
		this.cr = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getCompteRenduManager();
		this.pa = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getPartenaireManager();
		this.an = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getAnnonceManager();
		this.ma = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getManifestationManager();
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		/* Cast de l'objet request */
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		/* Non-filtrage des ressources statiques */
		String chemin = request.getRequestURI().substring(request.getContextPath().length());
		if (chemin.startsWith("/deconnexion")) {
			chain.doFilter(request, response);
			return;
		}

		/* R�cup�ration de la session depuis la requ�te */
		HttpSession session = request.getSession();

		/*
		 * Si la map des collaborateurs n'existe pas en session, alors
		 * l'collaborateur se connecte pour la premi�re fois et nous devons
		 * pr�charger en session les infos contenues dans la BDD.
		 */

		if (session.getAttribute(ATT_SESSION_COLLABORATEURS) == null) {
			// R�cup�ration de la liste des collaborateurs existants, et
			// enregistrement en session

			List<Utilisateur> listeCollaborateurs = u.lister();
			Map<Long, Utilisateur> mapCollaborateurs = new HashMap<Long, Utilisateur>();
			for (Utilisateur u : listeCollaborateurs) {

				mapCollaborateurs.put(u.getId(), u);

			}
			Comparator<Utilisateur> valueComparator = new Comparator<Utilisateur>() {
				@Override
				public int compare(Utilisateur arg0, Utilisateur arg1) {

					int result = arg0.getNom().compareToIgnoreCase(arg1.getNom());
					if (result == 0) {
						result = arg0.getPrenom().compareToIgnoreCase(arg1.getPrenom());
					}
					return result;
				}
			};
			MapValueComparator<Long, Utilisateur> mapComparator = new MapValueComparator<Long, Utilisateur>(
					mapCollaborateurs, valueComparator);
			Map<Long, Utilisateur> sortedOnValuesMap = new TreeMap<Long, Utilisateur>(mapComparator);
			sortedOnValuesMap.putAll(mapCollaborateurs);
			mapCollaborateurs = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_COLLABORATEURS, mapCollaborateurs);
		}

		if (session.getAttribute(ATT_SESSION_ANNIVERSAIRES) == null) {
			// R�cup�ration de la liste des collaborateurs existants, et
			// enregistrement en session

			List<Utilisateur> listeAnniversaires = u.anniversaire();
			Map<Long, Utilisateur> mapAnniversaires = new HashMap<Long, Utilisateur>();
			for (Utilisateur u : listeAnniversaires) {

				mapAnniversaires.put(u.getId(), u);

			}
			Comparator<Utilisateur> valueComparator = new Comparator<Utilisateur>() {
				@Override
				public int compare(Utilisateur arg0, Utilisateur arg1) {

					int result = arg0.getPrenom().compareToIgnoreCase(arg1.getPrenom());
					if (result == 0) {
						result = arg0.getNom().compareToIgnoreCase(arg1.getNom());
					}
					return result;
				}
			};
			MapValueComparator<Long, Utilisateur> mapComparator = new MapValueComparator<Long, Utilisateur>(
					mapAnniversaires, valueComparator);
			Map<Long, Utilisateur> sortedOnValuesMap = new TreeMap<Long, Utilisateur>(mapComparator);
			sortedOnValuesMap.putAll(mapAnniversaires);
			mapAnniversaires = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_ANNIVERSAIRES, mapAnniversaires);
		}

		if (session.getAttribute(ATT_SESSION_PROCHAINS_ANNIVERSAIRES) == null) {
			// R�cup�ration de la liste des collaborateurs existants, et
			// enregistrement en session

			List<Utilisateur> listeProchainsAnniversaires = u.prochainAnniversaire();
			Map<Long, Utilisateur> mapProchainsAnniversaires = new HashMap<Long, Utilisateur>();
			for (Utilisateur u : listeProchainsAnniversaires) {

				mapProchainsAnniversaires.put(u.getId(), u);

			}
			Comparator<Utilisateur> valueComparator = new Comparator<Utilisateur>() {
				@Override
				public int compare(Utilisateur arg0, Utilisateur arg1) {

					int result = arg0.getPrenom().compareToIgnoreCase(arg1.getPrenom());
					if (result == 0) {
						result = arg0.getNom().compareToIgnoreCase(arg1.getNom());
					}
					return result;
				}
			};
			MapValueComparator<Long, Utilisateur> mapComparator = new MapValueComparator<Long, Utilisateur>(
					mapProchainsAnniversaires, valueComparator);
			Map<Long, Utilisateur> sortedOnValuesMap = new TreeMap<Long, Utilisateur>(mapComparator);
			sortedOnValuesMap.putAll(mapProchainsAnniversaires);
			mapProchainsAnniversaires = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_PROCHAINS_ANNIVERSAIRES, mapProchainsAnniversaires);
		}

		/*
		 * De m�me pour les autres maps. R�cup�ration des listes existantes, et
		 * enregistrement en session
		 */
		if (session.getAttribute(ATT_SESSION_APPLICATIONS) == null) {

			List<Application> listeApplications = a.lister();
			Map<Long, Application> mapApplications = new HashMap<Long, Application>();
			for (Application a : listeApplications) {
				mapApplications.put(a.getId(), a);
			}
			Comparator<Application> valueComparator = new Comparator<Application>() {
				@Override
				public int compare(Application arg0, Application arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Application> mapComparator = new MapValueComparator<Long, Application>(
					mapApplications, valueComparator);
			Map<Long, Application> sortedOnValuesMap = new TreeMap<Long, Application>(mapComparator);
			sortedOnValuesMap.putAll(mapApplications);
			mapApplications = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_APPLICATIONS, mapApplications);
		}

		if (session.getAttribute(ATT_SESSION_BEAMERS) == null) {

			List<Beamer> listeBeamers = b.lister();
			Map<Long, Beamer> mapBeamers = new HashMap<Long, Beamer>();
			for (Beamer a : listeBeamers) {
				mapBeamers.put(a.getId(), a);
			}
			Comparator<Beamer> valueComparator = new Comparator<Beamer>() {
				@Override
				public int compare(Beamer arg0, Beamer arg1) {

					return arg0.getNumero().compareToIgnoreCase(arg1.getNumero());
				}
			};
			MapValueComparator<Long, Beamer> mapComparator = new MapValueComparator<Long, Beamer>(mapBeamers,
					valueComparator);
			Map<Long, Beamer> sortedOnValuesMap = new TreeMap<Long, Beamer>(mapComparator);
			sortedOnValuesMap.putAll(mapBeamers);
			mapBeamers = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_BEAMERS, mapBeamers);
		}

		if (session.getAttribute(ATT_SESSION_SEANCES) == null) {

			List<Seance> listeSeances = d.lister();
			Map<Long, Seance> mapSeances = new HashMap<Long, Seance>();
			for (Seance d : listeSeances) {
				mapSeances.put(d.getId(), d);
			}

			Comparator<Seance> valueComparator = new Comparator<Seance>() {
				@Override
				public int compare(Seance arg0, Seance arg1) {
					DateTimeComparator comparator = DateTimeComparator.getInstance();
					if (comparator.compare(arg0.getDate(), arg1.getDate()) == 0) {

						return -1;
					} else {
						return 1;
					}

				}
			};
			MapValueComparator<Long, Seance> mapComparator = new MapValueComparator<Long, Seance>(mapSeances,
					valueComparator);
			Map<Long, Seance> sortedOnValuesMap = new TreeMap<Long, Seance>(mapComparator);
			sortedOnValuesMap.putAll(mapSeances);
			mapSeances = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_SEANCES, mapSeances);
		}
		if (session.getAttribute(ATT_SESSION_ADRESSES) == null) {

			List<Adresse> listeAdresses = l.lister();
			Map<Long, Adresse> mapAdresses = new HashMap<Long, Adresse>();
			for (Adresse l : listeAdresses) {
				mapAdresses.put(l.getId(), l);
			}
			Comparator<Adresse> valueComparator = new Comparator<Adresse>() {
				@Override
				public int compare(Adresse arg0, Adresse arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Adresse> mapComparator = new MapValueComparator<Long, Adresse>(mapAdresses,
					valueComparator);
			Map<Long, Adresse> sortedOnValuesMap = new TreeMap<Long, Adresse>(mapComparator);
			sortedOnValuesMap.putAll(mapAdresses);
			mapAdresses = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_ADRESSES, mapAdresses);
		}
		if (session.getAttribute(ATT_SESSION_FONCTIONS) == null) {

			List<Fonction> listeFonctions = m.lister();
			Map<Long, Fonction> mapFonctions = new HashMap<Long, Fonction>();
			for (Fonction m : listeFonctions) {
				mapFonctions.put(m.getId(), m);
			}
			Comparator<Fonction> valueComparator = new Comparator<Fonction>() {
				@Override
				public int compare(Fonction arg0, Fonction arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Fonction> mapComparator = new MapValueComparator<Long, Fonction>(mapFonctions,
					valueComparator);
			Map<Long, Fonction> sortedOnValuesMap = new TreeMap<Long, Fonction>(mapComparator);
			sortedOnValuesMap.putAll(mapFonctions);
			mapFonctions = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_FONCTIONS, mapFonctions);
		}
		if (session.getAttribute(ATT_SESSION_EVENEMENTS) == null) {

			List<Evenement> listeEvenements = n.lister();
			Map<Long, Evenement> mapEvenements = new HashMap<Long, Evenement>();
			for (Evenement n : listeEvenements) {
				mapEvenements.put(n.getId(), n);
			}
			Comparator<Evenement> valueComparator = new Comparator<Evenement>() {
				@Override
				public int compare(Evenement arg0, Evenement arg1) {

					return arg0.getDate().compareTo(arg1.getDate());
				}
			};
			MapValueComparator<Long, Evenement> mapComparator = new MapValueComparator<Long, Evenement>(mapEvenements,
					valueComparator);
			Map<Long, Evenement> sortedOnValuesMap = new TreeMap<Long, Evenement>(mapComparator);
			sortedOnValuesMap.putAll(mapEvenements);
			mapEvenements = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_EVENEMENTS, mapEvenements);
		}
		if (session.getAttribute(ATT_SESSION_COMITES) == null) {

			List<Localisation> listeComites = p.lister();
			Map<Long, Localisation> mapComites = new HashMap<Long, Localisation>();
			for (Localisation p : listeComites) {
				mapComites.put(p.getId(), p);
			}
			Comparator<Localisation> valueComparator = new Comparator<Localisation>() {
				@Override
				public int compare(Localisation arg0, Localisation arg1) {

					return arg0.getAcronyme().compareToIgnoreCase(arg1.getAcronyme());
				}
			};
			MapValueComparator<Long, Localisation> mapComparator = new MapValueComparator<Long, Localisation>(mapComites,
					valueComparator);
			Map<Long, Localisation> sortedOnValuesMap = new TreeMap<Long, Localisation>(mapComparator);
			sortedOnValuesMap.putAll(mapComites);
			mapComites = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_COMITES, mapComites);
		}
		if (session.getAttribute(ATT_SESSION_SERVICES) == null) {

			List<Service> listeServices = s.lister();
			Map<Long, Service> mapServices = new HashMap<Long, Service>();
			for (Service s : listeServices) {
				mapServices.put(s.getId(), s);
			}
			Comparator<Service> valueComparator = new Comparator<Service>() {
				@Override
				public int compare(Service arg0, Service arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Service> mapComparator = new MapValueComparator<Long, Service>(mapServices,
					valueComparator);
			Map<Long, Service> sortedOnValuesMap = new TreeMap<Long, Service>(mapComparator);
			sortedOnValuesMap.putAll(mapServices);
			mapServices = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_SERVICES, mapServices);
		}

		if (session.getAttribute(ATT_SESSION_DISTRIBUTIONS) == null) {

			List<Distribution> listeDistributions = di.lister();
			Map<Long, Distribution> mapDistributions = new HashMap<Long, Distribution>();
			for (Distribution s : listeDistributions) {
				mapDistributions.put(s.getId(), s);
			}
			Comparator<Distribution> valueComparator = new Comparator<Distribution>() {
				@Override
				public int compare(Distribution arg0, Distribution arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Distribution> mapComparator = new MapValueComparator<Long, Distribution>(
					mapDistributions, valueComparator);
			Map<Long, Distribution> sortedOnValuesMap = new TreeMap<Long, Distribution>(mapComparator);
			sortedOnValuesMap.putAll(mapDistributions);
			mapDistributions = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_DISTRIBUTIONS, mapDistributions);
		}

		if (session.getAttribute(ATT_SESSION_FOURNISSEURS) == null) {

			List<Fournisseur> listeFournisseurs = f.lister();
			Map<Long, Fournisseur> mapFournisseurs = new HashMap<Long, Fournisseur>();
			for (Fournisseur s : listeFournisseurs) {
				mapFournisseurs.put(s.getId(), s);
			}
			Comparator<Fournisseur> valueComparator = new Comparator<Fournisseur>() {
				@Override
				public int compare(Fournisseur arg0, Fournisseur arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Fournisseur> mapComparator = new MapValueComparator<Long, Fournisseur>(
					mapFournisseurs, valueComparator);
			Map<Long, Fournisseur> sortedOnValuesMap = new TreeMap<Long, Fournisseur>(mapComparator);
			sortedOnValuesMap.putAll(mapFournisseurs);
			mapFournisseurs = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_FOURNISSEURS, mapFournisseurs);
		}

		if (session.getAttribute(ATT_SESSION_HORAIRES) == null) {

			List<Horaire> listeHoraires = h.lister();
			Map<Long, Horaire> mapHoraires = new HashMap<Long, Horaire>();
			for (Horaire s : listeHoraires) {
				mapHoraires.put(s.getId(), s);
			}
			Comparator<Horaire> valueComparator = new Comparator<Horaire>() {
				@Override
				public int compare(Horaire arg0, Horaire arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Horaire> mapComparator = new MapValueComparator<Long, Horaire>(mapHoraires,
					valueComparator);
			Map<Long, Horaire> sortedOnValuesMap = new TreeMap<Long, Horaire>(mapComparator);
			sortedOnValuesMap.putAll(mapHoraires);
			mapHoraires = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_HORAIRES, mapHoraires);
		}

		if (session.getAttribute(ATT_SESSION_IMPRIMANTES) == null) {

			List<Imprimante> listeImprimantes = i.lister();
			Map<Long, Imprimante> mapImprimantes = new HashMap<Long, Imprimante>();
			for (Imprimante s : listeImprimantes) {
				mapImprimantes.put(s.getId(), s);
			}
			Comparator<Imprimante> valueComparator = new Comparator<Imprimante>() {
				@Override
				public int compare(Imprimante arg0, Imprimante arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Imprimante> mapComparator = new MapValueComparator<Long, Imprimante>(
					mapImprimantes, valueComparator);
			Map<Long, Imprimante> sortedOnValuesMap = new TreeMap<Long, Imprimante>(mapComparator);
			sortedOnValuesMap.putAll(mapImprimantes);
			mapImprimantes = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_IMPRIMANTES, mapImprimantes);
		}

		if (session.getAttribute(ATT_SESSION_SALLES) == null) {

			List<Salle> listeSalles = sa.lister();
			Map<Long, Salle> mapSalles = new HashMap<Long, Salle>();
			for (Salle s : listeSalles) {
				mapSalles.put(s.getId(), s);
			}
			Comparator<Salle> valueComparator = new Comparator<Salle>() {
				@Override
				public int compare(Salle arg0, Salle arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Salle> mapComparator = new MapValueComparator<Long, Salle>(mapSalles,
					valueComparator);
			Map<Long, Salle> sortedOnValuesMap = new TreeMap<Long, Salle>(mapComparator);
			sortedOnValuesMap.putAll(mapSalles);
			mapSalles = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_SALLES, mapSalles);
		}

		if (session.getAttribute(ATT_SESSION_STATIONS) == null) {

			List<Station> listeStations = st.lister();
			Map<Long, Station> mapStations = new HashMap<Long, Station>();
			for (Station s : listeStations) {
				mapStations.put(s.getId(), s);
			}
			Comparator<Station> valueComparator = new Comparator<Station>() {
				@Override
				public int compare(Station arg0, Station arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Station> mapComparator = new MapValueComparator<Long, Station>(mapStations,
					valueComparator);
			Map<Long, Station> sortedOnValuesMap = new TreeMap<Long, Station>(mapComparator);
			sortedOnValuesMap.putAll(mapStations);
			mapStations = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_STATIONS, mapStations);
		}

		if (session.getAttribute(ATT_SESSION_TELEPHONES) == null) {

			List<Telephone> listeTelephones = t.lister();
			Map<Long, Telephone> mapTelephones = new HashMap<Long, Telephone>();
			for (Telephone s : listeTelephones) {
				mapTelephones.put(s.getId(), s);
			}
			Comparator<Telephone> valueComparator = new Comparator<Telephone>() {
				@Override
				public int compare(Telephone arg0, Telephone arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Telephone> mapComparator = new MapValueComparator<Long, Telephone>(mapTelephones,
					valueComparator);
			Map<Long, Telephone> sortedOnValuesMap = new TreeMap<Long, Telephone>(mapComparator);
			sortedOnValuesMap.putAll(mapTelephones);
			mapTelephones = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_TELEPHONES, mapTelephones);
		}

		if (session.getAttribute(ATT_SESSION_VOITURES) == null) {

			List<Voiture> listeVoitures = v.lister();
			Map<Long, Voiture> mapVoitures = new HashMap<Long, Voiture>();
			for (Voiture s : listeVoitures) {
				mapVoitures.put(s.getId(), s);
			}
			Comparator<Voiture> valueComparator = new Comparator<Voiture>() {
				@Override
				public int compare(Voiture arg0, Voiture arg1) {

					return arg0.getNoPlaques().compareToIgnoreCase(arg1.getNoPlaques());
				}
			};
			MapValueComparator<Long, Voiture> mapComparator = new MapValueComparator<Long, Voiture>(mapVoitures,
					valueComparator);
			Map<Long, Voiture> sortedOnValuesMap = new TreeMap<Long, Voiture>(mapComparator);
			sortedOnValuesMap.putAll(mapVoitures);
			mapVoitures = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_VOITURES, mapVoitures);
		}

		if (session.getAttribute(ATT_SESSION_DIRECTIVES) == null) {

			List<Directive> listeDirectives = dir.lister();
			Map<Long, Directive> mapDirectives = new HashMap<Long, Directive>();
			for (Directive s : listeDirectives) {
				mapDirectives.put(s.getId(), s);
			}
			Comparator<Directive> valueComparator = new Comparator<Directive>() {
				@Override
				public int compare(Directive arg0, Directive arg1) {

					if (arg0.getTheme().compareToIgnoreCase(arg1.getTheme()) == 0) {
						return -1;
					} else {
						return 1;
					}

				}
			};
			MapValueComparator<Long, Directive> mapComparator = new MapValueComparator<Long, Directive>(mapDirectives,
					valueComparator);
			Map<Long, Directive> sortedOnValuesMap = new TreeMap<Long, Directive>(mapComparator);
			sortedOnValuesMap.putAll(mapDirectives);
			mapDirectives = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_DIRECTIVES, mapDirectives);
		}

		if (session.getAttribute(ATT_SESSION_OUTILS) == null) {

			List<Outil> listeOutils = o.lister();
			Map<Long, Outil> mapOutils = new HashMap<Long, Outil>();
			for (Outil s : listeOutils) {
				mapOutils.put(s.getId(), s);
			}
			Comparator<Outil> valueComparator = new Comparator<Outil>() {
				@Override
				public int compare(Outil arg0, Outil arg1) {

					return arg0.getNom().compareToIgnoreCase(arg1.getNom());
				}
			};
			MapValueComparator<Long, Outil> mapComparator = new MapValueComparator<Long, Outil>(mapOutils,
					valueComparator);
			Map<Long, Outil> sortedOnValuesMap = new TreeMap<Long, Outil>(mapComparator);
			sortedOnValuesMap.putAll(mapOutils);
			mapOutils = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_OUTILS, mapOutils);
		}

		if (session.getAttribute(ATT_SESSION_BREVES) == null) {

			List<Breve> listeBreves = br.lister();
			Map<Long, Breve> mapBreves = new HashMap<Long, Breve>();
			for (Breve s : listeBreves) {
				mapBreves.put(s.getId(), s);
			}
			Comparator<Breve> valueComparator = new Comparator<Breve>() {
				@Override
				public int compare(Breve arg0, Breve arg1) {

					return arg0.getTitre().compareToIgnoreCase(arg1.getTitre());
				}
			};
			MapValueComparator<Long, Breve> mapComparator = new MapValueComparator<Long, Breve>(mapBreves,
					valueComparator);
			Map<Long, Breve> sortedOnValuesMap = new TreeMap<Long, Breve>(mapComparator);
			sortedOnValuesMap.putAll(mapBreves);
			mapBreves = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_BREVES, mapBreves);
		}

		if (session.getAttribute(ATT_SESSION_COMPTES_RENDUS) == null) {

			List<CompteRendu> listeCompteRendus = cr.lister();
			Map<Long, CompteRendu> mapCompteRendus = new HashMap<Long, CompteRendu>();
			for (CompteRendu s : listeCompteRendus) {
				mapCompteRendus.put(s.getId(), s);
			}
			Comparator<CompteRendu> valueComparator = new Comparator<CompteRendu>() {
				@Override
				public int compare(CompteRendu arg0, CompteRendu arg1) {

					return arg0.getTitre().compareToIgnoreCase(arg1.getTitre());
				}
			};
			MapValueComparator<Long, CompteRendu> mapComparator = new MapValueComparator<Long, CompteRendu>(
					mapCompteRendus, valueComparator);
			Map<Long, CompteRendu> sortedOnValuesMap = new TreeMap<Long, CompteRendu>(mapComparator);
			sortedOnValuesMap.putAll(mapCompteRendus);
			mapCompteRendus = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_COMPTES_RENDUS, mapCompteRendus);
		}

		if (session.getAttribute(ATT_SESSION_PARTENAIRES) == null) {

			List<Partenaire> listePartenaires = pa.lister();
			Map<Long, Partenaire> mapPartenaires = new HashMap<Long, Partenaire>();
			for (Partenaire s : listePartenaires) {
				mapPartenaires.put(s.getId(), s);
			}
			Comparator<Partenaire> valueComparator = new Comparator<Partenaire>() {
				@Override
				public int compare(Partenaire arg0, Partenaire arg1) {

					if (arg0.getType().compareToIgnoreCase(arg1.getType()) == 0) {
						return -1;
					} else {
						return 1;
					}

				}
			};
			MapValueComparator<Long, Partenaire> mapComparator = new MapValueComparator<Long, Partenaire>(
					mapPartenaires, valueComparator);
			Map<Long, Partenaire> sortedOnValuesMap = new TreeMap<Long, Partenaire>(mapComparator);
			sortedOnValuesMap.putAll(mapPartenaires);
			mapPartenaires = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_PARTENAIRES, mapPartenaires);
		}

		if (session.getAttribute(ATT_SESSION_ANNONCES) == null) {

			List<Annonce> listeAnnonces = an.lister();
			Map<Long, Annonce> mapAnnonces = new HashMap<Long, Annonce>();
			for (Annonce s : listeAnnonces) {
				mapAnnonces.put(s.getId(), s);
			}
			Comparator<Annonce> valueComparator = new Comparator<Annonce>() {
				@Override
				public int compare(Annonce arg0, Annonce arg1) {

					return arg0.getTitre().compareToIgnoreCase(arg1.getTitre());
				}
			};
			MapValueComparator<Long, Annonce> mapComparator = new MapValueComparator<Long, Annonce>(mapAnnonces,
					valueComparator);
			Map<Long, Annonce> sortedOnValuesMap = new TreeMap<Long, Annonce>(mapComparator);
			sortedOnValuesMap.putAll(mapAnnonces);
			mapAnnonces = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_ANNONCES, mapAnnonces);
		}

		if (session.getAttribute(ATT_SESSION_MANIFESTATIONS) == null) {

			List<Manifestation> listeManifestations = ma.lister();
			Map<Long, Manifestation> mapManifestations = new HashMap<Long, Manifestation>();
			for (Manifestation s : listeManifestations) {
				mapManifestations.put(s.getId(), s);
			}
			Comparator<Manifestation> valueComparator = new Comparator<Manifestation>() {
				@Override
				public int compare(Manifestation arg0, Manifestation arg1) {

					if (arg0.getDate().compareTo(arg1.getDate()) == 0) {
						return -1;
					} else {
						return 1;
					}

				}
			};
			MapValueComparator<Long, Manifestation> mapComparator = new MapValueComparator<Long, Manifestation>(
					mapManifestations, valueComparator);
			Map<Long, Manifestation> sortedOnValuesMap = new TreeMap<Long, Manifestation>(mapComparator);
			sortedOnValuesMap.putAll(mapManifestations);
			mapManifestations = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_MANIFESTATIONS, mapManifestations);
		}

		/*
		 * if (session.getAttribute(ATT_SESSION_IMAGES) == null) {
		 * 
		 * File dir = new File("C:/fichiers/galerie"); File[] files =
		 * dir.listFiles(); List<String> tokens = new ArrayList<String>(); for
		 * (File file : files) {
		 * 
		 * tokens.add((Paths.get(file.getCanonicalPath())).toString());
		 * 
		 * } for (String t : tokens) System.out.println(t);
		 * 
		 * session.setAttribute(ATT_SESSION_IMAGES, tokens); }
		 * 
		 * /* Pour terminer, poursuite de la requ�te en cours
		 */
		chain.doFilter(request, res);
	}

	public void destroy() {
	}
}
