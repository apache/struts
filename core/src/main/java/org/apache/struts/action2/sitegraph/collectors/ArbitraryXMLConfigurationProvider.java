/*
 * Created on Aug 12, 2004 by mgreer
 */
package org.apache.struts.action2.sitegraph.collectors;

import com.opensymphony.util.FileManager;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Override Xwork class so we cn use an arbitrary config file
 */
public class ArbitraryXMLConfigurationProvider extends XmlConfigurationProvider {

    private static final Log LOG = LogFactory.getLog(ArbitraryXMLConfigurationProvider.class);
    private String configFileName = "xwork.xml";
    private String basePathString = "";

    public ArbitraryXMLConfigurationProvider() {
    }

    public ArbitraryXMLConfigurationProvider(String filename) {
        this.configFileName = filename;
        this.basePathString = new File(filename).getParent() + "/";
    }

    /**
     * Override Xwork method so we cn use an arbitrary config file
     *
     * @see com.opensymphony.xwork.config.providers.XmlConfigurationProvider#getInputStream(java.lang.String)
     */
    protected InputStream getInputStream(String fileName) {
        InputStream is = null;
        if (LOG.isDebugEnabled())
            LOG.debug("fileName=" + this.basePathString + fileName);
        try {
            is = new FileInputStream(this.basePathString + fileName);
        } catch (FileNotFoundException e) {
            // ok, try to check the ClassLoader
            is = FileManager.loadFile(fileName, this.getClass());
        }

        return is;
    }
}
