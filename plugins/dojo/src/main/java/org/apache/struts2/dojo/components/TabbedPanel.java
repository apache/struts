/*
 * $Id: TabbedPanel.java 508575 2007-02-16 20:46:49Z musachy $
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
package org.apache.struts2.dojo.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * The tabbedpanel widget is primarily an AJAX component, where each tab can either be local content or remote
 * content (refreshed each time the user selects that tab).</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdesc -->
 * The following is an example of a tabbedpanel and panel tag utilizing local and remote content.<p/>
 * <!-- END SNIPPET: exdesc -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:tabbedpanel id=&quot;test&quot; &gt;
 *    &lt;s:div id=&quot;one&quot; label=&quot;one&quot; theme=&quot;ajax&quot; labelposition=&quot;top&quot; &gt;
 *        This is the first pane&lt;br/&gt;
 *        &lt;s:form&gt;
 *            &lt;s:textfield name=&quot;tt&quot; label=&quot;Test Text&quot;/&gt;  &lt;br/&gt;
 *            &lt;s:textfield name=&quot;tt2&quot; label=&quot;Test Text2&quot;/&gt;
 *        &lt;/s:form&gt;
 *    &lt;/s:div&gt;
 *    &lt;s:div id=&quot;three&quot; label=&quot;remote&quot; theme=&quot;ajax&quot; href=&quot;/AjaxTest.action&quot; &gt;
 *        This is the remote tab
 *    &lt;/s:div&gt;
 * &lt;/s:tabbedpanel&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="tabbedpanel", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.TabbedPanelTag", description="Render a tabbedPanel widget.")
public class TabbedPanel extends ClosingUIBean {
    public static final String TEMPLATE = "tabbedpanel";
    public static final String TEMPLATE_CLOSE = "tabbedpanel-close";
    final private static String COMPONENT_NAME = TabbedPanel.class.getName();

    protected String selectedTab;
    protected String closeButton;
    protected String doLayout ;
    protected String templateCssPath;

    public TabbedPanel(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }


    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if(selectedTab != null)
            addParameter("selectedTab", findString(selectedTab));
        if(closeButton != null)
            addParameter("closeButton", findString(closeButton));
        addParameter("doLayout", doLayout != null ? findValue(doLayout, Boolean.class) : Boolean.FALSE);
        if(labelPosition != null) {
            //dojo has some weird name for label positions
            if(labelPosition.equalsIgnoreCase("left"))
               labelPosition = "left-h";
            if(labelPosition.equalsIgnoreCase("right"))
                labelPosition = "right-h";
            addParameter("labelPosition", null);
            addParameter("labelPosition", labelPosition);
        }
        if(templateCssPath != null)
            addParameter("templateCssPath", findString(templateCssPath));
    }

    @Override
    @StrutsTagSkipInheritance
    public void setTheme(String theme) {
        super.setTheme(theme);
    }
    
    @Override
    public String getTheme() {
        return "ajax";
    }
    
    public String getDefaultOpenTemplate() {
        return TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE_CLOSE;
    }

    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @StrutsTagAttribute(description="The id to assign to the component.", required=true)
    public void setId(String id) {
        // This is required to override tld generation attributes to required=true
        super.setId(id);
    }


    @StrutsTagAttribute(description=" The id of the tab that will be selected by default")
    public void setSelectedTab(String selectedTab) {
      this.selectedTab = selectedTab;
    }

    @StrutsTagAttribute(description="Where the close button will be placed, possible values are 'tab' and 'pane'")
    public void setCloseButton(String closeButton) {
        this.closeButton = closeButton;
    }

    @StrutsTagAttribute(description="If doLayout is false, the tab container's height equals the height of the currently selected tab", type="Boolean", defaultValue="false")
    public void setDoLayout(String doLayout) {
        this.doLayout = doLayout;
    }

    @StrutsTagAttribute(description="Template css path")
    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }
}
