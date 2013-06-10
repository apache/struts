/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * All Struts requests are wrapped with this class, which provides simple JSTL accessibility. This is because JSTL
 * works with request attributes, so this class delegates to the value stack except for a few cases where required to
 * prevent infinite loops. Namely, we don't let any attribute name with "#" in it delegate out to the value stack, as it
 * could potentially cause an infinite loop. For example, an infinite loop would take place if you called:
 * request.getAttribute("#attr.foo").
 *
 * <!-- END SNIPPET: javadoc -->
 *
 */
public class StrutsRequestWrapper extends HttpServletRequestWrapper {

    private static final String REQUEST_WRAPPER_GET_ATTRIBUTE = "__requestWrapper.getAttribute";
    private final boolean disableRequestAttributeValueStackLookup;

    /**
     * The constructor
     * @param req The request
     */
    public StrutsRequestWrapper(HttpServletRequest req) {
        this(req, false);
    }

    /**
     * The constructor
     * @param req The request
     * @param disableRequestAttributeValueStackLookup flag for disabling request attribute value stack lookup (JSTL accessibility)
     */
    public StrutsRequestWrapper(HttpServletRequest req, boolean disableRequestAttributeValueStackLookup) {
        super(req);
        this.disableRequestAttributeValueStackLookup = disableRequestAttributeValueStackLookup;
    }

    /**
     * Gets the object, looking in the value stack if not found
     *
     * @param key The attribute key
     */
    public Object getAttribute(String key) {
        if (key == null) {
            throw new NullPointerException("You must specify a key value");
        }

        if (disableRequestAttributeValueStackLookup || key.startsWith("javax.servlet")) {
            // don't bother with the standard javax.servlet attributes, we can short-circuit this
            // see WW-953 and the forums post linked in that issue for more info
            return super.getAttribute(key);
        }

        ActionContext ctx = ActionContext.getContext();
        Object attribute = super.getAttribute(key);

        if (ctx != null && attribute == null) {
            boolean alreadyIn = isTrue((Boolean) ctx.get(REQUEST_WRAPPER_GET_ATTRIBUTE));

            // note: we don't let # come through or else a request for
            // #attr.foo or #request.foo could cause an endless loop
            if (!alreadyIn && !key.contains("#")) {
                try {
                    // If not found, then try the ValueStack
                    ctx.put(REQUEST_WRAPPER_GET_ATTRIBUTE, Boolean.TRUE);
                    ValueStack stack = ctx.getValueStack();
                    if (stack != null) {
                        attribute = stack.findValue(key);
                    }
                } finally {
                    ctx.put(REQUEST_WRAPPER_GET_ATTRIBUTE, Boolean.FALSE);
                }
            }
        }
        return attribute;
    }
}
