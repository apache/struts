/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.views.jsp.AbstractUITagTest;

/**
 * Unit test for {@link DebugTag}.
 *
 * @author Claus Ibsen
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
