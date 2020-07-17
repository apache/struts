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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;


public class CspInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final Logger LOG = LoggerFactory.getLogger(CspInterceptor.class);

    private CspSettings settings = new DefaultCspSettings();

    public void setSettings(CspSettings settings) {
        this.settings = settings;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        // TODO : check content-type and uri for csp reports and logCspViolation()
//        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        invocation.addPreResultListener(this);
        return invocation.invoke();

    }

    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        settings.addCspHeaders(response);
    }

    private void logCspViolation() {

    }
}
