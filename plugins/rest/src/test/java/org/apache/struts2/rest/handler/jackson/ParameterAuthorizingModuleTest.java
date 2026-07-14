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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import junit.framework.TestCase;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizer;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

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

    public void testBuilderDeserializationNoContextPassThrough() throws Exception {
        // No bind → AuthorizingSettableBeanProperty.deserializeSetAndReturn falls through
        // to the delegate without consulting the authorization context.
        ImmutablePerson p = mapper.readValue("{\"name\":\"alice\",\"role\":\"admin\"}", ImmutablePerson.class);
        assertEquals("alice", p.name);
        assertEquals("admin", p.role);
    }

    public void testBuilderDeserializationAuthorizedTopLevel() throws Exception {
        bind((path, t, a) -> "name".equals(path), new ImmutablePerson.Builder());
        ImmutablePerson p = mapper.readValue("{\"name\":\"alice\",\"role\":\"admin\"}", ImmutablePerson.class);
        assertEquals("alice", p.name);
        assertNull("unauthorized property must be skipped on builder path", p.role);
    }

    public void testBuilderDeserializationRejectsAllProperties() throws Exception {
        bind((path, t, a) -> false, new ImmutablePerson.Builder());
        ImmutablePerson p = mapper.readValue("{\"name\":\"alice\",\"role\":\"admin\"}", ImmutablePerson.class);
        assertNull(p.name);
        assertNull(p.role);
    }

    public void testRecordComponentAuthorizedByPath() throws Exception {
        bind((path, t, a) -> "recordAddress".equals(path) || "recordAddress.city".equals(path), new Person());
        Person result = mapper.readValue(
                "{\"recordAddress\":{\"city\":\"Warsaw\",\"secret\":\"admin-only\"}}", Person.class);
        assertNotNull(result.recordAddress);
        assertEquals("Warsaw", result.recordAddress.city());
        assertNull(result.recordAddress.secret());
    }

    public void testTopLevelRecordAuthorizedByPath() throws Exception {
        // The REST body target itself is a record, not merely a nested field -- exercises the same
        // creator-bound wrapping at the outermost readValue() call.
        bind((path, t, a) -> "name".equals(path), new TopLevelRecord("", ""));
        TopLevelRecord result = mapper.readValue("{\"name\":\"alice\",\"secret\":\"top\"}", TopLevelRecord.class);
        assertEquals("alice", result.name());
        assertNull(result.secret());
    }

    public void testStaticFactoryCreatorAuthorizedByPath() throws Exception {
        // @JsonCreator on a static factory method, rather than a constructor -- a distinct
        // ValueInstantiator flavor from the constructor/record cases above.
        bind((path, t, a) -> "name".equals(path), FactoryCreated.of("", ""));
        FactoryCreated result = mapper.readValue("{\"name\":\"alice\",\"secret\":\"top\"}", FactoryCreated.class);
        assertEquals("alice", result.name);
        assertNull(result.secret);
    }

    public void testConstructorPropertiesAuthorizedByPath() throws Exception {
        // @ConstructorProperties (java.beans), rather than @JsonCreator -- the other JDK-native
        // "creator style" Jackson recognizes for properties-based construction.
        bind((path, t, a) -> "name".equals(path), new ConstructorPropsBean("", ""));
        ConstructorPropsBean result = mapper.readValue("{\"name\":\"alice\",\"secret\":\"top\"}", ConstructorPropsBean.class);
        assertEquals("alice", result.name);
        assertNull(result.secret);
    }

    public void testCreatorPropertyEntirelyRejected_dropsWholeSubtree() throws Exception {
        // "inner" itself is never authorized -- the whole nested creator-bound object must be
        // dropped, matching how a rejected non-creator nested bean property behaves (see
        // testNestedRejectedAtParent), not partially constructed with defaults.
        bind((path, t, a) -> "top".equals(path), new Wrapper("", null));
        Wrapper result = mapper.readValue(
                "{\"top\":\"T\",\"inner\":{\"mid\":\"M\",\"innerinner\":{\"bottom\":\"B\",\"secret\":\"S\"}}}",
                Wrapper.class);
        assertEquals("T", result.top());
        assertNull(result.inner());
    }

    public void testThreeLevelNestedCreatorPathAuthorization() throws Exception {
        bind((path, t, a) -> "top".equals(path) || "inner".equals(path) || "inner.mid".equals(path)
                || "inner.innerinner".equals(path) || "inner.innerinner.bottom".equals(path),
                new Wrapper("", null));
        Wrapper result = mapper.readValue(
                "{\"top\":\"T\",\"inner\":{\"mid\":\"M\",\"innerinner\":{\"bottom\":\"B\",\"secret\":\"S\"}}}",
                Wrapper.class);
        assertEquals("T", result.top());
        assertEquals("M", result.inner().mid());
        assertEquals("B", result.inner().innerinner().bottom());
        assertNull(result.inner().innerinner().secret());
    }

    public void testListOfRecordsAsCreatorParam_elementsAuthorizedByIndexedPath() throws Exception {
        bind((path, t, a) -> "items".equals(path) || "items[0].value".equals(path), new WithList("", null));
        WithList result = mapper.readValue(
                "{\"label\":\"x\",\"items\":[{\"value\":\"v1\",\"secret\":\"s1\"}]}", WithList.class);
        assertNull("unauthorized top-level creator property must be dropped", result.label());
        assertEquals(1, result.items().size());
        assertEquals("v1", result.items().get(0).value());
        assertNull(result.items().get(0).secret());
    }

    public void testMapOfRecordsAsCreatorParam_elementsAuthorizedByIndexedPath() throws Exception {
        bind((path, t, a) -> "items".equals(path) || "items[0].value".equals(path), new WithMap("", null));
        WithMap result = mapper.readValue(
                "{\"label\":\"x\",\"items\":{\"a\":{\"value\":\"v1\",\"secret\":\"s1\"}}}", WithMap.class);
        assertNull(result.label());
        assertEquals("v1", result.items().get("a").value());
        assertNull(result.items().get("a").secret());
    }

    public void testArrayOfRecordsAsCreatorParam_elementsAuthorizedByIndexedPath() throws Exception {
        // Array-valued creator param -- exercises the type.isArray() branch of prefixForNested,
        // rounding out the collection matrix alongside the List/Map cases above.
        bind((path, t, a) -> "items".equals(path) || "items[0].value".equals(path), new WithArray("", null));
        WithArray result = mapper.readValue(
                "{\"label\":\"x\",\"items\":[{\"value\":\"v1\",\"secret\":\"s1\"}]}", WithArray.class);
        assertNull(result.label());
        assertEquals(1, result.items().length);
        assertEquals("v1", result.items()[0].value());
        assertNull(result.items()[0].secret());
    }

    public void testListOfPlainPojosAsCreatorParam_elementsAuthorizedByIndexedPath() throws Exception {
        // The creator param itself (List<PlainAddr>) is record-bound, but its elements are an
        // ordinary field-based POJO -- exercises the two wrapping mechanisms handing off to each
        // other across a collection boundary.
        bind((path, t, a) -> "addrs".equals(path) || "addrs[0].city".equals(path), new ListOfPlain("", null));
        ListOfPlain result = mapper.readValue(
                "{\"label\":\"x\",\"addrs\":[{\"city\":\"Warsaw\",\"zip\":\"00-001\"}]}", ListOfPlain.class);
        assertNull(result.label());
        assertEquals("Warsaw", result.addrs().get(0).city);
        assertNull(result.addrs().get(0).zip);
    }

    public void testValidatingRecordRejectedRequiredComponent_dropsObjectInsteadOfThrowing() throws Exception {
        // Regression for the gap flagged in review: a record whose compact constructor requires a
        // non-null component throws ValueInstantiationException when that component is redacted by
        // authorization. Without RedactionAwareDeserializer, that exception used to propagate raw
        // and fail the whole request body; it must instead be treated as "this object is
        // unauthorized" (null), the same fail-closed outcome as testCreatorPropertyEntirelyRejected.
        bind((path, t, a) -> "name".equals(path), new Validated("", "x"));
        Validated result = mapper.readValue("{\"name\":\"alice\",\"secret\":\"top\"}", Validated.class);
        assertNull("construction failure caused by our own redaction must drop the object, not throw",
                result);
    }

    public void testPrimitiveCreatorParamRejected_underFailOnNullForPrimitives_dropsObjectInsteadOfThrowing()
            throws Exception {
        // Regression for the other gap flagged in review: with FAIL_ON_NULL_FOR_PRIMITIVES enabled,
        // rejecting a primitive-typed creator component makes Jackson itself throw
        // MismatchedInputException when building the object. Same fail-closed contract as above.
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        bind((path, t, a) -> "currency".equals(path), new Money(0, ""));
        Money result = mapper.readValue("{\"amount\":100,\"currency\":\"USD\"}", Money.class);
        assertNull(result);
    }

    public void testValidatingRecord_genuineClientErrorStillPropagates() throws Exception {
        // Guards against over-broad swallowing: when nothing in this object was redacted, a
        // construction failure is a genuine client/data error and must still be reported, not
        // silently converted into null.
        bind((path, t, a) -> true, new Validated("", "x"));
        try {
            mapper.readValue("{\"name\":\"alice\",\"secret\":null}", Validated.class);
            fail("expected the compact constructor's own validation failure to propagate");
        } catch (Exception expected) {
            // ValueInstantiationException (or its cause chain) -- exact type not asserted to avoid
            // coupling this test to Jackson's internal exception hierarchy.
        }
    }

    // --- Fixtures ---

    public static class Person {
        public String name;
        public String role;
        public Address address;
        public java.util.List<Address> addresses;
        public Address[] addressArray;
        public java.util.Map<String, Address> addressMap;
        public RecordAddress recordAddress;
    }

    public static class Address {
        public String city;
        public String zip;
    }

    /**
     * Record fixture: forces Jackson to bind {@code city}/{@code secret} via its creator/constructor
     * path, the code path {@link AuthorizingSettableBeanProperty#withValueDeserializer} covers.
     */
    public record RecordAddress(String city, String secret) {
    }

    /**
     * Builder-pattern fixture: forces Jackson to use {@code BuilderBasedDeserializer},
     * which dispatches property deserialization through {@code SettableBeanProperty.deserializeSetAndReturn}
     * — the alternate code path on {@code AuthorizingSettableBeanProperty} not exercised by
     * setter-based fixtures like {@link Person}.
     */
    @JsonDeserialize(builder = ImmutablePerson.Builder.class)
    public static final class ImmutablePerson {
        public final String name;
        public final String role;

        private ImmutablePerson(Builder b) {
            this.name = b.name;
            this.role = b.role;
        }

        @JsonPOJOBuilder(withPrefix = "set")
        public static class Builder {
            private String name;
            private String role;

            public Builder setName(String n) { this.name = n; return this; }
            public Builder setRole(String r) { this.role = r; return this; }
            public ImmutablePerson build() { return new ImmutablePerson(this); }
        }
    }

    /** Top-level creator-bound target: no enclosing POJO field, exercises the outermost readValue(). */
    public record TopLevelRecord(String name, String secret) {
    }

    /** @JsonCreator on a static factory method rather than a constructor. */
    public static class FactoryCreated {
        public final String name;
        public final String secret;

        private FactoryCreated(String name, String secret) {
            this.name = name;
            this.secret = secret;
        }

        @JsonCreator
        public static FactoryCreated of(@JsonProperty("name") String name, @JsonProperty("secret") String secret) {
            return new FactoryCreated(name, secret);
        }
    }

    /** @ConstructorProperties (java.beans) rather than @JsonCreator. */
    public static class ConstructorPropsBean {
        public final String name;
        public final String secret;

        @ConstructorProperties({"name", "secret"})
        public ConstructorPropsBean(String name, String secret) {
            this.name = name;
            this.secret = secret;
        }
    }

    /** Three-level nesting of creator-bound records, to exercise cumulative path-stack depth. */
    public record Wrapper(String top, Inner inner) {
    }

    public record Inner(String mid, InnerInner innerinner) {
    }

    public record InnerInner(String bottom, String secret) {
    }

    /** A creator-bound record whose component is a List of further creator-bound records. */
    public record WithList(String label, List<Item> items) {
    }

    public record Item(String value, String secret) {
    }

    /** A creator-bound record whose component is a Map of further creator-bound records. */
    public record WithMap(String label, Map<String, Item> items) {
    }

    /** A creator-bound record whose component is an array of further creator-bound records. */
    public record WithArray(String label, Item[] items) {
    }

    /** A creator-bound record whose component is a List of an ordinary field-based POJO. */
    public record ListOfPlain(String label, List<PlainAddr> addrs) {
    }

    public static class PlainAddr {
        public String city;
        public String zip;
    }

    /**
     * A record whose compact constructor enforces a non-null invariant. When {@code secret} is
     * redacted by authorization, Jackson substitutes {@code null} for it, and this constructor
     * throws -- exercising {@link RedactionAwareDeserializer}'s fail-closed handling of that
     * construction failure.
     */
    public record Validated(String name, String secret) {
        public Validated {
            if (secret == null) {
                throw new IllegalArgumentException("secret must not be null");
            }
        }
    }

    /** A record with a primitive component, to exercise FAIL_ON_NULL_FOR_PRIMITIVES interaction. */
    public record Money(int amount, String currency) {
    }
}
