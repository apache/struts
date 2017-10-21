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

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import freemarker.template.Template;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.views.freemarker.FreemarkerManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Default implementation of {@link org.apache.struts2.dispatcher.DispatcherErrorHandler}
 * which sends Error Report in devMode or {@link javax.servlet.http.HttpServletResponse#sendError} otherwise.
 */
public class DefaultDispatcherErrorHandler implements DispatcherErrorHandler {

    private static final Logger LOG = LogManager.getLogger(DefaultDispatcherErrorHandler.class);

    private FreemarkerManager freemarkerManager;
    private boolean devMode;
    private Template template;

    @Inject
    public void setFreemarkerManager(FreemarkerManager freemarkerManager) {
        this.freemarkerManager = freemarkerManager;
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String devMode) {
        this.devMode = BooleanUtils.toBoolean(devMode);
    }

    public void init(ServletContext ctx) {
        try {
            freemarker.template.Configuration config = freemarkerManager.getConfiguration(ctx);
            template = config.getTemplate("/org/apache/struts2/dispatcher/error.ftl");
        } catch (IOException e) {
            throw new StrutsException(e);
        }
    }

    public void handleError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
        Boolean devModeOverride = PrepareOperations.getDevModeOverride();
        if (devModeOverride != null ? devModeOverride : devMode) {
            handleErrorInDevMode(response, code, e);
        } else {
            sendErrorResponse(request, response, code, e);
        }
    }

    protected void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
        try {
            // WW-1977: Only put errors in the request when code is a 500 error
            if (code == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                // WW-4103: Only logs error when application error occurred, not Struts error
                LOG.error("Exception occurred during processing request: {}", e.getMessage(), e);
                // send a http error response to use the servlet defined error handler
                // make the exception available to the web.xml defined error page
                request.setAttribute("javax.servlet.error.exception", e);

                // for compatibility
                request.setAttribute("javax.servlet.jsp.jspException", e);
            }

            // send the error response
            response.sendError(code, e.getMessage());
        } catch (IOException e1) {
            // we're already sending an error, not much else we can do if more stuff breaks
        }
    }

    protected void handleErrorInDevMode(HttpServletResponse response, int code, Exception e) {
        LOG.debug("Exception occurred during processing request: {}", e.getMessage(), e);
        try {
            List<Throwable> chain = new ArrayList<>();
            Throwable cur = e;
            chain.add(cur);
            while ((cur = cur.getCause()) != null) {
                chain.add(cur);
            }

            Writer writer = new StringWriter();
            template.process(createReportData(e, chain), writer);

            response.setContentType("text/html");
            response.getWriter().write(writer.toString());
            response.getWriter().close();
        } catch (Exception exp) {
            try {
                LOG.debug("Cannot show problem report!", exp);
                response.sendError(code, "Unable to show problem report:\n" + exp + "\n\n" + LocationUtils.getLocation(exp));
            } catch (IOException ex) {
                // we're already sending an error, not much else we can do if more stuff breaks
            }
        }
    }

    protected HashMap<String, Object> createReportData(Exception e, List<Throwable> chain) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("exception", e);
        data.put("unknown", Location.UNKNOWN);
        data.put("chain", chain);
        data.put("locator", new Dispatcher.Locator());
        return data;
    }
}
