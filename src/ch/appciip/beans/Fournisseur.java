package ch.appciip.beans;

public class Fournisseur {
	private Long id;
	private String nom;
	private String email;
	private Adresse adresse;

	public Fournisseur(String nom) {
		this.setNom(nom);
	}

	public Fournisseur(Long id) {
		this.id = id;
	}

	public Fournisseur() {

	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
