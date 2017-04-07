package ch.appciip.beans;

public class Salle {
	private Long id;
	private String nom;
	private String description;
	private int capacite;

	private Adresse adresse;

	public Salle(String nom) {
		this.nom = nom;

	}

	public Salle(Long id) {
		this.id = id;
	}

	public Salle() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCapacite() {
		return capacite;
	}

	public void setCapacite(int capacite) {
		this.capacite = capacite;
	}

}
