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
 * 
 * <!-- START SNIPPET: example -->
 * <pre>
 * &lt;s:tabbedpanel id="test" &gt;
 *    &lt;s:div id="one" label="one" theme="ajax" labelposition="top" &gt;
 *        This is the first pane&lt;br/&gt;
 *        &lt;s:form&gt;
 *            &lt;s:textfield name="tt" label="Test Text"/&gt;  &lt;br/&gt;
 *            &lt;s:textfield name="tt2" label="Test Text2"/&gt;
 *        &lt;/s:form&gt;
 *    &lt;/s:div&gt;
 *    &lt;s:div id="three" label="remote" theme="ajax" href="/AjaxTest.action" &gt;
 *        This is the remote tab
 *    &lt;/s:div&gt;
 * &lt;/s:tabbedpanel&gt;
 * </pre>
 * <!-- END SNIPPET: example -->
 * 
 * <!-- START SNIPPET: example2 -->
 * <p>Use notify topics to prevent a tab from being selected</p>
 * <pre>
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/beforeSelect", function(tab, cancel){
 *     cancel.cancel = true;
 * });
 * &lt;/script&gt;
 * 
 * &lt;s:tabbedpanel id="test" beforeSelectTabNotifyTopics="/beforeSelect"&gt;
 *    &lt;s:div id="three" label="remote" theme="ajax" href="/AjaxTest.action" &gt;
 *        One Tab
 *    &lt;/s:div&gt;
 *    &lt;s:div id="three" label="remote" theme="ajax" href="/AjaxTest.action" &gt;
 *        Another tab
 *    &lt;/s:div&gt;
 * &lt;/s:tabbedpanel&gt;
 * </pre>
 * <!-- END SNIPPET: example2 -->
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
    protected String beforeSelectTabNotifyTopics;
    protected String afterSelectTabNotifyTopics;
    
    public TabbedPanel(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }


    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (selectedTab != null)
            addParameter("selectedTab", findString(selectedTab));
        if (closeButton != null)
            addParameter("closeButton", findString(closeButton));
        addParameter("doLayout", doLayout != null ? findValue(doLayout, Boolean.class) : Boolean.FALSE);
        if (labelPosition != null) {
            //dojo has some weird name for label positions
            if(labelPosition.equalsIgnoreCase("left"))
               labelPosition = "left-h";
            if(labelPosition.equalsIgnoreCase("right"))
                labelPosition = "right-h";
            addParameter("labelPosition", null);
            addParameter("labelPosition", labelPosition);
        }
        if (templateCssPath != null)
            addParameter("templateCssPath", findString(templateCssPath));
        if (beforeSelectTabNotifyTopics!= null)
            addParameter("beforeSelectTabNotifyTopics", findString(beforeSelectTabNotifyTopics));
        if (afterSelectTabNotifyTopics!= null)
            addParameter("afterSelectTabNotifyTopics", findString(afterSelectTabNotifyTopics));
        
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

    @StrutsTagAttribute(description="Deprecated. Use 'closable' on each div(tab)")
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


    @StrutsTagAttribute(description="Comma separated list of topics to be published when a tab is clicked on (before it is selected)" +
    		"The tab widget will be passed as the first argument to the topic. The event can be cancelled setting to 'true' the 'cancel' property " +
    		"of the second parameter passed to the topics.")
    public void setBeforeSelectTabNotifyTopics(String selectedTabNotifyTopics) {
        this.beforeSelectTabNotifyTopics = selectedTabNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma separated list of topics to be published when a tab is clicked on (after it is selected)." +
        "The tab widget will be passed as the first argument to the topic.")
    public void setAfterSelectTabNotifyTopics(String afterSelectTabNotifyTopics) {
        this.afterSelectTabNotifyTopics = afterSelectTabNotifyTopics;
    }
}
