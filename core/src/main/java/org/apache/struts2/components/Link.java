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
 * Add nonce propagation feature to implement CSP in link tags
 * </p>
 *
 * <p>
 * The link tag allows the user to load external resources, most usually style sheets. External resources
 * can inject malicious code and perform XSS and data injection attacks. The s:link tag includes a nonce
 * attribute that is being randomly generated with each request and only allows links with the valid
 * nonce value to be executed.
 * </p>
 *
 * <p><b>Examples</b></p>
 *
 * <pre>
 *
 * &lt;s:link ... /&gt;
 *
 * </pre>
 *
 */
@StrutsTag(name="link",
        tldTagClass="org.apache.struts2.views.jsp.ui.LinkTag",
        description="Link tag automatically adds nonces to link elements - should be used in combination with Struts' CSP Interceptor.",
        allowDynamicAttributes=true)
public class Link extends UIBean{

    private static final String TEMPLATE="link";

    protected String href;
    protected String hreflang;
    protected String rel;
    protected String media;
    protected String referrerpolicy;
    protected String sizes;
    protected String crossorigin;
    protected String type;
    protected String as;

    public Link(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @StrutsTagAttribute(description="HTML link href attribute")
    public void setHref(String href) {
        this.href = href;
    }

    @StrutsTagAttribute(description="HTML link hreflang attribute")
    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    @StrutsTagAttribute(description="HTML link rel attribute")
    public void setRel(String rel) {
        this.rel = rel;
    }

    @StrutsTagAttribute(description="HTML link sizes attribute")
    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    @StrutsTagAttribute(description="HTML link crossorigin attribute")
    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    @StrutsTagAttribute(description="HTML link type attribute")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="HTML link as attribute")
    public void setAs(String as) {
        this.as = as;
    }

    @StrutsTagAttribute(description="HTML link media attribute")
    public void setMedia(String media) {
        this.media = media;
    }

    @StrutsTagAttribute(description="HTML link referrerpolicy attribute")
    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (href != null) {
            addParameter("href", findString(href));
        }

        if (hreflang != null) {
            addParameter("hreflang", findString(hreflang));
        }

        if (rel != null) {
            addParameter("rel", findString(rel));
        }

        if (media != null) {
            addParameter("media", findString(media));
        }

        if (referrerpolicy != null) {
            addParameter("referrerpolicy", findString(referrerpolicy));
        }

        if (sizes != null) {
            addParameter("sizes", findString(sizes));
        }

        if (crossorigin != null) {
            addParameter("crossorigin", findString(crossorigin));
        }

        if (type != null) {
            addParameter("type", findString(type));
        }

        if (as != null) {
            addParameter("as", findString(as));
        }

        if (disabled != null) {
            addParameter("disabled", findString(disabled));
        }

        if (title != null) {
            addParameter("title", findString(title));
        }
    }
}
