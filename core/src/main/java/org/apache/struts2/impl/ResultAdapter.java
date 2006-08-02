// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.ActionInvocation;

public class ResultAdapter implements Result {

    final org.apache.struts2.spi.Result delegate;

    public ResultAdapter(org.apache.struts2.spi.Result delegate) {
        this.delegate = delegate;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        delegate.execute(RequestContextImpl.get());
    }
}
