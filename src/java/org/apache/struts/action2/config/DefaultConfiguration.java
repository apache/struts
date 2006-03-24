/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.config;

import com.opensymphony.xwork.util.LocalizedTextUtil;
import com.opensymphony.webwork.WebWorkConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;


/**
 * Default implementation of Configuration - creates and delegates to other configurations by using an internal
 * {@link DelegatingConfiguration}.
 *
 * @author Rickard �berg
 * @author Jason Carreira
 * @author Bill Lynch (docs)
 */
public class DefaultConfiguration extends Configuration {

    protected Log log = LogFactory.getLog(this.getClass());
    Configuration config;


    /**
     * Creates a new DefaultConfiguration object by loading all property files
     * and creating an internal {@link DelegatingConfiguration} object. All calls to get and set
     * in this class will call that configuration object.
     */
    public DefaultConfiguration() {
        // Create default implementations 
        // Use default properties and webwork.properties
        ArrayList list = new ArrayList();

        try {
            list.add(new PropertiesConfiguration("webwork"));
        } catch (Exception e) {
            log.warn("Could not find webwork.properties");
        }

        try {
            list.add(new PropertiesConfiguration("com/opensymphony/webwork/default"));
        } catch (Exception e) {
            log.error("Could not find com/opensymphony/webwork/default.properties", e);
        }

        Configuration[] configList = new Configuration[list.size()];
        config = new DelegatingConfiguration((Configuration[]) list.toArray(configList));

        // Add list of additional properties configurations
        try {
            StringTokenizer configFiles = new StringTokenizer((String) config.getImpl(WebWorkConstants.WEBWORK_CUSTOM_PROPERTIES), ",");

            while (configFiles.hasMoreTokens()) {
                String name = configFiles.nextToken();

                try {
                    list.add(new PropertiesConfiguration(name));
                } catch (Exception e) {
                    log.error("Could not find " + name + ".properties. Skipping");
                }
            }

            configList = new Configuration[list.size()];
            config = new DelegatingConfiguration((Configuration[]) list.toArray(configList));
        } catch (IllegalArgumentException e) {
        }

        // Add addtional list of i18n global resource bundles
        try {

            LocalizedTextUtil.addDefaultResourceBundle("com/opensymphony/webwork/webwork-messages");
            StringTokenizer bundleFiles = new StringTokenizer((String) config.getImpl(WebWorkConstants.WEBWORK_CUSTOM_I18N_RESOURCES), ", ");

            while (bundleFiles.hasMoreTokens()) {
                String name = bundleFiles.nextToken();

                try {
                    log.info("Loading global messages from " + name);
                    LocalizedTextUtil.addDefaultResourceBundle(name);
                } catch (Exception e) {
                    log.error("Could not find " + name + ".properties. Skipping");
                }
            }
        } catch (IllegalArgumentException e) {
            // webwork.custom.i18n.resources wasn't provided
        }
    }


    /**
     * Sets the given property - delegates to the internal config implementation.
     *
     * @see #set(String, Object)
     */
    public void setImpl(String aName, Object aValue) throws IllegalArgumentException, UnsupportedOperationException {
        config.setImpl(aName, aValue);
    }

    /**
     * Gets the specified property - delegates to the internal config implementation.
     *
     * @see #get(String)
     */
    public Object getImpl(String aName) throws IllegalArgumentException {
        // Delegate
        return config.getImpl(aName);
    }

    /**
     * Determines whether or not a value has been set - delegates to the internal config implementation.
     *
     * @see #isSet(String)
     */
    public boolean isSetImpl(String aName) {
        return config.isSetImpl(aName);
    }

    /**
     * Returns a list of all property names - delegates to the internal config implementation.
     *
     * @see #list()
     */
    public Iterator listImpl() {
        return config.listImpl();
    }
}
