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

package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * <!-- START SNIPPET: description -->
 *
 * A result that send the content out as plain text. Useful typically when needed
 * to display the raw content of a JSP or Html file for example.
 *
 * <!-- END SNIPPET: description -->
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *  <li>location (default) = location of the file (jsp/html) to be displayed as plain text.</li>
 *  <li>charSet (optional) = character set to be used. This character set will be used to set the
 *  response type (eg. Content-Type=text/plain; charset=UTF-8) and when reading
 *  using a Reader. Some example of charSet would be UTF-8, ISO-8859-1 etc.
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;action name="displayJspRawContent" &gt;
 *   &lt;result type="plainText"&gt;/myJspFile.jsp&lt;/result&gt;
 * &lt;/action&gt;
 *
 *
 * &lt;action name="displayJspRawContent" &gt;
 *   &lt;result type="plainText"&gt;
 *      &lt;param name="location"&gt;/myJspFile.jsp&lt;/param&gt;
 *      &lt;param name="charSet"&gt;UTF-8&lt;/param&gt;
 *   &lt;/result&gt;
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
public class PlainTextResult extends StrutsResultSupport {

    public static final int BUFFER_SIZE = 1024;

    private static final Logger LOG = LoggerFactory.getLogger(PlainTextResult.class);

    private static final long serialVersionUID = 3633371605905583950L;

    private String charSet;

    public PlainTextResult() {
        super();
    }

    public PlainTextResult(String location) {
        super(location);
    }

    /**
     * Set the character set
     *
     * @return The character set
     */
    public String getCharSet() {
        return charSet;
    }

    /**
     * Set the character set
     *
     * @param charSet The character set
     */
    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.StrutsResultSupport#doExecute(java.lang.String, com.opensymphony.xwork2.ActionInvocation)
     */
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        // verify charset
        Charset charset = readCharset();

        HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);

        applyCharset(charset, response);
        applyAdditionalHeaders(response);
        String location = adjustLocation(finalLocation);

        PrintWriter writer = response.getWriter();
        InputStreamReader reader = null;
        try {
            InputStream resourceAsStream = readStream(invocation, location);
            logWrongStream(finalLocation, resourceAsStream);
            if (charset != null) {
                reader = new InputStreamReader(resourceAsStream, charset);
            } else {
                reader = new InputStreamReader(resourceAsStream);
            }
            if (resourceAsStream != null) {
                sendStream(writer, reader);
            }
        } finally {
            if (reader != null)
                reader.close();
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    protected InputStream readStream(ActionInvocation invocation, String location) {
        ServletContext servletContext = (ServletContext) invocation.getInvocationContext().get(SERVLET_CONTEXT);
        return servletContext.getResourceAsStream(location);
    }

    protected void logWrongStream(String finalLocation, InputStream resourceAsStream) {
        if (resourceAsStream == null) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Resource at location [" + finalLocation + "] cannot be obtained (return null) from ServletContext !!! ");
            }
        }
    }

    protected void sendStream(PrintWriter writer, InputStreamReader reader) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int charRead;
        while((charRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, charRead);
        }
    }

    protected String adjustLocation(String location) {
        if (location.charAt(0) != '/') {
            return "/" + location;
        }
        return location;
    }

    protected void applyAdditionalHeaders(HttpServletResponse response) {
        response.setHeader("Content-Disposition", "inline");
    }

    protected void applyCharset(Charset charset, HttpServletResponse response) {
        if (charset != null) {
            response.setContentType("text/plain; charset=" + charSet);
        } else {
            response.setContentType("text/plain");
        }
    }

    protected Charset readCharset() {
        Charset charset = null;
        if (charSet != null) {
            if (Charset.isSupported(charSet)) {
                charset = Charset.forName(charSet);
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("charset [" + charSet + "] is not recognized ");
                }
                charset = null;
            }
        }
        return charset;
    }

}
