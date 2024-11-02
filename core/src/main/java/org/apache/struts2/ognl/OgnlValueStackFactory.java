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
package org.apache.struts2.ognl;

import org.apache.struts2.text.TextProvider;
import org.apache.struts2.conversion.NullHandler;
import org.apache.struts2.conversion.impl.XWorkConverter;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.accessor.RootAccessor;
import org.apache.struts2.util.CompoundRoot;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import ognl.MethodAccessor;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.util.Set;

/**
 * Creates an Ognl value stack
 */
public class OgnlValueStackFactory implements ValueStackFactory {

    private static final Logger LOG = LogManager.getLogger(OgnlValueStackFactory.class);

    protected XWorkConverter xworkConverter;
    protected RootAccessor compoundRootAccessor;
    protected TextProvider textProvider;
    protected Container container;

    @Inject
    protected void setXWorkConverter(XWorkConverter converter) {
        this.xworkConverter = converter;
    }

    @Inject
    protected void setCompoundRootAccessor(RootAccessor compoundRootAccessor) {
        this.compoundRootAccessor = compoundRootAccessor;
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, compoundRootAccessor);
        OgnlRuntime.setMethodAccessor(CompoundRoot.class, compoundRootAccessor);
    }

    @Inject
    protected void setMethodAccessor(MethodAccessor methodAccessor) {
        OgnlRuntime.setMethodAccessor(Object.class, methodAccessor);
    }

    @Inject("system")
    protected void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    @Override
    public ValueStack createValueStack() {
        return createValueStack(null, true);
    }

    @Override
    public ValueStack createValueStack(ValueStack stack) {
        return createValueStack(stack, false);
    }

    protected ValueStack createValueStack(ValueStack stack, boolean useTextProvider) {
        ValueStack newStack = new OgnlValueStack(
                stack, xworkConverter, compoundRootAccessor, useTextProvider ? textProvider : null, container.getInstance(SecurityMemberAccess.class));
        container.inject(newStack);
        return newStack.getActionContext().withContainer(container).withValueStack(newStack).getValueStack();
    }

    /**
     * {@link PropertyAccessor}'s, {@link MethodAccessor}'s and {@link NullHandler}'s are registered on a per-class
     * basis by defining a bean adhering to the corresponding interface with a name corresponding to the class it is
     * intended to handle.
     * <p>
     * The only exception is the {@link MethodAccessor} for the {@link Object} type which has its own extension point.
     *
     * @see #setMethodAccessor(MethodAccessor)
     * @see #registerAdditionalMethodAccessors()
     */
    @Inject
    protected void setContainer(Container container) throws ClassNotFoundException {
        this.container = container;
        registerPropertyAccessors();
        registerNullHandlers();
        registerAdditionalMethodAccessors();
    }

    /**
     * Note that the default {@link MethodAccessor} for handling {@link Object} methods is registered in
     * {@link #setMethodAccessor} and can be configured using the extension point
     * {@link StrutsConstants#STRUTS_METHOD_ACCESSOR}.
     */
    protected void registerAdditionalMethodAccessors() {
        Set<String> names = container.getInstanceNames(MethodAccessor.class);
        for (String name : names) {
            Class<?> cls;
            try {
                cls = Class.forName(name);
                if (cls.equals(Object.class)) {
                    // The Object method accessor can only be configured using the struts.methodAccessor extension point
                    continue;
                }
                if (cls.equals(CompoundRoot.class)) {
                    // TODO: This bean is deprecated, please remove this if statement when removing the struts-beans.xml entry
                    continue;
                }
            } catch (ClassNotFoundException e) {
                // Since this interface is also used as an extension point for the Object MethodAccessor, we expect
                // there to be beans with names that don't correspond to classes. We can safely ignore these.
                continue;
            }
            MethodAccessor methodAccessor = container.getInstance(MethodAccessor.class, name);
            OgnlRuntime.setMethodAccessor(cls, methodAccessor);
            LOG.debug("Registered custom OGNL MethodAccessor [{}] for class [{}]", methodAccessor.getClass().getName(), cls.getName());
        }
    }

    protected void registerNullHandlers() throws ClassNotFoundException {
        Set<String> names = container.getInstanceNames(NullHandler.class);
        for (String name : names) {
            Class<?> cls = Class.forName(name);
            NullHandler nullHandler = container.getInstance(NullHandler.class, name);
            OgnlRuntime.setNullHandler(cls, new OgnlNullHandlerWrapper(nullHandler));
            LOG.debug("Registered custom OGNL NullHandler [{}] for class [{}]", nullHandler.getClass().getName(), cls.getName());
        }
    }

    protected void registerPropertyAccessors() throws ClassNotFoundException {
        Set<String> names = container.getInstanceNames(PropertyAccessor.class);
        for (String name : names) {
            Class<?> cls = Class.forName(name);
            if (cls.equals(CompoundRoot.class)) {
                // TODO: This bean is deprecated, please remove this if statement when removing the struts-beans.xml entry
                continue;
            }
            PropertyAccessor propertyAccessor = container.getInstance(PropertyAccessor.class, name);
            OgnlRuntime.setPropertyAccessor(cls, propertyAccessor);
            LOG.debug("Registered custom OGNL PropertyAccessor [{}] for class [{}]", propertyAccessor.getClass().getName(), cls.getName());
        }
    }
}
