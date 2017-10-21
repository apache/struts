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

import com.mockobjects.servlet.MockHttpServletRequest;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.inject.Container;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.result.StrutsResultSupport;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * DefaultActionMapper test case.
 *
 */
public class DefaultActionMapperTest extends StrutsInternalTestCase {

    private MockHttpServletRequest req;
    private ConfigurationManager configManager;
    private Configuration config;

    protected void setUp() throws Exception {
        super.setUp();
        req = new MockHttpServletRequest();
        req.setupGetParameterMap(new HashMap());
        req.setupGetContextPath("/my/namespace");

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

    public void testGetMapping() throws Exception {
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithMethod() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName!add.action");
        req.setupGetServletPath("/my/namespace/actionName!add.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertEquals("add", mapping.getMethod());
    }

    public void testGetMappingWithSlashedName() throws Exception {

        req.setupGetRequestURI("/my/foo/actionName.action");
        req.setupGetServletPath("/my/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my", mapping.getNamespace());
        assertEquals("foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithSlashedNameAtRootButNoSlashPackage() throws Exception {

        req.setupGetRequestURI("/foo/actionName.action");
        req.setupGetServletPath("/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithSlashedNameAtRoot() throws Exception {
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

        req.setupGetRequestURI("/foo/actionName.action");
        req.setupGetServletPath("/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/", mapping.getNamespace());
        assertEquals("foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }



    public void testGetMappingWithNamespaceSlash() throws Exception {

        req.setupGetRequestURI("/my-hh/abc.action");
        req.setupGetServletPath("/my-hh/abc.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("abc", mapping.getName());

        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        mapper = new DefaultActionMapper();
        mapper.setSlashesInActionNames("true");
        mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("my-hh/abc", mapping.getName());
    }

    public void testGetMappingWithUnknownNamespace() throws Exception {
        req.setupGetRequestURI("/bo/foo/actionName.action");
        req.setupGetServletPath("/bo/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithUnknownNamespaceButFullNamespaceSelect() throws Exception {
        req.setupGetRequestURI("/bo/foo/actionName.action");
        req.setupGetServletPath("/bo/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAlwaysSelectFullNamespace("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/bo/foo", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithActionName_methodAndName() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping mapping = mapper.getMappingFromActionName("actionName!add");
        assertEquals("actionName", mapping.getName());
        assertEquals("add", mapping.getMethod());
    }

    public void testGetMappingWithActionName_name() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMappingFromActionName("actionName");
        assertEquals("actionName", mapping.getName());
        assertEquals(null, mapping.getMethod());
    }

    public void testGetMappingWithActionName_noDynamicMethod() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setAllowDynamicMethodCalls("false");
        ActionMapping mapping = mapper.getMappingFromActionName("actionName!add");
        assertEquals("actionName!add", mapping.getName());
        assertEquals(null, mapping.getMethod());
    }

    public void testGetMappingWithActionName_noDynamicMethodColonPrefix() throws Exception {

        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.METHOD_PREFIX + "someMethod", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowDynamicMethodCalls("false");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("someServletPath", actionMapping.getName());
        assertEquals(null, actionMapping.getMethod());
    }

    public void testGetMappingWithActionName_null() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMappingFromActionName(null);
        assertNull(mapping);
    }

    public void testGetUri() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithSemicolonPresent() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.action;abc=123rty56");
        req.setupGetServletPath("/my/namespace/actionName.action;abc=123rty56");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithMethod() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName!add.action");
        req.setupGetServletPath("/my/namespace/actionName!add.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace/actionName!add.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithOriginalExtension() throws Exception {
        ActionMapping mapping = new ActionMapping("actionName", "/ns", null, new HashMap());

        ActionMapping orig = new ActionMapping();
        orig.setExtension("foo");
        ActionContext.getContext().put(ServletActionContext.ACTION_MAPPING, orig);

        DefaultActionMapper mapper = new DefaultActionMapper();
        assertEquals("/ns/actionName.foo", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetMappingWithNoExtension() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName");
        req.setupGetServletPath("/my/namespace/actionName");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetMappingWithNoExtensionButUriHasExtension() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.html");
        req.setupGetServletPath("/my/namespace/actionName.html");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

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

    public void testParseNameAndNamespace1() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }

    public void testParseNameAndNamespace2() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/");
    }

    public void testParseNameAndNamespace3() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/my/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/my");
    }

    public void testParseNameAndNamespace_NoSlashes() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setSlashesInActionNames("false");
        defaultActionMapper.parseNameAndNamespace("/foo/someAction", actionMapping, configManager);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }

    public void testParseNameAndNamespace_AllowSlashes() throws Exception {
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

    public void testActionPrefixWhenDisabled() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("someServletPath", actionMapping.getName());
    }

    public void testActionPrefixWhenEnabled() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("myAction", actionMapping.getName());
    }

    public void testActionPrefixWhenSlashesAndCrossNamespaceDisabled() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "my/Action", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        defaultActionMapper.setSlashesInActionNames("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("my/Action", actionMapping.getName());
    }

    public void testActionPrefixWhenSlashesButSlashesDisabledAndCrossNamespaceDisabled() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "my/Action", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        defaultActionMapper.setSlashesInActionNames("false");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("Action", actionMapping.getName());
    }

    public void testActionPrefixWhenSlashesButSlashesDisabledAndCrossNamespace() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "my/Action", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        defaultActionMapper.setAllowActionCrossNamespaceAccess("true");
        defaultActionMapper.setSlashesInActionNames("false");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("my/Action", actionMapping.getName());
    }

    public void testActionPrefixWhenCrossNamespace() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "/my/Action", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        defaultActionMapper.setAllowActionCrossNamespaceAccess("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("/my/Action", actionMapping.getName());
    }

    public void testActionPrefix_fromImageButton() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.x", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.y", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("myAction", actionMapping.getName());
    }

