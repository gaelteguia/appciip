package forms;

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

import modele.ApplicationManager;
import modele.BeamerManager;
import modele.BreveManager;
import modele.DatabaseException;
import modele.DirectiveManager;
import modele.DistributionManager;
import modele.SeanceManager;
import modele.AdresseManager;
import modele.AnnonceManager;
import modele.FonctionManager;
import modele.FournisseurManager;
import modele.HoraireManager;
import modele.ImprimanteManager;
import modele.ManifestationManager;
import modele.OutilManager;
import modele.PartenaireManager;
import modele.SalleManager;
import modele.EvenementManager;
import modele.ComiteManager;
import modele.CompteRenduManager;
import modele.ServiceManager;
import modele.StationManager;
import modele.TelephoneManager;
import modele.VoitureManager;
import modele.CollaborateurManager;

import org.joda.time.DateTime;

import beans.Application;
import beans.Beamer;
import beans.Breve;
import beans.Seance;
import beans.Adresse;
import beans.Annonce;
import beans.Fonction;
import beans.Fournisseur;
import beans.Horaire;
import beans.Imprimante;
import beans.Manifestation;
import beans.Outil;
import beans.Partenaire;
import beans.Salle;
import beans.Evenement;
import beans.Comite;
import beans.CompteRendu;
import beans.Directive;
import beans.Distribution;
import beans.Service;
import beans.Station;
import beans.Statut;
import beans.Telephone;
import beans.Voiture;
import beans.Collaborateur;
import eu.medsea.mimeutil.MimeUtil;

public abstract class Form {
	protected CollaborateurManager u;
	protected ApplicationManager a;
	protected SeanceManager d;
	protected AdresseManager l;
	protected FonctionManager m;
	protected EvenementManager n;
	protected ComiteManager p;
	protected ServiceManager s;
	protected VoitureManager v;
	protected HoraireManager h;
	protected FournisseurManager f;
	protected BeamerManager b;
	protected SalleManager sa;
	protected ImprimanteManager i;
	protected StationManager st;
	protected TelephoneManager t;
	protected DistributionManager di;

	protected DirectiveManager dir;

	protected OutilManager o;

	protected BreveManager br;
	protected CompteRenduManager cr;
	protected PartenaireManager pa;

	protected AnnonceManager an;

	protected ManifestationManager ma;

	protected static final String CHAMP_CHOIX_UTILISATEUR = "choixNouvelCollaborateur";
	protected static final String CHAMP_CHOIX_CONTACT_FONCTIONNEL = "choixNouveauContactFonctionnel";
	protected static final String CHAMP_CHOIX_CONTACT_TECHNIQUE = "choixNouveauContactTechnique";
	protected static final String CHAMP_LISTE_UTILISATEURS = "listeCollaborateurs";
	protected static final String CHAMP_LISTE_CONTACT_FONCTIONNELS = "listeContactFonctionnels";
	protected static final String CHAMP_LISTE_CONTACT_TECHNIQUES = "listeContactTechniques";
	protected static final String ANCIEN_UTILISATEUR = "ancienCollaborateur";
	protected static final String ANCIEN_CONTACT_TECHNIQUE = "ancienContactTechnique";
	protected static final String ANCIEN_CONTACT_FONCTIONNEL = "ancienContactFonctionnel";
	protected static final String SESSION_UTILISATEURS = "collaborateurs";

	protected static final String CHAMP_CHOIX_SERVICE = "choixNouveauService";
	protected static final String ANCIEN_SERVICE = "ancienService";
	protected static final String CHAMP_LISTE_SERVICES = "listeServices";
	protected static final String SESSION_SERVICES = "services";

	protected static final String CHAMP_CHOIX_METIER = "choixNouveauFonction";
	protected static final String ANCIEN_METIER = "ancienFonction";
	protected static final String CHAMP_LISTE_METIERS = "listeFonctions";
	protected static final String SESSION_METIERS = "metiers";

	protected static final String CHAMP_LISTE_COMITES = "listeProfils";
	protected static final String ANCIEN_COMITE = "ancienComite";
	protected static final String CHAMP_CHOIX_COMITE = "choixNouveauProfil";
	protected static final String SESSION_COMITES = "comites";

	protected static final String CHAMP_LISTE_DROITS = "listeDroits";
	protected static final String CHAMP_CHOIX_DROIT = "choixNouveauDroit";
	protected static final String SESSION_DROITS = "droits";

	protected static final String CHAMP_LISTE_APPLICATIONS = "listeApplications";
	protected static final String CHAMP_CHOIX_APPLICATION = "choixNouvelleApplication";
	protected static final String SESSION_APPLICATIONS = "applications";
	protected static final String ANCIENNE_APPLICATION = "ancienneApplication";

	protected static final String CHAMP_LISTE_EVENEMENTS = "listeEvenements";
	protected static final String CHAMP_CHOIX_EVENEMENT = "choixNouvelleEvenement";
	protected static final String SESSION_EVENEMENTS = "evenements";

	protected static final String CHAMP_LISTE_MANIFESTATIONS = "listeManifestations";
	protected static final String CHAMP_CHOIX_MANIFESTATION = "choixNouvelleManifestation";
	protected static final String SESSION_MANIFESTATIONS = "manifestations";

	protected static final String CHAMP_LISTE_BEAMERS = "listeBeamers";
	protected static final String CHAMP_CHOIX_BEAMER = "choixNouveauBeamer";
	protected static final String SESSION_BEAMERS = "beamers";

	protected static final String CHAMP_CHOIX_ADRESSE = "choixNouvelleAdresse";
	protected static final String ANCIENNE_ADRESSE = "ancienneAdresse";
	protected static final String CHAMP_LISTE_ADRESSES = "listeAdresses";
	protected static final String SESSION_ADRESSES = "adresses";

	protected static final String CHAMP_CHOIX_DIRECTIVE = "choixNouvelleDirective";
	protected static final String ANCIENNE_DIRECTIVE = "ancienneDirective";
	protected static final String CHAMP_LISTE_DIRECTIVES = "listeDirectives";
	protected static final String SESSION_DIRECTIVES = "directives";

	protected static final String CHAMP_CHOIX_VOITURE = "choixNouvelleVoiture";
	protected static final String ANCIENNE_VOITURE = "ancienneVoiture";
	protected static final String CHAMP_LISTE_VOITURES = "listeVoitures";
	protected static final String SESSION_VOITURES = "voitures";

	protected static final String CHAMP_CHOIX_SALLE = "choixNouvelleSalle";
	protected static final String ANCIENNE_SALLE = "ancienneSalle";
	protected static final String CHAMP_LISTE_SALLES = "listeSalles";
	protected static final String SESSION_SALLES = "salles";

	protected static final String CHAMP_CHOIX_SEANCE = "choixNouvelleSeance";
	protected static final String ANCIENNE_SEANCE = "ancienneSeance";
	protected static final String CHAMP_LISTE_SEANCES = "listeSeances";
	protected static final String SESSION_SEANCES = "seances";

	protected static final String CHAMP_CHOIX_ANNONCE = "choixNouvelleAnnonce";
	protected static final String ANCIENNE_ANNONCE = "ancienneAnnonce";
	protected static final String CHAMP_LISTE_ANNONCES = "listeAnnonces";
	protected static final String SESSION_ANNONCES = "annonces";

