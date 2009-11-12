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

package org.apache.struts2.osgi;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.struts2.osgi.loaders.FreeMarkerBundleResourceLoader;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.StrutsClassTemplateLoader;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;

/**
 * This class extends FreemarkerManager in core to add a template loader
 * (that finds resources inside bundles) to MultiTemplateLoader
 */
public class BundleFreemarkerManager extends FreemarkerManager {
    private static final Logger LOG = LoggerFactory.getLogger(BundleFreemarkerManager.class);

    protected TemplateLoader getTemplateLoader(ServletContext servletContext) {
        // construct a FileTemplateLoader for the init-param 'TemplatePath'
        FileTemplateLoader templatePathLoader = null;

        String templatePath = servletContext.getInitParameter("TemplatePath");
        if (templatePath == null) {
            templatePath = servletContext.getInitParameter("templatePath");
        }

        if (templatePath != null) {
            try {
                templatePathLoader = new FileTemplateLoader(new File(templatePath));
            } catch (IOException e) {
                if (LOG.isErrorEnabled())
                    LOG.error("Invalid template path specified: [#0]", e, e.getMessage());
            }
        }

        // presume that most apps will require the class and webapp template loader
        // if people wish to
        return templatePathLoader != null ?
                new MultiTemplateLoader(new TemplateLoader[]{
                        templatePathLoader,
                        new WebappTemplateLoader(servletContext),
                        new StrutsClassTemplateLoader(),
                        new FreeMarkerBundleResourceLoader()
                })
                : new MultiTemplateLoader(new TemplateLoader[]{
                new WebappTemplateLoader(servletContext),
                new StrutsClassTemplateLoader(),
                new FreeMarkerBundleResourceLoader()
        });
    }

}
