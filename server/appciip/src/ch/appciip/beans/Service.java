/**
 * Bean de gestion des services
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public class Service implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nom;
	private String acronyme;
	private Service departement;
	private Utilisateur superviseur;
	private Collection<Adresse> adresses = new Vector<Adresse>();
	private Collection<Fonction> fonctions = new Vector<Fonction>();

	public Service(Long id) {
		this.id = id;
	}

	public Service(Long id, String nom, String acronyme, Service departement, Utilisateur superviseur) {
		this.id = id;
		this.nom = nom;
		this.acronyme = acronyme;
		this.departement = departement;
		this.superviseur = superviseur;
	}

	public Service(String acronyme) {
		this.acronyme = acronyme;
	}

	public Service() {
	}

	public Long getId() {
		return id;
	}

	public Service getDepartement() {
		return departement;
	}

	public String getAcronyme() {
		return acronyme;
	}

	public String getNom() {
		return nom;
	}

	public Utilisateur getSuperviseur() {
		return superviseur;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setAcronyme(String acronyme) {
		this.acronyme = acronyme;
	}

	public void setDepartement(Service departement) {
		this.departement = departement;
	}

	public void setSuperviseur(Utilisateur superviseur) {

		this.superviseur = superviseur;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public Collection<Adresse> getAdresses() {
		return adresses;
	}

	public void setAdresses(Collection<Adresse> adresses) {
		this.adresses = adresses;
	}

	public Collection<Fonction> getFonctions() {
		return fonctions;
	}

	public void setFonctions(Collection<Fonction> fonctions) {
		this.fonctions = fonctions;
	}

}
