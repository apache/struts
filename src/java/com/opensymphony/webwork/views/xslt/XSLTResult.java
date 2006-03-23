/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


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
 * <code>webwork.properties</code> related configuration:
 * </p>
 * <ul>
 *
 * <li><b>webwork.xslt.nocache</b> - Defaults to false. If set to true, disables
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
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 * @author Mike Mosiewicz
 * @author Rainer Hermanns
 */
public class XSLTResult implements Result {

    private static final Log log = LogFactory.getLog(XSLTResult.class);
    public static final String DEFAULT_PARAM = "location";

    private static final Object LOCK = new Object(); // lock in synchronized code

    protected boolean noCache = false;
    protected Map templatesCache;
    protected String location;
    protected boolean parse;
    protected Pattern matchingPattern = null;
    protected Pattern excludingPattern = null;

    public XSLTResult() {
        templatesCache = new HashMap();
        noCache = Configuration.getString(WebWorkConstants.WEBWORK_XSLT_NOCACHE).trim().equalsIgnoreCase("true");
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMatchingPattern(String matchingPattern) {
        this.matchingPattern = Pattern.compile(matchingPattern);
    }

    public void setExcludingPattern(String excludingPattern) {
        this.excludingPattern = Pattern.compile(excludingPattern);
    }

    public void setParse(boolean parse) {
        this.parse = parse;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        long startTime = -1;

        if (log.isDebugEnabled()) {
            startTime = System.currentTimeMillis();
        }

        if (parse) {
            OgnlValueStack stack = ActionContext.getContext().getValueStack();
            location = TextParseUtil.translateVariables(location, stack);
        }

        if (location == null || location.trim().length() == 0) {
            String msg = "Location paramter is empty. " +
                "Check the <param name=\"location\"> tag specified for this action.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            HttpServletResponse response = ServletActionContext.getResponse();

            Writer writer = response.getWriter();

            // Create a transformer for the stylesheet.
            Templates templates = getTemplates(location);
            Transformer transformer = templates.newTransformer();

            String mimeType = templates.getOutputProperties().getProperty(OutputKeys.MEDIA_TYPE);

            if (mimeType == null) {
                // guess (this is a servlet, so text/html might be the best guess)
                log.debug("Not possible to determine MineType from media-type, using text/html then");
                mimeType = "text/html";
            }

            response.setContentType(mimeType);

            Source xmlSource = getTraxSourceForStack(invocation.getAction());

            // Transform the source XML to System.out.
            PrintWriter out = response.getWriter();
            try {
                transformer.transform(xmlSource, new StreamResult(out));
            } finally {
                out.close(); // ...and flush...
            }

            if (log.isDebugEnabled()) {
                log.debug("Time: " + (System.currentTimeMillis() - startTime) + "ms");
            }

            writer.flush();
        } catch (Exception e) {
            log.error("Unable to render XSLT Template with location=[" + location + "]", e);
            throw e;
        }
    }

    private Templates getTemplates(String path) throws TransformerException, IOException {
        String pathFromRequest = ServletActionContext.getRequest().getParameter("xslt.location");

        if (pathFromRequest != null) {
            path = pathFromRequest;
        }

        if (path == null) {
            throw new TransformerException("Stylesheet path is null");
        }

        Templates templates = (Templates) templatesCache.get(path);

        if (noCache || (templates == null)) {
            synchronized (LOCK) {
                URL resource = ServletActionContext.getServletContext().getResource(path);

                if (resource == null) {
                    throw new TransformerException("Stylesheet " + path + " not found in resources.");
                }

                if (log.isDebugEnabled()) {
                    // This may result in the template being put into the cache multiple times
                    // if concurrent requests are made, but that's ok.
                    log.debug("Preparing new XSLT stylesheet: " + path);
                }

                TransformerFactory factory = TransformerFactory.newInstance();
                log.trace("Uri-Resolver is: " + factory.getURIResolver());
                factory.setURIResolver(new ServletURIResolver(ServletActionContext.getServletContext()));
                log.trace("Uri-Resolver is: " + factory.getURIResolver());
                templates = factory.newTemplates(new StreamSource(resource.openStream()));
                templatesCache.put(path, templates);
            }
        }

        return templates;
    }

    protected Source getTraxSourceForStack(Object action) throws IllegalAccessException, InstantiationException {
        DOMAdapter adapter = new DOMAdapter();
        adapter.setPattern(matchingPattern, excludingPattern);
        return new DOMSource(adapter.adapt(action));
    }
}
