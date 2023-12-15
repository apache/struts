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

import java.util.HashMap;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.url.StrutsUrlDecoder;
import org.springframework.mock.web.MockHttpServletRequest;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.inject.Container;

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
        mapper.setDecoder(new StrutsUrlDecoder());
        req = new MockHttpServletRequest();
        req.setParameters(new HashMap());
        req.setContextPath("/my/namespace");

        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig.Builder("myns")
            .namespace("/my/namespace").build();
        PackageConfig pkg2 = new PackageConfig.Builder("my")
            .namespace("/my").build();
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        configManager = new ConfigurationManager(Container.DEFAULT_NAME) {
            public Configuration getConfiguration() {
                return config;
            }
        };
    }

    public void testGetIndex() throws Exception {
        req.setRequestURI("/my/namespace/foo/");
        req.setServletPath("/my/namespace/foo/");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("index", mapping.getMethod());
    }

    public void testGetId() throws Exception {
        mapper.setIdParameterName("id");
        req.setRequestURI("/my/namespace/foo/3");
        req.setServletPath("/my/namespace/foo/3");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/3", mapping.getName());
        assertEquals("view", mapping.getMethod());
        assertEquals("3", mapping.getParams().get("id"));
    }

    public void testGetEdit() throws Exception {
        mapper.setIdParameterName("id");
        mapper.setAllowDynamicMethodCalls("true");
        req.setRequestURI("/my/namespace/foo/3!edit");
        req.setServletPath("/my/namespace/foo/3!edit");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/3", mapping.getName());
        assertEquals("edit", mapping.getMethod());
        assertEquals("3", mapping.getParams().get("id"));
    }

    public void testGetIndexWithParams() throws Exception {
        req.setRequestURI("/my/namespace/bar/1/foo/");
        req.setServletPath("/my/namespace/bar/1/foo/");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("index", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testPostCreate() throws Exception {
        req.setRequestURI("/my/namespace/bar/1/foo/");
        req.setServletPath("/my/namespace/bar/1/foo/");
        req.setMethod("POST");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("create", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testPutUpdate() throws Exception {

        req.setRequestURI("/my/namespace/bar/1/foo/2");
        req.setServletPath("/my/namespace/bar/1/foo/2");
        req.setMethod("PUT");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/2", mapping.getName());
        assertEquals("update", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testPutUpdateWithIdParam() throws Exception {

        mapper.setIdParameterName("id");
        req.setRequestURI("/my/namespace/bar/1/foo/2");
        req.setServletPath("/my/namespace/bar/1/foo/2");
        req.setMethod("PUT");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo", mapping.getName());
        assertEquals("update", mapping.getMethod());
        assertEquals(2, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
        assertEquals("2", mapping.getParams().get("id"));

    }

    public void testPutUpdateWithFakePut() throws Exception {

        req.setRequestURI("/my/namespace/bar/1/foo/2");
        req.setServletPath("/my/namespace/bar/1/foo/2");
        req.setParameter(Restful2ActionMapper.HTTP_METHOD_PARAM, "put");
        req.setParameter(Restful2ActionMapper.HTTP_METHOD_PARAM, "put");
        req.setMethod("POST");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/2", mapping.getName());
        assertEquals("update", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testDeleteRemove() throws Exception {

        req.setRequestURI("/my/namespace/bar/1/foo/2");
        req.setServletPath("/my/namespace/bar/1/foo/2");
        req.setMethod("DELETE");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/2", mapping.getName());
        assertEquals("remove", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testDeleteRemoveWithFakeDelete() throws Exception {

        req.setRequestURI("/my/namespace/bar/1/foo/2");
        req.setServletPath("/my/namespace/bar/1/foo/2");
        req.setParameter(Restful2ActionMapper.HTTP_METHOD_PARAM, "DELETE");
        req.setParameter(Restful2ActionMapper.HTTP_METHOD_PARAM, "DELETE");
        req.setMethod("POST");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/2", mapping.getName());
        assertEquals("remove", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }
}
