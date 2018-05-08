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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * A result that renders the current request parameters as a form which
 * immediately submits a <a href="http://en.wikipedia.org/wiki/Postback">postback</a>
 * to the specified destination.
 * <!-- END SNIPPET: description -->
 * <p/>
 * <b>Parameters:</b>
 * <!-- START SNIPPET: params -->
 * <ul>
 *     <li>location - http location to post the form</li>
 *     <li>prependServletContext (true|false) -  when location is relative, controls if to add Servlet Context, default "true"</li>
 *     <li>actionName - action name to post the form (resolved as an expression)</li>
 *     <li>namespace - action's namespace to use (resolved as an expression)</li>
 *     <li>method - actions' method to use (resolved as an expression)</li>
 *     <li>cache (true|false) - when set to true adds cache control headers, default "true"</li>
 *     <li>parse (true|false) - when set to true actionName, namespace and method are parsed, default "true"</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 * <p/>
 * <b>Examples:</b>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="registerThirdParty" &gt;
 *   &lt;result type="postback"&gt;https://www.example.com/register&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;action name="registerThirdParty" &gt;
 *   &lt;result type="postback"&gt;
 *     &lt;param name="namespace"&gt;/secure&lt;/param&gt;
 *     &lt;param name="actionName"&gt;register2&lt;/param&gt;
 *   &lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class PostbackResult extends StrutsResultSupport {

    private String actionName;
    private String namespace;
    private String method;
    private boolean prependServletContext = true;
    private boolean cache = true;

    protected ActionMapper actionMapper;

    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);

        // Cache?
        if (!cache) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
            response.setDateHeader("Expires", 0); // Proxies
        }

        // Render
        PrintWriter pw = new PrintWriter(response.getOutputStream());
        pw.write("<!DOCTYPE html><html><body><form action=\"" + finalLocation + "\" method=\"POST\">");
        writeFormElements(request, pw);
        writePrologueScript(pw);
        pw.write("</html>");
        pw.flush();
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        String postbackUri = makePostbackUri(invocation);
        setLocation(postbackUri);
        super.execute(invocation);
    }

    /**
     * Determines if the specified form input element should be included.
     *
     * @param name the input element name
     * @param values the input element values
     * @return {@code true} if included; otherwise {@code false}
     */
    protected boolean isElementIncluded(String name, String[] values) {
        return !name.startsWith("action:");
    }

    protected String makePostbackUri(ActionInvocation invocation) {
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
        String postbackUri;

        if (actionName != null) {
            actionName = conditionalParse(actionName, invocation);
            parseLocation = false;

            if (namespace == null) {
                namespace = invocation.getProxy().getNamespace();
            } else {
                namespace = conditionalParse(namespace, invocation);
            }
            if (method == null) {
                method = "";
            } else {
                method = conditionalParse(method, invocation);
            }
            postbackUri = request.getContextPath() + actionMapper.getUriFromActionMapping(new ActionMapping(actionName, namespace, method, null));
        } else {
            String location = getLocation();
            // Do not prepend if the URL is a FQN
            if (!location.matches("^([a-zA-z]+:)?//.*")) {
                // If the URL is relative to the servlet context, prepend the servlet context path
                if (prependServletContext && (request.getContextPath() != null) && (request.getContextPath().length() > 0)) {
                    location = request.getContextPath() + location;
                }
            }
            postbackUri = location;
        }

        return postbackUri;
    }

    @Inject
    public final void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    /**
     * Sets the name of the destination action.
     *
     * @param actionName the action name
     */
    public final void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * Stores the option to cache the rendered intermediate page. The default
     * is {@code true}.
     *
     * @return {@code true} to cache; otherwise {@code false}
     */
    public final void setCache(boolean cache) {
        this.cache = cache;
    }

    /**
     * Sets the method of the destination action.
     *
     * @param method the method
     */
    public final void setMethod(String method) {
        this.method = method;
    }

    /**
     * Sets the namespace of the destination action.
     *
     * @param namespace the namespace
     */
    public final void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public final void setPrependServletContext(boolean prependServletContext) {
        this.prependServletContext = prependServletContext;
    }

    protected void writeFormElement(PrintWriter pw, String name, String[] values) throws UnsupportedEncodingException {
        for (String value : values) {
            String encName = URLEncoder.encode(name, "UTF-8");
            String encValue = URLEncoder.encode(value, "UTF-8");
            pw.write("<input type=\"hidden\" name=\"" + encName + "\" value=\"" + encValue + "\"/>");
        }
    }

    private void writeFormElements(HttpServletRequest request, PrintWriter pw) throws UnsupportedEncodingException {
        Map<String, String[]> params = request.getParameterMap();
        for (String name : params.keySet()) {
            String[] values = params.get(name);
            if (isElementIncluded(name, values)) {
                writeFormElement(pw, name, values);
            }
        }
    }

    /**
     * Outputs the script after the form has been emitted. The default script
     * is to submit the form using a JavaScript time out that immediately expires.
     *
     * @param pw the print writer
     */
    protected void writePrologueScript(PrintWriter pw) {
        pw.write("<script>");
        pw.write("setTimeout(function(){document.forms[0].submit();},0);");
        pw.write("</script>");
    }

}
