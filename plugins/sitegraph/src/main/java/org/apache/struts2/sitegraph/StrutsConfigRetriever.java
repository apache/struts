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

package org.apache.struts2.sitegraph;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.config.DefaultBeanSelectionProvider;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.PropertiesConfigurationProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.sitegraph.entities.FreeMarkerView;
import org.apache.struts2.sitegraph.entities.JspView;
import org.apache.struts2.sitegraph.entities.VelocityView;
import org.apache.struts2.sitegraph.entities.View;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Initializes and retrieves XWork config elements
 */
public class StrutsConfigRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsConfigRetriever.class);
    private static String configDir;
    private static String[] views;
    private static boolean isXWorkStarted = false;
    private static Map<String, View> viewCache = new LinkedHashMap<String, View>();
    private static ConfigurationManager cm;

    /**
     * Returns a Map of all action names/configs
     *
     * @return Map of all action names/configs
     */
    public static Map<String, Map<String,ActionConfig>> getActionConfigs() {
        if (!isXWorkStarted)
            initXWork();
        return cm.getConfiguration().getRuntimeConfiguration().getActionConfigs();
    }

    private static void initXWork() {
        String configFilePath = configDir + "/struts.xml";
        File configFile = new File(configFilePath);
        try {
            ConfigurationProvider configProvider = new StrutsXmlConfigurationProvider(configFile.getCanonicalPath(), true, null);
            cm = new ConfigurationManager();
            cm.addContainerProvider(new DefaultPropertiesProvider());
            cm.addContainerProvider(new StrutsXmlConfigurationProvider("struts-default.xml", false, null));
            cm.addContainerProvider(configProvider);
            cm.addContainerProvider(new PropertiesConfigurationProvider());
            cm.addContainerProvider(new DefaultBeanSelectionProvider());
            isXWorkStarted = true;
        } catch (IOException e) {
            LOG.error("IOException", e);
        }
    }

    public static Set<String> getNamespaces() {
        Set<String> namespaces= Collections.emptySet();
        Map<String, Map<String, ActionConfig>> allActionConfigs = getActionConfigs();
        if (allActionConfigs != null) {
            namespaces = allActionConfigs.keySet();
        }
        return namespaces;
    }

    /**
     * Return a Set of the action names for this namespace.
     *
     * @param namespace
     * @return Set of the action names for this namespace.
     */
    public static Set<String> getActionNames(String namespace) {
        Set<String> actionNames = Collections.emptySet();
        Map<String, Map<String, ActionConfig>> allActionConfigs = getActionConfigs();
        if (allActionConfigs != null) {
            Map<String, ActionConfig> actionMappings = allActionConfigs.get(namespace);
            if (actionMappings != null) {
                actionNames = actionMappings.keySet();
            }
        }
        return actionNames;
    }

    /**
     * Returns the ActionConfig for this action name at this namespace.
     *
     * @param namespace
     * @param actionName
     * @return The ActionConfig for this action name at this namespace.
     */
    public static ActionConfig getActionConfig(String namespace, String actionName) {
        ActionConfig config = null;
        Map allActionConfigs = getActionConfigs();
        if (allActionConfigs != null) {
            Map actionMappings = (Map) allActionConfigs.get(namespace);
            if (actionMappings != null) {
                config = (ActionConfig) actionMappings.get(actionName);
            }
        }
        return config;
    }

    public static ResultConfig getResultConfig(String namespace, String actionName,
                                               String resultName) {
        ResultConfig result = null;
        ActionConfig actionConfig = getActionConfig(namespace, actionName);
        if (actionConfig != null) {
            Map resultMap = actionConfig.getResults();
            result = (ResultConfig) resultMap.get(resultName);
        }
        return result;
    }

    public static File getViewFile(String namespace, String actionName, String resultName) {
        ResultConfig result = getResultConfig(namespace, actionName, resultName);
        String location = result.getParams().get("location");
        for (String viewRoot : views) {
            File viewFile = getViewFileInternal(viewRoot, location, namespace);
            if (viewFile != null) {
                return viewFile;
            }
        }

        return null;
    }

    private static File getViewFileInternal(String root, String location, String namespace) {
        StringBuilder filePath = new StringBuilder(root);
        if (!location.startsWith("/")) {
            filePath.append(namespace).append('/');
        }
        filePath.append(location);
        File viewFile = new File(filePath.toString());
        if (viewFile.exists()) {
            return viewFile;
        } else {
            return null;
        }
    }

    public static View getView(String namespace, String actionName, String resultName, int type) {
        String viewId = namespace + "/" + actionName + "/" + resultName;
        View view = viewCache.get(viewId);
        if (view == null) {
            File viewFile = StrutsConfigRetriever.getViewFile(namespace, actionName, resultName);
            if (viewFile != null) {
                switch (type) {
                    case View.TYPE_JSP:
                        view = new JspView(viewFile);
                        break;
                    case View.TYPE_FTL:
                        view = new FreeMarkerView(viewFile);
                        break;
                    case View.TYPE_VM:
                        view = new VelocityView(viewFile);
                        break;
                    default:
                        return null;
                }

                viewCache.put(viewId, view);
            }
        }
        return view;
    }

    public static void setConfiguration(String configDir, String[] views) {
        StrutsConfigRetriever.configDir = configDir;
        StrutsConfigRetriever.views = views;
        isXWorkStarted = false;
    }
}
