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
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;

import java.io.IOException;

/**
 * Enforces {@code @StrutsParameter} authorization on a property's value deserializer, and owns the
 * path push for its nested members. It is installed on every property by
 * {@link AuthorizingSettableBeanProperty#withValueDeserializer}, so the same path is computed whether
 * Jackson reaches the value through {@code deserializeAndSet}, through the {@code final}
 * {@code SettableBeanProperty#deserialize} used for creator parameters and buffered properties, in
 * place for a setterless collection, or through a type deserializer for a polymorphic property.
 */
final class AuthorizingValueDeserializer extends DelegatingDeserializer {

    private static final Logger LOG = LogManager.getLogger(AuthorizingValueDeserializer.class);

    private final String propertyName;
    private final JavaType propertyType;

    AuthorizingValueDeserializer(JsonDeserializer<?> delegate, String propertyName, JavaType propertyType) {
        super(delegate);
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new AuthorizingValueDeserializer(newDelegatee, propertyName, propertyType);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return super.deserialize(p, ctxt);
        }
        String path = ParameterAuthorizationContext.pathFor(propertyName);
        if (!authorize(path, p)) {
            // Returning null redacts the value. For a primitive creator component this becomes the
            // type default (0/false) unless FAIL_ON_NULL_FOR_PRIMITIVES is on (then construction
            // fails and RedactionAwareDeserializer drops the whole object) -- either way the
            // client-supplied value never lands, which is the point of the redaction.
            return null;
        }
        ParameterAuthorizationContext.pushPath(prefixForNested(path));
        try {
            return super.deserialize(p, ctxt);
        } finally {
            ParameterAuthorizationContext.popPath();
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return super.deserialize(p, ctxt, intoValue);
        }
        String path = ParameterAuthorizationContext.pathFor(propertyName);
        if (!authorize(path, p)) {
            return intoValue;
        }
        ParameterAuthorizationContext.pushPath(prefixForNested(path));
        try {
            return super.deserialize(p, ctxt, intoValue);
        } finally {
            ParameterAuthorizationContext.popPath();
        }
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
            throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return super.deserializeWithType(p, ctxt, typeDeserializer);
        }
        String path = ParameterAuthorizationContext.pathFor(propertyName);
        if (!authorize(path, p)) {
            return null;
        }
        ParameterAuthorizationContext.pushPath(prefixForNested(path));
        try {
            return super.deserializeWithType(p, ctxt, typeDeserializer);
        } finally {
            ParameterAuthorizationContext.popPath();
        }
    }

    private boolean authorize(String path, JsonParser p) throws IOException {
        if (DynamicKeyAuthorizationContext.isAuthorized(path)) {
            return true;
        }
        LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization", path);
        ParameterAuthorizationContext.markRedacted();
        p.skipChildren();
        return false;
    }

    /**
     * For Collection / Map / Array properties, the path to push for nested element members is
     * {@code path + "[0]"} -- matching {@code ParametersInterceptor} bracket-depth semantics. Scalar /
     * bean-valued properties push the path unchanged.
     */
    private String prefixForNested(String pathOfThisProperty) {
        if (propertyType != null
                && (propertyType.isCollectionLikeType() || propertyType.isMapLikeType() || propertyType.isArrayType())) {
            return pathOfThisProperty + "[0]";
        }
        return pathOfThisProperty;
    }
}
