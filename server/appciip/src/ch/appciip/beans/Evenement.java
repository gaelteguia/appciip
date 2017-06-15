/**
 * Bean de gestion des notes
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.beans;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Evenement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String titre;
	private String description;
	private DateTime date;
	private Adresse adresse;
	private Utilisateur collaborateur;

	public Evenement(String titre, String description, DateTime date, Utilisateur u) {
		this.titre = titre;
		this.description = description;
		this.date = date;
		this.collaborateur = u;
	}

	public Evenement() {
	}

	public Evenement(Long id) {
		this.id = id;
	}

	public String getTitre() {
		return titre;
	}

	public String getDescription() {
		return description;
	}

	public DateTime getDate() {
		return date;
	}

	public Utilisateur getCollaborateur() {
		return collaborateur;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public void setCollaborateur(Utilisateur collaborateur) {
		this.collaborateur = collaborateur;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}
}
