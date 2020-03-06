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


public class MailTemplateModel {
    
    @Caller
    def caller;
    
    String name;
    String template;
    def handler;
    def mode = "create";
    
    def getQuerySvc() {
        return InvokerProxy.instance.create("QueryService", null, caller.getConnection() );
    }

    def getPersistenceSvc() {
        return InvokerProxy.instance.create("PersistenceService", null, caller.getConnection() );
    }
    
    
    void init() {
        def m =[_schemaname: "sys_email_template"];
        m.findBy = [objid: name];
        def z = querySvc.findFirst( m );
        if( z ) {
            template = z.message;
            mode = "edit";
        }
    }
    
    def doOk() {
        def p = [_schemaname:"sys_email_template", objid:name, message:template];
        if(mode=="create") {
            persistenceSvc.create( p );
        }
        else {
            persistenceSvc.update( p );
        }
        if(handler) handler(p);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}