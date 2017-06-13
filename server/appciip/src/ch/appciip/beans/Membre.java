package ch.appciip.beans;

import java.sql.Date;
import java.util.List;

public class Membre {

	private int id;
	private String nom;
	private String prenom;
	private String email;
	private Date ddn;
	private String poste;
	private Adresse adresse;
	private List<Salle> ecoles;
	private List<Vacance> filieres;

	public Membre(int id) {
		this.id = id;
	}

	public Membre(String nom, String prenom, String email, Date ddn, String poste) {

	}

	public void setNom(String nom) {

		this.nom = nom;
	}

	public String getNom() {
		return nom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public String getPoste() {
		return poste;
	}

	public void setPoste(String poste) {
		this.poste = poste;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDdn() {
		return ddn;
	}

	public void setDdn(Date ddn) {
		this.ddn = ddn;
	}

	public List<Salle> getEcoles() {
		return ecoles;
	}

	public void setEcoles(List<Salle> ecoles) {
		this.ecoles = ecoles;
	}

	public List<Vacance> getFilieres() {
		return filieres;
	}

	public void setFilieres(List<Vacance> filieres) {
		this.filieres = filieres;
	}
}