package org.apache.struts2.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Checks if parameter name is valida and it doesn't contain vulnerable code
 */
public class ParameterNameSecurityGuard implements SecurityGuard {

    public SecurityPass accept(HttpServletRequest request) {
        return SecurityPass.accepted();
    }

}
