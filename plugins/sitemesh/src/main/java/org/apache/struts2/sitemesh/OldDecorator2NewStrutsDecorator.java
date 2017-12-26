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
package org.apache.struts2.sitemesh;

import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.webapp.decorator.BaseWebAppDecorator;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import freemarker.template.Configuration;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Adapts a SiteMesh 2 {@link com.opensymphony.module.sitemesh.Decorator} to a
 * SiteMesh 3 {@link com.opensymphony.sitemesh.Decorator}.
 *
 * @since SiteMesh 3
 */
public abstract class OldDecorator2NewStrutsDecorator extends BaseWebAppDecorator implements RequestConstants {

    protected com.opensymphony.module.sitemesh.Decorator oldDecorator;
    private static String customEncoding;

    public OldDecorator2NewStrutsDecorator(com.opensymphony.module.sitemesh.Decorator oldDecorator) {
        this.oldDecorator = oldDecorator;
    }

    public OldDecorator2NewStrutsDecorator() {
        oldDecorator = null;
    }


    /**
     * Applies the decorator, using the relevent contexts
     *
     * @param content        The content
     * @param request        The servlet request
     * @param response       The servlet response
     * @param servletContext The servlet context
     * @param ctx            The action context for this request, populated with the server state
     */
    protected abstract void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, ActionContext ctx) throws ServletException, IOException;

    /**
     * Applies the decorator, creating the relevent contexts and delegating to
     * the extended applyDecorator().
     *
     * @param content        The content
     * @param request        The servlet request
     * @param response       The servlet response
     * @param servletContext The servlet context
     * @param webAppContext  The web app context
     */

    protected void render(Content content, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, SiteMeshWebAppContext webAppContext) throws IOException, ServletException {

        // see if the URI path (webapp) is set
        if (oldDecorator.getURIPath() != null) {
            // in a security conscious environment, the servlet container
            // may return null for a given URL
            if (servletContext.getContext(oldDecorator.getURIPath()) != null) {
                servletContext = servletContext.getContext(oldDecorator.getURIPath());
            }
        }

        ActionContext ctx = ServletActionContext.getActionContext(request);
        if (ctx == null) {
            // ok, one isn't associated with the request, so let's create one using the current Dispatcher
            ValueStack vs = Dispatcher.getInstance().getContainer().getInstance(ValueStackFactory.class).createValueStack();
            vs.getContext().putAll(Dispatcher.getInstance().createContextMap(request, response, null));
            ctx = new ActionContext(vs.getContext());
            if (ctx.getActionInvocation() == null) {
                // put in a dummy ActionSupport so basic functionality still works
                ActionSupport action = new ActionSupport();
                vs.push(action);
                ctx.setActionInvocation(new DummyActionInvocation(action));
            }
        }

        // delegate to the actual page decorator
        render(content, request, response, servletContext, ctx);

    }

    /**
     * Returns the locale used for the {@link freemarker.template.Configuration#getTemplate(String, java.util.Locale)} call. The base implementation
     * simply returns the locale setting of the action (assuming the action implements {@link LocaleProvider}) or, if
     * the action does not the configuration's locale is returned. Override this method to provide different behaviour,
     */
    protected Locale getLocale(ActionInvocation invocation, Configuration configuration) {
        if (invocation.getAction() instanceof LocaleProvider) {
            return ((LocaleProvider) invocation.getAction()).getLocale();
        } else {
            return configuration.getLocale();
        }
    }


    /**
     * Gets the L18N encoding of the system.  The default is UTF-8.
     */
    protected String getEncoding() {
        String encoding = customEncoding;
        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }


    static class DummyActionInvocation implements ActionInvocation {

        ActionSupport action;

        public DummyActionInvocation(ActionSupport action) {
            this.action = action;
        }

        public Object getAction() {
            return action;
        }

        public boolean isExecuted() {
            return false;
        }

        public ActionContext getInvocationContext() {
            return null;
        }

        public ActionProxy getProxy() {
            return null;
        }

        public Result getResult() throws Exception {
            return null;
        }

        public String getResultCode() {
            return null;
        }

        public void setResultCode(String resultCode) {
        }

        public ValueStack getStack() {
            return null;
        }

        public void addPreResultListener(PreResultListener listener) {
        }

        public String invoke() throws Exception {
            return null;
        }

        public String invokeActionOnly() throws Exception {
            return null;
        }

        public void setActionEventListener(ActionEventListener listener) {
        }

        public void init(ActionProxy proxy) {
        }

    }

}
