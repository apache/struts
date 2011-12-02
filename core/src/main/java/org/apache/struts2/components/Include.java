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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.FastByteArrayOutputStream;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Include a servlet's output (result of servlet or a JSP page).</p>
 * <p>Note: Any additional params supplied to the included page are <b>not</b>
 * accessible within the rendered page through the &lt;s:property...&gt; tag
 * since no valuestack will be created. You can, however, access them in a
 * servlet via the HttpServletRequest object or from a JSP page via
 * a scriptlet.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 *      <li>value* (String) - jsp page to be included</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;-- One: --&gt;
 * &lt;s:include value="myJsp.jsp" /&gt;
 *
 * &lt;-- Two: --&gt;
 * &lt;s:include value="myJsp.jsp"&gt;
 *    &lt;s:param name="param1" value="value2" /&gt;
 *    &lt;s:param name="param2" value="value2" /&gt;
 * &lt;/s:include&gt;
 *
 * &lt;-- Three: --&gt;
 * &lt;s:include value="myJsp.jsp"&gt;
 *    &lt;s:param name="param1"&gt;value1&lt;/s:param&gt;
 *    &lt;s:param name="param2"&gt;value2&lt;/s:param&gt;
 * &lt;/s:include&gt;
 * <!-- END SNIPPET: example -->
 *
 * <!-- START SNIPPET: exampledescription -->
 * Example one - do an include myJsp.jsp page
 * Example two - do an include to myJsp.jsp page with parameters param1=value1 and param2=value2
 * Example three - do an include to myJsp.jsp page with parameters param1=value1 and param2=value2
 * <!-- END SNIPPET: exampledescription -->
 * </pre>
 *
 */
@StrutsTag(name="include", tldTagClass="org.apache.struts2.views.jsp.IncludeTag", description="Include a servlet's output " +
                "(result of servlet or a JSP page)")
public class Include extends Component {

    private static final Logger LOG = LoggerFactory.getLogger(Include.class);

    private static String systemEncoding = System.getProperty("file.encoding");

    protected String value;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private static String defaultEncoding;

    public Include(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String encoding) {
        defaultEncoding = encoding;
    }

    public boolean end(Writer writer, String body) {
        String page = findString(value, "value", "You must specify the URL to include. Example: /foo.jsp");
        StringBuilder urlBuf = new StringBuilder();

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
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("unable to url-encode "+values.get(i).toString()+", it will be ignored");
                        }
                    }

                    concat = "&";
                }
            }
        }

        String result = urlBuf.toString();

        // Include
        try {
            include(result, writer, req, res, defaultEncoding);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Exception thrown during include of " + result, e);
            }
        }

        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="The jsp/servlet output to include", required=true)
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

            StringBuilder flatPathBuffer = new StringBuilder();

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

    /**
     * @deprecated use {@link #include(String, java.io.Writer, javax.servlet.ServletRequest,
     *             javax.servlet.http.HttpServletResponse, String)} instead with correct encoding specified
     */
    public static void include( String relativePath, Writer writer, ServletRequest request,
                                HttpServletResponse response ) throws ServletException, IOException {
        include(relativePath, writer, request, response, null);
    }

    /**
     * Include a resource in a response.
     *
     * @param relativePath the relative path of the resource to include; resolves to {@link #getContextRelativePath(javax.servlet.ServletRequest,
     *                     String)}
     * @param writer       the Writer to write output to
     * @param request      the current request
     * @param response     the response to write to
     * @param encoding     the file encoding to use for including the resource; if <tt>null</tt>, it will default to the
     *                     platform encoding
     *
     * @throws ServletException
     * @throws IOException
     */
    public static void include( String relativePath, Writer writer, ServletRequest request,
                                HttpServletResponse response, String encoding ) throws ServletException, IOException {
        String resourcePath = getContextRelativePath(request, relativePath);
        RequestDispatcher rd = request.getRequestDispatcher(resourcePath);

        if (rd == null) {
            throw new ServletException("Not a valid resource path:" + resourcePath);
        }

        PageResponse pageResponse = new PageResponse(response);

        // Include the resource
        rd.include(request, pageResponse);

        if (encoding != null) {
            // Use given encoding
            pageResponse.getContent().writeTo(writer, encoding);
        } else {
            //use the platform specific encoding
            pageResponse.getContent().writeTo(writer, systemEncoding);
        }
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
