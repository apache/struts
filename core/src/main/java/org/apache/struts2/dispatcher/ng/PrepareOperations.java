/*
 * $Id: DefaultActionSupport.java 651946 2008-04-27 13:41:38Z apetrelli $
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
package org.apache.struts2.dispatcher.ng;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Contains preparation operations for a request before execution
 */
public class PrepareOperations {

    private static final Logger LOG = LoggerFactory.getLogger(PrepareOperations.class);

    private Dispatcher dispatcher;
    private static final String STRUTS_ACTION_MAPPING_KEY = "struts.actionMapping";
    public static final String CLEANUP_RECURSION_COUNTER = "__cleanup_recursion_counter";
    private Logger log = LoggerFactory.getLogger(PrepareOperations.class);

    @Deprecated
    public PrepareOperations(ServletContext servletContext, Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public PrepareOperations(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Creates the action context and initializes the thread local
     */
    public ActionContext createActionContext(HttpServletRequest request, HttpServletResponse response) {
        ActionContext ctx;
        Integer counter = 1;
        Integer oldCounter = (Integer) request.getAttribute(CLEANUP_RECURSION_COUNTER);
        if (oldCounter != null) {
            counter = oldCounter + 1;
        }
        
        ActionContext oldContext = ActionContext.getContext();
        if (oldContext != null) {
            // detected existing context, so we are probably in a forward
            ctx = new ActionContext(new HashMap<String, Object>(oldContext.getContextMap()));
        } else {
            ValueStack stack = dispatcher.getContainer().getInstance(ValueStackFactory.class).createValueStack();
            stack.getContext().putAll(dispatcher.createContextMap(request, response, null));
            ctx = new ActionContext(stack.getContext());
        }
        request.setAttribute(CLEANUP_RECURSION_COUNTER, counter);
        ActionContext.setContext(ctx);
        return ctx;
    }

    /**
     * Cleans up a request of thread locals
     */
    public void cleanupRequest(HttpServletRequest request) {
        Integer counterVal = (Integer) request.getAttribute(CLEANUP_RECURSION_COUNTER);
        if (counterVal != null) {
            counterVal -= 1;
            request.setAttribute(CLEANUP_RECURSION_COUNTER, counterVal);
            if (counterVal > 0 ) {
                if (log.isDebugEnabled()) {
                    log.debug("skipping cleanup counter="+counterVal);
                }
                return;
            }
        }
        // always clean up the thread request, even if an action hasn't been executed
        try {
            dispatcher.cleanUpRequest(request);
        } finally {
            ActionContext.setContext(null);
            Dispatcher.setInstance(null);
        }
    }

    /**
     * Assigns the dispatcher to the dispatcher thread local
     */
    public void assignDispatcherToThread() {
        Dispatcher.setInstance(dispatcher);
    }

    /**
     * Sets the request encoding and locale on the response
     */
    public void setEncodingAndLocale(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.prepare(request, response);
    }

    /**
     * Wraps the request with the Struts wrapper that handles multipart requests better
     * @return The new request, if there is one
     * @throws ServletException
     */
    public HttpServletRequest wrapRequest(HttpServletRequest oldRequest) throws ServletException {
        HttpServletRequest request = oldRequest;
        try {
            // Wrap request first, just in case it is multipart/form-data
            // parameters might not be accessible through before encoding (ww-1278)
            request = dispatcher.wrapRequest(request);
            ServletActionContext.setRequest(request);
        } catch (IOException e) {
            throw new ServletException("Could not wrap servlet request with MultipartRequestWrapper!", e);
        }
        return request;
    }

    /**
     *   Finds and optionally creates an {@link ActionMapping}.  It first looks in the current request to see if one
     * has already been found, otherwise, it creates it and stores it in the request.  No mapping will be created in the
     * case of static resource requests or unidentifiable requests for other servlets, for example.
     */
    public ActionMapping findActionMapping(HttpServletRequest request, HttpServletResponse response) {
        return findActionMapping(request, response, false);
    }

    /**
     * Finds and optionally creates an {@link ActionMapping}.  if forceLookup is false, it first looks in the current request to see if one
     * has already been found, otherwise, it creates it and stores it in the request.  No mapping will be created in the
     * case of static resource requests or unidentifiable requests for other servlets, for example.
     * @param forceLookup if true, the action mapping will be looked up from the ActionMapper instance, ignoring if there is one
     * in the request or not 
     */
    public ActionMapping findActionMapping(HttpServletRequest request, HttpServletResponse response, boolean forceLookup) {
        ActionMapping mapping = (ActionMapping) request.getAttribute(STRUTS_ACTION_MAPPING_KEY);
        if (mapping == null || forceLookup) {
            try {
                mapping = dispatcher.getContainer().getInstance(ActionMapper.class).getMapping(request, dispatcher.getConfigurationManager());
                if (mapping != null) {
                    request.setAttribute(STRUTS_ACTION_MAPPING_KEY, mapping);
                }
            } catch (Exception ex) {
                if (dispatcher.isHandleException() || dispatcher.isDevMode()) {
                    dispatcher.sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
                }
            }
        }

        return mapping;
    }

    /**
     * Cleans up the dispatcher instance
     */
    public void cleanupDispatcher() {
        if (dispatcher == null) {
            throw new StrutsException("Something is seriously wrong, Dispatcher is not initialized (null) ");
        } else {
            try {
                dispatcher.cleanup();
            } finally {
                ActionContext.setContext(null);
            }
        }
    }

    /**
     * Check whether the request matches a list of exclude patterns.
     *
     * @param request          The request to check patterns against
     * @param excludedPatterns list of patterns for exclusion
     *
     * @return <tt>true</tt> if the request URI matches one of the given patterns
     */
    public boolean isUrlExcluded( HttpServletRequest request, List<Pattern> excludedPatterns ) {
        if (excludedPatterns != null) {
            String uri = RequestUtils.getUri(request);
            for ( Pattern pattern : excludedPatterns ) {
                if (pattern.matcher(uri).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

}
