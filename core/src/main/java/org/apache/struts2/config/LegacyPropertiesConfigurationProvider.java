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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapper;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public class LegacyPropertiesConfigurationProvider implements ConfigurationProvider {

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
        
        for (Iterator i = settings.list(); i.hasNext(); ) {
            String name = (String) i.next();
            props.put(name, settings.get(name));
            if (StrutsConstants.STRUTS_DEVMODE.equals(name)) {
                props.setProperty("devMode", settings.get(name), settings.getLocation(name));
            }
        }
        
        // Set default locale
        final Locale locale = settings.getLocale();
        builder.factory(Locale.class, new Factory() {
            public Object create(Context context) throws Exception {
                return locale;
            }
        });
    }
}
