/*
 * $Id: ServletConfigInterceptor.java 471756 2006-11-06 15:01:43Z husted $
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
package org.apache.struts2.portlet.interceptor;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.portlet.context.PortletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;


/**
 * <!-- START SNIPPET: description -->
 *
 * An interceptor which provides an implementation of PortletPreferences if the Action implements
 * PortletPreferencesAware.
 * 
 * If running in a servlet environment, a testing implementation of PortletPreferences will be
 * created and provided, but it should not be used in a production environment.
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
 *     &lt;interceptor-ref name="portlet-preferences"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see PortletPreferencesAware
 */
public class PortletPreferencesInterceptor extends AbstractInterceptor implements StrutsStatics {

    private static final Log LOG = LogFactory.getLog(PortletPreferencesInterceptor.class);
    
    public String intercept(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        final ActionContext context = invocation.getInvocationContext();

        if (action instanceof PortletPreferencesAware) {
            PortletRequest request = PortletActionContext.getRequest();
            PortletPreferencesAware awareAction = (PortletPreferencesAware) action;
            
            // Check if running in a servlet environment
            if (request == null) {
                LOG.warn("This portlet preferences implementation should only be used during development");
                awareAction.setPortletPreferences(new ServletPortletPreferences(ActionContext.getContext().getSession()));
            } else {
                awareAction.setPortletPreferences(request.getPreferences());
            }
        }
        return invocation.invoke();
    }
}
