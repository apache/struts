package org.apache.struts.action2.interceptor;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Proxy class used together with PrincipalAware interface. It allows to get indirect access to
 * HttpServletRequest Principal related methods.
 *
 * @author Remigijus Bauzys
 * @version $Revision$
 */
public class PrincipalProxy {
    private HttpServletRequest request;

    public PrincipalProxy(HttpServletRequest request) {
        this.request = request;
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    public boolean isRequestSecure() {
        return request.isSecure();
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}
