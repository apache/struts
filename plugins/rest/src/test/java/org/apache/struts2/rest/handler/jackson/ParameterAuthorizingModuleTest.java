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

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizer;

public class ParameterAuthorizingModuleTest extends TestCase {

    private ObjectMapper mapper;

    @Override
    protected void setUp() {
        mapper = new ObjectMapper().registerModule(new ParameterAuthorizingModule());
    }

    @Override
    protected void tearDown() {
        ParameterAuthorizationContext.unbind();
    }

    private void bind(ParameterAuthorizer authorizer, Object instance) {
        ParameterAuthorizationContext.bind(authorizer, instance, instance);
    }

    public void testNoContext_passThrough() throws Exception {
        // No bind → wrapper is a no-op
        Person p = mapper.readValue("{\"name\":\"alice\",\"role\":\"admin\"}", Person.class);
        assertEquals("alice", p.name);
        assertEquals("admin", p.role);
    }

    public void testTopLevelAuthorized() throws Exception {
        bind((path, t, a) -> "name".equals(path), new Person());
        Person result = mapper.readValue("{\"name\":\"alice\",\"role\":\"admin\"}", Person.class);
        assertEquals("alice", result.name);
        assertNull(result.role);
    }

    public void testNestedPropertyAuthorizedByPath() throws Exception {
        bind((path, t, a) -> "address".equals(path) || "address.city".equals(path), new Person());
        Person result = mapper.readValue(
                "{\"address\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"}}", Person.class);
        assertNotNull(result.address);
        assertEquals("Warsaw", result.address.city);
        assertNull(result.address.zip);
    }

    public void testNestedRejectedAtParent() throws Exception {
        bind((path, t, a) -> "name".equals(path), new Person());
        Person result = mapper.readValue(
                "{\"name\":\"alice\",\"address\":{\"city\":\"Warsaw\"}}", Person.class);
        assertEquals("alice", result.name);
        assertNull(result.address);
    }

    public void testListUsesIndexedPath() throws Exception {
        bind((path, t, a) -> "addresses".equals(path) || "addresses[0].city".equals(path), new Person());
        Person result = mapper.readValue(
                "{\"addresses\":[{\"city\":\"Warsaw\",\"zip\":\"00-001\"}]}", Person.class);
        assertEquals(1, result.addresses.size());
        assertEquals("Warsaw", result.addresses.get(0).city);
        assertNull(result.addresses.get(0).zip);
    }

    public void testArrayUsesIndexedPath() throws Exception {
        bind((path, t, a) -> "addressArray".equals(path) || "addressArray[0].city".equals(path), new Person());
        Person result = mapper.readValue(
                "{\"addressArray\":[{\"city\":\"Warsaw\",\"zip\":\"00-001\"}]}", Person.class);
        assertEquals(1, result.addressArray.length);
        assertEquals("Warsaw", result.addressArray[0].city);
        assertNull(result.addressArray[0].zip);
    }

    public void testMapUsesIndexedPath() throws Exception {
        bind((path, t, a) -> "addressMap".equals(path) || "addressMap[0].city".equals(path), new Person());
        Person result = mapper.readValue(
                "{\"addressMap\":{\"home\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"}}}", Person.class);
        assertNotNull(result.addressMap.get("home"));
        assertEquals("Warsaw", result.addressMap.get("home").city);
        assertNull(result.addressMap.get("home").zip);
    }

    public void testPathStackCleanAfterDeserialization() throws Exception {
        bind((path, t, a) -> true, new Person());
        mapper.readValue("{\"name\":\"alice\",\"address\":{\"city\":\"Warsaw\"}}", Person.class);
        assertEquals("path stack must be empty after deserialization", "",
                ParameterAuthorizationContext.currentPathPrefix());
    }

    // --- Fixtures ---

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
}
