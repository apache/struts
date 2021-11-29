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

import static com.opensymphony.xwork2.security.DefaultNotExcludedAcceptedPatternsCheckerTest.NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER;


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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        BeanTag freshTag = new BeanTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple_clearTagStateSet() {
        BeanTag tag = new BeanTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("org.apache.struts2.TestAction");

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        BeanTag freshTag = new BeanTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testNotAccepted() throws Exception {
        BeanTag tag = new BeanTag();
        tag.setPageContext(pageContext);
        tag.setName("org.apache.struts2.TestAction");

        Map<String, String> tmp = new HashMap<>();
        tmp.put("paramName", "getArray()[0]");
        context.put("parameters", HttpParameters.create(tmp).build());
        ParamTag param1 = new ParamTag();
        param1.setPageContext(pageContext);
        param1.setName("%{#parameters['paramName']}");
        param1.setValue("'success'");

        tag.doStartTag();
        param1.doStartTag();

        try {
            param1.doEndTag();
            fail("an excluded or not accepted is evaluated?!");
        } catch (StrutsException e) {
            assertEquals("Excluded or not accepted name found: getArray()[0]", e.getMessage());
            assertNull(stack.findValue("result"));
        }

        param1.component.setNotExcludedAcceptedPatterns(NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER);
        tag.component.addParameter("array", "just to instantiate array to avoid null for getArray()");

        param1.doEndTag();
        assertEquals("success", stack.findValue("array[0]"));

        tag.doEndTag();
    }

    public void testGetterAccepted() throws Exception {
        BeanTag tag = new BeanTag();
        tag.setPageContext(pageContext);
        tag.setName("org.apache.struts2.TestAction");

        ParamTag param1 = new ParamTag();
        param1.setPageContext(pageContext);
        param1.setName("getArray()[0]");
        param1.setValue("'success'");

        tag.doStartTag();
        param1.doStartTag();

        tag.component.addParameter("array", "just to instantiate array to avoid null for getArray()");

        param1.doEndTag();
        assertEquals("success", stack.findValue("array[0]"));

        tag.doEndTag();
    }

}
