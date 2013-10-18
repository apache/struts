package org.apache.struts2.security;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO lukaszlenart: write a JavaDoc
 */
public interface SecurityGuard {

    SecurityPass accept(HttpServletRequest request);

}
