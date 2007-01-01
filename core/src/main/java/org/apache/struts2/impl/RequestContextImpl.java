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
// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;
import static org.apache.struts2.StrutsStatics.HTTP_RESPONSE;
import static org.apache.struts2.StrutsStatics.SERVLET_CONTEXT;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.Messages;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.spi.ActionContext;
import org.apache.struts2.spi.RequestContext;
import org.apache.struts2.spi.ValueStack;

import com.opensymphony.xwork2.ActionInvocation;

public class RequestContextImpl implements RequestContext {

    com.opensymphony.xwork2.ActionContext xworkContext;
    ActionContext actionContext;
    Messages messages = new MessagesImpl();

    public static final Callable<String> ILLEGAL_PROCEED = new Callable<String>() {
        public String call() throws Exception {
            throw new IllegalStateException();
        }
    };

    public RequestContextImpl(com.opensymphony.xwork2.ActionContext xworkContext) {
        this.xworkContext = xworkContext;
    }

    public ActionContext getActionContext() {
        return actionContext;
    }

    public Object getAction() {
        return getActionContext().getAction();
    }

    void setActionContext(ActionContext actionContext) {
        this.actionContext = actionContext;
    }

    public Map<String, String[]> getParameterMap() {
        return xworkContext.getParameters();
    }

    Map<String, Object> attributeMap;

    public Map<String, Object> getAttributeMap() {
        if (attributeMap == null) {
            attributeMap = new RequestMap(getServletRequest());
        }
        return attributeMap;
    }

    public Map<String, Object> getSessionMap() {
        return xworkContext.getSession();
    }

    public Map<String, Object> getApplicationMap() {
        return xworkContext.getApplication();
    }

    public List<Cookie> findCookiesForName(String name) {
        List<Cookie> cookies = new ArrayList<Cookie>();
        for (Cookie cookie : getServletRequest().getCookies())
            if (name.equals(cookie.getName()))
                cookies.add(cookie);

        return cookies;
   }

    public Locale getLocale() {
        return xworkContext.getLocale();
    }

    public void setLocale(Locale locale) {
        xworkContext.setLocale(locale);
    }

    public Messages getMessages() {
        return messages;
    }

    public HttpServletRequest getServletRequest() {
        return (HttpServletRequest) xworkContext.get(HTTP_REQUEST);
    }

    public HttpServletResponse getServletResponse() {
        return (HttpServletResponse) xworkContext.get(HTTP_RESPONSE);
    }

    public ServletContext getServletContext() {
        return (ServletContext) xworkContext.get(SERVLET_CONTEXT);
    }

    ValueStack valueStack;

    public ValueStack getValueStack() {
        if (valueStack == null) {
            valueStack = new ValueStackAdapter(xworkContext.getValueStack());
        }
        return valueStack;
    }

    Callable<String> proceed = ILLEGAL_PROCEED;

    public String proceed() throws Exception {
        return proceed.call();
    }

    public void setProceed(Callable<String> proceed) {
        this.proceed = proceed;
    }

    public Callable<String> getProceed() {
        return proceed;
    }

    static ThreadLocal<Object[]> threadLocalRequestContext = new ThreadLocal<Object[]>() {
        protected RequestContextImpl[] initialValue() {
            return new RequestContextImpl[1];
        }
    };

    /**
     * Creates RequestContext if necessary. Always creates a new ActionContext and restores an existing ActionContext
     * when finished.
     */
    public static String callInContext(ActionInvocation invocation, Callable<String> callable)
            throws Exception {
        RequestContextImpl[] reference = (RequestContextImpl[])threadLocalRequestContext.get();

        if (reference[0] == null) {
            // Initial invocation.
            reference[0] = new RequestContextImpl(invocation.getInvocationContext());
            reference[0].setActionContext(new ActionContextImpl(invocation));
            try {
                return callable.call();
            } finally {
                reference[0] = null;
            }
        } else {
            // Nested invocation.
            RequestContextImpl requestContext = reference[0];
            ActionContext previous = requestContext.getActionContext();
            requestContext.setActionContext(new ActionContextImpl(invocation));
            try {
                return callable.call();
            } finally {
                requestContext.setActionContext(previous);
            }
        }
    }

    public static RequestContextImpl get() {
        RequestContextImpl requestContext = ((RequestContextImpl[])threadLocalRequestContext.get())[0];

        if (requestContext == null)
            throw new IllegalStateException("RequestContext has not been created.");

        return requestContext;
    }
}
