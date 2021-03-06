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
import beans.Application;
import beans.Collaborateur;
import forms.ApplicationForm;

public class CreerApplication extends HttpServlet {
	/**
	 * 
	 */
	public static final String CONF_FACTORY = "factory";

	public static final String ATT_APPLICATION = "application";
	public static final String ATT_USER = "utilisateur";
	public static final String ATT_FORM = "form";
	public static final String ATT_SESSION_USER = "sessionCollaborateur";

	public static final String APPLICATION_UTILISATEURS = "initCollaborateurs";
	public static final String APPLICATION_SERVICES = "initServices";

	public static final String SESSION_APPLICATIONS = "applications";
	public static final String SESSION_UTILISATEURS = "utilisateurs";
	public static final String SESSION_LOCALISATIONS = "localisations";

	public static final String SESSION_METIERS = "metiers";

	private static final long serialVersionUID = 1L;

	public static final String CHEMIN = "chemin";

	public static final String MSG = "message";
	public static final String SUCCES = "succes";
	boolean succes = false;

	public static final String VUE_SUCCES = "/WEB-INF/ConsulterApplication.jsp";
	public static final String VUE_FORM = "/WEB-INF/CreerApplication.jsp";

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
		ApplicationForm form = new ApplicationForm(u, a, d, l, m, n, p, s, null);

		/* Traitement de la requ�te et r�cup�ration du bean en r�sultant */
		Application application = form.creerApplication(request);

		/* Ajout du bean et de l'objet m�tier � l'objet requ�te */
		request.setAttribute(ATT_APPLICATION, application);
		request.setAttribute(ATT_FORM, form);

		/* Si aucune erreur */
		if (form.getErreurs().isEmpty()) {
			HttpSession session = request.getSession(true);
			// Map<Long, Application> applications = (TreeMap<Long,
			// Application>) session
			// .getAttribute(SESSION_APPLICATIONS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			// if (applications == null) {
			// applications = new TreeMap<Long, Application>();
			// }
			/* Puis ajout de la commande courante dans la map */
			// applications.put(application.getId(), application);

			Map<Long, Utilisateur> utilisateurs = (TreeMap<Long, Utilisateur>) session
					.getAttribute(SESSION_UTILISATEURS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (utilisateurs == null) {
				utilisateurs = new TreeMap<Long, Utilisateur>();
			}
			/*
			 * /* Puis ajout du client de la commande courante dans la map
			 */
			utilisateurs.put(application.getContactTechnique().getId(), application.getContactTechnique());
			/* Puis ajout du client de la commande courante dans la map */
			utilisateurs.put(application.getContactFonctionnel().getId(), application.getContactFonctionnel());
			/* Et enfin (r�)enregistrement de la map en session */
			session.setAttribute(SESSION_UTILISATEURS, utilisateurs);

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
