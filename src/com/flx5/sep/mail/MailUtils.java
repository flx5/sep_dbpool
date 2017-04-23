package com.flx5.sep.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailUtils {

	private static final String USER = "";
	private static final String FROM = USER + "@fim.uni-passau.de";
	private static final String PASSWORD = "";

	private MailUtils() {
		// utility class
	}

	public static void main(String[] args) throws MessagingException {
		Session session = getGMailSession(USER, PASSWORD);

		// Send the mail back to myself :)
		String to = FROM;

		FormattedMail message = new FormattedMail(to, "Test Java", "Hello, World", "<i><b>Hello, World</b></i>");
		
		postMail(session, message);
	}

	public static Session getGMailSession(String user, String pass) {
		final Properties props = new Properties();

		// SMTP
		props.setProperty("mail.smtp.host", "mail.fim.uni-passau.de");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");

		return Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		});
	}

	public static void postMail(Session session, FormattedMail message)
			throws MessagingException {
		Message msg = new MimeMessage(session);

		InternetAddress addressTo = new InternetAddress(message.getRecipient());
		msg.setRecipient(Message.RecipientType.TO, addressTo);
		msg.setFrom(new InternetAddress(FROM));
		msg.setSubject(message.getSubject());

		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(message.getText(), "text/plain");

		final MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(message.getHtml(), "text/html");
		
        final Multipart multipart = new MimeMultipart("alternative");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);
        
		msg.setContent(multipart);
		Transport.send(msg);
	}
}
