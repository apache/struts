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

package org.apache.struts2.config_browser;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ResolverUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * ConfigurationHelper
 */
public class ConfigurationHelper {
    
    private Configuration configuration;

    @Inject
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
    
    public Set<String> getNamespaces() {
        Set<String> namespaces = Collections.emptySet();
        Map<String, Map<String, ActionConfig>>  allActionConfigs = configuration.getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            namespaces = allActionConfigs.keySet();
        }
        return namespaces;
    }

    public Set<String> getActionNames(String namespace) {
        Set<String> actionNames = Collections.emptySet();
        Map<String, Map<String, ActionConfig>> allActionConfigs = configuration.getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            Map<String, ActionConfig> actionMappings = allActionConfigs.get(namespace);
            if (actionMappings != null) {
                actionNames = actionMappings.keySet();
            }
        }
        return actionNames;
    }

    public ActionConfig getActionConfig(String namespace, String actionName) {
        ActionConfig config = null;
        Map<String, Map<String, ActionConfig>> allActionConfigs = configuration.getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            Map<String, ActionConfig> actionMappings = allActionConfigs.get(namespace);
            if (actionMappings != null) {
                config = actionMappings.get(actionName);
            }
        }
        return config;
    }
    
    public List<Properties> getJarProperties() throws IOException {
        ResolverUtil resolver = new ResolverUtil();
        List<Properties> poms = new ArrayList<Properties>();
        resolver.findNamedResource("pom.properties", "META-INF/maven");
        Set<URL> urls = resolver.getResources();
        for (URL url : urls) {
            Properties p = new Properties();
            p.load(url.openStream());
            poms.add(p);
        }
        return poms;
    }
}
