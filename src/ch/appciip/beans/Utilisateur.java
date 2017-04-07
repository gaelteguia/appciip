/**
 * Bean de gestion des utilisateurs
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import org.joda.time.DateTime;

public class Utilisateur implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String initiales;
	private String nom;
	private String prenom;
	private String mdp;
	private String confirmation;
	private String sexe;
	private Statut statut;
	private String email;
	private String telInterne;
	private String telPrive;
	private String image;
	private String question;
	private String reponse;
	private String activites;
	private boolean actif;
	private boolean admin;
	private boolean dp;
	private Integer taux;
	private DateTime dateAnniversaire;
	private DateTime dateDebutService;
	private DateTime dateFinService;
	private DateTime dateFinEffective;
	private Utilisateur superieurHierarchique;
	private Service service;
	private Adresse adresse;
	private Fonction fonction;
	private Collection<Canton> applications = new Vector<Canton>();
	private Collection<Imprimante> imprimantes = new Vector<Imprimante>();
	private Collection<Distribution> distributions = new Vector<Distribution>();
	private Collection<Meteo> beamers = new Vector<Meteo>();
	private Collection<Localisation> comites = new Vector<Localisation>();
	private Collection<Seance> seances = new Vector<Seance>();
	private Collection<Service> services = new Vector<Service>();
	private Collection<Horaire> horaires = new Vector<Horaire>();

	private Collection<Evenement> evenements = new Vector<Evenement>();
	private Collection<Manifestation> manifestations = new Vector<Manifestation>();
	private Collection<Recherche> annonces = new Vector<Recherche>();
	private Collection<Utilisateur> subalternes = new Vector<Utilisateur>();

	private Collection<Utilisateur> superieurs = new Vector<Utilisateur>();
	private Collection<Utilisateur> supplees = new Vector<Utilisateur>();
	private Collection<Utilisateur> suppleants = new Vector<Utilisateur>();

	public Utilisateur(Long id) {
		this.id = id;
	}

	public Utilisateur(Long id, String initiales, String mdp, String nom, String prenom, String sexe, Statut statut,
			String email, boolean actif, DateTime dateAnniversaire, DateTime dateDebutService, DateTime dateFinService,
			DateTime dateFinEffective, Utilisateur superieurHierarchique, Service service, Adresse adresse,
			Fonction fonction, String telInterne, String telPrive, String image, String question, String reponse,
			boolean admin, String confirmation) {

		this.id = id;
		this.initiales = initiales;
		this.mdp = mdp;
		this.nom = nom;
		this.prenom = prenom;
		this.sexe = sexe;
		this.statut = statut;
		this.email = email;
		this.actif = actif;
		this.telInterne = telInterne;
		this.telPrive = telPrive;
		this.image = image;
		this.question = question;
		this.reponse = reponse;
		this.admin = admin;
		this.confirmation = confirmation;
		this.dateAnniversaire = dateAnniversaire;
		this.dateDebutService = dateDebutService;
		this.dateFinService = dateFinService;
		this.dateFinEffective = dateFinEffective;
		this.superieurHierarchique = superieurHierarchique;
		this.service = service;
		this.adresse = adresse;
		this.fonction = fonction;
	}

	public Utilisateur(String nom, String prenom) {
		this.nom = nom;
		this.prenom = prenom;
	}

	public Utilisateur(String initiales, String prenom, String nom) {

		this.initiales = initiales;
		this.prenom = prenom;
		this.nom = nom;

	}

	public Utilisateur() {
	}

	public Utilisateur(String initiales) {
		this.initiales = initiales;
	}

	public Utilisateur(Long id, String initiales, String nom, String prenom) {
		this.id = id;
		this.initiales = initiales;
		this.nom = nom;
		this.prenom = prenom;
	}

	public String getNom() {
		return nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public String getInitiales() {
		return initiales;
	}

	public Long getId() {
		return id;
	}

	public DateTime getDateDebutService() {
		return dateDebutService;
	}

	public Statut getType() {
		return statut;
	}

	public boolean isActif() {
		return actif;
	}

	public String getEmail() {
		return email;
	}

	public Fonction getFonction() {
		return fonction;
	}

	public String getSexe() {
		return sexe;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public Utilisateur getSuperviseur() {
		return superieurHierarchique;
	}

	public Service getService() {
		return service;
	}

	public DateTime getDateFinEffective() {
		return dateFinEffective;
	}

	public DateTime getDateFinService() {
		return dateFinService;
	}

	public void setInitiales(String initiales) {
		this.initiales = initiales;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public void setDateDebutService(DateTime dateDebutService) {
		this.dateDebutService = dateDebutService;
	}

	public void setDateFinService(DateTime dateFinService) {
		this.dateFinService = dateFinService;
	}

	public void setDateFinEffective(DateTime dateFinEffective) {
		this.dateFinEffective = dateFinEffective;
	}

	public void setSuperviseur(Utilisateur superieurHierarchique) {
		this.superieurHierarchique = superieurHierarchique;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	public void setFonction(Fonction fonction) {
		this.fonction = fonction;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public void setMdp(String mdp) {
		this.mdp = mdp;

	}

	public String getMdp() {
		return mdp;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getReponse() {
		return reponse;
	}

	public void setReponse(String reponse) {
		this.reponse = reponse;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}

	public Collection<Localisation> getComites() {
		return comites;
	}

	public void setComites(Collection<Localisation> comites) {
		this.comites = comites;
	}

	public Collection<Evenement> getEvenements() {
		return evenements;
	}

	public void setEvenements(Collection<Evenement> evenements) {
		this.evenements = evenements;
	}

	public Collection<Utilisateur> getSubalternes() {
		return subalternes;
	}

	public void setSubalternes(Collection<Utilisateur> subalternes) {
		this.subalternes = subalternes;
	}

	public Statut getStatut() {
		return statut;
	}

	public void setStatut(Statut statut) {
		this.statut = statut;
	}

	public String getTelInterne() {
		return telInterne;
	}

	public void setTelInterne(String telInterne) {
		this.telInterne = telInterne;
	}

	public String getTelPrive() {
		return telPrive;
	}

	public void setTelPrive(String telPrive) {
		this.telPrive = telPrive;
	}

	public DateTime getDateAnniversaire() {
		return dateAnniversaire;
	}

	public void setDateAnniversaire(DateTime dateAnniversaire) {
		this.dateAnniversaire = dateAnniversaire;
	}

	public boolean isDp() {
		return dp;
	}

	public void setDp(boolean dp) {
		this.dp = dp;
	}

	public Collection<Canton> getApplications() {
		return applications;
	}

	public void setApplications(Collection<Canton> applications) {
		this.applications = applications;
	}

	public Collection<Meteo> getBeamers() {
		return beamers;
	}

	public void setBeamers(Collection<Meteo> beamers) {
		this.beamers = beamers;
	}

	public Collection<Distribution> getDistributions() {
		return distributions;
	}

	public void setDistributions(Collection<Distribution> distributions) {
		this.distributions = distributions;
	}

	public Collection<Imprimante> getImprimantes() {
		return imprimantes;
	}

	public void setImprimantes(Collection<Imprimante> imprimantes) {
		this.imprimantes = imprimantes;
	}

	public Collection<Seance> getSeances() {
		return seances;
	}

	public void setSeances(Collection<Seance> seances) {
		this.seances = seances;
	}

	public Collection<Horaire> getHoraires() {
		return horaires;
	}

	public void setHoraires(Collection<Horaire> horaires) {
		this.horaires = horaires;
	}

	public Collection<Service> getServices() {
		return services;
	}

	public void setServices(Collection<Service> services) {
		this.services = services;
	}

	public String getActivites() {
		return activites;
	}

	public void setActivites(String activites) {
		this.activites = activites;
	}

	public Collection<Recherche> getAnnonces() {
		return annonces;
	}

	public void setAnnonces(Collection<Recherche> annonces) {
		this.annonces = annonces;
	}

	public Integer getTaux() {
		return taux;
	}

	public void setTaux(Integer taux) {
		this.taux = taux;
	}

	public Collection<Utilisateur> getSuperieurs() {
		return superieurs;
	}

	public void setSuperieurs(Collection<Utilisateur> superieurs) {
		this.superieurs = superieurs;
	}

	public Collection<Utilisateur> getSupplees() {
		return supplees;
	}

	public void setSupplees(Collection<Utilisateur> supplees) {
		this.supplees = supplees;
	}

	public Collection<Utilisateur> getSuppleants() {
		return suppleants;
	}

	public void setSuppleants(Collection<Utilisateur> suppleants) {
		this.suppleants = suppleants;
	}

	public Collection<Manifestation> getManifestations() {
		return manifestations;
	}

	public void setManifestations(Collection<Manifestation> manifestations) {
		this.manifestations = manifestations;
	}

}
