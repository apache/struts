/*
 * Created on Aug 12, 2004 by mgreer
 */
package org.apache.struts2.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsException;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

/**
 * Override Xwork class so we can use an arbitrary config file
 */
public class StrutsXMLConfigurationProvider extends XmlConfigurationProvider {

    private static final Log LOG = LogFactory.getLog(StrutsXMLConfigurationProvider.class);
    private File baseDir = null;

    /** 
     * Constructs the configuration provider
     * 
     * @param errorIfMissing If we should throw an exception if the file can't be found
     */
    public StrutsXMLConfigurationProvider(boolean errorIfMissing) {
        this("struts.xml", errorIfMissing);
    }
    
    /** 
     * Constructs the configuration provider
     * 
     * @param filename The filename to look for
     * @param errorIfMissing If we should throw an exception if the file can't be found
     */
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
     * Look for the configuration file on the classpath and in the file system
     *
     * @param fileName The file name to retrieve
     * @see com.opensymphony.xwork2.config.providers.XmlConfigurationProvider#getInputStream(java.lang.String)
     */
    @Override
    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
        URL url = null;
        if (baseDir != null) {
            url = findInFileSystem(fileName);
            if (url == null) {
                return super.getConfigurationUrls(fileName);
            }
        } 
        if (url != null) {
            List<URL> list = new ArrayList<URL>();
            list.add(url);
            return list.iterator();
        } else {
            return super.getConfigurationUrls(fileName);
        }
    }
    
    protected URL findInFileSystem(String fileName) throws IOException {
        URL url = null;
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
                url = file.toURL();
            } catch (MalformedURLException e) {
                throw new IOException("Unable to convert "+file+" to a URL");
            }
        } 
        return url;
    }
}