    public void testActionPrefix_fromIEImageButton() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.x", "");
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction.y", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setAllowActionPrefix("true");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("myAction", actionMapping.getName());
    }

    public void testRedirectPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put("redirect:" + "http://www.google.com", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        Result result = actionMapping.getResult();
        assertNull(result);
    }

    public void testUnsafeRedirectPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put("redirect:" + "http://%{3*4}", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        Result result = actionMapping.getResult();
        assertNull(result);
    }

    public void testRedirectActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put("redirectAction:" + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);


        StrutsResultSupport result = (StrutsResultSupport) actionMapping.getResult();
        assertNull(result);
    }

    public void testUnsafeRedirectActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put("redirectAction:" + "%{3*4}", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);


        StrutsResultSupport result = (StrutsResultSupport) actionMapping.getResult();
        assertNull(result);
    }

    public void testRedirectActionPrefixWithEmptyExtension() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put("redirectAction:" + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.setContainer(container);
        defaultActionMapper.setExtensions(",,");
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);


        StrutsResultSupport result = (StrutsResultSupport) actionMapping.getResult();
        assertNull(result);
    }

    public void testCustomActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put("foo:myAction", "");

        final StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.addParameterAction("foo", new ParameterAction() {
            public void execute(String key, ActionMapping mapping) {
                mapping.setName("myAction");
            }
        });
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals(actionMapping.getName(), "myAction");
    }

    public void testDropExtension() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        String name = mapper.dropExtension("foo.action", new ActionMapping());
        assertTrue("Name not right: "+name, "foo".equals(name));

        name = mapper.dropExtension("foo.action.action", new ActionMapping());
        assertTrue("Name not right: "+name, "foo.action".equals(name));

    }

    public void testDropExtensionWhenBlank() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("action,,");
        String name = mapper.dropExtension("foo.action", new ActionMapping());
        assertTrue("Name not right: "+name, "foo".equals(name));
        name = mapper.dropExtension("foo", new ActionMapping());
        assertTrue("Name not right: "+name, "foo".equals(name));
        assertNull(mapper.dropExtension("foo.bar", new ActionMapping()));
        assertNull(mapper.dropExtension("foo.", new ActionMapping()));
    }

    public void testDropExtensionEmbeddedDot() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("action,,");

        String name = mapper.dropExtension("/foo/bar-1.0/baz.action", new ActionMapping());
        assertTrue("Name not right: "+name, "/foo/bar-1.0/baz".equals(name));

        name = mapper.dropExtension("/foo/bar-1.0/baz", new ActionMapping());
        assertTrue("Name not right: "+name, "/foo/bar-1.0/baz".equals(name));
    }

    public void testGetUriFromActionMapper1() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper2() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper3() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action", uri);
    }


    public void testGetUriFromActionMapper4() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper5() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    //
    public void testGetUriFromActionMapper6() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper7() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper8() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }


    public void testGetUriFromActionMapper9() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper10() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper11() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName.action");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper12() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName.action");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper_justActionAndMethod() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setExtension("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("myActionName!myMethod", uri);
    }

    public void testGetUriFromActionMapperWhenBlankExtension() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions(",,");
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod", uri);
    }

    public void testSetExtension() throws Exception {
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
        assertEquals(Arrays.asList("xml"), mapper.extensions);

        mapper.setExtensions(",");
        assertEquals(Arrays.asList(""), mapper.extensions);


    }

    public void testAllowedActionNames() throws Exception {
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

    public void testAllowedMethodNames() throws Exception {
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

}
