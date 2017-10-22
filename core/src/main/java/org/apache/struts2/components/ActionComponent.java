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
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.TagUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>This tag enables developers to call actions directly from a JSP page by specifying the action name and an optional
 * namespace.  The body content of the tag is used to render the results from the Action.  Any result processor defined
 * for this action in struts.xml will be ignored, <i>unless</i> the executeResult parameter is specified.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 *      <li>id (String) - the id (if specified) to put the action under stack's context.
 *      <li>name* (String) - name of the action to be executed (without the extension suffix eg. .action)</li>
 *      <li>namespace (String) - default to the namespace where this action tag is invoked</li>
 *      <li>executeResult (Boolean) -  default is false. Decides whether the result of this action is to be executed or not</li>
 *      <li>ignoreContextParams (Boolean) - default to false. Decides whether the request parameters are to be included when the action is invoked</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 * <pre>
 * <!-- START SNIPPET: javacode -->
 * public class ActionTagAction extends ActionSupport {
 *
 *  public String execute() throws Exception {
 *      return "done";
 *  }
 *
 *  public String doDefault() throws Exception {
 *      ServletActionContext.getRequest().setAttribute("stringByAction", "This is a String put in by the action's doDefault()");
 *      return "done";
 *  }
 * }
 * <!-- END SNIPPET: javacode -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: strutsxml -->
 *   &lt;xwork&gt;
 *      ....
 *     &lt;action name=&quot;actionTagAction1&quot; class=&quot;tmjee.testing.ActionTagAction&quot;&gt;
 *         &lt;result name=&quot;done&quot;&gt;success.jsp&lt;/result&gt;
 *     &lt;/action&gt;
 *      &lt;action name=&quot;actionTagAction2&quot; class=&quot;tmjee.testing.ActionTagAction&quot; method=&quot;default&quot;&gt;
 *         &lt;result name=&quot;done&quot;&gt;success.jsp&lt;/result&gt;
 *     &lt;/action&gt;
 *      ....
 *   &lt;/xwork&gt;
 * <!-- END SNIPPET: strutsxml -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *  <span>The following action tag will execute result and include it in this page</span>
 *  <br>
 *  &lt;s:action name=&quot;actionTagAction&quot; executeResult=&quot;true&quot; /&gt;
 *  <br>
 *  <span>The following action tag will do the same as above, but invokes method specialMethod in action</span>
 *  <br>
 *  &lt;s:action name=&quot;actionTagAction!specialMethod&quot; executeResult=&quot;true&quot; /&gt;
 *  <br>
 *  <span>The following action tag will not execute result, but put a String in request scope
 *       under an id "stringByAction" which will be retrieved using property tag</span>
 *  &lt;s:action name=&quot;actionTagAction!default&quot; executeResult=&quot;false&quot; /&gt;
 *  &lt;s:property value=&quot;#attr.stringByAction&quot; /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="action", tldTagClass="org.apache.struts2.views.jsp.ActionTag", description="Execute an action from within a view")
public class ActionComponent extends ContextBean {
    private static final Logger LOG = LogManager.getLogger(ActionComponent.class);

    protected HttpServletResponse res;
    protected HttpServletRequest req;

    protected ValueStackFactory valueStackFactory;
    protected ActionProxyFactory actionProxyFactory;
    protected ActionProxy proxy;
    protected String name;
    protected String namespace;
    protected boolean executeResult;
    protected boolean ignoreContextParams;
    protected boolean flush = true;
    protected boolean rethrowException;

    public ActionComponent(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
    }

