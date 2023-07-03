/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.freemarker.ext.jsp;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.jsp.JspEngineInfo;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.PageContext;

/**
 */
abstract class FreeMarkerJspFactory extends JspFactory {
    protected abstract String getSpecificationVersion();
    
    @Override
    public JspEngineInfo getEngineInfo() {
        return new JspEngineInfo() {
            @Override
            public String getSpecificationVersion() {
                return FreeMarkerJspFactory.this.getSpecificationVersion();
            }
        };
    }

    @Override
    public PageContext getPageContext(Servlet servlet, ServletRequest request, 
            ServletResponse response, String errorPageURL, 
            boolean needsSession, int bufferSize, boolean autoFlush) {
        // This is never meant to be called. JSP pages compiled to Java 
        // bytecode use this API, but in FreeMarker, we're running templates,
        // and not JSP pages precompiled to bytecode, therefore we have no use
        // for this API.
        throw new UnsupportedOperationException();
    }

    @Override
    public void releasePageContext(PageContext ctx) {
        // This is never meant to be called. JSP pages compiled to Java 
        // bytecode use this API, but in FreeMarker, we're running templates,
        // and not JSP pages precompiled to bytecode, therefore we have no use
        // for this API.
        throw new UnsupportedOperationException();
    }
}