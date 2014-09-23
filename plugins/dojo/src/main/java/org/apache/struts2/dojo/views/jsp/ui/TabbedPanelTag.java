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

package org.apache.struts2.dojo.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.dojo.components.TabbedPanel;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see TabbedPanel
 */
public class TabbedPanelTag extends AbstractClosingTag {

    private static final long serialVersionUID = -4719930205515386252L;

    private String selectedTab;
    private String closeButton;
    private String doLayout;
    private String templateCssPath;
    private String beforeSelectTabNotifyTopics;
    private String afterSelectTabNotifyTopics;
    private String disabledTabCssClass;
    private String useSelectedTabCookie;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TabbedPanel(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();
        TabbedPanel tabbedPanel = (TabbedPanel) component;
        tabbedPanel.setSelectedTab(selectedTab);
        tabbedPanel.setCloseButton(closeButton);
        tabbedPanel.setDoLayout(doLayout);
        tabbedPanel.setLabelposition(labelposition);
        tabbedPanel.setTemplateCssPath(templateCssPath);
        tabbedPanel.setBeforeSelectTabNotifyTopics(beforeSelectTabNotifyTopics);
        tabbedPanel.setAfterSelectTabNotifyTopics(afterSelectTabNotifyTopics);
        tabbedPanel.setDisabledTabCssClass(disabledTabCssClass);
        tabbedPanel.setUseSelectedTabCookie(useSelectedTabCookie);
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    public void setCloseButton(String closeButton) {
        this.closeButton = closeButton;
    }

    public void setDoLayout(String doLayout) {
        this.doLayout = doLayout;
    }

    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }

    public void setBeforeSelectTabNotifyTopics(String beforeSelectTabNotifyTopics) {
        this.beforeSelectTabNotifyTopics = beforeSelectTabNotifyTopics;
    }

    public void setAfterSelectTabNotifyTopics(String afterSelectTabNotifyTopics) {
        this.afterSelectTabNotifyTopics = afterSelectTabNotifyTopics;
    }

    public void setDisabledTabCssClass(String disabledTabCssClass) {
        this.disabledTabCssClass = disabledTabCssClass;
    }

    public void setUseSelectedTabCookie( String useSelectedTabCookie ) {
        this.useSelectedTabCookie = useSelectedTabCookie;
    }
}
