/**
 * Bean de gestion des profils d'acc�s
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public class Localisation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nom;
	private String acronyme;
	private boolean statut;
	private boolean officiel;
	private String description;
	private String website;
	private Utilisateur responsable;
	private Localisation parent;
	private Collection<Utilisateur> participants = new Vector<Utilisateur>();
	private Collection<Seance> seances = new Vector<Seance>();

	public Localisation(Long id, String nom, Utilisateur responsable) {
		this.id = id;
		this.nom = nom;
		this.responsable = responsable;
	}

	public Localisation(Long id) {
		this.id = id;
	}

	public Localisation() {
	}

	public Localisation(String acronyme) {
		this.acronyme = acronyme;
	}

	public Long getId() {
		return id;
	}

	public Utilisateur getResponsable() {
		return responsable;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setResponsable(Utilisateur responsable) {
		this.responsable = responsable;
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

	public Collection<Seance> getSeances() {
		return seances;
	}

	public void setSeances(Collection<Seance> seances) {
		this.seances = seances;
	}

	public String getAcronyme() {
		return acronyme;
	}

	public void setAcronyme(String acronyme) {
		this.acronyme = acronyme;
	}

	public boolean isStatut() {
		return statut;
	}

	public void setStatut(boolean statut) {
		this.statut = statut;
	}

	public boolean isOfficiel() {
		return officiel;
	}

	public void setOfficiel(boolean officiel) {
		this.officiel = officiel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Localisation getParent() {
		return parent;
	}

	public void setParent(Localisation parent) {
		this.parent = parent;
	}

}
