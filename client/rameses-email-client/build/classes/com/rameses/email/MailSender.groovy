package com.rameses.email;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.common.*;
import com.rameses.util.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.rcp.framework.*;

public class MailSender {
    
    
    private Map conf;
    private String smtphost;
    
    public MailSender( Map conf ) {
        this.conf = conf;
        smtphost = (String)conf.get("mail.smtp.host");
    }
    
    public void send( Map info ) throws Exception {
        if(smtphost == null) {
            throw new Exception("MailSender error. There is no mail.smtp.host defined in conf");
        }
        
        String from = (String)conf.get("mail.from"); 
        
	String to = (String) info.get("to");
	String subject = (String)info.get("subject");
	String msg = (String)info.get("message");
        List<String> attachments = (List)info.get("attachments");
        
        try { 
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.host", smtphost); 
            properties.setProperty("mail.smtp.port", "25");
            Session session = Session.getDefaultInstance( properties );   

            MimeMessage message = new MimeMessage(session);  
            if(from !=null ) message.setFrom(new InternetAddress(from));  
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); 
            if (subject !=null ) message.setSubject(subject); 

            if ( attachments!=null && attachments.size() > 0 ) {
                Multipart multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent( (msg==null?"":msg),"text/html");
                multipart.addBodyPart(messageBodyPart);
                for( String filename : attachments ) {
                    messageBodyPart = new MimeBodyPart();
                    File f = new File(filename);
                    DataSource source = new FileDataSource(filename);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);
                    multipart.addBodyPart(messageBodyPart);
                }
                message.setContent(multipart); 
            } 
            else if ( msg != null ) { 
                message.setContent(msg, "text/html");
            }

            Transport.send(message); 
        } 
        catch (RuntimeException re) { 
            throw re;  
        } 
        catch (Exception e) {  
            throw e; 
        } 
        
    }
    
    
}