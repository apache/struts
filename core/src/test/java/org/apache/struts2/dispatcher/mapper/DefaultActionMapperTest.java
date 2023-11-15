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
package org.apache.struts2.dispatcher.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletRequest;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.inject.Container;

/**
 * DefaultActionMapper test case.
 */
public class DefaultActionMapperTest extends StrutsInternalTestCase {

    private MockHttpServletRequest req;
    private ConfigurationManager configManager;
    private Configuration config;

    @SuppressWarnings("rawtypes")
    protected void setUp() throws Exception {
        super.setUp();
        req = new MockHttpServletRequest();
        req.setParameters(new HashMap());
        req.setContextPath("/my/namespace");

        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig.Builder("myns")
            .namespace("/my/namespace").build();
        PackageConfig pkg2 = new PackageConfig.Builder("my").namespace("/my").build();
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        configManager = new ConfigurationManager(Container.DEFAULT_NAME) {
            public Configuration getConfiguration() {
                return config;
            }
        };
    }

    public void testGetMapping() {
        req.setRequestURI("/my/namespace/actionName.action");
        req.setServletPath("/my/namespace/actionName.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    @SuppressWarnings("rawtypes")
    public void testGetMappingWithMethod() {
        req.setParameters(new HashMap());
        req.setRequestURI("/my/namespace/actionName!add.action");
        req.setServletPath("/my/namespace/actionName!add.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertEquals("add", mapping.getMethod());
    }

    public void testGetMappingWithSlashedName() {

        req.setRequestURI("/my/foo/actionName.action");
        req.setServletPath("/my/foo/actionName.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my", mapping.getNamespace());
        assertEquals("foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithSlashedNameAtRootButNoSlashPackage() {

        req.setRequestURI("/foo/actionName.action");
        req.setServletPath("/foo/actionName.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithSlashedNameAtRoot() {
        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig.Builder("myns")
            .namespace("/my/namespace").build();
        PackageConfig pkg2 = new PackageConfig.Builder("my").namespace("/my").build();
        PackageConfig pkg3 = new PackageConfig.Builder("root").namespace("/").build();
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        config.addPackageConfig("root", pkg3);
        configManager = new ConfigurationManager(Container.DEFAULT_NAME) {
            public Configuration getConfiguration() {
                return config;
            }
        };

        req.setRequestURI("/foo/actionName.action");
        req.setServletPath("/foo/actionName.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/", mapping.getNamespace());
        assertEquals("foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }


    public void testGetMappingWithNamespaceSlash() {

        req.setRequestURI("/my-hh/abc.action");
        req.setServletPath("/my-hh/abc.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("abc", mapping.getName());

        
        
        mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("my-hh/abc", mapping.getName());
    }

    public void testGetMappingWithUnknownNamespace() {
        req.setRequestURI("/bo/foo/actionName.action");
        req.setServletPath("/bo/foo/actionName.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithUnknownNamespaceButFullNamespaceSelect() {
        req.setRequestURI("/bo/foo/actionName.action");
        req.setServletPath("/bo/foo/actionName.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAlwaysSelectFullNamespace("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/bo/foo", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithActionName_methodAndName() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping mapping = mapper.getMappingFromActionName("actionName!add");
        assertEquals("actionName", mapping.getName());
        assertEquals("add", mapping.getMethod());
    }

    public void testGetMappingWithActionName_name() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMappingFromActionName("actionName");
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithActionName_noDynamicMethod() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("false");
        ActionMapping mapping = mapper.getMappingFromActionName("actionName!add");
        assertEquals("actionName!add", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithActionName_noDynamicMethodColonPrefix() {

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(DefaultActionMapper.METHOD_PREFIX + "someMethod", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowDynamicMethodCalls("false");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("someServletPath", actionMapping.getName());
        assertNull(actionMapping.getMethod());
    }

    public void testGetMappingWithActionName_null() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMappingFromActionName(null);
        assertNull(mapping);
    }

    @SuppressWarnings("rawtypes")
    public void testGetUri() {
        req.setParameters(new HashMap());
        req.setRequestURI("/my/namespace/actionName.action");
        req.setServletPath("/my/namespace/actionName.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    @SuppressWarnings("rawtypes")
    public void testGetUriWithSemicolonPresent() {
        req.setParameters(new HashMap());
        req.setRequestURI("/my/namespace/actionName.action;abc=123rty56");
        req.setServletPath("/my/namespace/actionName.action;abc=123rty56");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    @SuppressWarnings("rawtypes")
    public void testGetUriWithMethod() {
        req.setParameters(new HashMap());
        req.setRequestURI("/my/namespace/actionName!add.action");
        req.setServletPath("/my/namespace/actionName!add.action");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace/actionName!add.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithOriginalExtension() {
        ActionMapping mapping = new ActionMapping("actionName", "/ns", null, new HashMap<>());

        ActionMapping orig = new ActionMapping();
        orig.setExtension("foo");
        ActionContext.getContext().put(ServletActionContext.ACTION_MAPPING, orig);

        DefaultActionMapper mapper = new DefaultActionMapper();
        assertEquals("/ns/actionName.foo", mapper.getUriFromActionMapping(mapping));
    }

    @SuppressWarnings("rawtypes")
    public void testGetMappingWithNoExtension() {
        req.setParameters(new HashMap());
        req.setRequestURI("/my/namespace/actionName");
        req.setServletPath("/my/namespace/actionName");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    @SuppressWarnings("rawtypes")
    public void testGetMappingWithNoExtensionButUriHasExtension() {
        req.setParameters(new HashMap());
        req.setRequestURI("/my/namespace/actionName.html");
        req.setServletPath("/my/namespace/actionName.html");
        
        

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName.html", mapping.getName());
        assertNull(mapping.getMethod());
    }

    // =============================
    // === test name & namespace ===
    // =============================

    public void testParseNameAndNamespace1() {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }

    public void testParseNameAndNamespace2() {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/");
    }

    public void testParseNameAndNamespace3() {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/my/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/my");
    }

    public void testParseNameAndNamespace_NoSlashes() {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setSlashesInActionNames("false");
        defaultActionMapper.parseNameAndNamespace("/foo/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }

    public void testParseNameAndNamespace_AllowSlashes() {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setSlashesInActionNames("true");
        defaultActionMapper.parseNameAndNamespace("/foo/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "foo/someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }


    // ===========================
    // === test special prefix ===
    // ===========================

    public void testActionPrefixWhenDisabled() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("someServletPath", actionMapping.getName());
    }

    public void testActionPrefixWhenEnabled() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("myAction", actionMapping.getName());
    }

    public void testActionPrefixWhenSlashesAndCrossNamespaceDisabled() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "my/Action", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        defaultActionMapper.setSlashesInActionNames("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("my/Action", actionMapping.getName());
    }

    public void testActionPrefixWhenSlashesButSlashesDisabledAndCrossNamespaceDisabled() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "my/Action", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        defaultActionMapper.setSlashesInActionNames("false");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("Action", actionMapping.getName());
    }

    public void testActionPrefix_fromImageButton() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.x", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.y", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("myAction", actionMapping.getName());
    }

    public void testActionPrefix_fromIEImageButton() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.x", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.y", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("myAction", actionMapping.getName());
    }

    public void testActionPrefix() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("action:" + "next", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setServletPath("/index.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertNotNull(actionMapping);
        assertEquals("/", actionMapping.getNamespace());
        assertEquals("index", actionMapping.getName());
        assertNull(actionMapping.getMethod());
    }

    public void testActionPrefixWhenAllowed() {
        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig.Builder("test")
            .namespace("/test")
            .addActionConfig("execute", new ActionConfig.Builder("test", "index", "org.test.TestAction")
                .methodName("execute")
                .addAllowedMethod("execute")
                .build())
            .addActionConfig("next", new ActionConfig.Builder("test", "next", "org.test.TestAction")
                .methodName("next")
                .addAllowedMethod("next")
                .addResultConfig(new ResultConfig.Builder("next", "org.test.TestResult")
                    .build())
                .build())
            .build();

        config.addPackageConfig("test", pkg);


        configManager = new ConfigurationManager(Container.DEFAULT_NAME) {
            public Configuration getConfiguration() {
                return config;
            }
        };

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("action:" + "next", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setServletPath("/test/index.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        defaultActionMapper.setAllowActionPrefix("true");

        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertNotNull(actionMapping);
        assertEquals("/test", actionMapping.getNamespace());
        assertEquals("next", actionMapping.getName());
        assertEquals("next", actionMapping.getMethod());
    }

    public void testActionPrefixWithBangWhenAllowed() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("action:" + "next!another", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setServletPath("/index.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        defaultActionMapper.setAllowActionPrefix("true");
        defaultActionMapper.setAllowDynamicMethodCalls("true");

        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertNotNull(actionMapping);
        assertEquals("/", actionMapping.getNamespace());
        assertEquals("next", actionMapping.getName());
        assertEquals("another", actionMapping.getMethod());
    }

    public void testMethodPrefixWhenAllowed() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("method:" + "another", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setServletPath("/index.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        defaultActionMapper.setAllowDynamicMethodCalls("true");

        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertNotNull(actionMapping);
        assertEquals("/", actionMapping.getNamespace());
        assertEquals("index", actionMapping.getName());
        assertEquals("another", actionMapping.getMethod());
    }

    public void testCustomActionPrefix() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("foo:myAction", "");

        final StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.addParameterAction("foo", (key, mapping) -> mapping.setName("myAction"));
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals(actionMapping.getName(), "myAction");
    }

    public void testDropExtension() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        String name = mapper.dropExtension("foo.action", new ActionMapping());
        assertEquals("Name not right: " + name, "foo", name);

        name = mapper.dropExtension("foo.action.action", new ActionMapping());
        assertEquals("Name not right: " + name, "foo.action", name);

    }

    public void testDropExtensionWhenBlank() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("action,,");
        String name = mapper.dropExtension("foo.action", new ActionMapping());
        assertEquals("Name not right: " + name, "foo", name);
        name = mapper.dropExtension("foo", new ActionMapping());
        assertEquals("Name not right: " + name, "foo", name);
        assertNull(mapper.dropExtension("foo.bar", new ActionMapping()));
        assertNull(mapper.dropExtension("foo.", new ActionMapping()));
    }

    public void testDropExtensionEmbeddedDot() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("action,,");

        String name = mapper.dropExtension("/foo/bar-1.0/baz.action", new ActionMapping());
        assertEquals("Name not right: " + name, "/foo/bar-1.0/baz", name);

        name = mapper.dropExtension("/foo/bar-1.0/baz", new ActionMapping());
        assertEquals("Name not right: " + name, "/foo/bar-1.0/baz", name);
    }

    public void testGetUriFromActionMapper1() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper2() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper3() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper4() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper5() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper6() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper7() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper8() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapperWithDisabledDMI() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper9() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper10() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper11() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName.action");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper12() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName.action");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper_justActionAndMethod() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setExtension("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("myActionName!myMethod", uri);
    }

    public void testGetUriFromActionMapperWhenBlankExtension() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions(",");
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod", uri);
    }

    public void testSetExtension() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("");
        assertNull(mapper.extensions);
        mapper.setExtensions(null);
        assertNull(mapper.extensions);

        mapper.setExtensions(",xml");
        assertEquals(Arrays.asList("", "xml"), mapper.extensions);

        mapper.setExtensions("html,xml,");
        assertEquals(Arrays.asList("html", "xml", ""), mapper.extensions);

        mapper.setExtensions("html,,xml");
        assertEquals(Arrays.asList("html", "", "xml"), mapper.extensions);

        mapper.setExtensions("xml");
        assertEquals(Collections.singletonList("xml"), mapper.extensions);

        mapper.setExtensions(",");
        assertEquals(Collections.singletonList(""), mapper.extensions);


    }

    public void testAllowedNamespaceNames() {
        DefaultActionMapper mapper = new DefaultActionMapper();

        String namespace = "/";
        assertEquals(namespace, mapper.cleanupNamespaceName(namespace));

        namespace = "${namespace}";
        assertEquals(mapper.defaultNamespaceName, mapper.cleanupNamespaceName(namespace));

        namespace = "${${%{namespace}}}";
        assertEquals(mapper.defaultNamespaceName, mapper.cleanupNamespaceName(namespace));

        namespace = "${#foo='namespace',#foo}";
        assertEquals(mapper.defaultNamespaceName, mapper.cleanupNamespaceName(namespace));

        namespace = "/test-namespace/namespace/";
        assertEquals("/test-namespace/namespace/", mapper.cleanupNamespaceName(namespace));

        namespace = "/test_namespace/namespace-test/";
        assertEquals("/test_namespace/namespace-test/", mapper.cleanupNamespaceName(namespace));

        namespace = "/test_namespace/namespace.test/";
        assertEquals("/test_namespace/namespace.test/", mapper.cleanupActionName(namespace));
    }

    public void testAllowedActionNames() {
        DefaultActionMapper mapper = new DefaultActionMapper();

        String actionName = "action";
        assertEquals(actionName, mapper.cleanupActionName(actionName));

        actionName = "${action}";
        assertEquals(mapper.defaultActionName, mapper.cleanupActionName(actionName));

        actionName = "${${%{action}}}";
        assertEquals(mapper.defaultActionName, mapper.cleanupActionName(actionName));

        actionName = "${#foo='action',#foo}";
        assertEquals(mapper.defaultActionName, mapper.cleanupActionName(actionName));

        actionName = "test-action";
        assertEquals("test-action", mapper.cleanupActionName(actionName));

        actionName = "test_action";
        assertEquals("test_action", mapper.cleanupActionName(actionName));

        actionName = "test!bar.action";
        assertEquals("test!bar.action", mapper.cleanupActionName(actionName));
    }

    public void testAllowedMethodNames() {
        DefaultActionMapper mapper = new DefaultActionMapper();

        assertEquals("", mapper.cleanupMethodName(""));
        assertEquals("test", mapper.cleanupMethodName("test"));
        assertEquals("test_method", mapper.cleanupMethodName("test_method"));
        assertEquals("_test", mapper.cleanupMethodName("_test"));
        assertEquals("test1", mapper.cleanupMethodName("test1"));

        assertEquals(mapper.defaultMethodName, mapper.cleanupMethodName("2test"));
        assertEquals(mapper.defaultMethodName, mapper.cleanupMethodName("%{exp}"));
        assertEquals(mapper.defaultMethodName, mapper.cleanupMethodName("${%{foo}}"));
        assertEquals(mapper.defaultMethodName, mapper.cleanupMethodName("${#foo='method',#foo}"));
    }

    public void testTestAllowedNamespaceName() {
        // give
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowedNamespaceNames("[a-z/]*");

        // when
        String result = mapper.cleanupNamespaceName("/ns");

        // then
        assertEquals("/ns", result);
    }

    public void testTestAllowedNamespaceNameAndFallbackToDefault() {
        // give
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowedNamespaceNames("[a-z/]*");
        mapper.setDefaultNamespaceName("/ns");

        // when
        String result = mapper.cleanupNamespaceName("/ns2");

        // then
        assertEquals("/ns", result);
    }

    public void testTestAllowedActionName() {
        // give
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowedActionNames("[a-z]*");

        // when
        String result = mapper.cleanupActionName("action");

        // then
        assertEquals("action", result);
    }

    public void testTestAllowedActionNameAndFallbackToDefault() {
        // give
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowedActionNames("[a-z]*");
        mapper.setDefaultActionName("error");

        // when
        String result = mapper.cleanupActionName("action2");

        // then
        assertEquals("error", result);
    }

    public void testTestAllowedMethodName() {
        // give
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowedMethodNames("[a-z]*");

        // when
        String result = mapper.cleanupMethodName("execute");

        // then
        assertEquals("execute", result);
    }

    public void testTestAllowedMethodNameAndFallbackToDefault() {
        // give
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowedMethodNames("[a-z]*");
        mapper.setDefaultMethodName("error");

        // when
        String result = mapper.cleanupMethodName("execute2");

        // then
        assertEquals("error", result);
    }
    
}
