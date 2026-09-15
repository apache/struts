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
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.beans.Introspector;
import java.util.Iterator;
import java.util.List;

/**
 * Jackson {@link SimpleModule} that wraps every {@link SettableBeanProperty} on every bean type
 * with an {@link AuthorizingSettableBeanProperty}, enforcing {@code @StrutsParameter} authorization
 * during deserialization via the {@link org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext}
 * ThreadLocal. Each wrapper is keyed by the Java member Jackson invokes for the property, not the
 * external name a {@code @JsonProperty} or naming strategy puts on the wire, since the authorizer
 * resolves the path against the member.
 *
 * <p>Register this module once on each handler's mapper (e.g. in the constructor). All per-request
 * authorization state is read from the ThreadLocal context, so the module + mapper combination is
 * thread-safe and reusable across requests.</p>
 *
 * @since 7.2.0
 */
public class ParameterAuthorizingModule extends SimpleModule {

    private static final long serialVersionUID = 1L;
    private static final List<String> MUTATOR_PREFIXES = List.of("set");
    private static final List<String> GETTER_PREFIXES = List.of("get", "is");
    private volatile boolean requireAnySetterAnnotations;

    public ParameterAuthorizingModule() {
        this(false);
    }

    public ParameterAuthorizingModule(boolean requireAnySetterAnnotations) {
        this.requireAnySetterAnnotations = requireAnySetterAnnotations;
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
                    builder.addOrReplaceProperty(
                            new AuthorizingSettableBeanProperty(original, memberNameOf(original)), true);
                }
                authorizeObjectIdProperty(builder);
                if (ParameterAuthorizingModule.this.requireAnySetterAnnotations) {
                    SettableAnyProperty anySetter = builder.getAnySetter();
                    if (anySetter != null && !(anySetter instanceof AuthorizingSettableAnyProperty)) {
                        builder.setAnySetter(null);
                        builder.setAnySetter(new AuthorizingSettableAnyProperty(anySetter));
                    }
                }
                return builder;
            }

            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                           BeanDescription beanDesc,
                                                           JsonDeserializer<?> deserializer) {
                if (deserializer instanceof RedactionAwareDeserializer) {
                    return deserializer; // idempotent; protect against double-registration
                }
                return new RedactionAwareDeserializer(deserializer);
            }
        });
    }

    /**
     * Jackson builds the {@code ObjectIdReader} for a property-based {@code @JsonIdentityInfo} before
     * the deserializer modifiers run, capturing the id property as it was then, and the
     * {@code ObjectIdValueProperty} it adds at build time assigns the id through that captured
     * property rather than through the builder's. Rebuild the reader around a wrapped one, and around
     * a deserializer that puts a bean-typed id's members under the id property's path.
     */
    private static void authorizeObjectIdProperty(BeanDeserializerBuilder builder) {
        ObjectIdReader reader = builder.getObjectIdReader();
        if (reader != null) {
            builder.setObjectIdReader(authorizedObjectIdReader(reader));
        }
    }

    /**
     * The same rebuild for a reader Jackson constructs later, in {@code createContextual}, for a
     * {@code @JsonIdentityInfo} placed on the referring property; returns the reader itself when it
     * carries no id property or is already rebuilt.
     */
    static ObjectIdReader authorizedObjectIdReader(ObjectIdReader reader) {
        if (reader.idProperty == null || reader.getDeserializer() instanceof ObjectIdPathDeserializer) {
            return reader;
        }
        String memberName = memberNameOf(reader.idProperty);
        SettableBeanProperty idProperty = reader.idProperty instanceof AuthorizingSettableBeanProperty
                ? reader.idProperty
                : new AuthorizingSettableBeanProperty(reader.idProperty, memberName);
        JsonDeserializer<?> idDeserializer = new ObjectIdPathDeserializer(reader.getDeserializer(), memberName);
        return ObjectIdReader.construct(reader.getIdType(), reader.propertyName,
                reader.generator, idDeserializer, idProperty, reader.resolver);
    }

    /**
     * The bean property name {@code StrutsParameterAuthorizer} resolves to the member Jackson will
     * invoke for this property: the field itself, or the property a one-argument {@code set} or
     * no-argument {@code get}/{@code is} accessor is named after. A creator parameter has no such
     * member, and Jackson merges a renamed accessor into whatever property already owns its external
     * name, so neither the external name nor {@code BeanPropertyDefinition#getInternalName()}
     * identifies the member reliably. Properties with no member, or an accessor outside the bean
     * convention, keep the external name.
     */
    static String memberNameOf(SettableBeanProperty property) {
        AnnotatedMember member = property.getMember();
        if (member instanceof AnnotatedField) {
            return member.getName();
        }
        if (member instanceof AnnotatedMethod method) {
            List<String> prefixes = method.getParameterCount() == 1 ? MUTATOR_PREFIXES
                    : method.getParameterCount() == 0 ? GETTER_PREFIXES : List.of();
            String methodName = method.getName();
            for (String prefix : prefixes) {
                if (methodName.length() > prefix.length() && methodName.startsWith(prefix)
                        && Character.isUpperCase(methodName.charAt(prefix.length()))) {
                    return Introspector.decapitalize(methodName.substring(prefix.length()));
                }
            }
        }
        return property.getName();
    }

    /**
     * Configures any-setter enforcement. Set this before the mapper is first used so Jackson has
     * not yet cached deserializers built by this module.
     */
    public void setRequireAnySetterAnnotations(boolean requireAnySetterAnnotations) {
        this.requireAnySetterAnnotations = requireAnySetterAnnotations;
    }

    /**
     * Clears the request-scoped state this module keeps on the thread — the dynamic-key scopes of
     * any-setters and the verdicts awaiting a forward reference — after a mapper read. A handler that
     * registers this module on its own mapper must call it in a {@code finally} around every read;
     * {@code ContentTypeInterceptor} clears the same state once more when it unbinds the context.
     *
     * @since 7.4.0
     */
    public void clearAuthorizationContext() {
        clearRequestState();
    }

    /**
     * @since 7.4.0
     */
    public static void clearRequestState() {
        DynamicKeyAuthorizationContext.clear();
        AuthorizedForwardReferences.clear();
    }
}
