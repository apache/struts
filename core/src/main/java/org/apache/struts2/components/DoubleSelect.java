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
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.annotations.StrutsTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders two HTML select elements with second one changing displayed values depending on selected entry of first one.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:doubleselect label="doubleselect test1" name="menu" list="{'fruit','other'}" doubleName="dishes" doubleList="top == 'fruit' ? {'apple', 'orange'} : {'monkey', 'chicken'}" /&gt;
 * &lt;s:doubleselect label="doubleselect test2" name="menu" list="#{'fruit':'Nice Fruits', 'other':'Other Dishes'}" doubleName="dishes" doubleList="top == 'fruit' ? {'apple', 'orange'} : {'monkey', 'chicken'}" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="doubleselect", tldTagClass="org.apache.struts2.views.jsp.ui.DoubleSelectTag", description="Renders two HTML select elements with second one changing displayed values depending on " +
                "selected entry of first one.")
public class DoubleSelect extends DoubleListUIBean {
    final public static String TEMPLATE = "doubleselect";


    public DoubleSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        StringBuilder onchangeParam = new StringBuilder();
        onchangeParam.append(getParameters().get("id")).append("Redirect(this.selectedIndex)");
        if(StringUtils.isNotEmpty(this.onchange)) {
        	onchangeParam.append(";").append(this.onchange);
        }
        // force the onchange parameter
        addParameter("onchange", onchangeParam.toString());
    }
}
