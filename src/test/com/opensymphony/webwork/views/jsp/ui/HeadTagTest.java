/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.views.jsp.AbstractUITagTest;
import com.opensymphony.webwork.components.Head;

/**
 * Unit test for {@link HeadTag}.
 * <p/>
 * Note: If unit test fails with encoding difference check the src/test/webwork.properties
 * and adjust the .txt files accordingly
 *
 * @author Claus Ibsen
 */
public class HeadTagTest extends AbstractUITagTest {

    private HeadTag tag;

    public void testHead1() throws Exception {
        tag.doStartTag();
        tag.doEndTag();

        verify(HeadTagTest.class.getResource("HeadTagTest-1.txt"));
    }

    public void testHead1NoCalender() throws Exception {
        tag.doStartTag();
        tag.doEndTag();
        tag.setCalendarcss(null); // null = should use calendar-blue.css
        
        verify(HeadTagTest.class.getResource("HeadTagTest-1.txt"));
    }

    public void testHead2() throws Exception {
        tag.setTheme("ajax");
        tag.doStartTag();
        Head component = (Head) tag.getComponent();
        assertTrue(!component.isDebug());
        tag.doEndTag();

        verify(HeadTagTest.class.getResource("HeadTagTest-2.txt"));
        assertTrue("should have debug false", writer.toString().indexOf("isDebug: false") > -1);
    }

    public void testHead3() throws Exception {
        tag.setTheme("ajax");
        tag.setDebug("true");
        tag.doStartTag();
        Head component = (Head) tag.getComponent(); // must be done between start and end
        assertTrue(component.isDebug());
        tag.doEndTag();

        verify(HeadTagTest.class.getResource("HeadTagTest-3.txt"));
        assertTrue("should have debug true", writer.toString().indexOf("isDebug: true") > -1);
    }

    public void testHead4() throws Exception {
        tag.setCalendarcss("my-calendar");
        tag.doStartTag();
        tag.doEndTag();

        verify(HeadTagTest.class.getResource("HeadTagTest-4.txt"));
        assertEquals("my-calendar", tag.getCalendarcss());
    }

    public void testHead4b() throws Exception {
        tag.setCalendarcss("my-calendar.css");
        tag.doStartTag();
        Head component = (Head) tag.getComponent(); // must be done between start and end
        assertEquals("my-calendar.css", component.getCalendarcss());
        tag.doEndTag();

        verify(HeadTagTest.class.getResource("HeadTagTest-4.txt"));
        assertEquals("my-calendar.css", tag.getCalendarcss());
    }

    protected void setUp() throws Exception {
        super.setUp();
        tag = new HeadTag();
        tag.setPageContext(pageContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
