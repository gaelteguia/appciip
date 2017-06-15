/**
 * Bean de gestion des profils d'acc�s
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public class Localisation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String content;
	private String acronyme;
	private boolean statut;
	private boolean officiel;
	private String description;
	private String website;

	private Localisation parent;
	private Collection<User> users = new Vector<User>();

	public Localisation(Long id, String content, User responsable) {
		this.id = id;
		this.content = content;

	}

	public Localisation(Long id) {
		this.id = id;
	}

	public Localisation() {
	}

	public Localisation(String acronyme) {
		this.acronyme = acronyme;
	}

	public Long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collection<User> getParticipants() {
		return users;
	}

	public void setParticipants(Collection<User> users) {
		this.users = users;
	}

	public String getAcronyme() {
		return acronyme;
	}

	public void setAcronyme(String acronyme) {
		this.acronyme = acronyme;
	}

	public boolean isStatut() {
		return statut;
	}

	public void setStatut(boolean statut) {
		this.statut = statut;
	}

	public boolean isOfficiel() {
		return officiel;
	}

	public void setOfficiel(boolean officiel) {
		this.officiel = officiel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Localisation getParent() {
		return parent;
	}

	public void setParent(Localisation parent) {
		this.parent = parent;
	}

}
