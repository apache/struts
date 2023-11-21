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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class StrutsVelocityContext extends VelocityContext {

    private final ValueStack stack;
    private final List<VelocityContext> chainedContexts;

    /**
     * Creates a content with link to the ValueStack and any other Velocity contexts
     *
     * @param chainedContexts Existing Velocity contexts to chain to
     * @param stack Struts ValueStack
     * @since 6.0.0
     */
    public StrutsVelocityContext(List<VelocityContext> chainedContexts, ValueStack stack) {
        this.chainedContexts = chainedContexts;
        this.stack = stack;
    }

    /**
     * @deprecated please use {@link #StrutsVelocityContext(List, ValueStack)}
     * and pass {null} or empty list if no chained contexts were defined
     */
    @Deprecated
    public StrutsVelocityContext(ValueStack stack) {
        this((List<VelocityContext>) null, stack);
    }

    /**
     * @deprecated please use {@link #StrutsVelocityContext(List, ValueStack)}
     */
    @Deprecated
    public StrutsVelocityContext(VelocityContext[] chainedContexts, ValueStack stack) {
        this(new ArrayList<>(Arrays.asList(chainedContexts)), stack);
    }

    @Override
    public boolean internalContainsKey(String key) {
        return internalGet(key) != null;
    }

    @Override
    public Object internalGet(String key) {
        for (Function<String, Object> contextGet : contextGetterList()) {
            Object val = contextGet.apply(key);
            if (val != null) {
                return val;
            }
        }
        return null;
    }

    protected List<Function<String, Object>> contextGetterList() {
        return Arrays.asList(this::superGet, this::chainedContextGet, this::stackGet);
    }

    protected Object superGet(String key) {
        return super.internalGet(key);
    }

    protected Object stackGet(String key) {
        if (stack == null) {
            return null;
        }
        return stack.findValue(key);
    }

    protected Object chainedContextGet(String key) {
        if (chainedContexts == null) {
            return null;
        }
        for (VelocityContext chainedContext : chainedContexts) {
            Object val = chainedContext.internalGet(key);
            if (val != null) {
                return val;
            }
        }
        return null;
    }
}
