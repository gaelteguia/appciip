package servlets;

import java.io.IOException;

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
import beans.Collaborateur;
import forms.CollaborateurForm;

public class Login extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CONF_FACTORY = "factory";
	public static final String ATT_USER = "utilisateur";
	public static final String ATT_FORM = "form";
	public static final String ATT_SESSION_USER = "sessionCollaborateur";

	public static final String VUE_FORM = "/WEB-INF/Login.jsp";
	public static final String VUE_SUCCES = "/WEB-INF/MenuPrincipal.jsp";
	public static final String VUE_MDP = "/WEB-INF/InitialiserCollaborateur.jsp";

	public static final String MSG = "message";

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

		/* Affichage de la page de connexion */
		this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Pr�paration de l'objet formulaire */
		CollaborateurForm form = new CollaborateurForm(u, a, d, l, m, n, p, s);

		/* Traitement de la requ�te et r�cup�ration du bean en r�sultant */
		Utilisateur utilisateur = form.connecterCollaborateur(request);

		/* Stockage du formulaire et du bean dans l'objet request */
		request.setAttribute(ATT_USER, utilisateur);
		request.setAttribute(ATT_FORM, form);

		/*
		 * Verification de l'existance de l'existence de l'utilisateur dans la
		 * base de donn�es Si aucune erreur de validation n'a eu lieu, alors
		 * ajout du bean Collaborateur � la session, sinon suppression du bean
		 * de la session. connecterCollaborateur est execut� que s'il n 'y a pas
		 * d'erreurs au prealable sinon il peut lever un NullPointerException
		 * pour un mot de passe null.
		 */

		if (form.getErreurs().isEmpty()) {
			/* R�cup�ration de la session depuis la requ�te */
			HttpSession session = request.getSession(true);
			session.setAttribute(ATT_SESSION_USER, utilisateur);
			if (utilisateur.isActif())
				this.getServletContext().getRequestDispatcher(VUE_SUCCES).forward(request, response);

			else {
				request.setAttribute(MSG,
						"Changez votre mot de passe et introduisez un challenge Question/Reponse qui vous sera utile en cas de perte de votre mot de passe.");
				this.getServletContext().getRequestDispatcher(VUE_MDP).forward(request, response);
			}

		} else {

			request.setAttribute(MSG, "Mot de passe oubli�...");
			this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
		}

	}
}