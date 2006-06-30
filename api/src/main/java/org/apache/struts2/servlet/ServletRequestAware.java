package org.apache.struts2.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * Implemented by actions which need direct access to the servlet request.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface ServletRequestAware {

    /**
     * Sets the servlet request.
     *
     * @param request servlet request.
     */
    void setServletRequest(HttpServletRequest request);
}
