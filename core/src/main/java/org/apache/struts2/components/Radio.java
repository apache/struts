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
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a radio button input field.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdescription -->
 * In this example, a radio control is displayed with a list of genders. The gender list is built from attribute
 * id=genders. The framework calls getGenders() which will return a Map. For examples using listKey and listValue attributes,
 * see the section select tag. The default selected one will be determined (in this case) by the getMale() method
 * in the action class which should retun a value similar to the key of the getGenters() map if that particular
 * gender is to be selected.<p/>
 * <!-- END SNIPPET: exdescription -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:action name="GenderMap" var="genders"/&gt;
 * &lt;s:radio label="Gender" name="male" list="#genders.genders"/&gt;
 * <!-- END SNIPPET: example -->
 *
 * <!-- START SNIPPET: example_fmt -->
 * &lt;@s.radio name="car" list={"ford": "Ford Motor Co", "toyota": "Toyota"} listKey="key" listValue="value" /&gt;
 * <!-- END SNIPPET: example_fmt -->
 *
 */
@StrutsTag(name="radio",
        tldTagClass="org.apache.struts2.views.jsp.ui.RadioTag",
        description="Renders a radio button input field",
        allowDynamicAttributes = true)
public class Radio extends ListUIBean {
    final public static String TEMPLATE = "radiomap";
    
    protected String readonly;

    public Radio(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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