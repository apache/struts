/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.util.LocalizedTextUtil;


/**
 * DefaultSettings implements optional methods of Settings.
 * <p>
 * This class creates and delegates to other settings by using an internal
 * {@link DelegatingSettings} object.
 */
public class DefaultSettings extends Settings {

    /**
     * The logging instance for this class.
     */
    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * The Settings object that handles API calls.
     */
    Settings delegate;

    /**
     * Constructs an instance by loading the standard property files, 
     * any custom property files (<code>struts.custom.properties</code>), 
     * and any custom message resources ().
     * <p>
     * Since this constructor  combines Settings from multiple resources,
     * it utilizes a {@link DelegatingSettings} instance,
     * and all API calls are handled by that instance.
     */
    public DefaultSettings() {

        ArrayList<Settings> list = new ArrayList<Settings>();

        // stuts.properties, default.properties
        try {
            list.add(new PropertiesSettings("struts"));
        } catch (Exception e) {
            log.warn("DefaultSettings: Could not find or error in struts.properties", e);
        }

        Settings[] settings = new Settings[list.size()];
        delegate = new DelegatingSettings(list.toArray(settings));

        // struts.custom.properties
        try {
            StringTokenizer customProperties = new StringTokenizer(delegate.getImpl(StrutsConstants.STRUTS_CUSTOM_PROPERTIES), ",");

            while (customProperties.hasMoreTokens()) {
                String name = customProperties.nextToken();

                try {
                    list.add(new PropertiesSettings(name));
                } catch (Exception e) {
                    log.error("DefaultSettings: Could not find " + name + ".properties. Skipping.");
                }
            }

            settings = new Settings[list.size()];
            delegate = new DelegatingSettings(list.toArray(settings));
        } catch (IllegalArgumentException e) {
            // Assume it's OK, since IllegalArgumentException is thrown  
            // when Settings is unable to find a certain setting,
            // like the struts.custom.properties, which is commented out
        }

        // struts.custom.i18n.resources
        try {

            LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
            StringTokenizer customBundles = new StringTokenizer(delegate.getImpl(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES), ", ");

            while (customBundles.hasMoreTokens()) {
                String name = customBundles.nextToken();
                try {
                    log.info("DefaultSettings: Loading global messages from " + name);
                    LocalizedTextUtil.addDefaultResourceBundle(name);
                } catch (Exception e) {
                    log.error("DefaultSettings: Could not find " + name + ".properties. Skipping");
                }
            }
        } catch (IllegalArgumentException e) {
            // Assume it's OK, since many applications do not provide custom resource bundles. 
        }
    }

    // See superclass for Javadoc
    public void setImpl(String name, String value) throws IllegalArgumentException, UnsupportedOperationException {
        delegate.setImpl(name, value);
    }

    // See superclass for Javadoc
    public String getImpl(String aName) throws IllegalArgumentException {
        return delegate.getImpl(aName);
    }

    // See superclass for Javadoc
    public boolean isSetImpl(String aName) {
        return delegate.isSetImpl(aName);
    }

    // See superclass for Javadoc
    public Iterator listImpl() {
        return delegate.listImpl();
    }
}
