/*
 * $Id: Restful2ActionMapper.java 540819 2007-05-23 02:48:36Z mrdon $
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

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.ContentTypeHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manages {@link ContentTypeHandler} instances and uses them to
 * process results
 */
public class ContentTypeHandlerManager {

    Map<String,ContentTypeHandler> handlers = new HashMap<String,ContentTypeHandler>();
    String defaultExtension;
    public static final String STRUTS_REST_HANDLER_OVERRIDE_PREFIX = "struts.rest.handlerOverride.";

    @Inject("struts.rest.defaultExtension")
    public void setDefaultExtension(String name) {
        this.defaultExtension = name;
    }

    @Inject
    public void setContainer(Container container) {
        Set<String> names = container.getInstanceNames(ContentTypeHandler.class);
        for (String name : names) {
            ContentTypeHandler handler = container.getInstance(ContentTypeHandler.class, name);

            // Check for overriding handlers for the current extension
            String overrideName = container.getInstance(String.class, STRUTS_REST_HANDLER_OVERRIDE_PREFIX +handler.getExtension());
            if (overrideName != null) {
                if (!handlers.containsKey(handler.getExtension())) {
                    handler = container.getInstance(ContentTypeHandler.class, overrideName);
                } else {
                    // overriding handler has already been registered
                    continue;
                }
            }
            this.handlers.put(handler.getExtension(), handler);
        }
    }
    
    /**
     * Gets the handler for the request by looking at the extension
     * @param req The request
     * @return The appropriate handler
     */
    public ContentTypeHandler getHandlerForRequest(HttpServletRequest req) {
        String extension = findExtension(req.getRequestURI());
        if (extension == null) {
            extension = defaultExtension;
        }
        return handlers.get(extension);
    }
    
    /**
     * Handles the result using handlers to generate content type-specific content
     * 
     * @param actionConfig The action config for the current request
     * @param methodResult The object returned from the action method
     * @param target The object to return, usually the action object
     * @return The new result code to process
     * @throws IOException If unable to write to the response
     */
    public String handleResult(ActionConfig actionConfig, Object methodResult, Object target)
            throws IOException {
        String resultCode = null;
        HttpServletRequest req = ServletActionContext.getRequest();
        HttpServletResponse res = ServletActionContext.getResponse();
        if (target instanceof ModelDriven) {
            target = ((ModelDriven)target).getModel();
        }

        boolean statusNotOk = false;
        if (methodResult instanceof HttpHeaders) {
            HttpHeaders info = (HttpHeaders) methodResult;
            resultCode = info.apply(req, res, target);
            if (info.getStatus() != SC_OK) {

                // Don't return content on a not modified
                if (info.getStatus() == SC_NOT_MODIFIED) {
                    target = null;
                } else {
                    statusNotOk = true;
                }

            }
        } else {
            resultCode = (String) methodResult;
        }
        
        // Don't return any content for PUT, DELETE, and POST where there are no errors
        if (!statusNotOk && !"get".equalsIgnoreCase(req.getMethod())) {
            target = null;
        }

        ContentTypeHandler handler = getHandlerForRequest(req);
        if (handler != null) {
            String extCode = resultCode+"-"+handler.getExtension();
            if (actionConfig.getResults().get(extCode) != null) {
                resultCode = extCode;
            } else {
                StringWriter writer = new StringWriter();
                resultCode = handler.fromObject(target, resultCode, writer);
                String text = writer.toString();
                if (text.length() > 0) {
                    byte[] data = text.getBytes("UTF-8");
                    res.setContentLength(data.length);
                    res.setContentType(handler.getContentType());
                    res.getOutputStream().write(data);
                    res.getOutputStream().close();
                }
            }
        }
        return resultCode;
        
    }
    
    /**
     * Finds the extension in the url
     * 
     * @param url The url
     * @return The extension
     */
    protected String findExtension(String url) {
        int dotPos = url.lastIndexOf('.');
        int slashPos = url.lastIndexOf('/');
        if (dotPos > slashPos && dotPos > -1) {
            return url.substring(dotPos+1);
        }
        return null;
    }
}
