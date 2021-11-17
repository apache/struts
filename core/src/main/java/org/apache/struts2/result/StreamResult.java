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
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.NotExcludedAcceptedPatternsChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A custom Result type for sending raw data (via an InputStream) directly to the
 * HttpServletResponse. Very useful for allowing users to download content.
 *
 * <b>This result type takes the following parameters:</b>
 *
 * <ul>
 * <li><b>contentType</b> - the stream mime-type as sent to the web browser
 * (default = <code>text/plain</code>).</li>
 * <li><b>contentLength</b> - the stream length in bytes (the browser displays a
 * progress bar).</li>
 * <li><b>contentDisposition</b> - the content disposition header value for
 * specifing the file name (default = <code>inline</code>, values are typically
 * <i>attachment;filename="document.pdf"</i>.</li>
 * <li><b>inputName</b> - the name of the InputStream property from the chained
 * action (default = <code>inputStream</code>).</li>
 * <li><b>bufferSize</b> - the size of the buffer to copy from input to output
 * (default = <code>1024</code>).</li>
 * <li><b>allowCaching</b> if set to 'false' it will set the headers 'Pragma' and 'Cache-Control'
 * to 'no-cahce', and prevent client from caching the content. (default = <code>true</code>)
 * <li><b>contentCharSet</b> if set to a string, ';charset=value' will be added to the
 * content-type header, where value is the string set. If set to an expression, the result
 * of evaluating the expression will be used. If not set, then no charset will be set on
 * the header</li>
 * </ul>
 *
 * <p>
 * These parameters can also be set by exposing a similarly named getter method on your Action.  For example, you can
 * provide <code>getContentType()</code> to override that parameter for the current action.
 * </p>
 *
 * <b>Example:</b>
 *
 * <pre>
 * &lt;result name="success" type="stream"&gt;
 *   &lt;param name="contentType"&gt;image/jpeg&lt;/param&gt;
 *   &lt;param name="inputName"&gt;imageStream&lt;/param&gt;
 *   &lt;param name="contentDisposition"&gt;attachment;filename="document.pdf"&lt;/param&gt;
 *   &lt;param name="bufferSize"&gt;1024&lt;/param&gt;
 * &lt;/result&gt;
 * </pre>
 */
public class StreamResult extends StrutsResultSupport {

    private static final long serialVersionUID = -1468409635999059850L;

    protected static final Logger LOG = LogManager.getLogger(StreamResult.class);

    public static final String DEFAULT_PARAM = "inputName";

    protected String contentType = "text/plain";
    protected String contentLength;
    protected String contentDisposition = "inline";
    protected String contentCharSet;
    protected String inputName = "inputStream";
    protected InputStream inputStream;
    protected int bufferSize = 1024;
    protected boolean allowCaching = true;

    private NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns;

    public StreamResult() {
        super();
    }

    public StreamResult(InputStream in) {
        this.inputStream = in;
    }

    @Inject
    public void setNotExcludedAcceptedPatterns(NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns) {
        this.notExcludedAcceptedPatterns = notExcludedAcceptedPatterns;
    }

    /**
     * @return Returns the whether or not the client should be requested to allow caching of the data stream.
     */
    public boolean getAllowCaching() {
        return allowCaching;
    }

    /**
     * Set allowCaching to <tt>false</tt> to indicate that the client should be requested not to cache the data stream.
     * This is set to <tt>false</tt> by default
     *
     * @param allowCaching Enable caching.
     */
    public void setAllowCaching(boolean allowCaching) {
        this.allowCaching = allowCaching;
    }


    /**
     * @return Returns the bufferSize.
     */
    public int getBufferSize() {
        return (bufferSize);
    }

    /**
     * @param bufferSize The bufferSize to set.
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * @return Returns the contentType.
     */
    public String getContentType() {
        return (contentType);
    }

    /**
     * @param contentType The contentType to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return Returns the contentLength.
     */
    public String getContentLength() {
        return contentLength;
    }

    /**
     * @param contentLength The contentLength to set.
     */
    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * @return Returns the Content-disposition header value.
     */
    public String getContentDisposition() {
        return contentDisposition;
    }

    /**
     * @param contentDisposition the Content-disposition header value to use.
     */
    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    /**
     * @return Returns the charset specified by the user
     */
    public String getContentCharSet() {
        return contentCharSet;
    }

    /**
     * @param contentCharSet the charset to use on the header when sending the stream
     */
    public void setContentCharSet(String contentCharSet) {
        this.contentCharSet = contentCharSet;
    }

    /**
     * @return Returns the inputName.
     */
    public String getInputName() {
        return (inputName);
    }

    /**
     * @param inputName The inputName to set.
     */
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    /**
     * @see StrutsResultSupport#doExecute(java.lang.String, com.opensymphony.xwork2.ActionInvocation)
     */
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        LOG.debug("Find the Response in context");

        OutputStream oOutput = null;

        try {
            String parsedInputName = conditionalParse(inputName, invocation);
            boolean evaluated = parsedInputName != null && !parsedInputName.equals(inputName);
            boolean reevaluate = !evaluated || isAcceptableExpression(parsedInputName);
            if (inputStream == null && reevaluate) {
                LOG.debug("Find the inputstream from the invocation variable stack");
                inputStream = (InputStream) invocation.getStack().findValue(parsedInputName);
            }

            if (inputStream == null) {
                String msg = ("Can not find a java.io.InputStream with the name [" + parsedInputName + "] in the invocation stack. " +
                    "Check the <param name=\"inputName\"> tag specified for this action is correct, not excluded and accepted.");
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }


            HttpServletResponse oResponse = invocation.getInvocationContext().getServletResponse();

            LOG.debug("Set the content type: {};charset{}", contentType, contentCharSet);
            if (contentCharSet != null && !contentCharSet.equals("")) {
                oResponse.setContentType(conditionalParse(contentType, invocation) + ";charset=" + conditionalParse(contentCharSet, invocation));
            } else {
                oResponse.setContentType(conditionalParse(contentType, invocation));
            }

            LOG.debug("Set the content length: {}", contentLength);
            if (contentLength != null) {
                String translatedContentLength = conditionalParse(contentLength, invocation);
                int contentLengthAsInt;
                try {
                    contentLengthAsInt = Integer.parseInt(translatedContentLength);
                    if (contentLengthAsInt >= 0) {
                        oResponse.setContentLength(contentLengthAsInt);
                    }
                } catch (NumberFormatException e) {
                    LOG.warn("failed to recognize {} as a number, contentLength header will not be set",
                            translatedContentLength, e);
                }
            }

            LOG.debug("Set the content-disposition: {}", contentDisposition);
            if (contentDisposition != null) {
                oResponse.addHeader("Content-Disposition", conditionalParse(contentDisposition, invocation));
            }

            LOG.debug("Set the cache control headers if necessary: {}", allowCaching);
            if (!allowCaching) {
                oResponse.addHeader("Pragma", "no-cache");
                oResponse.addHeader("Cache-Control", "no-cache");
            }

            oOutput = oResponse.getOutputStream();

            LOG.debug("Streaming result [{}] type=[{}] length=[{}] content-disposition=[{}] charset=[{}]",
                inputName, contentType, contentLength, contentDisposition, contentCharSet);

            LOG.debug("Streaming to output buffer +++ START +++");
            byte[] oBuff = new byte[bufferSize];
            int iSize;
            while (-1 != (iSize = inputStream.read(oBuff))) {
                LOG.debug("Sending stream ... {}", iSize);
                oOutput.write(oBuff, 0, iSize);
            }
            LOG.debug("Streaming to output buffer +++ END +++");

            // Flush
            oOutput.flush();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (oOutput != null) {
                oOutput.close();
            }
        }
    }

    /**
     * Checks if expression doesn't contain vulnerable code
     *
     * @param expression of result
     * @return true|false
     * @since 2.6
     */
    protected boolean isAcceptableExpression(String expression) {
        NotExcludedAcceptedPatternsChecker.IsAllowed isAllowed = notExcludedAcceptedPatterns.isAllowed(expression);
        if (isAllowed.isAllowed()) {
            return true;
        }

        LOG.warn("Expression [{}] isn't allowed by pattern [{}]! See Accepted / Excluded patterns at\n" +
                "https://struts.apache.org/security/", expression, isAllowed.getAllowedPattern());

        return false;
    }
}
