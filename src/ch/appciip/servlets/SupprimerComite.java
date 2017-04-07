package servlets;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import modele.ComiteManager;
import modele.DatabaseException;
import modele.Factory;
import beans.Comite;

public class SupprimerComite extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CONF_FACTORY = "factory";
	public static final String PARAM_ID_PROFIL = "idComite";
	public static final String SESSION_PROFILS = "profils";

	public static final String VUE = "/consulterListeComite";

	private ComiteManager p;

	public void init() throws ServletException {
		/* R�cup�ration d'une instance de notre DAO Utilisateur */
		this.p = ((Factory) getServletContext().getAttribute(CONF_FACTORY))
				.getComiteManager();
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* R�cup�ration du param�tre */
		String idComite = getValeurParametre(request, PARAM_ID_PROFIL);
		Long id = Long.parseLong(idComite);

		/* R�cup�ration de la Map des commandes enregistr�es en session */
		HttpSession session = request.getSession();
		Map<Long, Localisation> profils = (TreeMap<Long, Localisation>) session
				.getAttribute(SESSION_PROFILS);

		/* Si l'id de la commande et la Map des commandes ne sont pas vides */
		if (id != null && profils != null) {
			try {
				/* Alors suppression de la commande de la BDD */
				p.supprimer(profils.get(id));
				/* Puis suppression de la commande de la Map */
				profils.remove(id);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			/* Et remplacement de l'ancienne Map en session par la nouvelle */
			session.setAttribute(SESSION_PROFILS, profils);
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