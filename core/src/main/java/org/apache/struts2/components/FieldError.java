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

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Param.UnnamedParametric;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Render field errors if they exists. Specific layout depends on the particular theme.
 * The field error strings will be html escaped by default.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 *    &lt;!-- example 1 --&gt;
 *    &lt;s:fielderror /&gt;
 *
 *    &lt;!-- example 2 --&gt;
 *    &lt;s:fielderror&gt;
 *         &lt;s:param&gt;field1&lt;/s:param&gt;
 *         &lt;s:param&gt;field2&lt;/s:param&gt;
 *    &lt;/s:fielderror&gt;
 *    &lt;s:form .... &gt;
 *       ....
 *    &lt;/s:form&gt;
 *
 *    OR
 *
 *    &lt;s:fielderror&gt;
 *          &lt;s:param value="%{'field1'}" /&gt;
 *          &lt;s:param value="%{'field2'}" /&gt;
 *    &lt;/s:fielderror&gt;
 *    &lt;s:form .... &gt;
 *       ....
 *    &lt;/s:form&gt;
 *
 *    OR
 *
 *    &lt;s:fielderror fieldName="field1" /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 *
 * <p/> <b>Description</b><p/>
 *
 *
 * <pre>
 * <!-- START SNIPPET: description -->
 *
 * Example 1: display all field errors<p/>
 * Example 2: display field errors only for 'field1' and 'field2'<p/>
 *
 * <!-- END SNIPPET: description -->
 * </pre>
 *
 */
@StrutsTag(name="fielderror", tldTagClass="org.apache.struts2.views.jsp.ui.FieldErrorTag", description="Render field error (all " +
                "or partial depending on param tag nested)if they exists")
public class FieldError extends UIBean implements UnnamedParametric {

    private List<String> errorFieldNames = new ArrayList<String>();
    private boolean escape = true;

    public FieldError(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    private static final String TEMPLATE = "fielderror";

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (errorFieldNames != null)
            addParameter("errorFieldNames", errorFieldNames);

        addParameter("escape", escape);
    }

    public void addParameter(Object value) {
        if (value != null) {
            errorFieldNames.add(value.toString());
        }
    }

    public List<String> getFieldErrorFieldNames() {
        return errorFieldNames;
    }

    @StrutsTagAttribute(description="Field name for single field attribute usage", type="String")
    public void setFieldName(String fieldName) {
        addParameter(fieldName);
    }

    @StrutsTagAttribute(description=" Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}

