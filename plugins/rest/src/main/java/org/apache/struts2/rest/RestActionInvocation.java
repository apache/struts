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

package org.apache.struts2.rest;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.DefaultActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpHeaderResult;
import org.apache.struts2.rest.handler.ContentTypeHandler;
import org.apache.struts2.rest.handler.HtmlHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * Extends the usual {@link ActionInvocation} to add support for processing the object returned
 * from the action execution.  This allows us to support methods that return {@link HttpHeaders}
 * as well as apply content type-specific operations to the result.
 */
public class RestActionInvocation extends DefaultActionInvocation {

    private static final long serialVersionUID = 3485701178946428716L;

    private static final Logger LOG = LoggerFactory.getLogger(RestActionInvocation.class);

    private ContentTypeHandlerManager handlerSelector;
    private boolean logger;
    private String defaultErrorResultName;
    private boolean restrictToGet = true;

    protected HttpHeaders httpHeaders;
    protected Object target;
    protected boolean isFirstInterceptor = true;
    protected boolean hasErrors;

    protected RestActionInvocation(final Map<String, Object> extraContext, boolean pushAction) {
        super(extraContext, pushAction);
    }

    @Inject("struts.rest.logger")
    public void setLogger(String value) {
        logger = Boolean.valueOf(value);
    }

    @Inject("struts.rest.defaultErrorResultName")
    public void setDefaultErrorResultName(String value) {
        defaultErrorResultName = value;
    }

    /**
     * If set to true (by default) blocks returning content from any other methods than GET,
     * if set to false, the content can be returned for any kind of method
     * 
     * @param value true or false
     */
    @Inject(value = "struts.rest.content.restrictToGET", required = false)
    public void setRestrictToGet(String value) {
        restrictToGet = "true".equalsIgnoreCase(value);
    }

    @Inject
    public void setMimeTypeHandlerSelector(ContentTypeHandlerManager sel) {
        this.handlerSelector = sel;
    }

    /**
     * Save the result to be used later.
     * @param actionConfig current ActionConfig
     * @param methodResult the result of the action.
     * @return the result code to process.
     *
     * @throws ConfigurationException If it is an incorrect result.
     */
    @Override
    protected String saveResult(ActionConfig actionConfig, Object methodResult) {
        if (methodResult instanceof Result) {
            explicitResult = (Result) methodResult;
            // Wire the result automatically
            container.inject(explicitResult);
        } else if (methodResult instanceof HttpHeaders) {
            httpHeaders = (HttpHeaders) methodResult;
            resultCode = httpHeaders.getResultCode();
        } else if (methodResult instanceof String) {
            resultCode = (String) methodResult;
        } else if (methodResult != null) {
            throw new ConfigurationException("The result type " + methodResult.getClass()
                    + " is not allowed. Use the type String, HttpHeaders or Result.");
        }
        return resultCode;
    }

    @Override
    public String invoke() throws Exception {
        long startTime = 0;

        boolean executeResult = false;
        if (isFirstInterceptor) {
            startTime = System.currentTimeMillis();
            executeResult = true;
            isFirstInterceptor = false;
        }

        // Normal invoke without execute the result
        proxy.setExecuteResult(false);
        resultCode = super.invoke();

        // Execute the result when the last interceptor has finished
        if (executeResult) {
            long middleTime = System.currentTimeMillis();

            try {
                processResult();

            } catch (ConfigurationException e) {
                throw e;

            } catch (Exception e) {

                // Error proccesing the result
                LOG.error("Exception processing the result.", e);

                if (!ServletActionContext.getResponse().isCommitted()) {
                    ServletActionContext.getResponse()
                        .setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    stack.set("exception", e);
                    result = null;
                    resultCode = null;
                    processResult();
                }
            }

            // Log execution + result time
            logger(startTime, middleTime);
        }

        return resultCode;
    }

