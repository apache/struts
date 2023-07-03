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

import freemarker.core.Environment;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.UndeclaredThrowableException;

import jakarta.servlet.jsp.PageContext;

/**
 */
class PageContextFactory {
    private static final Class pageContextImpl = getPageContextImpl();
    
    private static Class getPageContextImpl() {
        try {
            try {
                PageContext.class.getMethod("getELContext", (Class[]) null);
                return Class.forName("freemarker.ext.jsp._FreeMarkerPageContext21");
            } catch (NoSuchMethodException e1) {
                try {
                    PageContext.class.getMethod("getExpressionEvaluator", (Class[]) null);
                    return Class.forName("freemarker.ext.jsp._FreeMarkerPageContext2");
                } catch (NoSuchMethodException e2) {
                    throw new IllegalStateException(
                            "Since FreeMarker 2.3.24, JSP support requires at least JSP 2.0.");
                }
            }
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    static FreeMarkerPageContext getCurrentPageContext() throws TemplateModelException {
        Environment env = Environment.getCurrentEnvironment();
        TemplateModel pageContextModel = env.getGlobalVariable(PageContext.PAGECONTEXT);
        if (pageContextModel instanceof FreeMarkerPageContext) {
            return (FreeMarkerPageContext) pageContextModel;
        }
        try {
            FreeMarkerPageContext pageContext = 
                (FreeMarkerPageContext) pageContextImpl.newInstance();
            env.setGlobalVariable(PageContext.PAGECONTEXT, pageContext);
            return pageContext;
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        } catch (InstantiationException e) {
            throw new UndeclaredThrowableException(e);
        }
    }
    
}
