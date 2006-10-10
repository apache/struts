// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import static org.apache.struts2.impl.RequestContextImpl.ILLEGAL_PROCEED;

import java.util.concurrent.Callable;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class InterceptorAdapter implements Interceptor {

    private static final long serialVersionUID = 8020658947818231684L;
    final org.apache.struts2.spi.Interceptor delegate;

    public InterceptorAdapter(org.apache.struts2.spi.Interceptor delegate) {
        this.delegate = delegate;
    }

    public String intercept(final ActionInvocation invocation) throws Exception {
        final RequestContextImpl requestContext = RequestContextImpl.get();

        // Save the existing proceed implementation so we can restore it later.
        Callable<String> previous = requestContext.getProceed();

        requestContext.setProceed(new Callable<String>() {
            public String call() throws Exception {
                // This proceed implementation is no longer valid past this point.
                requestContext.setProceed(ILLEGAL_PROCEED);
                try {
                    return invocation.invoke();
                } finally {
                    // We're valid again.
                    requestContext.setProceed(this);
                }
            }
        });

        try {
            return delegate.intercept(requestContext);
        } finally {
            requestContext.setProceed(previous);
        }
    }

    public void destroy() {}
    public void init() {}
}
