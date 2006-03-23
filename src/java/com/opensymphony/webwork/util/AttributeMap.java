/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import com.opensymphony.webwork.ServletActionContext;

import javax.servlet.jsp.PageContext;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * A Map that holds 4 levels of scope.
 * <p/>
 * The scopes are the ones known in the web world.:
 * <ul>
 *   <li>Page scope</li>
 *   <li>Request scope</li>
 *   <li>Session scope</li>
 *   <li>Application scope</li>
 * </ul>
 * A object is searched in the order above, starting from page and ending at application scope.
 *
 * @author plightbo
 */
public class AttributeMap implements Map {

    protected static final String UNSUPPORTED = "method makes no sense for a simplified map";


    Map context;


    public AttributeMap(Map context) {
        this.context = context;
    }


    public boolean isEmpty() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public void clear() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public boolean containsKey(Object key) {
        return (get(key) != null);
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Set entrySet() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Object get(Object key) {
        PageContext pc = getPageContext();

        if (pc == null) {
            Map request = (Map) context.get("request");
            Map session = (Map) context.get("session");
            Map application = (Map) context.get("application");

            if ((request != null) && (request.get(key) != null)) {
                return request.get(key);
            } else if ((session != null) && (session.get(key) != null)) {
                return session.get(key);
            } else if ((application != null) && (application.get(key) != null)) {
                return application.get(key);
            }
        } else {
            try{
                return pc.findAttribute(key.toString());
            }catch (NullPointerException npe){
                return null;
            }
        }

        return null;
    }

    public Set keySet() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Object put(Object key, Object value) {
        PageContext pc = getPageContext();
        if (pc != null) {
            pc.setAttribute(key.toString(), value);
        }

        return null;
    }

    public void putAll(Map t) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public int size() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Collection values() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    private PageContext getPageContext() {
        return (PageContext) context.get(ServletActionContext.PAGE_CONTEXT);
    }
}
