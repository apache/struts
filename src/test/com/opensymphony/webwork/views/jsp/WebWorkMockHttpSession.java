/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.mockobjects.servlet.MockHttpSession;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 * WebWorkMockHttpSession
 *
 * @author Jason Carreira
 *         Created Jun 5, 2003 9:26:31 PM
 */
public class WebWorkMockHttpSession extends MockHttpSession {

    Hashtable attributes = new Hashtable();


    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public Enumeration getAttributeNames() {
        return attributes.keys();
    }

    public void setExpectedAttribute(String s, Object o) {
        throw new UnsupportedOperationException();
    }

    public void setExpectedRemoveAttribute(String s) {
        throw new UnsupportedOperationException();
    }

    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    public void setupGetAttribute(String s, Object o) {
        throw new UnsupportedOperationException();
    }

    public void setupGetAttributeNames(Enumeration enumeration) {
        throw new UnsupportedOperationException();
    }
}
