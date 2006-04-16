/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.views.jsp.AbstractUITagTest;

/**
 * Unit test for {@link DebugTag}.
 *
 */
public class DebugTagTest extends AbstractUITagTest {

    private DebugTag tag;

    public void testDebug() throws Exception {
        tag.doStartTag();
        tag.doEndTag();

        assertNotNull(writer.toString());
        assertTrue("There should be at alot of debug data", writer.toString().length() > 5000);
    }

    public void testDebug2() throws Exception {
        MyPerson person = new MyPerson("Santa Claus", "rudolph");
        stack.push(person);
        tag.doStartTag();
        tag.doEndTag();

        assertNotNull(writer.toString());
        assertTrue("There should be at alot of debug data", writer.toString().length() > 5000);
        assertTrue("MyPerson should be in stack and in debug output", writer.toString().indexOf("MyPerson") > -1);
    }

    protected void setUp() throws Exception {
        super.setUp();
        tag = new DebugTag();
        tag.setPageContext(pageContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private class MyPerson {
        private String username;
        private String password;

        public MyPerson(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String toString() {
            return "MyPerson{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }


}
