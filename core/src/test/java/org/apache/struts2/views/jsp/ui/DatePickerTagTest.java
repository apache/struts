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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.views.jsp.AbstractUITagTest;
import com.opensymphony.xwork.ActionContext;

import java.util.Locale;

/**
 * Unit test for {@link DatePickerTag}.
 *
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
        tag.setFormat("#yyyy/#MM/#dd #hh:#mm:#ss");

        tag.doStartTag();
        tag.doEndTag();
        assertTrue("Should contain format", writer.toString().indexOf("#yyyy/#MM/#dd #hh:#mm:#ss") > -1);
    }

    // NOTE: Switching to Dojo's DatePicker, i18n is not supported, 
    //       Commenting out this test case
    /*public void testLanguage() throws Exception {
        tag.setLanguage("da");

        tag.doStartTag();
        tag.doEndTag();
        assertTrue("Should contain danish language", writer.toString().indexOf("/struts/jscalendar/lang/calendar-da.js") > -1);
    }*/

    
    // NOTE: Switching to Dojo's DatePicker, showing time is not supported.
    //       Commenting this test case
    /*public void testShowstime() throws Exception {
        tag.setShowstime("24");

        tag.doStartTag();
        tag.doEndTag();
        assertTrue("Should contain showsTime 24", writer.toString().indexOf("showsTime      :    \"24\"") > -1);
    }*/

    // NOTE: Switching to Dojo's DatePickuer, single / double click to popup
    //       is not supported. Commenting out this test case.
    /*public void testSingleclick() throws Exception {
        tag.setSingleclick("true");

        tag.doStartTag();
        tag.doEndTag();
    }*/

    protected void setUp() throws Exception {
        super.setUp();
        tag = new DatePickerTag();
        tag.setPageContext(pageContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
