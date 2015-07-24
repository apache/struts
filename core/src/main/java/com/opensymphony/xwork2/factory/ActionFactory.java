package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.Map;

/**
 * Used by {@link com.opensymphony.xwork2.ObjectFactory} to build actions
 */
public interface ActionFactory {

    /**
     * Builds action instance
     *
     * @param actionName name of the action
     * @param namespace namespace for the action
     * @param config action config
     * @param extraContext extra context map
     *
     * @return action object
     *
     * @throws Exception in case of any errors
     */
    Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception;

}

