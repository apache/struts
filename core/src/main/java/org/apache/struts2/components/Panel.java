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
package org.apache.struts2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a panel for tabbedPanel.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * See the example in {@link TabbedPanel}.
 * <p/>
 *
 * @see TabbedPanel
 *
 * @a2.tag name="panel" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.PanelTag"
 * description="Render a panel for tabbedPanel"
 */
public class Panel extends Div {

    public static final String TEMPLATE = "tab";
    public static final String TEMPLATE_CLOSE = "tab-close";
    public static final String COMPONENT_NAME = Panel.class.getName();

    protected String tabName;
    protected String subscribeTopicName;
    protected String remote;

    public Panel(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE_CLOSE;
    }

    public boolean end(Writer writer, String body) {
        TabbedPanel tabbedPanel = ((TabbedPanel) findAncestor(TabbedPanel.class));
        subscribeTopicName = tabbedPanel.getTopicName();
        tabbedPanel.addTab(this);

        return super.end(writer, body);
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (tabName != null) {
            addParameter("tabName", findString(tabName));
        }

        if (subscribeTopicName != null) {
            addParameter("subscribeTopicName", subscribeTopicName);
        }

        if (remote != null && "true".equalsIgnoreCase(remote)) {
            addParameter("remote", "true");
        } else {
            addParameter("remote", "false");
        }
    }

    public String getTabName() {
        return findString(tabName);
    }

    public String getComponentName() {
        return COMPONENT_NAME;
    }

    /**
     * The text of the tab to display in the header tab list
     * @a2.tagattribute required="true"
     */
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    /**
     * Set subscribeTopicName attribute
     * @a2.tagattribute required="false"
     */
    public void setSubscribeTopicName(String subscribeTopicName) {
        this.subscribeTopicName = subscribeTopicName;
    }

    /**
     * determines whether this is a remote panel (ajax) or a local panel (content loaded into visible/hidden containers)
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setRemote(String remote) {
        this.remote = remote;
    }
}
