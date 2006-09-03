/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.dispatcher.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

/**
 * DefaultActionMapper test case.
 *
 */
public class DefaultActionMapperTest extends StrutsTestCase {

    private MockHttpServletRequest req;
    private Configuration config;

    protected void setUp() throws Exception {
        super.setUp();
        req = new MockHttpServletRequest();
        req.setupGetParameterMap(new HashMap());
        req.setupGetContextPath("/my/namespace");
        
        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig("myns", "/my/namespace", false, null);
        PackageConfig pkg2 = new PackageConfig("my", "/my", false, null);
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
    }

    public void testGetMapping() throws Exception {
        setUp();
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, config);

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
        ActionMapping mapping = mapper.getMapping(req, config);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName!add", mapping.getName());
    }
    
    public void testGetMappingWithSlashedName() throws Exception {
        setUp();
        req.setupGetRequestURI("/my/foo/actionName.action");
        req.setupGetServletPath("/my/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, config);

        assertEquals("/my", mapping.getNamespace());
        assertEquals("foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }
    
    public void testGetMappingWithUnknownNamespace() throws Exception {
        setUp();
        req.setupGetRequestURI("/bo/foo/actionName.action");
        req.setupGetServletPath("/bo/foo/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, config);

        assertEquals("", mapping.getNamespace());
        assertEquals("bo/foo/actionName", mapping.getName());
        assertNull(mapping.getMethod());
    }

    public void testGetUri() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, config);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithMethod() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName!add.action");
        req.setupGetServletPath("/my/namespace/actionName!add.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req, config);

        assertEquals("/my/namespace/actionName!add.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetMappingWithNoExtension() throws Exception {
        String old = org.apache.struts2.config.Settings.get(StrutsConstants.STRUTS_ACTION_EXTENSION);
        org.apache.struts2.config.Settings.set(StrutsConstants.STRUTS_ACTION_EXTENSION, "");
        try {
            req.setupGetParameterMap(new HashMap());
            req.setupGetRequestURI("/my/namespace/actionName");
            req.setupGetServletPath("/my/namespace/actionName");
            req.setupGetAttribute(null);
            req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

            DefaultActionMapper mapper = new DefaultActionMapper();
            ActionMapping mapping = mapper.getMapping(req, config);

            assertEquals("/my/namespace", mapping.getNamespace());
            assertEquals("actionName", mapping.getName());
            assertNull(mapping.getMethod());
        }
        finally {
            org.apache.struts2.config.Settings.set(StrutsConstants.STRUTS_ACTION_EXTENSION, old);
        }
    }
    
    // =============================
    // === test name & namespace ===
    // =============================

    public void testParseNameAndNamespace1() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("someAction.action", actionMapping, config);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }

    public void testParseNameAndNamespace2() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/someAction.action", actionMapping, config);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/");
    }

    public void testParseNameAndNamespace3() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/my/someAction.action", actionMapping, config);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/my");
    }


    // ===========================
    // === test special prefix ===
    // ===========================

    public void testActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, config);

        assertEquals(actionMapping.getName(), "myAction");
    }

    public void testRedirectPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.REDIRECT_PREFIX + "www.google.com", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, config);

        Result result = actionMapping.getResult();
        assertNotNull(result);
        assertTrue(result instanceof ServletRedirectResult);

        //TODO: need to test location but there's noaccess to the property/method, unless we use reflection
    }

    public void testRedirectActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.REDIRECT_ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request, config);

        Result result = actionMapping.getResult();
        assertNotNull(result);
        assertTrue(result instanceof ServletRedirectResult);

        // TODO: need to test location but there's noaccess to the property/method, unless we use reflection
    }
    
    public void testDropExtension() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        String name = mapper.dropExtension("foo.action");
        assertTrue("Name not right: "+name, "foo".equals(name));
        
        name = mapper.dropExtension("foo.action.action");
        assertTrue("Name not right: "+name, "foo.action".equals(name));
        
    }

    public void testGetUriFromActionMapper1() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper2() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper3() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
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

        assertEquals("/myNamespace/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper7() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper8() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
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

        assertEquals("/myActionName.action.action", uri);
    }
    
    public void testGetUriFromActionMapper12() throws Exception {
        String old = org.apache.struts2.config.Settings.get(StrutsConstants.STRUTS_COMPATIBILITY_MODE);
        org.apache.struts2.config.Settings.set(StrutsConstants.STRUTS_COMPATIBILITY_MODE, "true");
        try {
            DefaultActionMapper mapper = new DefaultActionMapper();
            ActionMapping actionMapping = new ActionMapping();
            actionMapping.setName("myActionName.action");
            actionMapping.setNamespace("/");
            String uri = mapper.getUriFromActionMapping(actionMapping);

            assertEquals("/myActionName.action", uri);
        }
        finally {
            org.apache.struts2.config.Settings.set(StrutsConstants.STRUTS_COMPATIBILITY_MODE, old);
        }
    }

}
