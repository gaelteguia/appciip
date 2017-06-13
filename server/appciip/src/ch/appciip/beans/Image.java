package ch.appciip.beans;

public class Image {
	private int id;
	private String nom;
	private String cible;
	private Membre membre;
	private Salle ecole;
	private Institution repository;
	private Localisation article;
	private Distribution event;
	private Outil job;
	private CompteRendu logement;
	private Directive objet;
	private Vacance filiere;

	public Image(String nom, String cible) {
		this.nom = nom;
		this.cible = cible;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCible() {
		return cible;
	}

	public void setCible(String cible) {
		this.cible = cible;
	}

	public Membre getMembre() {
		return membre;
	}

	public void setMembre(Membre membre) {
		this.membre = membre;
	}

	public Salle getEcole() {
		return ecole;
	}

	public void setEcole(Salle ecole) {
		this.ecole = ecole;
	}

	public Institution getRepository() {
		return repository;
	}

	public void setRepository(Institution repository) {
		this.repository = repository;
	}

	public Localisation getArticle() {
		return article;
	}

	public void setArticle(Localisation article) {
		this.article = article;
	}

	public Distribution getEvent() {
		return event;
	}

	public void setEvent(Distribution event) {
		this.event = event;
	}

	public Outil getJob() {
		return job;
	}

	public void setJob(Outil job) {
		this.job = job;
	}

	public CompteRendu getLogement() {
		return logement;
	}

	public void setLogement(CompteRendu logement) {
		this.logement = logement;
	}

	public Directive getObjet() {
		return objet;
	}

	public void setObjet(Directive objet) {
		this.objet = objet;
	}

	public Vacance getFiliere() {
		return filiere;
	}

	public void setFiliere(Vacance filiere) {
		this.filiere = filiere;
	}

}
