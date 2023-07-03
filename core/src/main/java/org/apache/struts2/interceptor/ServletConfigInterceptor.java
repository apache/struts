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
package org.apache.struts2.interceptor;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.action.ApplicationAware;
import org.apache.struts2.action.ParametersAware;
import org.apache.struts2.action.PrincipalAware;
import org.apache.struts2.action.ServletContextAware;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.action.ServletResponseAware;
import org.apache.struts2.action.SessionAware;
import org.apache.struts2.interceptor.servlet.ServletPrincipalProxy;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * An interceptor which sets action properties based on the interfaces an action implements. For example, if the action
 * implements {@link ParameterAware} then the action context's parameter map will be set on it.
 * </p>
 *
 * <p>This interceptor is designed to set all properties an action needs if it's aware of servlet parameters, the
 * servlet context, the session, etc. Interfaces that it supports are:
 * </p>
 *
 * <ul>
 * <li>{@link ServletContextAware}</li>
 * <li>{@link ServletRequestAware}</li>
 * <li>{@link ServletResponseAware}</li>
 * <li>{@link ParametersAware}</li>
 * <li>{@link ServletRequestAware}</li>
 * <li>{@link SessionAware}</li>
 * <li>{@link ApplicationAware}</li>
 * <li>{@link PrincipalAware}</li>
 * </ul>
 *
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Interceptor parameters:</u></p>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Extending the interceptor:</u></p>
 *
 * <!-- START SNIPPET: extending -->
 *
 * <p>There are no known extension points for this interceptor.</p>
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p><u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="servletConfig"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see ServletContextAware
 * @see org.apache.struts2.action.ServletContextAware
 * @see ServletRequestAware
 * @see ServletResponseAware
 * @see ParametersAware
 * @see SessionAware
 * @see ApplicationAware
 * @see PrincipalAware
 */
public class ServletConfigInterceptor extends AbstractInterceptor implements StrutsStatics {

    private static final long serialVersionUID = 605261777858676638L;

    /**
     * Sets action properties based on the interfaces an action implements. Things like application properties,
     * parameters, session attributes, etc are set based on the implementing interface.
     *
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when setting action properties.
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        final ActionContext context = invocation.getInvocationContext();

        if (action instanceof ServletRequestAware) {
            HttpServletRequest request = context.getServletRequest();
            ((ServletRequestAware) action).withServletRequest(request);
        }

        if (action instanceof ServletResponseAware) {
            HttpServletResponse response = context.getServletResponse();
            ((ServletResponseAware) action).withServletResponse(response);
        }

        if (action instanceof ParametersAware) {
            ((ParametersAware) action).withParameters(context.getParameters());
        }

        if (action instanceof ApplicationAware) {
            ((ApplicationAware) action).withApplication(context.getApplication());
        }

        if (action instanceof SessionAware) {
            ((SessionAware) action).withSession(context.getSession());
        }

        if (action instanceof PrincipalAware) {
            HttpServletRequest request = context.getServletRequest();
            if(request != null) {
                // We are in servlet environment, so principal information resides in HttpServletRequest
                ((PrincipalAware) action).withPrincipalProxy(new ServletPrincipalProxy(request));
            }
        }

        if (action instanceof ServletContextAware) {
            ServletContext servletContext = context.getServletContext();
            ((ServletContextAware) action).withServletContext(servletContext);
        }

        return invocation.invoke();
    }
}
