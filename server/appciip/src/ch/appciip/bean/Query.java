/**
 * Bean de gestion des notes
 * @author Ga�l TEGUIA TEGUIA
 */
package ch.appciip.bean;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Query implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String content;
	private String contenu;
	private DateTime date;
	private DateTime echeance;

	private User user;

	public Query(String content, String contenu, DateTime date, DateTime echeance, User u) {
		this.content = content;
		this.contenu = contenu;
		this.date = date;
		this.echeance = echeance;
		this.user = u;
	}

	public Query() {
	}

	public Query(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public String getContenu() {
		return contenu;
	}

	public DateTime getDate() {
		return date;
	}

	public User getAuteur() {
		return user;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public void setAuteur(User user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DateTime getEcheance() {
		return echeance;
	}

	public void setEcheance(DateTime echeance) {
		this.echeance = echeance;
	}
}
