package servlets;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

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
import forms.CollaborateurForm;

public class ConsulterCollaborateur extends HttpServlet {
	/**
	 * 
	 */
	public static final String CONF_FACTORY = "factory";

	public static final String ATT_SERVICE = "service";
	public static final String ATT_USER = "collaborateur";
	public static final String ATT_FORM = "form";
	public static final String ATT_SESSION_USER = "sessionCollaborateur";

	public static final String APPLICATION_COLLABORATEURS = "initCollaborateurs";
	public static final String APPLICATION_SERVICES = "initServices";

	public static final String SESSION_SERVICES = "services";
	public static final String SESSION_COLLABORATEURS = "collaborateurs";
	public static final String SESSION_ADRESSES = "adresses";

	public static final String SESSION_FONCTIONS = "fonctions";

	private static final long serialVersionUID = 1L;

	public static final String CHEMIN = "chemin";

	public static final String MSG = "message";
	public static final String SUCCES = "succes";
	boolean succes = false;

	public static final String VUE_SUCCES = "/WEB-INF/collaborateur.jsp";
	public static final String VUE_FORM = "/WEB-INF/collaborateur.jsp";

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
		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		/* Traitement de la requ�te et r�cup�ration du bean en r�sultant */
		Utilisateur collaborateur = form.consulterCollaborateur(request);

		/* Ajout du bean et de l'objet m�tier � l'objet requ�te */
		request.setAttribute(ATT_USER, collaborateur);
		request.setAttribute(ATT_FORM, form);

		/* Si aucune erreur */
		if (form.getErreurs().isEmpty()) {
			/* R�cup�ration de la session depuis la requ�te */
			HttpSession session = request.getSession(true);
			Map<Long, Utilisateur> collaborateurs = (TreeMap<Long, Utilisateur>) session
					.getAttribute(SESSION_COLLABORATEURS);

			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (collaborateurs == null) {
				collaborateurs = new TreeMap<Long, Utilisateur>();
			}

			/* Et enfin (r�)enregistrement de la map en session */
			session.setAttribute(SESSION_COLLABORATEURS, collaborateurs);

			/* Ensuite r�cup�ration de la map des commandes dans la session */
			Map<Long, Service> services = (TreeMap<Long, Service>) session.getAttribute(SESSION_SERVICES);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (services == null) {
				services = new TreeMap<Long, Service>();
			}

			/* Et enfin (r�)enregistrement de la map en session */
			session.setAttribute(SESSION_SERVICES, services);

			/* Ensuite r�cup�ration de la map des commandes dans la session */
			Map<Long, Adresse> adresses = (TreeMap<Long, Adresse>) session.getAttribute(SESSION_ADRESSES);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (adresses == null) {
				adresses = new TreeMap<Long, Adresse>();
			}

			/* Et enfin (r�)enregistrement de la map en session */
			session.setAttribute(SESSION_ADRESSES, adresses);

			/* Ensuite r�cup�ration de la map des commandes dans la session */
			Map<Long, Fonction> fonctions = (TreeMap<Long, Fonction>) session.getAttribute(SESSION_FONCTIONS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (fonctions == null) {
				fonctions = new TreeMap<Long, Fonction>();
			}

			/* Et enfin (r�)enregistrement de la map en session */
			session.setAttribute(SESSION_FONCTIONS, fonctions);

			succes = true;
			request.setAttribute(SUCCES, succes);
			/* Affichage de la fiche r�capitulative */
			this.getServletContext().getRequestDispatcher(VUE_SUCCES).forward(request, response);
		} else {
			request.setAttribute(SUCCES, succes);
			/* Sinon, r�-affichage du formulaire de cr�ation avec les erreurs */
			this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
		}
	}

}
