/*
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
package org.apache.struts2.portlet.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.result.ServletActionRedirectResult;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.views.util.UrlHelper;

import javax.portlet.PortletMode;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Portlet modification of the {@link ServletActionRedirectResult}.
 * 
 * <!-- START SNIPPET: description -->
 * 
 * This result uses the {@link ActionMapper} provided by the
 * <code>ActionMapperFactory</code> to instruct the render phase to invoke the
 * specified action and (optional) namespace. This is better than the
 * {@link PortletResult} because it does not require you to encode the URL
 * patterns processed by the {@link ActionMapper} in to your struts.xml
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
 * <li><b>actionName (default)</b> - the name of the action that will be
 * redirect to</li>
 * 
 * <li><b>namespace</b> - used to determine which namespace the action is in
 * that we're redirecting to . If namespace is null, this defaults to the
 * current namespace</li>
 * 
 * </ul>
 * 
 * <!-- END SNIPPET: params -->
 * 
 * <b>Example:</b>
 * 
 * <pre>
 * &lt;!-- START SNIPPET: example --&gt;
 *  &lt;package name=&quot;public&quot; extends=&quot;struts-default&quot;&gt;
 *      &lt;action name=&quot;login&quot; class=&quot;...&quot;&gt;
 *          &lt;!-- Redirect to another namespace --&gt;
 *          &lt;result type=&quot;redirect-action&quot;&gt;
 *              &lt;param name=&quot;actionName&quot;&gt;dashboard&lt;/param&gt;
 *              &lt;param name=&quot;namespace&quot;&gt;/secure&lt;/param&gt;
 *          &lt;/result&gt;
 *      &lt;/action&gt;
 *  &lt;/package&gt;
 * 
 *  &lt;package name=&quot;secure&quot; extends=&quot;struts-default&quot; namespace=&quot;/secure&quot;&gt;
 *      &lt;-- Redirect to an action in the same namespace --&gt;
 *      &lt;action name=&quot;dashboard&quot; class=&quot;...&quot;&gt;
 *          &lt;result&gt;dashboard.jsp&lt;/result&gt;
 *          &lt;result name=&quot;error&quot; type=&quot;redirect-action&quot;&gt;error&lt;/result&gt;
 *      &lt;/action&gt;
 * 
 *      &lt;action name=&quot;error&quot; class=&quot;...&quot;&gt;
 *          &lt;result&gt;error.jsp&lt;/result&gt;
 *      &lt;/action&gt;
 *  &lt;/package&gt;
 * 
 *  &lt;package name=&quot;passingRequestParameters&quot; extends=&quot;struts-default&quot; namespace=&quot;/passingRequestParameters&quot;&gt;
 *     &lt;-- Pass parameters (reportType, width and height) --&gt;
 *     &lt;!--
 *     The redirect-action url generated will be :
 *     /genReport/generateReport.action?reportType=pie&amp;width=100&amp;height=100
 *     --&gt;
 *     &lt;action name=&quot;gatherReportInfo&quot; class=&quot;...&quot;&gt;
 *        &lt;result name=&quot;showReportResult&quot; type=&quot;redirect-action&quot;&gt;
 *           &lt;param name=&quot;actionName&quot;&gt;generateReport&lt;/param&gt;
 *           &lt;param name=&quot;namespace&quot;&gt;/genReport&lt;/param&gt;
 *           &lt;param name=&quot;reportType&quot;&gt;pie&lt;/param&gt;
 *           &lt;param name=&quot;width&quot;&gt;100&lt;/param&gt;
 *           &lt;param name=&quot;height&quot;&gt;100&lt;/param&gt;
 *        &lt;/result&gt;
 *     &lt;/action&gt;
 *  &lt;/package&gt;
 * 
 * 
 *  &lt;!-- END SNIPPET: example --&gt;
 * </pre>
 * 
 * @see ActionMapper
 */
public class PortletActionRedirectResult extends PortletResult {

	private static final long serialVersionUID = -7627388936683562557L;

	/** The default parameter */
	public static final String DEFAULT_PARAM = "actionName";

	protected String actionName;
	protected String namespace;
	protected String method;

	private Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
	private ActionMapper actionMapper;
    private UrlHelper urlHelper;

	public PortletActionRedirectResult() {
		super();
	}

	public PortletActionRedirectResult(String actionName) {
		this(null, actionName, null);
	}

	public PortletActionRedirectResult(String actionName, String method) {
		this(null, actionName, method);
	}

	public PortletActionRedirectResult(String namespace, String actionName, String method) {
		super(null);
		this.namespace = namespace;
		this.actionName = actionName;
		this.method = method;
	}

	protected List<String> prohibitedResultParam = Arrays.asList(DEFAULT_PARAM, "namespace", "method", "encode", "parse",
            "location", "prependServletContext");

	@Inject
	public void setActionMapper(ActionMapper actionMapper) {
		this.actionMapper = actionMapper;
	}

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    /**
	 * @see com.opensymphony.xwork2.Result#execute(com.opensymphony.xwork2.ActionInvocation)
	 */
	public void execute(ActionInvocation invocation) throws Exception {
		actionName = conditionalParse(actionName, invocation);
		String portletNamespace = (String)invocation.getInvocationContext().get(PortletConstants.PORTLET_NAMESPACE);
		if (portletMode != null) {
			Map<PortletMode, String> namespaceMap = getNamespaceMap(invocation);
			namespace = namespaceMap.get(portletMode);
		}
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

		String resultCode = invocation.getResultCode();
		if (resultCode != null) {
			ResultConfig resultConfig = invocation.getProxy().getConfig().getResults().get(resultCode);
			Map<String, String> resultConfigParams = resultConfig.getParams();
            for (Map.Entry<String, String> e : resultConfigParams.entrySet()) {
                if (!prohibitedResultParam.contains(e.getKey())) {
                    requestParameters.put(e.getKey(), e.getValue() == null ? "" : conditionalParse(e.getValue(), invocation));
                }
            }
		}

		StringBuilder tmpLocation = new StringBuilder(actionMapper.getUriFromActionMapping(new ActionMapping(actionName,
				(portletNamespace == null ? namespace : portletNamespace + namespace), method, null)));
		urlHelper.buildParametersString(requestParameters, tmpLocation, "&");

		setLocation(tmpLocation.toString());

		super.execute(invocation);
	}

    @SuppressWarnings("unchecked")
    private Map<PortletMode, String> getNamespaceMap(ActionInvocation invocation) {
        return (Map<PortletMode, String>) invocation.getInvocationContext().get(PortletConstants.MODE_NAMESPACE_MAP);
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

	/**
	 * Adds a request parameter to be added to the redirect url
	 * 
	 * @param key The parameter name
	 * @param value The parameter value
	 *
	 * @return the portlet action redirect result
	 */
	public PortletActionRedirectResult addParameter(String key, Object value) {
		requestParameters.put(key, String.valueOf(value));
		return this;
	}

}
