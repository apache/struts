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

package org.apache.struts2.dojo.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

/**
 * <!-- START SNIPPET: javadoc -->
 * The tabbedpanel widget is primarily an AJAX component, where each tab can either be local content or remote
 * content (refreshed each time the user selects that tab).</p>
 * If the useSelectedTabCookie attribute is set to true, the id of the selected tab is saved in a cookie on activation.
 * When coming back to this view, the cookie is read and the tab will be activated again, unless an actual value for the
 * selectedTab attribute is specified.</p>
 * If you want to use the cookie feature, please be sure that you provide a unique id for your tabbedpanel component,
 * since this will also be the identifying name component of the stored cookie.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * 
 * <!-- START SNIPPET: example1 -->
 * &lt;sx:head /&gt;
 * &lt;sx:tabbedpanel id="test" &gt;
 *    &lt;sx:div id="one" label="one" theme="ajax" labelposition="top" &gt;
 *        This is the first pane&lt;br/&gt;
 *        &lt;s:form&gt;
 *            &lt;s:textfield name="tt" label="Test Text"/&gt;  &lt;br/&gt;
 *            &lt;s:textfield name="tt2" label="Test Text2"/&gt;
 *        &lt;/s:form&gt;
 *    &lt;/sx:div&gt;
 *    &lt;sx:div id="three" label="remote" theme="ajax" href="/AjaxTest.action" &gt;
 *        This is the remote tab
 *    &lt;/sx:div&gt;
 * &lt;/sx:tabbedpanel&gt;
 * <!-- END SNIPPET: example1 -->
 * 
 * <!-- START SNIPPET: example2 -->
 * &lt;sx:head /&gt;
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/beforeSelect", function(event, tab, tabContainer){
 *     event.cancel = true;
 * });
 * &lt;/script&gt;
 * 
 * &lt;sx:tabbedpanel id="test" beforeSelectTabNotifyTopics="/beforeSelect"&gt;
 *    &lt;sx:div id="three" label="remote" theme="ajax" href="/AjaxTest.action" &gt;
 *        One Tab
 *    &lt;/sx:div&gt;
 *    &lt;sx:div id="three" label="remote" theme="ajax" href="/AjaxTest.action" &gt;
 *        Another tab
 *    &lt;/sx:div&gt;
 * &lt;/sx:tabbedpanel&gt;
 * <!-- END SNIPPET: example2 -->
 */
@StrutsTag(name="tabbedpanel", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.TabbedPanelTag", description="Render a tabbedPanel widget.")
public class TabbedPanel extends ClosingUIBean {
    public static final String TEMPLATE = "tabbedpanel";
    public static final String TEMPLATE_CLOSE = "tabbedpanel-close";
    final private static String COMPONENT_NAME = TabbedPanel.class.getName();
    private final static transient Random RANDOM = new Random();    

    protected String selectedTab;
    protected String closeButton;
    protected String doLayout ;
    protected String templateCssPath;
    protected String beforeSelectTabNotifyTopics;
    protected String afterSelectTabNotifyTopics;
    protected String disabledTabCssClass; 
    protected String useSelectedTabCookie;

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
        if (disabledTabCssClass!= null)
            addParameter("disabledTabCssClass", findString(disabledTabCssClass));
        if(useSelectedTabCookie != null) {
            addParameter("useSelectedTabCookie", findString(useSelectedTabCookie));
        }

        // generate a random ID if not explicitly set and not parsing the content
        Boolean parseContent = (Boolean)stack.getContext().get(Head.PARSE_CONTENT);
        boolean generateId = (parseContent != null ? !parseContent : true);
        
        addParameter("pushId", generateId);
        if ((this.id == null || this.id.length() == 0) && generateId) {
            // resolves Math.abs(Integer.MIN_VALUE) issue reported by FindBugs 
            // http://findbugs.sourceforge.net/bugDescriptions.html#RV_ABSOLUTE_VALUE_OF_RANDOM_INT
            int nextInt = RANDOM.nextInt();
            nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);  
            this.id = "widget_" + String.valueOf(nextInt);
            addParameter("id", this.id);
        }
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
    		"The tab container widget will be passed as the first argument to the topic. The second parameter is the tab widget." +
    		"The event can be cancelled setting to 'true' the 'cancel' property " +
    		"of the third parameter passed to the topics.")
    public void setBeforeSelectTabNotifyTopics(String selectedTabNotifyTopics) {
        this.beforeSelectTabNotifyTopics = selectedTabNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma separated list of topics to be published when a tab is clicked on (after it is selected)." +
        "The tab container widget will be passed as the first argument to the topic. The second parameter is the tab widget.")
    public void setAfterSelectTabNotifyTopics(String afterSelectTabNotifyTopics) {
        this.afterSelectTabNotifyTopics = afterSelectTabNotifyTopics;
    }

    @StrutsTagAttribute(description="Css class to be applied to the tab button of disabled tabs", defaultValue="strutsDisabledTab")
    public void setDisabledTabCssClass(String disabledTabCssClass) {
        this.disabledTabCssClass = disabledTabCssClass;
    }

    @StrutsTagAttribute(required = false, defaultValue = "false", description = "If set to true, the id of the last selected " +
            "tab will be stored in cookie. If the view is rendered, it will be tried to read this cookie and activate " +
            "the corresponding tab on success, unless overridden by the selectedTab attribute. The cookie name is \"Struts2TabbedPanel_selectedTab_\"+id.")
    public void setUseSelectedTabCookie( String useSelectedTabCookie ) {
        this.useSelectedTabCookie = useSelectedTabCookie;
    }
}
