package com.smart.emailconfig;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;
@Service
public class OtpVerification {
	boolean f=false;
	String from="anandkishorjava@gmail.com";
	//String host="smtp.gmail.com";
	/*
	 * @Autowired private JavaMailSender mailSender;
	 */
    public boolean sendEmailOtp(String subject,String message,String to) {
    	Properties props = new Properties();
    	   props.put("mail.smtp.auth", "true");
    	   props.put("mail.smtp.starttls.enable", "true");
    	   props.put("mail.smtp.host", "smtp.gmail.com");
    	   props.put("mail.smtp.port", "587");
    	   props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    	   Session session = Session.getInstance(props, new javax.mail.Authenticator() {
    		      protected PasswordAuthentication getPasswordAuthentication() {
    		         return new PasswordAuthentication("anandkishorjava@gmail.com", "anand@9643");
    		      }
    		   });
    	   session.setDebug(true);
    	   MimeMessage m=new MimeMessage(session);
		   try {
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			m.setSubject(subject);
			m.setText(message);
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


