package config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import modele.DataParser;
import modele.Factory;

public class Initialisation implements ServletContextListener {
	private static final String ATT_FACTORY = "factory";

	private Factory factory;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		/* R�cup�ration du ServletContext lors du chargement de l'application */
		ServletContext servletContext = event.getServletContext();
		/* Instanciation de notre Factory */
		this.factory = Factory.getInstance();
		// Execution du thread toutes les 4 heures
		new DataParser(14400);
		/*
		 * Enregistrement dans un attribut ayant pour port�e toute l'application
		 */
		servletContext.setAttribute(ATT_FACTORY, this.factory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		/* Rien � r�aliser lors de la fermeture de l'application... */
	}
}