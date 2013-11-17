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

package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Test case for StrutsUtil.
 *
 */
public class StrutsUtilTest extends StrutsInternalTestCase {

    protected ValueStack stack = null;
    protected InternalMockHttpServletRequest request = null;
    protected MockHttpServletResponse response = null;
    protected StrutsUtil strutsUtil = null;

    public void testBeanMethod() throws Exception {
        Object o = strutsUtil.bean("org.apache.struts2.TestAction");
        assertNotNull(o);
        assertTrue(o instanceof TestAction);
    }

    public void testIsTrueMethod() throws Exception {
        stack.push(new Object() {
            public String getMyString() {
                return "myString";
            }
            public boolean getMyBoolean(boolean bool) {
                return bool;
            }
        });
        assertTrue(strutsUtil.isTrue("myString == 'myString'"));
        assertFalse(strutsUtil.isTrue("myString == 'myOtherString'"));
        assertTrue(strutsUtil.isTrue("getMyBoolean(true)"));
        assertFalse(strutsUtil.isTrue("getMyBoolean(false)"));
    }

    public void testFindStringMethod() throws Exception {
        stack.push(new Object() {
            public String getMyString() {
                return "myString";
            }
            public boolean getMyBoolean(boolean bool) {
                return bool;
            }
        });

        assertEquals(strutsUtil.findString("myString"), "myString");
        assertNull(strutsUtil.findString("myOtherString"));
        assertEquals(strutsUtil.findString("getMyBoolean(true)"), "true");
    }

    public void testIncludeMethod() throws Exception {
        strutsUtil.include("/some/includedJspFile.jsp");

        // with include, this must have been created and should not be null
        assertNotNull(request.getDispatcher());
        // this must be true, indicating we actaully ask container to do an include
        assertTrue(request.getDispatcher().included);
    }


    public void testUrlEncodeMethod() throws Exception {
        assertEquals(
                strutsUtil.urlEncode("http://www.opensymphony.com/action2/index.jsp?param1=value1"),
                "http%3A%2F%2Fwww.opensymphony.com%2Faction2%2Findex.jsp%3Fparam1%3Dvalue1");
    }

    public void testBuildUrlMethod() throws Exception {
        request.setContextPath("/myContextPath");
        assertEquals(strutsUtil.buildUrl("/someUrl?param1=value1"), "/myContextPath/someUrl?param1=value1");
    }


    public void testFindValueMethod() throws Exception {
        stack.push(new Object() {
            public String getMyString() {
                return "myString";
            }
            public boolean getMyBoolean(boolean bool) {
                return bool;
            }
        });
        Object obj1 = strutsUtil.findValue("myString", "java.lang.String");
        Object obj2 = strutsUtil.findValue("getMyBoolean(true)", "java.lang.Boolean");

        assertNotNull(obj1);
        assertNotNull(obj2);
        assertTrue(obj1 instanceof String);
        assertTrue(obj2 instanceof Boolean);
        assertEquals(obj1, "myString");
        assertEquals(obj2, Boolean.TRUE);
    }



    public void testGetTextMethod() throws Exception {
        // this should be in xwork-messages.properties (included by default
        // by LocalizedTextUtil
        assertNotNull(strutsUtil.getText("xwork.error.action.execution"));
        assertEquals(strutsUtil.getText("xwork.error.action.execution"), "Error during Action invocation");
    }


    public void testGetContextMethod() throws Exception {
        request.setContextPath("/myContext");
        assertEquals(strutsUtil.getContext(), "/myContext");
    }


    public void testMakeSelectListMethod() throws Exception {
        String[] selectedList = new String[] { "Car", "Airplane", "Bus" };
        List list = new ArrayList();
        list.add("Lorry");
        list.add("Car");
        list.add("Helicopter");

        stack.getContext().put("mySelectedList", selectedList);
        stack.getContext().put("myList", list);

        List listMade = strutsUtil.makeSelectList("#mySelectedList", "#myList", null, null);

        assertEquals(listMade.size(), 3);
        assertEquals(((ListEntry)listMade.get(0)).getKey(), "Lorry");
        assertEquals(((ListEntry)listMade.get(0)).getValue(), "Lorry");
        assertEquals(((ListEntry)listMade.get(0)).getIsSelected(), false);
        assertEquals(((ListEntry)listMade.get(1)).getKey(), "Car");
        assertEquals(((ListEntry)listMade.get(1)).getValue(), "Car");
        assertEquals(((ListEntry)listMade.get(1)).getIsSelected(), true);
        assertEquals(((ListEntry)listMade.get(2)).getKey(), "Helicopter");
        assertEquals(((ListEntry)listMade.get(2)).getValue(), "Helicopter");
        assertEquals(((ListEntry)listMade.get(2)).getIsSelected(), false);
    }

    public void testToInt() throws Exception {
        assertEquals(strutsUtil.toInt(11l), 11);
    }


    public void testToLong() throws Exception {
        assertEquals(strutsUtil.toLong(11), 11l);
    }


    public void testToString() throws Exception {
        assertEquals(strutsUtil.toString(1), "1");
        assertEquals(strutsUtil.toString(11l), "11");
    }

    public void testTranslateVariables() throws Exception {
        stack.push(new Object() {
            public String getFoo() {
                return "bar";
            }
        });
        Object obj1 = strutsUtil.translateVariables("try: %{foo}");

        assertNotNull(obj1);
        assertTrue(obj1 instanceof String);
        assertEquals(obj1, "try: bar");
    }

    // === Junit Hook

    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        request = new InternalMockHttpServletRequest();
        response = new MockHttpServletResponse();
        strutsUtil = new StrutsUtil(stack, request, response);
    }

    protected void tearDown() throws Exception {
        stack = null;
        request = null;
        response = null;
        strutsUtil = null;
        super.tearDown();
    }



    // === internal class to assist in testing

    class InternalMockHttpServletRequest extends MockHttpServletRequest {
        InternalMockRequestDispatcher dispatcher = null;
        public RequestDispatcher getRequestDispatcher(String path) {
            dispatcher = new InternalMockRequestDispatcher(path);
            return dispatcher;
        }

        public InternalMockRequestDispatcher getDispatcher() {
            return dispatcher;
        }
    }

    class InternalMockRequestDispatcher extends MockRequestDispatcher {
        private String url;
        boolean included = false;
        public InternalMockRequestDispatcher(String url) {
            super(url);
            this.url = url;
        }
        public void include(ServletRequest servletRequest, ServletResponse servletResponse) {
            if (servletResponse instanceof MockHttpServletResponse) {
                ((MockHttpServletResponse) servletResponse).setIncludedUrl(this.url);
            }
            included = true;
        }
    }

}
