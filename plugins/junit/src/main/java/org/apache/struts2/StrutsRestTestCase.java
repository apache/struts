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
package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.sun.net.httpserver.HttpsParameters;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

public class StrutsRestTestCase<T> extends StrutsJUnit4TestCase<T> {

    /**
     * Executes an action and returns it's output (not the result returned from
     * execute()), but the actual output that would be written to the response.
     * For this to work the configured result for the action needs to be JSON,
     * FreeMarker, or Velocity (JSPs can be used with the Embedded JSP plugin)
     *
     * @param uri action uri to test
     * @return execution result
     *
     * @throws ServletException in case of servlet errors
     * @throws UnsupportedEncodingException in case of unsupported encoding
     */
    @Override
    protected String executeAction(String uri) throws ServletException, UnsupportedEncodingException {
        return executeAction("GET", uri);
    }

    /**
     * Executes an action and returns it's output (not the result returned from
     * execute()), but the actual output that would be written to the response.
     * For this to work the configured result for the action needs to be JSON,
     * FreeMarker, or Velocity (JSPs can be used with the Embedded JSP plugin)
     *
     * @param httpMethod HTTP method of request like GET, POST, PUT or DELETE
     * @param uri action uri to test
     * @return execution result
     *
     * @throws ServletException in case of servlet errors
     * @throws UnsupportedEncodingException in case of unsupported encoding
     */
    protected String executeAction(String httpMethod, String uri) throws ServletException, UnsupportedEncodingException {
        request.setRequestURI(uri);
        request.setMethod(httpMethod);

        ActionMapping mapping = getActionMapping(request);

        assertNotNull(mapping);
        Dispatcher.getInstance().serviceAction(request, response, mapping);

        if (response.getStatus() != HttpServletResponse.SC_OK)
            throw new ServletException("Error code [" + response.getStatus() + "], Error: ["
                    + response.getErrorMessage() + "]");

        return response.getContentAsString();
    }


    /**
     * Creates an action proxy for a request, and sets parameters of the ActionInvocation to the passed
     * parameters. Make sure to set the request parameters in the protected "request" object before calling this method.
     *
     * @param uri request uri to test
     * @return action proxy found for this request uri
     */
    @Override
    protected ActionProxy getActionProxy(String uri) {
        return getActionProxy("GET", uri);
    }

    /**
     * Creates an action proxy for a request, and sets parameters of the ActionInvocation to the passed
     * parameters. Make sure to set the request parameters in the protected "request" object before calling this method.
     *
     * @param httpMethod HTTP method of request like GET, POST, PUT or DELETE
     * @param uri request uri to test
     * @return action proxy found for this request uri
     */
    protected ActionProxy getActionProxy(String httpMethod, String uri) {
		request.setRequestURI(uri);
		request.setMethod(httpMethod);

		ActionMapping mapping = getActionMapping(request);
		String namespace = mapping.getNamespace();
		String name = mapping.getName();
		String method = mapping.getMethod();

		Configuration config = configurationManager.getConfiguration();
		ActionProxy proxy = config.getContainer()
				                    .getInstance(ActionProxyFactory.class)
				                    .createActionProxy(namespace, name, method, new HashMap<String, Object>(), true, false);

        ActionContext invocationContext = proxy.getInvocation().getInvocationContext();
        invocationContext.getContextMap().put(ServletActionContext.ACTION_MAPPING, mapping);
        invocationContext.setParameters(HttpParameters.create(request.getParameterMap()).build());
        // set the action context to the one used by the proxy
        ActionContext.setContext(invocationContext);

        // set the action context to the one used by the proxy
        ActionContext.setContext(invocationContext);

        // this is normally done in onSetUp(), but we are using Struts internal
        // objects (proxy and action invocation)
        // so we have to hack around so it works
		ServletActionContext.setServletContext(servletContext);
		ServletActionContext.setRequest(request);
		ServletActionContext.setResponse(response);

		return proxy;
	}

    @Override
    protected void initServletMockObjects() {
        servletContext = new MockServletContext(resourceLoader);
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        pageContext = new MockPageContext(servletContext, request, response);
        resourceLoader = new ConventionPluginResourceLoader();
    }
}
