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

import java.util.Iterator;

import org.apache.struts2.spi.ValueStack;

import com.opensymphony.xwork2.util.ValueStackFactory;

public class ValueStackAdapter implements ValueStack {

    final com.opensymphony.xwork2.util.ValueStack delegate;

    public ValueStackAdapter(com.opensymphony.xwork2.util.ValueStack delegate) {
        this.delegate = delegate;
    }

    public Object peek() {
        return delegate.peek();
    }

    public Object pop() {
        return delegate.pop();
    }

    public void push(Object o) {
        delegate.push(o);
    }

    public ValueStack clone() {
        return new ValueStackAdapter(ValueStackFactory.getFactory().createValueStack(delegate));
    }

    public Object get(String expr) {
        return delegate.findValue(expr);
    }

    public <T> T get(String expr, Class<T> requiredType) {
        return (T) delegate.findValue(expr, requiredType);
    }

    public String getString(String expr) {
        return delegate.findString(expr);
    }

    public void set(String expr, Object o) {
        delegate.set(expr, o);
    }

    public int size() {
        return delegate.size();
    }

    public Iterator<Object> iterator() {
        return delegate.getRoot().iterator();
    }
}