	protected static final String CHAMP_ID = "id";
	protected static final String CHAMP_NOM = "nom";
	protected static final String CHAMP_PRENOM = "prenom";
	protected static final String CHAMP_TELEPHONE = "telephone";
	protected static final String CHAMP_FAX = "fax";
	protected static final String CHAMP_CP = "cp";
	protected static final String CHAMP_TELINTERNE = "telinterne";
	protected static final String CHAMP_EMAIL = "email";
	protected static final String CHAMP_IMAGE = "image";
	protected static final String CHAMP_INITIALES = "initiales";
	protected static final String CHAMP_PASS = "mdp";
	protected static final String CHAMP_CONF = "confirmation";
	protected static final String CHAMP_QUESTION = "question";
	protected static final String CHAMP_REPONSE = "reponse";
	protected static final String CHAMP_STATUT = "statut";
	protected static final String CHAMP_SEXE = "sexe";
	protected static final String CHAMP_ADMIN = "admin";
	protected static final String CHAMP_ACTIVITES = "activites";
	protected static final String CHAMP_DATE_ANNIVERSAIRE = "dateanniversaire";
	protected static final String CHAMP_DATE_DEBUT_SERVICE = "datedebutservice";
	protected static final String CHAMP_DATE_FIN_SERVICE = "datefinservice";
	protected static final String CHAMP_DATE_FIN_EFFECTIVE = "datefineffective";
	protected static final String CHAMP_DATE_ACHAT = "dateachat";
	protected static final String CHAMP_DATE_PEREMPTION = "dateperemption";
	protected static final String CHAMP_NOM_SUPERVISEUR = "nomsuperviseur";
	protected static final String CHAMP_NOM_CONTACT_FONCTIONNEL = "nomcontactfonctionnel";
	protected static final String CHAMP_NOM_CONTACT_TECHNIQUE = "nomcontacttechnique";
	protected static final String CHAMP_NOM_CONTACT_INTERNE = "nomcontactinterne";
	protected static final String CHAMP_PRENOM_SUPERVISEUR = "prenomsuperviseur";
	protected static final String CHAMP_NOM_SERVICE = "nomservice";
	protected static final String CHAMP_NOM_METIER = "nommetier";
	protected static final String CHAMP_NOM_ADRESSE = "nomadresse";
	protected static final String CHAMP_NOM_HORAIRE = "nomhoraire";
	protected static final String CHAMP_NOM_APPLICATION = "nomapplication";
	protected static final String CHAMP_NOM_FOURNISSEUR = "nomfournisseur";
	protected static final String CHAMP_NOM_COMITE = "nomcomite";
	protected static final String CHAMP_NOM_BEAMER = "nombeamer";
	protected static final String CHAMP_NOM_SALLE = "nomsalle";
	protected static final String CHAMP_NOM_OUTIL = "nomoutil";

	protected static final String CHAMP_ACTIF = "actif";
	protected static final String CHAMP_VILLE = "ville";
	protected static final String CHAMP_PAYS = "pays";
	protected static final String CHAMP_RUE = "rue";
	protected static final String CHAMP_CANTON = "canton";
	protected static final String CHAMP_TYPE = "type";
	protected static final String CHAMP_ETAGE = "etage";
	protected static final String CHAMP_NPA = "npa";
	protected static final String CHAMP_NORUE = "norue";
	protected static final String CHAMP_CAPACITE = "capacite";
	protected static final String CHAMP_NOPLAQUES = "noplaques";
	protected static final String CHAMP_NUMERO = "numero";
	protected static final String CHAMP_LIBELLE = "libelle";
	protected static final String CHAMP_PRIX = "prix";
	protected static final String CHAMP_DESCRIPTION = "description";
	protected static final String CHAMP_ECHEANCE = "echeance";
	protected static final String CHAMP_ACRONYME = "acronyme";
	protected static final String CHAMP_NOM_DEPARTEMENT = "nomdepartement";
	protected static final String CHAMP_IDENTIFIANT = "identifiant";
	protected static final String CHAMP_VALIDE = "valide";
	protected static final String CHAMP_VALIDEUR = "valideur";
	protected static final String CHAMP_DATE_VALIDATION = "datevalidation";
	protected static final String CHAMP_COMMENTAIRES = "commentaires";
	protected static final String CHAMP_TITRE = "titre";
	protected static final String CHAMP_CONTENU = "contenu";
	protected static final String CHAMP_DATE = "date";
	protected static final String CHAMP_MARQUE = "marque";
	protected static final String CHAMP_MODELE = "modele";
	protected static final String CHAMP_OS = "os";

	protected static final String CHAMP_TAUX = "taux";
	protected static final String CHAMP_AMORTISSEMENT = "amortissement";
	protected static final String CHAMP_OFFICIEL = "officiel";
	protected static final String CHAMP_THEME = "theme";
	protected static final String CHAMP_LIEN = "lien";
	protected static final String CHAMP_NOM_RESPONSABLE = "nomresponsable";
	protected static final String CHAMP_NOM_DISTRIBUTION = "nomdistribution";
	protected static final String CHAMP_NOM_DIRECTIVE = "nomdirective";

	protected static final String CHAMP_WEBSITE = "website";

	protected static final int TAILLE_TAMPON = 10240; // 10ko

	private String resultat;
	protected Map<String, String> erreurs = new HashMap<String, String>();
	private Scanner scanner;

	public Map<String, String> getErreurs() {
		return erreurs;
	}

