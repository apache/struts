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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A tag that creates a HTML &lt;a href='' /&gt; that when clicked calls a URL remote XMLHttpRequest call via the dojo
 * framework. The result from the URL is executed as JavaScript. If a "listenTopics" is supplied, it will publish a
 * 'click' message to that topic when the result is returned.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;s:a id="link1" theme="ajax" href="/DoIt.action" errorText="An error ocurred" showErrorTransportText="true"&gt;
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
 * &lt;a dojoType="BindAnchor" evalResult="true" id="link1" href="/DoIt.action?id=1" errorHtml="An error ocurred"
 * showTransportError="true"&gt;&lt;/a&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * </p>
 *
 * <!-- START SNIPPET: exampledescription2 -->
 *
 * Here is an example that uses the postInvokeJS. This example is in altSyntax=true:
 *
 * <!-- END SNIPPET: exampledescription2 -->
 *
 * </p>
 *
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;s:a id="test" theme="ajax" href="/simpeResult.action" preInvokeJS="confirm(\'You sure\')"&gt;
 * 	A
 * &lt;/s:a&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 *
 * @s.tag name="a" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.AnchorTag"
 * description="Render a HTML href element that when clicked calls a URL via remote XMLHttpRequest"
 * 
 */
public class Anchor extends RemoteCallUIBean {
    final public static String OPEN_TEMPLATE = "a";
    final public static String TEMPLATE = "a-close";
    final public static String COMPONENT_NAME = Anchor.class.getName();

    protected String notifyTopics;
    protected String preInvokeJS;

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

        if (notifyTopics != null) {
            addParameter("notifyTopics", findString(notifyTopics));
        }

        if (preInvokeJS != null) {
            addParameter("preInvokeJS", findString(preInvokeJS));
        }
    }

    /**
     * The id to assign the component
     * @s.tagattribute required="false" type="String"
     */
    public void setId(String id) {
        super.setId(id);
    }

    /**
     * Topic names to post an event to after the remote call has been made
     * @s.tagattribute required="false"
     */
    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }

    /**
     * A javascript snippet that will be invoked prior to the execution of the target href. If provided must return true or false. True indicates to continue executing target, false says do not execute link target. Possible uses are for confirm dialogs.
     * @s.tagattribute required="false" type="String"
     */
    public void setPreInvokeJS(String preInvokeJS) {
        this.preInvokeJS = preInvokeJS;
    }
}
