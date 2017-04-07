/**
 * Bean de gestion des droits d'acc�s
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import org.joda.time.DateTime;

public class Seance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String libelle;
	private String description;
	private DateTime date;

	private Adresse adresse;
	private Localisation comite;
	private Utilisateur responsable;

	private Collection<Utilisateur> participants = new Vector<Utilisateur>();
	private Collection<Localisation> comites = new Vector<Localisation>();

	public Seance(String libelle) {
		this.libelle = libelle;
	}

	public Seance() {
	}

	public Seance(Long id) {
		this.id = id;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collection<Utilisateur> getParticipants() {
		return participants;
	}

	public void setParticipants(Collection<Utilisateur> participants) {
		this.participants = participants;
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

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	public Localisation getComite() {
		return comite;
	}

	public void setComite(Localisation comite) {
		this.comite = comite;
	}

	public Utilisateur getResponsable() {
		return responsable;
	}

	public void setResponsable(Utilisateur responsable) {
		this.responsable = responsable;
	}

	public Collection<Localisation> getComites() {
		return comites;
	}

	public void setComites(Collection<Localisation> comites) {
		this.comites = comites;
	}

}
