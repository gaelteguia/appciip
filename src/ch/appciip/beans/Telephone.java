/**
 * Bean de gestion des applications
 * @author Ga�l TEGUIA TEGUIA
 */

package ch.appciip.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import org.joda.time.DateTime;

public class Telephone implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private Long id;
	private String numero;
	private String marque;
	private Float amortissement;
	private String type;
	private DateTime dateAchat;
	private DateTime datePeremption;
	private BigDecimal prix;
	private Utilisateur collaborateur;
	private Fournisseur fournisseur;
	private Salle salle;

	public Telephone(Long id, String numero, BigDecimal prix, Utilisateur collaborateur, Fournisseur fournisseur) {
		this.id = id;
		this.numero = numero;
		this.prix = prix;
		this.collaborateur = collaborateur;
		this.fournisseur = fournisseur;
	}

	public Telephone(Long id) {
		this.id = id;
	}

	public Telephone(Long id, String numero, BigDecimal prix) {
		this.id = id;
		this.numero = numero;
		this.prix = prix;
	}

	public Telephone(String numero) {
		this.numero = numero;
	}

	public Telephone(Long id, String numero, BigDecimal prix, Utilisateur collaborateur) {
		this.id = id;
		this.numero = numero;
		this.prix = prix;
		this.collaborateur = collaborateur;
	}

	public Telephone() {
	}

	public String getNom() {
		return numero;
	}

	public BigDecimal getPrix() {
		return prix;
	}

	public Long getId() {
		return id;
	}

	public Utilisateur getCollaborateur() {
		return collaborateur;
	}

	public void setNom(String numero) {
		this.numero = numero;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getMarque() {
		return marque;
	}

	public void setMarque(String marque) {
		this.marque = marque;
	}

	public Float getAmortissement() {
		return amortissement;
	}

	public void setAmortissement(Float amortissement) {
		this.amortissement = amortissement;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DateTime getDateAchat() {
		return dateAchat;
	}

	public void setDateAchat(DateTime dateAchat) {
		this.dateAchat = dateAchat;
	}

	public DateTime getDatePeremption() {
		return datePeremption;
	}

	public void setDatePeremption(DateTime datePeremption) {
		this.datePeremption = datePeremption;
	}

	public void setPrix(BigDecimal prix) {
		this.prix = prix;
	}

	public void setCollaborateur(Utilisateur collaborateur) {
		this.collaborateur = collaborateur;
	}

	public Fournisseur getFournisseur() {
		return fournisseur;
	}

	public void setFournisseur(Fournisseur fournisseur) {
		this.fournisseur = fournisseur;
	}

	public Salle getSalle() {
		return salle;
	}

	public void setSalle(Salle salle) {
		this.salle = salle;
	}

}
