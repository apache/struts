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
package org.apache.struts2.views.java.simple;

import org.apache.struts2.components.DateTextField;
import org.apache.struts2.components.UIBean;

public class DateTextFieldTest extends AbstractCommonAttributesTest {

    private DateTextField tag;

    public void testRenderDateTextField() {
    	tag.setId("id");
        tag.setName("name");
        tag.setFormat("yyyy-MM-dd");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<div id='id'>" +
        		"<input type='text' class='date_year' size='4' maxlength='4' id='__year_id' name='__year_name'></input>" +
        		"-<input type='text' class='date_month' size='2' maxlength='2' id='__month_id' name='__month_name'></input>" +
        		"-<input type='text' class='date_day' size='2' maxlength='2' id='__day_id' name='__day_name'></input></div>");
        assertEquals(expected, output);
    }
    
    @Override
    public void testRenderTextFieldScriptingAttrs() throws Exception { }
    
    @Override
    public void testRenderTextFieldCommonAttrs() throws Exception { }

    @Override
    public void testRenderTextFieldDynamicAttrs() throws Exception { }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new DateTextField(stack, request, response);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "datetextfield";
    }

}
