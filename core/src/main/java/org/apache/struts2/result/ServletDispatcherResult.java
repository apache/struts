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
package org.apache.struts2.result;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.url.QueryStringParser;

import java.io.Serial;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * Includes or forwards to a view (usually a jsp). Behind the scenes Struts
 * will use a RequestDispatcher, where the target servlet/JSP receives the same
 * request/response objects as the original servlet/JSP. Therefore, you can pass
 * data between them using request.setAttribute() - the Struts action is
 * available.
 * </p>
 *
 * <p>
 * There are three possible ways the result can be executed:
 * </p>
 *
 * <ul>
 *
 * <li>If we are in the scope of a JSP (a PageContext is available), PageContext's
 * {@link PageContext#include(String) include} method is called.</li>
 *
 * <li>If there is no PageContext and we're not in any sort of include (there is no
 * {@link RequestDispatcher#INCLUDE_SERVLET_PATH} in the request attributes), then a call to
 * {@link RequestDispatcher#forward(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse) forward}
 * is made.</li>
 *
 * <li>Otherwise, {@link RequestDispatcher#include(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse) include}
 * is called.</li>
 *
 * </ul>
 * <!-- END SNIPPET: description -->
 *
 * <p><b>This result type takes the following parameters:</b></p>
 * <p>
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>location (default)</b> - the location to go to after execution (ex. jsp).</li>
 *
 * <li><b>parse</b> - true by default. If set to false, the location param will not be parsed for Ognl expressions.</li>
 *
 * </ul>
 * <p>
 * <!-- END SNIPPET: params -->
 *
 * <p><b>Example:</b></p>
 *
 * <pre><!-- START SNIPPET: example -->
 * &lt;result name="success" type="dispatcher"&gt;
 *   &lt;param name="location"&gt;foo.jsp&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example --></pre>
 *
 * <p>
 * This result follows the same rules from {@link StrutsResultSupport}.
 * </p>
 *
 * @see jakarta.servlet.RequestDispatcher
 */
public class ServletDispatcherResult extends StrutsResultSupport {

    @Serial
    private static final long serialVersionUID = -1970659272360685627L;

    private static final Logger LOG = LogManager.getLogger(ServletDispatcherResult.class);

    private QueryStringParser queryStringParser;

    public ServletDispatcherResult() {
        super();
    }

    public ServletDispatcherResult(String location) {
        super(location);
    }

    @Inject
    public void setQueryStringParser(QueryStringParser queryStringParser) {
        this.queryStringParser = queryStringParser;
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
        LOG.debug("Processing location: {}", finalLocation);

        PageContext pageContext = ServletActionContext.getPageContext();

        if (pageContext != null) {
            pageContext.include(finalLocation);
        } else {
            HttpServletRequest request = ServletActionContext.getRequest();
            HttpServletResponse response = ServletActionContext.getResponse();
            RequestDispatcher dispatcher = request.getRequestDispatcher(finalLocation);

            //add parameters passed on the location to #parameters
            // see WW-2120
            if (StringUtils.isNotEmpty(finalLocation) && finalLocation.indexOf('?') > 0) {
                String queryString = finalLocation.substring(finalLocation.indexOf('?') + 1);
                HttpParameters parameters = getParameters(invocation);
                QueryStringParser.Result queryParams = queryStringParser.parse(queryString);
                if (!queryParams.isEmpty()) {
                    parameters = HttpParameters.create(queryParams.getQueryParams()).withParent(parameters).build();
                    invocation.getInvocationContext().withParameters(parameters);
                }
            }

            // if the view doesn't exist, let's do a 404
            if (dispatcher == null) {
                LOG.warn("Location {} not found!", finalLocation);
                response.sendError(404, "result '" + finalLocation + "' not found");
                return;
            }

            //if we are inside an action tag, we always need to do an include
            boolean insideActionTag = (Boolean) ObjectUtils.defaultIfNull(request.getAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION), Boolean.FALSE);

            // If we're included, then include the view
            // Otherwise do forward
            // This allow the page to, for example, set content type
            if (!insideActionTag && !response.isCommitted() && (request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH) == null)) {
                LOG.debug("Forwarding to location: {}", finalLocation);
                request.setAttribute("struts.view_uri", finalLocation);
                request.setAttribute("struts.request_uri", request.getRequestURI());

                dispatcher.forward(request, response);
            } else {
                LOG.debug("Including location: {}", finalLocation);

                dispatcher.include(request, response);
            }
        }
    }

    protected HttpParameters getParameters(ActionInvocation invocation) {
        return invocation.getInvocationContext().getParameters();
    }

}
