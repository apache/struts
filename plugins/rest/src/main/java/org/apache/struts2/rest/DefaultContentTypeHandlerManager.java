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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.ContentTypeHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manages {@link ContentTypeHandler} instances and uses them to
 * process results
 */
public class DefaultContentTypeHandlerManager implements ContentTypeHandlerManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContentTypeHandlerManager.class);

    /** ContentTypeHandlers keyed by the extension */
    Map<String, ContentTypeHandler> handlersByExtension = new HashMap<String, ContentTypeHandler>();
    /** ContentTypeHandlers keyed by the content-type */
    Map<String, ContentTypeHandler> handlersByContentType = new HashMap<String, ContentTypeHandler>();

    private String defaultExtension;

    @Inject("struts.rest.defaultExtension")
    public void setDefaultExtension(String name) {
        this.defaultExtension = name;
    }

    @Inject
    public void setContainer(Container container) {
        Set<String> names = container.getInstanceNames(ContentTypeHandler.class);
        for (String name : names) {
            ContentTypeHandler handler = container.getInstance(ContentTypeHandler.class, name);

            if (handler.getExtension() != null) {
                // Check for overriding handlers for the current extension
                String overrideName = container.getInstance(String.class, STRUTS_REST_HANDLER_OVERRIDE_PREFIX + handler.getExtension());
                if (overrideName != null) {
                    if (!handlersByExtension.containsKey(handler.getExtension())) {
                        handler = container.getInstance(ContentTypeHandler.class, overrideName);
                    } else {
                        // overriding handler has already been registered
                        continue;
                    }
                }
                this.handlersByExtension.put(handler.getExtension(), handler);
            }

            if (handler.getContentType() != null) {
                //dont store character encoding
                String typeOnly = handler.getContentType() ;
                int index = handler.getContentType().indexOf(';');
                if (index != -1)
                {
                    typeOnly = handler.getContentType().substring(0, index).trim();
                }

                this.handlersByContentType.put(typeOnly, handler);
            }
        }
    }

    /**
     * Gets the handler for the request by looking at the request content type and extension
     * @param request The request
     * @return The appropriate handler
     */
    public ContentTypeHandler getHandlerForRequest(HttpServletRequest request) {
        ContentTypeHandler handler = null;
        String contentType = request.getContentType();
        if (contentType != null) {
            handler = handlersByContentType.get(contentType);
            if (handler == null) {
                // strip off encoding and search again (e.g., application/json;charset=ISO-8859-1)
                int index = contentType.indexOf(';');
                if (index != -1) {
                    contentType = contentType.substring(0, index).trim();
                }
                handler = handlersByContentType.get(contentType);
            }
        }
        if (handler == null) {
            String extension = findExtension(request.getRequestURI());
            handler = handlersByExtension.get(extension);
        }
        return handler;
    }

    /**
     * Gets the handler for the response by looking at the extension of the request
     * @param request The request
     * @return The appropriate handler
     *
     * WW-4588: modified to get a handler for the response side and auto generate the response type
     * from the Accept: header
     *
     */
    public ContentTypeHandler getHandlerForResponse(HttpServletRequest request, HttpServletResponse res) {

        String extension = getExtensionIfPresent(request.getRequestURI());
        if (extension == null) {
            extension = defaultExtension;
            final String acceptHeader = request.getHeader("accept") ;
            if (acceptHeader != null) {
                final String[] types = acceptHeader.split(",");
                for (final String type : types) {
                    final ContentTypeHandler handler = findHandler(type);
                    if (handler != null) {
                        return handler;
                    }
                }
            }
        }
        return handlersByExtension.get(extension);
    }

    private ContentTypeHandler findHandler(final String type) {
        ContentTypeHandler handler = handlersByContentType.get(type);
        if (handler == null) {
            // strip off encoding and search again (e.g., application/json;charset=ISO-8859-1)
            final int index = type.indexOf(';');
            if (index != -1) {
                return handlersByContentType.get(type.substring(0, index).trim());
            }
        }
        return handler;
    }

    public String handleResult(ActionConfig actionConfig, Object methodResult, Object target) throws IOException {
        LOG.warn("This method is deprecated!");
        return readResultCode(methodResult);
    }

    /**
     * Handles the result using handlers to generate content type-specific content
     * 
     * @param invocation The action invocation for the current request
     * @param methodResult The object returned from the action method
     * @param target The object to return, usually the action object
     * @return The new result code to process
     * @throws IOException If unable to write to the response
     */
    public String handleResult(ActionInvocation invocation, Object methodResult, Object target) throws IOException {
        String resultCode = readResultCode(methodResult);
        Integer statusCode = readStatusCode(methodResult);
        HttpServletRequest req = ServletActionContext.getRequest();
        HttpServletResponse res = ServletActionContext.getResponse();
        ActionConfig actionConfig = invocation.getProxy().getConfig();

        if(statusCode != null) {
            res.setStatus(statusCode);
        }

        ContentTypeHandler handler = getHandlerForResponse(req, res);
        if (handler != null) {
            String extCode = resultCode + "." + handler.getExtension();
            if (actionConfig.getResults().get(extCode) != null) {
                resultCode = extCode;
            } else {
                StringWriter writer = new StringWriter();
                resultCode = handler.fromObject(invocation, target, resultCode, writer);
                String text = writer.toString();
                if (text.length() > 0) {
                    byte[] data = text.getBytes("UTF-8");
                    res.setContentLength(data.length);
                    res.setContentType(handler.getContentType());
                    res.getOutputStream().write(data);
                    res.getOutputStream().flush();
                }
            }
        }
        return resultCode;
    }


    protected Integer readStatusCode(Object methodResult) {
        if (methodResult instanceof HttpHeaders) {
            return ((HttpHeaders) methodResult).getStatus();
        } else {
            return null;
        }
    }

    protected String readResultCode(Object methodResult) {
        if (methodResult == null) {
            return null;
        }
        if (methodResult instanceof HttpHeaders) {
            return ((HttpHeaders) methodResult).getResultCode();
        } else {
            return methodResult.toString();
        }
    }

    /**
     * Gets the extension in the url
     *
     * @param url The url
     * @return The extension, or null
     */
    public String getExtensionIfPresent(String url) {
        int dotPos = url.lastIndexOf('.');
        int slashPos = url.lastIndexOf('/');
        if (dotPos > slashPos && dotPos > -1) {
            return url.substring(dotPos + 1);
        }
        return null;
    }

    /**
     * Finds the extension in the url
     * 
     * @param url The url
     * @return The extension, or the default extension if there is none
     */
    public String findExtension(String url) {
        int dotPos = url.lastIndexOf('.');
        int slashPos = url.lastIndexOf('/');
        if (dotPos > slashPos && dotPos > -1) {
            return url.substring(dotPos + 1);
        }
        return defaultExtension;
    }
}
