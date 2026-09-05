/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.rest.handler.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.io.IOException;

/**
 * Requires an explicit {@link StrutsParameter#allowDynamicKeys()} opt-in before a Jackson
 * any-setter can consume dynamic REST body properties.
 */
final class AuthorizingSettableAnyProperty extends SettableAnyProperty {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(AuthorizingSettableAnyProperty.class);
    private static final Object REJECTED_VALUE = new Object();

    private final SettableAnyProperty delegate;
    private final StrutsParameter permission;
    private final boolean creatorParameter;

    AuthorizingSettableAnyProperty(SettableAnyProperty delegate) {
        super(delegate.getProperty(), memberOf(delegate.getProperty()), delegate.getType(),
                null, null, null);
        this.delegate = delegate;
        this.permission = permissionOf(delegate.getProperty());
        this.creatorParameter = delegate.getParameterIndex() >= 0;
    }

    private static AnnotatedMember memberOf(BeanProperty property) {
        return property == null ? null : property.getMember();
    }

    private static StrutsParameter permissionOf(BeanProperty property) {
        AnnotatedMember member = memberOf(property);
        return member == null ? null : member.getAnnotation(StrutsParameter.class);
    }

    @Override
    public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deserializer) {
        return new AuthorizingSettableAnyProperty(delegate.withValueDeserializer(deserializer));
    }

    @Override
    public void fixAccess(DeserializationConfig config) {
        delegate.fixAccess(config);
    }

    @Override
    public boolean hasValueDeserializer() {
        return delegate.hasValueDeserializer();
    }

    @Override
    public String getPropertyName() {
        return delegate.getPropertyName();
    }

    @Override
    public int getParameterIndex() {
        return delegate.getParameterIndex();
    }

    @Override
    public boolean isFieldType() {
        return delegate.isFieldType();
    }

    @Override
    public boolean isSetterType() {
        return delegate.isSetterType();
    }

    @Override
    public Object createParameterObject() {
        return delegate.createParameterObject();
    }

    @Override
    public void deserializeAndSet(JsonParser parser, DeserializationContext context,
                                  Object instance, String propertyName) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            delegate.deserializeAndSet(parser, context, instance, propertyName);
            return;
        }

        String path = ParameterAuthorizationContext.pathFor(propertyName);
        int allowedDepth = allowedDepth(path);
        if (allowedDepth < 0) {
            rejectPermission(parser, path);
            return;
        }

        try (TokenBuffer value = context.bufferAsCopyOfValue(parser)) {
            int valueDepth = valueDepth(value);
            if (valueDepth > allowedDepth) {
                rejectDepth(parser, path, valueDepth, allowedDepth);
                return;
            }
            try (JsonParser replay = value.asParserOnFirstToken()) {
                boolean scopePushed = false;
                boolean pathPushed = false;
                try {
                    DynamicKeyAuthorizationContext.push(path, allowedDepth);
                    scopePushed = true;
                    ParameterAuthorizationContext.pushPath(prefixForNested(path));
                    pathPushed = true;
                    delegate.deserializeAndSet(replay, context, instance, propertyName);
                } finally {
                    if (pathPushed) {
                        ParameterAuthorizationContext.popPath();
                    }
                    if (scopePushed) {
                        DynamicKeyAuthorizationContext.pop();
                    }
                }
            }
        }
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return delegate.deserialize(parser, context);
        }

        String propertyName = parser.currentName();
        if (propertyName == null) {
            rejectMissingPropertyName(parser);
            return REJECTED_VALUE;
        }

        String path = ParameterAuthorizationContext.pathFor(propertyName);
        int allowedDepth = allowedDepth(path);
        if (allowedDepth < 0) {
            rejectPermission(parser, path);
            return REJECTED_VALUE;
        }

        try (TokenBuffer value = context.bufferAsCopyOfValue(parser)) {
            int valueDepth = valueDepth(value);
            if (valueDepth > allowedDepth) {
                rejectDepth(parser, path, valueDepth, allowedDepth);
                return REJECTED_VALUE;
            }
            try (JsonParser replay = value.asParserOnFirstToken()) {
                boolean scopePushed = false;
                boolean pathPushed = false;
                try {
                    DynamicKeyAuthorizationContext.push(path, allowedDepth);
                    scopePushed = true;
                    ParameterAuthorizationContext.pushPath(prefixForNested(path));
                    pathPushed = true;
                    return delegate.deserialize(replay, context);
                } finally {
                    if (pathPushed) {
                        ParameterAuthorizationContext.popPath();
                    }
                    if (scopePushed) {
                        DynamicKeyAuthorizationContext.pop();
                    }
                }
            }
        }
    }

    @Override
    public void set(Object instance, Object propertyName, Object value) throws IOException {
        if (value != REJECTED_VALUE) {
            delegate.set(instance, propertyName, value);
        }
    }

    @Override
    protected void _set(Object instance, Object propertyName, Object value) throws Exception {
        if (value != REJECTED_VALUE) {
            delegate.set(instance, propertyName, value);
        }
    }

    private int allowedDepth(String path) {
        if (creatorParameter || permission == null || !permission.allowDynamicKeys()) {
            return -1;
        }
        return DynamicKeyAuthorizationContext.limitForNestedScope(path, permission.depth());
    }

    private void rejectPermission(JsonParser parser, String path) throws IOException {
        if (creatorParameter) {
            LOG.warn("REST body creator-parameter any-setter [{}] rejected; dynamic-key consent "
                    + "can only be declared on an any-setter method or field", path);
        } else {
            LOG.warn("REST body any-setter parameter [{}] rejected; dynamic keys require "
                    + "@StrutsParameter(allowDynamicKeys = true) on a method or field", path);
        }
        redactAndSkip(parser);
    }

    private void rejectDepth(JsonParser parser, String path, int valueDepth, int allowedDepth) throws IOException {
        LOG.warn("REST body any-setter parameter [{}] rejected; value depth [{}] exceeds "
                + "@StrutsParameter depth [{}]", path, valueDepth, allowedDepth);
        redactAndSkip(parser);
    }

    private void rejectMissingPropertyName(JsonParser parser) throws IOException {
        LOG.warn("REST body any-setter parameter rejected; dynamic property name is unavailable");
        redactAndSkip(parser);
    }

    private void redactAndSkip(JsonParser parser) throws IOException {
        ParameterAuthorizationContext.markRedacted();
        parser.skipChildren();
    }

    private int valueDepth(TokenBuffer value) throws IOException {
        int currentDepth = 0;
        int maximumDepth = 0;
        try (JsonParser parser = value.asParserOnFirstToken()) {
            for (JsonToken token = parser.currentToken(); token != null; token = parser.nextToken()) {
                if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                    currentDepth++;
                    maximumDepth = Math.max(maximumDepth, currentDepth);
                } else if (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY) {
                    currentDepth--;
                }
            }
        }
        return maximumDepth;
    }

    private String prefixForNested(String path) {
        JavaType type = delegate.getType();
        if (type != null && (type.isCollectionLikeType() || type.isMapLikeType() || type.isArrayType())) {
            return path + "[0]";
        }
        return path;
    }
}
