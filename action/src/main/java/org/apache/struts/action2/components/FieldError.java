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
package org.apache.struts.action2.components;

import org.apache.struts.action2.components.Param.UnnamedParametric;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Render field errors if they exists. Specific layout depends on the particular theme.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 *    &lt;!-- example 1 --&gt;
 *    &lt;a:fielderror /&gt;
 *
 *    &lt;!-- example 2 --&gt;
 *    &lt;a:fielderror&gt;
 *         &lt;a:param&gt;field1&lt;/a:param&gt;
 *         &lt;a:param&gt;field2&lt;/a:param&gt;
 *    &lt;/a:fielderror&gt;
 *    &lt;a:form .... &gt;>
 *       ....
 *    &lt;/a:form&gt;
 *
 *    OR
 *
 *    &lt;a:fielderror&gt;
 *    		&lt;a:param value="%{'field1'}" /&gt;
 *    		&lt;a:param value="%{'field2'}" /&gt;
 *    &lt;/a:fielderror&gt;
 *    &lt;a:form .... &gt;>
 *       ....
 *    &lt;/a:form&gt;
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
 *
 * @author tm_jee
 * @version $Date$ $Id$
 * @a2.tag name="fielderror" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.FieldErrorTag"
 * description="Render field error (all or partial depending on param tag nested)if they exists"
 */
public class FieldError extends UIBean implements UnnamedParametric {

    private List errorFieldNames = new ArrayList();

    public FieldError(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    private static final String TEMPLATE = "fielderror";

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void addParameter(Object value) {
        if (value != null) {
            errorFieldNames.add(value.toString());
        }
    }

    public List getFieldErrorFieldNames() {
        return errorFieldNames;
    }
}

