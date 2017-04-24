package ch.appciip.form;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import ch.appciip.bean.Address;
import ch.appciip.bean.Canton;
import ch.appciip.bean.Holiday;
import ch.appciip.bean.Institution;
import ch.appciip.bean.Localisation;
import ch.appciip.bean.Manifestation;
import ch.appciip.bean.Query;
import ch.appciip.bean.User;
import ch.appciip.bean.Weather;
import ch.appciip.model.AddressManager;
import ch.appciip.model.AddressManager;
import ch.appciip.model.InstitutionManager;
import ch.appciip.model.DatabaseException;

import ch.appciip.model.QueryManager;
import ch.appciip.model.LocalisationManager;
import ch.appciip.model.QueryManager;
import ch.appciip.model.WeatherManager;

import ch.appciip.model.HolidayManager;
import ch.appciip.model.InstitutionManager;
import ch.appciip.model.ManifestationManager;

import ch.appciip.model.CantonManager;
import ch.appciip.model.CantonManager;

import ch.appciip.model.UserManager;

import org.joda.time.DateTime;

import eu.medsea.mimeutil.MimeUtil;

public abstract class Form {
	protected UserManager u;
	protected AddressManager a;
	protected QueryManager q;
	protected LocalisationManager l;
	protected WeatherManager w;

	protected CantonManager p;

	protected HolidayManager h;

	protected InstitutionManager i;

	protected CantonManager c;

	protected ManifestationManager m;

	protected static final String FIELD_CHOICE_USER = "choiceNouvelUser";
	protected static final String FIELD_CHOICE_CONTACT_FONCTIONNEL = "choiceNewContactWeathernel";
	protected static final String FIELD_CHOICE_CONTACT_TECHNIQUE = "choiceNewContactTechnique";
	protected static final String FIELD_LIST_USERS = "listUsers";
	protected static final String FIELD_LIST_CONTACT_FONCTIONNELS = "listContactWeathernels";
	protected static final String FIELD_LIST_CONTACT_TECHNIQUES = "listContactTechniques";
	protected static final String FORMER_USER = "formerUser";
	protected static final String FORMER_CONTACT_TECHNIQUE = "formerContactTechnique";
	protected static final String FORMER_CONTACT_FONCTIONNEL = "formerContactWeathernel";
	protected static final String SESSION_USERS = "users";

	protected static final String FIELD_CHOICE_INSTITUTION = "choiceNewInstitution";
	protected static final String FORMER_INSTITUTION = "formerInstitution";
	protected static final String FIELD_LIST_INSTITUTIONS = "listInstitutions";
	protected static final String SESSION_INSTITUTIONS = "institutions";

	protected static final String FIELD_CHOICE_WEATHER = "choiceNewWeather";
	protected static final String FORMER_WEATHER = "formerWeather";
	protected static final String FIELD_LIST_WEATHERS = "listWeathers";
	protected static final String SESSION_WEATHERS = "weathers";

	protected static final String FIELD_LIST_COMITES = "listProfils";
	protected static final String FORMER_COMITE = "formerComite";
	protected static final String FIELD_CHOICE_COMITE = "choiceNewProfil";
	protected static final String SESSION_COMITES = "localisations";

	protected static final String FIELD_LIST_DROITS = "listDroits";
	protected static final String FIELD_CHOICE_DROIT = "choiceNewDroit";
	protected static final String SESSION_DROITS = "droits";

	protected static final String FIELD_LIST_MANIFESTATIONS = "listManifestations";
	protected static final String FIELD_CHOICE_MANIFESTATION = "choiceNewManifestation";
	protected static final String SESSION_MANIFESTATIONS = "manifestations";
	protected static final String FORMER_MANIFESTATION = "formerManifestation";

	protected static final String FIELD_LIST_EVENEMENTS = "listEvenements";
	protected static final String FIELD_CHOICE_EVENEMENT = "choiceNewEvenement";
	protected static final String SESSION_EVENEMENTS = "evenements";

	protected static final String FIELD_LIST_BEAMERS = "listAddresss";
	protected static final String FIELD_CHOICE_BEAMER = "choiceNewAddress";
	protected static final String SESSION_BEAMERS = "beamers";

	protected static final String FIELD_CHOICE_ADDRESS = "choiceNewLocalisation";
	protected static final String FORMER_ADDRESS = "formerLocalisation";
	protected static final String FIELD_LIST_ADDRESSES = "listLocalisations";
	protected static final String SESSION_ADDRESSES = "addresses";

	protected static final String FIELD_CHOICE_DIRECTIVE = "choiceNewDirective";
	protected static final String FORMER_DIRECTIVE = "formerDirective";
	protected static final String FIELD_LIST_DIRECTIVES = "listDirectives";
	protected static final String SESSION_DIRECTIVES = "directives";

	protected static final String FIELD_CHOICE_VOITURE = "choiceNewVoiture";
	protected static final String FORMER_VOITURE = "formerVoiture";
	protected static final String FIELD_LIST_VOITURES = "listVoitures";
	protected static final String SESSION_VOITURES = "voitures";

	protected static final String FIELD_CHOICE_SALLE = "choiceNewSalle";
	protected static final String FORMER_SALLE = "formerSalle";
	protected static final String FIELD_LIST_SALLES = "listSalles";
	protected static final String SESSION_SALLES = "salles";

