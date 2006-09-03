/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.xslt;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.Settings;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.OgnlValueStack;
import com.opensymphony.xwork2.util.TextParseUtil;


/**
 * <!-- START SNIPPET: description -->
 *
 * XSLTResult uses XSLT to transform action object to XML. Recent version has 
 * been specifically modified to deal with Xalan flaws. When using Xalan you may
 * notice that even though you have very minimal stylesheet like this one
 * <pre>
 * &lt;xsl:template match="/result"&gt;
 *   &lt;result /&gt;
 * &lt;/xsl:template&gt;</pre>
 *
 * <p>
 * then Xalan would still iterate through every property of your action and it's
 * all descendants.
 * </p>
 *
 * <p>
 * If you had double-linked objects then Xalan would work forever analysing
 * infinite object tree. Even if your stylesheet was not constructed to process
 * them all. It's becouse current Xalan eagerly and extensively converts
 * everything to it's internal DTM model before further processing.
 * </p>
 * 
 * <p>
 * Thet's why there's a loop eliminator added that works by indexing every
 * object-property combination during processing. If it notices that some
 * object's property were already walked through, it doesn't get any deeper.
 * Say, you have two objects x and y with the following properties set
 * (pseudocode):
 * </p>
 * <pre>
 * x.y = y;
 * and
 * y.x = x;
 * action.x=x;</pre>
 *
 * <p>
 * Due to that modification the resulting XML document based on x would be:
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
 * Without it there would be an endless x/y/x/y/x/y/... elements.
 * </p>
 *
 * <p>
 * The XSLTResult code tries also to deal with the fact that DTM model is built
 * in a manner that childs are processed before siblings. The result is that if
 * there is object x that is both set in action's x property, and very deeply
 * under action's a property then it would only appear under a, not under x.
 * That's not what we expect, and that's why XSLTResult allows objects to repeat
 * in various places to some extent.
 * </p>
 *
 * <p>
 * Sometimes the object mesh is still very dense and you may notice that even
 * though you have relatively simple stylesheet execution takes a tremendous
 * amount of time. To help you to deal with that obstacle of Xalan you may
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
 *   &lt;html&gt;
 * &lt;xsl:template/&gt;
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
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>location (default)</b> - the location to go to after execution.</li>
 *
 * <li><b>parse</b> - true by default. If set to false, the location param will 
 * not be parsed for Ognl expressions.</li>
 *
 * <li><b>matchingPattern</b> - Pattern that matches only desired elements, by
 * default it matches everything.</li>
 *
 * <li><b>excludingPattern</b> - Pattern that eliminates unwanted elements, by
 * default it matches none.</li>
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
    private static final Log log = LogFactory.getLog(XSLTResult.class);
    public static final String DEFAULT_PARAM = "stylesheetLocation";

    protected boolean noCache;
    private final Map<String, Templates> templatesCache;
    private String stylesheetLocation;
    private boolean parse;
    private AdapterFactory adapterFactory;

    public XSLTResult() {
        templatesCache = new HashMap<String, Templates>();
        noCache = Settings.get("struts.xslt.nocache").trim().equalsIgnoreCase("true");
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
        System.out.println("location = " + location);
        this.stylesheetLocation = location;
    }

    public String getStylesheetLocation() {
        return stylesheetLocation;
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
            OgnlValueStack stack = ActionContext.getContext().getValueStack();
            location = TextParseUtil.translateVariables(location, stack);
        }

        try {
            HttpServletResponse response = ServletActionContext.getResponse();

            Writer writer = response.getWriter();

            // Create a transformer for the stylesheet.
            Templates templates = null;
            Transformer transformer;
            if (location != null) {
                templates = getTemplates(location);
                transformer = templates.newTransformer();
            } else
                transformer = TransformerFactory.newInstance().newTransformer();

            transformer.setURIResolver(getURIResolver());

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

            Source xmlSource = getDOMSourceForStack(invocation.getAction());

            // Transform the source XML to System.out.
            PrintWriter out = response.getWriter();

            log.debug("xmlSource = " + xmlSource);
            transformer.transform(xmlSource, new StreamResult(out));

            out.close(); // ...and flush...

            if (log.isDebugEnabled()) {
                log.debug("Time:" + (System.currentTimeMillis() - startTime) + "ms");
            }

            writer.flush();
        } catch (Exception e) {
            log.error("Unable to render XSLT Template, '" + location + "'", e);
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

    protected Templates getTemplates(String path) throws TransformerException, IOException {
        String pathFromRequest = ServletActionContext.getRequest().getParameter("xslt.location");

        if (pathFromRequest != null)
            path = pathFromRequest;

        if (path == null)
            throw new TransformerException("Stylesheet path is null");

        Templates templates = templatesCache.get(path);

        if (noCache || (templates == null)) {
            synchronized (templatesCache) {
                URL resource = ServletActionContext.getServletContext().getResource(path);

                if (resource == null) {
                    throw new TransformerException("Stylesheet " + path + " not found in resources.");
                }

                log.debug("Preparing XSLT stylesheet templates: " + path);

                TransformerFactory factory = TransformerFactory.newInstance();
                templates = factory.newTemplates(new StreamSource(resource.openStream()));
                templatesCache.put(path, templates);
            }
        }

        return templates;
    }

    protected Source getDOMSourceForStack(Object action)
            throws IllegalAccessException, InstantiationException {
        return new DOMSource(getAdapterFactory().adaptDocument("result", action) );
	}
}