    protected void processResult() throws Exception {
        String timerKey = "processResult: " + getResultCode();
        try {
            UtilTimerStack.push(timerKey);

            HttpServletRequest request = ServletActionContext.getRequest();
            HttpServletResponse response = ServletActionContext.getResponse();

            // Select the target
            selectTarget();

            // Get the httpHeaders
            if (httpHeaders == null) {
                httpHeaders = new DefaultHttpHeaders(resultCode);
            }

            // Apply headers
            if (!hasErrors) {
                httpHeaders.apply(request, response, target);
            } else {
                disableCatching(response);
            }

            // Don't return content on a not modified
            if (httpHeaders.getStatus() != HttpServletResponse.SC_NOT_MODIFIED ) {
                executeResult();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Result not processed because the status code is not modified.");
                }
            }

        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    /**
     * Execute the current result. If it is an error and no result is selected load
     * the default error result (default-error).
     */
    private void executeResult() throws Exception {

        // Get handler by representation
        ContentTypeHandler handler = handlerSelector.getHandlerForResponse(
                ServletActionContext.getRequest(), ServletActionContext.getResponse());

        // get the result
        this.result = createResult();

        if (this.result instanceof HttpHeaderResult) {

            // execute the result to apply headers and status in every representations
            this.result.execute(this);
            updateStatusFromResult();
        }

        if (handler != null && !(handler instanceof HtmlHandler)) {

            // Specific representation (json, xml...)
            resultCode = handlerSelector.handleResult(this, httpHeaders, target);
        } else {

            // Normal struts execution (html o other struts result)
            findResult();
            if (result != null) {
                this.result.execute(this);

            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No result returned for action " + getAction().getClass().getName()
                            + " at " + proxy.getConfig().getLocation());
                }
            }
        }
    }

    /**
     * Get the status code from HttpHeaderResult and it is saved in the HttpHeaders object.
     */
    protected void updateStatusFromResult() {
        if (result instanceof HttpHeaderResult) {
            int status = ((HttpHeaderResult) result).getStatus();
            if (status != -1) {
                httpHeaders.setStatus(status);
            }
        }
    }

    /**
     * Find the most appropriate result:
     * - Find by result code.
     * - If it is an error, find the default error result.
     *
     * @throws ConfigurationException If not result can be found
     */
    private void findResult() throws Exception {

        boolean isHttpHeaderResult = false;
        if (result != null && result instanceof HttpHeaderResult) {
            result = null;
            isHttpHeaderResult = true;
        }

        if (result == null && resultCode != null && !Action.NONE.equals(resultCode)
                && unknownHandlerManager.hasUnknownHandlers()) {

            // Find result by resultCode
            this.result = unknownHandlerManager.handleUnknownResult(
                    invocationContext, proxy.getActionName(), proxy.getConfig(), resultCode);
        }

        if (result == null && hasErrors && defaultErrorResultName != null) {

            // Get default error result
            ResultConfig resultConfig = this.proxy.getConfig().getResults().get(defaultErrorResultName);
            if (resultConfig != null) {
                this.result = objectFactory.buildResult(resultConfig, invocationContext.getContextMap());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Found default error result.");
                }
            }
        }

        if (result == null && resultCode != null &&
                !Action.NONE.equals(resultCode) && !isHttpHeaderResult) {
            throw new ConfigurationException("No result defined for action "
                    + getAction().getClass().getName()
                    + " and result " + getResultCode(), proxy.getConfig());
        }
    }

    @SuppressWarnings("unchecked")
    protected void selectTarget() {

        // Select target (content to return)
        Throwable e = (Throwable)stack.findValue("exception");
        if (e != null) {

            // Exception
            target = e;
            hasErrors = true;

        } else if (action instanceof ValidationAware && ((ValidationAware)action).hasErrors()) {

            // Error messages
            ValidationAware validationAwareAction = ((ValidationAware)action);

            Map errors = new HashMap();
            if (validationAwareAction.getActionErrors().size() > 0) {
                errors.put("actionErrors", validationAwareAction.getActionErrors());
            }
            if (validationAwareAction.getFieldErrors().size() > 0) {
                errors.put("fieldErrors", validationAwareAction.getFieldErrors());
            }
            target = errors;
            hasErrors = true;

        } else if (action instanceof ModelDriven) {

            // Model
            target = ((ModelDriven)action).getModel();

        } else {
            target = action;
        }

        if (shouldRestrictToGET()) {
            target = null;
        }
    }

    // don't return any content for PUT, DELETE, and POST where there are no errors
    // or backward compatible restrictToGET flag is set to true
    private boolean shouldRestrictToGET() {
        return !hasErrors
                && !"get".equalsIgnoreCase(ServletActionContext.getRequest().getMethod())
                && restrictToGet;
    }

    private void disableCatching(HttpServletResponse response) {
        // No cache
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Last-Modified", 0);
        response.setHeader("ETag", "-1");
    }

    private void logger(long startTime, long middleTime) {
         if (logger && LOG.isInfoEnabled()) {
             long endTime = System.currentTimeMillis();
             long executionTime = middleTime - startTime;
             long processResult = endTime - middleTime;
             long total = endTime - startTime;

             String message = "Executed action [/";
             String namespace = getProxy().getNamespace();
             if ((namespace != null) && (namespace.trim().length() > 1)) {
                 message += namespace + "/";
             }
             message += getProxy().getActionName() + "!" + getProxy().getMethod();
             String extension = handlerSelector.findExtension(
                     ServletActionContext.getRequest().getRequestURI());
             if (extension != null) {
                 message += "!" + extension;
             }
             if (httpHeaders != null) {
                 message += "!" + httpHeaders.getStatus();
             }
             message += "] took " + total + " ms (execution: " + executionTime
                + " ms, result: " + processResult + " ms)";

             LOG.info(message);
         }
    }

}