	/*
	 * Ajoute un message correspondant au champ spècifiè è la map des erreurs.
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

	private void validationNoRue(Integer noRue) throws FormValidationException {

		if (noRue == null) {
			throw new FormValidationException("noRue invalide.");
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
			throw new FormValidationException("Valeur invalide.");
		}
	}

	private void validationStatut(Statut statut) throws FormValidationException {
		if (statut.name().length() < 2) {
			throw new FormValidationException("Le statut d'collaborateur doit contenir au moins 2 caractères.");
		}

	}

	private void validationSexe(String sexe) throws FormValidationException {
		if (sexe != null && sexe.length() < 1) {
			throw new FormValidationException("Le sexe d'collaborateur doit contenir au moins 1 caractères.");
		}

	}

	private void validationNom(String nom) throws FormValidationException {
		if (nom != null) {
			if (nom.trim().isEmpty() || nom.length() < 2) {
				throw new FormValidationException("Le nom doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un nom.");
		}
	}

	private void validationTitre(String titre) throws FormValidationException {
		if (titre != null) {
			if (titre.trim().isEmpty() || titre.length() < 2) {
				throw new FormValidationException("Le titre doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un titre.");
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
			throw new FormValidationException("Merci d'entrer un modele.");
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

	private void validationCommentaires(String commentaires) throws FormValidationException {
		if (commentaires != null) {
			if (commentaires.length() < 2) {
				throw new FormValidationException("Les commentaires doivent contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer des commentaires.");
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

	private void validationLien(String lien) throws FormValidationException {
		if (lien != null) {
			if (lien.length() < 2) {
				throw new FormValidationException("Le lien doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un lien.");
		}

	}

	private void validationIdentifiant(String valideur) throws FormValidationException {
		if (valideur != null) {
			if (valideur.length() < 2) {
				throw new FormValidationException("Le nom du valideur doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer le nom du valideur.");
		}

	}

	private void validationPrenom(String prenom) throws FormValidationException {
		if (prenom != null && prenom.length() < 2) {
			throw new FormValidationException("Le prènom d'collaborateur doit contenir au moins 2 caractères.");
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
			throw new FormValidationException("Merci de saisir une adresse mail valide.");
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

		String nomFichier = null;
		InputStream contenuFichier = null;
		try {
			Part part = request.getPart("image");
			nomFichier = getNomFichier(part);

			/*
			 * Si la mèthode getNomFichier() a renvoyè quelque chose, il s'agit
			 * donc d'un champ de statut fichier (input statut="file").
			 */
			if (nomFichier != null && !nomFichier.isEmpty()) {
				/*
				 * Antibug pour Internet Explorer, qui transmet pour une raison
				 * mystique le chemin du fichier local è la machine du client...
				 * 
				 * Ex : C:/dossier/sous-dossier/fichier.ext
				 * 
				 * On doit donc faire en sorte de ne sèlectionner que le nom et
				 * l'extension du fichier, et de se dèbarrasser du superflu.
				 */
				nomFichier = nomFichier.substring(nomFichier.lastIndexOf('/') + 1)
						.substring(nomFichier.lastIndexOf('\\') + 1);

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
					ecrireFichier(contenuFichier, nomFichier, chemin);
				} else {
					throw new FormValidationException("Le fichier envoyè doit ètre une image.");
				}
			}
		} catch (IllegalStateException e) {
			/*
			 * Exception retournèe si la taille des donnèes dèpasse les limites
			 * dèfinies dans la section <multipart-config> de la dèclaration de
			 * notre servlet d'upload dans le fichier web.xml
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

		return nomFichier;
	}

	/*
	 * Mèthode utilitaire qui a pour unique but d'analyser l'en-tète
	 * "content-disposition", et de vèrifier si le paramètre "filename" y est
	 * prèsent. Si oui, alors le champ traitè est de statut File et la mèthode
	 * retourne son nom, sinon il s'agit d'un champ de formulaire classique et
	 * la mèthode retourne null.
	 */
	private static String getNomFichier(Part part) {
		/*
		 * Boucle sur chacun des paramètres de l'en-tète "content-disposition".
		 */
		for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
			/* Recherche de l'èventuelle prèsence du paramètre "filename". */
			if (contentDisposition.trim().startsWith("filename")) {
				/*
				 * Si "filename" est prèsent, alors renvoi de sa valeur,
				 * c'est-è-dire du nom de fichier sans guillemets.
				 */
				return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		/* Et pour terminer, si rien n'a ètè trouvè... */
		return null;
	}

	/*
	 * Mèthode utilitaire qui a pour but d'ècrire le fichier passè en paramètre
	 * sur le disque, dans le rèpertoire donnè et avec le nom donnè.
	 */
	private void ecrireFichier(InputStream contenuFichier, String nomFichier, String chemin)
			throws FormValidationException {
		/* Prèpare les flux. */
		BufferedInputStream entree = null;
		BufferedOutputStream sortie = null;
		try {
			/* Ouvre les flux. */
			entree = new BufferedInputStream(contenuFichier, TAILLE_TAMPON);
			sortie = new BufferedOutputStream(new FileOutputStream(new File(chemin + nomFichier)), TAILLE_TAMPON);

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
	 * Mèthode utilitaire qui retourne null si un champ est vide, et son contenu
	 * sinon.
	 */
	protected static String getValeurChamp(HttpServletRequest request, String nomChamp) {
		String valeur = request.getParameter(nomChamp);
		if (valeur == null || valeur.trim().length() == 0) {
			return null;
		} else {
			return valeur;
		}
	}

	protected void traiterNom(Adresse adresse, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_ADRESSE, e.getMessage());
		}
		adresse.setNom(nom);

	}

	protected void traiterLibelle(Seance adresse, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_ADRESSE, e.getMessage());
		}
		adresse.setLibelle(nom);

	}

	protected void traiterMarque(Beamer beamer, String marque) {
		try {
			validationMarque(marque);
		} catch (FormValidationException e) {
			setErreur(CHAMP_MARQUE, e.getMessage());
		}
		beamer.setMarque(marque);

	}

	protected void traiterMarque(Imprimante imprimante, String marque) {
		try {
			validationMarque(marque);
		} catch (FormValidationException e) {
			setErreur(CHAMP_MARQUE, e.getMessage());
		}
		imprimante.setMarque(marque);

	}

	protected void traiterMarque(Station station, String marque) {
		try {
			validationMarque(marque);
		} catch (FormValidationException e) {
			setErreur(CHAMP_MARQUE, e.getMessage());
		}
		station.setMarque(marque);

	}

	protected void traiterModele(Station station, String modele) {
		try {
			validationModele(modele);
		} catch (FormValidationException e) {
			setErreur(CHAMP_MODELE, e.getMessage());
		}
		station.setModele(modele);

	}

	protected void traiterOs(Station station, String os) {
		try {
			validationOs(os);
		} catch (FormValidationException e) {
			setErreur(CHAMP_OS, e.getMessage());
		}
		station.setOs(os);

	}

	protected void traiterMarque(Telephone telephone, String marque) {
		try {
			validationMarque(marque);
		} catch (FormValidationException e) {
			setErreur(CHAMP_MARQUE, e.getMessage());
		}
		telephone.setMarque(marque);

	}

	protected void traiterNom(Horaire horaire, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_HORAIRE, e.getMessage());
		}
		horaire.setNom(nom);

	}

	protected void traiterNom(Fonction metier, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_METIER, e.getMessage());
		}
		metier.setNom(nom);

	}

	protected void traiterNom(Service service, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_SERVICE, e.getMessage());
		}
		service.setNom(nom);

	}

	protected void traiterNom(Partenaire partenaire, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_SERVICE, e.getMessage());
		}
		partenaire.setNom(nom);

	}

	protected void traiterNom(Outil outil, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_OUTIL, e.getMessage());
		}
		outil.setNom(nom);

	}

	protected void traiterNom(Salle salle, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_SALLE, e.getMessage());
		}
		salle.setNom(nom);

	}

	protected void traiterNom(Fournisseur service, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_FOURNISSEUR, e.getMessage());
		}
		service.setNom(nom);

	}

	protected void traiterNom(Distribution distribution, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_DISTRIBUTION, e.getMessage());
		}
		distribution.setNom(nom);

	}

	protected void traiterId(Utilisateur collaborateur, String id) {

		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			collaborateur.setId(i);
		}

	}

	protected void traiterId(Fonction metier, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			metier.setId(i);
		}
	}

	protected void traiterId(Service service, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			service.setId(i);
		}
	}

	protected void traiterId(Beamer beamer, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			beamer.setId(i);
		}
	}

	protected void traiterId(Imprimante imprimante, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			imprimante.setId(i);
		}
	}

	protected void traiterId(Station station, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			station.setId(i);
		}
	}

	protected void traiterId(Telephone telephone, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			telephone.setId(i);
		}
	}

	protected void traiterId(Annonce annonce, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			annonce.setId(i);
		}
	}

	protected void traiterId(Breve breve, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			breve.setId(i);
		}
	}

	protected void traiterId(CompteRendu compteRendu, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			compteRendu.setId(i);
		}
	}

	protected void traiterId(Partenaire partenaire, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			partenaire.setId(i);
		}
	}

	protected void traiterId(Outil outil, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			outil.setId(i);
		}
	}

	protected void traiterId(Directive directive, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			directive.setId(i);
		}
	}

	protected void traiterId(Fournisseur fournisseur, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			fournisseur.setId(i);
		}
	}

	protected void traiterId(Salle salle, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			salle.setId(i);
		}
	}

	protected void traiterId(Distribution distribution, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			distribution.setId(i);
		}
	}

	protected void traiterId(Localisation comite, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			comite.setId(i);
		}
	}

	protected void traiterId(Evenement evenement, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			evenement.setId(i);
		}
	}

	protected void traiterId(Manifestation manifestation, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			manifestation.setId(i);
		}
	}

	protected void traiterId(Seance droit, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			droit.setId(i);
		}

	}

	protected void traiterId(Application application, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			application.setId(i);
		}

	}

	protected void traiterId(Adresse adresse, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			adresse.setId(i);
		}

	}

	protected void traiterAmortissement(Beamer beamer, String amortissement) {
		if (amortissement != null && !amortissement.trim().isEmpty()) {
			Float i = Float.parseFloat(amortissement);

			try {
				validationFloat(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_AMORTISSEMENT, e.getMessage());
			}
			beamer.setAmortissement(i);
		}

	}

	protected void traiterAmortissement(Imprimante imprimante, String amortissement) {
		if (amortissement != null && !amortissement.trim().isEmpty()) {
			Float i = Float.parseFloat(amortissement);

			try {
				validationFloat(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_AMORTISSEMENT, e.getMessage());
			}
			imprimante.setAmortissement(i);
		}

	}

	protected void traiterAmortissement(Station station, String amortissement) {
		if (amortissement != null && !amortissement.trim().isEmpty()) {
			Float i = Float.parseFloat(amortissement);

			try {
				validationFloat(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_AMORTISSEMENT, e.getMessage());
			}
			station.setAmortissement(i);
		}

	}

	protected void traiterAmortissement(Telephone telephone, String amortissement) {
		if (amortissement != null && !amortissement.trim().isEmpty()) {
			Float i = Float.parseFloat(amortissement);

			try {
				validationFloat(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_AMORTISSEMENT, e.getMessage());
			}
			telephone.setAmortissement(i);
		}

	}

	protected void traiterId(Horaire horaire, String id) {
		if (id != null && !id.trim().isEmpty()) {
			Long i = Long.parseLong(id);

			try {
				validationId(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ID, e.getMessage());
			}
			horaire.setId(i);
		}

	}

	protected void traiterFonction(Utilisateur collaborateur, Fonction metier) {
		if (metier == null) {
			setErreur(CHAMP_NOM_METIER, "Profil mètier inconnu.");

		} else
			collaborateur.setFonction(metier);
	}

	protected void traiterAdresse(Utilisateur collaborateur, Adresse adresse) {
		if (adresse == null) {
			setErreur(CHAMP_NOM_ADRESSE, "Adresse inconnue.");

		} else
			collaborateur.setAdresse(adresse);

	}

	protected void traiterService(Utilisateur collaborateur, Service service) {
		if (service == null) {
			setErreur(CHAMP_NOM_SERVICE, "Service inconnu.");

		}
		collaborateur.setService(service);

	}

	protected void traiterService(Fonction metier, Service service) {
		if (service == null) {
			setErreur(CHAMP_NOM_SERVICE, "Service inconnu.");

		}
		metier.setService(service);
	}

	protected void traiterService(Outil outil, Service service) {
		if (service == null) {
			setErreur(CHAMP_NOM_SERVICE, "Service inconnu.");

		}
		outil.setService(service);
	}

	protected void traiterService(Breve breve, Service service) {
		if (service == null) {
			setErreur(CHAMP_NOM_SERVICE, "Service inconnu.");

		}
		breve.setService(service);
	}

	protected void traiterService(CompteRendu compteRendu, Service service) {
		if (service == null) {
			setErreur(CHAMP_NOM_SERVICE, "Service inconnu.");

		}
		compteRendu.setService(service);
	}

	protected void traiterCollaborateur(Evenement evenement, Utilisateur collaborateur) {
		if (collaborateur == null) {
			setErreur(CHAMP_NOM, "Collaborateur inconnu.");

		}
		evenement.setCollaborateur(collaborateur);

	}

	protected void traiterCollaborateur(Manifestation manifestation, Utilisateur collaborateur) {
		if (collaborateur == null) {
			setErreur(CHAMP_NOM, "Collaborateur inconnu.");

		}
		manifestation.setCollaborateur(collaborateur);

	}

	protected void traiterCollaborateur(CompteRendu compteRendu, Utilisateur collaborateur) {
		if (collaborateur == null) {
			setErreur(CHAMP_NOM, "Collaborateur inconnu.");

		}
		compteRendu.setCollaborateur(collaborateur);

	}

	protected void traiterAuteur(Annonce annonce, Utilisateur auteur) {
		if (auteur == null) {
			setErreur(CHAMP_NOM, "Collaborateur inconnu.");

		}
		annonce.setAuteur(auteur);

	}

	protected void traiterCollaborateur(Voiture voiture, Utilisateur collaborateur) {
		if (collaborateur == null) {
			setErreur(CHAMP_NOM, "Collaborateur inconnu.");

		}
		voiture.setCollaborateur(collaborateur);

	}

	protected void traiterCollaborateur(Telephone t, Utilisateur collaborateur) {
		if (collaborateur == null) {
			setErreur(CHAMP_NOM, "Collaborateur inconnu.");

		}
		t.setCollaborateur(collaborateur);

	}

	protected void traiterCollaborateur(Station station, Utilisateur collaborateur) {
		if (collaborateur == null) {
			setErreur(CHAMP_NOM, "Collaborateur inconnu.");

		}
		station.setCollaborateur(collaborateur);

	}

	protected void traiterSuperviseur(Utilisateur collaborateur, Utilisateur superviseur) {
		if (superviseur == null) {
			setErreur(CHAMP_NOM_SUPERVISEUR, "Superieur hièrarchique inconnu.");

		} else
			collaborateur.setSuperviseur(superviseur);

	}

	protected void traiterAcronyme(Service service, String acronyme) {
		try {
			validationNom(acronyme);
		} catch (FormValidationException e) {
			setErreur(CHAMP_ACRONYME, e.getMessage());
		}
		service.setAcronyme(acronyme);

	}

	protected void traiterIdentifiant(Fonction fonction, String identifiant) {
		try {
			validationIdentifiant(identifiant);
		} catch (FormValidationException e) {
			setErreur(CHAMP_IDENTIFIANT, e.getMessage());
		}
		fonction.setIdentifiant(identifiant);

	}

	protected void traiterDepartement(Service service, Service departement) {
		if (departement == null) {
			setErreur(CHAMP_NOM_DEPARTEMENT, "Departement inconnu.");

		}
		service.setDepartement(departement);

	}

	protected void traiterSuperviseur(Service service, Utilisateur superviseur) {
		if (superviseur == null) {
			setErreur(CHAMP_NOM_SUPERVISEUR, "Superieur hièrarchique inconnu.");

		}
		service.setSuperviseur(superviseur);

	}

	protected void traiterResponsable(Localisation comite, Utilisateur responsable) {
		if (responsable == null) {
			setErreur(CHAMP_NOM_RESPONSABLE, "Responsable inconnu.");

		}
		comite.setResponsable(responsable);

	}

	protected void traiterResponsable(Seance comite, Utilisateur responsable) {
		if (responsable == null) {
			setErreur(CHAMP_NOM_RESPONSABLE, "Responsable inconnu.");

		}
		comite.setResponsable(responsable);

	}

	protected void traiterParent(Localisation comite, Localisation parent) {
		if (parent == null) {
			setErreur(CHAMP_NOM_COMITE, "Comite inconnu.");

		}
		comite.setParent(parent);

	}

	protected void traiterContactFonctionnel(Application application, Utilisateur contactFonctionnel) {
		if (contactFonctionnel == null) {
			setErreur(CHAMP_NOM_CONTACT_FONCTIONNEL, "Contact fonctionnel inconnu.");

		}
		application.setContactFonctionnel(contactFonctionnel);

	}

	protected void traiterContactTechnique(Application application, Utilisateur contactTechnique) {
		if (contactTechnique == null) {
			setErreur(CHAMP_NOM_CONTACT_TECHNIQUE, "Contact technique inconnu.");

		}
		application.setContactTechnique(contactTechnique);

	}

	protected void traiterContactInterne(Beamer beamer, Utilisateur contactInterne) {
		if (contactInterne == null) {
			setErreur(CHAMP_NOM_CONTACT_INTERNE, "Contact interne inconnu.");

		}
		beamer.setContactInterne(contactInterne);

	}

	protected void traiterContactInterne(Imprimante imprimante, Utilisateur contactInterne) {
		if (contactInterne == null) {
			setErreur(CHAMP_NOM_CONTACT_INTERNE, "Contact interne inconnu.");

		}
		imprimante.setContactInterne(contactInterne);

	}

	protected void traiterFournisseur(Application application, Fournisseur fournisseur) {
		if (fournisseur == null) {
			setErreur(CHAMP_NOM_FOURNISSEUR, "Fournisseur inconnu.");

		}
		application.setFournisseur(fournisseur);

	}

	protected void traiterFournisseur(Beamer beamer, Fournisseur fournisseur) {
		if (fournisseur == null) {
			setErreur(CHAMP_NOM_FOURNISSEUR, "Fournisseur inconnu.");

		}
		beamer.setFournisseur(fournisseur);

	}

	protected void traiterFournisseur(Imprimante imprimante, Fournisseur fournisseur) {
		if (fournisseur == null) {
			setErreur(CHAMP_NOM_FOURNISSEUR, "Fournisseur inconnu.");

		}
		imprimante.setFournisseur(fournisseur);

	}

	protected void traiterFournisseur(Station imprimante, Fournisseur fournisseur) {
		if (fournisseur == null) {
			setErreur(CHAMP_NOM_FOURNISSEUR, "Fournisseur inconnu.");

		}
		imprimante.setFournisseur(fournisseur);

	}

	protected void traiterFournisseur(Telephone imprimante, Fournisseur fournisseur) {
		if (fournisseur == null) {
			setErreur(CHAMP_NOM_FOURNISSEUR, "Fournisseur inconnu.");

		}
		imprimante.setFournisseur(fournisseur);

	}

	protected void traiterSalle(Beamer beamer, Salle salle) {
		if (salle == null) {
			setErreur(CHAMP_NOM_SALLE, "Salle inconnu.");

		}
		beamer.setSalle(salle);

	}

	protected void traiterSalle(Station beamer, Salle salle) {
		if (salle == null) {
			setErreur(CHAMP_NOM_SALLE, "Salle inconnu.");

		}
		beamer.setSalle(salle);

	}

	protected void traiterSalle(Telephone beamer, Salle salle) {
		if (salle == null) {
			setErreur(CHAMP_NOM_SALLE, "Salle inconnu.");

		}
		beamer.setSalle(salle);

	}

	protected void traiterActif(Utilisateur collaborateur, Boolean actif) {
		try {
			validationBoolean(actif);
		} catch (FormValidationException e) {
			setErreur(CHAMP_ACTIF, e.getMessage());
		}
		collaborateur.setActif(actif);

	}

	protected void traiterAdmin(Utilisateur collaborateur, Boolean admin) {
		try {
			validationBoolean(admin);
		} catch (FormValidationException e) {
			setErreur(CHAMP_ADMIN, e.getMessage());
		}
		collaborateur.setAdmin(admin);

	}

	protected void traiterValide(Fonction metier, Boolean valide) {
		try {
			validationBoolean(valide);
		} catch (FormValidationException e) {
			setErreur(CHAMP_VALIDE, e.getMessage());
		}
		metier.setValide(valide);

	}

	protected void traiterStatut(Utilisateur collaborateur, Statut statut) {
		try {
			if (statut == null) {
				setErreur(CHAMP_STATUT, "Statut inconnu.");

			} else {
				validationStatut(statut);
				collaborateur.setStatut(statut);
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_STATUT, e.getMessage());
		}

	}

	protected void traiterSexe(Utilisateur collaborateur, String sexe) {
		try {
			validationSexe(sexe);
			collaborateur.setSexe(sexe);
		} catch (FormValidationException e) {
			setErreur(CHAMP_SEXE, e.getMessage());
		}

	}

	protected void traiterDateFinEffective(Utilisateur collaborateur, String dateFinEffective) {
		Date d = null;

		try {
			if (dateFinEffective != null && dateFinEffective.length() > 2) {
				if (!dateFinEffective.trim().isEmpty())
					d = format(dateFinEffective);
				validationDate(d);
				if (collaborateur.getDateDebutService() != null
						&& collaborateur.getDateDebutService().compareTo(new DateTime(d)) > 0) {
					throw new FormValidationException("La date de fin est incorrecte.");
				}
				collaborateur.setDateFinEffective(new DateTime(d));

			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_FIN_EFFECTIVE, e.getMessage());
		}
	}

	protected void traiterDateFinService(Utilisateur collaborateur, String dateFinService) {
		Date d = null;

		try {
			if (dateFinService != null && dateFinService.length() > 2) {
				if (!dateFinService.trim().isEmpty())
					d = format(dateFinService);
				validationDate(d);
				if (collaborateur.getDateDebutService() != null
						&& collaborateur.getDateDebutService().compareTo(new DateTime(d)) > 0) {
					throw new FormValidationException("La date de fin est incorrecte.");
				}
				collaborateur.setDateFinService(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_FIN_SERVICE, e.getMessage());
		}

	}

	protected void traiterDateValidation(Fonction metier, String dateValidation) {
		Date d = null;

		try {
			if (dateValidation != null && dateValidation.length() > 2) {
				if (!dateValidation.trim().isEmpty())
					d = format(dateValidation);
				validationDate(d);
				metier.setDateValidation(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_VALIDATION, e.getMessage());
		}

	}

	protected void traiterDateAchat(Beamer beamer, String dateAchat) {
		Date d = null;

		try {
			if (dateAchat != null && dateAchat.length() > 2) {
				if (!dateAchat.trim().isEmpty())
					d = format(dateAchat);
				validationDate(d);
				beamer.setDateAchat(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_ACHAT, e.getMessage());
		}

	}

	protected void traiterDateAchat(Imprimante imprimante, String dateAchat) {
		Date d = null;

		try {
			if (dateAchat != null && dateAchat.length() > 2) {
				if (!dateAchat.trim().isEmpty())
					d = format(dateAchat);
				validationDate(d);
				imprimante.setDateAchat(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_ACHAT, e.getMessage());
		}

	}

	protected void traiterDateAchat(Station imprimante, String dateAchat) {
		Date d = null;

		try {
			if (dateAchat != null && dateAchat.length() > 2) {
				if (!dateAchat.trim().isEmpty())
					d = format(dateAchat);
				validationDate(d);
				imprimante.setDateAchat(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_ACHAT, e.getMessage());
		}

	}

	protected void traiterDateAchat(Telephone imprimante, String dateAchat) {
		Date d = null;

		try {
			if (dateAchat != null && dateAchat.length() > 2) {
				if (!dateAchat.trim().isEmpty())
					d = format(dateAchat);
				validationDate(d);
				imprimante.setDateAchat(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_ACHAT, e.getMessage());
		}

	}

	protected void traiterDatePeremption(Imprimante imprimante, String datePeremption) {
		Date d = null;

		try {
			if (datePeremption != null && datePeremption.length() > 2) {
				if (!datePeremption.trim().isEmpty())
					d = format(datePeremption);
				validationDate(d);
				imprimante.setDatePeremption(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_PEREMPTION, e.getMessage());
		}

	}

	protected void traiterDatePeremption(Station imprimante, String datePeremption) {
		Date d = null;

		try {
			if (datePeremption != null && datePeremption.length() > 2) {
				if (!datePeremption.trim().isEmpty())
					d = format(datePeremption);
				validationDate(d);
				imprimante.setDatePeremption(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_PEREMPTION, e.getMessage());
		}

	}

	protected void traiterDatePeremption(Telephone imprimante, String datePeremption) {
		Date d = null;

		try {
			if (datePeremption != null && datePeremption.length() > 2) {
				if (!datePeremption.trim().isEmpty())
					d = format(datePeremption);
				validationDate(d);
				imprimante.setDatePeremption(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_PEREMPTION, e.getMessage());
		}

	}

	protected void traiterDatePeremption(Beamer beamer, String datePeremption) {
		Date d = null;

		try {
			if (datePeremption != null && datePeremption.length() > 2) {
				if (!datePeremption.trim().isEmpty())
					d = format(datePeremption);
				validationDate(d);
				beamer.setDatePeremption(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_PEREMPTION, e.getMessage());
		}

	}

	protected void traiterDate(Evenement evenement, String date) {
		Date d = null;

		try {
			if (date != null && date.length() > 2) {
				if (!date.trim().isEmpty())
					d = format(date);
				validationDate(d);
				evenement.setDate(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE, e.getMessage());
		}

	}

	protected void traiterTitre(Manifestation manifestation, String titre) {
		try {
			validationTitre(titre);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TITRE, e.getMessage());
		}
		manifestation.setTitre(titre);

	}

	protected void traiterDescription(Manifestation manifestation, String contenu) {
		try {
			validationCommentaires(contenu);
		} catch (FormValidationException e) {
			setErreur(CHAMP_CONTENU, e.getMessage());
		}
		manifestation.setDescription(contenu);

	}

	protected void traiterDate(Manifestation manifestation, String date) {
		Date d = null;

		try {
			if (date != null && date.length() > 2) {
				if (!date.trim().isEmpty())
					d = format(date);
				validationDate(d);
				manifestation.setDate(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE, e.getMessage());
		}

	}

	protected void traiterDate(Seance evenement, String date) {
		Date d = null;

		try {
			if (date != null && date.length() > 2) {
				if (!date.trim().isEmpty())
					d = format(date);
				validationDate(d);
				evenement.setDate(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE, e.getMessage());
		}

	}

	protected void traiterDate(Breve breve, String date) {
		Date d = null;

		try {
			if (date != null && date.length() > 2) {
				if (!date.trim().isEmpty())
					d = format(date);
				validationDate(d);
				breve.setDate(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE, e.getMessage());
		}

	}

	protected void traiterDate(Annonce annonce, String date) {
		Date d = null;

		try {
			if (date != null && date.length() > 2) {
				if (!date.trim().isEmpty())
					d = format(date);
				validationDate(d);
				annonce.setDate(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE, e.getMessage());
		}

	}

	protected void traiterEcheance(Annonce annonce, String echeance) {
		Date d = null;

		try {
			if (echeance != null && echeance.length() > 2) {
				if (!echeance.trim().isEmpty())
					d = format(echeance);
				validationDate(d);
				annonce.setEcheance(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_ECHEANCE, e.getMessage());
		}

	}

	protected void traiterDate(CompteRendu compteRendu, String date) {
		Date d = null;

		try {
			if (date != null && date.length() > 2) {
				if (!date.trim().isEmpty())
					d = format(date);
				validationDate(d);
				compteRendu.setDate(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE, e.getMessage());
		}

	}

	private Date format(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		Date d = null;
		try {

			d = sdf.parse(date);
		} catch (ParseException e) {
			sdf = new SimpleDateFormat("dd/MM/yy");
			try {
				date = date.replace('.', '/');
				date = date.replace('-', '/');

				d = sdf.parse(date);
			} catch (ParseException e1) {
				throw new DatabaseException(e1);
			}

		}
		return d;

	}

	protected void traiterDateDebutService(Utilisateur collaborateur, String dateDebutService) {
		/* Validation du champ date de debut de service. */
		Date d = null;

		try {
			if (dateDebutService != null && dateDebutService.length() > 2) {
				if (!dateDebutService.trim().isEmpty())
					d = format(dateDebutService);
				validationDate(d);
				collaborateur.setDateDebutService(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_DEBUT_SERVICE, e.getMessage());
		}

	}

	protected void traiterDateAnniversaire(Utilisateur collaborateur, String dateAnniversaire) {
		/* Validation du champ date de debut de service. */
		Date d = null;

		try {
			if (dateAnniversaire != null && dateAnniversaire.length() > 2) {
				if (!dateAnniversaire.trim().isEmpty())
					d = format(dateAnniversaire);
				validationDate(d);
				collaborateur.setDateAnniversaire(new DateTime(d));
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_DATE_ANNIVERSAIRE, e.getMessage());
		}

	}

	protected void traiterInitiales(Utilisateur collaborateur, String initiales) {
		/* Validation du champ initiales. */
		try {
			validationInitiales(initiales);
			collaborateur.setInitiales(initiales);
		} catch (FormValidationException e) {
			setErreur(CHAMP_INITIALES, e.getMessage());
		}

	}

	protected void traiterConfirmation(Utilisateur collaborateur, String confirmation) {
		try {
			validationConfirmation(confirmation, collaborateur.getMdp());
		} catch (FormValidationException e) {
			setErreur(CHAMP_CONF, e.getMessage());
		}

	}

	protected void traiterMdp(Utilisateur collaborateur, String mdp) {

		/* Validation du champ mot de passe. */
		try {
			validationMotDePasse(mdp);
		} catch (FormValidationException e) {
			setErreur(CHAMP_PASS, e.getMessage());
		}
		collaborateur.setMdp(mdp);

	}

	protected void traiterNom(Utilisateur collaborateur, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM, e.getMessage());
		}
		collaborateur.setNom(nom);
	}

	protected void traiterNom(Localisation comite, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM, e.getMessage());
		}
		comite.setNom(nom);
	}

	protected void traiterTitre(Evenement evenement, String titre) {
		try {
			validationTitre(titre);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TITRE, e.getMessage());
		}
		evenement.setTitre(titre);

	}

	protected void traiterTitre(Breve breve, String titre) {
		try {
			validationTitre(titre);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TITRE, e.getMessage());
		}
		breve.setTitre(titre);

	}

	protected void traiterTitre(CompteRendu compteRendu, String titre) {
		try {
			validationTitre(titre);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TITRE, e.getMessage());
		}
		compteRendu.setTitre(titre);

	}

	protected void traiterTitre(Annonce annonce, String titre) {
		try {
			validationTitre(titre);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TITRE, e.getMessage());
		}
		annonce.setTitre(titre);

	}

	protected void traiterTitre(Directive directive, String titre) {
		try {
			validationTitre(titre);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TITRE, e.getMessage());
		}
		directive.setTitre(titre);

	}

	protected void traiterAcronyme(Localisation comite, String acronyme) {
		try {
			validationNom(acronyme);
		} catch (FormValidationException e) {
			setErreur(CHAMP_ACRONYME, e.getMessage());
		}
		comite.setAcronyme(acronyme);

	}

	protected void traiterNom(Seance droit, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_LIBELLE, e.getMessage());
		}
		droit.setLibelle(nom);
	}

	protected void traiterNom(Application application, String nom) {
		try {
			validationNom(nom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_APPLICATION, e.getMessage());
		}
		application.setNom(nom);

	}

	protected void traiterNoPlaques(Voiture voiture, String noPlaques) {
		try {
			validationNoPlaques(noPlaques);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOPLAQUES, e.getMessage());
		}
		voiture.setNoPlaques(noPlaques);
	}

	protected void traiterNumero(Beamer beamer, String numero) {
		try {
			validationNumero(numero);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NUMERO, e.getMessage());
		}
		beamer.setNumero(numero);
	}

	protected void traiterNumero(Station station, String numero) {
		try {
			validationNumero(numero);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NUMERO, e.getMessage());
		}
		station.setNumero(numero);
	}

	protected void traiterNumero(Telephone t, String numero) {
		try {
			validationNumero(numero);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NUMERO, e.getMessage());
		}
		t.setNumero(numero);
	}

	protected void traiterNumero(Imprimante imprimante, String numero) {
		try {
			validationNumero(numero);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NUMERO, e.getMessage());
		}
		imprimante.setNumero(numero);
	}

	private void validationNoPlaques(String nom) throws FormValidationException {
		if (nom != null) {
			if (nom.length() < 2) {
				throw new FormValidationException("Le nom d'utilisateur doit contenir au moins 2 caractères.");
			}
		} else {
			throw new FormValidationException("Merci d'entrer un nom d'utilisateur.");
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

	protected void traiterPrenom(Utilisateur collaborateur, String prenom) {
		try {
			validationPrenom(prenom);
		} catch (FormValidationException e) {
			setErreur(CHAMP_PRENOM, e.getMessage());
		}
		collaborateur.setPrenom(prenom);
	}

	protected void traiterDescription(Application application, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		application.setDescription(description);

	}

	protected void traiterContenu(Annonce annonce, String contenu) {
		try {
			validationDescription(contenu);
		} catch (FormValidationException e) {
			setErreur(CHAMP_CONTENU, e.getMessage());
		}
		annonce.setContenu(contenu);

	}

	protected void traiterDescription(Fonction metier, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		metier.setDescription(description);

	}

	protected void traiterDescription(Localisation comite, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		comite.setDescription(description);

	}

	protected void traiterDescription(Seance seance, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		seance.setDescription(description);

	}

	protected void traiterDescription(Partenaire partenaire, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		partenaire.setDescription(description);

	}

	protected void traiterDescription(Breve breve, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		breve.setDescription(description);

	}

	protected void traiterDescription(CompteRendu compteRendu, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		compteRendu.setDescription(description);

	}

	protected void traiterDescription(Outil outil, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		outil.setDescription(description);

	}

	protected void traiterDescription(Directive directive, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		directive.setDescription(description);

	}

	protected void traiterTheme(Directive directive, String theme) {
		try {
			validationTheme(theme);
		} catch (FormValidationException e) {
			setErreur(CHAMP_THEME, e.getMessage());
		}
		directive.setTheme(theme);

	}

	protected void traiterLien(Directive directive, String lien) {
		try {
			validationLien(lien);
		} catch (FormValidationException e) {
			setErreur(CHAMP_LIEN, e.getMessage());
		}
		directive.setLien(lien);

	}

	protected void traiterLien(Outil outil, String lien) {
		try {
			validationLien(lien);
		} catch (FormValidationException e) {
			setErreur(CHAMP_LIEN, e.getMessage());
		}
		outil.setLien(lien);

	}

	protected void traiterLien(Breve breve, String lien) {
		try {
			validationLien(lien);
		} catch (FormValidationException e) {
			setErreur(CHAMP_LIEN, e.getMessage());
		}
		breve.setLien(lien);

	}

	protected void traiterLien(CompteRendu compteRendu, String lien) {
		try {
			validationLien(lien);
		} catch (FormValidationException e) {
			setErreur(CHAMP_LIEN, e.getMessage());
		}
		compteRendu.setLien(lien);

	}

	protected void traiterDescription(Salle salle, String description) {
		try {
			validationDescription(description);
		} catch (FormValidationException e) {
			setErreur(CHAMP_DESCRIPTION, e.getMessage());
		}
		salle.setDescription(description);

	}

	protected void traiterCommentaires(Fonction metier, String commentaires) {
		try {
			validationCommentaires(commentaires);
		} catch (FormValidationException e) {
			setErreur(CHAMP_COMMENTAIRES, e.getMessage());
		}
		metier.setCommentaires(commentaires);

	}

	protected void traiterCanton(Adresse adresse, String canton) {
		try {
			validationCanton(canton);
		} catch (FormValidationException e) {
			setErreur(CHAMP_CANTON, e.getMessage());
		}
		adresse.setCanton(canton);

	}

	protected void traiterDescription(Evenement evenement, String contenu) {
		try {
			validationCommentaires(contenu);
		} catch (FormValidationException e) {
			setErreur(CHAMP_CONTENU, e.getMessage());
		}
		evenement.setDescription(contenu);

	}

	protected void traiterAdresse(Evenement evenement, Adresse adresse) {
		if (adresse == null) {
			setErreur(CHAMP_NOM_ADRESSE, "Adresse inconnue.");

		} else
			evenement.setAdresse(adresse);

	}

	protected void traiterAdresse(Manifestation manifestation, Adresse adresse) {
		if (adresse == null) {
			setErreur(CHAMP_NOM_ADRESSE, "Adresse inconnue.");

		} else
			manifestation.setAdresse(adresse);

	}

	protected void traiterAdresse(Imprimante imprimante, Adresse adresse) {
		if (adresse == null) {
			setErreur(CHAMP_NOM_ADRESSE, "Adresse inconnue.");

		} else
			imprimante.setAdresse(adresse);

	}

	protected void traiterAdresse(Salle salle, Adresse adresse) {
		if (adresse == null) {
			setErreur(CHAMP_NOM_ADRESSE, "Adresse inconnue.");

		} else
			salle.setAdresse(adresse);

	}

	protected void traiterAdresse(Seance salle, Adresse adresse) {
		if (adresse == null) {
			setErreur(CHAMP_NOM_ADRESSE, "Adresse inconnue.");

		} else
			salle.setAdresse(adresse);

	}

	protected void traiterComite(Seance salle, Localisation adresse) {
		if (adresse == null) {
			setErreur(CHAMP_NOM_ADRESSE, "Adresse inconnue.");

		} else
			salle.setComite(adresse);

	}

	protected void traiterAdresse(Fournisseur fournisseur, Adresse adresse) {
		if (adresse == null) {
			setErreur(CHAMP_NOM_FOURNISSEUR, "Fournisseur inconnu.");

		} else
			fournisseur.setAdresse(adresse);

	}

	protected void traiterPrenomSuperviseur(Utilisateur superviseur, String prenomSuperviseur) {
		try {
			validationPrenom(prenomSuperviseur);
		} catch (FormValidationException e) {
			setErreur(CHAMP_PRENOM_SUPERVISEUR, e.getMessage());
		}
		superviseur.setPrenom(prenomSuperviseur);

	}

	protected void traiterNomSuperviseur(Utilisateur superviseur, String nomSuperviseur) {
		try {
			validationNom(nomSuperviseur);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NOM_SUPERVISEUR, e.getMessage());
		}
		superviseur.setNom(nomSuperviseur);

	}

	protected void traiterTelInterne(Utilisateur collaborateur, String telephone) {
		scanner = new Scanner(telephone);
		try {

			scanner.useDelimiter("/");
			if (scanner.hasNext()) {
				// assumes the line has a certain structure
				telephone = scanner.next();

			}

			telephone = telephone.replaceAll("\\W", "").trim();

			if (telephone == null) {
				setErreur(CHAMP_TELINTERNE, "Numèro de telephone incorrect.");

			} else {
				validationTelephone(telephone);

				collaborateur.setTelInterne(telephone);
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_TELINTERNE, e.getMessage());
		}

	}

	protected void traiterTelephone(Adresse adresse, String telephone) {
		scanner = new Scanner(telephone);
		try {

			scanner.useDelimiter("/");
			if (scanner.hasNext()) {
				// assumes the line has a certain structure
				telephone = scanner.next();

			}

			telephone = telephone.replaceAll("\\W", "").trim();

			if (telephone == null) {
				setErreur(CHAMP_TELEPHONE, "Numèro de telephone incorrect.");

			} else {
				validationTelephone(telephone);
				adresse.setTelephone(telephone);
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_TELEPHONE, e.getMessage());
		}

	}

	protected void traiterFax(Adresse adresse, String fax) {
		try {

			if (fax == null) {
				setErreur(CHAMP_FAX, "Numèro de telephone incorrect.");

			} else {
				validationTelephone(fax);
				adresse.setFax(fax);
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_FAX, e.getMessage());
		}

	}

	protected void traiterTelPrive(Utilisateur collaborateur, String telephone) {
		scanner = new Scanner(telephone);
		try {

			scanner.useDelimiter("/");
			if (scanner.hasNext()) {
				// assumes the line has a certain structure
				telephone = scanner.next();

			}

			telephone = telephone.replaceAll("\\W", "").trim();

			if (telephone == null) {
				setErreur(CHAMP_TELINTERNE, "Numèro de telephone incorrect.");

			} else {
				validationTelephone(telephone);
				collaborateur.setTelPrive(telephone);
			}
		} catch (FormValidationException e) {
			setErreur(CHAMP_TELINTERNE, e.getMessage());
		}

	}

	protected void traiterEmail(Utilisateur collaborateur, String email) {
		try {
			validationEmail(email);
			collaborateur.setEmail(email);
		} catch (FormValidationException e) {
			setErreur(CHAMP_EMAIL, e.getMessage());
		}

	}

	protected void traiterActivites(Utilisateur collaborateur, String activites) {
		try {

			validationDescription(activites);
			collaborateur.setActivites(activites.replace("<br>", "\r"));
		} catch (FormValidationException e) {
			setErreur(CHAMP_ACTIVITES, e.getMessage());
		}

	}

	protected void traiterEmail(Fournisseur fournisseur, String email) {
		try {
			validationEmail(email);
			fournisseur.setEmail(email);
		} catch (FormValidationException e) {
			setErreur(CHAMP_EMAIL, e.getMessage());
		}

	}

	protected void traiterImage(Utilisateur collaborateur, HttpServletRequest request, String chemin) {
		String image = null;
		try {
			image = validationImage(request, chemin);
		} catch (FormValidationException e) {
			setErreur(CHAMP_IMAGE, e.getMessage());
		}
		collaborateur.setImage(image);
	}

	protected void traiterImage(Utilisateur collaborateur, String image) {
		try {
			validationImage(image);
		} catch (FormValidationException e) {
			setErreur(CHAMP_IMAGE, e.getMessage());
		}
		collaborateur.setImage(image);

	}

	protected void traiterReponse(Utilisateur collaborateur, String reponse) {
		try {
			validationReponse(reponse);
		} catch (FormValidationException e) {
			setErreur(CHAMP_REPONSE, e.getMessage());
		}
		collaborateur.setReponse(reponse);

	}

	protected void traiterQuestion(Utilisateur collaborateur, String question) {
		try {
			validationQuestion(question);
		} catch (FormValidationException e) {
			setErreur(CHAMP_QUESTION, e.getMessage());
		}
		collaborateur.setQuestion(question);

	}

	protected void traiterNoRue(Adresse adresse, Integer noRue) {
		try {
			validationNo(noRue);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NORUE, e.getMessage());
		}
		adresse.setNoRue(noRue);

	}

	protected void traiterNoRue(Adresse adresse, String noRue) {
		if (noRue != null && !noRue.trim().isEmpty()) {
			Integer i = Integer.parseInt(noRue);

			try {
				validationNoRue(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_NORUE, e.getMessage());
			}
			adresse.setNoRue(i);
		} else
			adresse.setNoRue(0);

	}

	protected void traiterTaux(Utilisateur collaborateur, String taux) {
		if (taux != null && !taux.trim().isEmpty()) {
			Integer i = Integer.parseInt(taux);

			try {
				validationNoRue(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_TAUX, e.getMessage());
			}
			collaborateur.setTaux(i);
		} else
			collaborateur.setTaux(0);

	}

	protected void traiterEtage(Adresse adresse, String etage) {
		if (etage != null && !etage.trim().isEmpty()) {
			Integer i = Integer.parseInt(etage);

			try {
				validationNoRue(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_ETAGE, e.getMessage());
			}
			adresse.setEtage(i);
		} else
			adresse.setEtage(0);

	}

	protected void traiterNpa(Adresse adresse, String npa) {
		if (npa != null && !npa.trim().isEmpty()) {
			Integer i = Integer.parseInt(npa);

			try {
				validationNpa(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_NPA, e.getMessage());
			}
			adresse.setNpa(i);
		} else
			adresse.setNpa(0);

	}

	protected void traiterCapacite(Salle salle, String capacite) {
		if (capacite != null && !capacite.trim().isEmpty()) {
			Integer i = Integer.parseInt(capacite);

			try {
				validationCapacite(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_CAPACITE, e.getMessage());
			}
			salle.setCapacite(i);
		} else
			salle.setCapacite(0);

	}

	protected void traiterCp(Adresse adresse, String cp) {
		if (cp != null && !cp.trim().isEmpty()) {
			Integer i = Integer.parseInt(cp);

			try {
				validationCp(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_CP, e.getMessage());
			}
			adresse.setCp(i);
		} else
			adresse.setCp(0);

	}

	protected void traiterStatut(Localisation comite, String statut) {
		if (statut != null && !statut.trim().isEmpty()) {
			Boolean i = Boolean.parseBoolean(statut);

			try {
				validationBoolean(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_STATUT, e.getMessage());
			}
			comite.setStatut(i);
		} else
			comite.setStatut(Boolean.FALSE);

	}

	protected void traiterOfficiel(Localisation comite, String officiel) {
		if (officiel != null && !officiel.trim().isEmpty()) {
			Boolean i = Boolean.parseBoolean(officiel);

			try {
				validationBoolean(i);
			} catch (FormValidationException e) {
				setErreur(CHAMP_OFFICIEL, e.getMessage());
			}
			comite.setOfficiel(i);
		} else
			comite.setOfficiel(Boolean.FALSE);

	}

	protected void traiterPrix(Beamer beamer, String prix) {
		// Create a DecimalFormat that fits your requirements
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);

		if (prix != null && !prix.trim().isEmpty()) {
			// parse the string
			BigDecimal i = null;
			try {
				i = (BigDecimal) decimalFormat.parse(prix);

				validationPrix(i);
			} catch (FormValidationException | ParseException e) {
				setErreur(CHAMP_PRIX, e.getMessage());
			}
			beamer.setPrix(i);
		} else
			beamer.setPrix(BigDecimal.ZERO);

	}

	protected void traiterPrix(Imprimante imprimante, String prix) {
		// Create a DecimalFormat that fits your requirements
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);

		if (prix != null && !prix.trim().isEmpty()) {
			// parse the string
			BigDecimal i = null;
			try {
				i = (BigDecimal) decimalFormat.parse(prix);

				validationPrix(i);
			} catch (FormValidationException | ParseException e) {
				setErreur(CHAMP_PRIX, e.getMessage());
			}
			imprimante.setPrix(i);
		} else
			imprimante.setPrix(BigDecimal.ZERO);

	}

	protected void traiterPrix(Station station, String prix) {
		// Create a DecimalFormat that fits your requirements
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);

		if (prix != null && !prix.trim().isEmpty()) {
			// parse the string
			BigDecimal i = null;
			try {
				i = (BigDecimal) decimalFormat.parse(prix);

				validationPrix(i);
			} catch (FormValidationException | ParseException e) {
				setErreur(CHAMP_PRIX, e.getMessage());
			}
			station.setPrix(i);
		} else
			station.setPrix(BigDecimal.ZERO);

	}

	protected void traiterPrix(Telephone telephone, String prix) {
		// Create a DecimalFormat that fits your requirements
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);

		if (prix != null && !prix.trim().isEmpty()) {
			// parse the string
			BigDecimal i = null;
			try {
				i = (BigDecimal) decimalFormat.parse(prix);

				validationPrix(i);
			} catch (FormValidationException | ParseException e) {
				setErreur(CHAMP_PRIX, e.getMessage());
			}
			telephone.setPrix(i);
		} else
			telephone.setPrix(BigDecimal.ZERO);

	}

	private void validationNo(Integer no) throws FormValidationException {
		if (no == null) {
			throw new FormValidationException("Numero invalide.");
		}

	}

	protected void traiterNpa(Adresse adresse, Integer npa) {
		try {
			validationNo(npa);
		} catch (FormValidationException e) {
			setErreur(CHAMP_NPA, e.getMessage());
		}
		adresse.setNpa(npa);

	}

	protected void traiterType(Adresse adresse, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		adresse.setType(type);

	}

	protected void traiterType(Beamer beamer, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		beamer.setType(type);

	}

	protected void traiterType(Imprimante imprimante, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		imprimante.setType(type);

	}

	protected void traiterType(Station imprimante, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		imprimante.setType(type);

	}

	protected void traiterType(Telephone telephone, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		telephone.setType(type);

	}

	protected void traiterType(Breve breve, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		breve.setType(type);

	}

	protected void traiterType(CompteRendu compteRendu, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		compteRendu.setType(type);

	}

	protected void traiterType(Partenaire partenaire, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		partenaire.setType(type);

	}

	protected void traiterType(Outil outil, String type) {
		try {
			validationNom(type);
		} catch (FormValidationException e) {
			setErreur(CHAMP_TYPE, e.getMessage());
		}
		outil.setType(type);

	}

	protected void traiterRue(Adresse adresse, String rue) {
		try {
			validationNom(rue);
		} catch (FormValidationException e) {
			setErreur(CHAMP_RUE, e.getMessage());
		}
		adresse.setRue(rue);

	}

	protected void traiterPays(Adresse adresse, String pays) {
		try {
			validationNom(pays);
		} catch (FormValidationException e) {
			setErreur(CHAMP_PAYS, e.getMessage());
		}
		adresse.setPays(pays);

	}

	protected void traiterVille(Adresse adresse, String ville) {
		try {
			validationNom(ville);
		} catch (FormValidationException e) {
			setErreur(CHAMP_VILLE, e.getMessage());
		}
		adresse.setVille(ville);

	}

	protected void traiterWebsite(Localisation comite, String website) {
		try {
			validationNom(website);
		} catch (FormValidationException e) {
			setErreur(CHAMP_WEBSITE, e.getMessage());
		}
		comite.setWebsite(website);

	}

	protected void traiterWebsite(Partenaire partenaire, String website) {
		try {
			validationNom(website);
		} catch (FormValidationException e) {
			setErreur(CHAMP_WEBSITE, e.getMessage());
		}
		partenaire.setWebsite(website);

	}

}
