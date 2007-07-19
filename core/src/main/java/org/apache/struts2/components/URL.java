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

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.views.util.UrlHelper;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <p>This tag is used to create a URL.</p>
 *
 * <p>You can use the "param" tag inside the body to provide
 * additional request parameters. If the value of a param is an Array or 
 * an Iterable all the values will be added to the URL.</p>
 *
 * <b>NOTE:</b>
 * <p>When includeParams is 'all' or 'get', the parameter defined in param tag will take
 * precedence and will not be overriden if they exists in the parameter submitted. For
 * example, in Example 3 below, if there is a id parameter in the url where the page this
 * tag is included like http://<host>:<port>/<context>/editUser.action?id=3333&name=John
 * the generated url will be http://<host>:<port>/context>/editUser.action?id=22&name=John
 * cause the parameter defined in the param tag will take precedence.</p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *      <li>action (String) - (value or action choose either one, if both exist value takes precedence) action's name (alias) <li>
 *      <li>value (String) - (value or action choose either one, if both exist value takes precedence) the url itself</li>
 *      <li>scheme (String) - http scheme (http, https) default to the scheme this request is in</li>
 *      <li>namespace - action's namespace</li>
 *      <li>method (String) - action's method, default to execute() </li>
 *      <li>encode (Boolean) - url encode the generated url. Default is true</li>
 *      <li>includeParams (String) - The includeParams attribute may have the value 'none', 'get' or 'all'. Default is 'get'.
 *                                   none - include no parameters in the URL
 *                                   get  - include only GET parameters in the URL (default)
 *                                   all  - include both GET and POST parameters in the URL
 *      </li>
 *      <li>includeContext (Boolean) - determine wheather to include the web app context path. Default is true.</li>
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;-- Example 1 --&gt;
 * &lt;s:url value="editGadget.action"&gt;
 *     &lt;s:param name="id" value="%{selected}" /&gt;
 * &lt;/s:url&gt;
 *
 * &lt;-- Example 2 --&gt;
 * &lt;s:url action="editGadget"&gt;
 *     &lt;s:param name="id" value="%{selected}" /&gt;
 * &lt;/s:url&gt;
 *
 * &lt;-- Example 3--&gt;
 * &lt;s:url includeParams="get"  &gt;
 *     &lt;s:param name="id" value="%{'22'}" /&gt;
 * &lt;/s:url&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see Param
 *
 */
@StrutsTag(name="url", tldTagClass="org.apache.struts2.views.jsp.URLTag", description="This tag is used to create a URL")
public class URL extends ContextBean {
    private static final Log LOG = LogFactory.getLog(URL.class);

    /**
     * The includeParams attribute may have the value 'none', 'get' or 'all'.
     * It is used when the url tag is used without a value attribute.
     * Its value is looked up on the ValueStack
     * If no includeParams is specified then 'get' is used.
     * none - include no parameters in the URL
     * get  - include only GET parameters in the URL (default)
     * all  - include both GET and POST parameters in the URL
     */
    public static final String NONE = "none";
    public static final String GET = "get";
    public static final String ALL = "all";

    protected HttpServletRequest req;
    protected HttpServletResponse res;

    protected String includeParams;
    protected String scheme;
    protected String value;
    protected String action;
    protected String namespace;
    protected String method;
    protected boolean encode = true;
    protected boolean includeContext = true;
    protected String portletMode;
    protected String windowState;
    protected String portletUrlType;
    protected String anchor;
    protected String urlIncludeParams;
    protected ExtraParameterProvider extraParameterProvider;
	protected UrlRenderer urlRenderer;

    public URL(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
    }

    @Inject(StrutsConstants.STRUTS_URL_INCLUDEPARAMS)
    public void setUrlIncludeParams(String urlIncludeParams) {
        this.urlIncludeParams = urlIncludeParams;
    }
    
    @Inject
	public void setUrlRenderer(UrlRenderer urlRenderer) {
		this.urlRenderer = urlRenderer;
	}

