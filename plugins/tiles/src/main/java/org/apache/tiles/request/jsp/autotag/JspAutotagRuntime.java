/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.jsp.autotag;

import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.jsp.JspRequest;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

/**
 * A Runtime for implementing JSP tag libraries.
 */
public class JspAutotagRuntime extends SimpleTagSupport implements AutotagRuntime<Request> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() {
        // do nothing like the parent implementation,
        // but don't throw exceptions either
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Request createRequest() {
        JspContext pageContext = getJspContext();
        return JspRequest.createServletJspRequest(org.apache.tiles.request.jsp.JspUtil.getApplicationContext(pageContext),
            (PageContext) pageContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelBody createModelBody() {
        return new JspModelBody(getJspBody(), getJspContext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getParameter(String name, Class<T> type, T defaultValue) {
        throw new UnsupportedOperationException("the parameters are injected into the tag itself, no need to fetch them");
    }
}
