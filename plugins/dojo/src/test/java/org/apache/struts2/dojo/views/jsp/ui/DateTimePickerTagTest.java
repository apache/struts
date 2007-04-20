/*
 * $Id: DateTimePickerTagTest.java 508605 2007-02-16 21:56:50Z musachy $
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
package org.apache.struts2.dojo.views.jsp.ui;

/**
 */
public class DateTimePickerTagTest extends AbstractUITagTest {
    public void testSimple() throws Exception {
        DateTimePickerTag tag = new DateTimePickerTag();
        tag.setPageContext(pageContext);

        tag.setId("id");

        tag.setAdjustWeeks("true");
        tag.setDayWidth("b");
        tag.setDisplayWeeks("true");
        tag.setEndDate("d");
        tag.setStartDate("e");
        tag.setStaticDisplay("false");
        tag.setWeekStartsOn("g");
        tag.setName("h");
        tag.setLanguage("i");
        tag.setTemplateCssPath("j");
        tag.setValueNotifyTopics("k");
        tag.setValue("l");
        tag.doStartTag();
        tag.doEndTag();

        verify(DateTimePickerTagTest.class.getResource("DateTimePickerTagTest-1.txt"));
    }

}
