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
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Enforces {@code @StrutsParameter} authorization for creator-bound properties (Java records,
 * {@code @JsonCreator} constructors, {@code @ConstructorProperties}), which Jackson deserializes
 * through the value deserializer directly rather than through a {@code SettableBeanProperty}.
 * See {@link AuthorizingSettableBeanProperty#withValueDeserializer} for where this is installed.
 */
final class AuthorizingValueDeserializer extends DelegatingDeserializer {

    private static final Logger LOG = LogManager.getLogger(AuthorizingValueDeserializer.class);

    private final String propertyName;

    AuthorizingValueDeserializer(JsonDeserializer<?> delegate, String propertyName) {
        super(delegate);
        this.propertyName = propertyName;
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new AuthorizingValueDeserializer(newDelegatee, propertyName);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return super.deserialize(p, ctxt);
        }
        String path = ParameterAuthorizationContext.pathFor(propertyName);
        if (!ParameterAuthorizationContext.isAuthorized(path)) {
            LOG.warn("REST body parameter [{}] rejected by @StrutsParameter authorization (creator-bound property)", path);
            ParameterAuthorizationContext.markRedacted();
            p.skipChildren();
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

    /**
     * For Collection / Map / Array-valued creator parameters, the path to push for nested element
     * members is {@code path + "[0]"} -- matching {@code ParametersInterceptor} bracket-depth
     * semantics, and {@link AuthorizingSettableBeanProperty#prefixForNested}. Scalar / bean-valued
     * parameters push the path unchanged.
     */
    private String prefixForNested(String pathOfThisProperty) {
        Class<?> type = handledType();
        if (type != null && (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type) || type.isArray())) {
            return pathOfThisProperty + "[0]";
        }
        return pathOfThisProperty;
    }
}
