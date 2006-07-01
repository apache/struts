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
 * Render a radio button input field.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdescription -->
 * In this example, a radio control is displayed with a list of genders. The gender list is built from attribute
 * id=genders. WW calls getGenders() which will return a Map. For examples using listKey and listValue attributes,
 * see the section select tag. The default selected one will be determined (in this case) by the getMale() method 
 * in the action class which should retun a value similar to the key of the getGenters() map if that particular 
 * gender is to be selected.<p/>
 * <!-- END SNIPPET: exdescription -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:action name="GenderMap" id="genders"/&gt;
 * &lt;a:radio label="Gender" name="male" list="#genders.genders"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @a2.tag name="radio" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.RadioTag"
 * description="Renders a radio button input field"
 */
public class Radio extends ListUIBean {
    final public static String TEMPLATE = "radiomap";

    public Radio(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}