    /**
     * @param actionProxyFactory the actionProxyFactory to set
     */
    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }
    
    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    public boolean end(Writer writer, String body) {
        boolean end = super.end(writer, "", false);
        try {
            if (flush) {
                try {
                    writer.flush();
                } catch (IOException e) {
                	LOG.warn("error while trying to flush writer ", e);
                }
            }
            executeAction();

            if ((getVar() != null) && (proxy != null)) {
                getStack().setValue("#attr['" + getVar() + "']", proxy.getAction());
            }
        } finally {
            popComponentStack();
        }
        return end;
    }

    protected Map createExtraContext() {
        HttpParameters newParams = createParametersForContext();

        ActionContext ctx = new ActionContext(stack.getContext());
        PageContext pageContext = (PageContext) ctx.get(ServletActionContext.PAGE_CONTEXT);
        Map session = ctx.getSession();
        Map application = ctx.getApplication();

        Dispatcher du = Dispatcher.getInstance();
        Map<String, Object> extraContext = du.createContextMap(new RequestMap(req),
                newParams,
                session,
                application,
                req,
                res);

        ValueStack newStack = valueStackFactory.createValueStack(stack);
        extraContext.put(ActionContext.VALUE_STACK, newStack);

        // add page context, such that ServletDispatcherResult will do an include
        extraContext.put(ServletActionContext.PAGE_CONTEXT, pageContext);

        return extraContext;
    }

    /**
     * Creates parameters map using parameters from the value stack and component parameters.  Any non-String array
     * values will be converted into a single-value String array.
     * 
     * @return A map of String[] parameters
     */
    protected HttpParameters createParametersForContext() {
        HttpParameters parentParams = null;

        if (!ignoreContextParams) {
            parentParams = new ActionContext(getStack().getContext()).getParameters();
        }

        HttpParameters.Builder builder = HttpParameters.create();
        if (parentParams != null) {
            builder = builder.withParent(parentParams);
        }

        if (parameters != null) {
            Map<String, String[]> params = new HashMap<>();
            for (Object o : parameters.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                Object val = entry.getValue();
                if (val.getClass().isArray() && String.class == val.getClass().getComponentType()) {
                    params.put(key, (String[])val);
                } else {
                    params.put(key, new String[]{val.toString()});
                }
            }
            builder = builder.withExtraParams(params);
        }
        return builder.build();
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    /**
     * Execute the requested action.  If no namespace is provided, we'll
     * attempt to derive a namespace using buildNamespace().  The ActionProxy
     * and the namespace will be saved into the instance variables proxy and
     * namespace respectively.
     *
     * @see org.apache.struts2.views.jsp.TagUtils#buildNamespace
     */
    protected void executeAction() {
        String actualName = findString(name, "name", "Action name is required. Example: updatePerson");

        if (actualName == null) {
            throw new StrutsException("Unable to find value for name " + name);
        }

        // handle "name!method" convention.
        final String actionName;
        final String methodName;

        ActionMapping mapping = actionMapper.getMappingFromActionName(actualName);
        actionName = mapping.getName();
        methodName = mapping.getMethod();

        String namespace;

        if (this.namespace == null) {
            namespace = TagUtils.buildNamespace(actionMapper, getStack(), req);
        } else {
            namespace = findString(this.namespace);
        }

        // get the old value stack from the request
        ValueStack stack = getStack();
        // execute at this point, after params have been set
        ActionInvocation inv = ActionContext.getContext().getActionInvocation();
        try {

            proxy = actionProxyFactory.createActionProxy(namespace, actionName, methodName, createExtraContext(), executeResult, true);
            // set the new stack into the request for the taglib to use
            req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, proxy.getInvocation().getStack());
            req.setAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION, Boolean.TRUE);
            proxy.execute();

        } catch (Exception e) {
            String message = "Could not execute action: " + namespace + "/" + actualName;
            LOG.error(message, e);
            if (rethrowException) {
                throw new StrutsException(message, e);
            }
        } finally {
            req.removeAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION);
            // set the old stack back on the request
            req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
            if (inv != null) {
                ActionContext.getContext().setActionInvocation(inv);
            }
        }

        if ((getVar() != null) && (proxy != null)) {
            putInContext(proxy.getAction());
        }
    }

    @StrutsTagAttribute(required=true,description="Name of the action to be executed (without the extension suffix eg. .action)")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="Namespace for action to call", defaultValue="namespace from where tag is used")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @StrutsTagAttribute(description="Whether the result of this action (probably a view) should be executed/rendered", type="Boolean", defaultValue="false")
    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    @StrutsTagAttribute(description="Whether the request parameters are to be included when the action is invoked", type="Boolean", defaultValue="false")
    public void setIgnoreContextParams(boolean ignoreContextParams) {
        this.ignoreContextParams = ignoreContextParams;
    }

    @StrutsTagAttribute(description="Whether the writer should be flush upon end of action component tag, default to true", type="Boolean", defaultValue="true")
    public void setFlush(boolean flush) {
        this.flush = flush;
    }

    @StrutsTagAttribute(description="Whether an exception should be rethrown, if the target action throws an exception", type="Boolean", defaultValue="false")
    public void setRethrowException(boolean rethrowException) {
        this.rethrowException = rethrowException;
    }
}
