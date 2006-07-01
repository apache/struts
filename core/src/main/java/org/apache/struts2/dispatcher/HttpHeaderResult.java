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
package org.apache.struts2.dispatcher;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * <!-- START SNIPPET: description -->
 *
 * A custom Result type for setting HTTP headers and status by optionally evaluating against the ValueStack.
 * 
 * <!-- END SNIPPET: description -->
 * <p/>
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>status</b> - the http servlet response status code that should be set on a response.</li>
 *
 * <li><b>parse</b> - true by default. If set to false, the headers param will not be parsed for Ognl expressions.</li>
 *
 * <li><b>headers</b> - header values.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre><!-- START SNIPPET: example -->
 * &lt;result name="success" type="httpheader"&gt;
 *   &lt;param name="status"&gt;204&lt;/param&gt;
 *   &lt;param name="headers.a"&gt;a custom header value&lt;/param&gt;
 *   &lt;param name="headers.b"&gt;another custom header value&lt;/param&gt;
 * &lt;/result&gt;
 * <!-- END SNIPPET: example --></pre>
 *
 */
public class HttpHeaderResult implements Result {

	private static final long serialVersionUID = 195648957144219214L;

	public static final String DEFAULT_PARAM = "status";


    protected boolean parse = true;
    private Map headers;
    private int status = -1;


    /**
     * Returns a Map of all HTTP headers.
     *
     * @return a Map of all HTTP headers.
     */
    public Map getHeaders() {
        if (headers == null) {
            headers = new HashMap();
        }

        return headers;
    }

    /**
     * Sets whether or not the HTTP header values should be evaluated against the ValueStack (by default they are).
     *
     * @param parse <tt>true</tt> if HTTP header values should be evaluated agains the ValueStack, <tt>false</tt>
     *              otherwise.
     */
    public void setParse(boolean parse) {
        this.parse = parse;
    }

    /**
     * Sets the http servlet response status code that should be set on a response.
     *
     * @param status the Http status code
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Sets the optional HTTP response status code and also re-sets HTTP headers after they've
     * been optionally evaluated against the ValueStack.
     *
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when re-setting the headers.
     */
    public void execute(ActionInvocation invocation) throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();

        if (status != -1) {
            response.setStatus(status);
        }

        if (headers != null) {
            OgnlValueStack stack = ActionContext.getContext().getValueStack();

            for (Iterator iterator = headers.entrySet().iterator();
                 iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String value = (String) entry.getValue();
                String finalValue = parse ? TextParseUtil.translateVariables(value, stack) : value;
                response.addHeader((String) entry.getKey(), finalValue);
            }
        }
    }
}
