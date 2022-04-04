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

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ActionMessage;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.ActionError;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * ActionMessage Tag.
 *
 */
public class ActionMessageTag extends AbstractUITag {

    private static final long serialVersionUID = 243396927554182506L;

    private boolean escape = true;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionMessage(stack, req, res);
    }

     protected void populateParams() {
        super.populateParams();

        ActionMessage message = (ActionMessage) component;
        message.setEscape(escape);
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}
