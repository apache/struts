/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.config;

import com.opensymphony.xwork.util.LocalizedTextUtil;
import org.apache.struts2.StrutsConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;


/**
 * Default implementation of Configuration - creates and delegates to other configurations by using an internal
 * {@link DelegatingConfiguration}.
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
        // Use default properties and struts.properties
        ArrayList list = new ArrayList();

        try {
            list.add(new PropertiesConfiguration("struts"));
        } catch (Exception e) {
            log.warn("Could not find or error in struts.properties", e);
        }

        try {
            list.add(new PropertiesConfiguration("org/apache/struts2/default"));
        } catch (Exception e) {
            log.error("Could not find org/apache/struts2/default.properties", e);
        }

        Configuration[] configList = new Configuration[list.size()];
        config = new DelegatingConfiguration((Configuration[]) list.toArray(configList));

        // Add list of additional properties configurations
        try {
            StringTokenizer configFiles = new StringTokenizer((String) config.getImpl(StrutsConstants.STRUTS_CUSTOM_PROPERTIES), ",");

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
            // thrown when Configuration is unable to find a certain property
            // eg. struts.custom.properties in default.properties which is commented
            // out
        }

        // Add addtional list of i18n global resource bundles
        try {

            LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
            StringTokenizer bundleFiles = new StringTokenizer((String) config.getImpl(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES), ", ");

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
            // struts.custom.i18n.resources wasn't provided
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
