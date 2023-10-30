/*
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
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Contains preparation operations for a request before execution
 */
public class PrepareOperations {

    private static final Logger LOG = LogManager.getLogger(PrepareOperations.class);

    /**
     * Maintains per-request override of devMode configuration.
     */
    private static final ThreadLocal<Boolean> devModeOverride = new InheritableThreadLocal<>();


    private final Dispatcher dispatcher;
    private static final String STRUTS_ACTION_MAPPING_KEY = "struts.actionMapping";
    private static final String NO_ACTION_MAPPING = "noActionMapping";
    private static final String PREPARE_COUNTER = "__prepare_recursion_counter";
    private static final String WRAP_COUNTER = "__wrap_recursion_counter";

    public PrepareOperations(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Should be called by {@link org.apache.struts2.dispatcher.filter.StrutsPrepareFilter} to track how many times this
     * request has been filtered.
     */
    public void trackRecursion(HttpServletRequest request) {
        incrementRecursionCounter(request, PREPARE_COUNTER);
    }

    /**
     * Cleans up request. When paired with {@link #trackRecursion}, only cleans up once the first filter instance has
     * completed, preventing cleanup by recursive filter calls - i.e. before the request is completely processed.
     */
    public void cleanupRequest(final HttpServletRequest request) {
        decrementRecursionCounter(request, PREPARE_COUNTER, () -> {
            try {
                dispatcher.cleanUpRequest(request);
            } finally {
                ActionContext.clear();
                Dispatcher.setInstance(null);
                devModeOverride.remove();
            }
        });
    }

    /**
     * Creates the action context and initializes the thread local
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @return the action context
     */
    public ActionContext createActionContext(HttpServletRequest request, HttpServletResponse response) {
        ActionContext ctx;
        ActionContext oldContext = ActionContext.getContext();
        if (oldContext != null) {
            // detected existing context, so we are probably in a forward
            ctx = ActionContext.of(new HashMap<>(oldContext.getContextMap())).bind();
        } else {
            ctx = ServletActionContext.getActionContext(request);   //checks if we are probably in an async
            if (ctx == null) {
                ValueStack stack = dispatcher.getContainer().getInstance(ValueStackFactory.class).createValueStack();
                stack.getContext().putAll(dispatcher.createContextMap(request, response, null));
                ctx = ActionContext.of(stack.getContext()).bind();
            }
        }
        return ctx;
    }

    /**
     * Assigns the dispatcher to the dispatcher thread local
     */
    public void assignDispatcherToThread() {
        Dispatcher.setInstance(dispatcher);
    }

    /**
     * Sets the request encoding and locale on the response
     *
     * @param request servlet request
     * @param response servlet response
     */
    public void setEncodingAndLocale(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.prepare(request, response);
    }

    /**
     * Wraps the request with the Struts wrapper that handles multipart requests better
     * Also tracks additional calls to this method on the same request.
     *
     * @param request servlet request
     *
     * @return The new request, if there is one
     * @throws ServletException on any servlet related error
     */
    public HttpServletRequest wrapRequest(HttpServletRequest request) throws ServletException {
        incrementRecursionCounter(request, WRAP_COUNTER);
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
     * Should be called after whenever {@link #wrapRequest} is called. Ensures the request is only cleaned up at the
     * instance it was initially wrapped in the case of multiple wrap calls - i.e. filter recursion.
     */
    public void cleanupWrappedRequest(final HttpServletRequest request) {
        decrementRecursionCounter(request, WRAP_COUNTER, () -> dispatcher.cleanUpRequest(request));
    }

    /**
     * Finds and optionally creates an {@link ActionMapping}.  It first looks in the current request to see if one
     * has already been found, otherwise, it creates it and stores it in the request.  No mapping will be created in the
     * case of static resource requests or unidentifiable requests for other servlets, for example.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @return the action mapping
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
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @return the action mapping
     */
    public ActionMapping findActionMapping(HttpServletRequest request, HttpServletResponse response, boolean forceLookup) {
        ActionMapping mapping = null;

        Object mappingAttr = request.getAttribute(STRUTS_ACTION_MAPPING_KEY);
        if (mappingAttr == null || forceLookup) {
            try {
                mapping = dispatcher.getContainer().getInstance(ActionMapper.class).getMapping(request, dispatcher.getConfigurationManager());
                if (mapping != null) {
                    request.setAttribute(STRUTS_ACTION_MAPPING_KEY, mapping);
                } else {
                    request.setAttribute(STRUTS_ACTION_MAPPING_KEY, NO_ACTION_MAPPING);
                }
            } catch (Exception ex) {
                if (dispatcher.isHandleException() || dispatcher.isDevMode()) {
                    dispatcher.sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
                }
            }
        } else if (!NO_ACTION_MAPPING.equals(mappingAttr)) {
            mapping = (ActionMapping) mappingAttr;
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
                ActionContext.clear();
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
    public boolean isUrlExcluded(HttpServletRequest request, List<Pattern> excludedPatterns) {
        if (excludedPatterns == null) {
            return false;
        }
        String uri = RequestUtils.getUri(request);
        for (Pattern pattern : excludedPatterns) {
            if (pattern.matcher(uri).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set an override of the static devMode value.  Do not set this via a
     * request parameter or any other unprotected method.  Using a signed
     * cookie is one safe way to turn it on per request.
     *
     * @param devMode   the override value
     */
    public static void overrideDevMode(boolean devMode) {
        devModeOverride.set(devMode);
    }

    /**
     * @return Boolean override value, or null if no override
     */
    public static Boolean getDevModeOverride()
    {
        return devModeOverride.get();
    }

    /**
     * Clear any override of the static devMode value being applied to the current thread.
     * This can be useful for any situation where {@link #overrideDevMode(boolean)} might be called
     * in a flow where {@link #cleanupRequest(jakarta.servlet.http.HttpServletRequest)} does not get called.
     * May be very situational (such as some unit tests), but may have other utility as well.
     */
    public static void clearDevModeOverride() {
        devModeOverride.remove();  // Remove current thread's value, enxure next read returns it to initialValue (typically null).
    }

    /**
     * Helper method to potentially count recursive executions with a request attribute. Should be used in conjunction
     * with {@link #decrementRecursionCounter}.
     */
    public static void incrementRecursionCounter(HttpServletRequest request, String attributeName) {
        Integer setCounter = (Integer) request.getAttribute(attributeName);
        if (setCounter == null) {
            setCounter = 0;
        }
        request.setAttribute(attributeName, ++setCounter);
    }

    /**
     * Helper method to count execution completions with a request attribute, and optionally execute some code
     * (e.g. cleanup) once all recursive executions have completed. Should be used in conjunction with
     * {@link #incrementRecursionCounter}.
     */
    public static void decrementRecursionCounter(HttpServletRequest request, String attributeName, Runnable runnable) {
        Integer setCounter = (Integer) request.getAttribute(attributeName);
        if (setCounter != null) {
            request.setAttribute(attributeName, --setCounter);
        }
        if ((setCounter == null || setCounter == 0) && runnable != null) {
            runnable.run();
        }
    }
}
