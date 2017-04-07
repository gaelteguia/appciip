package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Collaborateur;
import forms.CollaborateurForm;
import modele.Factory;
import modele.CollaborateurManager;

public class Challenge extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String VUE_FORM = "/WEB-INF/Challenge.jsp";
	public static final String VUE_LOGIN = "/WEB-INF/Login.jsp";
	public static final String CONF_FACTORY = "factory";
	public static final String ATT_USER = "utilisateur";
	public static final String ATT_FORM = "form";
	public static final String ATT_SESSION_USER = "sessionCollaborateur";
	public static final String VUE_SUCCES = "/WEB-INF/InitialiserCollaborateur.jsp";

	private CollaborateurManager u;

	public void init() throws ServletException {
		this.u = ((Factory) getServletContext().getAttribute(CONF_FACTORY)).getCollaborateurManager();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* Pr�paration de l'objet formulaire */
		CollaborateurForm form = new CollaborateurForm(u);

		Utilisateur utilisateur = form.retrouverCollaborateur(request);

		request.setAttribute(ATT_FORM, form);
		request.setAttribute(ATT_USER, utilisateur);
		if (utilisateur.getQuestion() != null)
			this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
		else
			/* Affichage de la page du challenge */
			this.getServletContext().getRequestDispatcher(VUE_LOGIN).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Pr�paration de l'objet formulaire */
		CollaborateurForm form = new CollaborateurForm(u);

		/* Traitement de la requ�te et r�cup�ration du bean en r�sultant */
		Utilisateur utilisateur = form.challengerCollaborateur(request);

		/* Stockage du formulaire et du bean dans l'objet request */
		request.setAttribute(ATT_FORM, form);
		request.setAttribute(ATT_USER, utilisateur);

		/*
		 * Verification de l'existance de l'existence de l'utilisateur dans la
		 * base de donn�es Si aucune erreur de validation n'a eu lieu, alors
		 * ajout du bean Collaborateur � la session, sinon suppression du bean
		 * de la session. challengerCollaborateur est execut� que s'il n 'y a
		 * pas d'erreurs au prealable sinon il peut lever un
		 * NullPointerException pour un mot de passe null.
		 */

		if (form.getErreurs().isEmpty()) {

			/* R�cup�ration de la session depuis la requ�te */
			HttpSession session = request.getSession(true);
			session.setAttribute(ATT_SESSION_USER, utilisateur);
			request.setAttribute("message",
					"Changez votre mot de passe et introduisez un challenge Question/Reponse qui vous sera utile en cas de perte de votre mot de passe.");
			this.getServletContext().getRequestDispatcher(VUE_SUCCES).forward(request, response);

		} else {
			doGet(request, response);
		}
	}

}
