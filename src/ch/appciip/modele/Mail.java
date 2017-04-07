package modele;

public class Mail {

	private String subject;
	private String sender;
	private String message;
	private String recipient;

	public String getSubject() {
		return subject;
	}

	public Mail(String subject, String sender, String message, String recipient) {
		this.subject = subject;
		this.message = message;
		this.sender = sender;
		this.recipient = recipient;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
}
