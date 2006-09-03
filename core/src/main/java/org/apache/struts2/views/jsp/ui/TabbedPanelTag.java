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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Panel;
import org.apache.struts2.components.TabbedPanel;

import com.opensymphony.xwork2.util.OgnlValueStack;

/**
 * @see TabbedPanel
 */
public class TabbedPanelTag extends AbstractClosingTag {
	
	private static final long serialVersionUID = -4719930205515386252L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TabbedPanel(stack, req, res);
    }

    public List getTabs() {
        return ((TabbedPanel) component).getTabs();
    }

    public void addTab(Panel pane) {
        ((TabbedPanel) component).addTab(pane);
    }

}
