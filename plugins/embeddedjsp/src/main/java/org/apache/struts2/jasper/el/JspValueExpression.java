/*
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
package org.apache.struts2.jasper.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;

/**
 * Wrapper for providing context to ValueExpressions
 * 
 * @author Jacob Hookom
 */
public final class JspValueExpression extends ValueExpression implements
        Externalizable {

    private ValueExpression target;

    private String mark;

    public JspValueExpression() {
        super();
    }

    public JspValueExpression(String mark, ValueExpression target) {
        this.target = target;
        this.mark = mark;
    }

    public Class<?> getExpectedType() {
        return this.target.getExpectedType();
    }

    public Class<?> getType(ELContext context) throws NullPointerException,
            PropertyNotFoundException, ELException {
        try {
            return this.target.getType(context);
        } catch (PropertyNotFoundException e) {
            if (e instanceof JspPropertyNotFoundException) throw e;
            throw new JspPropertyNotFoundException(this.mark, e);
        } catch (ELException e) {
            if (e instanceof JspELException) throw e;
            throw new JspELException(this.mark, e);
        }
    }

    public boolean isReadOnly(ELContext context) throws NullPointerException,
            PropertyNotFoundException, ELException {
        try {
            return this.target.isReadOnly(context);
        } catch (PropertyNotFoundException e) {
            if (e instanceof JspPropertyNotFoundException) throw e;
            throw new JspPropertyNotFoundException(this.mark, e);
        } catch (ELException e) {
            if (e instanceof JspELException) throw e;
            throw new JspELException(this.mark, e);
        }
    }

    public void setValue(ELContext context, Object value)
            throws NullPointerException, PropertyNotFoundException,
            PropertyNotWritableException, ELException {
        try {
            this.target.setValue(context, value);
        } catch (PropertyNotWritableException e) {
            if (e instanceof JspPropertyNotWritableException) throw e;
            throw new JspPropertyNotWritableException(this.mark, e);
        } catch (PropertyNotFoundException e) {
            if (e instanceof JspPropertyNotFoundException) throw e;
            throw new JspPropertyNotFoundException(this.mark, e);
        } catch (ELException e) {
            if (e instanceof JspELException) throw e;
            throw new JspELException(this.mark, e);
        }
    }

    public Object getValue(ELContext context) throws NullPointerException,
            PropertyNotFoundException, ELException {
        try {
            return this.target.getValue(context);
        } catch (PropertyNotFoundException e) {
            if (e instanceof JspPropertyNotFoundException) throw e;
            throw new JspPropertyNotFoundException(this.mark, e);
        } catch (ELException e) {
            if (e instanceof JspELException) throw e;
            throw new JspELException(this.mark, e);
        }
    }

    public boolean equals(Object obj) {
        return this.target.equals(obj);
    }

    public int hashCode() {
        return this.target.hashCode();
    }

    public String getExpressionString() {
        return this.target.getExpressionString();
    }

    public boolean isLiteralText() {
        return this.target.isLiteralText();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.mark);
        out.writeObject(this.target);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.mark = in.readUTF();
        this.target = (ValueExpression) in.readObject();
    }
}
