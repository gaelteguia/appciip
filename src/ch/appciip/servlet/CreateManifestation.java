package ch.appciip.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.appciip.bean.User;
import ch.appciip.bean.Address;
import ch.appciip.bean.Institution;
import ch.appciip.bean.Manifestation;
import ch.appciip.bean.User;
import ch.appciip.form.ManifestationForm;
import ch.appciip.model.UserManager;
import ch.appciip.model.ManifestationManager;
import ch.appciip.model.UserManager;
import ch.appciip.model.Factory;

public class CreateManifestation extends HttpServlet {
	/**
	 * 
	 */
	public static final String CONF_FACTORY = "factory";

	public static final String ATT_MANIFESTATION = "manifestation";
	public static final String ATT_USER = "user";
	public static final String ATT_FORM = "form";
	public static final String ATT_SESSION_USER = "sessionUser";

	public static final String APPLICATION_USERS = "initUsers";
	public static final String APPLICATION_INSTITUTIONS = "initServices";

	public static final String SESSION_INSTITUTIONS = "institutions";
	public static final String SESSION_USERS = "users";

	public static final String SESSION_MANIFESTATIONS = "manifestations";

	public static final String SESSION_ADDRESSES = "addresses";
	private static final long serialVersionUID = 1L;

	public static final String CHEMIN = "chemin";

	public static final String MSG = "message";
	public static final String SUCCES = "succes";
	boolean succes = false;

	public static final String VUE_SUCCES = "/WEB-INF/ReadManifestation.jsp";
	public static final String VUE_FORM = "/WEB-INF/CreateManifestation.jsp";

	private UserManager u;
	private UserManager a;
	private ManifestationManager m;

	public void init() throws ServletException {
		// this.u = ((Factory)
		// getServletContext().getAttribute(CONF_FACTORY)).getUserManager();
		// this.a = ((Factory)
		// getServletContext().getAttribute(CONF_FACTORY)).getUserManager();
		this.m = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getManifestationManager();

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("voilavouletteu");
		/* À la réception d'une requête GET, simple affichage du formulaire */
		this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * Lecture du paramètre 'chemin' passé à la servlet via la déclaration
		 * dans le web.xml
		 */
		String chemin = this.getServletConfig().getInitParameter(CHEMIN);

		/* Préparation de l'objet formulaire */
		ManifestationForm form = new ManifestationForm(m);

		/* Traitement de la requête et récupération du bean en résultant */
		Manifestation manifestation = form.createManifestation(request, chemin);

		/* Ajout du bean et de l'objet métier à l'objet requête */
		request.setAttribute(ATT_MANIFESTATION, manifestation);
		request.setAttribute(ATT_FORM, form);

		/* Si aucune erreur */
		if (form.getErreurs().isEmpty()) {

			/* Récupération de la session depuis la requête */
			HttpSession session = request.getSession(true);
			Map<Long, Manifestation> manifestations = (TreeMap<Long, Manifestation>) session
					.getAttribute(SESSION_MANIFESTATIONS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (manifestations == null) {
				manifestations = new TreeMap<Long, Manifestation>();
			}
			/* Puis ajout de la commande courante dans la map */
			manifestations.put(Long.parseLong(manifestation.getId()), manifestation);
			/* Puis ajout du client de la commande courante dans la map */
			// manifestations.put(manifestation.getSuperviseur().getId(),
			// manifestation.getSuperviseur());
			/* Et enfin (ré)enregistrement de la map en session */
			session.setAttribute(SESSION_MANIFESTATIONS, manifestations);

			/* Ensuite récupération de la map des commandes dans la session */
			Map<Long, User> users = (TreeMap<Long, User>) session.getAttribute(SESSION_USERS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (users == null) {
				users = new TreeMap<Long, User>();
			}

			/* Puis ajout de la commande courante dans la map */
			Iterator<User> itr = manifestation.getUsers().iterator();
			while (itr.hasNext()) {
				users.put(((User) itr.next()).getId(), (User) itr.next());
			}

			/* Et enfin (ré)enregistrement de la map en session */
			session.setAttribute(SESSION_USERS, users);

			/* Ensuite récupération de la map des commandes dans la session */
			Map<Long, Address> addresses = (TreeMap<Long, Address>) session.getAttribute(SESSION_ADDRESSES);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (addresses == null) {
				addresses = new TreeMap<Long, Address>();
			}
			/* Puis ajout de la commande courante dans la map */
			addresses.put(manifestation.getAddress().getId(), manifestation.getAddress());
			/* Et enfin (ré)enregistrement de la map en session */
			session.setAttribute(SESSION_ADDRESSES, addresses);

			/* Ensuite récupération de la map des commandes dans la session */
			Map<Long, Institution> institutions = (TreeMap<Long, Institution>) session
					.getAttribute(SESSION_INSTITUTIONS);
			/*
			 * Si aucune map n'existe, alors initialisation d'une nouvelle map
			 */
			if (institutions == null) {
				institutions = new TreeMap<Long, Institution>();
			}
			/* Puis ajout de la commande courante dans la map */
			institutions.put(manifestation.getInstitution().getId(), manifestation.getInstitution());
			/* Et enfin (ré)enregistrement de la map en session */
			session.setAttribute(SESSION_INSTITUTIONS, institutions);

			succes = true;
			request.setAttribute("succes", succes);
			/* Affichage de la fiche récapitulative */
			this.getServletContext().getRequestDispatcher(VUE_SUCCES).forward(request, response);
		} else {
			request.setAttribute("succes", succes);
			/* Sinon, ré-affichage du formulaire de création avec les erreurs */
			this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
		}

	}
}
