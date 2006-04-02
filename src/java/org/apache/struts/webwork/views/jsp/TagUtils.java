/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp;

import org.apache.struts.webwork.RequestUtils;
import org.apache.struts.webwork.ServletActionContext;
import org.apache.struts.webwork.dispatcher.ApplicationMap;
import org.apache.struts.webwork.dispatcher.DispatcherUtils;
import org.apache.struts.webwork.dispatcher.RequestMap;
import org.apache.struts.webwork.dispatcher.SessionMap;
import org.apache.struts.webwork.dispatcher.mapper.ActionMapper;
import org.apache.struts.webwork.dispatcher.mapper.ActionMapperFactory;
import org.apache.struts.webwork.dispatcher.mapper.ActionMapping;
import org.apache.struts.webwork.util.AttributeMap;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Map;


/**
 * User: plightbo
 * Date: Oct 17, 2003
 * Time: 7:09:59 AM
 */
public class TagUtils {

    public static OgnlValueStack getStack(PageContext pageContext) {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        OgnlValueStack stack = (OgnlValueStack) req.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);

        if (stack == null) {
            stack = new OgnlValueStack();

            HttpServletResponse res = (HttpServletResponse) pageContext.getResponse();
            DispatcherUtils.initialize(pageContext.getServletContext());
            DispatcherUtils du = DispatcherUtils.getInstance();
            Map extraContext = du.createContextMap(new RequestMap(req),
                    req.getParameterMap(),
                    new SessionMap(req),
                    new ApplicationMap(pageContext.getServletContext()),
                    req,
                    res,
                    pageContext.getServletContext());
            extraContext.put(ServletActionContext.PAGE_CONTEXT, pageContext);
            stack.getContext().putAll(extraContext);
            req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

            // also tie this stack/context to the ThreadLocal
            ActionContext.setContext(new ActionContext(stack.getContext()));
        } else {
            // let's make sure that the current page context is in the action context
            Map context = stack.getContext();
            context.put(ServletActionContext.PAGE_CONTEXT, pageContext);

            AttributeMap attrMap = new AttributeMap(context);
            context.put("attr", attrMap);
        }

        return stack;
    }

    public static String buildNamespace(OgnlValueStack stack, HttpServletRequest request) {
        ActionContext context = new ActionContext(stack.getContext());
        ActionInvocation invocation = context.getActionInvocation();

        if (invocation == null) {
            ActionMapper mapper = ActionMapperFactory.getMapper();
            ActionMapping mapping = mapper.getMapping(request);

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