	protected static final String FIELD_CHOICE_SEANCE = "choiceNewQuery";
	protected static final String FORMER_SEANCE = "formerQuery";
	protected static final String FIELD_LIST_SEANCES = "listQuerys";
	protected static final String SESSION_SEANCES = "seances";

	protected static final String FIELD_CHOICE_ANNONCE = "choiceNewAnnonce";
	protected static final String FORMER_ANNONCE = "formerAnnonce";
	protected static final String FIELD_LIST_ANNONCES = "listAnnonces";
	protected static final String SESSION_ANNONCES = "annonces";

	protected static final String FIELD_ID = "id";
	protected static final String FIELD_NAME = "name";
	protected static final String FIELD_FORENAME = "forename";
	protected static final String FIELD_TELEPHONE = "telephone";
	protected static final String FIELD_FAX = "fax";
	protected static final String FIELD_CP = "cp";
	protected static final String FIELD_TELINTERNE = "telinterne";
	protected static final String FIELD_EMAIL = "email";
	protected static final String FIELD_IMAGE = "image";
	protected static final String FIELD_INITIALES = "initiales";
	protected static final String FIELD_PASS = "mdp";
	protected static final String FIELD_CONF = "confirmation";
	protected static final String FIELD_QUESTION = "question";
	protected static final String FIELD_REPONSE = "reponse";
	protected static final String FIELD_STATUT = "statut";
	protected static final String FIELD_SEXE = "sexe";
	protected static final String FIELD_ADMIN = "admin";
	protected static final String FIELD_ACTIVITES = "activites";
	protected static final String FIELD_START_TIME_ANNIVERSAIRE = "endTimeanniversaire";
	protected static final String FIELD_START_TIME_DEBUT_INSTITUTION = "endTimedebutcanton";
	protected static final String FIELD_START_TIME_FIN_INSTITUTION = "endTimefincanton";
	protected static final String FIELD_START_TIME_FIN_EFFECTIVE = "endTimefineffective";
	protected static final String FIELD_START_TIME_ACHAT = "endTimeachat";
	protected static final String FIELD_START_TIME_PEREMPTION = "endTimeperemption";
	protected static final String FIELD_NAME_SUPERVISOR = "namesuperviseur";
	protected static final String FIELD_NAME_CONTACT_FONCTIONNEL = "namecontactweathernel";
	protected static final String FIELD_NAME_CONTACT_TECHNIQUE = "namecontacttechnique";
	protected static final String FIELD_NAME_CONTACT_INTERNE = "namecontactinterne";
	protected static final String FIELD_FORENAME_SUPERVISOR = "forenamesuperviseur";
	protected static final String FIELD_NAME_INSTITUTION = "namecanton";
	protected static final String FIELD_NAME_WEATHER = "nameweather";
	protected static final String FIELD_NAME_ADDRESS = "nameaddress";
	protected static final String FIELD_NAME_HOLIDAY = "nameholiday";
	protected static final String FIELD_NAME_MANIFESTATION = "namemanifestation";
	protected static final String FIELD_NAME_FOURNISSEUR = "namefournisseur";
	protected static final String FIELD_NAME_COMITE = "namelocalisation";
	protected static final String FIELD_NAME_BEAMER = "namebeamer";
	protected static final String FIELD_NAME_SALLE = "namesalle";
	protected static final String FIELD_NAME_OUTIL = "nameoutil";

	protected static final String FIELD_ACTIVE = "active";
	protected static final String FIELD_CITY = "city";
	protected static final String FIELD_COUNTRY = "country";
	protected static final String FIELD_STREET = "street";
	protected static final String FIELD_CANTON = "canton";
	protected static final String FIELD_TYPE = "type";
	protected static final String FIELD_ETAGE = "etage";
	protected static final String FIELD_NPA = "npa";
	protected static final String FIELD_NUMBER = "nostreet";
	protected static final String FIELD_CAPACITE = "capacite";
	protected static final String FIELD_NOPLAQUES = "noplaques";
	protected static final String FIELD_NUMERO = "numero";
	protected static final String FIELD_LIBELLE = "libelle";
	protected static final String FIELD_PRIX = "prix";
	protected static final String FIELD_DESCRIPTION = "description";
	protected static final String FIELD_ECHEANCE = "echeance";
	protected static final String FIELD_ACRONYM = "acronyme";
	protected static final String FIELD_NAME_DEPARTEMENT = "namedepartement";
	protected static final String FIELD_IDENTIFIANT = "identifiant";
	protected static final String FIELD_VALIDE = "valide";
	protected static final String FIELD_VALIDEUR = "valideur";
	protected static final String FIELD_START_TIME_VALIDATION = "endTimevalidation";
	protected static final String FIELD_COMMENTAIRES = "content";
	protected static final String FIELD_TITLE = "title";
	protected static final String FIELD_CONTENU = "contenu";
	protected static final String FIELD_START_TIME = "endTime";
	protected static final String FIELD_END_TIME = "endTime";
	protected static final String FIELD_MARQUE = "marque";
	protected static final String FIELD_MODELE = "modele";
	protected static final String FIELD_OS = "os";

