// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;

import java.util.Map;

public class StrutsActionProxyFactory extends DefaultActionProxyFactory {

    public ActionProxy createActionProxy(Configuration config, String namespace, String actionName, Map extraContext)
            throws Exception {
        return new StrutsActionProxy(config, namespace, actionName, extraContext, true, true);
    }

    public ActionProxy createActionProxy(Configuration config, String namespace, String actionName, Map extraContext,
            boolean executeResult, boolean cleanupContext) throws Exception {
        return new StrutsActionProxy(config, namespace, actionName, extraContext, executeResult, cleanupContext);
    }
}
