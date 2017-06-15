package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RestrictionFilter implements Filter {
	public static final String ACCES_CONNEXION = "/home";
	public static final String ATT_SESSION_USER = "sessionCollaborateur";
	public static final String URL_REDIRECTION = "/ciip/login";

	public void init(FilterConfig config) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		/* Cast des objets request et response */
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		/* R�cup�ration de la session depuis la requ�te */
		HttpSession session = request.getSession(true);

		/* Non-filtrage des ressources statiques */
		String chemin = request.getRequestURI().substring(request.getContextPath().length());

		if (chemin.startsWith("/css")) {
			chain.doFilter(request, response);
			return;
		}
		if (chemin.startsWith("/img")) {
			chain.doFilter(request, response);
			return;
		}
		if (chemin.startsWith("/js")) {
			chain.doFilter(request, response);
			return;
		}

		/**
		 * Si l'objet utilisateur n'existe pas dans la session en cours, alors
		 * l'utilisateur n'est pas connect�.
		 */
		if (chemin.startsWith("/login") || chemin.startsWith("/challenge") || chemin.startsWith("/deconnexion")) {

			chain.doFilter(request, response);

		}

		else if (session.getAttribute(ATT_SESSION_USER) == null) {

			response.sendRedirect(URL_REDIRECTION);

		} else {

			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}
}