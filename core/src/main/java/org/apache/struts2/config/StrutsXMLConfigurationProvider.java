/*
 * Created on Aug 12, 2004 by mgreer
 */
package org.apache.struts2.config;

import com.opensymphony.util.FileManager;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Override Xwork class so we cn use an arbitrary config file
 */
public class StrutsXMLConfigurationProvider extends XmlConfigurationProvider {

    private static final Log LOG = LogFactory.getLog(StrutsXMLConfigurationProvider.class);
    private File baseDir = null;

    public StrutsXMLConfigurationProvider(boolean errorIfMissing) {
        this("struts.xml", errorIfMissing);
    }
    
    public StrutsXMLConfigurationProvider(String filename, boolean errorIfMissing) {
        super(filename, errorIfMissing);
        
        Map<String,String> dtdMappings = new HashMap<String,String>(getDtdMappings());
        dtdMappings.put("-//Apache Software Foundation//DTD Struts Configuration 2.0//EN", "struts-2.0.dtd");
        setDtdMappings(dtdMappings);
        File file = new File(filename);
        if (file.getParent() != null) {
            this.baseDir = file.getParentFile();
        }
    }

    /**
     * Override Xwork method so we cn use an arbitrary config file
     *
     * @see com.opensymphony.xwork2.config.providers.XmlConfigurationProvider#getInputStream(java.lang.String)
     */
    protected InputStream getInputStream(String fileName) {
        InputStream is = null;
        if (baseDir != null) {
            is = findInFileSystem(fileName);
            if (is == null) {
                is = super.getInputStream(fileName);
            }
        } else {
            is = super.getInputStream(fileName);
        }
        return is;
    }
    
    protected InputStream findInFileSystem(String fileName) {
        InputStream is = null;
        File file = new File(fileName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Trying to load file " + file);
        }
        
        // Trying relative path to original file
        if (!file.exists()) {
            file = new File(baseDir, fileName);
        }
        if (file.exists()) {
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException("File not found: "+file, ex);
            }
        } else {
            
        }
        return is;
    }
}
