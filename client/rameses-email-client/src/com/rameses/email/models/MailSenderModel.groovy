package com.rameses.email.models;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.rameses.email.*;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.common.*;
import com.rameses.util.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.rcp.framework.*;


public class MailSenderModel {
    
    @Caller
    def caller;
    
    String name;
    String mailto;
    def entity;
    def mail = [:];
    def attachments = [];
    def conf = ClientContext.getCurrentContext().getAppEnv();
    
    def getQuerySvc() {
        return InvokerProxy.instance.create("QueryService", null, caller.getConnection() );
    }
    
    def buildMessage()  {
        def m = [_schemaname: "sys_email_template" ];
        m.findBy = [objid: name ];
        def z = querySvc.findFirst( m );
        if(z) {
            //place here the template substitution
            def txt = z.message;
            def binding = [entity: entity];
            def engine = new groovy.text.SimpleTemplateEngine(); 
            def template = engine.createTemplate(txt).make(binding); 
            return template;
        }
        return null;
    } 
    
    void init() {
        if( !name ) throw new Exception("Please specify name in mail_sender");
        if( !entity ) throw new Exception("entity is required in mail sender");
        if(mailto) mail.to = mailto;
        mail.message = buildMessage();
        if(attachments) {
            attachments.each {
                if(!it.params) it.params = [entity: entity];
                it.opener = Inv.lookupOpener( it.handler, it.params );
                it.file = new File( it.filename );
            }
        }
    }
    
    def doOk() {
        def m = [:];
        m.to = mail.to;
        m.subject = mail.subject;
        m.message = mail.message;
        try {
            if(attachments) {
                mail.attachments = [];
                attachments.each { a->
                     a.opener.handle.exportToPDF(a.file);
                     mail.attachments << a.filename;
                }
            }
            MailSender ms = new MailSender(conf);
            ms.send( mail );
            MsgBox.alert("Message sent!");
            return "_close";
        }
        catch(e) {
            throw e;
        }
        finally {
            //cleanup files after sending
            attachments.each {
                it.file.delete();
            }
        }
    }
    
    def doCancel() {
        return "_close";
    }
    
    boolean getHasAttachments() {
        return true;
    }
}