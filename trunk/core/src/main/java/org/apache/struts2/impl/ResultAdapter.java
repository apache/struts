// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

public class ResultAdapter implements Result {

    private static final long serialVersionUID = -5107033078266553554L;
    final org.apache.struts2.spi.Result delegate;

    public ResultAdapter(org.apache.struts2.spi.Result delegate) {
        this.delegate = delegate;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        delegate.execute(RequestContextImpl.get());
    }
}
