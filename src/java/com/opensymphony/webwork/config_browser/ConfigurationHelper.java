package com.opensymphony.webwork.config_browser;

import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.entities.ActionConfig;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * ConfigurationHelper
 *
 * @author Jason Carreira
 *         Created Aug 11, 2003 8:41:17 PM
 */
public class ConfigurationHelper {

    public static Set getNamespaces() {
        Set namespaces = Collections.EMPTY_SET;
        Map allActionConfigs = ConfigurationManager.getConfiguration().getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            namespaces = allActionConfigs.keySet();
        }
        return namespaces;
    }

    public static Set getActionNames(String namespace) {
        Set actionNames = Collections.EMPTY_SET;
        Map allActionConfigs = ConfigurationManager.getConfiguration().getRuntimeConfiguration().getActionConfigs();
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
        Map allActionConfigs = ConfigurationManager.getConfiguration().getRuntimeConfiguration().getActionConfigs();
        if (allActionConfigs != null) {
            Map actionMappings = (Map) allActionConfigs.get(namespace);
            if (actionMappings != null) {
                config = (ActionConfig) actionMappings.get(actionName);
            }
        }
        return config;
    }
}
