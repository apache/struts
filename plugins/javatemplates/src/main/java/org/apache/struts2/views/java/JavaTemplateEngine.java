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
package org.apache.struts2.views.java;

import org.apache.struts2.StrutsException;
import org.apache.struts2.components.template.BaseTemplateEngine;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.views.java.simple.SimpleTheme;

import java.util.HashMap;
import java.util.StringTokenizer;

import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Template engine that renders tags using java implementations
 */
public class JavaTemplateEngine extends BaseTemplateEngine {

    private static final Logger LOG = LoggerFactory.getLogger(JavaTemplateEngine.class);

    private Themes themes = new Themes() {{
        add(new SimpleTheme());
    }};

    @Override
    protected String getSuffix() {
        return "java";
    }

    public void renderTemplate(TemplateRenderingContext templateContext)
            throws Exception {
        Template t = templateContext.getTemplate();
        Theme theme = themes.get(t.getTheme());
        if (theme == null) {
            throw new StrutsException("Cannot render tag [" + t.getName() + "] because theme [" + t.getTheme() + "] was not found.");
        }
        theme.renderTag(t.getName(), templateContext);
    }

    private class Themes {
        private HashMap<String, Theme> themes = new HashMap<String, Theme>();

        public void add(Theme theme) {
            themes.put(theme.getName(), theme);
        }

        public Theme get(String name) {
            return themes.get(name);
        }
    }

    /**
     * Allows for providing custom theme classes (implementations of the org.apache.struts2.views.java.Theme) interface
     * for custom rendering of tags using the javatemplates engine
     *
     * @param themeClasses a comma delimited list of custom theme class names
     */
    @Inject(value = "struts.javatemplates.customThemes", required = false)
    public void setThemeClasses(String themeClasses) {

        StringTokenizer customThemes = new StringTokenizer(themeClasses, ",");

        while (customThemes.hasMoreTokens()) {
            String themeClass = customThemes.nextToken().trim();
            try {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Registering custom theme '" + themeClass + "' to javatemplates engine");
                }

                //FIXME: This means Themes must have no-arg constructor - should use object factory here
                //ObjectFactory.getObjectFactory().buildBean(ClassLoaderUtil.loadClass(themeClass, getClass()), null);
                themes.add((Theme) ClassLoaderUtil.loadClass(themeClass, getClass()).newInstance());

            } catch (ClassCastException cce) {
                LOG.error("Invalid java them class '" + themeClass + "'. Class does not implement 'org.apache.struts2.views.java.Theme' interface");
            } catch (ClassNotFoundException cnf) {
                LOG.error("Invalid java theme class '" + themeClass + "'. Class not found");
            } catch (Exception e) {
                LOG.error("Could not find messages file " + themeClass + ".properties. Skipping");
            }
        }
    }
}
