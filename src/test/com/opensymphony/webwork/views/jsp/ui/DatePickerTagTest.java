/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.views.jsp.AbstractUITagTest;
import com.opensymphony.xwork.ActionContext;

import java.util.Locale;

/**
 * Unit test for {@link DatePickerTag}.
 *
 * @author Claus Ibsen
 */
public class DatePickerTagTest extends AbstractUITagTest {

    private DatePickerTag tag;

    public void testDefault() throws Exception {
        tag.doStartTag();
        tag.doEndTag();

        verify(DatePickerTagTest.class.getResource("DatePickerTagTest-1.txt"));
    }

    public void testLocaleInStack() throws Exception {
        stack.getContext().put(ActionContext.LOCALE, Locale.FRANCE);

        tag.setLanguage(null);
        tag.doStartTag();
        tag.doEndTag();

        verify(DatePickerTagTest.class.getResource("DatePickerTagTest-2.txt"));
    }

    public void testFormat() throws Exception {
        tag.setFormat("yyyy/MM/dd hh:mm:ss");

        tag.doStartTag();
        tag.doEndTag();
        assertTrue("Should contain format", writer.toString().indexOf("yyyy/MM/dd hh:mm:ss") > -1);
    }

    public void testLanguage() throws Exception {
        tag.setLanguage("da");

        tag.doStartTag();
        tag.doEndTag();
        assertTrue("Should contain danish language", writer.toString().indexOf("/webwork/jscalendar/lang/calendar-da.js") > -1);
    }

    public void testShowstime() throws Exception {
        tag.setShowstime("24");

        tag.doStartTag();
        tag.doEndTag();
        assertTrue("Should contain showsTime 24", writer.toString().indexOf("showsTime      :    \"24\"") > -1);
    }

    public void testSingleclick() throws Exception {
        tag.setSingleclick("true");

        tag.doStartTag();
        tag.doEndTag();
    }

    protected void setUp() throws Exception {
        super.setUp();
        tag = new DatePickerTag();
        tag.setPageContext(pageContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
