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

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationImpl;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;


/**
 * A class to handle settings via a properties file.
 */
class PropertiesSettings implements Settings {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesSettings.class);

    private LocatableProperties settings;

    /**
     * Creates a new properties config given the name of a properties file. The name is expected to NOT have
     * the ".properties" file extension.  So when <tt>new PropertiesSettings("foo")</tt> is called
     * this class will look in the classpath for the <tt>foo.properties</tt> file.
     *
     * @param name the name of the properties file, excluding the ".properties" extension.
     */
    public PropertiesSettings(String name) {
        
        URL settingsUrl = ClassLoaderUtil.getResource(name + ".properties", getClass());
        
        if (settingsUrl == null) {
            if (LOG.isDebugEnabled()) {
        	LOG.debug(name + ".properties missing");
            }
            settings = new LocatableProperties();
            return;
        }
        
        settings = new LocatableProperties(new LocationImpl(null, settingsUrl.toString()));

        // Load settings
        InputStream in = null;
        try {
            in = settingsUrl.openStream();
            settings.load(in);
        } catch (IOException e) {
            throw new StrutsException("Could not load " + name + ".properties:" + e, e);
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch(IOException io) {
                    if (LOG.isWarnEnabled()) {
                	LOG.warn("Unable to close input stream", io);
                    }
                }
            }
        }
    }


    /**
     * Gets a property from the properties file.
     *
     * @see #get(String)
     */
    public String get(String aName) throws IllegalArgumentException {
        return settings.getProperty(aName);
    }
    
    /**
     * Gets the location of a property from the properties file.
     *
     * @see #getLocation(String)
     */
    public Location getLocation(String aName) throws IllegalArgumentException {
        return settings.getPropertyLocation(aName);
    }

    /**
     * Lists all keys in the properties file.
     *
     * @see #list()
     */
    public Iterator list() {
        return settings.keySet().iterator();
    }

}
