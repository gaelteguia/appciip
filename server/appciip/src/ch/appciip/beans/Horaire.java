package ch.appciip.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public class Horaire implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private String nom;

	private Collection<Voiture> voitures = new Vector<Voiture>();
	private Collection<Utilisateur> collaborateurs = new Vector<Utilisateur>();

	public Horaire() {
	}

	public Horaire(String nom) {
		this.nom = nom;
	}

	public Horaire(Long id) {
		this.id = id;
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

	public void setVoitures(Collection<Voiture> voitures) {
		this.voitures = voitures;
	}

	public Collection<Voiture> getVoitures() {
		return voitures;
	}

	public Collection<Utilisateur> getCollaborateurs() {
		return collaborateurs;
	}

	public void setCollaborateurs(Collection<Utilisateur> collaborateurs) {
		this.collaborateurs = collaborateurs;
	}

}