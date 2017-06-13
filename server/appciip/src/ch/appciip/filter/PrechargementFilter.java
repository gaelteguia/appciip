package ch.appciip.filter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.appciip.bean.Address;
import ch.appciip.bean.Manifestation;
import ch.appciip.bean.User;
import ch.appciip.model.AddressManager;
import ch.appciip.model.Factory;
import ch.appciip.model.ManifestationManager;
import ch.appciip.model.UserManager;

/**
 * Servlet Filter implementation class PrechargementFilter
 */
public class PrechargementFilter implements Serializable, Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CONF_FACTORY = "factory";
	public static final String ATT_SESSION_USERS = "users";
	public static final String ATT_SESSION_ADDRESSES = "addresses";
	public static final String ATT_SESSION_MANIFESTATIONS = "manifestations";

	public static final String ATT_SESSION_IMAGES = "images";

	public static final String ATT_SESSION_USER = "sessionUser";

	public static final String URL_REDIRECTION = "/appciip/home";

	private UserManager u;

	private AddressManager a;

	private ManifestationManager m;

	public void init(FilterConfig config) throws ServletException {
		/* R�cup�ration d'instances de nos Manager */
		this.u = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getUserManager();
		this.a = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getAddressManager();
		this.m = ((Factory) config.getServletContext().getAttribute(CONF_FACTORY)).getManifestationManager();

	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		/* Cast de l'objet request */
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		/* Non-filtrage des ressources statiques */
		String chemin = request.getRequestURI().substring(request.getContextPath().length());
		if (chemin.startsWith("/deconnexion")) {
			chain.doFilter(request, response);
			return;
		}

		/* R�cup�ration de la session depuis la requ�te */
		HttpSession session = request.getSession();

		/*
		 * Si la map des users n'existe pas en session, alors l'user se connecte
		 * pour la premi�re fois et nous devons pr�charger en session les
		 * infos contenues dans la BDD.
		 */
		if (session.getAttribute(ATT_SESSION_MANIFESTATIONS) == null) {
			
			List<Manifestation> listeManifestations = m.list();
			Map<Long, Manifestation> mapManifestations = new HashMap<Long, Manifestation>();
			for (Manifestation s : listeManifestations) {
				mapManifestations.put(Long.parseLong(s.getId()), s);
			}
			Comparator<Manifestation> valueComparator = new Comparator<Manifestation>() {
				@Override
				public int compare(Manifestation arg0, Manifestation arg1) {

					if (arg0.getStartTime().compareTo(arg1.getStartTime()) == 0) {
						return -1;
					} else {
						return 1;
					}

				}
			};
			MapValueComparator<Long, Manifestation> mapComparator = new MapValueComparator<Long, Manifestation>(
					mapManifestations, valueComparator);
			Map<Long, Manifestation> sortedOnValuesMap = new TreeMap<Long, Manifestation>(mapComparator);
			sortedOnValuesMap.putAll(mapManifestations);
			mapManifestations = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_MANIFESTATIONS, mapManifestations);
		}

		if (session.getAttribute(ATT_SESSION_USERS) == null) {
			// R�cup�ration de la liste des users existants, et
			// enregistrement en session

			List<User> listeUsers = u.list();
			Map<Long, User> mapUsers = new HashMap<Long, User>();
			for (User u : listeUsers) {

				mapUsers.put(u.getId(), u);

			}
			Comparator<User> valueComparator = new Comparator<User>() {
				@Override
				public int compare(User arg0, User arg1) {

					int result = arg0.getName().compareToIgnoreCase(arg1.getName());
					if (result == 0) {
						result = arg0.getForename().compareToIgnoreCase(arg1.getForename());
					}
					return result;
				}
			};
			MapValueComparator<Long, User> mapComparator = new MapValueComparator<Long, User>(mapUsers,
					valueComparator);
			Map<Long, User> sortedOnValuesMap = new TreeMap<Long, User>(mapComparator);
			sortedOnValuesMap.putAll(mapUsers);
			mapUsers = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_USERS, mapUsers);
		}

		if (session.getAttribute(ATT_SESSION_ADDRESSES) == null) {

			List<Address> listeAddresss = a.list();
			Map<Long, Address> mapAddresss = new HashMap<Long, Address>();
			for (Address l : listeAddresss) {
				mapAddresss.put(l.getId(), l);
			}
			Comparator<Address> valueComparator = new Comparator<Address>() {
				@Override
				public int compare(Address arg0, Address arg1) {

					return arg0.getName().compareToIgnoreCase(arg1.getName());
				}
			};
			MapValueComparator<Long, Address> mapComparator = new MapValueComparator<Long, Address>(mapAddresss,
					valueComparator);
			Map<Long, Address> sortedOnValuesMap = new TreeMap<Long, Address>(mapComparator);
			sortedOnValuesMap.putAll(mapAddresss);
			mapAddresss = sortedOnValuesMap;
			session.setAttribute(ATT_SESSION_ADDRESSES, mapAddresss);
		}

		/*
		 * if (session.getAttribute(ATT_SESSION_IMAGES) == null) {
		 * 
		 * File dir = new File("C:/fichiers/galerie"); File[] files =
		 * dir.listFiles(); List<String> tokens = new ArrayList<String>(); for
		 * (File file : files) {
		 * 
		 * tokens.add((Paths.get(file.getCanonicalPath())).toString());
		 * 
		 * } for (String t : tokens) System.out.println(t);
		 * 
		 * session.setAttribute(ATT_SESSION_IMAGES, tokens); }
		 * 
		 * /* Pour terminer, poursuite de la requ�te en cours
		 */
		chain.doFilter(request, res);
	}

	public void destroy() {
	}
}