    @Inject(required=false)
    public void setExtraParameterProvider(ExtraParameterProvider provider) {
        this.extraParameterProvider = provider;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        if (value != null) {
            value = findString(value);
        }

        // no explicit url set so attach params from current url, do
        // this at start so body params can override any of these they wish.
        try {
            // ww-1266
            String includeParams = (urlIncludeParams != null ? urlIncludeParams.toLowerCase() : GET);

            if (this.includeParams != null) {
                includeParams = findString(this.includeParams);
            }

            if (NONE.equalsIgnoreCase(includeParams)) {
                mergeRequestParameters(value, parameters, Collections.EMPTY_MAP);
            } else if (ALL.equalsIgnoreCase(includeParams)) {
                mergeRequestParameters(value, parameters, req.getParameterMap());

                // for ALL also include GET parameters
                includeGetParameters();
                includeExtraParameters();
            } else if (GET.equalsIgnoreCase(includeParams) || (includeParams == null && value == null && action == null)) {
                includeGetParameters();
                includeExtraParameters();
            } else if (includeParams != null) {
                LOG.warn("Unknown value for includeParams parameter to URL tag: " + includeParams);
            }
        } catch (Exception e) {
            LOG.warn("Unable to put request parameters (" + req.getQueryString() + ") into parameter map.", e);
        }


        return result;
    }

    private void includeExtraParameters() {
        if (extraParameterProvider != null) {
            mergeRequestParameters(value, parameters, extraParameterProvider.getExtraParameters());
        }
    }
    private void includeGetParameters() {
    	String query = extractQueryString();
    	mergeRequestParameters(value, parameters, UrlHelper.parseQueryString(query));
    }

    private String extractQueryString() {
        // Parse the query string to make sure that the parameters come from the query, and not some posted data
        String query = req.getQueryString();
        if (query == null) {
            query = (String) req.getAttribute("javax.servlet.forward.query_string");
        }

        if (query != null) {
            // Remove possible #foobar suffix
            int idx = query.lastIndexOf('#');

            if (idx != -1) {
                query = query.substring(0, idx);
            }
        }
        return query;
    }

    public boolean end(Writer writer, String body) {
    	urlRenderer.renderUrl(writer, this);
        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="The includeParams attribute may have the value 'none', 'get' or 'all'", defaultValue="get")
    public void setIncludeParams(String includeParams) {
        this.includeParams = includeParams;
    }

    @StrutsTagAttribute(description="Set scheme attribute")
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @StrutsTagAttribute(description="The target value to use, if not using action")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="The action to generate the URL for, if not using value")
    public void setAction(String action) {
        this.action = action;
    }

    @StrutsTagAttribute(description="The namespace to use")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @StrutsTagAttribute(description="The method of action to use")
    public void setMethod(String method) {
        this.method = method;
    }

    @StrutsTagAttribute(description="Whether to encode parameters", type="Boolean", defaultValue="true")
    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    @StrutsTagAttribute(description="Whether actual context should be included in URL", type="Boolean", defaultValue="true")
    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }

    @StrutsTagAttribute(description="The resulting portlet mode")
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    @StrutsTagAttribute(description="The resulting portlet window state")
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    @StrutsTagAttribute(description="Specifies if this should be a portlet render or action URL")
    public void setPortletUrlType(String portletUrlType) {
        this.portletUrlType = portletUrlType;
    }

    @StrutsTagAttribute(description="The anchor for this URL")
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }


    /**
     * Merge request parameters into current parameters. If a parameter is
     * already present, than the request parameter in the current request and value atrribute
     * will not override its value.
     *
     * The priority is as follows:-
     * <ul>
     *  <li>parameter from the current request (least priority)</li>
     *  <li>parameter form the value attribute (more priority)</li>
     *  <li>parameter from the param tag (most priority)</li>
     * </ul>
     *
     * @param value the value attribute (url to be generated by this component)
     * @param parameters component parameters
     * @param contextParameters request parameters
     */
    protected void mergeRequestParameters(String value, Map parameters, Map contextParameters){

        Map mergedParams = new LinkedHashMap(contextParameters);

        // Merge contextParameters (from current request) with parameters specified in value attribute
        // eg. value="someAction.action?id=someId&venue=someVenue"
        // where the parameters specified in value attribute takes priority.

        if (value != null && value.trim().length() > 0 && value.indexOf("?") > 0) {
            mergedParams = new LinkedHashMap();

            String queryString = value.substring(value.indexOf("?")+1);

            mergedParams = UrlHelper.parseQueryString(queryString);
            for (Iterator iterator = contextParameters.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object key = entry.getKey();

                if (!mergedParams.containsKey(key)) {
                    mergedParams.put(key, entry.getValue());
                }
            }
        }


        // Merge parameters specified in value attribute
        // eg. value="someAction.action?id=someId&venue=someVenue"
        // with parameters specified though param tag
        // eg. <param name="id" value="%{'someId'}" />
        // where parameters specified through param tag takes priority.

        for (Iterator iterator = mergedParams.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();

            if (!parameters.containsKey(key)) {
                parameters.put(key, entry.getValue());
            }
        }
    }

    public static interface ExtraParameterProvider {
        public Map getExtraParameters();
    }
}
