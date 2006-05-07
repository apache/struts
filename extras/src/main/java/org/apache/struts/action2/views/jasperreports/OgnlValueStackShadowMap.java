/*
 * $Id: OgnlValueStackShadowMap.java 392647 2006-04-09 00:41:12Z plightbo $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.views.jasperreports;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.HashMap;
import java.util.Set;


/**
 * Ported to Struts:
 *
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
