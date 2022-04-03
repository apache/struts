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
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>
 * Add nonce propagation feature to implement CSP in script tags
 * </p>
 *
 * <p>
 * The script tag allows the user to execute JavaScript. It also allows external resources to execute
 * scripts which can be malicious. The s:script tag includes a nonce attribute that is being randomly
 * generated with each request and only allows scripts with the valid nonce value to be executed.
 * </p>
 *
 * <p><b>Examples</b></p>
 *
 * <pre>
 *
 * &lt;s:script ... /&gt;
 *
 * </pre>
 *
 */
@StrutsTag(name="script",
        tldTagClass="org.apache.struts2.views.jsp.ui.ScriptTag",
        description="Script tag automatically adds nonces to script blocks - should be used in combination with Struts' CSP Interceptor.",
        allowDynamicAttributes=true)
public class Script extends ClosingUIBean {

    protected String async;
    protected String charset;
    protected String defer;
    protected String src;
    protected String type;
    protected String referrerpolicy;
    protected String nomodule;
    protected String integrity;
    protected String crossorigin;

    private static final String TEMPLATE = "script-close";
    private static final String OPEN_TEMPLATE = "script";

    public Script(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @StrutsTagAttribute(description="HTML script async attribute")
    public void setAsync(String async) {
        this.async = async;
    }

    @StrutsTagAttribute(description="HTML script charset attribute")
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @StrutsTagAttribute(description="HTML script defer attribute")
    public void setDefer(String defer) {
        this.defer = defer;
    }

    @StrutsTagAttribute(description="HTML script src attribute")
    public void setSrc(String src) {
        this.src = src;
    }

    @StrutsTagAttribute(description="HTML script type attribute")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="HTML script referrerpolicy attribute")
    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    @StrutsTagAttribute(description="HTML script nomodule attribute")
    public void setNomodule(String nomodule) {
        this.nomodule = nomodule;
    }

    @StrutsTagAttribute(description="HTML script integrity attribute")
    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    @StrutsTagAttribute(description="HTML script crossorigin attribute")
    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    @Override
    public boolean usesBody() {
        return true;
    }
    
    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (async != null) {
            addParameter("async", findString(async));
        }

        if (charset != null) {
            addParameter("charset", findString(charset));
        }

        if (defer != null) {
            addParameter("defer", findString(defer));
        }

        if (src != null) {
            addParameter("src", findString(src));
        }

        if (type != null) {
            addParameter("type", findString(type));
        }

        if (referrerpolicy != null) {
            addParameter("referrerpolicy", findString(referrerpolicy));
        }

        if (nomodule != null) {
            addParameter("nomodule", findString(nomodule));
        }

        if (integrity != null) {
            addParameter("integrity", findString(integrity));
        }

        if (crossorigin != null) {
            addParameter("crossorigin", findString(crossorigin));
        }
    }

}
