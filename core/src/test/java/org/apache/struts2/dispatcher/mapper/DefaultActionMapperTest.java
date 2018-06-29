/*
 * $Id$
 * $Id$
 *
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
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.StrutsResultSupport;
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
        configManager = new ConfigurationManager() {
            public Configuration getConfiguration() {
                return config;
            }
        };
    }

    public void testGetMapping() {
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

    public void testGetMappingWithMethod() {
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

    public void testGetMappingWithSlashedName() {

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

    public void testGetMappingWithSlashedNameAtRootButNoSlashPackage() {

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

    public void testGetMappingWithSlashedNameAtRoot() {
        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig.Builder("myns")
            .namespace("/my/namespace").build();
        PackageConfig pkg2 = new PackageConfig.Builder("my").namespace("/my").build();
        PackageConfig pkg3 = new PackageConfig.Builder("root").namespace("/").build();
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        config.addPackageConfig("root", pkg3);
        configManager = new ConfigurationManager() {
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



    public void testGetMappingWithNamespaceSlash() {

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

    public void testGetMappingWithUnknownNamespace() {
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

    public void testGetMappingWithUnknownNamespaceButFullNamespaceSelect() {
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

        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.METHOD_PREFIX + "someMethod", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

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

    public void testGetUri() {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithSemicolonPresent() {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.action;abc=123rty56");
        req.setupGetServletPath("/my/namespace/actionName.action;abc=123rty56");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithMethod() {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName!add.action");
        req.setupGetServletPath("/my/namespace/actionName!add.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace/actionName!add.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithOriginalExtension() {
        ActionMapping mapping = new ActionMapping("actionName", "/ns", null, new HashMap());

        ActionMapping orig = new ActionMapping();
        orig.setExtension("foo");
        ActionContext.getContext().put(ServletActionContext.ACTION_MAPPING, orig);

        DefaultActionMapper mapper = new DefaultActionMapper();
        assertEquals("/ns/actionName.foo", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetMappingWithNoExtension() {
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

    public void testGetMappingWithNoExtensionButUriHasExtension() {
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
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, configManager);

        assertEquals("someServletPath", actionMapping.getName());
    }

    public void testActionPrefixWhenEnabled() {
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

    public void testActionPrefixWhenSlashesAndCrossNamespaceDisabled() {
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

    public void testActionPrefixWhenSlashesButSlashesDisabledAndCrossNamespaceDisabled() {
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

    public void testActionPrefixWhenSlashesButSlashesDisabledAndCrossNamespace() {
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

    public void testActionPrefixWhenCrossNamespace() {
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

    public void testActionPrefix_fromImageButton() {
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

    public void testActionPrefix_fromIEImageButton() {
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

    public void testRedirectPrefix() {
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

    public void testUnsafeRedirectPrefix() {
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

    public void testRedirectActionPrefix() {
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

    public void testUnsafeRedirectActionPrefix() {
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

    public void testRedirectActionPrefixWithEmptyExtension() {
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

    public void testCustomActionPrefix() {
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

    public void testDropExtension() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        String name = mapper.dropExtension("foo.action");
        assertTrue("Name not right: "+name, "foo".equals(name));

        name = mapper.dropExtension("foo.action.action");
        assertTrue("Name not right: "+name, "foo.action".equals(name));

    }

    public void testDropExtensionWhenBlank() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("action,,");
        String name = mapper.dropExtension("foo.action");
        assertTrue("Name not right: "+name, "foo".equals(name));
        name = mapper.dropExtension("foo");
        assertTrue("Name not right: "+name, "foo".equals(name));
        assertNull(mapper.dropExtension("foo.bar"));
        assertNull(mapper.dropExtension("foo."));
    }

    public void testDropExtensionEmbeddedDot() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions("action,,");

        String name = mapper.dropExtension("/foo/bar-1.0/baz.action");
        assertTrue("Name not right: "+name, "/foo/bar-1.0/baz".equals(name));

        name = mapper.dropExtension("/foo/bar-1.0/baz");
        assertTrue("Name not right: "+name, "/foo/bar-1.0/baz".equals(name));
    }

    public void testGetUriFromActionMapper1() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper2() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action", uri);
    }

    public void testGetUriFromActionMapper3() {
        DefaultActionMapper mapper = new DefaultActionMapper();
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

    //
    public void testGetUriFromActionMapper6() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper7() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper8() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName!myMethod.action?test=bla", uri);
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
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setExtension("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("myActionName!myMethod", uri);
    }

    public void testGetUriFromActionMapperWhenBlankExtension() {
        DefaultActionMapper mapper = new DefaultActionMapper();
        mapper.setExtensions(",,");
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
        assertEquals(Arrays.asList("xml"), mapper.extensions);

        mapper.setExtensions(",");
        assertEquals(Arrays.asList(""), mapper.extensions);


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

}
