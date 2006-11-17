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

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsException;

import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationImpl;


/**
 * A class to handle settings via a properties file.
 */
class PropertiesSettings extends Settings {

    LocatableProperties settings;
    static Log LOG = LogFactory.getLog(PropertiesSettings.class);


    /**
     * Creates a new properties config given the name of a properties file. The name is expected to NOT have
     * the ".properties" file extension.  So when <tt>new PropertiesSettings("foo")</tt> is called
     * this class will look in the classpath for the <tt>foo.properties</tt> file.
     *
     * @param name the name of the properties file, excluding the ".properties" extension.
     */
    public PropertiesSettings(String name) {
        
        URL settingsUrl = Thread.currentThread().getContextClassLoader().getResource(name + ".properties");
        
        if (settingsUrl == null) {
            LOG.debug(name + ".properties missing");
            return;
        }
        
        settings = new LocatableProperties(new LocationImpl(null, settingsUrl.toString()));

        // Load settings
        try {
            settings.load(settingsUrl.openStream());
        } catch (IOException e) {
            throw new StrutsException("Could not load " + name + ".properties:" + e, e);
        }
    }


    /**
     * Sets a property in the properties file.
     *
     * @see #set(String, String)
     */
    public void setImpl(String aName, String aValue) {
        settings.setProperty(aName, aValue);
    }

    /**
     * Gets a property from the properties file.
     *
     * @see #get(String)
     */
    public String getImpl(String aName) throws IllegalArgumentException {
        String setting = settings.getProperty(aName);

        if (setting == null) {
            throw new IllegalArgumentException("No such setting:" + aName);
        }

        return setting;
    }
    
    /**
     * Gets the location of a property from the properties file.
     *
     * @see #getLocation(String)
     */
    public Location getLocationImpl(String aName) throws IllegalArgumentException {
        Location loc = settings.getPropertyLocation(aName);

        if (loc == null) {
            if (!settings.containsKey(aName)) {
                throw new IllegalArgumentException("No such setting:" + aName);
            } 
        }

        return loc;
    }

    /**
     * Tests to see if a property exists in the properties file.
     *
     * @see #isSet(String)
     */
    public boolean isSetImpl(String aName) {
        if (settings.get(aName) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Lists all keys in the properties file.
     *
     * @see #list()
     */
    public Iterator listImpl() {
        return settings.keySet().iterator();
    }
}
