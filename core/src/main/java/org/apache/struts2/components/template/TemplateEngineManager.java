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

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

/**
 * The TemplateEngineManager will return a template engine for the template
 */
public class TemplateEngineManager {
    public static final String DEFAULT_TEMPLATE_TYPE_CONFIG_KEY = "struts.ui.templateSuffix";

    /** The default template extenstion is <code>ftl</code>. */
    public static final String DEFAULT_TEMPLATE_TYPE = "ftl";

    Map templateEngines = new HashMap();
    Container container;
    String defaultTemplateType;
    
    @Inject(DEFAULT_TEMPLATE_TYPE_CONFIG_KEY)
    public void setDefaultTemplateType(String type) {
        this.defaultTemplateType = type;
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
    
    @Inject(StrutsConstants.STRUTS_TEMPLATE_ENGINES)
    public void setTemplateEngines(String engines) {
        if (engines != null) {
            String[] list = engines.split(",");
            for (String name : list) {
                TemplateEngine eng = container.getInstance(TemplateEngine.class, name);
                if (eng != null) {
                    templateEngines.put(name, eng);
                } else {
                    throw new IllegalArgumentException("Invalid template engine name: "+name);
                }
            }
        }
    }
    
    /**
     * Registers the given template engine.
     * <p/>
     * Will add the engine to the existing list of known engines.
     * @param templateExtension  filename extension (eg. .jsp, .ftl, .vm).
     * @param templateEngine     the engine.
     */
    public void registerTemplateEngine(String templateExtension, TemplateEngine templateEngine) {
        templateEngines.put(templateExtension, templateEngine);
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
        return (TemplateEngine) templateEngines.get(templateType);
    }


}
