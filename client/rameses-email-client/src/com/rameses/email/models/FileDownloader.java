/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.email.models;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author elmonazareno
 */
public class FileDownloader {
    
    public static void download( String urlPath, String targetFileName ) throws Exception {
        download( urlPath, new File(targetFileName));
    }
    
    public static void download( String urlPath, File f) throws Exception {
        URLConnection conn = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            if(f.exists() ) f.delete();
            if( f.getParentFile() !=null) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            fos = new FileOutputStream( f );
            bos = new BufferedOutputStream(fos);
            URL url = new URL(urlPath);
            conn = url.openConnection();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is);
            // Create buffer
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer,0,bytesRead);
            }
            bos.flush();
        } 
        catch (Exception e) {
            throw e;
        } 
        finally {
            try { bis.close(); } catch(Exception ign){;}
            try { is.close(); } catch(Exception ign){;}
            try { bos.close(); } catch(Exception ign){;}            
            try { fos.close(); } catch(Exception ign){;}                        
        }        
    }
    
}
