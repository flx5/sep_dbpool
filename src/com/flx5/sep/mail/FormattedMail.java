package com.flx5.sep.mail;

public class FormattedMail {
	private String recipient;
	private String subject;
	private String text;
	private String html;

	public FormattedMail(String recipient, String subject, String textMessage, String htmlMessage) {
		super();
		this.recipient = recipient;
		this.subject = subject;
		this.text = textMessage;
		this.html = htmlMessage;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
}