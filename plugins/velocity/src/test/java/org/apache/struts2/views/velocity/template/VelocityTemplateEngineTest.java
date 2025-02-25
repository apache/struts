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
package org.apache.struts2.views.velocity.template;

import org.apache.struts2.components.template.FreemarkerTemplateEngine;
import org.apache.struts2.components.template.JspTemplateEngine;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.inject.Container;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VelocityTemplateEngineTest {

    private TemplateEngineManager mgr;

    @Before
    public void setUp() throws Exception {
        mgr = new TemplateEngineManager();

        var mockContainer = mock(Container.class);
        when(mockContainer.getInstance(TemplateEngine.class, "jsp")).thenReturn(new JspTemplateEngine());
        when(mockContainer.getInstance(TemplateEngine.class, "vm")).thenReturn(new VelocityTemplateEngine());
        when(mockContainer.getInstance(TemplateEngine.class, "ftl")).thenReturn(new FreemarkerTemplateEngine());
        when(mockContainer.getInstanceNames(TemplateEngine.class)).thenReturn(Set.of("jsp", "vm", "ftl"));

        mgr.setContainer(mockContainer);
        mgr.setDefaultTemplateType("jsp");
    }

    @Test
    public void templateTypeFromTemplateNameAndDefaults() {

        TemplateEngine engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo"), null);
        assertTrue(engine instanceof JspTemplateEngine);
        engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo.vm"), null);
        assertTrue(engine instanceof VelocityTemplateEngine);
    }

    @Test
    public void templateTypeOverrides() {
        TemplateEngine engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo"), "ftl");
        assertTrue(engine instanceof FreemarkerTemplateEngine);
        engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo.vm"), "ftl");
        assertTrue(engine instanceof VelocityTemplateEngine);
        engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo.ftl"), "");
        assertTrue(engine instanceof FreemarkerTemplateEngine);
    }

    @Test
    public void templateTypeUsesDefaultWhenNotSetInConfiguration() {
        mgr.setDefaultTemplateType(null);
        TemplateEngine engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo"), null);
        Template template = new Template("/template", "simple", "foo." + TemplateEngineManager.DEFAULT_TEMPLATE_TYPE);
        TemplateEngine defaultTemplateEngine = mgr.getTemplateEngine(template, null);
        assertEquals(engine.getClass(), defaultTemplateEngine.getClass());
    }

}
