package servlets;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import modele.SeanceManager;
import modele.DatabaseException;
import modele.Factory;
import beans.Seance;

public class SupprimerSeance extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CONF_FACTORY = "factory";
	public static final String PARAM_ID_DROIT = "idSeance";
	public static final String SESSION_DROITS = "droits";

	public static final String VUE = "/consulterListeSeance";

	private SeanceManager d;

	public void init() throws ServletException {
		/* R�cup�ration d'une instance de notre DAO Utilisateur */
		this.d = ((Factory) getServletContext().getAttribute(CONF_FACTORY))
				.getSeanceManager();
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* R�cup�ration du param�tre */
		String idSeance = getValeurParametre(request, PARAM_ID_DROIT);
		Long id = Long.parseLong(idSeance);

		/* R�cup�ration de la Map des commandes enregistr�es en session */
		HttpSession session = request.getSession();
		Map<Long, Seance> droits = (TreeMap<Long, Seance>) session
				.getAttribute(SESSION_DROITS);

		/* Si l'id de la commande et la Map des commandes ne sont pas vides */
		if (id != null && droits != null) {
			try {
				/* Alors suppression de la commande de la BDD */
				d.supprimer(droits.get(id));
				/* Puis suppression de la commande de la Map */
				droits.remove(id);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			/* Et remplacement de l'ancienne Map en session par la nouvelle */
			session.setAttribute(SESSION_DROITS, droits);
		}

		/* Redirection vers la fiche r�capitulative */
		response.sendRedirect(request.getContextPath() + VUE);
	}

	/*
	 * M�thode utilitaire qui retourne null si un param�tre est vide, et son
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