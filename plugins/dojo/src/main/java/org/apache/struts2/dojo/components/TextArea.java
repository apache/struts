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
package org.apache.struts2.dojo.components;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Render Dojo Editor2 widget
 *
 */
@StrutsTag(name="textarea", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.TextareaTag", description="Renders Dojo Editor2 widget")
public class TextArea extends org.apache.struts2.components.TextArea {

    public TextArea(ValueStack stack, HttpServletRequest request,
        HttpServletResponse response) {
        super(stack, request, response);
    }
    
    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        boolean generateId = !(Boolean)stack.getContext().get(Head.PARSE_CONTENT);
        addParameter("pushId", generateId);
        if ((this.id == null || this.id.length() == 0) && generateId) {
            Random random = new Random();
            this.id = "widget_" + Math.abs(random.nextInt());
            addParameter("id", this.id);
        }
    }

    @Override
    public String getTheme() {
        return "ajax";
    }
}
