package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.Map;

/**
 * Used by {@link com.opensymphony.xwork2.ObjectFactory} to build actions
 */
public interface ActionFactory {

    /**
     * Builds action instance
     */
    Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception;

}

