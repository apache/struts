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

package org.apache.struts2.components.template;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.StrutsConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The TemplateEngineManager will return a template engine for the template
 */
public class TemplateEngineManager {

    /** The default template extenstion is <code>ftl</code>. */
    public static final String DEFAULT_TEMPLATE_TYPE = "ftl";

    
    Map<String,EngineFactory> templateEngines = new HashMap<String,EngineFactory>();
    Container container;
    String defaultTemplateType;
    
    @Inject(StrutsConstants.DEFAULT_TEMPLATE_TYPE_CONFIG_KEY)
    public void setDefaultTemplateType(String type) {
        this.defaultTemplateType = type;
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
        Map<String,EngineFactory> map = new HashMap<String,EngineFactory>();
        Set<String> prefixes = container.getInstanceNames(TemplateEngine.class);
        for (String prefix : prefixes) {
            map.put(prefix, new LazyEngineFactory(prefix));
        }
        this.templateEngines = Collections.unmodifiableMap(map);
        
    }
    
    /**
     * Registers the given template engine.
     * <p/>
     * Will add the engine to the existing list of known engines.
     * @param templateExtension  filename extension (eg. .jsp, .ftl, .vm).
     * @param templateEngine     the engine.
     */
    public void registerTemplateEngine(String templateExtension, final TemplateEngine templateEngine) {
        templateEngines.put(templateExtension, new EngineFactory() {
            public TemplateEngine create() {
                return templateEngine;
            }
        });
    }

    /**
     * Gets the TemplateEngine for the template name. If the template name has an extension (for instance foo.jsp), then
     * this extension will be used to look up the appropriate TemplateEngine. If it does not have an extension, it will
     * look for a Configuration setting "struts.ui.templateSuffix" for the extension, and if that is not set, it
     * will fall back to "ftl" as the default.
     *
     * @param template               Template used to determine which TemplateEngine to return
     * @param templateTypeOverride Overrides the default template type
     * @return the engine.
     */
    public TemplateEngine getTemplateEngine(Template template, String templateTypeOverride) {
        String templateType = DEFAULT_TEMPLATE_TYPE;
        String templateName = template.toString();
        if (templateName.indexOf(".") > 0) {
            templateType = templateName.substring(templateName.indexOf(".") + 1);
        } else if (templateTypeOverride !=null && templateTypeOverride.length() > 0) {
            templateType = templateTypeOverride;
        } else {
            String type = defaultTemplateType;
            if (type != null) {
                templateType = type;
            }
        }
        return templateEngines.get(templateType).create();
    }

    /** Abstracts loading of the template engine */
    interface EngineFactory {
        public TemplateEngine create();
    }    

    /** 
     * Allows the template engine to be loaded at request time, so that engines that are missing
     * dependencies aren't accessed if never used.
     */
    class LazyEngineFactory implements EngineFactory {
        private String name;
        public LazyEngineFactory(String name) {
            this.name = name;
        }    
        public TemplateEngine create() {
            TemplateEngine engine = container.getInstance(TemplateEngine.class, name);
            if (engine == null) {
                throw new ConfigurationException("Unable to locate template engine: "+name);
            }
            return engine;
        }    
    }    
}
