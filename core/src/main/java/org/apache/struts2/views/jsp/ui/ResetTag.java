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
import org.apache.struts2.components.Reset;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see org.apache.struts2.components.Reset
 */
public class ResetTag extends AbstractUITag {
	
	private static final long serialVersionUID = 4742704832277392108L;
	
	protected String action;
    protected String method;
    protected String align;
    protected String type;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Reset(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Reset reset = ((Reset) component);
        reset.setAction(action);
        reset.setMethod(method);
        reset.setAlign(align);
        reset.setType(type);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setType(String type) {
        this.type = type;
    }

}
