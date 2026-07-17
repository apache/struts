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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;

import java.io.IOException;

/**
 * Wraps every bean-type deserializer so that a construction failure caused by
 * {@link AuthorizingValueDeserializer} / {@link AuthorizingSettableBeanProperty} substituting a
 * redacted ({@code null}) value for an unauthorized property -- e.g. a record's compact constructor
 * rejecting a {@code null} it requires, a primitive creator parameter that can't hold {@code null}
 * under {@code DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES}, or a builder's {@code build()}
 * validating a redacted field -- is treated the same way a rejected non-creator nested property
 * already is: the whole object is dropped ({@code null}), rather than the raw
 * {@link JsonMappingException} crashing the entire request body's deserialization.
 *
 * <p>Installed via {@link ParameterAuthorizingModule#modifyDeserializer}, scoped to every bean
 * deserializer (per-object construction), regardless of whether that particular bean turns out to
 * be creator-bound, builder-bound, or plain setter/field-bound -- the redaction scope this pushes is
 * a no-op unless something inside actually calls
 * {@link ParameterAuthorizationContext#markRedacted()}.</p>
 *
 * <p>Only {@link JsonMappingException} thrown while <em>this object's own</em> redaction scope is
 * marked is swallowed. A construction failure with no redaction recorded in the current scope is a
 * genuine client/data error, unrelated to authorization, and is rethrown unchanged.</p>
 */
final class RedactionAwareDeserializer extends DelegatingDeserializer {

    private static final Logger LOG = LogManager.getLogger(RedactionAwareDeserializer.class);

    RedactionAwareDeserializer(JsonDeserializer<?> delegate) {
        super(delegate);
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new RedactionAwareDeserializer(newDelegatee);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return super.deserialize(p, ctxt);
        }
        ParameterAuthorizationContext.pushRedactionScope();
        boolean swallowed = false;
        try {
            try {
                return super.deserialize(p, ctxt);
            } catch (JsonMappingException e) {
                if (!ParameterAuthorizationContext.wasRedactedInCurrentScope()) {
                    throw e;
                }
                // If this object had a property redacted AND also hit an unrelated mapping error,
                // the two are indistinguishable here, so the unrelated error is folded into
                // "object dropped". This is deliberately fail-closed: we never expose a
                // partially-built object, at the cost of a slightly less specific error.
                LOG.warn("REST body object of type [{}] failed to construct after @StrutsParameter " +
                                "redaction dropped one of its properties; treating the object as unauthorized: {}",
                        handledType() != null ? handledType().getName() : "?", e.getMessage());
                swallowed = true;
                return null;
            }
        } finally {
            ParameterAuthorizationContext.popRedactionScope();
            if (swallowed) {
                ParameterAuthorizationContext.markRedacted();
            }
        }
    }
}
