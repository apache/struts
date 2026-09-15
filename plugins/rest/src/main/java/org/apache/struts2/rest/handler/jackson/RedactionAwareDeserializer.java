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
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.util.TokenBufferReadContext;
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

    /**
     * A {@code @JsonIdentityInfo} on the referring property makes Jackson build a fresh
     * {@code ObjectIdReader} here, after {@link ParameterAuthorizingModule#updateBuilder} rebuilt the
     * class-level one; give it the same treatment. A bean serialized as an array keeps the properties
     * it reads in an array of its own that {@code withObjectIdReader} does not rebuild and this wrapper
     * cannot reach, so a bean-typed id declared on the referring property of such a bean stays on the
     * enclosing path.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        JsonDeserializer<?> contextual = super.createContextual(ctxt, property);
        JsonDeserializer<?> bean = ((DelegatingDeserializer) contextual).getDelegatee();
        if (bean instanceof BeanDeserializerBase beanDeserializer && beanDeserializer.getObjectIdReader() != null) {
            ObjectIdReader reader = beanDeserializer.getObjectIdReader();
            ObjectIdReader authorized = ParameterAuthorizingModule.authorizedObjectIdReader(reader);
            if (authorized != reader) {
                return new RedactionAwareDeserializer(beanDeserializer.withObjectIdReader(authorized));
            }
        }
        return contextual;
    }

    /**
     * The context of the object or array about to be read, when the parser stands on its start token
     * or already inside it; {@code null} for a scalar, which the failed read consumes whole.
     */
    private static JsonStreamContext structuredValue(JsonParser p) {
        JsonToken token = p.currentToken();
        if (token == null || !(token.isStructStart() || token == JsonToken.FIELD_NAME)) {
            return null;
        }
        return p.getParsingContext();
    }

    /**
     * Dropping the object must leave the parser on its end token, or the fields left unread land in
     * the enclosing bean. The end is found by context identity, which every parser keeps, including
     * the token buffers Jackson replays unwrapped and any-setter values from. The one value that
     * cannot be followed is a polymorphic one Jackson reads from a parser spliced from a buffer and
     * the real parser — a type id that is not the first key, or a visible one — because that value
     * straddles the splice. The buffer holds no start token, so such a value is entered mid-object on
     * a buffer context while the parser is a splice, and that is what is refused. The test also
     * catches a type-id-first bean inside an outer value's buffer, which could have been followed;
     * its drop escalates to the enclosing bean instead, which errs on the side of dropping more.
     */
    private static boolean canResync(JsonParser p, JsonToken entry, JsonStreamContext value) {
        return value == null || !(p instanceof JsonParserSequence)
                || !(value instanceof TokenBufferReadContext) || entry != JsonToken.FIELD_NAME;
    }

    private static void skipToEndOf(JsonParser p, JsonStreamContext value) throws IOException {
        if (value == null) {
            return;
        }
        JsonToken token = p.hasCurrentToken() ? p.currentToken() : p.nextToken();
        while (token != null) {
            if (token.isStructStart()) {
                p.skipChildren();
                token = p.currentToken();
            }
            if (token.isStructEnd() && !within(p.getParsingContext(), value)) {
                return;
            }
            token = p.nextToken();
        }
    }

    private static boolean within(JsonStreamContext context, JsonStreamContext value) {
        for (JsonStreamContext current = context; current != null; current = current.getParent()) {
            if (current == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return super.deserialize(p, ctxt);
        }
        ParameterAuthorizationContext.pushRedactionScope();
        JsonToken entry = p.currentToken();
        JsonStreamContext value = structuredValue(p);
        boolean swallowed = false;
        try {
            try {
                return super.deserialize(p, ctxt);
            } catch (JsonMappingException e) {
                if (!ParameterAuthorizationContext.wasRedactedInCurrentScope()) {
                    throw e;
                }
                if (!canResync(p, entry, value)) {
                    // The nearest enclosing object that can leave its parser in order drops itself.
                    LOG.warn("REST body object of type [{}] failed to construct after @StrutsParameter " +
                                    "redaction dropped one of its properties and cannot be dropped in place; " +
                                    "leaving it to the enclosing object: {}",
                            handledType() != null ? handledType().getName() : "?", e.getMessage());
                    swallowed = true;
                    throw e;
                }
                // If this object had a property redacted AND also hit an unrelated mapping error,
                // the two are indistinguishable here, so the unrelated error is folded into
                // "object dropped". This is deliberately fail-closed: we never expose a
                // partially-built object, at the cost of a slightly less specific error.
                LOG.warn("REST body object of type [{}] failed to construct after @StrutsParameter " +
                                "redaction dropped one of its properties; treating the object as unauthorized: {}",
                        handledType() != null ? handledType().getName() : "?", e.getMessage());
                skipToEndOf(p, value);
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
