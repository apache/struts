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

import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;

public class LegacyPropertiesConfigurationProvider implements ConfigurationProvider {

    /**
     * The Logging instance for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LegacyPropertiesConfigurationProvider.class);

    public void destroy() {
        Settings.reset();
    }

    public void init(Configuration configuration)
        throws ConfigurationException {
        Settings.reset();
    }
    
    public void loadPackages()
            throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {
        
        final Settings settings = Settings.getInstance();
        
        loadSettings(props, settings);
        
        // Set default locale by lazily resolving the locale property as needed into a Locale object
        builder.factory(Locale.class, new Factory() {
            private Locale locale;

            public synchronized Object create(Context context) throws Exception {
                if (locale == null) {
                    String loc = context.getContainer().getInstance(String.class, StrutsConstants.STRUTS_LOCALE);
                    if (loc != null) {
                        StringTokenizer localeTokens = new StringTokenizer(loc, "_");
                        String lang = null;
                        String country = null;

                        if (localeTokens.hasMoreTokens()) {
                            lang = localeTokens.nextToken();
                        }

                        if (localeTokens.hasMoreTokens()) {
                            country = localeTokens.nextToken();
                        }
                        locale = new Locale(lang, country);
                    } else {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("No locale define, substituting the default VM locale");
                        }
                        locale = Locale.getDefault();
                    }
                }
                return locale;
            }
        });
    }

    /**
     * @param props
     * @param settings
     */
    protected void loadSettings(LocatableProperties props, final Settings settings) {
        // We are calling the impl methods to get around the single instance of Settings that is expected
        for (Iterator i = settings.listImpl(); i.hasNext(); ) {
            String name = (String) i.next();
            props.setProperty(name, settings.getImpl(name), settings.getLocationImpl(name));
        }
    }
}
