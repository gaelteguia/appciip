package ch.appciip.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class Factory {

	private static final String FICHIER_PROPERTIES = "database.properties";
	private static final String PROPERTY_URL = "url";
	private static final String PROPERTY_DRIVER = "driver";
	private static final String PROPERTY_NOM_UTILISATEUR = "nomutilisateur";
	private static final String PROPERTY_MOT_DE_PASSE = "motdepasse";

	BoneCP connectionPool = null;

	Factory(BoneCP connectionPool) {
		this.connectionPool = connectionPool;
	}

	/*
	 * M�thode charg�e de r�cup�rer les informations de connexion � la
	 * base de donn�es, charger le driver JDBC et retourner une instance de la
	 * Factory
	 */
	public static Factory getInstance() throws ConfigurationException {
		Properties properties = new Properties();
		String url;
		String driver;
		String nomUser;
		String motDePasse;
		BoneCP connectionPool = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		InputStream fichierProperties = classLoader.getResourceAsStream(FICHIER_PROPERTIES);

		if (fichierProperties == null) {

			throw new ConfigurationException("Le fichier properties " + FICHIER_PROPERTIES + " est introuvable.");
		}

		try {
			properties.load(fichierProperties);
			url = properties.getProperty(PROPERTY_URL);
			driver = properties.getProperty(PROPERTY_DRIVER);
			nomUser = properties.getProperty(PROPERTY_NOM_UTILISATEUR);
			motDePasse = properties.getProperty(PROPERTY_MOT_DE_PASSE);
		} catch (FileNotFoundException e) {
			throw new ConfigurationException("Le fichier properties " + FICHIER_PROPERTIES + " est introuvable", e);
		} catch (IOException e) {
			throw new ConfigurationException("Impossible de charger le fichier properties " + FICHIER_PROPERTIES, e);
		}

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Le driver est introuvable dans le classpath.", e);
		}

		try {
			/*
			 * Cr�ation d'une configuration de pool de connexions via l'objet
			 * BoneCPConfig et les diff�rents setters associ�s.
			 */
			BoneCPConfig config = new BoneCPConfig();
			/* Mise en place de l'URL, du nom et du mot de passe */
			config.setJdbcUrl(url);
			config.setUsername(nomUser);
			config.setPassword(motDePasse);
			/* Param�trage de la taille du pool */
			config.setMinConnectionsPerPartition(5);
			config.setMaxConnectionsPerPartition(10);
			config.setPartitionCount(2);
			/*
			 * Cr�ation du pool � partir de la configuration, via l'objet
			 * BoneCP
			 */
			connectionPool = new BoneCP(config);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ConfigurationException("Erreur de configuration du pool de connexions.", e);
		}
		/*
		 * Enregistrement du pool cr�� dans une variable d'instance via un
		 * appel au constructeur de Factory
		 */
		Factory instance = new Factory(connectionPool);
		return instance;
	}

	/* M�thode charg�e de fournir une connexion � la base de donn�es */
	/* package */Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}

	/*
	 * M�thodes de r�cup�ration de l'impl�mentation des diff�rents
	 * Manager
	 */
	public UserManager getUserManager() {
		return new UserManagerBean(this);
	}

	public AddressManager getAddressManager() {
		return new AddressManagerBean(this);
	}

	public ManifestationManager getManifestationManager() {
		return new ManifestationManagerBean(this);
	}

	public InstitutionManager getInstitutionManager() {
		return new InstitutionManagerBean(this);
	}

}