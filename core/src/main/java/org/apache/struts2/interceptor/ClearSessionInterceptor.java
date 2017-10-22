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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * This interceptor clears the HttpSession.
 * </p>
 *
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Interceptor parameters:</u></p>
 *
 * <!-- START SNIPPET: extending -->
 *
 * <ul>
 *  <li>None</li>
 * </ul>
 *
 * <!-- END SNIPPET: extending -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 * <li>None</li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p><b>Example:</b></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;action name="exampleAction" class="com.examples.ExampleAction"&gt;
 *     &lt;interceptor-ref name="clearSession"/&gt;
 *     &lt;interceptor-ref name="defaultStack"/&gt;
 *     &lt;result name="success"&gt;example.jsp&lt;/result&gt;
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class ClearSessionInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -2102199238428329238L;

    private static final Logger LOG = LogManager.getLogger(ClearSessionInterceptor.class);

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.debug("Clearing HttpSession");

        ActionContext ac = invocation.getInvocationContext();
        Map session = ac.getSession();
 
        if (null != session) {
            session.clear();
        }
        return invocation.invoke();
    }
}
