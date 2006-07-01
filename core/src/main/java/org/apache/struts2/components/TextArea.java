/*
 * $Id$
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
package org.apache.struts2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render HTML textarea tag.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:textarea label="Comments" name="comments" cols="30" rows="8"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see TabbedPanel
 *
 * @a2.tag name="textarea" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.TextareaTag"
 * description="Render HTML textarea tag."
 */
public class TextArea extends UIBean {
    final public static String TEMPLATE = "textarea";

    protected String cols;
    protected String readonly;
    protected String rows;
    protected String wrap;

    public TextArea(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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

    /**
     * HTML cols attribute
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setCols(String cols) {
        this.cols = cols;
    }

    /**
     * Whether the textarea is readonly
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    /**
     * HTML rows attribute
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setRows(String rows) {
        this.rows = rows;
    }

    /**
     * HTML wrap attribute
     * @a2.tagattribute required="false" type="String"
     */
    public void setWrap(String wrap) {
        this.wrap = wrap;
    }
}
