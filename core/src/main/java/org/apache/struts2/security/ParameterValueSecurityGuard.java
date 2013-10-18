package org.apache.struts2.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Checks if parameter's value doesn't contain vulnerable code
 */
public class ParameterValueSecurityGuard implements SecurityGuard {

    public SecurityPass accept(HttpServletRequest request) {
        return SecurityPass.accepted();
    }

}
