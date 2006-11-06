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
package org.apache.struts2.views;

import junit.framework.TestCase;

import org.apache.struts2.components.template.FreemarkerTemplateEngine;
import org.apache.struts2.components.template.JspTemplateEngine;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.components.template.VelocityTemplateEngine;
import org.apache.struts2.config.Settings;

/**
 * TemplateEngineManagerTest
 *
 */
public class TemplateEngineManagerTest extends TestCase {
    public void testTemplateTypeFromTemplateNameAndDefaults() {
        Settings.setInstance(new Settings() {
            public boolean isSetImpl(String name) {
                return name.equals(TemplateEngineManager.DEFAULT_TEMPLATE_TYPE_CONFIG_KEY);
            }

            public String getImpl(String aName) throws IllegalArgumentException {
                if (aName.equals(TemplateEngineManager.DEFAULT_TEMPLATE_TYPE_CONFIG_KEY)) {
                    return "jsp";
                }
                return null;
            }
        });
        TemplateEngine engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo"), null);
        assertTrue(engine instanceof JspTemplateEngine);
        engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo.vm"), null);
        assertTrue(engine instanceof VelocityTemplateEngine);
    }

    public void testTemplateTypeOverrides() {
        TemplateEngine engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo"), "ftl");
        assertTrue(engine instanceof FreemarkerTemplateEngine);
        engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo.vm"), "ftl");
        assertTrue(engine instanceof VelocityTemplateEngine);
        engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo.ftl"), "");
        assertTrue(engine instanceof FreemarkerTemplateEngine);
    }

    public void testTemplateTypeUsesDefaultWhenNotSetInConfiguration() {
        TemplateEngine engine = TemplateEngineManager.getTemplateEngine(new Template("/template", "simple", "foo"), null);
        Template template = new Template("/template", "simple", "foo." + TemplateEngineManager.DEFAULT_TEMPLATE_TYPE);
        TemplateEngine defaultTemplateEngine = TemplateEngineManager.getTemplateEngine(template, null);
        assertTrue(engine.getClass().equals(defaultTemplateEngine.getClass()));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        Settings.setInstance(null);
    }
}
