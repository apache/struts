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

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Creates a series of checkboxes from a list. Setup is like &lt;s:select /&gt; or &lt;s:radio /&gt;, but creates checkbox tags.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:checkboxlist name="foo" list="bar"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(
        name="checkboxlist",
        tldTagClass="org.apache.struts2.views.jsp.ui.CheckboxListTag",
        description="Render a list of checkboxes",
        allowDynamicAttributes = true)
public class CheckboxList extends ListUIBean {
    final public static String TEMPLATE = "checkboxlist";
    
    protected String readonly;

    public CheckboxList(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
    
    public void evaluateExtraParams() {
    	super.evaluateExtraParams();
        
        if (readonly != null) {
            addParameter("readonly", findValue(readonly, Boolean.class));
        }
    }
    
    @StrutsTagAttribute(description="Whether the input is readonly", type="Boolean", defaultValue="false")
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

}