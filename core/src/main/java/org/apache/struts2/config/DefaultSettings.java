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

import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;



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
    protected Logger log = LoggerFactory.getLogger(this.getClass());

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
