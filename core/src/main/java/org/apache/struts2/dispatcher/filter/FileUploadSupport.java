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
package org.apache.struts2.dispatcher.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.ServletMultiPartRequest;
import sun.print.resources.serviceui;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Detects if request is a multipart request with file upload, it works
 * only when combined with {@link StrutsExecuteFilter} or {@link StrutsPrepareAndExecuteFilter}
 * and {@link org.apache.struts2.dispatcher.servlet.FileUploadServlet}
 */
interface FileUploadSupport {

    Logger LOG = LogManager.getLogger(FileUploadSupport.class);

    default boolean shouldSkipProcessingFileUploadRequest(HttpServletRequest request) throws ServletException {
        Dispatcher dispatcher = Dispatcher.getInstance(request.getServletContext());

        if (dispatcher == null) {
            throw new ServletException("Dispatcher is not initialised!");
        }

        if (dispatcher.isMultipartRequest(request)) {
            MultiPartRequest multiPartRequest = dispatcher.getContainer().getInstance(MultiPartRequest.class);
            if (multiPartRequest instanceof ServletMultiPartRequest) {
                LOG.debug("Using the new Servlet API 3.1 based file upload support");
                if (dispatcher.isMultipartSupportEnabled(request)) {
                    LOG.debug("The file upload request is going to be handled by servlet");
                    return true;
                }
            } else if (LOG.isDebugEnabled()){
                String servletPath = RequestUtils.getServletPath(request);
                LOG.debug("Continue processing request: {} as other implementation of: {} is used: {}",
                    servletPath, MultiPartRequest.class, multiPartRequest.getClass());
            }
        }
        return false;
    }

}
