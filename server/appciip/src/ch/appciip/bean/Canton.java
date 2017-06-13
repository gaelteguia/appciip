/**
 * Bean de gestion des applications
 * @author Ga�l TEGUIA TEGUIA
 */

package ch.appciip.bean;

import java.io.Serializable;

public class Canton implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;
	private String acronym;

	private String website;

	public Canton(Long id) {
		this.id = id;
	}

	public Canton(Long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Canton(String name) {
		this.name = name;
	}

	public Canton() {
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Long id) {
		this.id = id;

	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

}
