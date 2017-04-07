package ch.appciip.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public class Voiture implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String noPlaques;
	private Utilisateur collaborateur;
	private Collection<Horaire> horaires = new Vector<Horaire>();

	public Voiture() {
	}

	public Voiture(Long id) {
		this.id = id;
	}

	public Voiture(String noPlaques) {
		this.noPlaques = noPlaques;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getNoPlaques() {
		return noPlaques;
	}

	public void setNoPlaques(String noPlaques) {
		this.noPlaques = noPlaques;
	}

	public Utilisateur getCollaborateur() {
		return collaborateur;
	}

	public void setCollaborateur(Utilisateur collaborateur) {
		this.collaborateur = collaborateur;
	}

	public Collection<Horaire> getHoraires() {
		return horaires;
	}

	public void setHoraires(Collection<Horaire> horaires) {
		this.horaires = horaires;
	}

}