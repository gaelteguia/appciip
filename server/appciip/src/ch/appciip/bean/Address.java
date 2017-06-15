package ch.appciip.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Address implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;

	private String city;
	private String street;

	private int npa;
	private int cp;

	private int number;
	private String telephone;
	private String fax;
	private String country;
	private String canton;
	private String type;
	private int etage;

	private Collection<Manifestation> manifestations = new Vector<Manifestation>();

	public Address(String city, String street) {
		this.city = city;
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Address(Long id) {
		this.id = id;
	}

	public Address(Long id, String name, String city, int npa, String street, int number, String country,
			String telephone) {
		this.id = id;
		this.name = name;
		this.city = city;
		this.npa = npa;
		this.street = street;
		this.number = number;
		this.telephone = telephone;
		this.country = country;
	}

	public Address(String name, String city, int npa, String street, int number, String country, String telephone) {

		this.name = name;
		this.city = city;
		this.npa = npa;
		this.street = street;
		this.number = number;
		this.telephone = telephone;
		this.country = country;
	}

	public Address(String country, String city, String street, int npa, int number, String telephone) {
		this.country = country;
		this.city = city;
		this.street = street;
		this.npa = npa;
		this.number = number;
		this.telephone = telephone;

	}

	public Address(String name) {
		this.name = name;
	}

	public Address() {
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRue() {
		return street;
	}

	public void setRue(String street) {
		this.street = street;
	}

	public int getNpa() {
		return npa;
	}

	public void setNpa(String npa) {
		this.npa = Integer.parseInt(npa);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setNpa(Integer npa) {
		this.npa = npa;

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
