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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.request.freemarker.servlet;

import freemarker.cache.TemplateLoader;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.Configuration;
import org.apache.tiles.request.reflect.ClassUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extends FreemarkerServlet to load Tiles directives as a shared variable.
 */
public class SharedVariableLoaderFreemarkerServlet extends FreemarkerServlet {

    /**
     * The init parameter under which the factories will be put. The value of the parameter
     * must be a semicolon (;) separated list of couples, each member of the couple must
     * be separated by commas (,).
     */
    public static final String CUSTOM_SHARED_VARIABLE_FACTORIES_INIT_PARAM =
        "org.apache.tiles.request.freemarker.CUSTOM_SHARED_VARIABLE_FACTORIES";

    /**
     * Maps a name of a shared variable to its factory.
     */
    private final Map<String, SharedVariableFactory> name2variableFactory = new LinkedHashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        String param = config.getInitParameter(CUSTOM_SHARED_VARIABLE_FACTORIES_INIT_PARAM);
        if (param != null) {
            String[] couples = param.split("\\s*;\\s*");
            for (String s : couples) {
                String[] couple = s.split("\\s*,\\s*");
                if (couple.length != 2) {
                    throw new ServletException("Cannot parse custom shared variable partial init param: '" + s + "'");
                }
                name2variableFactory.put(couple[0], (SharedVariableFactory) ClassUtil.instantiate(couple[1]));
            }
        }
        super.init(new ExcludingParameterServletConfig(config));
    }

    /**
     * Adds anew shared variable factory in a manual way.
     *
     * @param variableName The name of the shared variable.
     * @param factory The shared variable factory.
     */
    public void addSharedVariableFactory(String variableName, SharedVariableFactory factory) {
        name2variableFactory.put(variableName, factory);
    }

    /** {@inheritDoc} */
    @Override
    protected Configuration createConfiguration() {
        Configuration configuration = super.createConfiguration();

        for (Map.Entry<String, SharedVariableFactory> entry : name2variableFactory.entrySet()) {
            configuration.setSharedVariable(entry.getKey(), entry.getValue().create());
        }
        return configuration;
    }

    /** {@inheritDoc} */

    @Override
    protected TemplateLoader createTemplateLoader(String templatePath) {
        return new WebappClassTemplateLoader(getServletContext());
    }

    /**
     * Servlet configuration that excludes some parameters. It is useful to adapt to
     * FreemarkerServlet behaviour, because it gets angry if it sees some extra
     * parameters that it does not recognize.
     */
    private static class ExcludingParameterServletConfig implements ServletConfig {

        /**
         * The servlet configuration.
         */
        private final ServletConfig config;

        /**
         * Constructor.
         *
         * @param config The servlet configuration.
         */
        public ExcludingParameterServletConfig(ServletConfig config) {
            this.config = config;
        }

        @Override
        public String getServletName() {
            return config.getServletName();
        }

        @Override
        public ServletContext getServletContext() {
            return config.getServletContext();
        }

        @Override
        public String getInitParameter(String name) {
            if (CUSTOM_SHARED_VARIABLE_FACTORIES_INIT_PARAM.equals(name)) {
                return null;
            }
            return config.getInitParameter(name);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Enumeration getInitParameterNames() {
            return new SkippingEnumeration(config.getInitParameterNames());
        }

    }

    /**
     * An enumeration that skip just
     * {@link SharedVariableLoaderFreemarkerServlet#CUSTOM_SHARED_VARIABLE_FACTORIES_INIT_PARAM},
     * again not to let the FreemarkerServlet be angry about it.
     */
    private static class SkippingEnumeration implements Enumeration<String> {

        /**
         * The original enumeration.
         */
        private final Enumeration<String> enumeration;

        /**
         * The next element.
         */
        private String next = null;

        /**
         * Constructor.
         *
         * @param enumeration The original enumeration.
         */
        public SkippingEnumeration(Enumeration<String> enumeration) {
            this.enumeration = enumeration;
            updateNextElement();
        }

        @Override
        public boolean hasMoreElements() {
            return next != null;
        }

        @Override
        public String nextElement() {
            String retValue = next;
            updateNextElement();
            return retValue;
        }

        /**
         * Updates the next element that will be passed.
         */
        private void updateNextElement() {
            String value = null;
            boolean done = false;
            while (this.enumeration.hasMoreElements() && !done) {
                value = this.enumeration.nextElement();
                if (value.equals(CUSTOM_SHARED_VARIABLE_FACTORIES_INIT_PARAM)) {
                    value = null;
                } else {
                    done = true;
                }
            }
            next = value;
        }

    }
}
