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
package org.apache.struts2.config_browser;

import com.opensymphony.xwork.XWorkStatic;
import com.opensymphony.xwork.config.entities.ActionConfig;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * ConfigurationHelper
 */
public class ConfigurationHelper {

    public static Set getNamespaces() {
        Set namespaces = Collections.EMPTY_SET;
        Map allActionConfigs = XWorkStatic.getConfigurationManager().getConfiguration().getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            namespaces = allActionConfigs.keySet();
        }
        return namespaces;
    }

    public static Set getActionNames(String namespace) {
        Set actionNames = Collections.EMPTY_SET;
        Map allActionConfigs = XWorkStatic.getConfigurationManager().getConfiguration().getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            Map actionMappings = (Map) allActionConfigs.get(namespace);
            if (actionMappings != null) {
                actionNames = actionMappings.keySet();
            }
        }
        return actionNames;
    }

    public static ActionConfig getActionConfig(String namespace, String actionName) {
        ActionConfig config = null;
        Map allActionConfigs = XWorkStatic.getConfigurationManager().getConfiguration().getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            Map actionMappings = (Map) allActionConfigs.get(namespace);
            if (actionMappings != null) {
                config = (ActionConfig) actionMappings.get(actionName);
            }
        }
        return config;
    }
}
