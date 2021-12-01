/*
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
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Checkbox;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Checkbox
 */
public class CheckboxTag extends AbstractUITag {

    private static final long serialVersionUID = -350752809266337636L;

    protected String fieldValue;
    protected String submitUnchecked;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Checkbox(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();

        ((Checkbox) component).setFieldValue(fieldValue);
        ((Checkbox) component).setSubmitUnchecked(submitUnchecked);
    }

    public void setFieldValue(String aValue) {
        this.fieldValue = aValue;
    }

    public void setSubmitUnchecked(String aValue) {
        this.submitUnchecked = aValue;
    }

    /**
     * Must declare the setter at the descendant Tag class level in order for the tag handler to locate the method.
     */
    @Override
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (getPerformClearTagStateForTagPoolingServers() == false) {
            return;  // If flag is false (default setting), do not perform any state clearing.
        }
        super.clearTagStateForTagPoolingServers();
        this.fieldValue = null;
    }

}
