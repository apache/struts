/*
 * $Id: Anchor.java 508285 2007-02-16 02:42:24Z musachy $
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

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A tag that creates a HTML &lt;a href='' /&gt; that when clicked calls a URL remote XMLHttpRequest call via the dojo
 * framework.<p/>
 *<!-- END SNIPPET: javadoc -->
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;s:a id="link1" theme="ajax" href="/DoIt.action" errorText="An error ocurred" loadingText="Loading..."&gt;
 *     &lt;img border="none" src="&lt;%=request.getContextPath()%&gt;/images/delete.gif"/&gt;
 *     &lt;s:param name="id" value="1"/&gt;
 * &lt;/s:a&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 * </p>
 *
 * <!-- START SNIPPET: exampledescription1 -->
 *
 * Results in
 *
 * <!-- END SNIPPET: exampledescription1 -->
 *
 * </p>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;a dojoType="BindAnchor" executeScripts="true" id="link1" href="/DoIt.action?id=1" errorText="An error ocurred"&gt;&lt;/a&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * </p>
 *
 * <!-- START SNIPPET: exampledescription2 -->
 *
 * Here is an example that uses the beforeLoading. This example is in altSyntax=true:
 *
 * <!-- END SNIPPET: exampledescription2 -->
 *
 * </p>
 *
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;s:a id="test" theme="ajax" href="/simpeResult.action" beforeLoading="confirm('Are you sure?')"&gt;
 *  A
 * &lt;/s:a&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 *
 */
@StrutsTag(name="a", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.AnchorTag", description="Render a HTML href element that when clicked can optionally call a URL via remote XMLHttpRequest and updates its targets")
public class Anchor extends AbstractRemoteCallUIBean {
    public static final String OPEN_TEMPLATE = "a";
    public static final String TEMPLATE = "a-close";
    public static final String COMPONENT_NAME = Anchor.class.getName();

    protected String targets;

    public Anchor(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if(targets != null)
            addParameter("targets", findString(targets));
    }

    @StrutsTagAttribute(description="Comma delimited list of ids of the elements whose content will be updated")
    public void setTargets(String targets) {
        this.targets = targets;
    }

    @StrutsTagAttribute(name="onLoadJS", description="Deprecated. Use 'notifyTopics'. Javascript code execute after reload")
    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }


    @StrutsTagAttribute(name="preInvokeJS", description="Deprecated. Use 'notifyTopics'. Javascript code execute before reload")
    public void setBeforeLoading(String beforeLoading) {
        this.beforeLoading = beforeLoading;
    }
}
