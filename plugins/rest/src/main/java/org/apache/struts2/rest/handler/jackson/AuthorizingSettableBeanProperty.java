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
import com.fasterxml.jackson.databind.JsonDeserializer;
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
 * <p>Path tracking for nested members is done by the {@link AuthorizingValueDeserializer} wrapped
 * around every property's value deserializer, so the direct path and the buffered creator path
 * compute the same paths. Values Jackson assigns after construction go through {@link #set} and
 * {@link #setAndReturn}, which apply the same authorization to the already-materialized value.</p>
 *
 * <p>When {@link ParameterAuthorizationContext#isActive()} is {@code false}, this wrapper is a
 * straight pass-through to the delegate — no overhead for default-config requests.</p>
 *
 * @since 7.2.0
 */
public class AuthorizingSettableBeanProperty extends SettableBeanProperty.Delegating {

    private static final Logger LOG = LogManager.getLogger(AuthorizingSettableBeanProperty.class);

    private final String memberName;

    /**
     * @deprecated keys authorization on the wire name; use
     * {@link #AuthorizingSettableBeanProperty(SettableBeanProperty, String)} with the Java member name
     */
    @Deprecated(since = "7.4.0", forRemoval = true)
    public AuthorizingSettableBeanProperty(SettableBeanProperty delegate) {
        this(delegate, delegate.getName());
    }

    /**
     * @param memberName the Java member name the authorizer resolves, which differs from
     *                   {@link #getName()} once the property is renamed on the wire
     */
    public AuthorizingSettableBeanProperty(SettableBeanProperty delegate, String memberName) {
        super(delegate);
        this.memberName = memberName;
    }

    @Override
    protected SettableBeanProperty withDelegate(SettableBeanProperty d) {
        return new AuthorizingSettableBeanProperty(d, memberName);
    }

    /**
     * Creator-bound properties, and non-creator properties Jackson buffers while collecting creator
     * parameters, never reach {@link #deserializeAndSet}/{@link #deserializeSetAndReturn}: Jackson calls
     * the {@code final} {@code SettableBeanProperty#deserialize} directly, through this property's own
     * value deserializer. Wrap that deserializer with {@link AuthorizingValueDeserializer} for every
     * property; it owns the path push for nested members on both the direct and the buffered path.
     */
    @Override
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        JsonDeserializer<?> effective = deser;
        if (!(deser instanceof AuthorizingValueDeserializer)) {
            effective = new AuthorizingValueDeserializer(deser, memberName, getType());
        }
        return _with(delegate.withValueDeserializer(effective));
    }

    @Override
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            delegate.deserializeAndSet(p, ctxt, instance);
            return;
        }
        String path = ParameterAuthorizationContext.pathFor(memberName);
        if (!DynamicKeyAuthorizationContext.isAuthorized(path)) {
            LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                    path, instance.getClass().getName());
            ParameterAuthorizationContext.markRedacted();
            p.skipChildren();
            return;
        }
        delegate.deserializeAndSet(p, ctxt, instance);
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return delegate.deserializeSetAndReturn(p, ctxt, instance);
        }
        String path = ParameterAuthorizationContext.pathFor(memberName);
        if (!DynamicKeyAuthorizationContext.isAuthorized(path)) {
            LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                    path, instance.getClass().getName());
            ParameterAuthorizationContext.markRedacted();
            p.skipChildren();
            return instance;
        }
        return delegate.deserializeSetAndReturn(p, ctxt, instance);
    }

    @Override
    public void set(Object instance, Object value) throws IOException {
        if (isAuthorizedForSet(instance)) {
            delegate.set(instance, value);
        }
    }

    @Override
    public Object setAndReturn(Object instance, Object value) throws IOException {
        if (isAuthorizedForSet(instance)) {
            return delegate.setAndReturn(instance, value);
        }
        return instance;
    }

    /**
     * Guards the already-materialized assignment path: Jackson buffers non-creator properties seen
     * before the last creator parameter and assigns them after construction via
     * {@code PropertyValue.Regular.assign} -> {@code set()}, which does not go through
     * {@link #deserializeAndSet}.
     */
    private boolean isAuthorizedForSet(Object instance) {
        if (!ParameterAuthorizationContext.isActive()) {
            return true;
        }
        String path = ParameterAuthorizationContext.pathFor(memberName);
        if (DynamicKeyAuthorizationContext.isAuthorized(path)) {
            return true;
        }
        LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                path, instance.getClass().getName());
        ParameterAuthorizationContext.markRedacted();
        return false;
    }
}
