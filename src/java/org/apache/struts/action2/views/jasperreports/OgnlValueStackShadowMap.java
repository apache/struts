/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jasperreports;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.HashMap;
import java.util.Set;


/**
 * Ported to Struts:
 *
 * @author &lt;a href="hermanns@aixcept.de"&gt;Rainer Hermanns&lt;/a&gt;
 * @version $Id: OgnlValueStackShadowMap.java,v 1.4 2005/10/09 04:26:35 plightbo Exp $
 */
public class OgnlValueStackShadowMap extends HashMap {

	private static final long serialVersionUID = -167109778490907240L;

	/**
     * valueStack reference
     */
    OgnlValueStack valueStack;

    /**
     * entries reference
     */
    Set entries;


    /**
     * Constructs an instance of OgnlValueStackShadowMap.
     *
     * @param valueStack - the underlying valuestack
     */
    public OgnlValueStackShadowMap(OgnlValueStack valueStack) {
        this.valueStack = valueStack;
    }


    /**
     * Implementation of containsKey(), overriding HashMap implementation.
     *
     * @param key - The key to check in HashMap and if not found to check on valueStack.
     * @return <tt>true</tt>, if conatins key, <tt>false</tt> otherwise.
     * @see java.util.HashMap#containsKey
     */
    public boolean containsKey(Object key) {
        boolean hasKey = super.containsKey(key);

        if (!hasKey) {
            if (valueStack.findValue((String) key) != null) {
                hasKey = true;
            }
        }

        return hasKey;
    }

    /**
     * Implementation of get(), overriding HashMap implementation.
     *
     * @param key - The key to get in HashMap and if not found there from the valueStack.
     * @return value - The object from HashMap or if null, from the valueStack.
     * @see java.util.HashMap#get
     */
    public Object get(Object key) {
        Object value = super.get(key);

        if ((value == null) && key instanceof String) {
            value = valueStack.findValue((String) key);
        }

        return value;
    }
}
