/**
 * Bean de gestion des applications
 * @author Ga�l TEGUIA TEGUIA
 */

package ch.appciip.bean;

import java.io.Serializable;

public class Weather implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private Long id;
	private String content;

	public Weather(Long id, String content) {
		this.id = id;
		this.content = content;

	}

	public Weather(Long id) {
		this.id = id;
	}

	public Weather(String content) {
		this.content = content;
	}

	public Weather() {
	}

	public String getContent() {
		return content;
	}

	public Long getId() {
		return id;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(Long id) {
		this.id = id;

	}

}
