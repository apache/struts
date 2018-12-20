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
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import ognl.MethodAccessor;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;

/**
 * Creates an Ognl value stack
 */
public class OgnlValueStackFactory implements ValueStackFactory {
    
	private static final Logger LOG = LogManager.getLogger(OgnlValueStackFactory.class);

    protected XWorkConverter xworkConverter;
    protected CompoundRootAccessor compoundRootAccessor;
    protected TextProvider textProvider;
    protected Container container;
    private boolean allowStaticMethodAccess;

    @Inject
    protected void setXWorkConverter(XWorkConverter converter) {
        this.xworkConverter = converter;
    }
    
    @Inject("system")
    protected void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }
    
    @Inject(value="allowStaticMethodAccess", required=false)
    protected void setAllowStaticMethodAccess(String allowStaticMethodAccess) {
        this.allowStaticMethodAccess = BooleanUtils.toBoolean(allowStaticMethodAccess);
        if (this.allowStaticMethodAccess) {
            LOG.warn("Setting allow static method access [{}] affects the safety of your application!",
                        this.allowStaticMethodAccess);
        }
    }

    public ValueStack createValueStack() {
        ValueStack stack = new OgnlValueStack(xworkConverter, compoundRootAccessor, textProvider, allowStaticMethodAccess);
        container.inject(stack);
        stack.getContext().put(ActionContext.CONTAINER, container);
        return stack;
    }

    public ValueStack createValueStack(ValueStack stack) {
        ValueStack result = new OgnlValueStack(stack, xworkConverter, compoundRootAccessor, allowStaticMethodAccess);
        container.inject(result);
        stack.getContext().put(ActionContext.CONTAINER, container);
        return result;
    }
    
    @Inject
    protected void setContainer(Container container) throws ClassNotFoundException {
        Set<String> names = container.getInstanceNames(PropertyAccessor.class);
        for (String name : names) {
            Class cls = Class.forName(name);
            if (cls != null) {
                if (Map.class.isAssignableFrom(cls)) {
                    PropertyAccessor acc = container.getInstance(PropertyAccessor.class, name);
                }
                OgnlRuntime.setPropertyAccessor(cls, container.getInstance(PropertyAccessor.class, name));
                if (compoundRootAccessor == null && CompoundRoot.class.isAssignableFrom(cls)) {
                    compoundRootAccessor = (CompoundRootAccessor) container.getInstance(PropertyAccessor.class, name);
                }
            }
        }

        names = container.getInstanceNames(MethodAccessor.class);
        for (String name : names) {
            Class cls = Class.forName(name);
            if (cls != null) {
                OgnlRuntime.setMethodAccessor(cls, container.getInstance(MethodAccessor.class, name));
            }
        }

        names = container.getInstanceNames(NullHandler.class);
        for (String name : names) {
            Class cls = Class.forName(name);
            if (cls != null) {
                OgnlRuntime.setNullHandler(cls, new OgnlNullHandlerWrapper(container.getInstance(NullHandler.class, name)));
            }
        }
        if (compoundRootAccessor == null) {
            throw new IllegalStateException("Couldn't find the compound root accessor");
        }
        this.container = container;
    }
}
