/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.mockobjects.servlet.MockPageContext;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 14-Mar-2003
 * Time: 5:52:36 PM
 * To change this template use Options | File Templates.
 */
public class WebWorkMockPageContext extends MockPageContext {

    private Map attributes = new HashMap();
    private ServletResponse response;


    public void setAttribute(String s, Object o) {
        if ((s == null) || (o == null)) {
            throw new NullPointerException("PageContext does not accept null attributes");
        }

        this.attributes.put(s, o);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Object getAttributes(String key) {
        return this.attributes.get(key);
    }

    public void setResponse(ServletResponse response) {
        this.response = response;
    }

    public ServletResponse getResponse() {
        return response;
    }

    public HttpSession getSession() {
        HttpSession session = super.getSession();

        if (session == null) {
            session = ((HttpServletRequest) getRequest()).getSession(true);
        }

        return session;
    }

    public Object findAttribute(String s) {
        return attributes.get(s);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }
}
