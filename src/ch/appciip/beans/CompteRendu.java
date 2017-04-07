package ch.appciip.beans;

import java.sql.Date;

import org.joda.time.DateTime;

public class CompteRendu {
	private Long id;
	private String titre;
	private String description;
	private String type;
	private String lien;
	private DateTime date;
	private boolean offre;
	private Service service;

	private Utilisateur collaborateur;

	public CompteRendu(Long id) {
		this.id = id;
	}

	public CompteRendu() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public boolean isOffre() {
		return offre;
	}

	public void setOffre(boolean offre) {
		this.offre = offre;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLien() {
		return lien;
	}

	public void setLien(String lien) {
		this.lien = lien;
	}

	public Utilisateur getCollaborateur() {
		return collaborateur;
	}

	public void setCollaborateur(Utilisateur collaborateur) {
		this.collaborateur = collaborateur;
	}

}
