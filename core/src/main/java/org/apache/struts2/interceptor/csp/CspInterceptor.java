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
package org.apache.struts2.interceptor.csp;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor that implements Content Security Policy on incoming requests used to protect against
 * common XSS and data injection attacks. Uses {@link CspSettings} to add appropriate Content Security Policy header
 * to the response. These headers determine what the browser will consider a policy violation and the browser's behavior
 * when a violation occurs. A detailed explanation of CSP can be found <a href="https://csp.withgoogle.com/docs/index.html">here</a>.
 *
 * @see <a href="https://csp.withgoogle.com/docs/index.html">https://csp.withgoogle.com/docs/index.html/</a>
 * @see CspSettings
 * @see DefaultCspSettings
 **/
public class CspInterceptor extends AbstractInterceptor implements PreResultListener {

    private boolean enforcingMode = false;
    private CspSettings settings = new DefaultCspSettings();
    private String path = "";

    public void setReportUri(String reportUri) {
        settings.setReportUri(path+reportUri);
    }

    public void setEnforcingMode(String value){
        enforcingMode = Boolean.parseBoolean(value);
        settings.setEnforcingMode(enforcingMode);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        path = invocation.getInvocationContext().getServletRequest().getContextPath();
        return invocation.invoke();
    }

    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        settings.addCspHeaders(response);
    }
}
