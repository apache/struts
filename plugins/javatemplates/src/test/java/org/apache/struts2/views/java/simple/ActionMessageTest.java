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

import org.apache.struts2.components.ActionError;
import org.apache.struts2.components.UIBean;

import java.util.ArrayList;
import java.util.List;

public class ActionMessageTest extends AbstractTest {
    private ActionError tag;
    private List<String> errors;

    public void testRenderActionError() {
        tag.setCssClass("class");
        tag.setCssStyle("style");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<ul style='style' class='class'><li><span>this clas is bad</span></li><li><span>baaaaad</span></li></ul>");
        assertEquals(expected, output);
    }

    public void testRenderActionErrorWithoutCssClass() {
        tag.setCssStyle("style");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<ul style='style' class='actionMessage'><li><span>this clas is bad</span></li><li><span>baaaaad</span></li></ul>");
        assertEquals(expected, output);
    }

    public void testRenderActionErrorNoErrors() {
        this.errors.clear();
        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        assertEquals("", output);
    }

    @Override
    protected void setUp() throws Exception {
        this.errors = new ArrayList<String>();
        this.errors.add("this clas is bad");
        this.errors.add("baaaaad");

        //errors are needed to setup stack
        super.setUp();
        this.tag = new ActionError(stack, request, response);
    }

    @Override
    protected void setUpStack() {
        super.setUpStack();
        expectFind("actionMessages", this.errors);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "actionmessage";
    }
}

