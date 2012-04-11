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
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Map;


/**
 * <!-- START SNIPPET: description -->
 *
 * Includes or forwards to a view (usually a jsp). Behind the scenes Struts
 * will use a RequestDispatcher, where the target servlet/JSP receives the same
 * request/response objects as the original servlet/JSP. Therefore, you can pass
 * data between them using request.setAttribute() - the Struts action is
 * available.
 * <p/>
 * There are three possible ways the result can be executed:
 *
 * <ul>
 *
 * <li>If we are in the scope of a JSP (a PageContext is available), PageContext's
 * {@link PageContext#include(String) include} method is called.</li>
 *
 * <li>If there is no PageContext and we're not in any sort of include (there is no
 * "javax.servlet.include.servlet_path" in the request attributes), then a call to
 * {@link RequestDispatcher#forward(javax.servlet.ServletRequest, javax.servlet.ServletResponse) forward}
 * is made.</li>
 *
 * <li>Otherwise, {@link RequestDispatcher#include(javax.servlet.ServletRequest, javax.servlet.ServletResponse) include}
 * is called.</li>
 *
 * </ul>
 * <!-- END SNIPPET: description -->
 *
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>location (default)</b> - the location to go to after execution (ex. jsp).</li>
 *
 * <li><b>parse</b> - true by default. If set to false, the location param will not be parsed for Ognl expressions.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre><!-- START SNIPPET: example -->
 * &lt;result name="success" type="dispatcher"&gt;
 *   &lt;param name="location"&gt;foo.jsp&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example --></pre>
 *
 * This result follows the same rules from {@link StrutsResultSupport}.
 *
 * @see javax.servlet.RequestDispatcher
 */
public class ServletDispatcherResult extends StrutsResultSupport {

    private static final long serialVersionUID = -1970659272360685627L;

    private static final Logger LOG = LoggerFactory.getLogger(ServletDispatcherResult.class);

    private UrlHelper urlHelper;

    public ServletDispatcherResult() {
        super();
    }

    public ServletDispatcherResult(String location) {
        super(location);
    }

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    /**
     * Dispatches to the given location. Does its forward via a RequestDispatcher. If the
     * dispatch fails a 404 error will be sent back in the http response.
     *
     * @param finalLocation the location to dispatch to.
     * @param invocation    the execution state of the action
     * @throws Exception if an error occurs. If the dispatch fails the error will go back via the
     *                   HTTP request.
     */
    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Forwarding to location " + finalLocation);
        }

        PageContext pageContext = ServletActionContext.getPageContext();

        if (pageContext != null) {
            pageContext.include(finalLocation);
        } else {
            HttpServletRequest request = ServletActionContext.getRequest();
            HttpServletResponse response = ServletActionContext.getResponse();
            RequestDispatcher dispatcher = request.getRequestDispatcher(finalLocation);

            //add parameters passed on the location to #parameters
            // see WW-2120
            if (StringUtils.isNotEmpty(finalLocation) && finalLocation.indexOf("?") > 0) {
                String queryString = finalLocation.substring(finalLocation.indexOf("?") + 1);
                Map<String, Object> parameters = getParameters(invocation);
                Map<String, Object> queryParams = urlHelper.parseQueryString(queryString, true);
                if (queryParams != null && !queryParams.isEmpty())
                    parameters.putAll(queryParams);
            }

            // if the view doesn't exist, let's do a 404
            if (dispatcher == null) {
                response.sendError(404, "result '" + finalLocation + "' not found");
                return;
            }

            //if we are inside an action tag, we always need to do an include
            Boolean insideActionTag = (Boolean) ObjectUtils.defaultIfNull(request.getAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION), Boolean.FALSE);

            // If we're included, then include the view
            // Otherwise do forward
            // This allow the page to, for example, set content type
            if (!insideActionTag && !response.isCommitted() && (request.getAttribute("javax.servlet.include.servlet_path") == null)) {
                request.setAttribute("struts.view_uri", finalLocation);
                request.setAttribute("struts.request_uri", request.getRequestURI());

                dispatcher.forward(request, response);
            } else {
                dispatcher.include(request, response);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getParameters(ActionInvocation invocation) {
        return (Map<String, Object>) invocation.getInvocationContext().getContextMap().get("parameters");
    }

}
