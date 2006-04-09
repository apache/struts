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
package org.apache.struts.action2.dispatcher;

import com.opensymphony.xwork.ActionInvocation;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <!-- START SNIPPET: description -->
 *
 * A custom Result type for send raw data (via an InputStream) directly to the
 * HttpServletResponse. Very useful for allowing users to download content.
 *
 * <!-- END SNIPPET: description -->
 * <p/>
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>contentType</b> - the stream mime-type as sent to the web browser
 * (default = <code>text/plain</code>).</li>
 *
 * <li><b>contentLength</b> - the stream length in bytes (the browser displays a
 * progress bar).</li>
 *
 * <li><b>contentDispostion</b> - the content disposition header value for
 * specifing the file name (default = <code>inline</code>, values are typically
 * <i>filename="document.pdf"</i>.</li>
 *
 * <li><b>inputName</b> - the name of the InputStream property from the chained
 * action (default = <code>inputStream</code>).</li>
 *
 * <li><b>bufferSize</b> - the size of the buffer to copy from input to output
 * (default = <code>1024</code>).</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre><!-- START SNIPPET: example -->
 * &lt;result name="success" type="stream"&gt;
 *   &lt;param name="contentType"&gt;image/jpeg&lt;/param&gt;
 *   &lt;param name="inputName"&gt;imageStream&lt;/param&gt;
 *   &lt;param name="contentDisposition"&gt;filename="document.pdf"&lt;/param&gt;
 *   &lt;param name="bufferSize"&gt;1024&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example --></pre>
 *
 * @author mcrawford
 * @author Rainer Hermanns
 */
public class StreamResult extends StrutsResultSupport {
	
	private static final long serialVersionUID = -1468409635999059850L;

	protected static final Log log = LogFactory.getLog(StreamResult.class);

    protected String contentType = "text/plain";
    protected int contentLength;
    protected String contentDisposition = "inline";
    protected String inputName = "inputStream";
    protected int bufferSize = 1024;

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
    public int getContentLength() {
        return contentLength;
    }

    /**
     * @param contentLength The contentLength to set.
     */
    public void setContentLength(int contentLength) {
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

    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

        InputStream oInput = null;
        OutputStream oOutput = null;

        try {
            // Find the inputstream from the invocation variable stack
            oInput = (InputStream) invocation.getStack().findValue(conditionalParse(inputName, invocation));

            if (oInput == null) {
                String msg = ("Can not find a java.io.InputStream with the name [" + inputName + "] in the invocation stack. " +
                    "Check the <param name=\"inputName\"> tag specified for this action.");
                log.error(msg);
                throw new IllegalArgumentException(msg);
            }

            // Find the Response in context
            HttpServletResponse oResponse = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);

            // Set the content type
            oResponse.setContentType(conditionalParse(contentType, invocation));

            // Set the content length
            if (contentLength != 0) {
                 oResponse.setContentLength(contentLength);
            }

            // Set the content-disposition
            if (contentDisposition != null) {
                oResponse.addHeader("Content-disposition", conditionalParse(contentDisposition, invocation));
            }

            // Get the outputstream
            oOutput = oResponse.getOutputStream();

            if (log.isDebugEnabled()) {
                log.debug("Streaming result [" + inputName + "] type=[" + contentType + "] length=[" + contentLength +
                    "] content-disposition=[" + contentDisposition + "]");
            }

            // Copy input to output
            log.debug("Streaming to output buffer +++ START +++");
            byte[] oBuff = new byte[bufferSize];
            int iSize;
            while (-1 != (iSize = oInput.read(oBuff))) {
                oOutput.write(oBuff, 0, iSize);
            }
            log.debug("Streaming to output buffer +++ END +++");

            // Flush
            oOutput.flush();
        }
        finally {
            if (oInput != null) oInput.close();
            if (oOutput != null) oOutput.close();
        }
    }

}
