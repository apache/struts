package org.apache.struts2.security;

import org.apache.struts2.StrutsException;

/**
 * Exception indicates possible security breach
 */
public class StrutsSecurityException extends StrutsException {

    public StrutsSecurityException(String message) {
        super(message);
    }

}
