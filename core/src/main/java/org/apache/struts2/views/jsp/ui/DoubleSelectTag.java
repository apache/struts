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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.DoubleSelect;
import com.opensymphony.xwork2.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see DoubleSelect
 */
public class DoubleSelectTag extends AbstractDoubleListTag {
    
	private static final long serialVersionUID = 7426011596359509386L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DoubleSelect(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        DoubleSelect doubleSelect = ((DoubleSelect) component);
        doubleSelect.setEmptyOption(emptyOption);
        doubleSelect.setHeaderKey(headerKey);
        doubleSelect.setHeaderValue(headerValue);
        doubleSelect.setMultiple(multiple);
        doubleSelect.setSize(size);
        
    }
}
