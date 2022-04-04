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

package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ApplicationMap;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.util.AttributeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Map;


/**
 */
public class TagUtils {

    public static ValueStack getStack(PageContext pageContext) {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        ValueStack stack = (ValueStack) req.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);

        if (stack == null) {

            HttpServletResponse res = (HttpServletResponse) pageContext.getResponse();
            Dispatcher du = Dispatcher.getInstance();
            if (du == null) {
                throw new ConfigurationException("The Struts dispatcher cannot be found.  This is usually caused by "+
                        "using Struts tags without the associated filter. Struts tags are only usable when the request "+
                        "has passed through its servlet filter, which initializes the Struts dispatcher needed for this tag.");
            }
            stack = du.getContainer().getInstance(ValueStackFactory.class).createValueStack();
            Map<String, Object> extraContext = du.createContextMap(new RequestMap(req),
                    req.getParameterMap(),
                    new SessionMap(req),
                    new ApplicationMap(pageContext.getServletContext()),
                    req,
                    res);
            extraContext.put(ServletActionContext.PAGE_CONTEXT, pageContext);
            stack.getContext().putAll(extraContext);
            req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

            // also tie this stack/context to the ThreadLocal
            ActionContext.setContext(new ActionContext(stack.getContext()));
        } else {
            // let's make sure that the current page context is in the action context
            Map<String, Object> context = stack.getContext();
            context.put(ServletActionContext.PAGE_CONTEXT, pageContext);

            AttributeMap attrMap = new AttributeMap(context);
            context.put("attr", attrMap);
        }

        return stack;
    }

    public static String buildNamespace(ActionMapper mapper, ValueStack stack, HttpServletRequest request) {
        ActionContext context = new ActionContext(stack.getContext());
        ActionInvocation invocation = context.getActionInvocation();

        if (invocation == null) {
            ActionMapping mapping = mapper.getMapping(request,
                    Dispatcher.getInstance().getConfigurationManager());

            if (mapping != null) {
                return mapping.getNamespace();
            } else {
                // well, if the ActionMapper can't tell us, and there is no existing action invocation,
                // let's just go with a default guess that the namespace is the last the path minus the
                // last part (/foo/bar/baz.xyz -> /foo/bar)

                String path = RequestUtils.getServletPath(request);
                return path.substring(0, path.lastIndexOf("/"));
            }
        } else {
            return invocation.getProxy().getNamespace();
        }
    }
}
