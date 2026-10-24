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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.action.Action;
import org.apache.struts2.components.ConstraintAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTextFieldTest extends AbstractUITagTest {

    private static final Date OCTOBER_24 = Date.from(LocalDate.of(2026, 10, 24).atStartOfDay(ZoneId.systemDefault()).toInstant());

    @Override
    public Action getAction() {
        return new ConstraintAction();
    }

    public void testGenericHtml5() throws Exception {
        DateTextFieldTag tag = new DateTextFieldTag();
        verifyGenericProperties(tag, "html5", new String[]{"value"});
    }

    public void testHtml5RendersTheDateAsIsoIgnoringFormat() throws Exception {
        ((ConstraintAction) action).setBirthday(OCTOBER_24);

        DateTextFieldTag tag = html5Tag();
        tag.setFormat("MM/dd/yyyy");
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTextFieldTag.class.getResource("DateTextField-1-html5.txt"));
    }

    public void testHtml5RendersSqlDateAsIso() throws Exception {
        ((ConstraintAction) action).setBirthday(java.sql.Date.valueOf(LocalDate.of(2026, 10, 24)));

        DateTextFieldTag tag = html5Tag();
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTextFieldTag.class.getResource("DateTextField-1-html5.txt"));
    }

    public void testHtml5RendersALocalDateAsIso() throws Exception {
        ((ConstraintAction) action).setDueDate(LocalDate.of(2026, 10, 24));

        DateTextFieldTag tag = html5Tag("dueDate");
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTextFieldTag.class.getResource("DateTextField-5-html5.txt"));
    }

    public void testHtml5RendersTheDatePartOfALocalDateTime() throws Exception {
        ((ConstraintAction) action).setMeetingAt(LocalDateTime.of(2026, 10, 24, 10, 15));

        DateTextFieldTag tag = html5Tag("meetingAt");
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTextFieldTag.class.getResource("DateTextField-6-html5.txt"));
    }

    public void testHtml5RendersNoValueWithoutADate() throws Exception {
        DateTextFieldTag tag = html5Tag();
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTextFieldTag.class.getResource("DateTextField-2-html5.txt"));
    }

    /**
     * After a conversion error the field re-renders the raw submitted string, which must come back unchanged.
     */
    public void testHtml5RendersAStringValueAsIs() throws Exception {
        DateTextFieldTag tag = html5Tag();
        tag.setValue("%{'2026-10-24'}");
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTextFieldTag.class.getResource("DateTextField-1-html5.txt"));
    }

    public void testHtml5RendersDynamicMinAndMax() throws Exception {
        DateTextFieldTag tag = html5Tag();
        tag.setDynamicAttribute(null, "min", "2026-01-01");
        tag.setDynamicAttribute(null, "max", "2026-12-31");
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTextFieldTag.class.getResource("DateTextField-3-html5.txt"));
    }

    public void testHtml5RendersCommonAttributes() throws Exception {
        ((ConstraintAction) action).setBirthday(OCTOBER_24);

        DateTextFieldTag tag = html5Tag();
        tag.setId("dob");
        tag.setCssClass("date");
        tag.setCssStyle("width: 10em");
        tag.setTitle("Date of birth");
        tag.setTabindex("3");
        tag.setDisabled("true");
        tag.setOnchange("check()");
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTextFieldTag.class.getResource("DateTextField-4-html5.txt"));
    }

    private DateTextFieldTag html5Tag() {
        return html5Tag("birthday");
    }

    private DateTextFieldTag html5Tag(String name) {
        DateTextFieldTag tag = new DateTextFieldTag();
        tag.setPageContext(pageContext);
        tag.setTheme("html5");
        tag.setName(name);
        return tag;
    }
}
