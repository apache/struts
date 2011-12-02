/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import java.util.Map;
import java.util.Set;

/**
 * Creates an Ognl value stack
 */
public class OgnlValueStackFactory implements ValueStackFactory {
    
    private XWorkConverter xworkConverter;
    private CompoundRootAccessor compoundRootAccessor;
    private TextProvider textProvider;
    private Container container;
    private boolean allowStaticMethodAccess;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }
    
    @Inject("system")
    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }
    
    @Inject(value="allowStaticMethodAccess", required=false)
    public void setAllowStaticMethodAccess(String allowStaticMethodAccess) {
        this.allowStaticMethodAccess = "true".equalsIgnoreCase(allowStaticMethodAccess);
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
    public void setContainer(Container container) throws ClassNotFoundException {
        Set<String> names = container.getInstanceNames(PropertyAccessor.class);
        if (names != null) {
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
        }
        
        names = container.getInstanceNames(MethodAccessor.class);
        if (names != null) {
            for (String name : names) {
                Class cls = Class.forName(name);
                if (cls != null) {
                    OgnlRuntime.setMethodAccessor(cls, container.getInstance(MethodAccessor.class, name));
                }
            }
        }
        
        names = container.getInstanceNames(NullHandler.class);
        if (names != null) {
            for (String name : names) {
                Class cls = Class.forName(name);
                if (cls != null) {
                    OgnlRuntime.setNullHandler(cls, new OgnlNullHandlerWrapper(container.getInstance(NullHandler.class, name)));
                }
            }
        }
        if (compoundRootAccessor == null) {
            throw new IllegalStateException("Couldn't find the compound root accessor");
        }
        this.container = container;
    }
}
