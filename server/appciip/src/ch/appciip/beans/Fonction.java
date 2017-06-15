/**
 * Bean de gestion des profils m�tiers
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import org.joda.time.DateTime;

public class Fonction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nom;
	private String description;
	private boolean valide;
	private String identifiant;
	private DateTime dateValidation;
	private String commentaires;
	private Service service;
	private Collection<Localisation> profils = new Vector<Localisation>();
	private Collection<Utilisateur> collaborateurs = new Vector<Utilisateur>();

	public Fonction(Long id) {
		this.id = id;
	}

	public Fonction(Long id, String nom, String description, boolean valide, String identifiant,
			DateTime dateValidation, String commentaires, Service service) {
		this.id = id;
		this.nom = nom;
		this.description = description;
		this.valide = valide;
		this.identifiant = identifiant;
		this.dateValidation = dateValidation;
		this.commentaires = commentaires;
		this.service = service;
	}

	public Fonction(Long id, String nom, String description, boolean valide) {
		this.id = id;
		this.nom = nom;
		this.description = description;
		this.valide = valide;
	}

	public Fonction(String nom) {
		this.nom = nom;
	}

	public Fonction() {
	}

	public Long getId() {
		return id;
	}

	public DateTime getDateValidation() {
		return dateValidation;
	}

	public String getCommentaires() {
		return commentaires;
	}

	public String getDescription() {
		return description;
	}

	public String getNom() {

		return nom;
	}

	public boolean getValide() {

		return valide;
	}

	public Service getService() {

		return service;
	}

	public String getIdentifiant() {

		return identifiant;
	}

	public void setNom(String nom) {

		this.nom = nom;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public void setValide(boolean valide) {

		this.valide = valide;
	}

	public void setIdentifiant(String identifiant) {

		this.identifiant = identifiant;
	}

	public void setDateValidation(DateTime datevalidation) {

		this.dateValidation = datevalidation;
	}

	public void setCommentaires(String commentaires) {

		this.commentaires = commentaires;
	}

	public void setService(Service s) {

		this.service = s;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public Collection<Localisation> getComites() {
		return profils;
	}

	public void setComites(Collection<Localisation> profils) {
		this.profils = profils;
	}

	public Collection<Utilisateur> getCollaborateurs() {
		return collaborateurs;
	}

	public void setCollaborateurs(Collection<Utilisateur> collaborateurs) {
		this.collaborateurs = collaborateurs;
	}

}
