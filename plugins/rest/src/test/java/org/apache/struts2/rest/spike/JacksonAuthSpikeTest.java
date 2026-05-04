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
package org.apache.struts2.rest.spike;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;
import junit.framework.TestCase;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.BiPredicate;

/**
 * SPIKE — NOT PRODUCTION CODE.
 *
 * Validates that Jackson's BeanDeserializerModifier + SettableBeanProperty wrapping can
 * intercept per-property deserialization for Approach C (handler-level @StrutsParameter
 * authorization). Path tracking uses a ThreadLocal Deque pushed/popped around each
 * authorized property's deserialization.
 *
 * Three claims under test:
 *   (1) updateBuilder() can replace SettableBeanProperty instances on the builder
 *   (2) A wrapping property can call parser.skipChildren() to discard unauthorized values
 *   (3) Path tracking via ThreadLocal Deque produces dot/bracket paths matching
 *       ParametersInterceptor depth semantics for nested objects
 *
 * If green, this approach is viable for production.
 */
public class JacksonAuthSpikeTest extends TestCase {

    // --- ThreadLocal path tracking ---

    private static final ThreadLocal<Deque<String>> PATH_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private static String currentPath(String propertyName) {
        Deque<String> stack = PATH_STACK.get();
        if (stack.isEmpty()) {
            return propertyName;
        }
        // Build path: stack contains parent prefix(es) bottom-to-top
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = stack.descendingIterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append('.');
        }
        sb.append(propertyName);
        return sb.toString();
    }

    // --- Wrapping SettableBeanProperty ---

    static class AuthorizingSettableBeanProperty extends SettableBeanProperty.Delegating {
        private final BiPredicate<String, Object> authorizer;

        AuthorizingSettableBeanProperty(SettableBeanProperty delegate, BiPredicate<String, Object> authorizer) {
            super(delegate);
            this.authorizer = authorizer;
        }

        @Override
        protected SettableBeanProperty withDelegate(SettableBeanProperty d) {
            return new AuthorizingSettableBeanProperty(d, authorizer);
        }

        /**
         * For Collection/Map/Array properties, the path to push for nested element members must include
         * the indexed bracket suffix so children build paths like "items[0].field" — matching
         * ParametersInterceptor depth semantics and the existing JSONInterceptor recursive filter.
         * For scalar/bean properties, push the path unchanged.
         */
        private String prefixForNested(String pathOfThisProperty) {
            com.fasterxml.jackson.databind.JavaType type = getType();
            if (type != null && (type.isCollectionLikeType() || type.isMapLikeType() || type.isArrayType())) {
                return pathOfThisProperty + "[0]";
            }
            return pathOfThisProperty;
        }

        @Override
        public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws java.io.IOException {
            String path = currentPath(getName());
            if (!authorizer.test(path, instance)) {
                p.skipChildren(); // discard the JSON value for this property
                return;
            }
            PATH_STACK.get().push(prefixForNested(path));
            try {
                delegate.deserializeAndSet(p, ctxt, instance);
            } finally {
                PATH_STACK.get().pop();
            }
        }

        @Override
        public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws java.io.IOException {
            String path = currentPath(getName());
            if (!authorizer.test(path, instance)) {
                p.skipChildren();
                return instance;
            }
            PATH_STACK.get().push(prefixForNested(path));
            try {
                return delegate.deserializeSetAndReturn(p, ctxt, instance);
            } finally {
                PATH_STACK.get().pop();
            }
        }
    }

    // --- Module that registers the modifier ---

    static ObjectMapper buildAuthorizingMapper(BiPredicate<String, Object> authorizer) {
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                                                         BeanDescription beanDesc,
                                                         BeanDeserializerBuilder builder) {
                Iterator<SettableBeanProperty> it = builder.getProperties();
                while (it.hasNext()) {
                    SettableBeanProperty original = it.next();
                    builder.addOrReplaceProperty(new AuthorizingSettableBeanProperty(original, authorizer), true);
                }
                return builder;
            }
        });
        return new ObjectMapper().registerModule(module);
    }

    // --- Test fixtures ---

    public static class Person {
        public String name;
        public String role;
        public Address address;
        public java.util.List<Address> addresses;
        public Address[] addressArray;
        public java.util.Map<String, Address> addressMap;
    }

    public static class Address {
        public String city;
        public String zip;
    }

    // --- Tests ---

    @Override
    protected void setUp() {
        PATH_STACK.remove();
    }

    public void testTopLevelAuthorizedPropertyIsApplied() throws Exception {
        ObjectMapper mapper = buildAuthorizingMapper((path, instance) -> "name".equals(path));
        Person p = mapper.readValue("{\"name\":\"alice\",\"role\":\"admin\"}", Person.class);
        assertEquals("alice", p.name);
        assertNull("role must be skipped", p.role);
    }

    public void testNestedPropertyAuthorizedByFullPath() throws Exception {
        // Authorize address (depth 0) and address.city (depth 1), but NOT address.zip
        ObjectMapper mapper = buildAuthorizingMapper((path, instance) ->
                "address".equals(path) || "address.city".equals(path));
        Person p = mapper.readValue("{\"address\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"}}", Person.class);
        assertNotNull("address should be set", p.address);
        assertEquals("Warsaw", p.address.city);
        assertNull("zip must be skipped because address.zip not authorized", p.address.zip);
    }

    public void testNestedRejectedAtParent() throws Exception {
        // Reject "address" entirely at depth 0; nested fields should not be visited
        ObjectMapper mapper = buildAuthorizingMapper((path, instance) -> "name".equals(path));
        Person p = mapper.readValue("{\"name\":\"alice\",\"address\":{\"city\":\"Warsaw\"}}", Person.class);
        assertEquals("alice", p.name);
        assertNull("address must be rejected at the parent, no nested visit", p.address);
    }

    // --- Collection / Array / Map indexed-path tests ---

    public void testListOfBeansUsesIndexedPath() throws Exception {
        // Authorize "addresses" (depth 0) AND "addresses[0].city" (depth 2) but NOT "addresses[0].zip"
        ObjectMapper mapper = buildAuthorizingMapper((path, instance) ->
                "addresses".equals(path) || "addresses[0].city".equals(path));
        Person p = mapper.readValue(
                "{\"addresses\":[{\"city\":\"Warsaw\",\"zip\":\"00-001\"},{\"city\":\"Krakow\",\"zip\":\"30-001\"}]}",
                Person.class);
        assertNotNull(p.addresses);
        assertEquals(2, p.addresses.size());
        assertEquals("Warsaw", p.addresses.get(0).city);
        assertNull("addresses[0].zip must be skipped on element 0", p.addresses.get(0).zip);
        assertEquals("Krakow", p.addresses.get(1).city);
        assertNull("addresses[0].zip must be skipped on element 1 too (same path token)", p.addresses.get(1).zip);
    }

    public void testListRejectedAtParentSkipsAllElements() throws Exception {
        ObjectMapper mapper = buildAuthorizingMapper((path, instance) -> "name".equals(path));
        Person p = mapper.readValue(
                "{\"name\":\"alice\",\"addresses\":[{\"city\":\"Warsaw\"}]}", Person.class);
        assertEquals("alice", p.name);
        assertNull("addresses must be rejected at parent — Jackson never visits elements", p.addresses);
    }

    public void testArrayOfBeansUsesIndexedPath() throws Exception {
        ObjectMapper mapper = buildAuthorizingMapper((path, instance) ->
                "addressArray".equals(path) || "addressArray[0].city".equals(path));
        Person p = mapper.readValue(
                "{\"addressArray\":[{\"city\":\"Warsaw\",\"zip\":\"00-001\"}]}", Person.class);
        assertNotNull(p.addressArray);
        assertEquals(1, p.addressArray.length);
        assertEquals("Warsaw", p.addressArray[0].city);
        assertNull("addressArray[0].zip must be skipped", p.addressArray[0].zip);
    }

    public void testMapOfBeansUsesIndexedPath() throws Exception {
        // Map values use [0] suffix (matching ParametersInterceptor bracket semantics + JSONInterceptor)
        ObjectMapper mapper = buildAuthorizingMapper((path, instance) ->
                "addressMap".equals(path) || "addressMap[0].city".equals(path));
        Person p = mapper.readValue(
                "{\"addressMap\":{\"home\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"}}}", Person.class);
        assertNotNull(p.addressMap);
        assertEquals(1, p.addressMap.size());
        assertNotNull(p.addressMap.get("home"));
        assertEquals("Warsaw", p.addressMap.get("home").city);
        assertNull("addressMap[0].zip must be skipped", p.addressMap.get("home").zip);
    }

    public void testPathStackIsCleanAfterDeserialization() throws Exception {
        ObjectMapper mapper = buildAuthorizingMapper((path, instance) -> true);
        mapper.readValue("{\"name\":\"alice\",\"address\":{\"city\":\"Warsaw\"}}", Person.class);
        assertTrue("ThreadLocal stack must be empty after deserialization", PATH_STACK.get().isEmpty());
    }
}
