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
package org.apache.struts.action2.components;

import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.util.FastByteArrayOutputStream;
import org.apache.struts.action2.RequestUtils;
import org.apache.struts.action2.StrutsConstants;
import com.opensymphony.xwork.util.OgnlValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.*;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Include a servlet's output (result of servlet or a JSP page).</p>
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 * 		<li>value* (String) - jsp page to be included</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;-- One: --&gt;
 * &lt;a:include value="myJsp.jsp" /&gt;
 *
 * &lt;-- Two: --&gt;
 * &lt;a:include value="myJsp.jsp"&gt;
 *    &lt;a:param name="param1" value="value2" /&gt;
 *    &lt;a:param name="param2" value="value2" /&gt;
 * &lt;/a:include&gt;
 *
 * &lt;-- Three: --&gt;
 * &lt;a:include value="myJsp.jsp"&gt;
 *    &lt;a:param name="param1"&gt;value1&lt;/a:param&gt;
 *    &lt;a:param name="param2"&gt;value2&lt;a:param&gt;
 * &lt;/a:include&gt;
 * <!-- END SNIPPET: example -->
 *
 * <!-- START SNIPPET: exampledescription -->
 * Example one - do an include myJsp.jsp page
 * Example two - do an include to myJsp.jsp page with parameters param1=value1 and param2=value2
 * Example three - do an include to myJsp.jsp page with parameters param1=value1 and param2=value2
 * <!-- END SNIPPET: exampledescription -->
 * </pre>
 *
 * @author Rickard Oberg (rickard@dreambean.com)
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @author Rene Gielen
 * @author tm_jee
 * @version $Revision$
 * @since 2.2
 *
 * @a2.tag name="include" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.IncludeTag"
 * description="Include a servlet's output (result of servlet or a JSP page)"
 */
public class Include extends Component {

	private static final Log _log = LogFactory.getLog(Include.class);

    private static String encoding;
    private static boolean encodingDefined = true;

    protected String value;
    private HttpServletRequest req;
    private HttpServletResponse res;

    public Include(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
    }

    public boolean end(Writer writer, String body) {
        String page = findString(value, "value", "You must specify the URL to include. Example: /foo.jsp");
        StringBuffer urlBuf = new StringBuffer();

        // Add URL
        urlBuf.append(page);

        // Add request parameters
        if (parameters.size() > 0) {
            urlBuf.append('?');

            String concat = "";

            // Set parameters
            Iterator iter = parameters.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object name = entry.getKey();
                List values = (List) entry.getValue();

                for (int i = 0; i < values.size(); i++) {
                    urlBuf.append(concat);
                    urlBuf.append(name);
                    urlBuf.append('=');

                    try {
                        urlBuf.append(URLEncoder.encode(values.get(i).toString(), "UTF-8"));
                    } catch (Exception e) {
                    	_log.warn("unable to url-encode "+values.get(i).toString()+", it will be ignored");
                    }

                    concat = "&";
                }
            }
        }

        String result = urlBuf.toString();

        // Include
        try {
            include(result, writer, req, res);
        } catch (Exception e) {
            LogFactory.getLog(getClass()).warn("Exception thrown during include of " + result, e);
        }

