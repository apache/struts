/*
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

import org.apache.struts2.StrutsInternalTestCase;
import com.mockobjects.servlet.MockHttpServletRequest;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

import java.util.HashMap;

public class Restful2ActionMapperTest extends StrutsInternalTestCase {

    private Restful2ActionMapper mapper;
    private MockHttpServletRequest req;
    private ConfigurationManager configManager;
    private Configuration config;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mapper = new Restful2ActionMapper();
        mapper.setExtensions("");
        req = new MockHttpServletRequest();
        req.setupGetParameterMap(new HashMap());
        req.setupGetContextPath("/my/namespace");

        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig.Builder("myns")
            .namespace("/my/namespace").build();
        PackageConfig pkg2 = new PackageConfig.Builder("my")
            .namespace("/my").build();
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        configManager = new ConfigurationManager() {
            public Configuration getConfiguration() {
                return config;
            }
        };
    }
    
    public void testGetIndex() throws Exception {
        req.setupGetRequestURI("/my/namespace/foo/");
        req.setupGetServletPath("/my/namespace/foo/");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("index", mapping.getMethod());
    }

    public void testGetId() throws Exception {
        mapper.setIdParameterName("id");
        req.setupGetRequestURI("/my/namespace/foo/3");
        req.setupGetServletPath("/my/namespace/foo/3");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/3", mapping.getName());
        assertEquals("view", mapping.getMethod());
        assertEquals("3", mapping.getParams().get("id"));
    }

    public void testGetEdit() throws Exception {
        mapper.setIdParameterName("id");
        mapper.setAllowDynamicMethodCalls("true");
        req.setupGetRequestURI("/my/namespace/foo/3!edit");
        req.setupGetServletPath("/my/namespace/foo/3!edit");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/3", mapping.getName());
        assertEquals("edit", mapping.getMethod());
        assertEquals("3", mapping.getParams().get("id"));
    }

    public void testGetIndexWithParams() throws Exception {
        req.setupGetRequestURI("/my/namespace/bar/1/foo/");
        req.setupGetServletPath("/my/namespace/bar/1/foo/");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("index", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testPostCreate() throws Exception {
        req.setupGetRequestURI("/my/namespace/bar/1/foo/");
        req.setupGetServletPath("/my/namespace/bar/1/foo/");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("POST");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("create", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }
 
    public void testPutUpdate() throws Exception {

        req.setupGetRequestURI("/my/namespace/bar/1/foo/2");
        req.setupGetServletPath("/my/namespace/bar/1/foo/2");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("PUT");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/2", mapping.getName());
        assertEquals("update", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }
    
    public void testPutUpdateWithIdParam() throws Exception {

        mapper.setIdParameterName("id");
        req.setupGetRequestURI("/my/namespace/bar/1/foo/2");
        req.setupGetServletPath("/my/namespace/bar/1/foo/2");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("PUT");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo", mapping.getName());
        assertEquals("update", mapping.getMethod());
        assertEquals(2, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
        assertEquals("2", mapping.getParams().get("id"));
        
    }

    public void testPutUpdateWithFakePut() throws Exception {

        req.setupGetRequestURI("/my/namespace/bar/1/foo/2");
        req.setupGetServletPath("/my/namespace/bar/1/foo/2");
        req.setupAddParameter(Restful2ActionMapper.HTTP_METHOD_PARAM, "put");
        req.setupAddParameter(Restful2ActionMapper.HTTP_METHOD_PARAM, "put");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("POST");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/2", mapping.getName());
        assertEquals("update", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testDeleteRemove() throws Exception {

        req.setupGetRequestURI("/my/namespace/bar/1/foo/2");
        req.setupGetServletPath("/my/namespace/bar/1/foo/2");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("DELETE");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/2", mapping.getName());
        assertEquals("remove", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testDeleteRemoveWithFakeDelete() throws Exception {

        req.setupGetRequestURI("/my/namespace/bar/1/foo/2");
        req.setupGetServletPath("/my/namespace/bar/1/foo/2");
        req.setupAddParameter(Restful2ActionMapper.HTTP_METHOD_PARAM, "DELETE");
        req.setupAddParameter(Restful2ActionMapper.HTTP_METHOD_PARAM, "DELETE");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("POST");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/2", mapping.getName());
        assertEquals("remove", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }
}
