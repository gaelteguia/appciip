/**
 * Bean de gestion des utilisateurs
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import org.joda.time.DateTime;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String initiales;
	private String name;
	private String forename;
	private String mdp;
	private String confirmation;
	private String sexe;

	private String email;
	private String telInterne;
	private String telPrive;
	private String image;
	private String question;
	private String reponse;
	private String activites;
	private boolean active;
	private boolean admin;
	private boolean dp;
	private Integer taux;
	private DateTime dateAnniversaire;
	private DateTime dateDebutService;
	private DateTime dateFinService;
	private DateTime dateFinEffective;
	private User superieurHierarchique;

	private Address address;

	private Collection<Localisation> localisations = new Vector<Localisation>();

	private Collection<Manifestation> manifestations = new Vector<Manifestation>();
	private Collection<Query> queries = new Vector<Query>();
	private Collection<User> subalternes = new Vector<User>();

	private Collection<User> superieurs = new Vector<User>();
	private Collection<User> supplees = new Vector<User>();
	private Collection<User> suppleants = new Vector<User>();

	public User(Long id) {
		this.id = id;
	}

	public User(Long id, String initiales, String mdp, String name, String forename, String sexe, String email,
			boolean active, DateTime dateAnniversaire, DateTime dateDebutService, DateTime dateFinService,
			DateTime dateFinEffective, User superieurHierarchique, Address address, String telInterne, String telPrive,
			String image, String question, String reponse, boolean admin, String confirmation) {

		this.id = id;
		this.initiales = initiales;
		this.mdp = mdp;
		this.name = name;
		this.forename = forename;
		this.sexe = sexe;

		this.email = email;
		this.active = active;
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

		this.address = address;

	}

	public User(String name, String forename) {
		this.name = name;
		this.forename = forename;
	}

	public User(String initiales, String forename, String name) {

		this.initiales = initiales;
		this.forename = forename;
		this.name = name;

	}

	public User() {
	}

	public User(String initiales) {
		this.initiales = initiales;
	}

	public User(Long id, String initiales, String name, String forename) {
		this.id = id;
		this.initiales = initiales;
		this.name = name;
		this.forename = forename;
	}

	public String getName() {
		return name;
	}

	public String getForename() {
		return forename;
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

	public boolean isActive() {
		return active;
	}

	public String getEmail() {
		return email;
	}

	public String getSexe() {
		return sexe;
	}

	public Address getAddress() {
		return address;
	}

	public User getSuperviseur() {
		return superieurHierarchique;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public void setSuperviseur(User superieurHierarchique) {
		this.superieurHierarchique = superieurHierarchique;
	}

	public void setAddress(Address address) {
		this.address = address;
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

	public Collection<Localisation> getLocalisations() {
		return localisations;
	}

	public void setLocalisations(Collection<Localisation> localisations) {
		this.localisations = localisations;
	}

	public Collection<User> getSubalternes() {
		return subalternes;
	}

	public void setSubalternes(Collection<User> subalternes) {
		this.subalternes = subalternes;
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

	public String getActivites() {
		return activites;
	}

	public void setActivites(String activites) {
		this.activites = activites;
	}

	public Collection<Query> getQueries() {
		return queries;
	}

	public void setQueries(Collection<Query> queries) {
		this.queries = queries;
	}

	public Integer getTaux() {
		return taux;
	}

	public void setTaux(Integer taux) {
		this.taux = taux;
	}

	public Collection<User> getSuperieurs() {
		return superieurs;
	}

	public void setSuperieurs(Collection<User> superieurs) {
		this.superieurs = superieurs;
	}

	public Collection<User> getSupplees() {
		return supplees;
	}

	public void setSupplees(Collection<User> supplees) {
		this.supplees = supplees;
	}

	public Collection<User> getSuppleants() {
		return suppleants;
	}

	public void setSuppleants(Collection<User> suppleants) {
		this.suppleants = suppleants;
	}

	public Collection<Manifestation> getManifestations() {
		return manifestations;
	}

	public void setManifestations(Collection<Manifestation> manifestations) {
		this.manifestations = manifestations;
	}

}
