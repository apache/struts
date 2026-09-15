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
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;

import java.io.IOException;

/**
 * Puts the members of a bean-typed {@code @JsonIdentityInfo} id under the id property's path. The
 * {@code ObjectIdReader} reads the id through its own deserializer, not the property's, so nothing
 * pushes a prefix for it and its members would be checked as the enclosing bean's. The reader uses
 * the same deserializer for a reference, so a generator that lets a reference be written as the id
 * structure puts it under the referring property's {@code id} as well. Nothing is authorized or
 * redacted here: the id property is checked when it is assigned. A redacted member leaves the id
 * bean incomplete, so the ids it should have told apart may collide and Jackson reports the
 * conflict; an id bean that does not construct at all is dropped by {@link RedactionAwareDeserializer}
 * and Jackson then fails to bind the {@code null} id. Both fail the read rather than bind a wrong id.
 */
final class ObjectIdPathDeserializer extends DelegatingDeserializer {

    private final String memberName;

    ObjectIdPathDeserializer(JsonDeserializer<?> delegate, String memberName) {
        super(delegate);
        this.memberName = memberName;
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new ObjectIdPathDeserializer(newDelegatee, memberName);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!ParameterAuthorizationContext.isActive()) {
            return super.deserialize(p, ctxt);
        }
        ParameterAuthorizationContext.pushPath(ParameterAuthorizationContext.pathFor(memberName));
        try {
            return super.deserialize(p, ctxt);
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
        ParameterAuthorizationContext.pushPath(ParameterAuthorizationContext.pathFor(memberName));
        try {
            return super.deserializeWithType(p, ctxt, typeDeserializer);
        } finally {
            ParameterAuthorizationContext.popPath();
        }
    }
}
