package org.apache.struts2.servlet;

import java.util.Map;

/**
 * Implemented by actions which need direct access to the request parameters.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface ParameterAware {

    /**
     * Sets parameters.
     *
     * @param parameters map of parameter name to parameter values
     */
    void setParameters(Map<String, String[]> parameters);
}
