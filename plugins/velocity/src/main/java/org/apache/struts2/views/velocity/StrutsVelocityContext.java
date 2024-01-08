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
package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import java.util.List;

/**
 * A Velocity {@link Context} which falls back to a {@link ValueStack} lookup after looking in any other provided
 * Velocity contexts.
 */
public class StrutsVelocityContext extends DirectiveVelocityContext {

    /**
     * @since 6.4.0.
     */
    public StrutsVelocityContext(ValueStack stack, Context ...contexts) {
        super(stack, contexts);
    }

    /**
     * Creates a content with link to the ValueStack and any other Velocity contexts
     *
     * @param chainedContexts Existing Velocity contexts to chain to
     * @param stack           Struts ValueStack
     * @since 6.0.0
     */
    public StrutsVelocityContext(List<? extends Context> chainedContexts, ValueStack stack) {
        super(stack, chainedContexts == null ? new Context[0] : chainedContexts.toArray(new Context[0]));
    }

    /**
     * @deprecated since 6.0.0, use {@link #StrutsVelocityContext(ValueStack, Context...)} instead.
     */
    @Deprecated
    public StrutsVelocityContext(ValueStack stack) {
        this(stack, new Context[0]);
    }

    /**
     * @deprecated since 6.0.0, use {@link #StrutsVelocityContext(ValueStack, Context...)} instead.
     */
    @Deprecated
    public StrutsVelocityContext(VelocityContext[] chainedContexts, ValueStack stack) {
        this(stack, chainedContexts);
    }

    @Override
    public Object get(String key) {
        Object value = super.get(key);
        if (value == null && getValueStack() != null) {
            value = getValueStack().findValue(key);
        }
        return value;
    }
}
