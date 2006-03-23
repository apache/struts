/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.interceptor;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.webwork.util.ServletContextAware;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;


/**
 * <!-- START SNIPPET: description -->
 *
 * An interceptor which sets action properties based on the interfaces an action implements. For example, if the action
 * implements {@link ParameterAware} then the action context's parameter map will be set on it.
 *
 * <p/> This interceptor is designed to set all properties an action needs if it's aware of servlet parameters, the
 * servlet context, the session, etc. Interfaces that it supports are:
 *
 * <ul>
 *
 * <li>{@link ServletContextAware}</li>
 *
 * <li>{@link ServletRequestAware}</li>
 *
 * <li>{@link ServletResponseAware}</li>
 *
 * <li>{@link ParameterAware}</li>
 *
 * <li>{@link SessionAware}</li>
 *
 * <li>{@link ApplicationAware}</li>
 *
 * <li>{@link PrincipalAware}</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
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
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="servlet-config"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Bill Lynch (docs)
 * @see ServletContextAware
 * @see ServletRequestAware
 * @see ServletResponseAware
 * @see ParameterAware
 * @see SessionAware
 * @see ApplicationAware
 * @see PrincipalAware
 */
public class ServletConfigInterceptor extends AroundInterceptor implements WebWorkStatics {
    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    /**
     * Sets action properties based on the interfaces an action implements. Things like application properties,
     * parameters, session attributes, etc are set based on the implementing interface.
     *
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when setting action properties.
     */
    protected void before(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        final ActionContext context = invocation.getInvocationContext();

        if (action instanceof ServletRequestAware) {
            HttpServletRequest request = (HttpServletRequest) context.get(HTTP_REQUEST);
            ((ServletRequestAware) action).setServletRequest(request);
        }

        if (action instanceof ServletResponseAware) {
            HttpServletResponse response = (HttpServletResponse) context.get(HTTP_RESPONSE);
            ((ServletResponseAware) action).setServletResponse(response);
        }

        if (action instanceof ParameterAware) {
            ((ParameterAware) action).setParameters(context.getParameters());
        }

        if (action instanceof SessionAware) {
            ((SessionAware) action).setSession(context.getSession());
        }

        if (action instanceof ApplicationAware) {
            ((ApplicationAware) action).setApplication(context.getApplication());
        }

        if (action instanceof PrincipalAware) {
            HttpServletRequest request = (HttpServletRequest) context.get(HTTP_REQUEST);
            ((PrincipalAware) action).setPrincipalProxy(new PrincipalProxy(request));
        }
        if (action instanceof ServletContextAware) {
            ServletContext servletContext = (ServletContext) context.get(SERVLET_CONTEXT);
            ((ServletContextAware) action).setServletContext(servletContext);
        }
    }
}
