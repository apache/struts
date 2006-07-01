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
 * Renders an HTML input element of type hidden, populated by the specified property from the OgnlValueStack.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;-- example one --&gt;
 * &lt;a:hidden name="foo" /&gt;
 * &lt;-- example two --&gt;
 * &lt;a:hidden name="foo" value="bar" /&gt;
 *
 * Example One Resulting HTML (if foo evaluates to bar):
 * &lt;input type="hidden" name="foo" value="bar" /&gt;
 * Example Two Resulting HTML (if getBar method of the action returns 'bar')
 * &lt;input type="hidden" name="foo" value="bar" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @a2.tag name="hidden" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.HiddenTag"
 * description="Render a hidden input field"
  */
public class Hidden extends UIBean {
    final public static String TEMPLATE = "hidden";

    public Hidden(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}
