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

import java.io.StringWriter;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.components.Component;
import org.springframework.mock.web.MockJspWriter;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

import jakarta.servlet.jsp.JspException;


/**
 * PropertyTag test case.
 */
public class PropertyTagTest extends StrutsInternalTestCase {

    StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
    ValueStack stack;


    public void testDefaultValue() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);

        tag.setPageContext(pageContext);
        tag.setValue("title");
        tag.setDefault("TEST");

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
       assertEquals("TEST", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testDefaultValue_clearTagStateSet() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setValue("title");
        tag.setDefault("TEST");

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
        assertEquals("TEST", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testNull() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPageContext(pageContext);
        tag.setValue("title");

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
        assertEquals("", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testNull_clearTagStateSet() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setValue("title");

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals("", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();
        foo.setTitle("test");

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPageContext(pageContext);
        tag.setValue("title");

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
        assertEquals("test", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple_clearTagStateSet() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();
        foo.setTitle("test");

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setValue("title");

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
        assertEquals("test", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testTopOfStack() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();
        foo.setTitle("test");

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPageContext(pageContext);

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
        assertEquals("Foo is: test", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        // PropertyTag had no explicit values set in this test, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testTopOfStack_clearTagStateSet() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();
        foo.setTitle("test");

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
        assertEquals("Foo is: test", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testWithAltSyntax1() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {
            PropertyTag tag = new PropertyTag();
            tag.setPageContext(pageContext);
            tag.setValue("%{formatTitle()}");
            tag.doStartTag();
            tag.doEndTag();

            // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
            PropertyTag freshTag = new PropertyTag();
            freshTag.setPageContext(pageContext);
            assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                    "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                    objectsAreReflectionEqual(tag, freshTag));
        }

        // verify test
        
        assertEquals("Foo is: tm_jee", sw.toString());
        
    }

    public void testWithAltSyntax1_clearTagStateSet() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {
            PropertyTag tag = new PropertyTag();
            tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
            tag.setPageContext(pageContext);
            tag.setValue("%{formatTitle()}");
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
            tag.doEndTag();

            // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
            PropertyTag freshTag = new PropertyTag();
            freshTag.setPerformClearTagStateForTagPoolingServers(true);
            freshTag.setPageContext(pageContext);
            assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                    "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                    objectsAreReflectionEqual(tag, freshTag));
        }

        // verify test
        
        assertEquals("Foo is: tm_jee", sw.toString());
        
    }

    public void testEscapeJavaScript() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("\t\b\n\f\r\"'/\\");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setEscapeHtml(false);
        tag.setEscapeJavaScript(true);    
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: \\t\\b\\n\\f\\r\\\"\\'\\/\\\\", sw.toString());
        
    }

    public void testEscapeJavaScript_clearTagStateSet() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("\t\b\n\f\r\"\'/\\");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setEscapeHtml(false);
        tag.setEscapeJavaScript(true);
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: \\t\\b\\n\\f\\r\\\"\\'\\/\\\\", sw.toString());
        
    }

    public void testEscapeXml() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("<>'\"&");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setEscapeHtml(false);
        tag.setEscapeXml(true);
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: &lt;&gt;&apos;&quot;&amp;", sw.toString());
        
    }

     public void testEscapeXml_clearTagStateSet() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("<>'\"&");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setEscapeHtml(false);
        tag.setEscapeXml(true);
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: &lt;&gt;&apos;&quot;&amp;", sw.toString());
        
    }

    public void testEscapeCsv() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("\"something,\",\"");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);
        
        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setEscapeHtml(false);
        tag.setEscapeCsv(true);
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("\"Foo is: \"\"something,\"\",\"\"\"", sw.toString());
        
    }

    public void testEscapeCsv_clearTagStateSet() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("\"something,\",\"");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setEscapeHtml(false);
        tag.setEscapeCsv(true);
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("\"Foo is: \"\"something,\"\",\"\"\"", sw.toString());
        
    }

    public void testWithAltSyntax2() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPageContext(pageContext);
        tag.setValue("formatTitle()");
        tag.doStartTag();
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: tm_jee", sw.toString());
        
    }

    public void testWithAltSyntax2_clearTagStateSet() throws Exception {
        // setups
        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setValue("formatTitle()");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: tm_jee", sw.toString());
        
    }

    public void testWithoutAltSyntax1() throws Exception {
        //      setups
        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPageContext(pageContext);
        tag.setValue("formatTitle()");
        tag.doStartTag();
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: tm_jee", sw.toString());
        
    }

   public void testWithoutAltSyntax1_clearTagStateSet() throws Exception {
        //      setups
        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setValue("formatTitle()");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: tm_jee", sw.toString());
                
    }

    public void testWithoutAltSyntax2() throws Exception {
        //      setups
        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: tm_jee", sw.toString());
        
    }

    public void testWithoutAltSyntax2_clearTagStateSet() throws Exception {
        //      setups
        Foo foo = new Foo();
        foo.setTitle("tm_jee");
        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        // test
        {PropertyTag tag = new PropertyTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setValue("%{formatTitle()}");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));}

        // verify test
        
        assertEquals("Foo is: tm_jee", sw.toString());
        
    }

    public void testSimple_release() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();
        foo.setTitle("test");

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPageContext(pageContext);
        tag.setValue("title");

        try {
            tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
        assertEquals("test", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));

        // Test release at least once in unit tests (with clear tag state not set).
        tag.release();
        assertTrue("Tag state after release() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() and release() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple_release_clearTagStateSet() {
        PropertyTag tag = new PropertyTag();

        Foo foo = new Foo();
        foo.setTitle("test");

        stack.push(foo);

        StringWriter sw = new StringWriter();
        MockJspWriter jspWriter = new MockJspWriter(sw);

        StrutsMockPageContext pageContext = new StrutsMockPageContext(null, request, null);
        pageContext.setJspWriter(jspWriter);
        

        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setValue("title");

        try {
            tag.doStartTag();
            setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        
        assertEquals("test", sw.toString());
        

        try {
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PropertyTag freshTag = new PropertyTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));

        // Test release at least once in unit tests (with clear tag state set).
        tag.release();
        assertTrue("Tag state after release() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() and release() calls are not working properly.",
                objectsAreReflectionEqual(tag, freshTag));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
    }

    /**
     * Helper method to simplify setting the performClearTagStateForTagPoolingServers state for a 
     * {@link ComponentTagSupport} tag's {@link Component} to match expectations for the test.
     * 
     * The component reference is not available to the tag until after the doStartTag() method is called.
     * We need to ensure the component's {@link Component#performClearTagStateForTagPoolingServers} state matches
     * what we set for the Tag when a non-default (true) value is used, so this method accesses the component instance,
     * sets the value specified and forces the tag's parameters to be repopulated again.
     * 
     * @param tag The ComponentTagSupport tag upon whose component we will set the performClearTagStateForTagPoolingServers state.
     * @param performClearTagStateForTagPoolingServers true to clear tag state, false otherwise
     */
    protected void setComponentTagClearTagState(ComponentTagSupport tag, boolean performClearTagStateForTagPoolingServers) {
        tag.component.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
        //tag.populateParams();  // Not safe to call after doStartTag() ... breaks some tests.
        tag.populatePerformClearTagStateForTagPoolingServersParam();  // Only populate the performClearTagStateForTagPoolingServers parameter for the Tag.
    }

    public static class Foo {
        private String title;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String formatTitle() {
            return "Foo is: " + title;
        }

        @Override
        public String toString() {
            return formatTitle();
        }
    }
}
