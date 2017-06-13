package ch.appciip.beans;

import java.sql.Date;

public class Directive {
	private Long id;
	private String titre;
	private String description;
	private String theme;
	private String lien;
	private Date date;
	private boolean offre;
	private Membre auteur;

	public Directive(Long id) {
		this.id = id;
	}

	public Directive() {
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

	public Membre getAuteur() {
		return auteur;
	}

	public void setAuteur(Membre auteur) {
		this.auteur = auteur;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getLien() {
		return lien;
	}

	public void setLien(String lien) {
		this.lien = lien;
	}

}
