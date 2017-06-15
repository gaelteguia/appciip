/**
 * Bean de gestion des notes
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.beans;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Recherche implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String titre;
	private String contenu;
	private DateTime date;
	private DateTime echeance;

	private Utilisateur auteur;

	public Recherche(String titre, String contenu, DateTime date, DateTime echeance, Utilisateur u) {
		this.titre = titre;
		this.contenu = contenu;
		this.date = date;
		this.echeance = echeance;
		this.auteur = u;
	}

	public Recherche() {
	}

	public Recherche(Long id) {
		this.id = id;
	}

	public String getTitre() {
		return titre;
	}

	public String getContenu() {
		return contenu;
	}

	public DateTime getDate() {
		return date;
	}

	public Utilisateur getAuteur() {
		return auteur;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public void setAuteur(Utilisateur auteur) {
		this.auteur = auteur;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DateTime getEcheance() {
		return echeance;
	}

	public void setEcheance(DateTime echeance) {
		this.echeance = echeance;
	}
}
