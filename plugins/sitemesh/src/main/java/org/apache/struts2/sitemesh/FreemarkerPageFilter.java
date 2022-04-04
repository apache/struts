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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.sitemesh;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.views.freemarker.FreemarkerManager;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * Core Filter for integrating SiteMesh + Freemarker into
 * a Java web application.
 */
public class FreemarkerPageFilter extends SiteMeshFilter {

    /*
      * @see com.opensymphony.module.sitemesh.Factory.SITEMESH_FACTORY
      */
    private static final String SITEMESH_FACTORY = "sitemesh.factory";

    @Inject(required = false)
    public static void setFreemarkerManager(FreemarkerManager mgr) {
        OldDecorator2NewStrutsFreemarkerDecorator.setFreemarkerManager(mgr);
    }

    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        super.init(filterConfig);
        ServletContext sc = filterConfig.getServletContext();
        Factory instance = (Factory) sc.getAttribute(SITEMESH_FACTORY);
        if (instance == null) {
            sc.setAttribute(SITEMESH_FACTORY, new StrutsSiteMeshFactory(new Config(filterConfig)));
        }
    }

    protected DecoratorSelector initDecoratorSelector(SiteMeshWebAppContext webAppContext) {
        Factory factory = Factory.getInstance(new Config(filterConfig));
        factory.refresh();
        return new FreemarkerMapper2DecoratorSelector(factory.getDecoratorMapper());
    }
}
