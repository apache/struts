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
package org.apache.struts2.components;

import org.apache.struts2.ActionContext;
import org.apache.struts2.dispatcher.DispatcherConstants;
import org.apache.struts2.views.jsp.AbstractTagTest;

import java.util.Map;

import static org.apache.struts2.components.UIBean.TEMPLATE_DIR;
import static org.apache.struts2.components.UIBean.THEME;

public class UIBeanTagTest extends AbstractTagTest {

    private UIBean bean;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        bean = new UIBean(stack, request, response) {
            @Override
            protected String getDefaultTemplate() {
                return null;
            }
        };
    }

    public void testTemplateDir_ognlExpression() {
        bean.setTemplateDir("%{testDir}");
        stack.push(new Object() {
            public String getTestDir() {
                return "testValue";
            }
        });

        assertEquals("testValue", bean.getTemplateDir());
    }

    public void testTemplateDir_attrMapFallback() {
        ActionContext.of(context).getApplication().put(TEMPLATE_DIR, "applicationValue");
        assertEquals("applicationValue", bean.getTemplateDir());

        ActionContext.of(context).getSession().put(TEMPLATE_DIR, "sessionValue");
        assertEquals("sessionValue", bean.getTemplateDir());

        ((Map<String, Object>) context.get(DispatcherConstants.REQUEST)).put(TEMPLATE_DIR, "requestValue");
        assertEquals("requestValue", bean.getTemplateDir());
    }

    public void testTheme_ognlExpression() {
        bean.setTheme("%{testTheme}");
        stack.push(new Object() {
            public String getTestTheme() {
                return "testValue";
            }
        });

        assertEquals("testValue", bean.getTheme());
    }

    public void testTheme_attrMapFallback() {
        ActionContext.of(context).getApplication().put(THEME, "applicationValue");
        assertEquals("applicationValue", bean.getTheme());

        ActionContext.of(context).getSession().put(THEME, "sessionValue");
        assertEquals("sessionValue", bean.getTheme());

        ((Map<String, Object>) context.get(DispatcherConstants.REQUEST)).put(THEME, "requestValue");
        assertEquals("requestValue", bean.getTheme());
    }
}
