/*
 * $Id: RestfulActionMapper.java 449367 2006-09-24 06:49:04Z mrdon $
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

import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.Settings;
import com.mockobjects.servlet.MockHttpServletRequest;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;

import java.util.HashMap;

public class Restful2ActionMapperTest extends StrutsTestCase {

    private MockHttpServletRequest req;
    private ConfigurationManager configManager;
    private Configuration config;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Settings.set(StrutsConstants.STRUTS_ACTION_EXTENSION, "");
        req = new MockHttpServletRequest();
        req.setupGetParameterMap(new HashMap());
        req.setupGetContextPath("/my/namespace");

        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig("myns", "/my/namespace", false, null);
        PackageConfig pkg2 = new PackageConfig("my", "/my", false, null);
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

        Restful2ActionMapper mapper = new Restful2ActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("index", mapping.getMethod());
    }

    public void testGetIndexWithParams() throws Exception {
        req.setupGetRequestURI("/my/namespace/bar/1/foo/");
        req.setupGetServletPath("/my/namespace/bar/1/foo/");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("GET");

        Restful2ActionMapper mapper = new Restful2ActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("index", mapping.getMethod());
        assertEquals(1, mapping.getParams().size());
        assertEquals("1", mapping.getParams().get("bar"));
    }

    public void testPostCreate() throws Exception {
        req.setupGetRequestURI("/my/namespace/foo/");
        req.setupGetServletPath("/my/namespace/foo/");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");
        req.setupGetMethod("POST");

        Restful2ActionMapper mapper = new Restful2ActionMapper();
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("foo/", mapping.getName());
        assertEquals("create", mapping.getMethod());
    }
}
