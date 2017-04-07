package servlets;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import modele.DatabaseException;
import modele.ServiceManager;
import modele.Factory;
import beans.Service;

public class SupprimerService extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CONF_FACTORY = "factory";
	public static final String PARAM_ID_SERVICE = "idService";
	public static final String SESSION_SERVICES = "services";

	public static final String VUE = "/consulterListeService";

	private ServiceManager s;

	public void init() throws ServletException {
		/* Récupération d'une instance de notre DAO Service */
		this.s = ((Factory) getServletContext().getAttribute(CONF_FACTORY))
				.getServiceManager();
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Récupération du paramètre */
		String idService = getValeurParametre(request, PARAM_ID_SERVICE);
		Long id = Long.parseLong(idService);

		/* Récupération de la Map des commandes enregistrées en session */
		HttpSession session = request.getSession();
		Map<Long, Service> services = (TreeMap<Long, Service>) session
				.getAttribute(SESSION_SERVICES);

		/* Si l'id de la commande et la Map des commandes ne sont pas vides */
		if (id != null && services != null) {
			try {
				/* Alors suppression de la commande de la BDD */
				s.supprimer(services.get(id));
				/* Puis suppression de la commande de la Map */
				services.remove(id);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			/* Et remplacement de l'ancienne Map en session par la nouvelle */
			session.setAttribute(SESSION_SERVICES, services);
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