package ch.appciip.beans;

public class Institution {
	private Long id;
	private String nom;

	private String type;
	private String website;
	private String description;

	public Long getId() {
		return id;
	}

	public Institution(Long id) {
		this.id = id;
	}

	public Institution() {

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

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
