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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.template.BaseTemplateEngine;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.views.java.simple.SimpleTheme;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Template engine that renders tags using java implementations
 */
public class JavaTemplateEngine extends BaseTemplateEngine {

    private static final Logger LOG = LoggerFactory.getLogger(JavaTemplateEngine.class);

    // The struts template engine manager
    protected TemplateEngineManager templateEngineManager;

    // The struts default template type. If struts ever changes this will need updating.
    private String defaultTemplateType = "ftl";

    @Inject
    public void setTemplateEngineManager(TemplateEngineManager mgr) {
        this.templateEngineManager = mgr;
    }

    private Themes themes = new Themes() {
        {
            add(new SimpleTheme());
        }
    };

    @Override
    protected String getSuffix() {
        return "java";
    }

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        Template t = templateContext.getTemplate();
        Theme theme = themes.get(t.getTheme());
        if (theme == null) {
            // Theme not supported, so do what struts would have done if we were not here.
            if (LOG.isDebugEnabled()) {
                LOG.debug("Theme not found [#0] trying default template engine using template type [#1]", t.getTheme(), defaultTemplateType);
            }
            final TemplateEngine engine = templateEngineManager.getTemplateEngine(templateContext.getTemplate(), defaultTemplateType);

            if (engine == null) {
                // May be the default template has changed?
                throw new ConfigurationException("Unable to find a TemplateEngine for template type '" + defaultTemplateType
                        + "' whilst trying to render template " + templateContext.getTemplate());
            } else {
                try {
                    // Retry render
                    engine.renderTemplate(templateContext);
                } catch (Exception e) {
                    // Give up and throw a new StrutsException(e);
                    throw new StrutsException("Cannot render tag [" + t.getName() + "] because theme ["
                            + t.getTheme() + "] was not found.", e);
                }
            }
        } else {
            // Render our template
            theme.renderTag(t.getName(), templateContext);
        }
    }

    private static class Themes {
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
                    LOG.info("Registering custom theme [#0] to javatemplates engine", themeClass);
                }
                ObjectFactory factory = ActionContext.getContext().getContainer().getInstance(ObjectFactory.class);
                Theme theme = (Theme) factory.buildBean(themeClass, new HashMap<String, Object>());
                themes.add(theme);
            } catch (ClassCastException cce) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Invalid java them class [#0]. Class does not implement 'org.apache.struts2.views.java.Theme' interface", cce, themeClass);
                }
            } catch (ClassNotFoundException cnf) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Invalid java theme class [#0]. Class not found!", cnf, themeClass);
                }
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Could not find messages file [#0].properties. Skipping!", e, themeClass);
                }
            }
        }
    }

    /**
     * Allows for providing an alternative default struts theme. Will default to "ftl" otherwise.
     *
     * @param defaultTemplateTheme the struts default theme
     */
    @Inject(value = "struts.javatemplates.defaultTemplateType", required = false)
    public void setDefaultTemplateType(String defaultTemplateTheme) {
        // Make sure we don't set ourself as default for race condition
        if (defaultTemplateTheme != null && !defaultTemplateTheme.equalsIgnoreCase(getSuffix())) {
            this.defaultTemplateType = defaultTemplateTheme.toLowerCase();
        } else if(LOG.isErrorEnabled()) {
            LOG.error("Invalid struts.javatemplates.defaultTemplateType value. Cannot be [#0]!", getSuffix());
        }
    }

}
