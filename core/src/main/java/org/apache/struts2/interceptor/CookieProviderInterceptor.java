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

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * <!-- START SNIPPET: description -->
 * Allows actions to send cookies to client, action must implement {@link CookieProvider}
 * You must reference this interceptor in your default stack or in action's stack, see example below.
 *
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 *
 * none
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 *
 * <ul>
 *     <li>addCookiesToResponse - this method applies cookie created by action to response</li>
 * </ul>
 *
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;action ... &gt;
 *   &lt;interceptor-ref name="defaultStack"/&gt;
 *   &lt;interceptor-ref name="cookieProvider"/&gt;
 *   ...
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class CookieProviderInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final Logger LOG = LoggerFactory.getLogger(CookieProviderInterceptor.class);

    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    /**
     * Do what name suggests
     *
     * @param action {@link CookieProvider} action
     * @param response current {@link HttpServletResponse}
     */
    protected void addCookiesToResponse(CookieProvider action, HttpServletResponse response) {
        Set<Cookie> cookies = action.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Sending cookie [#0] with value [#1] for domain [#2]",
                            cookie.getName(), cookie.getValue(), (cookie.getDomain() != null ? cookie.getDomain() : "no domain"));
                }
                response.addCookie(cookie);
            }
        }
    }

    public void beforeResult(ActionInvocation invocation, String resultCode) {
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("beforeResult start");
            }
            ActionContext ac = invocation.getInvocationContext();
            if (invocation.getAction() instanceof CookieProvider) {
                HttpServletResponse response = (HttpServletResponse) ac.get(StrutsStatics.HTTP_RESPONSE);
                addCookiesToResponse((CookieProvider) invocation.getAction(), response);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("beforeResult end");
            }
        } catch (Exception ex) {
            LOG.error("Unable to setup cookies", ex);
        }
    }

}
