package ch.appciip.bean;

import java.util.Collection;
import java.util.Vector;

public class Institution {
	private Long id;
	private String name;

	private String type;
	private String website;
	private String description;

	private String acronyme;
	private Institution department;
	private User supervisor;
	private Collection<Address> adresses = new Vector<Address>();

	private Collection<Manifestation> manifestations = new Vector<Manifestation>();

	public Long getId() {
		return id;
	}

	public Institution(Long id) {
		this.id = id;
	}

	public Institution() {

	}

	public Institution(String name) {
		this.name = name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Collection<Manifestation> getManifestations() {
		return manifestations;
	}

	public void setManifestations(Collection<Manifestation> manifestations) {
		this.manifestations = manifestations;
	}

	public String getAcronyme() {
		return acronyme;
	}

	public void setAcronyme(String acronyme) {
		this.acronyme = acronyme;
	}

	public Institution getDepartment() {
		return department;
	}

	public void setDepartment(Institution department) {
		this.department = department;
	}

	public User getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}

	public Collection<Address> getAdresses() {
		return adresses;
	}

	public void setAdresses(Collection<Address> adresses) {
		this.adresses = adresses;
	}

}
