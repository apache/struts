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

package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.IteratorComponent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * @see IteratorComponent
 */
public class IteratorTag extends ContextBeanTag {

    private static final long serialVersionUID = -1827978135193581901L;

    protected String statusAttr;
    protected String value;
    protected String begin;
    protected String end;
    protected String step;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new IteratorComponent(stack);
    }

    protected void populateParams() {
        super.populateParams();

        IteratorComponent tag = (IteratorComponent) getComponent();
        tag.setStatus(statusAttr);
        tag.setValue(value);
        tag.setBegin(begin);
        tag.setEnd(end);
        tag.setStep(step);
    }

    public void setStatus(String status) {
        this.statusAttr = status;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public int doEndTag() throws JspException {
        component = null;
        return EVAL_PAGE;
    }

    public int doAfterBody() throws JspException {
        boolean again = component.end(pageContext.getOut(), getBody());

        if (again) {
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (Exception e) {
                    throw new JspException(e);
                }
            }
            return SKIP_BODY;
        }
    }

}