	protected static final String FIELD_TAUX = "taux";
	protected static final String FIELD_AMORTISSEMENT = "amortissement";
	protected static final String FIELD_OFFICIEL = "officiel";
	protected static final String FIELD_THEME = "theme";
	protected static final String FIELD_LIEN = "website";
	protected static final String FIELD_NAME_RESPONSABLE = "nameresponsable";
	protected static final String FIELD_NAME_DISTRIBUTION = "namedistribution";
	protected static final String FIELD_NAME_DIRECTIVE = "namedirective";

	protected static final String FIELD_WEBSITE = "website";

	protected static final int TAILLE_TAMPON = 10240; // 10ko

	private String resultat;
	protected Map<String, String> erreurs = new HashMap<String, String>();
	private Scanner scanner;

	public Map<String, String> getErreurs() {
		return erreurs;
	}

	/*
	 * Ajoute un message correspondant au champ spècifiè è la map des
	 * erreurs.
	 */
	public void setErreur(String champ, String message) {
		erreurs.put(champ, message);
	}

	public String getResultat() {
		return resultat;
	}

	public void setResultat(String resultat) {
		this.resultat = resultat;
	}

	/**
	 * Valide le initiales saisi.
	 */
	private void validationInitiales(String initiales) throws FormValidationException {
		if (initiales == null || initiales.trim().isEmpty()) {
			throw new FormValidationException("Merci de saisir un initiales valide.");
		}
	}

	/**
	 * Valide le mot de passe saisi.
	 */
	private void validationMotDePasse(String motDePasse) throws FormValidationException {
		if (motDePasse != null) {
			if (motDePasse.length() < 3) {
				throw new FormValidationException("Le mot de passe doit contenir au moins 3 caractères.");
			}
		} else {
			throw new FormValidationException("Merci de saisir votre mot de passe.");
		}
	}

	private void validationConfirmation(String confirmation, String mdp) throws FormValidationException {
		if (confirmation != null) {
			if (confirmation.length() < 3) {
				throw new FormValidationException("La confirmation doit contenir au moins 3 caractères.");
			}
			if (!confirmation.matches(mdp)) {
				throw new FormValidationException("Votre confirmation est erronnèe.");
			}

		} else {
			throw new FormValidationException("Merci de saisir votre confirmation.");
		}
	}

	private void validationId(Long id) throws FormValidationException {

		if (id == null) {
			throw new FormValidationException("Id invalide.");
		}
	}

	private void validationFloat(Float id) throws FormValidationException {

		if (id == null) {
			throw new FormValidationException("Float invalide.");
		}
	}

	private void validationNumber(Integer number) throws FormValidationException {

		if (number == null) {
			throw new FormValidationException("number invalide.");
		}
	}

	private void validationNpa(Integer npa) throws FormValidationException {

		if (npa == null) {
			throw new FormValidationException("npa invalide.");
		}
	}

	private void validationCapacite(Integer capacite) throws FormValidationException {

		if (capacite == null) {
			throw new FormValidationException("capacite invalide.");
		}
	}

	private void validationCp(Integer cp) throws FormValidationException {

		if (cp == null) {
			throw new FormValidationException("cp invalide.");
		}
	}

	private void validationPrix(BigDecimal i) throws FormValidationException {
		if (i == null) {
			throw new FormValidationException("prix invalide.");
		}

	}

	private void validationDate(Date d) throws FormValidationException {

	}

	private void validationBoolean(Boolean bool) throws FormValidationException {
		if (bool == null) {
			throw new FormValidationException("Value invalide.");
		}
	}

	private void validationSexe(String sexe) throws FormValidationException {
		if (sexe != null && sexe.length() < 1) {
			throw new FormValidationException("Le sexe d'user doit contenir au moins 1 caractères.");
		}

	}

