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
package org.apache.struts2.rest.handler.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.Iterator;

/**
 * Jackson {@link SimpleModule} that wraps every {@link SettableBeanProperty} on every bean type
 * with an {@link AuthorizingSettableBeanProperty}, enforcing {@code @StrutsParameter} authorization
 * during deserialization via the {@link org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext}
 * ThreadLocal.
 *
 * <p>Register this module once on each handler's mapper (e.g. in the constructor). All per-request
 * authorization state is read from the ThreadLocal context, so the module + mapper combination is
 * thread-safe and reusable across requests.</p>
 *
 * @since 7.2.0
 */
public class ParameterAuthorizingModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public ParameterAuthorizingModule() {
        setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                                                         BeanDescription beanDesc,
                                                         BeanDeserializerBuilder builder) {
                Iterator<SettableBeanProperty> it = builder.getProperties();
                while (it.hasNext()) {
                    SettableBeanProperty original = it.next();
                    if (original instanceof AuthorizingSettableBeanProperty) {
                        continue; // idempotent; protect against double-registration
                    }
                    builder.addOrReplaceProperty(new AuthorizingSettableBeanProperty(original), true);
                }
                return builder;
            }
        });
    }
}
