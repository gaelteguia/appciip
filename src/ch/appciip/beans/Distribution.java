package ch.appciip.beans;

import java.sql.Date;
import java.util.Collection;
import java.util.Vector;

public class Distribution {
	private Long id;
	private String nom;
	private String description;
	private Date date;
	private Membre auteur;

	private Collection<Utilisateur> collaborateurs = new Vector<Utilisateur>();

	public Distribution(String nom) {
		this.nom = nom;
	}

	public Distribution(Long id) {
		this.id = id;
	}

	public Distribution() {

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Membre getAuteur() {
		return auteur;
	}

	public void setAuteur(Membre auteur) {
		this.auteur = auteur;
	}

	public Collection<Utilisateur> getCollaborateurs() {
		return collaborateurs;
	}

	public void setCollaborateurs(Collection<Utilisateur> collaborateurs) {
		this.collaborateurs = collaborateurs;
	}

}
