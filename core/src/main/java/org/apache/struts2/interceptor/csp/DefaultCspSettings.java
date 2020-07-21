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
import com.opensymphony.xwork2.util.ValueStack;

import javax.accessibility.AccessibleIcon;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;


public class DefaultCspSettings implements CspSettings {

    private static final int NONCE_LENGTH = 18;
    private String reportUri = "cspviolation";
    //TODO add string constants for csp header to avoid doing string operations each time

    public void addCspHeaders(HttpServletResponse response) {
        createNonce();
        response.setHeader(CSP_HEADER, getPolicyString());
    }

    public void setReportUri(String reportUri) {
        this.reportUri = reportUri;
    }

    public String getNonceString() {
        Map<String, Object> session = ActionContext.getContext().getSession();
        return (String) session.get("nonce");
    }

    protected void createNonce() {
        String nonceValue = Base64.getUrlEncoder().encodeToString(getRandomBytes(NONCE_LENGTH));
        Map<String, Object> session = ActionContext.getContext().getSession();
        session.put("nonce", nonceValue);
    }

    private String getPolicyString() {
        //TODO add reportURI
        return String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s';",
                OBJECT_SRC, NONE,
                SCRIPT_SRC, getNonceString(), STRICT_DYNAMIC, HTTP, HTTPS,
                BASE_URI, NONE
        );
    }

    private byte[] getRandomBytes(int length)
    {
        SecureRandom sRand  = new SecureRandom();
        byte[] ret = new byte[length];
        sRand.nextBytes(ret);
        return ret;
    }
}
