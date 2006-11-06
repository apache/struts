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
