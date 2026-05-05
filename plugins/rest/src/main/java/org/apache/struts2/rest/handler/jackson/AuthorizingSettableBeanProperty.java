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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;

import java.io.IOException;

/**
 * A {@link SettableBeanProperty.Delegating} that authorizes each property against the
 * {@link ParameterAuthorizationContext} before delegating to the underlying property's
 * {@code deserializeAndSet}. Unauthorized properties are silently dropped — the JSON value is
 * skipped via {@link JsonParser#skipChildren()}, so any nested object graph is never instantiated
 * and setter side effects on unauthorized properties never fire.
 *
 * <p>Path tracking: the wrapper pushes the full path of the current property onto the context's
 * path stack before delegating, then pops in a {@code finally} block. For collection / map / array-typed
 * properties, the path pushed is suffixed with {@code [0]} so nested element members produce paths like
 * {@code items[0].field} — matching {@code ParametersInterceptor} depth semantics.</p>
 *
 * <p>When {@link ParameterAuthorizationContext#isActive()} is {@code false}, this wrapper is a
 * straight pass-through to the delegate — no overhead for default-config requests.</p>
 *
 * @since 7.2.0
 */
public class AuthorizingSettableBeanProperty extends SettableBeanProperty.Delegating {

    private static final Logger LOG = LogManager.getLogger(AuthorizingSettableBeanProperty.class);

    public AuthorizingSettableBeanProperty(SettableBeanProperty delegate) {
        super(delegate);
    }

    @Override
    protected SettableBeanProperty withDelegate(SettableBeanProperty d) {
        return new AuthorizingSettableBeanProperty(d);
    }

    @Override
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            delegate.deserializeAndSet(p, ctxt, instance);
            return;
        }
        String path = ParameterAuthorizationContext.pathFor(getName());
        if (!ParameterAuthorizationContext.isAuthorized(path)) {
            LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                    path, instance.getClass().getName());
            p.skipChildren();
            return;
        }
        ParameterAuthorizationContext.pushPath(prefixForNested(path));
        try {
            delegate.deserializeAndSet(p, ctxt, instance);
        } finally {
            ParameterAuthorizationContext.popPath();
        }
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return delegate.deserializeSetAndReturn(p, ctxt, instance);
        }
        String path = ParameterAuthorizationContext.pathFor(getName());
        if (!ParameterAuthorizationContext.isAuthorized(path)) {
            LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                    path, instance.getClass().getName());
            p.skipChildren();
            return instance;
        }
        ParameterAuthorizationContext.pushPath(prefixForNested(path));
        try {
            return delegate.deserializeSetAndReturn(p, ctxt, instance);
        } finally {
            ParameterAuthorizationContext.popPath();
        }
    }

    /**
     * For Collection / Map / Array properties, the path to push for nested element members is
     * {@code path + "[0]"} — matching {@code ParametersInterceptor} bracket-depth semantics. Scalar /
     * bean properties push the path unchanged.
     */
    private String prefixForNested(String pathOfThisProperty) {
        JavaType type = getType();
        if (type != null && (type.isCollectionLikeType() || type.isMapLikeType() || type.isArrayType())) {
            return pathOfThisProperty + "[0]";
        }
        return pathOfThisProperty;
    }
}
