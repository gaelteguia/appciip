/**
 * Bean de gestion des applications
 * @author Ga�l TEGUIA TEGUIA
 */

package ch.appciip.beans;

import java.io.Serializable;

public class Canton implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private Long id;
	private String nom;
	private String description;
	private Utilisateur contactTechnique;
	private Utilisateur contactFonctionnel;
	private Fournisseur fournisseur;

	public Canton(Long id, String nom, String description, Utilisateur contactTechnique,
			Utilisateur contactFonctionnel) {
		this.id = id;
		this.nom = nom;
		this.description = description;
		this.contactTechnique = contactTechnique;
		this.contactFonctionnel = contactFonctionnel;
	}

	public Canton(Long id) {
		this.id = id;
	}

	public Canton(Long id, String nom, String description) {
		this.id = id;
		this.nom = nom;
		this.description = description;
	}

	public Canton(String nom) {
		this.nom = nom;
	}

	public Canton(Long id, String nom, String description, Utilisateur contactTechnique) {
		this.id = id;
		this.nom = nom;
		this.description = description;
		this.contactTechnique = contactTechnique;
	}

	public Canton() {
	}

	public String getNom() {
		return nom;
	}

	public String getDescription() {
		return description;
	}

	public Long getId() {
		return id;
	}

	public Utilisateur getContactTechnique() {
		return contactTechnique;
	}

	public Utilisateur getContactFonctionnel() {
		return contactFonctionnel;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setContactTechnique(Utilisateur contactTechnique) {
		this.contactTechnique = contactTechnique;
	}

	public void setContactFonctionnel(Utilisateur contactFonctionnel) {
		this.contactFonctionnel = contactFonctionnel;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public Fournisseur getFournisseur() {
		return fournisseur;
	}

	public void setFournisseur(Fournisseur fournisseur) {
		this.fournisseur = fournisseur;
	}

}
