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
package org.apache.struts2.views.xslt;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * XSLTResult uses XSLT to transform an action object to XML.
 */
public class XSLTResult implements Result {

    private static final long serialVersionUID = 6424691441777176763L;

    /** Log instance for this result. */
    private static final Logger LOG = LogManager.getLogger(XSLTResult.class);

    /** 'stylesheetLocation' parameter.  Points to the xsl. */
    public static final String DEFAULT_PARAM = "stylesheetLocation";

    /**
     * Cache of all templates.
     */
    private static final Map<String, Templates> templatesCache;

    static {
        templatesCache = new HashMap<>();
    }

    // Configurable Parameters

    /** Determines whether or not the result should allow caching. */
    protected boolean noCache;

    /** Indicates the location of the xsl template. */
    private String stylesheetLocation;

    /** Indicates the property name patterns which should be exposed to the xml. */
    private String matchingPattern;

    /** Indicates the property name patterns which should be excluded from the xml. */
    private String excludingPattern;

    /** Indicates the ognl expression representing the bean which is to be exposed as xml. */
    private String exposedValue;

    /** Indicates the status to return in the response */
    private int status = 200;

    private String encoding = "UTF-8";

    private boolean parse;
    private AdapterFactory adapterFactory;

    public XSLTResult() {
    }

    public XSLTResult(String stylesheetLocation) {
        this();
        setStylesheetLocation(stylesheetLocation);
    }
    
    @Inject(StrutsConstants.STRUTS_XSLT_NOCACHE)
    public void setNoCache(String xsltNoCache) {
        this.noCache = BooleanUtils.toBoolean(xsltNoCache);
    }

    public void setStylesheetLocation(String location) {
        this.stylesheetLocation = location;
    }

    public String getStylesheetLocation() {
        return stylesheetLocation;
    }

    public String getExposedValue() {
        return exposedValue;
    }

    public void setExposedValue(String exposedValue) {
        this.exposedValue = exposedValue;
    }

    public String getStatus() {
        return String.valueOf(status);
    }

    public void setStatus(String status) {
        try {
            this.status = Integer.valueOf(status);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Status value not number " + e.getMessage(), e);
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @param parse if true, parse the stylesheet location for OGNL expressions.
     */
    public void setParse(boolean parse) {
        this.parse = parse;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        long startTime = System.currentTimeMillis();
        String location = getStylesheetLocation();

        if (location == null) {
            throw new IllegalArgumentException("Parameter 'stylesheetLocation' cannot be null!");
        }

        if (parse) {
            ValueStack stack = ActionContext.getContext().getValueStack();
            location = TextParseUtil.translateVariables(location, stack);
        }

        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.setStatus(status);
            response.setCharacterEncoding(encoding);
            PrintWriter writer = response.getWriter();

            // Create a transformer for the stylesheet.
            Templates templates = null;
            Transformer transformer;
            if (location != null) {
                templates = getTemplates(location);
                transformer = templates.newTransformer();
            } else {
                transformer = TransformerFactory.newInstance().newTransformer();
            }

            transformer.setURIResolver(getURIResolver());
            transformer.setErrorListener(buildErrorListener());

            String mimeType;
            if (templates == null) {
                mimeType = "text/xml"; // no stylesheet, raw xml
            } else {
                mimeType = templates.getOutputProperties().getProperty(OutputKeys.MEDIA_TYPE);
            }

            if (mimeType == null) {
                // guess (this is a servlet, so text/html might be the best guess)
                mimeType = "text/html";
            }

            response.setContentType(mimeType);

            Object result = invocation.getAction();
            if (exposedValue != null) {
                ValueStack stack = invocation.getStack();
                result = stack.findValue(exposedValue);
            }

            Source xmlSource = getDOMSourceForStack(result);

            // Transform the source XML to System.out.
            LOG.debug("xmlSource = {}", xmlSource);
            transformer.transform(xmlSource, new StreamResult(writer));

            writer.flush(); // ...and flush...

            LOG.debug("Time: {}ms", (System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            LOG.error("Unable to render XSLT Template, '{}'", location, e);
            throw e;
        }
    }

    protected ErrorListener buildErrorListener() {
        return new ErrorListener() {

            public void error(TransformerException exception) throws TransformerException {
                throw new StrutsException("Error transforming result", exception);
            }

            public void fatalError(TransformerException exception) throws TransformerException {
                throw new StrutsException("Fatal error transforming result", exception);
            }

            public void warning(TransformerException exception) throws TransformerException {
                LOG.warn(exception.getMessage(), exception);
            }

        };
    }

    protected AdapterFactory getAdapterFactory() {
        if (adapterFactory == null) {
            adapterFactory = new AdapterFactory();
        }
        return adapterFactory;
    }

    protected void setAdapterFactory(AdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    /**
     * @return the URI Resolver to be called by the processor when it encounters an xsl:include, xsl:import, or document()
     * function. The default is an instance of ServletURIResolver, which operates relative to the servlet context.
     */
    protected URIResolver getURIResolver() {
        return new ServletURIResolver(ServletActionContext.getServletContext());
    }

    protected Templates getTemplates(final String path) throws TransformerException, IOException {
        if (path == null)
            throw new TransformerException("Stylesheet path is null");

        Templates templates = templatesCache.get(path);

        if (noCache || (templates == null)) {
            synchronized (templatesCache) {
                URL resource = ServletActionContext.getServletContext().getResource(path);

                if (resource == null) {
                    throw new TransformerException("Stylesheet " + path + " not found in resources.");
                }

                LOG.debug("Preparing XSLT stylesheet templates: {}", path);

                TransformerFactory factory = TransformerFactory.newInstance();
                factory.setURIResolver(getURIResolver());
                factory.setErrorListener(buildErrorListener());
                templates = factory.newTemplates(new StreamSource(resource.openStream()));
                templatesCache.put(path, templates);
            }
        }

        return templates;
    }

    protected Source getDOMSourceForStack(Object value) throws IllegalAccessException, InstantiationException {
        return new DOMSource(getAdapterFactory().adaptDocument("result", value) );
    }
}
