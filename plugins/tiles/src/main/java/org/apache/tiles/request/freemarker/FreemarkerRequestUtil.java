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
package org.apache.tiles.request.freemarker;

import freemarker.core.Environment;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.TemplateModelException;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.servlet.ServletUtil;

/**
 * Utilities to work with Freemarker requests.
 */
public final class FreemarkerRequestUtil {

    /**
     * Constructor.
     */
    private FreemarkerRequestUtil() {
    }

    /**
     * Returns the HTTP request hash model.
     *
     * @param env The current FreeMarker environment.
     * @return The request hash model.
     */
    public static HttpRequestHashModel getRequestHashModel(Environment env) {
        try {
            return (HttpRequestHashModel) env.getDataModel().get(FreemarkerServlet.KEY_REQUEST);
        } catch (TemplateModelException e) {
            throw new NotAvailableFreemarkerServletException("Exception got when obtaining the request hash model", e);
        }
    }

    /**
     * Returns the servlet context hash model.
     *
     * @param env The current FreeMarker environment.
     * @return The servlet context hash model.
     */
    public static ServletContextHashModel getServletContextHashModel(Environment env) {
        try {
            return (ServletContextHashModel) env.getDataModel().get(FreemarkerServlet.KEY_APPLICATION);
        } catch (TemplateModelException e) {
            throw new NotAvailableFreemarkerServletException("Exception got when obtaining the application hash model", e);
        }
    }

    /**
     * Returns the application context. It must be
     * first saved creating an {@link ApplicationContext} and using
     * {@link org.apache.tiles.request.ApplicationAccess#register(ApplicationContext)}.
     *
     * @param env The Freemarker environment.
     * @return The
     */
    public static ApplicationContext getApplicationContext(Environment env) {
        return ServletUtil.getApplicationContext(
            getServletContextHashModel(env).getServlet().getServletContext()
        );
    }

}
