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
package org.apache.struts2.dispatcher.multipart;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletRequestContext;

/**
 * Provides a specialized request context for Struts applications,
 * extending the Jakarta Servlet request context to add custom handling
 * for multipart-related requests.
 * <p>
 * This class overrides multipart detection logic to ensure that requests
 * without a content type are not treated as multipart, improving robustness
 * in file upload scenarios.
 */
public class StrutsRequestContext extends JakartaServletRequestContext {
    
    /**
     * Constructs a context for this request.
     *
     * @param request The request to which this context applies.
     */
    public StrutsRequestContext(HttpServletRequest request) {
        super(request);
    }

    /**
     * Determines if the current request is multipart-related.
     * <p>
     * This implementation first checks if the request's content type is set.
     * If the content type is {@code null}, it returns {@code false} immediately.
     * Otherwise, it delegates to the superclass implementation to perform
     * further checks.
     *
     * @return {@code true} if the request is multipart-related; {@code false} otherwise.
     */
    @Override
    public boolean isMultipartRelated() {
        if (this.getRequest().getContentType() == null) {
            return false;
        }
        return super.isMultipartRelated();
    }
}
