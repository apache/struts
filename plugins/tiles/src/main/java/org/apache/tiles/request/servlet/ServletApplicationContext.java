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
package org.apache.tiles.request.servlet;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.collection.ScopeMap;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.apache.tiles.request.servlet.extractor.ApplicationScopeExtractor;
import org.apache.tiles.request.servlet.extractor.InitParameterExtractor;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Servlet-based implementation of the TilesApplicationContext interface.
 */
public class ServletApplicationContext implements ApplicationContext {

    /**
     * The servlet context to use.
     */
    private final ServletContext servletContext;

    /**
     * <p>The lazily instantiated <code>Map</code> of application scope
     * attributes.</p>
     */
    private Map<String, Object> applicationScope = null;

    /**
     * <p>The lazily instantiated <code>Map</code> of context initialization
     * parameters.</p>
     */
    private Map<String, String> initParam = null;

    /**
     * Creates a new instance of ServletTilesApplicationContext.
     *
     * @param servletContext The servlet context to use.
     */
    public ServletApplicationContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /** {@inheritDoc} */
    public Object getContext() {
        return servletContext;
    }

    /** {@inheritDoc} */
    public Map<String, Object> getApplicationScope() {

        if ((applicationScope == null) && (servletContext != null)) {
            applicationScope = new ScopeMap(new ApplicationScopeExtractor(servletContext));
        }
        return (applicationScope);

    }

    /** {@inheritDoc} */
    public Map<String, String> getInitParams() {

        if ((initParam == null) && (servletContext != null)) {
            initParam = new ReadOnlyEnumerationMap<>(new InitParameterExtractor(servletContext));
        }
        return (initParam);

    }

    /** {@inheritDoc} */
    public ApplicationResource getResource(String localePath) {
        try {
            URL url = servletContext.getResource(localePath);
            if (url != null) {
                return new URLApplicationResource(localePath, url);
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    public ApplicationResource getResource(ApplicationResource base, Locale locale) {
        try {
            URL url = servletContext.getResource(base.getLocalePath(locale));
            if (url != null) {
                return new URLApplicationResource(base.getPath(), locale, url);
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    public Collection<ApplicationResource> getResources(String path) {
        ArrayList<ApplicationResource> resources = new ArrayList<>();
        resources.add(getResource(path));
        return resources;
    }
}
