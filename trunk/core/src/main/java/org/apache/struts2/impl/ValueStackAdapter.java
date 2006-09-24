// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import java.util.Iterator;

import org.apache.struts2.spi.ValueStack;

import com.opensymphony.xwork2.util.OgnlValueStack;

public class ValueStackAdapter implements ValueStack {

    final OgnlValueStack delegate;

    public ValueStackAdapter(OgnlValueStack delegate) {
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
        return new ValueStackAdapter(new OgnlValueStack(delegate));
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