	private void validationName(String name) throws FormValidationException {
		if (name != null) {
			if (name.trim().isEmpty() || name.length() < 2) {
				throw new FormValidationException("Le name doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un name.");
		}
	}

	private void validationTitle(String title) throws FormValidationException {
		if (title != null) {
			if (title.trim().isEmpty() || title.length() < 2) {
				throw new FormValidationException("Le title doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un title.");
		}
	}

	private void validationMarque(String marque) throws FormValidationException {
		if (marque != null) {
			if (marque.trim().isEmpty() || marque.length() < 2) {
				throw new FormValidationException("La marque doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer une marque.");
		}
	}

	private void validationModele(String modele) throws FormValidationException {
		if (modele != null) {
			if (modele.trim().isEmpty() || modele.length() < 2) {
				throw new FormValidationException("Le modele doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un ch.appciip.model.");
		}
	}

	private void validationOs(String os) throws FormValidationException {
		if (os != null) {
			if (os.trim().isEmpty() || os.length() < 2) {
				throw new FormValidationException("L'os doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un os.");
		}
	}

	private void validationQuestion(String question) throws FormValidationException {
		if (question != null) {
			if (question.length() < 5) {
				throw new FormValidationException("La question doit contenir au moins 5 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer une question.");
		}
	}

	private void validationReponse(String reponse) throws FormValidationException {
		if (reponse != null) {
			if (reponse.length() < 2) {
				throw new FormValidationException("Le reponse doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer une reponse.");
		}
	}

	private void validationDescription(String description) throws FormValidationException {
		if (description != null) {
			if (description.length() < 2) {
				throw new FormValidationException("La description doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer une description.");
		}

	}

	private void validationCanton(String canton) throws FormValidationException {
		if (canton != null) {
			if (canton.length() < 2) {
				throw new FormValidationException("Le canton doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer une canton.");
		}

	}

	private void validationContent(String content) throws FormValidationException {
		if (content != null) {
			if (content.length() < 2) {
				throw new FormValidationException("Les content doivent contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer des content.");
		}

	}

	private void validationTheme(String theme) throws FormValidationException {
		if (theme != null) {
			if (theme.length() < 2) {
				throw new FormValidationException("Le theme doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un theme.");
		}

	}

	private void validationWebsite(String website) throws FormValidationException {
		if (website != null) {
			if (website.length() < 2) {
				throw new FormValidationException("Le website doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un website.");
		}

	}

	private void validationIdentifiant(String valideur) throws FormValidationException {
		if (valideur != null) {
			if (valideur.length() < 2) {
				throw new FormValidationException("Le name du valideur doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer le name du valideur.");
		}

	}

	private void validationForename(String forename) throws FormValidationException {
		if (forename != null && forename.length() < 2) {
			throw new FormValidationException("Le prèname d'user doit contenir au moins 2 caractères.");
		}
	}

	private void validationTelephone(String telephone) throws FormValidationException {
		System.err.println(telephone);
		if (telephone != null) {
			if (!telephone.matches("^\\d+$")) {
				throw new FormValidationException("Le numèro de tèlèphone doit uniquement contenir des chiffres.");
			} else if (telephone.trim().isEmpty() || telephone.length() < 4) {
				throw new FormValidationException("Le numèro de tèlèphone doit contenir au moins 4 chiffres.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un numèro de tèlèphone.");
		}
	}

	private void validationEmail(String email) throws FormValidationException {
		if (email != null && !email.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
			throw new FormValidationException("Merci de saisir une address mail valide.");
		}
	}

	private void validationImage(String image) throws FormValidationException {
		if (image != null && image.length() < 2) {
			throw new FormValidationException("Le chemin vers l'image doit contenir au moins 2 caractères.");
		}
	}

	private String validationImage(HttpServletRequest request, String chemin) throws FormValidationException {
		/*
		 * Rècupèration du contenu du champ image du formulaire. Il faut ici
		 * utiliser la mèthode getPart().
		 */

		String nameFichier = null;
		InputStream contenuFichier = null;
		try {
			Part part = request.getPart("image");
			nameFichier = getNameFichier(part);

			/*
			 * Si la mèthode getNameFichier() a renvoyè quelque chose, il
			 * s'agit donc d'un champ de statut fichier (input statut="file").
			 */
			if (nameFichier != null && !nameFichier.isEmpty()) {
				/*
				 * Antibug pour Internet Explorer, qui transmet pour une raison
				 * mystique le chemin du fichier local è la machine du
				 * cwebsitet...
				 * 
				 * Ex : C:/dossier/sous-dossier/fichier.ext
				 * 
				 * On doit donc faire en sorte de ne sèlectionner que le name
				 * et l'extension du fichier, et de se dèbarrasser du superflu.
				 */
				nameFichier = nameFichier.substring(nameFichier.lastIndexOf('/') + 1)
						.substring(nameFichier.lastIndexOf('\\') + 1);

				/* Rècupèration du contenu du fichier */
				contenuFichier = part.getInputStream();
				// check si le stream supporte les methodes mark/reset
				if (!contenuFichier.markSupported()) {
					throw new RuntimeException("Mark/Reset non-supportè!");
				}

				/* Extraction du statut MIME du fichier depuis l'InputStream */
				MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
				Collection<?> mimeTypes = MimeUtil.getMimeTypes(contenuFichier);

				/*
				 * Si le fichier est bien une image, alors son en-tète MIME
				 * commence par la chaène "image"
				 */
				if (mimeTypes.toString().startsWith("image")) {
					/* ècriture du fichier sur le disque */
					ecrireFichier(contenuFichier, nameFichier, chemin);
				} else {
					throw new FormValidationException("Le fichier envoyè doit ètre une image.");
				}
			}
		} catch (IllegalStateException e) {
			/*
			 * Exception retournèe si la taille des donnèes dèpasse les
			 * limites dèfinies dans la section <multipart-config> de la
			 * dèclaration de notre servlet d'upload dans le fichier web.xml
			 */
			e.printStackTrace();
			throw new FormValidationException("Le fichier envoyè ne doit pas dèpasser 1Mo.");
		} catch (IOException e) {
			/*
			 * Exception retournèe si une erreur au niveau des rèpertoires de
			 * stockage survient (rèpertoire inexistant, droits d'accès
			 * insuffisants, etc.)
			 */
			e.printStackTrace();
			throw new FormValidationException("Erreur de configuration du serveur.");
		} catch (ServletException e) {
			/*
			 * Exception retournèe si la requète n'est pas de statut
			 * multipart/form-data.
			 */
			e.printStackTrace();
			throw new FormValidationException(
					"Ce statut de requète n'est pas supportè, merci d'utiliser le formulaire prèvu pour envoyer votre fichier.");
		}

		return nameFichier;
	}

	/*
	 * Mèthode utilitaire qui a pour unique but d'analyser l'en-tète
	 * "content-disposition", et de vèrifier si le paramètre "filename" y est
	 * prèsent. Si oui, alors le champ traitè est de statut File et la
	 * mèthode retourne son name, sinon il s'agit d'un champ de formulaire
	 * classique et la mèthode retourne null.
	 */
	private static String getNameFichier(Part part) {
		/*
		 * Boucle sur chacun des paramètres de l'en-tète
		 * "content-disposition".
		 */
		for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
			/* Recherche de l'èventuelle prèsence du paramètre "filename". */
			if (contentDisposition.trim().startsWith("filename")) {
				/*
				 * Si "filename" est prèsent, alors renvoi de sa valeur,
				 * c'est-è-dire du name de fichier sans guillemets.
				 */
				return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		/* Et pour terminer, si rien n'a ètè trouvè... */
		return null;
	}

	/*
	 * Mèthode utilitaire qui a pour but d'ècrire le fichier passè en
	 * paramètre sur le disque, dans le rèpertoire donnè et avec le name
	 * donnè.
	 */
	private void ecrireFichier(InputStream contenuFichier, String nameFichier, String chemin)
			throws FormValidationException {
		/* Prèpare les flux. */
		BufferedInputStream entree = null;
		BufferedOutputStream sortie = null;
		try {
			/* Ouvre les flux. */
			entree = new BufferedInputStream(contenuFichier, TAILLE_TAMPON);
			sortie = new BufferedOutputStream(new FileOutputStream(new File(chemin + nameFichier)), TAILLE_TAMPON);

			/*
			 * Lit le fichier reèu et ècrit son contenu dans un fichier sur le
			 * disque.
			 */
			byte[] tampon = new byte[TAILLE_TAMPON];
			int longueur = 0;
			while ((longueur = entree.read(tampon)) > 0) {
				sortie.write(tampon, 0, longueur);
			}
		} catch (Exception e) {
			throw new FormValidationException("Erreur lors de l'ècriture du fichier sur le disque.");
		} finally {
			try {
				sortie.close();
			} catch (IOException ignore) {
			}
			try {
				entree.close();
			} catch (IOException ignore) {
			}
		}
	}

	/*
	 * Mèthode utilitaire qui retourne null si un champ est vide, et son
	 * contenu sinon.
	 */
	protected static String getValueField(HttpServletRequest request, String nameField) {
		String valeur = request.getParameter(nameField);
		if (valeur == null || valeur.trim().length() == 0) {
			return null;
		} else {
			return valeur;
		}
	}

	protected void processName(Address address, String name) {
		try {
			validationName(name);
		} catch (FormValidationException e) {
			setErreur(FIELD_NAME_ADDRESS, e.getMessage());
		}
		address.setName(name);

	}

	protected void processId(User user, String id) {

		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			user.setId(i);
		}

	}

	protected void processId(Weather weather, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			weather.setId(i);
		}
	}

	protected void processId(Institution institution, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			institution.setId(i);
		}
	}

	protected void processId(Canton canton, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			canton.setId(i);
		}
	}

	protected void processId(Localisation localisation, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			localisation.setId(i);
		}
	}

	protected void processId(Manifestation manifestation, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			manifestation.setId(id);
		}
	}

	protected void processId(Query droit, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			droit.setId(i);
		}

	}

	protected void processId(Address address, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			address.setId(i);
		}

	}

	protected void processId(Holiday holiday, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ID, e.getMessage());
			}
			holiday.setId(i);
		}

	}

	protected void processAddress(User user, Address address) {
		if (address == null) {
			setErreur(FIELD_NAME_ADDRESS, "Localisation unknowne.");

		} else
			user.setAddress(address);

	}

	protected void processSuperviseur(User user, User superviseur) {
		if (superviseur == null) {
			setErreur(FIELD_NAME_SUPERVISOR, "Superieur hièrarchique unknown.");

		} else
			user.setSuperviseur(superviseur);

	}

	protected void processAcronym(Canton canton, String acronym) {
		try {
			validationName(acronym);
		} catch (FormValidationException e) {
			setErreur(FIELD_ACRONYM, e.getMessage());
		}
		canton.setAcronym(acronym);

	}

	protected void processParent(Localisation localisation, Localisation parent) {
		if (parent == null) {
			setErreur(FIELD_NAME_COMITE, "Comite unknown.");

		}
		localisation.setParent(parent);

	}

	protected void processActive(User user, Boolean active) {
		try {
			validationBoolean(active);
		} catch (FormValidationException e) {
			setErreur(FIELD_ACTIVE, e.getMessage());
		}
		user.setActive(active);

	}

	protected void processAdmin(User user, Boolean admin) {
		try {
			validationBoolean(admin);
		} catch (FormValidationException e) {
			setErreur(FIELD_ADMIN, e.getMessage());
		}
		user.setAdmin(admin);

	}

	protected void processSexe(User user, String sexe) {
		try {
			validationSexe(sexe);
			user.setSexe(sexe);
		} catch (FormValidationException e) {
			setErreur(FIELD_SEXE, e.getMessage());
		}

	}

	protected void processTitle(Manifestation manifestation, String title) {
		try {
			validationTitle(title);
		} catch (FormValidationException e) {
			setErreur(FIELD_TITLE, e.getMessage());
		}
		manifestation.setTitle(title);

	}

	protected void processDescription(Manifestation manifestation, String contenu) {
		try {
			validationContent(contenu);
		} catch (FormValidationException e) {
			setErreur(FIELD_CONTENU, e.getMessage());
		}
		manifestation.setDescription(contenu);

	}

	protected void processDate(Query evenement, String endTime) {
		Date d = null;

		try {
			if (endTime != null && endTime.length() > 2) {
				if (!endTime.trim().isEmpty())
					d = format(endTime);
				validationDate(d);
				evenement.setDate(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(FIELD_START_TIME, e.getMessage());
		}

	}

	private Date format(String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		Date d = null;
		try {

			d = sdf.parse(endTime);
		} catch (ParseException e) {
			sdf = new SimpleDateFormat("dd/MM/yy");
			try {
				endTime = endTime.replace('.', '/');
				endTime = endTime.replace('-', '/');

				d = sdf.parse(endTime);
			} catch (ParseException e1) {
				throw new DatabaseException(e1);
			}

		}
		return d;

	}

	protected void processDateAnniversaire(User user, String endTimeAnniversaire) {
		/* Validation du champ endTime de debut de canton. */
		Date d = null;

		try {
			if (endTimeAnniversaire != null && endTimeAnniversaire.length() > 2) {
				if (!endTimeAnniversaire.trim().isEmpty())
					d = format(endTimeAnniversaire);
				validationDate(d);
				user.setDateAnniversaire(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(FIELD_START_TIME_ANNIVERSAIRE, e.getMessage());
		}

	}

	protected void processInitiales(User user, String initiales) {
		/* Validation du champ initiales. */
		try {
			validationInitiales(initiales);
			user.setInitiales(initiales);
		} catch (FormValidationException e) {
			setErreur(FIELD_INITIALES, e.getMessage());
		}

	}

	protected void processConfirmation(User user, String confirmation) {
		try {
			validationConfirmation(confirmation, user.getMdp());
		} catch (FormValidationException e) {
			setErreur(FIELD_CONF, e.getMessage());
		}

	}

	protected void processMdp(User user, String mdp) {

		/* Validation du champ mot de passe. */
		try {
			validationMotDePasse(mdp);
		} catch (FormValidationException e) {
			setErreur(FIELD_PASS, e.getMessage());
		}
		user.setMdp(mdp);

	}

	protected void processName(User user, String name) {
		try {
			validationName(name);
		} catch (FormValidationException e) {
			setErreur(FIELD_NAME, e.getMessage());
		}
		user.setName(name);
	}

	protected void processName(Institution institution, String name) {
		try {
			validationTitle(name);
		} catch (FormValidationException e) {
			setErreur(FIELD_NAME, e.getMessage());
		}
		institution.setName(name);

	}

	protected void processName(Canton canton, String name) {
		try {
			validationTitle(name);
		} catch (FormValidationException e) {
			setErreur(FIELD_NAME, e.getMessage());
		}
		canton.setName(name);

	}

	protected void processAcronyme(Localisation localisation, String acronyme) {
		try {
			validationName(acronyme);
		} catch (FormValidationException e) {
			setErreur(FIELD_ACRONYM, e.getMessage());
		}
		localisation.setAcronyme(acronyme);

	}

	private void validationNoPlaques(String name) throws FormValidationException {
		if (name != null) {
			if (name.length() < 2) {
				throw new FormValidationException("Le name d'user doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un name d'user.");
		}
	}

	private void validationNumero(String numero) throws FormValidationException {
		if (numero != null) {
			if (numero.length() < 2) {
				throw new FormValidationException("Le numero doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un numero.");
		}
	}

	protected void processForename(User user, String forename) {
		try {
			validationForename(forename);
		} catch (FormValidationException e) {
			setErreur(FIELD_FORENAME, e.getMessage());
		}
		user.setForename(forename);
	}

	protected void processDescription(Localisation localisation, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(FIELD_DESCRIPTION, e.getMessage());
		}
		localisation.setDescription(description);

	}

	protected void processDescription(Institution institution, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(FIELD_DESCRIPTION, e.getMessage());
		}
		institution.setDescription(description);

	}

	protected void processDescription(Canton canton, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(FIELD_DESCRIPTION, e.getMessage());
		}
		canton.setDescription(description);

	}

	protected void processWebsite(Institution institution, String website) {
		try {
			validationWebsite(website);
		} catch (FormValidationException e) {
			setErreur(FIELD_LIEN, e.getMessage());
		}
		institution.setWebsite(website);

	}

	protected void processWebsite(Canton canton, String website) {
		try {
			validationWebsite(website);
		} catch (FormValidationException e) {
			setErreur(FIELD_LIEN, e.getMessage());
		}
		canton.setWebsite(website);

	}

	protected void processContent(Weather weather, String content) {
		try {
			validationContent(content);
		} catch (FormValidationException e) {
			setErreur(FIELD_COMMENTAIRES, e.getMessage());
		}
		weather.setContent(content);

	}

	protected void processCanton(Address address, String canton) {
		try {
			validationCanton(canton);
		} catch (FormValidationException e) {
			setErreur(FIELD_CANTON, e.getMessage());
		}
		address.setCanton(canton);

	}

	protected void processForenameSuperviseur(User superviseur, String forenameSuperviseur) {
		try {
			validationForename(forenameSuperviseur);
		} catch (FormValidationException e) {
			setErreur(FIELD_FORENAME_SUPERVISOR, e.getMessage());
		}
		superviseur.setForename(forenameSuperviseur);

	}

	protected void processNameSuperviseur(User superviseur, String nameSuperviseur) {
		try {
			validationName(nameSuperviseur);
		} catch (FormValidationException e) {
			setErreur(FIELD_NAME_SUPERVISOR, e.getMessage());
		}
		superviseur.setName(nameSuperviseur);

	}

	protected void processTelInterne(User user, String telephone) {
		scanner = new Scanner(telephone);
		try {

			scanner.useDelimiter("/");
			if (scanner.hasNext()) {
				// assumes the line has a certain structure
				telephone = scanner.next();

			}

			telephone = telephone.replaceAll("\\W", "").trim();

			if (telephone == null) {
				setErreur(FIELD_TELINTERNE, "Numèro de telephone incorrect.");

			} else {
				validationTelephone(telephone);

				user.setTelInterne(telephone);
			}
		} catch (FormValidationException e) {
			setErreur(FIELD_TELINTERNE, e.getMessage());
		}

	}

	protected void processTelephone(Address address, String telephone) {
		scanner = new Scanner(telephone);
		try {

			scanner.useDelimiter("/");
			if (scanner.hasNext()) {
				// assumes the line has a certain structure
				telephone = scanner.next();

			}

			telephone = telephone.replaceAll("\\W", "").trim();

			if (telephone == null) {
				setErreur(FIELD_TELEPHONE, "Numèro de telephone incorrect.");

			} else {
				validationTelephone(telephone);
				address.setTelephone(telephone);
			}
		} catch (FormValidationException e) {
			setErreur(FIELD_TELEPHONE, e.getMessage());
		}

	}

	protected void processFax(Address address, String fax) {
		try {

			if (fax == null) {
				setErreur(FIELD_FAX, "Numèro de telephone incorrect.");

			} else {
				validationTelephone(fax);
				address.setFax(fax);
			}
		} catch (FormValidationException e) {
			setErreur(FIELD_FAX, e.getMessage());
		}

	}

	protected void processTelPrive(User user, String telephone) {
		scanner = new Scanner(telephone);
		try {

			scanner.useDelimiter("/");
			if (scanner.hasNext()) {
				// assumes the line has a certain structure
				telephone = scanner.next();

			}

			telephone = telephone.replaceAll("\\W", "").trim();

			if (telephone == null) {
				setErreur(FIELD_TELINTERNE, "Numèro de telephone incorrect.");

			} else {
				validationTelephone(telephone);
				user.setTelPrive(telephone);
			}
		} catch (FormValidationException e) {
			setErreur(FIELD_TELINTERNE, e.getMessage());
		}

	}

	protected void processEmail(User user, String email) {
		try {
			validationEmail(email);
			user.setEmail(email);
		} catch (FormValidationException e) {
			setErreur(FIELD_EMAIL, e.getMessage());
		}

	}

	protected void processActivites(User user, String activites) {
		try {

			validationDescription(activites);
			user.setActivites(activites.replace("<br>", "\r"));
		} catch (FormValidationException e) {
			setErreur(FIELD_ACTIVITES, e.getMessage());
		}

	}

	protected void processImage(User user, HttpServletRequest request, String chemin) {
		String image = null;
		try {
			image = validationImage(request, chemin);
		} catch (FormValidationException e) {
			setErreur(FIELD_IMAGE, e.getMessage());
		}
		user.setImage(image);
	}

	protected void processImage(User user, String image) {
		try {
			validationImage(image);
		} catch (FormValidationException e) {
			setErreur(FIELD_IMAGE, e.getMessage());
		}
		user.setImage(image);

	}

	protected void processReponse(User user, String reponse) {
		try {
			validationReponse(reponse);
		} catch (FormValidationException e) {
			setErreur(FIELD_REPONSE, e.getMessage());
		}
		user.setReponse(reponse);

	}

	protected void processQuestion(User user, String question) {
		try {
			validationQuestion(question);
		} catch (FormValidationException e) {
			setErreur(FIELD_QUESTION, e.getMessage());
		}
		user.setQuestion(question);

	}

	protected void processNumber(Address address, Integer number) {
		try {
			validationNo(number);
		} catch (FormValidationException e) {
			setErreur(FIELD_NUMBER, e.getMessage());
		}
		address.setNumber(number);

	}

	protected void processNumber(Address address, String number) {
		if (number != null && !number.trim().isEmpty()) {
			Integer i = Integer.parseInt(number);

			try {
				validationNumber(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_NUMBER, e.getMessage());
			}
			address.setNumber(i);
		} else
			address.setNumber(0);

	}

	protected void processTaux(User user, String taux) {
		if (taux != null && !taux.trim().isEmpty()) {
			Integer i = Integer.parseInt(taux);

			try {
				validationNumber(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_TAUX, e.getMessage());
			}
			user.setTaux(i);
		} else
			user.setTaux(0);

	}

	protected void processEtage(Address address, String etage) {
		if (etage != null && !etage.trim().isEmpty()) {
			Integer i = Integer.parseInt(etage);

			try {
				validationNumber(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_ETAGE, e.getMessage());
			}
			address.setEtage(i);
		} else
			address.setEtage(0);

	}

	protected void processNpa(Address address, String npa) {
		if (npa != null && !npa.trim().isEmpty()) {
			Integer i = Integer.parseInt(npa);

			try {
				validationNpa(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_NPA, e.getMessage());
			}
			address.setNpa(i);
		} else
			address.setNpa(0);

	}

	protected void processCp(Address address, String cp) {
		if (cp != null && !cp.trim().isEmpty()) {
			Integer i = Integer.parseInt(cp);

			try {
				validationCp(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_CP, e.getMessage());
			}
			address.setCp(i);
		} else
			address.setCp(0);

	}

	protected void processStatut(Localisation localisation, String statut) {
		if (statut != null && !statut.trim().isEmpty()) {
			Boolean i = Boolean.parseBoolean(statut);

			try {
				validationBoolean(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_STATUT, e.getMessage());
			}
			localisation.setStatut(i);
		} else
			localisation.setStatut(Boolean.FALSE);

	}

	protected void processOfficiel(Localisation localisation, String officiel) {
		if (officiel != null && !officiel.trim().isEmpty()) {
			Boolean i = Boolean.parseBoolean(officiel);

			try {
				validationBoolean(i);
			} catch (FormValidationException e) {
				setErreur(FIELD_OFFICIEL, e.getMessage());
			}
			localisation.setOfficiel(i);
		} else
			localisation.setOfficiel(Boolean.FALSE);

	}

	private void validationNo(Integer no) throws FormValidationException {
		if (no == null) {
			throw new FormValidationException("Numero invalide.");
		}

	}

	protected void processNpa(Address address, Integer npa) {
		try {
			validationNo(npa);
		} catch (FormValidationException e) {
			setErreur(FIELD_NPA, e.getMessage());
		}
		address.setNpa(npa);

	}

	protected void processType(Address address, String type) {
		try {
			validationName(type);
		} catch (FormValidationException e) {
			setErreur(FIELD_TYPE, e.getMessage());
		}
		address.setType(type);

	}

	protected void processType(Institution institution, String type) {
		try {
			validationName(type);
		} catch (FormValidationException e) {
			setErreur(FIELD_TYPE, e.getMessage());
		}
		institution.setType(type);

	}

	protected void processStreet(Address address, String street) {
		try {
			validationName(street);
		} catch (FormValidationException e) {
			setErreur(FIELD_STREET, e.getMessage());
		}
		address.setStreet(street);

	}

	protected void processCountry(Address address, String country) {
		try {
			validationName(country);
		} catch (FormValidationException e) {
			setErreur(FIELD_COUNTRY, e.getMessage());
		}
		address.setCountry(country);

	}

	protected void processCity(Address address, String city) {
		try {
			validationName(city);
		} catch (FormValidationException e) {
			setErreur(FIELD_CITY, e.getMessage());
		}
		address.setCity(city);

	}

	protected void processWebsite(Localisation localisation, String website) {
		try {
			validationName(website);
		} catch (FormValidationException e) {
			setErreur(FIELD_WEBSITE, e.getMessage());
		}
		localisation.setWebsite(website);

	}

	protected void processUser(Manifestation manifestation, User user) {
		if (user == null) {
			setErreur(FIELD_NAME, "User unknown.");

		}
		// manifestation.setUser(user);

	}

	protected void processEndTime(Manifestation manifestation, String endTime) {
		Date d = null;

		try {
			if (endTime != null && endTime.length() > 2) {
				if (!endTime.trim().isEmpty())
					d = format(endTime);
				validationDate(d);
				if (manifestation.getStartTime() != null
						&& manifestation.getStartTime().compareTo(new DateTime(d)) > 0) {
					throw new FormValidationException("La date de fin est incorrecte.");
				}
				manifestation.setEndTime(new DateTime(d));

			}
		} catch (FormValidationException e) {
			setErreur(FIELD_END_TIME, e.getMessage());
		}
	}

	protected void processStartTime(Manifestation manifestation, String startTime) {
		/* Validation du champ date de debut de institution. */
		Date d = null;

		try {
			if (startTime != null && startTime.length() > 2) {
				if (!startTime.trim().isEmpty())
					d = format(startTime);
				validationDate(d);
				manifestation.setStartTime(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(FIELD_START_TIME, e.getMessage());
		}

	}

	protected void processAddress(Manifestation manifestation, Address address) {
		if (address == null) {
			setErreur(FIELD_NAME_ADDRESS, "Localisation unknowne.");

		}
		manifestation.setAddress(address);

	}

	protected void processInstitution(Manifestation manifestation, Institution institution) {
		if (institution == null) {
			setErreur(FIELD_NAME_INSTITUTION, "Institution unknown.");

		}
		manifestation.setInstitution(institution);

	}

	protected void processImage(Manifestation manifestation, HttpServletRequest request, String chemin) {
		String image = null;
		try {
			image = validationImage(request, chemin);
		} catch (FormValidationException e) {
			setErreur(FIELD_IMAGE, e.getMessage());
		}
		manifestation.setImage(image);
	}
}
