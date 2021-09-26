package com.smart.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject,String message,String to)
	{
		boolean f=false;
		String from="anandkishorjava@gmail.com";
		String host="smtp.gmail.com";
		Properties properties=System.getProperties();
		System.out.println("PROPERTIES :"+properties);
		 Properties props = new Properties();
		   props.put("mail.smtp.host", host);
		   props.put("mail.smtp.port", "587");
		   props.put("mail.smtp.ssl.enable", "true");
		   props.put("mail.smtp.auth", "true");
			/*
			 * props.put("mail.smtp.host", "smtp.gmail.com"); props.put("mail.smtp.port",
			 * "587");
			 */
			/*
			 * Properties props = new Properties(); props.put("mail.smtp.auth", "true");
			 * props.put("mail.smtp.starttls.enable", "true"); props.put("mail.smtp.host",
			 * "smtp.gmail.com"); props.put("mail.smtp.port", "587");
			 */
		   
		   Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			      protected PasswordAuthentication getPasswordAuthentication() {
			         return new PasswordAuthentication("anandkishorjava@gmail.com", "9643225141");
			      }
		   
	});
		   session.setDebug(true);
		   MimeMessage m=new MimeMessage(session);
		   try {
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			m.setSubject("");
			m.setText(message);
			m.setContent(message, "tex/html");
			Transport.send(m);
			System.out.println("send success..........");
			f=true;
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		   return f;
		   }
	

}
