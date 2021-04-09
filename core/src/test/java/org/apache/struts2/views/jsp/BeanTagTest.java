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
package org.apache.struts2.views.jsp;

import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.HttpParameters;

import javax.servlet.jsp.JspException;
import java.util.HashMap;
import java.util.Map;


/**
 */
public class BeanTagTest extends AbstractUITagTest {

    public void testSimple() {
        BeanTag tag = new BeanTag();
        tag.setPageContext(pageContext);
        tag.setName("org.apache.struts2.TestAction");

        try {
            tag.doStartTag();
            tag.component.addParameter("result", "success");

            assertEquals("success", stack.findValue("result"));
            // TestAction from bean tag, Action from execution and DefaultTextProvider
            assertEquals(3, stack.size());
            tag.doEndTag();
            assertEquals(2, stack.size());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        request.verify();
        pageContext.verify();
    }

    public void testAccepted() throws Exception {
        BeanTag tag = new BeanTag();
        tag.setPageContext(pageContext);
        tag.setName("org.apache.struts2.TestAction");

        Map<String, String> tmp = new HashMap<>();
        tmp.put("property", "array[0]");
        tmp.put("property2", "myTexts['key']");
        tmp.put("property3", "myTexts.key2");
        tmp.put("property4", "myUser.name");
        context.put("parameters", HttpParameters.create(tmp).build());
        ParamTag param1 = new ParamTag();
        param1.setPageContext(pageContext);
        param1.setName("%{#parameters['property']}");
        param1.setValue("20");
        ParamTag param2 = new ParamTag();
        param2.setPageContext(pageContext);
        param2.setName("%{#parameters['property2']}");
        param2.setValue("{'err20', 'err21'}");
        ParamTag param3 = new ParamTag();
        param3.setPageContext(pageContext);
        param3.setName("%{#parameters['property3']}");
        param3.setValue("{'err22', 'err23'}");
        ParamTag param4 = new ParamTag();
        param4.setPageContext(pageContext);
        param4.setName("%{#parameters['property4']}");
        param4.setValue("%{getStatus()}");

        tag.doStartTag();
        tag.component.addParameter("array", "instantiate array[0]");
        param1.doStartTag();
        param1.doEndTag();
        param2.doStartTag();
        param2.doEndTag();
        param3.doStartTag();
        param3.doEndTag();
        param4.doStartTag();
        param4.doEndTag();

        assertEquals("20", stack.findValue("array[0]"));
        assertEquals("[err20, err21]", stack.findValue("myTexts.key").toString());
        assertEquals("[err22, err23]", stack.findValue("myTexts.key2").toString());
        assertEquals("COMPLETED", stack.findValue("myUser.name"));

        tag.doEndTag();
    }

    public void testNotAccepted() throws Exception {
        BeanTag tag = new BeanTag();
        tag.setPageContext(pageContext);
        tag.setName("org.apache.struts2.TestAction");

        Map<String, String> tmp = new HashMap<>();
        tmp.put("property", "getResult()");
        context.put("parameters", HttpParameters.create(tmp).build());
        ParamTag param1 = new ParamTag();
        param1.setPageContext(pageContext);
        param1.setName("%{#parameters['property']}");
        param1.setValue("20");

        tag.doStartTag();
        param1.doStartTag();

        try {
            param1.doEndTag();
            fail("a not nested java identifiers is evaluated?!");
        } catch (StrutsException e) {
            assertEquals("Not valid or no name found for following expression: %{#parameters['property']}", e.getMessage());
        }

        tag.doEndTag();
    }
}
