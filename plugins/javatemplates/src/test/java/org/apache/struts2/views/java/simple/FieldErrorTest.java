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

import org.apache.struts2.components.FieldError;
import org.apache.struts2.components.UIBean;

import java.util.*;

public class FieldErrorTest extends AbstractTest {
    private FieldError tag;
    private Map<String, List<String>> errors;
    private List<String> fieldNames;

    public void testRenderFieldError() {
        tag.setCssClass("class");
        tag.setCssStyle("style");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<ul style='style' class='class'><li><span>not good</span></li><li><span>bad</span></li><li><span>bad to the bone</span></li></ul>");
        assertEquals(expected, output);
    }

    public void testRenderFieldErrorWithoutCssClass() {
        tag.setCssStyle("style");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<ul style='style' class='errorMessage'><li><span>not good</span></li><li><span>bad</span></li><li><span>bad to the bone</span></li></ul>");
        assertEquals(expected, output);
    }

    public void testRenderFieldErrorWithoutFieldName() {
        this.fieldNames.clear();

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<ul class='errorMessage'><li><span>not good</span></li><li><span>bad</span></li><li><span>bad to the bone</span></li></ul>");
        assertEquals(expected, output);
    }

    public void testRenderFieldErrorWithoutOneFieldName() {
        tag.setFieldName("field1");
        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<ul class='errorMessage'><li><span>not good</span></li><li><span>bad</span></li></ul>");
        assertEquals(expected, output);
    }

    public void testRenderActionErrorNoErrors() {
        this.errors.clear();
        this.fieldNames.clear();

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        assertEquals("", output);
    }

    @Override
    protected void setUp() throws Exception {
        this.errors = new LinkedHashMap<String, List<String>>() {
            {
                put("field1", Arrays.asList("not good", "bad"));
                put("field2", Arrays.asList("bad to the bone"));
            }
        };
        this.fieldNames = new ArrayList<String>();
        this.fieldNames.add("field1");
        this.fieldNames.add("field2");

        //errors are needed to setup stack
        super.setUp();
        this.tag = new FieldError(stack, request, response);
    }

    @Override
    protected void setUpStack() {
        super.setUpStack();
        expectFind("fieldErrorFieldNames", this.fieldNames);
        expectFind("fieldErrors", this.errors);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "fielderror";
    }
}
