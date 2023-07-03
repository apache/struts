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
package org.apache.struts2.tiles.request.servlet;

import org.apache.struts2.tiles.request.servlet.extractor.ApplicationScopeExtractor;
import org.apache.struts2.tiles.request.servlet.extractor.InitParameterExtractor;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.collection.ScopeMap;
import org.apache.tiles.request.locale.URLApplicationResource;

import jakarta.servlet.ServletContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public class ServletApplicationContext implements ApplicationContext {
    private ServletContext servletContext;
    private Map<String, Object> applicationScope = null;
    private Map<String, String> initParam = null;

    public ServletApplicationContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Object getContext() {
        return this.servletContext;
    }

    public Map<String, Object> getApplicationScope() {
        if (this.applicationScope == null && this.servletContext != null) {
            this.applicationScope = new ScopeMap(new ApplicationScopeExtractor(this.servletContext));
        }

        return this.applicationScope;
    }

    public Map<String, String> getInitParams() {
        if (this.initParam == null && this.servletContext != null) {
            this.initParam = new ReadOnlyEnumerationMap(new InitParameterExtractor(this.servletContext));
        }

        return this.initParam;
    }

    public ApplicationResource getResource(String localePath) {
        try {
            URL url = this.servletContext.getResource(localePath);
            return url != null ? new URLApplicationResource(localePath, url) : null;
        } catch (MalformedURLException var3) {
            return null;
        }
    }

    public ApplicationResource getResource(ApplicationResource base, Locale locale) {
        try {
            URL url = this.servletContext.getResource(base.getLocalePath(locale));
            return url != null ? new URLApplicationResource(base.getPath(), locale, url) : null;
        } catch (MalformedURLException var4) {
            return null;
        }
    }

    public Collection<ApplicationResource> getResources(String path) {
        ArrayList<ApplicationResource> resources = new ArrayList();
        resources.add(this.getResource(path));
        return resources;
    }
}
