package ch.appciip.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public class Adresse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String nom;
	private String localite;
	private int npa;
	private int cp;
	private String rue;
	private int noRue;
	private String telephone;
	private String fax;
	private String pays;
	private String canton;
	private String type;
	private int etage;
	private Collection<Service> services = new Vector<Service>();
	private Collection<Evenement> evenements = new Vector<Evenement>();
	private Collection<Manifestation> manifestations = new Vector<Manifestation>();

	public Adresse(Long id) {
		this.id = id;
	}

	public Adresse(Long id, String nom, String localite, int npa, String rue, int noRue, String pays,
			String telephone) {
		this.id = id;
		this.nom = nom;
		this.localite = localite;
		this.npa = npa;
		this.rue = rue;
		this.noRue = noRue;
		this.telephone = telephone;
		this.pays = pays;
	}

	public Adresse(String nom, String localite, int npa, String rue, int noRue, String pays, String telephone) {

		this.nom = nom;
		this.localite = localite;
		this.npa = npa;
		this.rue = rue;
		this.noRue = noRue;
		this.telephone = telephone;
		this.pays = pays;
	}

	public Adresse(String nom) {
		this.nom = nom;
	}

	public Adresse() {
	}

	public Long getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public String getNomCourt() {
		return telephone;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setNomCourt(String telephone) {
		this.telephone = telephone;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public Collection<Service> getServices() {
		return services;
	}

	public void setServices(Collection<Service> services) {
		this.services = services;
	}

	public Adresse(String pays, String localite, String rue, int npa, int noRue, String telephone) {
		this.pays = pays;
		this.localite = localite;
		this.rue = rue;
		this.npa = npa;
		this.noRue = noRue;
		this.telephone = telephone;

	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPays() {
		return pays;
	}

	public void setPays(String pays) {
		this.pays = pays;
	}

	public String getLocalite() {
		return localite;
	}

	public void setLocalite(String localite) {
		this.localite = localite;
	}

	public String getRue() {
		return rue;
	}

	public void setRue(String rue) {
		this.rue = rue;
	}

	public int getNpa() {
		return npa;
	}

	public void setNpa(String npa) {
		this.npa = Integer.parseInt(npa);
	}

	public int getNoRue() {
		return noRue;
	}

	public void setNoRue(int noRue) {
		this.noRue = noRue;
	}

	public void setNpa(Integer npa) {
		this.npa = npa;

	}

	public Collection<Evenement> getEvenements() {
		return evenements;
	}

	public void setEvenements(Collection<Evenement> evenements) {
		this.evenements = evenements;
	}

	public String getCanton() {
		return canton;
	}

	public void setCanton(String canton) {
		this.canton = canton;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getEtage() {
		return etage;
	}

	public void setEtage(int etage) {
		this.etage = etage;
	}

	public int getCp() {
		return cp;
	}

	public void setCp(int cp) {
		this.cp = cp;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Collection<Manifestation> getManifestations() {
		return manifestations;
	}

	public void setManifestations(Collection<Manifestation> manifestations) {
		this.manifestations = manifestations;
	}

}
