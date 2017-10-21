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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;

import javax.servlet.http.HttpSession;

/**
 * <!-- START SNIPPET: description -->
 *
 * <p>
 * This interceptor creates the HttpSession if it doesn't exist, also SessionMap is recreated and put in ServletActionContext.
 * </p>
 *
 * <p>
 * This is particular useful when using the &lt;@s.token&gt; tag in freemarker templates.
 * The tag <b>do</b> require that a HttpSession is already created since freemarker commits
 * the response to the client immediately.
 * </p>
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Interceptor parameters:</u></p>
 *
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
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="createSession"/&gt;
 *     &lt;interceptor-ref name="defaultStack"/&gt;
 *     &lt;result name="input"&gt;input_with_token_tag.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class CreateSessionInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -4590322556118858869L;

    private static final Logger LOG = LogManager.getLogger(CreateSessionInterceptor.class);


    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpSession httpSession = ServletActionContext.getRequest().getSession(false);
        if (httpSession == null) {
            LOG.debug("Creating new HttpSession and new SessionMap in ServletActionContext");
            ServletActionContext.getRequest().getSession(true);
            ServletActionContext.getContext().setSession(new SessionMap<String, Object>(ServletActionContext.getRequest()));
        }
        return invocation.invoke();
    }

}
