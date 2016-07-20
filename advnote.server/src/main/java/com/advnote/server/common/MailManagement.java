package com.advnote.server.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.advnote.server.model.config.Config;
import com.advnote.server.model.config.ConfigDAO;
import com.advnote.server.model.usuario.Usuario;

public class MailManagement {
	private static final String UNAUTHORIZED_MAIL_FILE = "unauthorizedMail.html";
	
	
	public void sendMailUnauthorized(Usuario usuario){
		File file = new File(UNAUTHORIZED_MAIL_FILE);
		String content = htmlFileToString(file);
		content.replaceAll("link_redirect", "http://www.google.com.br");
		//sendMail(usuario, content);
	}
	
	public void sendMailWelcome(Usuario usuario){
		//Redirect user page
	}
	
	public void sendMailWaiting(Usuario usuario){
		//Waiting a moment
	}
	
	public static void sendMail(String from, String to, String displayNameTo, String subject, String message, List<Config> configurations)
			throws AddressException, MessagingException {
		Map<Integer, String> map = new HashMap<>();
		configurations.forEach(config -> map.put(config.getKey(), config.getValue()));

		String smtpServer = map.get(ConfigDAO.MAIL_ACCOUNT_SMTP_URL);
		String smtpPort = map.get(ConfigDAO.MAIL_ACCOUNT_SMTP_PORT);
		String smtpUser = map.get(ConfigDAO.MAIL_ACCOUNT_NAME);
		String password = map.get(ConfigDAO.MAIL_ACCOUNT_PASSWORD);

		if (password != null && !password.isEmpty()) {
			//password = AES.decrypt(password);
		}

		if (smtpServer == null || smtpPort == null) {
			throw new MessagingException();
		}

		Properties props = new Properties();
//		props.put(MAIL_SMTP_AUTH, TRUE);
//		props.put(MAIL_SMTP_STARTTLS_ENABLE, TRUE);
//		props.put(MAIL_SMTP_HOST, smtpServer);
//		props.put(MAIL_SMTP_PORT, smtpPort);

		javax.mail.Authenticator auth;
		Session session;
		final String smtpPassword = password;
		if (smtpUser != null && !smtpUser.isEmpty() && password != null && !password.isEmpty()) {
			auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUser, smtpPassword);
				}
			};
			session = Session.getInstance(props, auth);
		} else {
			session = Session.getInstance(props);
		}

		MimeMessage mailMessage = new MimeMessage(session);

		try {
			InternetAddress toAddress = (displayNameTo != null) ? new InternetAddress(to, displayNameTo) : new InternetAddress(to);

			//mailMessage.setFrom(new InternetAddress(from, Constants.HEROES_MAIL_FROM_NAME));
			mailMessage.setRecipients(Message.RecipientType.TO, new InternetAddress[] { toAddress });
		} catch (UnsupportedEncodingException e) {
			throw new AddressException();
		}

		//mailMessage.setSubject(subject, Constants.UTF8_ENCODING);
		//mailMessage.setText(message, Constants.UTF8_ENCODING);

		Transport.send(mailMessage);
	}
	
	private String htmlFileToString(File file){
		StringBuilder contentBuilder = new StringBuilder();
		String content = "";
		try {
		    BufferedReader in = new BufferedReader(new FileReader(file));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		    content = contentBuilder.toString();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return content;
	}

}
