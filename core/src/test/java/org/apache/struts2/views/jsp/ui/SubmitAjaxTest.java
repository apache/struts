/*
 * $Id: SelectTest.java 418521 2006-07-01 23:36:50Z mrdon $
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

/**
 * Test Submit component in "ajax" theme.
 */
public class SubmitAjaxTest extends AbstractUITagTest {
	
	public void testSimple() throws Exception {
        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);

        tag.setId("mylink");
        tag.setValue("submit");
        tag.setTheme("ajax");
        tag.setResultDivId("formId");
        tag.setOnLoadJS("alert('form submitted');");
        tag.setListenTopics("a");
        tag.setNotifyTopics("b");

        tag.doStartTag();
        tag.doEndTag();

        verify(AnchorTest.class.getResource("submit-ajax-1.txt"));
    }
}
