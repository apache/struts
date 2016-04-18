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

package org.apache.struts2.views.xslt;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * <!-- START SNIPPET: description -->
 *
 * XSLTResult uses XSLT to transform an action object to XML. The recent version
 * has been specifically modified to deal with Xalan flaws. When using Xalan you
 * may notice that even though you have a very minimal stylesheet like this one
 * <pre>
 * &lt;xsl:template match="/result"&gt;
 *   &lt;result/&gt;
 * &lt;/xsl:template&gt;</pre>
 *
 * <p>
 * Xalan would still iterate through every property of your action and all
 * its descendants.
 * </p>
 *
 * <p>
 * If you had double-linked objects, Xalan would work forever analysing an
 * infinite object tree. Even if your stylesheet was not constructed to process
 * them all. It's because the current Xalan eagerly and extensively converts
 * everything to its internal DTM model before further processing.
 * </p>
 *
 * <p>
 * That's why there's a loop eliminator added that works by indexing every
 * object-property combination during processing. If it notices that some
 * object's property was already walked through, it doesn't go any deeper.
 * Say you have two objects, x and y, with the following properties set
 * (pseudocode):
 * </p>
 * <pre>
 * x.y = y;
 * and
 * y.x = x;
 * action.x=x;</pre>
 *
 * <p>
 * Due to that modification, the resulting XML document based on x would be:
 * </p>
 *
 * <pre>
 * &lt;result&gt;
 *   &lt;x&gt;
 *     &lt;y/&gt;
 *   &lt;/x&gt;
 * &lt;/result&gt;</pre>
 *
 * <p>
 * Without it there would be endless x/y/x/y/x/y/... elements.
 * </p>
 *
 * <p>
 * The XSLTResult code tries also to deal with the fact that DTM model is built
 * in a manner that children are processed before siblings. The result is that if
 * there is object x that is both set in action's x property, and very deeply
 * under action's a property then it would only appear under a, not under x.
 * That's not what we expect, and that's why XSLTResult allows objects to repeat
 * in various places to some extent.
 * </p>
 *
 * <p>
 * Sometimes the object mesh is still very dense and you may notice that even
 * though you have a relatively simple stylesheet, execution takes a tremendous
 * amount of time. To help you to deal with that obstacle of Xalan, you may
 * attach regexp filters to elements paths (xpath).
 * </p>
 *
 * <p>
 * <b>Note:</b> In your .xsl file the root match must be named <tt>result</tt>.
 * <br/>This example will output the username by using <tt>getUsername</tt> on your
 * action class:
 * <pre>
 * &lt;xsl:template match="result"&gt;
 *   &lt;html&gt;
 *   &lt;body&gt;
 *   Hello &lt;xsl:value-of select="username"/&gt; how are you?
 *   &lt;/body&gt;
 *   &lt;/html&gt;
 * &lt;/xsl:template&gt;
 * </pre>
 *
 * <p>
 * In the following example the XSLT result would only walk through action's
 * properties without their childs. It would also skip every property that has
 * "hugeCollection" in their name. Element's path is first compared to
 * excludingPattern - if it matches it's no longer processed. Then it is
 * compared to matchingPattern and processed only if there's a match.
 * </p>
 *
 * <!-- END SNIPPET: description -->
 *
 * <pre><!-- START SNIPPET: description.example -->
 * &lt;result name="success" type="xslt"&gt;
 *   &lt;param name="location"&gt;foo.xslt&lt;/param&gt;
 *   &lt;param name="matchingPattern"&gt;^/result/[^/*]$&lt;/param&gt;
 *   &lt;param name="excludingPattern"&gt;.*(hugeCollection).*&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: description.example --></pre>
 *
 * <p>
 * In the following example the XSLT result would use the action's user property
 * instead of the action as it's base document and walk through it's properties.
 * The exposedValue uses an ognl expression to derive it's value.
 * </p>
 *
 * <pre>
 * &lt;result name="success" type="xslt"&gt;
 *   &lt;param name="location"&gt;foo.xslt&lt;/param&gt;
 *   &lt;param name="exposedValue"&gt;user$&lt;/param&gt;
 * &lt;/result&gt;
 * </pre>
 * *
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>location (default)</b> - the location to go to after execution.</li>
 * <li><b>encoding</b> - character encoding used in XML, default UTF-8.</li>
 *
 * <li><b>parse</b> - true by default. If set to false, the location param will
 * not be parsed for Ognl expressions.</li>
 *
 * <!--
 * <li><b>matchingPattern</b> - Pattern that matches only desired elements, by
 * default it matches everything.</li>
 *
 * <li><b>excludingPattern</b> - Pattern that eliminates unwanted elements, by
 * default it matches none.</li>
 * -->
 *
 * </ul>
 *
 * <p>
 * <code>struts.properties</code> related configuration:
 * </p>
 * <ul>
 *
 * <li><b>struts.xslt.nocache</b> - Defaults to false. If set to true, disables
 * stylesheet caching. Good for development, bad for production.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre><!-- START SNIPPET: example -->
 * &lt;result name="success" type="xslt"&gt;foo.xslt&lt;/result&gt;
 * <!-- END SNIPPET: example --></pre>
 *
 */
public class XSLTResult implements Result {

    private static final long serialVersionUID = 6424691441777176763L;

    /** Log instance for this result. */
    private static final Logger LOG = LoggerFactory.getLogger(XSLTResult.class);

    /** 'stylesheetLocation' parameter.  Points to the xsl. */
    public static final String DEFAULT_PARAM = "stylesheetLocation";

    /** Cache of all tempaltes. */
    private static final Map<String, Templates> templatesCache;

    static {
        templatesCache = new HashMap<String, Templates>();
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

    /** Indicates the ognl expression respresenting the bean which is to be exposed as xml. */
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
    public void setNoCache(String val) {
        noCache = "true".equals(val);
    }

    /**
     * @deprecated Use #setStylesheetLocation(String)
     */
    public void setLocation(String location) {
        setStylesheetLocation(location);
    }

    public void setStylesheetLocation(String location) {
        if (location == null)
            throw new IllegalArgumentException("Null location");
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

    /**
     * @deprecated Since 2.1.1
     */
    public String getMatchingPattern() {
        return matchingPattern;
    }

    /**
     * @deprecated Since 2.1.1
     */
    public void setMatchingPattern(String matchingPattern) {
        this.matchingPattern = matchingPattern;
    }

    /**
     * @deprecated Since 2.1.1
     */
    public String getExcludingPattern() {
        return excludingPattern;
    }

    /**
     * @deprecated Since 2.1.1
     */
    public void setExcludingPattern(String excludingPattern) {
        this.excludingPattern = excludingPattern;
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
     * If true, parse the stylesheet location for OGNL expressions.
     *
     * @param parse
     */
    public void setParse(boolean parse) {
        this.parse = parse;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        long startTime = System.currentTimeMillis();
        String location = getStylesheetLocation();

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
            } else
                transformer = TransformerFactory.newInstance().newTransformer();

            transformer.setURIResolver(getURIResolver());
            transformer.setErrorListener(new ErrorListener() {

                public void error(TransformerException exception)
                        throws TransformerException {
                    throw new StrutsException("Error transforming result", exception);
                }

                public void fatalError(TransformerException exception)
                        throws TransformerException {
                    throw new StrutsException("Fatal error transforming result", exception);
                }

                public void warning(TransformerException exception)
                        throws TransformerException {
                    if (LOG.isWarnEnabled()) {
                	LOG.warn(exception.getMessage(), exception);
                    }
                }
                
            });

            String mimeType;
            if (templates == null)
                mimeType = "text/xml"; // no stylesheet, raw xml
            else
                mimeType = templates.getOutputProperties().getProperty(OutputKeys.MEDIA_TYPE);
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
            if (LOG.isDebugEnabled()) {
        	LOG.debug("xmlSource = " + xmlSource);
            }
            transformer.transform(xmlSource, new StreamResult(writer));

            writer.flush(); // ...and flush...

            if (LOG.isDebugEnabled()) {
                LOG.debug("Time:" + (System.currentTimeMillis() - startTime) + "ms");
            }

        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Unable to render XSLT Template, '#0'", e, location);
            }
            throw e;
        }
    }

    protected AdapterFactory getAdapterFactory() {
        if (adapterFactory == null)
            adapterFactory = new AdapterFactory();
        return adapterFactory;
    }

    protected void setAdapterFactory(AdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    /**
     * Get the URI Resolver to be called by the processor when it encounters an xsl:include, xsl:import, or document()
     * function. The default is an instance of ServletURIResolver, which operates relative to the servlet context.
     */
    protected URIResolver getURIResolver() {
        return new ServletURIResolver(
                ServletActionContext.getServletContext());
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

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Preparing XSLT stylesheet templates: " + path);
                }

                TransformerFactory factory = TransformerFactory.newInstance();
                factory.setURIResolver(getURIResolver());
                templates = factory.newTemplates(new StreamSource(resource.openStream()));
                templatesCache.put(path, templates);
            }
        }

        return templates;
    }

    protected Source getDOMSourceForStack(Object value)
            throws IllegalAccessException, InstantiationException {
        return new DOMSource(getAdapterFactory().adaptDocument("result", value) );
    }
}
