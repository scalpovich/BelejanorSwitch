package com.belejanor.switcher.notifications;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;

public class MailSender {

	 private Logger log;
	 protected String domain;
     protected String host;
     protected String port;
     protected String username;
     protected String password;
     
     public MailSender(String domain, String host, String port, String username,
             String password) {
	     super();
	     this.domain = domain;
	     this.host = host;
	     this.port = port;
	     this.username = username;
	     this.password = password;
	     this.log = new Logger();
     }
	public String getDomain() {
	     return domain;
	}
	public void setDomain(String domain) {
	     this.domain = domain;
	}
	public String getHost() {
	     return host;
	}
	public void setHost(String host) {
	     this.host = host;
	}
	public String getPort() {
	     return port;
	}
	public void setPort(String port) {
	     this.port = port;
	}
	public String getUsername() {
	     return username;
	}
	public void setUsername(String username) {
	     this.username = username;
	}
	public String getPassword() {
	     return password;
	}
	public void setPassword(String password) {
	     this.password = password;
	}
	
	public MailSender() {
	     
		this.log = new Logger();
	}
	
	public void sendEmail(ArrayList<String> to, String subject, String content, Ref<String> refError){
		    
			refError.set("OK");
					
	        Properties properties = new Properties();
	        properties.setProperty("mail.smtp.host", host); 
	        properties.setProperty("mail.smtp.starttls.enable", "true");
	        
	        properties.setProperty("mail.smtp.port", port);
	        properties.setProperty("mail.smtp.user", username);
	        properties.setProperty("mail.smtp.auth", "true");
	                                
	        Session session = Session.getInstance(properties);
	        //session.setDebug(true);
	        
	        MimeMessage message  = new MimeMessage(session);
	        Transport transport = null;
	        try{
                message.setFrom(new InternetAddress(username));
                message.setSubject(subject);
                InternetAddress[] address = new InternetAddress[to.size()];
                for(int i = 0; i < to.size(); i++){
                        address[i] = new InternetAddress(to.get(i));                    
                }
                message.setRecipients(Message.RecipientType.TO, address);
                message.setText(content, "ISO-8859-1", "html");
                
                transport = session.getTransport("smtp");
                transport.connect(username, password);
                transport.sendMessage(message, message.getAllRecipients());
                
	        }
	        catch (Exception e) {
	        
	        	log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::sendEmail"
						, TypeMonitor.error, e);
	        	refError.set(GeneralUtils.ExceptionToString("Error al enviar mail ", e, false));
	        	
	        }finally {
				
        		try {
        			if(transport.isConnected())
						transport.close();
				} catch (MessagingException e) {
					log.WriteLogMonitor("Error cerrar conexion Modulo" + this.getClass().getName() + "::sendEmail"
							, TypeMonitor.error, e);
					refError.set(GeneralUtils.ExceptionToString("Error al cerrar conexion SendMail ", e, false));
				}
			} 
   }
}

