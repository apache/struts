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

package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * A tag that creates a HTML &lt;a &gt;.This tag supports the same attributes as the "url" tag,
 * including nested parameters using the "param" tag.<p/>
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * <p/> <b>Examples</b>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;s:a id="link1" theme="ajax" href="/DoIt.action"&gt;
 *     &lt;img border="none" src="&lt;%=request.getContextPath()%&gt;/images/delete.gif"/&gt;
 *     &lt;s:param name="id" value="1"/&gt;
 * &lt;/s:a&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 */
@StrutsTag(
        name = "a",
        tldTagClass = "org.apache.struts2.views.jsp.ui.AnchorTag",
        description = "Render a HTML href element that when clicked can optionally call a URL via remote XMLHttpRequest and updates its targets",
        allowDynamicAttributes = true)
public class Anchor extends ClosingUIBean {
    private static final Logger LOG = LoggerFactory.getLogger(Anchor.class);

    public static final String OPEN_TEMPLATE = "a";
    public static final String TEMPLATE = "a-close";
    public static final String COMPONENT_NAME = Anchor.class.getName();

    protected String href;
    protected UrlProvider urlProvider;
    protected UrlRenderer urlRenderer;
    protected boolean processingTagBody = false;
    
    //these params are passed by the Param tag
    protected Map urlParameters = new LinkedHashMap();

    public Anchor(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        urlProvider = new ComponentUrlProvider(this, this.urlParameters);
        urlProvider.setHttpServletRequest(request);
        urlProvider.setHttpServletResponse(response);
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public boolean usesBody() {
        return true;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (href != null)
            addParameter("href", ensureAttributeSafelyNotEscaped(findString(href)));
        else {
            //no href, build it from URL attributes
            StringWriter sw = new StringWriter();
            urlRenderer.beforeRenderUrl(urlProvider);
            urlRenderer.renderUrl(sw, urlProvider);
            String builtHref = sw.toString();
            if (StringUtils.isNotEmpty(builtHref))
                addParameter("href", ensureAttributeSafelyNotEscaped(builtHref));
        }
    }

    @Inject(StrutsConstants.STRUTS_URL_INCLUDEPARAMS)
    public void setUrlIncludeParams(String urlIncludeParams) {
       urlProvider.setUrlIncludeParams(urlIncludeParams);
    }

    @Inject
	public void setUrlRenderer(UrlRenderer urlRenderer) {
		urlProvider.setUrlRenderer(urlRenderer);
        this.urlRenderer = urlRenderer;
	}

    @Inject(required=false)
    public void setExtraParameterProvider(ExtraParameterProvider provider) {
        urlProvider.setExtraParameterProvider(provider);
    }

    @Override
    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        this.processingTagBody = true;
        return result;
    }

    /**
     * Overrides to be able to render body in a template rather than always before the template
     */
    public boolean end(Writer writer, String body) {
        this.processingTagBody = false;
        evaluateParams();
        try {
            addParameter("body", body);
            mergeTemplate(writer, buildTemplateName(template, getDefaultTemplate()));
        } catch (Exception e) {
            LOG.error("error when rendering", e);
        }
        finally {
            popComponentStack();
        }

        return false;
    }


    public void addParameter(String key, Object value) {
        /*
          the parameters added by this method are used in the template. this method is also
          called by Param to add params into ancestestor. This tag needs to keep both set of parameters
          separated (You gotta keep 'em separated!)
         */
        if (processingTagBody) {
            this.urlParameters.put(key, value);
        } else
            super.addParameter(key, value);
    }

    @Override
    public void addAllParameters(Map params) {
        /*
          the parameters added by this method are used in the template. this method is also
          called by Param to add params into ancestestor. This tag needs to keep both set of parameters
          separated (You gotta keep 'em separated!)
         */
        if (processingTagBody) {
            this.urlParameters.putAll(params);
        } else
            super.addAllParameters(params);
    }

    public UrlProvider getUrlProvider() {
        return urlProvider;
    }

    @StrutsTagAttribute(description = "The URL.")
    public void setHref(String href) {
        this.href = href;
    }

    @StrutsTagAttribute(description = "The includeParams attribute may have the value 'none', 'get' or 'all'", defaultValue = "none")
    public void setIncludeParams(String includeParams) {
        urlProvider.setIncludeParams(includeParams);
    }

    @StrutsTagAttribute(description = "Set scheme attribute")
    public void setScheme(String scheme) {
        urlProvider.setScheme(scheme);
    }

    @StrutsTagAttribute(description = "The target value to use, if not using action")
    public void setValue(String value) {
        urlProvider.setValue(value);
    }

    @StrutsTagAttribute(description = "The action to generate the URL for, if not using value")
    public void setAction(String action) {
        urlProvider.setAction(action);
    }

    @StrutsTagAttribute(description = "The namespace to use")
    public void setNamespace(String namespace) {
        urlProvider.setNamespace(namespace);
    }

    @StrutsTagAttribute(description = "The method of action to use")
    public void setMethod(String method) {
        urlProvider.setMethod(method);
    }

    @StrutsTagAttribute(description = "Whether to encode parameters", type = "Boolean", defaultValue = "true")
    public void setEncode(boolean encode) {
        urlProvider.setEncode(encode);
    }

    @StrutsTagAttribute(description = "Whether actual context should be included in URL", type = "Boolean", defaultValue = "true")
    public void setIncludeContext(boolean includeContext) {
        urlProvider.setIncludeContext(includeContext);
    }

    @StrutsTagAttribute(description = "The resulting portlet mode")
    public void setPortletMode(String portletMode) {
        urlProvider.setPortletMode(portletMode);
    }

    @StrutsTagAttribute(description = "The resulting portlet window state")
    public void setWindowState(String windowState) {
        urlProvider.setWindowState(windowState);
    }

    @StrutsTagAttribute(description = "Specifies if this should be a portlet render or action URL. Default is \"render\". To create an action URL, use \"action\".")
    public void setPortletUrlType(String portletUrlType) {
        urlProvider.setPortletUrlType(portletUrlType);
    }

    @StrutsTagAttribute(description = "The anchor for this URL")
    public void setAnchor(String anchor) {
        urlProvider.setAnchor(anchor);
    }

    @StrutsTagAttribute(description = "Specifies whether to escape ampersand (&amp;) to (&amp;amp;) or not", type = "Boolean", defaultValue = "true")
    public void setEscapeAmp(boolean escapeAmp) {
        urlProvider.setEscapeAmp(escapeAmp);
    }

    @StrutsTagAttribute(description = "Specifies whether to force the addition of scheme, host and port or not", type = "Boolean", defaultValue = "false")
    public void setForceAddSchemeHostAndPort(boolean forceAddSchemeHostAndPort) {
        urlProvider.setForceAddSchemeHostAndPort(forceAddSchemeHostAndPort);
    }
}
