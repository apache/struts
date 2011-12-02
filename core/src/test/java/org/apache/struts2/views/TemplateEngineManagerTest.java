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

import java.util.HashSet;

import junit.framework.TestCase;

import org.apache.struts2.components.template.FreemarkerTemplateEngine;
import org.apache.struts2.components.template.JspTemplateEngine;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.components.template.VelocityTemplateEngine;
import org.apache.struts2.dispatcher.mapper.CompositeActionMapper;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.inject.Container;

/**
 * TemplateEngineManagerTest
 *
 */
public class TemplateEngineManagerTest extends TestCase {
    
    TemplateEngineManager mgr;
    Mock mockContainer;
    
    public void setUp() throws Exception {
        mgr = new TemplateEngineManager();
        mockContainer = new Mock(Container.class);
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(TemplateEngine.class), C.eq("jsp")), new JspTemplateEngine());
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(TemplateEngine.class), C.eq("vm")), new VelocityTemplateEngine());
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(TemplateEngine.class), C.eq("ftl")), new FreemarkerTemplateEngine());
        mockContainer.matchAndReturn("getInstanceNames", C.args(C.eq(TemplateEngine.class)), new HashSet() {{
            add("jsp");
            add("vm");
            add("ftl");
        }});
        
        mgr.setContainer((Container)mockContainer.proxy());
        mgr.setDefaultTemplateType("jsp");
    }
    
    public void testTemplateTypeFromTemplateNameAndDefaults() {
        
        TemplateEngine engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo"), null);
        assertTrue(engine instanceof JspTemplateEngine);
        engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo.vm"), null);
        assertTrue(engine instanceof VelocityTemplateEngine);
    }

    public void testTemplateTypeOverrides() {
        TemplateEngine engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo"), "ftl");
        assertTrue(engine instanceof FreemarkerTemplateEngine);
        engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo.vm"), "ftl");
        assertTrue(engine instanceof VelocityTemplateEngine);
        engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo.ftl"), "");
        assertTrue(engine instanceof FreemarkerTemplateEngine);
    }

    public void testTemplateTypeUsesDefaultWhenNotSetInConfiguration() {
        mgr.setDefaultTemplateType(null);
        TemplateEngine engine = mgr.getTemplateEngine(new Template("/template", "simple", "foo"), null);
        Template template = new Template("/template", "simple", "foo." + TemplateEngineManager.DEFAULT_TEMPLATE_TYPE);
        TemplateEngine defaultTemplateEngine = mgr.getTemplateEngine(template, null);
        assertTrue(engine.getClass().equals(defaultTemplateEngine.getClass()));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
