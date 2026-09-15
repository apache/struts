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

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizationContext;
import org.apache.struts2.interceptor.parameter.ParameterAuthorizer;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.apache.struts2.rest.handler.JacksonJsonHandler;

import java.beans.ConstructorProperties;
import java.io.StringReader;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ParameterAuthorizingModuleTest extends TestCase {

    private ObjectMapper mapper;
    private Logger rejectionLogger;
    private Level rejectionLoggerLevel;
    private RecordingAppender rejectionLog;

    @Override
    protected void setUp() {
        mapper = new ObjectMapper().registerModule(new ParameterAuthorizingModule());
        rejectionLogger = (Logger) LogManager.getLogger(DynamicKeyRejections.class);
        rejectionLoggerLevel = rejectionLogger.getLevel();
        Configurator.setLevel(rejectionLogger.getName(), Level.DEBUG);
        rejectionLog = new RecordingAppender();
        rejectionLog.start();
        rejectionLogger.addAppender(rejectionLog);
    }

    @Override
    protected void tearDown() {
        rejectionLogger.removeAppender(rejectionLog);
        rejectionLog.stop();
        Configurator.setLevel(rejectionLogger.getName(), rejectionLoggerLevel);
        ParameterAuthorizationContext.unbind();
        ParameterAuthorizingModule.clearRequestState();
    }

    private List<String> rejectionMessages(Level level) {
        return rejectionLog.events.stream()
                .filter(event -> event.getLevel() == level)
                .map(event -> event.getMessage().getFormattedMessage())
                .collect(Collectors.toList());
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

    public void testNamingStrategyAuthorizesJavaMemberNamesAtEveryLevel() throws Exception {
        ObjectMapper snakeCase = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(new ParameterAuthorizingModule());
        List<String> seen = new ArrayList<>();
        bind((path, t, a) -> seen.add(path), new CamelCasePerson());
        CamelCasePerson result = snakeCase.readValue(
                "{\"user_name\":\"alice\",\"home_address\":{\"street_name\":\"Main\"},"
                        + "\"other_addresses\":[{\"street_name\":\"Side\"}]}",
                CamelCasePerson.class);
        assertEquals("alice", result.userName);
        assertEquals("Main", result.homeAddress.streetName);
        assertEquals("Side", result.otherAddresses.get(0).streetName);
        assertEquals(List.of("userName", "homeAddress", "homeAddress.streetName",
                "otherAddresses", "otherAddresses[0].streetName"), List.copyOf(new LinkedHashSet<>(seen)));
    }

    public void testMemberNameKeepsExternalNameForMutatorOutsideBeanConvention() throws Exception {
        List<String> seen = new ArrayList<>();
        bind((path, t, a) -> seen.add(path), new FluentMutatorBean());
        mapper.readValue("{\"settings\":\"dark\",\"issue\":\"open\"}", FluentMutatorBean.class);
        assertEquals(List.of("settings", "issue"), List.copyOf(new LinkedHashSet<>(seen)));
    }

    public void testMemberNameKeepsExternalNameForOneArgGetterNamedMutator() throws Exception {
        List<String> seen = new ArrayList<>();
        bind((path, t, a) -> seen.add(path), new GetterNamedMutatorBean());
        mapper.readValue("{\"nick\":\"x\"}", GetterNamedMutatorBean.class);
        assertEquals(List.of("nick"), List.copyOf(new LinkedHashSet<>(seen)));
    }

    public void testAnySetterEnforcementDisabledByDefault() throws Exception {
        bind((path, t, a) -> false, new UnannotatedAnySetterBean());
        UnannotatedAnySetterBean result = mapper.readValue(
                "{\"role\":\"admin\"}", UnannotatedAnySetterBean.class);
        assertEquals("admin", result.values.get("role"));
    }

    public void testUnannotatedAnySetterRejectedWhenEnforcementEnabled() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> true, new UnannotatedAnySetterBean());
        UnannotatedAnySetterBean result = enforcingMapper.readValue(
                "{\"role\":\"admin\"}", UnannotatedAnySetterBean.class);
        assertTrue(result.values.isEmpty());
    }

    public void testAnySetterRejectionsAreLoggedOncePerRequest() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> true, new UnannotatedAnySetterBean());
        enforcingMapper.readValue(
                "{\"role\":\"admin\",\"level\":9,\"token\":\"x\"}", UnannotatedAnySetterBean.class);

        assertEquals(List.of(), rejectionMessages(Level.WARN));
        List<String> detail = rejectionMessages(Level.DEBUG);
        assertEquals(3, detail.size());
        assertTrue(detail.get(0), detail.get(0).contains("[role]"));
        assertTrue(detail.get(1), detail.get(1).contains("[level]"));
        assertTrue(detail.get(2), detail.get(2).contains("[token]"));

        ParameterAuthorizingModule.clearRequestState();

        List<String> summary = rejectionMessages(Level.WARN);
        assertEquals(1, summary.size());
        String sink = UnannotatedAnySetterBean.class.getName() + "#put";
        assertTrue(summary.get(0), summary.get(0).contains("[" + sink + "]"));
        assertTrue(summary.get(0), summary.get(0).contains("[3] dynamic key(s), logged at DEBUG;"));
        assertTrue(summary.get(0), summary.get(0).contains("@StrutsParameter(allowDynamicKeys = true)"));

        ParameterAuthorizingModule.clearRequestState();
        assertEquals(1, rejectionMessages(Level.WARN).size());
    }

    public void testDepthRejectionsAreSummarizedByReason() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicDepthZeroAnySetterBean());
        enforcingMapper.readValue(
                "{\"home\":{\"city\":\"Warsaw\"},\"work\":{\"geo\":{\"country\":\"PL\"}}}",
                DynamicDepthZeroAnySetterBean.class);

        List<String> detail = rejectionMessages(Level.DEBUG);
        assertEquals(2, detail.size());
        assertTrue(detail.get(0), detail.get(0).contains("value depth [1] exceeds @StrutsParameter depth [0]"));
        assertTrue(detail.get(1), detail.get(1).contains("value depth [2] exceeds @StrutsParameter depth [0]"));

        ParameterAuthorizingModule.clearRequestState();

        List<String> summary = rejectionMessages(Level.WARN);
        assertEquals(1, summary.size());
        assertTrue(summary.get(0), summary.get(0).contains("[2] dynamic key(s), logged at DEBUG; value depth exceeds"));
    }

    public void testRejectionsAreSummarizedPerSink() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> "nested".equals(path), new NestedAnySettersBean());
        enforcingMapper.readValue(
                "{\"role\":\"admin\",\"nested\":{\"role\":\"admin\",\"level\":9}}",
                NestedAnySettersBean.class);

        ParameterAuthorizingModule.clearRequestState();

        List<String> summary = rejectionMessages(Level.WARN);
        assertEquals(2, summary.size());
        assertTrue(summary.get(0), summary.get(0).contains(
                "[" + NestedAnySettersBean.class.getName() + "#put] rejected [1]"));
        assertTrue(summary.get(1), summary.get(1).contains(
                "[" + UnannotatedAnySetterBean.class.getName() + "#put] rejected [2]"));
    }

    public void testAnySetterWithoutDynamicKeyOptInRejected() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> true, new AnnotatedAnySetterBean());
        AnnotatedAnySetterBean result = enforcingMapper.readValue(
                "{\"role\":\"admin\"}", AnnotatedAnySetterBean.class);
        assertTrue(result.values.isEmpty());
    }

    public void testMethodAnySetterWithDynamicKeyOptInAcceptsScalar() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicScalarAnySetterBean());
        DynamicScalarAnySetterBean result = enforcingMapper.readValue(
                "{\"role\":\"admin\"}", DynamicScalarAnySetterBean.class);
        assertEquals("admin", result.values.get("role"));
    }

    public void testFieldAnySetterWithDynamicKeyOptInAcceptsScalar() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicFieldAnySetterBean());
        DynamicFieldAnySetterBean result = enforcingMapper.readValue(
                "{\"role\":\"admin\"}", DynamicFieldAnySetterBean.class);
        assertEquals("admin", result.values.get("role"));
    }

    public void testMethodAnySetterPreservesFloatingPointValues() throws Exception {
        String number = "1.2345678901234567890123456789";
        for (boolean useBigDecimal : new boolean[]{false, true}) {
            ObjectMapper enforcingMapper = enforcingMapper()
                    .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, useBigDecimal);
            bind((path, t, a) -> false, new DynamicScalarAnySetterBean());
            DynamicScalarAnySetterBean result = enforcingMapper.readValue(
                    "{\"amount\":" + number + "}", DynamicScalarAnySetterBean.class);
            assertEquals(useBigDecimal ? new BigDecimal(number) : Double.valueOf(number),
                    result.values.get("amount"));
        }
    }

    public void testFieldAnySetterPreservesFloatingPointValues() throws Exception {
        String number = "1.2345678901234567890123456789";
        for (boolean useBigDecimal : new boolean[]{false, true}) {
            ObjectMapper enforcingMapper = enforcingMapper()
                    .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, useBigDecimal);
            bind((path, t, a) -> false, new DynamicFieldAnySetterBean());
            DynamicFieldAnySetterBean result = enforcingMapper.readValue(
                    "{\"amount\":" + number + "}", DynamicFieldAnySetterBean.class);
            assertEquals(useBigDecimal ? new BigDecimal(number) : Double.valueOf(number),
                    result.values.get("amount"));
        }
    }

    public void testPropertyCreatorAnySetterPreservesFloatingPointValues() throws Exception {
        String number = "1.2345678901234567890123456789";
        for (boolean useBigDecimal : new boolean[]{false, true}) {
            ObjectMapper enforcingMapper = enforcingMapper()
                    .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, useBigDecimal);
            bind((path, t, a) -> true, new PropertyCreatorWithAnySetterBean(""));
            // Read the dynamic value before the constructor argument so Jackson buffers it.
            PropertyCreatorWithAnySetterBean result = enforcingMapper.readValue(
                    "{\"amount\":" + number + ",\"name\":\"alice\"}", PropertyCreatorWithAnySetterBean.class);
            assertEquals("alice", result.name);
            assertEquals(useBigDecimal ? new BigDecimal(number) : Double.valueOf(number),
                    result.values.get("amount"));
        }
    }

    public void testXmlAnySetterPreservesNumericTextRoundTrip() throws Exception {
        String number = "1.2345678901234567890123456789";
        for (boolean useBigDecimal : new boolean[]{false, true}) {
            XmlMapper xmlMapper = enforcingXmlMapper();
            xmlMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, useBigDecimal);
            bind((path, t, a) -> false, new DynamicScalarAnySetterBean());
            DynamicScalarAnySetterBean result = xmlMapper.readValue(
                    "<value><amount>" + number + "</amount></value>", DynamicScalarAnySetterBean.class);
            assertEquals(number, result.values.get("amount"));

            DynamicScalarAnySetterBean roundTrip = xmlMapper.readValue(
                    xmlMapper.writeValueAsString(result.values), DynamicScalarAnySetterBean.class);
            assertEquals(number, roundTrip.values.get("amount"));
        }
    }

    public void testFieldAnySetterDepthOneAcceptsDirectMember() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicDepthOneFieldAnySetterBean());
        DynamicDepthOneFieldAnySetterBean result = enforcingMapper.readValue(
                "{\"home\":{\"city\":\"Warsaw\"}}",
                DynamicDepthOneFieldAnySetterBean.class);
        assertEquals("Warsaw", result.values.get("home").city);
    }

    public void testDynamicKeyDepthZeroRejectsNestedMembers() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicDepthZeroAnySetterBean());
        DynamicDepthZeroAnySetterBean result = enforcingMapper.readValue(
                "{\"home\":{\"city\":\"Warsaw\"}}", DynamicDepthZeroAnySetterBean.class);
        assertTrue(result.values.isEmpty());
    }

    public void testDynamicKeyDepthOneAcceptsDirectMember() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicDepthOneAnySetterBean());
        DynamicDepthOneAnySetterBean result = enforcingMapper.readValue(
                "{\"home\":{\"city\":\"Warsaw\"}}",
                DynamicDepthOneAnySetterBean.class);
        assertEquals("Warsaw", result.values.get("home").city);
    }

    public void testDynamicKeyDepthOneRejectsGrandchildBeforeConstruction() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicDepthOneAnySetterBean());
        DynamicDepthOneAnySetterBean result = enforcingMapper.readValue(
                "{\"home\":{\"city\":\"Warsaw\",\"geo\":{\"country\":\"PL\"}}}",
                DynamicDepthOneAnySetterBean.class);
        assertTrue(result.values.isEmpty());
    }

    public void testDynamicKeyDepthZeroRejectsUntypedObject() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicScalarAnySetterBean());
        DynamicScalarAnySetterBean result = enforcingMapper.readValue(
                "{\"settings\":{\"role\":\"admin\"}}", DynamicScalarAnySetterBean.class);
        assertTrue(result.values.isEmpty());
    }

    public void testDynamicKeyDepthTwoAcceptsGrandchild() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicDepthTwoAnySetterBean());
        DynamicDepthTwoAnySetterBean result = enforcingMapper.readValue(
                "{\"home\":{\"geo\":{\"country\":\"PL\"}}}", DynamicDepthTwoAnySetterBean.class);
        assertEquals("PL", result.values.get("home").geo.country);
    }

    public void testCreatorParameterAnySetterRejectedWhenEnforcementEnabled() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> true, new CreatorAnySetterBean(Map.of()));
        CreatorAnySetterBean result = enforcingMapper.readValue(
                "{\"role\":\"admin\"}", CreatorAnySetterBean.class);
        assertTrue(result.values.isEmpty());

        ParameterAuthorizingModule.clearRequestState();
        List<String> summary = rejectionMessages(Level.WARN);
        assertEquals(1, summary.size());
        assertTrue(summary.get(0), summary.get(0).contains(
                "[" + CreatorAnySetterBean.class.getName() + "#creator[0]] rejected [1]"));
        assertTrue(summary.get(0), summary.get(0).contains("can only be declared on an any-setter method or field"));
    }

    public void testDeserializeWithoutCurrentNameRejectsAndClearsScope() throws Exception {
        AtomicReference<SettableAnyProperty> captured = new AtomicReference<>();
        SimpleModule captureModule = new SimpleModule("capture-any-setter");
        captureModule.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                                                          BeanDescription beanDesc,
                                                          BeanDeserializerBuilder builder) {
                if (beanDesc.getBeanClass() == PropertyCreatorWithAnySetterBean.class) {
                    captured.set(builder.getAnySetter());
                }
                return builder;
            }
        });
        ObjectMapper captureMapper = new ObjectMapper().registerModule(captureModule);
        captureMapper.readValue("{\"name\":\"alice\"}", PropertyCreatorWithAnySetterBean.class);

        assertNotNull(captured.get());
        AuthorizingSettableAnyProperty property = new AuthorizingSettableAnyProperty(captured.get());
        bind((path, t, a) -> true, new PropertyCreatorWithAnySetterBean(""));
        try (TokenBuffer value = new TokenBuffer(captureMapper, false)) {
            value.writeString("admin");
            try (JsonParser parser = value.asParserOnFirstToken()) {
                assertNull(parser.currentName());
                assertNotNull(property.deserialize(parser, null));
            }
        }

        assertFalse(DynamicKeyAuthorizationContext.isActive());
        assertEquals("", ParameterAuthorizationContext.currentPathPrefix());

        ParameterAuthorizingModule.clearRequestState();
        List<String> summary = rejectionMessages(Level.WARN);
        assertEquals(1, summary.size());
        assertTrue(summary.get(0), summary.get(0).contains(
                "[" + PropertyCreatorWithAnySetterBean.class.getName() + "#put] rejected [1]"));
        assertTrue(summary.get(0), summary.get(0).contains("dynamic property name is unavailable"));
    }

    public void testJacksonHandlerClearsDynamicScopeAfterReadFailure() throws Exception {
        DynamicKeyAuthorizationContext.push("stale", 0);
        assertTrue(DynamicKeyAuthorizationContext.isActive());

        try {
            new JacksonJsonHandler().toObject(null, new StringReader("{"), new Person());
            fail("expected malformed JSON to fail");
        } catch (Exception expected) {
            // The handler's request-boundary cleanup must run even when Jackson aborts the read.
        }

        assertFalse(DynamicKeyAuthorizationContext.isActive());
    }

    public void testUnauthorizedParentStillBlocksNestedAnySetter() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new AnySetterParent());
        AnySetterParent result = enforcingMapper.readValue(
                "{\"child\":{\"role\":\"admin\"}}", AnySetterParent.class);
        assertNull(result.child);
    }

    public void testJsonUnwrappedRemainsUnaffected() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> true, new UnwrappedBean());
        UnwrappedBean result = enforcingMapper.readValue("{\"city\":\"Warsaw\"}", UnwrappedBean.class);
        assertEquals("Warsaw", result.address.city);
    }

    public void testDynamicKeyScopeCleanAfterDeserialization() throws Exception {
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicDepthTwoAnySetterBean());
        enforcingMapper.readValue(
                "{\"home\":{\"geo\":{\"country\":\"PL\"}}}", DynamicDepthTwoAnySetterBean.class);
        assertFalse(DynamicKeyAuthorizationContext.isActive());
        assertEquals("", ParameterAuthorizationContext.currentPathPrefix());
    }

    public void testXmlAnySetterUsesSameOptIn() throws Exception {
        XmlMapper xmlMapper = enforcingXmlMapper();

        bind((path, t, a) -> false, new DynamicScalarAnySetterBean());
        DynamicScalarAnySetterBean allowed = xmlMapper.readValue(
                "<DynamicScalarAnySetterBean><role>admin</role></DynamicScalarAnySetterBean>",
                DynamicScalarAnySetterBean.class);
        assertEquals("admin", allowed.values.get("role"));

        bind((path, t, a) -> true, new UnannotatedAnySetterBean());
        UnannotatedAnySetterBean rejected = xmlMapper.readValue(
                "<UnannotatedAnySetterBean><role>admin</role></UnannotatedAnySetterBean>",
                UnannotatedAnySetterBean.class);
        assertTrue(rejected.values.isEmpty());
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

    public void testSetterBufferedBeforeCreatorParamIsAuthorized() throws Exception {
        // A non-creator property that appears before the last creator parameter is buffered by
        // Jackson and assigned through SettableBeanProperty.set() after construction.
        bind((path, t, a) -> "name".equals(path), new CreatorWithSetter(""));
        CreatorWithSetter result = mapper.readValue("{\"role\":\"admin\",\"name\":\"alice\"}", CreatorWithSetter.class);
        assertEquals("alice", result.name);
        assertNull("buffered setter property assigned without authorization ?", result.getRole());
    }

    public void testSetterAfterCreatorParamIsAuthorized() throws Exception {
        // Control: the same property after the last creator parameter takes the direct path.
        bind((path, t, a) -> "name".equals(path), new CreatorWithSetter(""));
        CreatorWithSetter result = mapper.readValue("{\"name\":\"alice\",\"role\":\"admin\"}", CreatorWithSetter.class);
        assertEquals("alice", result.name);
        assertNull(result.getRole());
    }

    public void testSetterOnlyTypeSameMemberOrderIsAuthorized() throws Exception {
        // Control: without a creator nothing is buffered, so member order does not matter.
        bind((path, t, a) -> "name".equals(path), new Person());
        Person result = mapper.readValue("{\"role\":\"admin\",\"name\":\"alice\"}", Person.class);
        assertEquals("alice", result.name);
        assertNull(result.role);
    }

    public void testNestedSetterBufferedBeforeCreatorParamIsAuthorized() throws Exception {
        bind((path, t, a) -> "inner".equals(path) || "inner.name".equals(path), new CreatorHolder(null));
        CreatorHolder result = mapper.readValue(
                "{\"inner\":{\"role\":\"admin\",\"name\":\"alice\"}}", CreatorHolder.class);
        assertNotNull(result.inner);
        assertEquals("alice", result.inner.name);
        assertNull("nested buffered setter property assigned without authorization ?", result.inner.getRole());
    }

    public void testBufferedBeanValuedSetterChildrenAuthorizedAtOwnDepth() throws Exception {
        // The buffered read goes through the final SettableBeanProperty.deserialize(), so the
        // nested bean's members must still be authorized under the property's own prefix.
        bind((path, t, a) -> "inner".equals(path) || "inner.name".equals(path)
                || "inner.address".equals(path) || "inner.address.city".equals(path), new CreatorAddressHolder(null));
        CreatorAddressHolder result = mapper.readValue(
                "{\"inner\":{\"address\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"},\"name\":\"alice\"}}",
                CreatorAddressHolder.class);
        assertEquals("alice", result.inner.name);
        assertNotNull("buffered nested bean dropped ?", result.inner.getAddress());
        assertEquals("nested member checked at the wrong depth ?", "Warsaw", result.inner.getAddress().city);
        assertNull(result.inner.getAddress().zip);
    }

    public void testBeanValuedSetterAfterCreatorParamChildrenAuthorizedAtOwnDepth() throws Exception {
        // Control: the direct path for the same property and authorizer.
        bind((path, t, a) -> "inner".equals(path) || "inner.name".equals(path)
                || "inner.address".equals(path) || "inner.address.city".equals(path), new CreatorAddressHolder(null));
        CreatorAddressHolder result = mapper.readValue(
                "{\"inner\":{\"name\":\"alice\",\"address\":{\"city\":\"Warsaw\",\"zip\":\"00-001\"}}}",
                CreatorAddressHolder.class);
        assertEquals("alice", result.inner.name);
        assertEquals("Warsaw", result.inner.getAddress().city);
        assertNull(result.inner.getAddress().zip);
    }

    public void testSetterlessCollectionElementsAuthorizedAtOwnDepth() throws Exception {
        // A collection getter without a setter is deserialized in place through the three-argument
        // deserialize(); element members must be checked under items[0], not under the parent.
        bind((path, t, a) -> "order".equals(path) || "order.items".equals(path) || "order.name".equals(path),
                new OrderHolder());
        OrderHolder result = mapper.readValue("{\"order\":{\"items\":[{\"name\":\"x\"}]}}", OrderHolder.class);
        assertEquals(1, result.order.getItems().size());
        assertNull("element member authorized by the parent's sibling grant ?", result.order.getItems().get(0).name);
    }

    public void testPolymorphicPropertyMembersAuthorizedAtOwnDepth() throws Exception {
        // A @JsonTypeInfo property is deserialized through deserializeWithType(); the subtype's
        // members must be checked under pet, not against the enclosing bean.
        bind((path, t, a) -> "pet".equals(path) || "owner".equals(path), new Kennel());
        Kennel result = mapper.readValue("{\"pet\":{\"@type\":\"dog\",\"owner\":\"alice\"}}", Kennel.class);
        assertTrue(result.pet instanceof Dog);
        assertNull("subtype member authorized by the enclosing bean's sibling grant ?", ((Dog) result.pet).owner);
    }

    public void testNoContext_passThroughBufferedSetter() throws Exception {
        CreatorWithSetter result = mapper.readValue("{\"role\":\"admin\",\"name\":\"alice\"}", CreatorWithSetter.class);
        assertEquals("alice", result.name);
        assertEquals("admin", result.getRole());
    }

    public void testNoContext_passThroughSetterlessCollection() throws Exception {
        OrderHolder result = mapper.readValue("{\"order\":{\"items\":[{\"name\":\"x\"}]}}", OrderHolder.class);
        assertEquals("x", result.order.getItems().get(0).name);
    }

    public void testNoContext_passThroughPolymorphicProperty() throws Exception {
        Kennel result = mapper.readValue("{\"pet\":{\"@type\":\"dog\",\"owner\":\"alice\"}}", Kennel.class);
        assertEquals("alice", ((Dog) result.pet).owner);
    }

    public void testBufferedPolymorphicSetterIsAuthorized() throws Exception {
        // A polymorphic setter buffered before the creator parameter goes through deserializeWithType().
        bind((path, t, a) -> "name".equals(path), new CreatorWithPet(""));
        CreatorWithPet result = mapper.readValue(
                "{\"pet\":{\"@type\":\"dog\",\"owner\":\"alice\"},\"name\":\"alice\"}", CreatorWithPet.class);
        assertEquals("alice", result.name);
        assertNull("buffered polymorphic setter assigned without authorization ?", result.getPet());
    }

    public void testMergeIntoExistingValueIsAuthorized() throws Exception {
        // @JsonMerge into a non-null value deserializes in place through the three-argument
        // deserialize(); an unauthorized property must leave the existing value untouched.
        bind((path, t, a) -> "name".equals(path), new MergingBean());
        MergingBean result = mapper.readValue("{\"name\":\"alice\",\"address\":{\"city\":\"Warsaw\"}}", MergingBean.class);
        assertEquals("alice", result.name);
        assertNull("merged into an unauthorized property ?", result.address.city);
    }

    public void testMergedPolymorphicPropertyWithInitialValueIsAuthorized() throws Exception {
        // @JsonMerge into a non-null polymorphic value would otherwise deserialize in place through
        // Jackson's typed merge, which reaches neither wrapper: the property is never authorized and
        // the subtype's members are checked against the enclosing bean's grants.
        bind((path, t, a) -> "owner".equals(path), new MergingKennel());
        MergingKennel result = mapper.readValue("{\"pet\":{\"owner\":\"alice\"}}", MergingKennel.class);
        assertNull("subtype member assigned through an unauthorized merged property ?",
                ((Dog) result.pet).owner);
    }

    public void testMergedPolymorphicPropertyIsReplacedThroughTheAuthorizedPath() throws Exception {
        MergingKennel bean = new MergingKennel();
        Animal initial = bean.pet;
        bind((path, t, a) -> "pet".equals(path) || "pet.owner".equals(path), bean);
        mapper.readerForUpdating(bean).readValue("{\"pet\":{\"@type\":\"dog\",\"owner\":\"alice\"}}");
        assertEquals("alice", ((Dog) bean.pet).owner);
        assertNotSame("merge must be disabled for a polymorphic property", initial, bean.pet);
    }

    public void testMergedPolymorphicPropertyBehindObjectIdWrapperIsAuthorized() throws Exception {
        // @JsonIdentityInfo makes resolve() copy the property into an ObjectIdReferenceProperty
        // before it decides on merging; the merge must still be refused there.
        bind((path, t, a) -> "owner".equals(path), new MergingIdentifiedKennel());
        MergingIdentifiedKennel result = mapper.readValue("{\"pet\":{\"owner\":\"alice\"}}",
                MergingIdentifiedKennel.class);
        assertNull("subtype member assigned through an unauthorized merged property ?",
                ((IdentifiedDog) result.pet).owner);
    }

    public void testMergedPolymorphicPropertyBehindManagedReferenceWrapperIsAuthorized() throws Exception {
        bind((path, t, a) -> "owner".equals(path), new MergingManagedKennel());
        MergingManagedKennel result = mapper.readValue("{\"pet\":{\"owner\":\"alice\"}}",
                MergingManagedKennel.class);
        assertNull("subtype member assigned through an unauthorized merged property ?",
                ((ManagedDog) result.pet).owner);
    }

    public void testMergedPolymorphicPropertyIsRefusedWhenUnmergeableIsNotIgnored() throws Exception {
        ObjectMapper strict = new ObjectMapper()
                .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
                .registerModule(new ParameterAuthorizingModule());
        bind((path, t, a) -> true, new MergingKennel());
        try {
            strict.readValue("{\"pet\":{\"@type\":\"dog\",\"owner\":\"alice\"}}", MergingKennel.class);
            fail("a polymorphic merge the module cannot authorize must be reported as a bad definition");
        } catch (InvalidDefinitionException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("cannot be merged"));
        }
    }

    public void testObjectIdPropertyIsAuthorized() throws Exception {
        // Jackson captures the id property in the ObjectIdReader before the module wraps it, and
        // ObjectIdValueProperty assigns the id through that captured property.
        bind((path, t, a) -> "name".equals(path), new PropertyIdentified());
        PropertyIdentified result = mapper.readValue("{\"id\":7,\"name\":\"alice\"}", PropertyIdentified.class);
        assertEquals("alice", result.name);
        assertEquals("id assigned through the ObjectIdReader without authorization ?", 0, result.id);
    }

    public void testObjectIdPropertyAssignedWhenAuthorized() throws Exception {
        bind((path, t, a) -> "id".equals(path), new PropertyIdentified());
        PropertyIdentified result = mapper.readValue("{\"id\":7,\"name\":\"alice\"}", PropertyIdentified.class);
        assertEquals(7, result.id);
        assertNull(result.name);
    }

    public void testBeanTypedObjectIdMembersAuthorizedUnderTheIdPath() throws Exception {
        // The id value is read through the ObjectIdReader's own deserializer; a bean-typed id's
        // members must be checked under id, not against the enclosing bean's same-named members.
        Set<String> granted = Set.of("id", "k", "name");
        bind((path, t, a) -> granted.contains(path), new KeyIdentified());
        KeyIdentified result = mapper.readValue("{\"id\":{\"k\":\"x\"},\"name\":\"alice\"}", KeyIdentified.class);
        assertEquals("alice", result.name);
        assertNotNull(result.id);
        assertNull("id member authorized by the enclosing bean's grant for [k] ?", result.id.k);
    }

    public void testBeanTypedObjectIdMembersBoundWhenGrantedUnderTheIdPath() throws Exception {
        Set<String> granted = Set.of("id", "id.k");
        bind((path, t, a) -> granted.contains(path), new KeyIdentified());
        KeyIdentified result = mapper.readValue("{\"id\":{\"k\":\"x\"},\"name\":\"alice\"}", KeyIdentified.class);
        assertEquals("x", result.id.k);
        assertNull(result.name);
    }

    public void testBeanTypedObjectIdDeclaredOnTheReferencingPropertyAuthorizedUnderTheIdPath() throws Exception {
        // A per-property @JsonIdentityInfo builds its reader in createContextual, after the module ran.
        Set<String> granted = Set.of("child", "child.id", "child.k", "child.name");
        bind((path, t, a) -> granted.contains(path), new KeyIdentifiedHolder());
        KeyIdentifiedHolder result = mapper.readValue("{\"child\":{\"id\":{\"k\":\"x\"},\"name\":\"alice\"}}",
                KeyIdentifiedHolder.class);
        assertEquals("alice", result.child.name);
        assertNull("id member authorized by the referring bean's grant for [child.k] ?", result.child.id.k);
    }

    public void testDroppedObjectLeavesTheParserAtItsEnd_nullObjectId() throws Exception {
        // The id bean fails on a redacted member, so the null id fails Jackson's binding mid-object;
        // the dropped object's remaining fields must not be read by the parent.
        Set<String> granted = Set.of("child", "child.id", "child.name", "other", "name");
        bind((path, t, a) -> granted.contains(path), new StrictHolder());
        StrictHolder result = mapper.readValue(
                "{\"child\":{\"id\":{\"k\":\"a\"},\"name\":\"x\"},\"other\":\"o\",\"name\":\"h\"}",
                StrictHolder.class);
        assertNull(result.child);
        assertEquals("o", result.other);
        assertEquals("child's name read into the parent ?", "h", result.name);
    }

    public void testDroppedObjectLeavesTheParserAtItsEnd_creatorFailure() throws Exception {
        // A creator with one parameter constructs as soon as it arrives; a redacted primitive fails it
        // with the rest of the object, including a nested one, still unread.
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        Set<String> granted = Set.of("money", "money.note", "money.name", "other", "name");
        bind((path, t, a) -> granted.contains(path), new StrictHolder());
        StrictHolder result = mapper.readValue(
                "{\"money\":{\"amount\":5,\"detail\":{\"x\":1},\"name\":\"x\"},\"other\":\"o\",\"name\":\"h\"}",
                StrictHolder.class);
        assertNull(result.money);
        assertEquals("o", result.other);
        assertEquals("h", result.name);
    }

    public void testDroppedPolymorphicObjectWithLateTypeIdFailsTheRead() throws Exception {
        // With the type id after other keys Jackson reads the subtype from a parser spliced from a
        // buffer and the real parser; the value straddles the splice, so its drop cannot resync and
        // is not swallowed. Read into an existing bean as the handlers do, so nothing above it can
        // drop itself instead.
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        Set<String> granted = Set.of("pet", "pet.name");
        bind((path, t, a) -> granted.contains(path), new StrictHolder());
        try {
            mapper.readerForUpdating(new StrictHolder())
                    .readValue("{\"pet\":{\"legs\":4,\"@type\":\"strict\",\"name\":\"x\"},\"name\":\"h\"}");
            fail("a drop that cannot resync the parser must fail the read");
        } catch (JsonMappingException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("legs"));
        }
    }

    public void testDroppedObjectNestedInALateTypeIdValueStillResyncs() throws Exception {
        // A bean nested inside the spliced value lies wholly on one side of the splice.
        Set<String> granted = Set.of("pet", "pet.legs", "pet.name", "pet.owner", "name");
        bind((path, t, a) -> granted.contains(path), new StrictHolder());
        StrictHolder result = mapper.readValue(
                "{\"pet\":{\"legs\":4,\"@type\":\"strict\",\"owner\":{\"k\":\"a\"},\"name\":\"x\"},\"name\":\"h\"}",
                StrictHolder.class);
        StrictPet pet = (StrictPet) result.pet;
        assertNull(pet.owner);
        assertEquals("x", pet.name);
        assertEquals("h", result.name);
    }

    public void testDroppedLateTypeIdValueNestedInAnotherLateTypeIdValueDoesNotLeak() throws Exception {
        // The inner splice is based on the outer value's buffer, so its root context has a buffer
        // parent; it still straddles its own splice and must be refused, escalating the drop.
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        Set<String> granted = Set.of("mid", "mid.pet", "mid.pet.name", "mid.pet.owner", "mid.name", "name");
        bind((path, t, a) -> granted.contains(path), new PolyOuter());
        PolyOuter result = mapper.readValue(
                "{\"mid\":{\"pet\":{\"legs\":4,\"@type\":\"strict\",\"owner\":{\"k\":\"a\"},\"name\":\"pet\"},"
                        + "\"name\":\"midname\",\"@type\":\"mid\"},\"name\":\"outer\"}",
                PolyOuter.class);
        assertNull("a drop that cannot resync must escalate, not leak the pet's fields into mid", result);
    }

    public void testRefusedResyncDropsTheNearestEnclosingObject() throws Exception {
        // The parent had no redaction of its own; the refused drop below it must still mark it.
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        Set<String> granted = Set.of("mid", "mid.secret", "mid.pet", "mid.pet.name", "name");
        bind((path, t, a) -> granted.contains(path), new StrictOuter());
        StrictOuter result = mapper.readValue(
                "{\"mid\":{\"secret\":\"s\",\"pet\":{\"legs\":4,\"@type\":\"strict\",\"name\":\"pet\"}},\"name\":\"outer\"}",
                StrictOuter.class);
        assertNull(result.mid);
        assertEquals("outer", result.name);
    }

    public void testDroppedObjectUnderADroppedPolymorphicChildLeavesTheParserAtItsEnd() throws Exception {
        // Jackson clears the parser's current token before splicing for a late type id; when the
        // subtype's drop is refused and the parent's own wrapper swallows, it must still resync.
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        Set<String> granted = Set.of("mid", "mid.pet", "mid.pet.name", "name");
        bind((path, t, a) -> granted.contains(path), new StrictOuter());
        StrictOuter result = mapper.readValue(
                "{\"mid\":{\"secret\":\"s\",\"pet\":{\"legs\":4,\"@type\":\"strict\",\"name\":\"pet\"}},\"name\":\"outer\"}",
                StrictOuter.class);
        assertNull(result.mid);
        assertEquals("dropped pet's name read into the outer bean ?", "outer", result.name);
    }

    public void testDroppedObjectInsideAnUnwrappedReplayStopsAtItsEnd() throws Exception {
        // Unwrapped properties are replayed from a token buffer whose contexts carry no nesting
        // depth; a drop there must stop at the object's end, not drain the buffer.
        Set<String> granted = Set.of("inner", "inner.keys", "inner.name", "other");
        bind((path, t, a) -> granted.contains(path), new UnwrappedHolder());
        UnwrappedHolder result = mapper.readValue(
                "{\"keys\":[{\"k\":\"a\"},{\"k\":\"b\"}],\"name\":\"n\",\"other\":\"o\"}",
                UnwrappedHolder.class);
        assertNotNull("unwrapped bean dropped because the replay was drained ?", result.inner);
        assertEquals(2, result.inner.keys.size());
        assertNull(result.inner.keys.get(0));
        assertEquals("n", result.inner.name);
        assertEquals("o", result.other);
    }

    public void testCreatorBoundObjectIdIsAssignedByTheCreatorOnly() throws Exception {
        // Jackson skips the post-construction write of a creator-bound id (records have no setter
        // for it); the wrapper must keep that skip and leave the id to the authorized creator path.
        bind((path, t, a) -> true, new IdentifiedRecord(0, null));
        IdentifiedRecord result = mapper.readValue("{\"id\":7,\"name\":\"alice\"}", IdentifiedRecord.class);
        assertEquals(7, result.id());
        assertEquals("alice", result.name());
    }

    public void testNoContext_passThroughCreatorBoundObjectId() throws Exception {
        IdentifiedRecord result = mapper.readValue("{\"id\":7,\"name\":\"alice\"}", IdentifiedRecord.class);
        assertEquals(7, result.id());
    }

    public void testCreatorBoundObjectIdRejectedAtTheCreator() throws Exception {
        bind((path, t, a) -> "name".equals(path), new IdentifiedRecord(0, null));
        IdentifiedRecord result = mapper.readValue("{\"id\":7,\"name\":\"alice\"}", IdentifiedRecord.class);
        assertEquals("alice", result.name());
        assertEquals(0, result.id());
    }

    public void testNoContext_passThroughCreatorBoundObjectIdDeclaredOnTheReferencingProperty() throws Exception {
        // A per-property @JsonIdentityInfo builds its reader in createContextual from the already
        // wrapped property; the creator-bound skip must hold there as well.
        IdentifiedRecordHolder result = mapper.readValue("{\"rec\":{\"id\":7,\"name\":\"alice\"}}",
                IdentifiedRecordHolder.class);
        assertEquals(7, result.rec.id());
    }

    public void testCreatorBoundObjectIdDeclaredOnTheReferencingPropertyIsAuthorized() throws Exception {
        bind((path, t, a) -> "rec".equals(path) || "rec.name".equals(path), new IdentifiedRecordHolder());
        IdentifiedRecordHolder result = mapper.readValue("{\"rec\":{\"id\":7,\"name\":\"alice\"}}",
                IdentifiedRecordHolder.class);
        assertEquals("alice", result.rec.name());
        assertEquals(0, result.rec.id());
    }

    public void testCreatorBoundObjectIdRepeatedAfterConstructionIsNotAssigned() throws Exception {
        // Stock Jackson pushes the repeated key through the creator property's fallback field; the
        // wrapper cannot tell that write from the one Jackson skips itself, so the creator's value stays.
        bind((path, t, a) -> true, new IdentifiedFinalField(0, null));
        IdentifiedFinalField result = mapper.readValue("{\"id\":1,\"name\":\"alice\",\"id\":7}",
                IdentifiedFinalField.class);
        assertEquals("alice", result.name);
        assertEquals("repeated id written through the creator property's fallback field ?", 1, result.id);
    }

    public void testForwardReferenceIsAuthorizedUnderTheReferringPropertyPath() throws Exception {
        // Jackson assigns a forward reference when the referenced object appears, wherever that is;
        // the deferred write must be checked under the referring property's own path, not the
        // target's.
        Set<String> granted = Set.of("people", "people[0].id", "people[0].name", "people[0].friend",
                "boss", "boss.id", "boss.name");
        bind((path, t, a) -> granted.contains(path), new Office());
        Office result = mapper.readValue(
                "{\"people\":[{\"id\":1,\"name\":\"a\",\"friend\":2}],\"boss\":{\"id\":2,\"name\":\"b\"}}",
                Office.class);
        assertSame("forward reference rejected under the target's path ?", result.boss, result.people.get(0).friend);
    }

    public void testForwardReferenceRejectedUnderTheReferringPropertyPath() throws Exception {
        Set<String> granted = Set.of("people", "people[0].id", "people[0].name",
                "boss", "boss.id", "boss.name", "boss.friend");
        bind((path, t, a) -> granted.contains(path), new Office());
        Office result = mapper.readValue(
                "{\"people\":[{\"id\":1,\"name\":\"a\",\"friend\":2}],\"boss\":{\"id\":2,\"name\":\"b\"}}",
                Office.class);
        assertNull("forward reference authorized under the target's path ?", result.people.get(0).friend);
        assertEquals("b", result.boss.name);
    }

    public void testForwardReferenceResolvedInsideDynamicKeyScopeKeepsTheReadTimeVerdict() throws Exception {
        // The target lands in an any-setter subtree, so the deferred write runs while that dynamic
        // scope is active; the verdict taken when the reference was read must stand.
        ObjectMapper enforcingMapper = enforcingMapper();
        Set<String> granted = Set.of("people", "people[0].id", "people[0].name", "people[0].friend");
        bind((path, t, a) -> granted.contains(path), new DynamicOffice());
        DynamicOffice result = enforcingMapper.readValue(
                "{\"people\":[{\"id\":1,\"name\":\"a\",\"friend\":2}],\"boss\":{\"id\":2,\"name\":\"b\"}}",
                DynamicOffice.class);
        assertSame("forward reference re-checked under the target's dynamic-key scope ?",
                result.values.get("boss"), result.people.get(0).friend);
    }

    public void testForwardReferenceFromCreatorBoundReferrerIsAuthorizedUnderItsOwnPath() throws Exception {
        // A creator-bound referrer is not constructed yet when its forward reference is read;
        // Jackson buffers the reference and assigns it after construction.
        Set<String> granted = Set.of("people", "people[0].id", "people[0].name", "people[0].friend",
                "boss", "boss.id", "boss.name");
        bind((path, t, a) -> granted.contains(path), new CreatorOffice());
        CreatorOffice result = mapper.readValue(
                "{\"people\":[{\"id\":1,\"friend\":2}],\"boss\":{\"id\":2,\"name\":\"b\"}}",
                CreatorOffice.class);
        assertSame("forward reference rejected under the target's path ?", result.boss, result.people.get(0).getFriend());
    }

    public void testNoContext_passThroughForwardReference() throws Exception {
        Office result = mapper.readValue(
                "{\"people\":[{\"id\":1,\"name\":\"a\",\"friend\":2}],\"boss\":{\"id\":2,\"name\":\"b\"}}",
                Office.class);
        assertSame(result.boss, result.people.get(0).friend);
        assertFalse(AuthorizedForwardReferences.isActive());
    }

    public void testJacksonHandlerClearsAuthorizedForwardReferencesAfterReadFailure() throws Exception {
        AuthorizedForwardReferences.expect(new ReadableObjectId(new ObjectIdGenerator.IdKey(Object.class, null, "stale")), "stale");
        assertTrue(AuthorizedForwardReferences.isActive());

        try {
            new JacksonJsonHandler().toObject(null, new StringReader("{"), new Person());
            fail("expected malformed JSON to fail");
        } catch (Exception expected) {
            // The handler's request-boundary cleanup must run even when Jackson aborts the read.
        }

        assertFalse(AuthorizedForwardReferences.isActive());
    }

    public void testBufferedSetterInsideDynamicKeyScopeIsAuthorizedByDepth() throws Exception {
        // Inside a dynamic-key scope the buffered path must consult the same depth rule as the
        // direct path, not the annotation authorizer (which rejects everything here).
        ObjectMapper enforcingMapper = enforcingMapper();
        bind((path, t, a) -> false, new DynamicDepthTwoCreatorAnySetterBean());
        DynamicDepthTwoCreatorAnySetterBean result = enforcingMapper.readValue(
                "{\"home\":{\"role\":\"admin\",\"name\":\"alice\"}}", DynamicDepthTwoCreatorAnySetterBean.class);
        assertEquals("alice", result.values.get("home").name);
        assertEquals("admin", result.values.get("home").getRole());
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

    private static final class RecordingAppender extends AbstractAppender {
        private final List<LogEvent> events = new ArrayList<>();

        private RecordingAppender() {
            super("WW-5720", null, null, false, null);
        }

        @Override
        public void append(LogEvent event) {
            events.add(event.toImmutable());
        }
    }

    private ObjectMapper enforcingMapper() {
        return new ObjectMapper().registerModule(new ParameterAuthorizingModule(true));
    }

    /** Built the way {@code JacksonXmlHandler} builds its mapper: the XML module registered last. */
    private XmlMapper enforcingXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper(new XmlFactory(), null);
        xmlMapper.registerModule(new ParameterAuthorizingModule(true));
        xmlMapper.registerModule(new JacksonXmlModule());
        return xmlMapper;
    }

    public static class Person {
        public String name;
        public String role;
        public Address address;
        public java.util.List<Address> addresses;
        public Address[] addressArray;
        public java.util.Map<String, Address> addressMap;
        public RecordAddress recordAddress;
    }

    public static class CreatorWithSetter {
        public final String name;
        private String role;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CreatorWithSetter(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class CreatorWithAddress {
        public final String name;
        private Address address;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CreatorWithAddress(@JsonProperty("name") String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class CreatorAddressHolder {
        public final CreatorWithAddress inner;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CreatorAddressHolder(@JsonProperty("inner") CreatorWithAddress inner) {
            this.inner = inner;
        }
    }

    public static class OrderHolder {
        public Order order;
    }

    public static class Order {
        public String name;
        private final java.util.List<OrderItem> itemList = new java.util.ArrayList<>();

        public java.util.List<OrderItem> getItems() {
            return itemList;
        }
    }

    public static class OrderItem {
        public String name;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(value = Dog.class, name = "dog"))
    public abstract static class Animal {
    }

    public static class Dog extends Animal {
        public String owner;
    }

    public static class Kennel {
        public Animal pet;
        public String owner;
    }

    public static class CreatorWithPet {
        public final String name;
        private Animal pet;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CreatorWithPet(@JsonProperty("name") String name) {
            this.name = name;
        }

        public Animal getPet() {
            return pet;
        }

        public void setPet(Animal pet) {
            this.pet = pet;
        }
    }

    public static class MergingBean {
        public String name;
        @JsonMerge
        public Address address = new Address();
    }

    public static class MergingKennel {
        @JsonMerge
        public Animal pet = new Dog();
        public String owner;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class PropertyIdentified {
        public int id;
        public String name;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class Employee {
        public int id;
        public String name;
        public Employee friend;
    }

    public static class Office {
        public List<Employee> people;
        public Employee boss;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class CreatorEmployee {
        public final int id;
        public final String name;
        private CreatorEmployee friend;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CreatorEmployee(@JsonProperty("id") int id, @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }

        public CreatorEmployee getFriend() {
            return friend;
        }

        public void setFriend(CreatorEmployee friend) {
            this.friend = friend;
        }
    }

    public static class CreatorOffice {
        public List<CreatorEmployee> people;
        public CreatorEmployee boss;
    }

    public static class DynamicOffice {
        public List<Employee> people;
        public final Map<String, Employee> values = new LinkedHashMap<>();

        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true, depth = 3)
        public void put(String name, Employee value) {
            values.put(name, value);
        }
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public record IdentifiedRecord(int id, String name) {
    }

    public record StrictKey(String k) {
        public StrictKey {
            java.util.Objects.requireNonNull(k);
        }
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class StrictIdentified {
        public StrictKey id;
        public String name;
    }

    public static class StrictMoney {
        public final int amount;
        public Address detail;
        public String name;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public StrictMoney(@JsonProperty("amount") int amount) {
            this.amount = amount;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(value = StrictPet.class, name = "strict"))
    public abstract static class StrictAnimal {
    }

    public static class StrictPet extends StrictAnimal {
        public final int legs;
        public StrictKey owner;
        public String name;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public StrictPet(@JsonProperty("legs") int legs) {
            this.legs = legs;
        }
    }

    public static class StrictMid {
        public String secret;
        public StrictAnimal pet;
    }

    public static class StrictOuter {
        public StrictMid mid;
        public String name;
    }

    public static class UnwrappedInner {
        public List<StrictKey> keys;
        public String name;
    }

    public static class UnwrappedHolder {
        @JsonUnwrapped
        public UnwrappedInner inner;
        public String other;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(value = StrictMidPoly.class, name = "mid"))
    public abstract static class StrictContainer {
    }

    public static class StrictMidPoly extends StrictContainer {
        public StrictAnimal pet;
        public String name;
    }

    public static class PolyOuter {
        public StrictContainer mid;
        public String name;
    }

    public static class StrictHolder {
        public StrictIdentified child;
        public StrictMoney money;
        public StrictAnimal pet;
        public String other;
        public String name;
    }

    public static class Key {
        public String k;

        @Override
        public boolean equals(Object other) {
            return other instanceof Key that && java.util.Objects.equals(k, that.k);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hashCode(k);
        }
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class KeyIdentified {
        public Key id;
        public String k;
        public String name;
    }

    public static class PlainKeyed {
        public Key id;
        public String k;
        public String name;
    }

    public static class KeyIdentifiedHolder {
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        public PlainKeyed child;
    }

    public record PlainRecord(int id, String name) {
    }

    public static class IdentifiedRecordHolder {
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        public PlainRecord rec;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class IdentifiedFinalField {
        public final int id;
        public String name;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public IdentifiedFinalField(@JsonProperty("id") int id, @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(value = IdentifiedDog.class, name = "dog"))
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    public abstract static class IdentifiedAnimal {
    }

    public static class IdentifiedDog extends IdentifiedAnimal {
        public String owner;
    }

    public static class MergingIdentifiedKennel {
        @JsonMerge
        public IdentifiedAnimal pet = new IdentifiedDog();
        public String owner;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(value = ManagedDog.class, name = "dog"))
    public abstract static class ManagedAnimal {
        @JsonBackReference
        public MergingManagedKennel kennel;
    }

    public static class ManagedDog extends ManagedAnimal {
        public String owner;
    }

    public static class MergingManagedKennel {
        @JsonMerge
        @JsonManagedReference
        public ManagedAnimal pet = new ManagedDog();
        public String owner;
    }

    public static class CreatorHolder {
        public final CreatorWithSetter inner;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CreatorHolder(@JsonProperty("inner") CreatorWithSetter inner) {
            this.inner = inner;
        }
    }

    public static class Address {
        public String city;
        public String zip;
        public Geo geo;
    }

    public static class Geo {
        public String country;
    }

    public static class UnannotatedAnySetterBean {
        public final Map<String, Object> values = new LinkedHashMap<>();

        @JsonAnySetter
        public void put(String name, Object value) {
            values.put(name, value);
        }
    }

    public static class NestedAnySettersBean {
        public final Map<String, Object> values = new LinkedHashMap<>();
        public UnannotatedAnySetterBean nested;

        @JsonAnySetter
        public void put(String name, Object value) {
            values.put(name, value);
        }
    }

    public static class AnnotatedAnySetterBean {
        public final Map<String, Object> values = new LinkedHashMap<>();

        @JsonAnySetter
        @StrutsParameter
        public void put(String name, Object value) {
            values.put(name, value);
        }
    }

    public static class DynamicScalarAnySetterBean {
        public final Map<String, Object> values = new LinkedHashMap<>();

        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true)
        public void put(String name, Object value) {
            values.put(name, value);
        }
    }

    public static class DynamicFieldAnySetterBean {
        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true)
        public Map<String, Object> values = new LinkedHashMap<>();
    }

    public static class DynamicDepthOneFieldAnySetterBean {
        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true, depth = 1)
        public Map<String, Address> values = new LinkedHashMap<>();
    }

    public static class DynamicDepthZeroAnySetterBean {
        public final Map<String, Address> values = new LinkedHashMap<>();

        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true)
        public void put(String name, Address value) {
            values.put(name, value);
        }
    }

    public static class DynamicDepthOneAnySetterBean {
        public final Map<String, Address> values = new LinkedHashMap<>();

        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true, depth = 1)
        public void put(String name, Address value) {
            values.put(name, value);
        }
    }

    public static class DynamicDepthTwoCreatorAnySetterBean {
        public final Map<String, CreatorWithSetter> values = new LinkedHashMap<>();

        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true, depth = 2)
        public void put(String name, CreatorWithSetter value) {
            values.put(name, value);
        }
    }

    public static class DynamicDepthTwoAnySetterBean {
        public final Map<String, Address> values = new LinkedHashMap<>();

        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true, depth = 2)
        public void put(String name, Address value) {
            values.put(name, value);
        }
    }

    public static class CreatorAnySetterBean {
        public final Map<String, Object> values;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CreatorAnySetterBean(@JsonAnySetter Map<String, Object> values) {
            this.values = values;
        }
    }

    public static class PropertyCreatorWithAnySetterBean {
        public final String name;
        public final Map<String, Object> values = new LinkedHashMap<>();

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public PropertyCreatorWithAnySetterBean(@JsonProperty("name") String name) {
            this.name = name;
        }

        @JsonAnySetter
        @StrutsParameter(allowDynamicKeys = true)
        public void put(String key, Object value) {
            values.put(key, value);
        }
    }

    public static class AnySetterParent {
        public DynamicScalarAnySetterBean child;
    }

    public static class UnwrappedBean {
        @JsonUnwrapped
        public Address address = new Address();
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

    public static class CamelCasePerson {
        public String userName;
        public CamelCaseAddress homeAddress;
        public List<CamelCaseAddress> otherAddresses;
    }

    public static class CamelCaseAddress {
        public String streetName;
    }

    public static class FluentMutatorBean {
        String settings;
        String issue;

        @JsonProperty("settings")
        public void settings(String settings) { this.settings = settings; }

        @JsonProperty("issue")
        public void issue(String issue) { this.issue = issue; }
    }

    public static class GetterNamedMutatorBean {
        String name;
        String nick;

        public void setName(String name) { this.name = name; }

        @JsonProperty("nick")
        public void getName(String nick) { this.nick = nick; }
    }
}
