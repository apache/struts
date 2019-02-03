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
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.EarlyInitializable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.ognl.accessor.ParameterPropertyAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import ognl.MethodAccessor;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.struts.util.PropertyMessageResources;

import java.util.Set;

/**
 * Creates an Ognl value stack
 */
public class OgnlValueStackFactory implements ValueStackFactory, EarlyInitializable {
    
    private static final Logger LOG = LogManager.getLogger(OgnlValueStackFactory.class);

    protected XWorkConverter xworkConverter;
    protected CompoundRootAccessor compoundRootAccessor;
    protected TextProvider textProvider;
    protected Container container;

    @Inject
    protected void setXWorkConverter(XWorkConverter converter) {
        this.xworkConverter = converter;
    }
    
    @Inject("system")
    protected void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    public ValueStack createValueStack() {
        final boolean allowStaticMethodAccess = isAllowStaticMethodAccess();
        ValueStack stack = new OgnlValueStack(xworkConverter, compoundRootAccessor, textProvider, allowStaticMethodAccess);
        container.inject(stack);
        stack.getContext().put(ActionContext.CONTAINER, container);
        return stack;
    }

    public ValueStack createValueStack(ValueStack stack) {
        final boolean allowStaticMethodAccess = isAllowStaticMethodAccess();
        ValueStack result = new OgnlValueStack(stack, xworkConverter, compoundRootAccessor, allowStaticMethodAccess);
        container.inject(result);
        stack.getContext().put(ActionContext.CONTAINER, container);
        return result;
    }

    boolean isAllowStaticMethodAccess() {
        if (container == null) {
            LOG.warn("Container is null, ValueStack created out of action flow?");
            return false;
        } else {
            return BooleanUtils.toBoolean(container.getInstance(String.class, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS));
        }
    }

    @Inject
    protected void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public void init() {
        Set<String> names = container.getInstanceNames(PropertyAccessor.class);
        for (String name : names) {
            Class cls = loadClass(name);
            if (cls != null) {
                OgnlRuntime.setPropertyAccessor(cls, container.getInstance(PropertyAccessor.class, name));
                if (compoundRootAccessor == null && CompoundRoot.class.isAssignableFrom(cls)) {
                    compoundRootAccessor = (CompoundRootAccessor) container.getInstance(PropertyAccessor.class, name);
                }
            }
        }

        names = container.getInstanceNames(MethodAccessor.class);
        for (String name : names) {
            Class cls = loadClass(name);
            if (cls != null) {
                OgnlRuntime.setMethodAccessor(cls, container.getInstance(MethodAccessor.class, name));
            }
        }

        names = container.getInstanceNames(NullHandler.class);
        for (String name : names) {
            Class cls = loadClass(name);
            if (cls != null) {
                OgnlRuntime.setNullHandler(cls, new OgnlNullHandlerWrapper(container.getInstance(NullHandler.class, name)));
            }
        }
        if (compoundRootAccessor == null) {
            throw new IllegalStateException("Couldn't find the compound root accessor");
        }
    }

    private Class loadClass(String name) {
        Class cls = null;
        try {
            cls = Class.forName(name);
        } catch (ClassNotFoundException e) {
            LOG.warn(new ParameterizedMessage("Cannot load class [{}]", name), e);
        }
        return cls;
    }
}
