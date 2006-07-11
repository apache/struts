/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts2;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.util.location.Locatable;
import com.opensymphony.xwork2.util.location.Location;


/**
 * A generic runtime exception that optionally contains Location information 
 */
public class StrutsException extends XWorkException implements Locatable {

    private Location location;


    /**
     * Constructs a <code>StrutsException</code> with no detail message.
     */
    public StrutsException() {
    }

    /**
     * Constructs a <code>StrutsException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public StrutsException(String s) {
        this(s, null, null);
    }
    
    /**
     * Constructs a <code>StrutsException</code> with the specified
     * detail message and target.
     *
     * @param s the detail message.
     * @param target the target of the exception.
     */
    public StrutsException(String s, Object target) {
        this(s, (Throwable) null, target);
    }

    /**
     * Constructs a <code>StrutsException</code> with the root cause
     *
     * @param cause The wrapped exception
     */
    public StrutsException(Throwable cause) {
        this(null, cause, null);
    }
    
    /**
     * Constructs a <code>StrutsException</code> with the root cause and target
     *
     * @param cause The wrapped exception
     * @param target The target of the exception
     */
    public StrutsException(Throwable cause, Object target) {
        this(null, cause, target);
    }

    /**
     * Constructs a <code>StrutsException</code> with the specified
     * detail message and exception cause.
     *
     * @param s the detail message.
     * @param cause the wrapped exception
     */
    public StrutsException(String s, Throwable cause) {
        this(s, cause, null);
    }
    
    
     /**
     * Constructs a <code>StrutsException</code> with the specified
     * detail message, cause, and target
     *
     * @param s the detail message.
     * @param cause The wrapped exception
     * @param target The target of the exception
     */
    public StrutsException(String s, Throwable cause, Object target) {
        super(s, cause, target);
    }
}