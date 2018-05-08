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

package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.util.Arrays;
import java.util.List;

/**
 * <!-- START SNIPPET: description -->
 *
 * This result uses the {@link ActionMapper} provided by the
 * {@link ActionMapperFactory} to redirect the browser to a URL that invokes the
 * specified action and (optional) namespace. This is better than the
 * {@link ServletRedirectResult} because it does not require you to encode the
 * URL patterns processed by the {@link ActionMapper} in to your struts.xml
 * configuration files. This means you can change your URL patterns at any point
 * and your application will still work. It is strongly recommended that if you
 * are redirecting to another action, you use this result rather than the
 * standard redirect result.
 *
 * See examples below for an example of how request parameters could be passed
 * in.
 *
 * <!-- END SNIPPET: description -->
 *
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>actionName (default)</b> - The name of the action that will be
 * redirected to.</li>
 *
 * <li><b>namespace</b> - Used to determine which namespace the action is in
 * that we're redirecting to.  If namespace is null, the default will be the
 * current namespace.</li>
 *
 * <li><b>suppressEmptyParameters</b> - Optional boolean (defaults to false) that
 * can prevent parameters with no values from being included in the redirect
 * URL.</li>
 *
 * <li><b>parse</b> - Boolean, true by default.  If set to false, the actionName
 * param will not be parsed for Ognl expressions.</li>
 *
 * <li><b>anchor</b> - Optional.  Also known as "fragment" or colloquially as
 * "hash".  You can specify an anchor for a result.</li>
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;package name="public" extends="struts-default"&gt;
 *     &lt;action name="login" class="..."&gt;
 *         &lt;!-- Redirect to another namespace --&gt;
 *         &lt;result type="redirectAction"&gt;
 *             &lt;param name="actionName"&gt;dashboard&lt;/param&gt;
 *             &lt;param name="namespace"&gt;/secure&lt;/param&gt;
 *         &lt;/result&gt;
 *     &lt;/action&gt;
 * &lt;/package&gt;
 *
 * &lt;package name="secure" extends="struts-default" namespace="/secure"&gt;
 *     &lt;-- Redirect to an action in the same namespace --&gt;
 *     &lt;action name="dashboard" class="..."&gt;
 *         &lt;result&gt;dashboard.jsp&lt;/result&gt;
 *         &lt;result name="error" type="redirectAction"&gt;error&lt;/result&gt;
 *     &lt;/action&gt;
 *
 *     &lt;action name="error" class="..."&gt;
 *         &lt;result&gt;error.jsp&lt;/result&gt;
 *     &lt;/action&gt;
 * &lt;/package&gt;
 *
 * &lt;package name="passingRequestParameters" extends="struts-default" namespace="/passingRequestParameters"&gt;
 *    &lt;!-- Pass parameters (reportType, width and height) --&gt;
 *    &lt;!--
 *    The redirectAction url generated will be :
 *    /genReport/generateReport.action?reportType=pie&width=100&height=100#summary
 *    --&gt;
 *    &lt;action name="gatherReportInfo" class="..."&gt;
 *       &lt;result name="showReportResult" type="redirectAction"&gt;
 *          &lt;param name="actionName"&gt;generateReport&lt;/param&gt;
 *          &lt;param name="namespace"&gt;/genReport&lt;/param&gt;
 *          &lt;param name="reportType"&gt;pie&lt;/param&gt;
 *          &lt;param name="width"&gt;100&lt;/param&gt;
 *          &lt;param name="height"&gt;100&lt;/param&gt;
 *          &lt;param name="empty"&gt;&lt;/param&gt;
 *          &lt;param name="suppressEmptyParameters"&gt;true&lt;/param&gt;
 *          &lt;param name="anchor"&gt;summary&lt;/param&gt;
 *       &lt;/result&gt;
 *    &lt;/action&gt;
 * &lt;/package&gt;
 *
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see ActionMapper
 */
public class ServletActionRedirectResult extends ServletRedirectResult implements ReflectionExceptionHandler {

    private static final long serialVersionUID = -9042425229314584066L;

    /* The default parameter */
    public static final String DEFAULT_PARAM = "actionName";

    protected String actionName;
    protected String namespace;
    protected String method;

    public ServletActionRedirectResult() {
        super();
    }

    public ServletActionRedirectResult(String actionName) {
        this(null, actionName, null, null);
    }

    public ServletActionRedirectResult(String actionName, String method) {
        this(null, actionName, method, null);
    }

    public ServletActionRedirectResult(String namespace, String actionName, String method) {
        this(namespace, actionName, method, null);
    }

    public ServletActionRedirectResult(String namespace, String actionName, String method, String anchor) {
        super(null, anchor);
        this.namespace = namespace;
        this.actionName = actionName;
        this.method = method;
    }

    /**
     * @see com.opensymphony.xwork2.Result#execute(com.opensymphony.xwork2.ActionInvocation)
     */
    public void execute(ActionInvocation invocation) throws Exception {
        actionName = conditionalParse(actionName, invocation);
        parseLocation = false;

        if (namespace == null) {
            namespace = invocation.getProxy().getNamespace();
        } else {
            namespace = conditionalParse(namespace, invocation);
        }
        if (method == null) {
            method = "";
        } else {
            method = conditionalParse(method, invocation);
        }

        String tmpLocation = actionMapper.getUriFromActionMapping(new ActionMapping(actionName, namespace, method, null));

        setLocation(tmpLocation);

        super.execute(invocation);
    }

    /**
     * Sets the action name
     *
     * @param actionName The name
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * Sets the namespace
     *
     * @param namespace The namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Sets the method
     *
     * @param method The method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    protected List<String> getProhibitedResultParams() {
        return Arrays.asList(
                DEFAULT_PARAM,
                "namespace",
                "method",
                "encode",
                "parse",
                "location",
                "prependServletContext",
                "suppressEmptyParameters",
                "anchor",
                "statusCode"
        );
    }

}
