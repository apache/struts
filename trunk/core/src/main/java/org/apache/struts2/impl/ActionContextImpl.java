// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.spi.ActionContext;
import org.apache.struts2.spi.Result;

import com.opensymphony.xwork2.ActionInvocation;

public class ActionContextImpl implements ActionContext {

    final ActionInvocation invocation;

    public ActionContextImpl(ActionInvocation invocation) {
        this.invocation = invocation;
    }

    public Object getAction() {
        return invocation.getAction();
    }

    public Method getMethod() {
        String methodName = invocation.getProxy().getMethod();
        try {
            return getAction().getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String getActionName() {
        return invocation.getProxy().getActionName();
    }

    public String getNamespacePath() {
        return invocation.getProxy().getNamespace();
    }

    // TODO: Do something with these.
    List<Result> resultInterceptors = new ArrayList<Result>();

    public void addResultInterceptor(Result interceptor) {
        resultInterceptors.add(interceptor);
    }

    public Result getResult() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public ActionContext getPrevious() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public ActionContext getNext() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
