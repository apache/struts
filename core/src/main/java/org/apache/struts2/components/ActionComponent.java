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
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.views.jsp.TagUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Invoke an action directly from a view.
 * See struts-tags.tld for documentation.
 */
public class ActionComponent extends Component {
    private static final Log LOG = LogFactory.getLog(ActionComponent.class);

    /**
     * Store our HttpServletResponse.
     */
    protected HttpServletResponse response;

    /**
     * Store our HttpServletRequest.
     */
    protected HttpServletRequest request;

    /**
     * Store our ActionProxy.
     */
    protected ActionProxy proxy;

    /**
     * Store the action mapping name.
     */
    protected String name;

    /**
     * Store the action mappinng namespace, if different.
     */
    protected String namespace;

    /**
     * Indicate whether to invoke the result class and render its content.
     */
    protected boolean executeResult;

    /**
     * Indicate whether to pass the request parameters to the Action invocation.
     */
    protected boolean ignoreContextParams;

    /**
     * Indicate whether WebWork compatibility mode is set.
     */
    protected static boolean compatibilityMode = false;

    static {
        if (org.apache.struts2.config.Settings.isSet(StrutsConstants.STRUTS_COMPATIBILITY_MODE_WEBWORK)) {
            compatibilityMode = "true".equals(org.apache.struts2.config.Settings.get(StrutsConstants.STRUTS_COMPATIBILITY_MODE_WEBWORK));
        }
    }

    /**
     * Construct object instance, setting runtime parameters.
     *
     * @param stack Our OgnlValueStack
     * @param request Our HttpServletRequest
     * @param response Our HttpServletResponse
     */
    public ActionComponent(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack);
        this.request = request;
        this.response = response;
    }


    // See superclass for documentation
    public boolean end(Writer writer, String body) {
        boolean end = super.end(writer, "", false);
        try {
            try {
                writer.flush();
            } catch (IOException e) {
                LOG.warn("error while trying to flush writer ", e);
            }
            executeAction();

            if ((getId() != null) && (proxy != null)) {
                getStack().setValue("#attr['" + getId() + "']",
                        proxy.getAction());
            }
        } finally {
            popComponentStack();
        }
        return end;
    }

    /**
     * Create a context in which to invoke Action class,
     * passing along context parameters
     * if ignoreContextParams is FALSE.
     *
     * @return A map representing the new context
     */
    private Map createExtraContext() {
        Map parentParams = null;

        if (!ignoreContextParams) {
            parentParams = new ActionContext(getStack().getContext()).getParameters();
        }

        Map newParams = (parentParams != null) ? new HashMap(parentParams) : new HashMap();

        if (parameters != null) {
            newParams.putAll(parameters);
        }

        ActionContext ctx = new ActionContext(stack.getContext());
        ServletContext servletContext = (ServletContext) ctx.get(ServletActionContext.SERVLET_CONTEXT);
        PageContext pageContext = (PageContext) ctx.get(ServletActionContext.PAGE_CONTEXT);
        Map session = ctx.getSession();
        Map application = ctx.getApplication();

        Dispatcher du = Dispatcher.getInstance();
        Map extraContext = du.createContextMap(new RequestMap(request),
                newParams,
                session,
                application,
                request,
                response,
                servletContext);

        OgnlValueStack newStack = new OgnlValueStack(stack);
        extraContext.put(ActionContext.VALUE_STACK, newStack);

        // add page context, such that ServletDispatcherResult will do an include
        extraContext.put(ServletActionContext.PAGE_CONTEXT, pageContext);

        return extraContext;
    }

    /**
     * Invoke the Action class,
     * If no namespace is provided, attempt to derive a namespace using the buildNamespace method.
     *
     * @see org.apache.struts2.views.jsp.TagUtils#buildNamespace
     */
    private void executeAction() {
        String actualName = findString(name, "name", "Action name is required. Example: updatePerson");

        if (actualName == null) {
            throw new StrutsException("Unable to find value for name " + name);
        }

        String actionName = actualName;
        String methodName = null;

        if (compatibilityMode) {
            // handle "name!method" convention.
            int exclamation = actualName.lastIndexOf("!");
            if (exclamation != -1) {
                actionName = actualName.substring(0, exclamation);
                methodName = actualName.substring(exclamation + 1);
            }
        }

        String namespace;

        if (this.namespace == null) {
            namespace = TagUtils.buildNamespace(getStack(), request);
        } else {
            namespace = findString(this.namespace);
        }

        // get the old value stack from the request
        OgnlValueStack stack = getStack();
        // execute at this point, after params have been set
        try {
            Configuration config = Dispatcher.getInstance().getConfigurationManager().getConfiguration();
            proxy = ActionProxyFactory.getFactory().createActionProxy(config, namespace, actionName,
                    createExtraContext(), executeResult, true);
            if (null != methodName) {
                proxy.setMethod(methodName);
            }
            // set the new stack into the request for the taglib to use
            request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, proxy.getInvocation().getStack());
            proxy.execute();

        } catch (Exception e) {
            String message = "Could not invoke action: " + namespace + "/" + actualName;
            LOG.error(message, e);
        } finally {
            // set the old stack back on the request
            request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
        }

        if ((getId() != null) && (proxy != null)) {
            final Map context = stack.getContext();
            context.put(getId(), proxy.getAction());
        }
    }

    /**
     * Expose proxy instance (for testing).
     *
     * @return proxy instance
     */
    public ActionProxy getProxy() {
        return proxy;
    }

    // See TLD for documentation
    public void setId(String id) {
        super.setId(id);
    }

    // See TLD for documentation
    public void setName(String name) {
        this.name = name;
    }

    // See TLD for documentation
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    // See TLD for documentation
    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    // See TLD for documentation
    public void setIgnoreContextParams(boolean ignoreContextParams) {
        this.ignoreContextParams = ignoreContextParams;
    }
}
