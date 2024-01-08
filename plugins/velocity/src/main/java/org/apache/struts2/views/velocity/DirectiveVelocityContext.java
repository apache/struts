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
import org.apache.struts2.util.ValueStackProvider;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import java.util.Map;

/**
 * A Velocity {@link Context} which implements {@link ValueStackProvider} as required to render
 * {@link org.apache.struts2.views.velocity.components.AbstractDirective Struts directives}.
 *
 * @since 6.4.0
 */
public class DirectiveVelocityContext extends ChainedVelocityContext implements ValueStackProvider {

    private final transient ValueStack stack;

    public DirectiveVelocityContext(ValueStack stack, Context ...contexts) {
        super(new CompositeContext(contexts));
        this.stack = stack;
    }

    public DirectiveVelocityContext(ValueStack stack, Map<String, Object> map) {
        this(stack, new VelocityContext(map));
    }

    @Override
    public ValueStack getValueStack() {
        return stack;
    }
}
