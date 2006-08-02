// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import com.opensymphony.xwork2.DefaultActionProxy;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;

import java.util.Map;
import java.util.concurrent.Callable;

public class StrutsActionProxy extends DefaultActionProxy {

    public StrutsActionProxy(Configuration cfg, String namespace, String actionName, Map extraContext,
                             boolean executeResult, boolean cleanupContext) throws Exception {
        super(cfg, namespace, actionName, extraContext, executeResult, cleanupContext);
    }

    public String execute() throws Exception {
        ActionContext previous = ActionContext.getContext();
        ActionContext.setContext(invocation.getInvocationContext());
        try {
            return RequestContextImpl.callInContext(invocation, new Callable<String>() {
                public String call() throws Exception {
                    return invocation.invoke();
                }
            });
        } finally {
            if (cleanupContext)
                ActionContext.setContext(previous);
        }
    }
}
