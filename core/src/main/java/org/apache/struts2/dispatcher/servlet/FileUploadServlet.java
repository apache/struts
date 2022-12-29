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
package org.apache.struts2.dispatcher.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A dedicated servlet, which is used to support multipart requests using the Servlet API 3.1.
 * It works in connection with @{link {@link org.apache.struts2.dispatcher.multipart.ServletMultiPartRequest}}
 */
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(FileUploadServlet.class);

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Dispatcher dispatcher = Dispatcher.getInstance(request.getServletContext());
        if (dispatcher == null) {
            throw new ServletException("No Dispatchers instance, servlet used out of the Struts filters!");
        }

        if (!dispatcher.isMultipartSupportEnabled(request)) {
            LOG.warn("Support for file upload is disabled! Either please remove this servlet from web.xml or enable support for multipart requests using: {}!",
                StrutsConstants.STRUTS_MULTIPART_ENABLED);
            return;
        }

        PrepareOperations prepare = new PrepareOperations(dispatcher);
        ExecuteOperations execute = new ExecuteOperations(dispatcher);

        String uri = RequestUtils.getUri(request);

        if (dispatcher.isMultipartRequest(request)) {
            prepare.setEncodingAndLocale(request, response);
            prepare.createActionContext(request, response);
            prepare.assignDispatcherToThread();
            HttpServletRequest wrappedRequest = prepare.wrapRequest(request);
            ActionMapping mapping = prepare.findActionMapping(wrappedRequest, response, true);
            if (mapping == null) {
                throw new ServletException(String.format("Cannot find mapping for %s, passing to other filters", uri));
            } else {
                LOG.trace("Found mapping {} for {}", mapping, uri);
                execute.executeAction(wrappedRequest, response, mapping);
            }
        } else {
            LOG.debug("Not a file upload request, ignoring uri: {}", uri);
        }
    }

}