        return super.end(writer, body);
    }

    /**
     * The jsp/servlet output to include
     * @a2.tagattribute required="true" type="String"
     */
    public void setValue(String value) {
        this.value = value;
    }

    public static String getContextRelativePath(ServletRequest request, String relativePath) {
        String returnValue;

        if (relativePath.startsWith("/")) {
            returnValue = relativePath;
        } else if (!(request instanceof HttpServletRequest)) {
            returnValue = relativePath;
        } else {
            HttpServletRequest hrequest = (HttpServletRequest) request;
            String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");

            if (uri == null) {
                uri = RequestUtils.getServletPath(hrequest);
            }

            returnValue = uri.substring(0, uri.lastIndexOf('/')) + '/' + relativePath;
        }

        // .. is illegal in an absolute path according to the Servlet Spec and will cause
        // known problems on Orion application servers.
        if (returnValue.indexOf("..") != -1) {
            Stack stack = new Stack();
            StringTokenizer pathParts = new StringTokenizer(returnValue.replace('\\', '/'), "/");

            while (pathParts.hasMoreTokens()) {
                String part = pathParts.nextToken();

                if (!part.equals(".")) {
                    if (part.equals("..")) {
                        stack.pop();
                    } else {
                        stack.push(part);
                    }
                }
            }

            StringBuffer flatPathBuffer = new StringBuffer();

            for (int i = 0; i < stack.size(); i++) {
                flatPathBuffer.append("/").append(stack.elementAt(i));
            }

            returnValue = flatPathBuffer.toString();
        }

        return returnValue;
    }

    public void addParameter(String key, Object value) {
        // don't use the default implementation of addParameter,
        // instead, include tag requires that each parameter be a list of objects,
        // just like the HTTP servlet interfaces are (String[]) 
        if (value != null) {
            List currentValues = (List) parameters.get(key);

            if (currentValues == null) {
                currentValues = new ArrayList();
                parameters.put(key, currentValues);
            }

            currentValues.add(value);
        }
    }

    public static void include(String aResult, Writer writer, ServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String resourcePath = getContextRelativePath(request, aResult);
        RequestDispatcher rd = request.getRequestDispatcher(resourcePath);

        if (rd == null) {
            throw new ServletException("Not a valid resource path:" + resourcePath);
        }

        PageResponse pageResponse = new PageResponse(response);

        // Include the resource
        rd.include((HttpServletRequest) request, pageResponse);

        //write the response back to the JspWriter, using the correct encoding.
        String encoding = getEncoding();

        if (encoding != null) {
            //use the encoding specified in the property file
            pageResponse.getContent().writeTo(writer, encoding);
        } else {
            //use the platform specific encoding
            pageResponse.getContent().writeTo(writer, null);
        }
    }

    /**
     * Get the encoding specified by the property 'struts.i18n.encoding' in struts.properties,
     * or return the default platform encoding if not specified.
     * <p/>
     * Note that if the property is not initially defined, this will return the system default,
     * even if the property is later defined.  This is mainly for performance reasons.  Undefined
     * properties throw exceptions, which are a costly operation.
     * <p/>
     * If the property is initially defined, it is read every time, until is is undefined, and then
     * the system default is used.
     * <p/>
     * Why not cache it completely?  Some applications will wish to be able to dynamically set the
     * encoding at runtime.
     *
     * @return The encoding to be used.
     */
    private static String getEncoding() {
        if (encodingDefined) {
            try {
                encoding = Configuration.getString(StrutsConstants.STRUTS_I18N_ENCODING);
            } catch (IllegalArgumentException e) {
                encoding = System.getProperty("file.encoding");
                encodingDefined = false;
            }
        }

        return encoding;
    }


    /**
     * Implementation of ServletOutputStream that stores all data written
     * to it in a temporary buffer accessible from {@link #getBuffer()} .
     *
     * @author <a href="joe@truemesh.com">Joe Walnes</a>
     * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
     */
    static final class PageOutputStream extends ServletOutputStream {

        private FastByteArrayOutputStream buffer;


        public PageOutputStream() {
            buffer = new FastByteArrayOutputStream();
        }


        /**
         * Return all data that has been written to this OutputStream.
         */
        public FastByteArrayOutputStream getBuffer() throws IOException {
            flush();

            return buffer;
        }

        public void close() throws IOException {
            buffer.close();
        }

        public void flush() throws IOException {
            buffer.flush();
        }

        public void write(byte[] b, int o, int l) throws IOException {
            buffer.write(b, o, l);
        }

        public void write(int i) throws IOException {
            buffer.write(i);
        }

        public void write(byte[] b) throws IOException {
            buffer.write(b);
        }
    }


    /**
     * Simple wrapper to HTTPServletResponse that will allow getWriter()
     * and getResponse() to be called as many times as needed without
     * causing conflicts.
     * <p/>
     * The underlying outputStream is a wrapper around
     * {@link PageOutputStream} which will store
     * the written content to a buffer.
     * <p/>
     * This buffer can later be retrieved by calling {@link #getContent}.
     *
     * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
     * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
     */
    static final class PageResponse extends HttpServletResponseWrapper {

        protected PrintWriter pagePrintWriter;
        protected ServletOutputStream outputStream;
        private PageOutputStream pageOutputStream = null;


        /**
         * Create PageResponse wrapped around an existing HttpServletResponse.
         */
        public PageResponse(HttpServletResponse response) {
            super(response);
        }


        /**
         * Return the content buffered inside the {@link PageOutputStream}.
         *
         * @return
         * @throws IOException
         */
        public FastByteArrayOutputStream getContent() throws IOException {
            //if we are using a writer, we need to flush the
            //data to the underlying outputstream.
            //most containers do this - but it seems Jetty 4.0.5 doesn't
            if (pagePrintWriter != null) {
                pagePrintWriter.flush();
            }

            return ((PageOutputStream) getOutputStream()).getBuffer();
        }

        /**
         * Return instance of {@link PageOutputStream}
         * allowing all data written to stream to be stored in temporary buffer.
         */
        public ServletOutputStream getOutputStream() throws IOException {
            if (pageOutputStream == null) {
                pageOutputStream = new PageOutputStream();
            }

            return pageOutputStream;
        }

        /**
         * Return PrintWriter wrapper around PageOutputStream.
         */
        public PrintWriter getWriter() throws IOException {
            if (pagePrintWriter == null) {
                pagePrintWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
            }

            return pagePrintWriter;
        }
    }
}
