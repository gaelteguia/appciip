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
import modele.DatabaseException;
import modele.Factory;
import beans.Application;

public class SupprimerApplication extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CONF_FACTORY = "factory";
	public static final String PARAM_ID_APPLICATION = "idApplication";
	public static final String SESSION_APPLICATIONS = "applications";

	public static final String VUE = "/consulterListeApplication";

	private ApplicationManager a;

	public void init() throws ServletException {
		/* Récupération d'une instance de notre DAO Utilisateur */
		this.a = ((Factory) getServletContext().getAttribute(CONF_FACTORY))
				.getApplicationManager();
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Récupération du paramètre */
		String idApplication = getValeurParametre(request, PARAM_ID_APPLICATION);
		Long id = Long.parseLong(idApplication);

		/* Récupération de la Map des commandes enregistrées en session */
		HttpSession session = request.getSession();
		Map<Long, Application> applications = (TreeMap<Long, Application>) session
				.getAttribute(SESSION_APPLICATIONS);

		/* Si l'id de la commande et la Map des commandes ne sont pas vides */
		if (id != null && applications != null) {
			try {
				/* Alors suppression de la commande de la BDD */
				a.supprimer(applications.get(id));
				/* Puis suppression de la commande de la Map */
				applications.remove(id);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			/* Et remplacement de l'ancienne Map en session par la nouvelle */
			session.setAttribute(SESSION_APPLICATIONS, applications);
		}

		/* Redirection vers la fiche récapitulative */
		response.sendRedirect(request.getContextPath() + VUE);
	}

	/*
	 * Méthode utilitaire qui retourne null si un paramètre est vide, et son
	 * contenu sinon.
	 */
	private static String getValeurParametre(HttpServletRequest request,
			String nomChamp) {
		String valeur = request.getParameter(nomChamp);
		if (valeur == null || valeur.trim().length() == 0) {
			return null;
		} else {
			return valeur;
		}
	}
}