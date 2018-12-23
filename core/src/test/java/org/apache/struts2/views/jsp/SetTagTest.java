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

import com.mockobjects.servlet.MockJspWriter;
import java.io.IOException;
import javax.servlet.jsp.JspException;


/**
 */
public class SetTagTest extends AbstractUITagTest {

    Chewbacca chewie;
    SetTag tag;


    public void testApplicationScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.setScope("application");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("chewie", servletContext.getAttribute("foo"));
    }

    public void testPageScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.setScope("page");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("chewie", pageContext.getAttribute("foo"));
    }

    public void testRequestScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.setScope("request");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("chewie", request.getAttribute("foo"));
    }

    public void testSessionScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.setScope("session");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("chewie", session.get("foo"));
    }

    public void testStrutsScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("chewie", context.get("foo"));
    }

    public void testStrutsScope2() throws JspException {
        tag.setName("chewie");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(chewie, context.get("chewie"));
    }

    public void testSetTrimBody() throws JspException, IOException {
        final String beginEndSpaceString = "  Preceding and trailing spaces.  ";
        final String trimmedBeginEndSpaceString = beginEndSpaceString.trim();
        StrutsMockBodyContent mockBodyContent;

        tag.setName("foo");
        tag.setValue(null);
        // Do not set any value - default for tag should be true
        mockBodyContent = new StrutsMockBodyContent(new MockJspWriter());
        mockBodyContent.setString(beginEndSpaceString);
        tag.setBodyContent(mockBodyContent);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(trimmedBeginEndSpaceString, context.get("foo"));

        tag.setName("foo");
        tag.setValue(null);
        tag.setTrimBody(true);
        mockBodyContent = new StrutsMockBodyContent(new MockJspWriter());
        mockBodyContent.setString(beginEndSpaceString);
        tag.setBodyContent(mockBodyContent);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(trimmedBeginEndSpaceString, context.get("foo"));

        tag.setName("foo");
        tag.setValue(null);
        tag.setTrimBody(false);
        mockBodyContent = new StrutsMockBodyContent(new MockJspWriter());
        mockBodyContent.setString(beginEndSpaceString);
        tag.setBodyContent(mockBodyContent);
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(beginEndSpaceString, context.get("foo"));
    }

    protected void setUp() throws Exception {
        super.setUp();

        tag = new SetTag();
        chewie = new Chewbacca("chewie", true);
        stack.push(chewie);
        tag.setPageContext(pageContext);
    }


    public class Chewbacca {
        String name;
        boolean furry;

        public Chewbacca(String name, boolean furry) {
            this.name = name;
            this.furry = furry;
        }

        public void setFurry(boolean furry) {
            this.furry = furry;
        }

        public boolean isFurry() {
            return furry;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
