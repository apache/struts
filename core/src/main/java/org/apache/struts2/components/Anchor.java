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
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A tag that creates a HTML &lt;a href='' /&gt; that when clicked calls a URL remote XMLHttpRequest call via the dojo
 * framework. The 'targets' attribute can hold a comma-delimited list of ids of the elements whose
 * content will be replaced with the response of the request.The result from the URL is executed as JavaScript is executeScripts is set to 'true'. If a
 * 'refreshListenTopic' is supplied, it will listen to that event and update its targets contents.
 *
 * <!-- END SNIPPET: javadoc -->
 *
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
 * &lt;s:a id="test" theme="ajax" href="/simpeResult.action" beforeLoading="confirm(\'You sure\')"&gt;
 *  A
 * &lt;/s:a&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 *
 * @s.tag name="a" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.AnchorTag"
 * description="Render a HTML href element that when clicked calls a URL via remote XMLHttpRequest and updates its targets"
 *
 */
public class Anchor extends AbstractRemoteCallUIBean {
    final public static String OPEN_TEMPLATE = "a";
    final public static String TEMPLATE = "a-close";
    final public static String COMPONENT_NAME = Anchor.class.getName();

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

    /**
     * The id to assign the component
     * @s.tagattribute required="false" type="String"
     */
    public void setId(String id) {
        super.setId(id);
    }

    /**
     * Comma delimited list of ids of the elements whose content will be updated
     * @s.tagattribute required="false" type="String"
     */
    public void setTargets(String targets) {
        this.targets = targets;
    }

}
