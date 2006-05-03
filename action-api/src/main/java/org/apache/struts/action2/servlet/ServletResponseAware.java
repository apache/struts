package org.apache.struts.action2.servlet;

import javax.servlet.http.HttpServletResponse;

/**
 * Implemented by actions which need direct access to the servlet response.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface ServletResponseAware {

    /**
     * Sets the servlet response.
     *
     * @param response servlet response
     */
    void setServletResponse(HttpServletResponse response);
}
