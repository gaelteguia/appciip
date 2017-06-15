package servlets;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import modele.ApplicationManager;
import modele.SeanceManager;
import modele.Factory;
import modele.AdresseManager;
import modele.FonctionManager;
import modele.EvenementManager;
import modele.ComiteManager;
import modele.ServiceManager;
import modele.CollaborateurManager;
import beans.Adresse;
import beans.Fonction;
import beans.Service;
import beans.Collaborateur;
import forms.ServiceForm;

public class CreerService extends HttpServlet {
	/**
	 * 
	 */
	public static final String CONF_FACTORY = "factory";

	public static final String ATT_SERVICE = "service";
	public static final String ATT_USER = "utilisateur";
	public static final String ATT_FORM = "form";
	public static final String ATT_SESSION_USER = "sessionCollaborateur";

	public static final String APPLICATION_UTILISATEURS = "initCollaborateurs";
	public static final String APPLICATION_SERVICES = "initServices";

	public static final String SESSION_SERVICES = "services";
	public static final String SESSION_UTILISATEURS = "utilisateurs";
	public static final String SESSION_LOCALISATIONS = "localisations";

	public static final String SESSION_METIERS = "metiers";

	private static final long serialVersionUID = 1L;

	public static final String CHEMIN = "chemin";

	public static final String MSG = "message";
	public static final String SUCCES = "succes";
	boolean succes = false;

	public static final String VUE_SUCCES = "/WEB-INF/AssocierAdresseService.jsp";
	public static final String VUE_FORM = "/WEB-INF/CreerService.jsp";

	private CollaborateurManager u;
	private ApplicationManager a;
	private SeanceManager d;
	private AdresseManager l;
	private FonctionManager m;
	private EvenementManager n;
	private ComiteManager p;
	private ServiceManager s;

	public void init() throws ServletException {
		this.u = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getCollaborateurManager();
		this.a = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getApplicationManager();
		this.d = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getSeanceManager();
		this.l = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getAdresseManager();
		this.m = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getFonctionManager();
		this.n = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getEvenementManager();
		this.p = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getComiteManager();
		this.s = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getServiceManager();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* � la r�ception d'une requ�te GET, simple affichage du formulaire */
		this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* Pr�paration de l'objet formulaire */
		ServiceForm form = new ServiceForm(u, a, d, l, m, n, p, s);

		/* Traitement de la requ�te et r�cup�ration du bean en r�sultant */
		Service service = form.creerService(request);

		/* Ajout du bean et de l'objet m�tier � l'objet requ�te */
		request.setAttribute(ATT_SERVICE, service);
		request.setAttribute(ATT_FORM, form);

		/* Si aucune erreur */
		if (form.getErreurs().isEmpty()) {
			/* R�cup�ration de la session depuis la requ�te */
			HttpSession session = request.getSession(true);
			Map<Long, Utilisateur> utilisateurs = (TreeMap<Long, Utilisateur>) session
					.getAttribute(SESSION_UTILISATEURS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (utilisateurs == null) {
				utilisateurs = new TreeMap<Long, Utilisateur>();
			}

			/* Et enfin (r�)enregistrement de la map en session */
			session.setAttribute(SESSION_UTILISATEURS, utilisateurs);

			/* Ensuite r�cup�ration de la map des commandes dans la session */
			// Map<Long, Service> services = (TreeMap<Long, Service>) session
			// .getAttribute(SESSION_SERVICES);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			// if (services == null) {
			// services = new TreeMap<Long, Service>();
			// }
			/* Puis ajout de la commande courante dans la map */
			// services.put(service.getId(), service);
			/* Et enfin (r�)enregistrement de la map en session */
			// session.setAttribute(SESSION_SERVICES, services);

			/* Ensuite r�cup�ration de la map des commandes dans la session */
			Map<Long, Adresse> localisations = (TreeMap<Long, Adresse>) session.getAttribute(SESSION_LOCALISATIONS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (localisations == null) {
				localisations = new TreeMap<Long, Adresse>();
			}

			/* Et enfin (r�)enregistrement de la map en session */
			session.setAttribute(SESSION_LOCALISATIONS, localisations);

			/* Ensuite r�cup�ration de la map des commandes dans la session */
			Map<Long, Fonction> metiers = (TreeMap<Long, Fonction>) session.getAttribute(SESSION_METIERS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (metiers == null) {
				metiers = new TreeMap<Long, Fonction>();
			}

			/* Et enfin (r�)enregistrement de la map en session */
			session.setAttribute(SESSION_METIERS, metiers);

			succes = true;
			request.setAttribute("succes", succes);
			/* Affichage de la fiche r�capitulative */
			this.getServletContext().getRequestDispatcher(VUE_SUCCES).forward(request, response);
		} else {
			request.setAttribute("succes", succes);
			/* Sinon, r�-affichage du formulaire de cr�ation avec les erreurs */
			this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
		}

	}
}