package ch.appciip.beans;

import java.sql.Date;

public class Outil {
	private Long id;
	private String nom;
	private String description;
	private String type;
	private String lien;
	private Date date;
	private boolean offre;
	private Service service;

	public Outil(Long id) {
		this.id = id;
	}

	public Outil() {
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

}
