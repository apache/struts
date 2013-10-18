package org.apache.struts2.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Main
 */
public interface SecurityGate {

    void check(HttpServletRequest request);

}
