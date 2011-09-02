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
 * Render HTML textarea tag.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:textarea label="Comments" name="comments" cols="30" rows="8"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see TabbedPanel
 *
 */
@StrutsTag(
    name="textarea",
    tldTagClass="org.apache.struts2.views.jsp.ui.TextareaTag",
    description="Render HTML textarea tag.",
    allowDynamicAttributes=true)
public class TextArea extends UIBean {
    final public static String TEMPLATE = "textarea";

    protected String cols;
    protected String readonly;
    protected String rows;
    protected String wrap;

    public TextArea(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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

        if (cols != null) {
            addParameter("cols", findString(cols));
        }

        if (rows != null) {
            addParameter("rows", findString(rows));
        }

        if (wrap != null) {
            addParameter("wrap", findString(wrap));
        }
    }

    @StrutsTagAttribute(description="HTML cols attribute", type="Integer")
    public void setCols(String cols) {
        this.cols = cols;
    }

    @StrutsTagAttribute(description="Whether the textarea is readonly", type="Boolean", defaultValue="false")
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    @StrutsTagAttribute(description="HTML rows attribute", type="Integer")
    public void setRows(String rows) {
        this.rows = rows;
    }

    @StrutsTagAttribute(description="HTML wrap attribute")
    public void setWrap(String wrap) {
        this.wrap = wrap;
    }
}
