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

package org.apache.struts2.rest;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import junit.framework.TestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.springframework.mock.web.MockHttpServletRequest;

public class RestActionMapperTest extends TestCase {

    private RestActionMapper mapper;
    private ConfigurationManager configManager;
    private Configuration config;
    private MockHttpServletRequest req;
    private String allowDynamicMethodInvocation = "true";

    protected void setUp() throws Exception {
        super.setUp();
        req = new MockHttpServletRequest();
        req.setContextPath("/myapp");
        req.setMethod("GET");

        mapper = new RestActionMapper();

        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig.Builder("myns").namespace("/animals").build();
        PackageConfig pkg2 = new PackageConfig.Builder("my").namespace("/my").build();
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        configManager = new ConfigurationManager() {
            public Configuration getConfiguration() {
                return config;
            }
        };
    }

    public void testRootMapping() throws Exception {
        req.setRequestURI("/myapp/");
        req.setServletPath("/");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertNotNull(mapping);
    }

    public void testGetMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog");
        req.setServletPath("/animals/dog");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("index", mapping.getMethod());
    }

    public void testPostMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog");
        req.setServletPath("/animals/dog");
        req.setMethod("POST");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("create", mapping.getMethod());
    }

    public void testDeleteMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("DELETE");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("destroy", mapping.getMethod());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
    }

    public void testPutMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("PUT");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("update", mapping.getMethod());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
    }

    public void testGetIdMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("show", mapping.getMethod());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
    }

    public void testNewMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/new");
        req.setServletPath("/animals/dog/new");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("editNew", mapping.getMethod());
    }

    public void testEditMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido/edit");
        req.setServletPath("/animals/dog/fido/edit");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
        assertEquals("edit", mapping.getMethod());
    }

    public void testEditSemicolonMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido;edit");
        req.setServletPath("/animals/dog/fido;edit");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
        assertEquals("edit", mapping.getMethod());
    }

    public void testGetJsessionIdSemicolonMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido;jsessionid=29fefpv23do1g");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
        assertEquals("show", mapping.getMethod());
    }

    public void testGetJsessionIdSemicolonMappingWithMethod() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido!update;jsessionid=29fefpv23do1g");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
        assertEquals("show", mapping.getMethod());
    }

    public void testGetJsessionIdSemicolonMappingWithMethodAllowDMI() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido!update;jsessionid=29fefpv23do1g");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("GET");

        // allow DMI
        mapper.setAllowDynamicMethodCalls("true");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
        assertEquals("update", mapping.getMethod());
    }

    public void testMappingWithMethodAndId() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido/test/some-id!create;jsessionid=29fefpv23do1g");
        req.setServletPath("/animals/dog/fido/test/some-id");
        req.setMethod("GET");
        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog/fido/test", mapping.getName());
        assertEquals("some-id", ((String[]) mapping.getParams().get("id"))[0]);
        assertEquals("create", mapping.getMethod());
    }

    public void testMappingForStaticFiles() throws Exception {
        req.setRequestURI("/myApp/custom/menu/Yosemite/Vernal_Fall/Vernal_Fall_Image!iframe");
        req.setServletPath("/custom/menu/Yosemite/Vernal_Fall/Vernal_Fall_Image");
        req.setMethod("GET");
        mapper.setAllowDynamicMethodCalls("true");
        final ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("custom/menu/Yosemite/Vernal_Fall", mapping.getName());
        assertEquals("Vernal_Fall_Image", ((String[]) mapping.getParams().get("id"))[0]);
        assertEquals("iframe", mapping.getMethod());
    }

    public void testMappingForStaticFilesWithJsessionId() throws Exception {
        req.setRequestURI("/myApp/custom/menu/Yosemite/Vernal_Fall/Vernal_Fall_Image!iframe;jsessionid=29fefpv23do1g");
        req.setServletPath("/custom/menu/Yosemite/Vernal_Fall/Vernal_Fall_Image");
        req.setMethod("GET");
        mapper.setAllowDynamicMethodCalls("true");
        final ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("", mapping.getNamespace());
        assertEquals("custom/menu/Yosemite/Vernal_Fall", mapping.getName());
        assertEquals("Vernal_Fall_Image", ((String[]) mapping.getParams().get("id"))[0]);
        assertEquals("iframe", mapping.getMethod());
    }

    public void testParseNameAndNamespace() {
        tryUri("/foo/23", "", "foo/23");
        tryUri("/foo/", "", "foo/");
        tryUri("foo", "", "foo");
        tryUri("/", "/", "");
    }

    public void testParseNameAndNamespaceWithNamespaces() {
        tryUri("/my/foo/23", "/my", "foo/23");
        tryUri("/my/foo/", "/my", "foo/");
    }

    public void testParseNameAndNamespaceWithEdit() {
        tryUri("/my/foo/23;edit", "/my", "foo/23;edit");
    }

    public void testShouldAllowExclamation() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido!edit");
        req.setServletPath("/animals/dog/fido!edit");
        req.setMethod("GET");

        mapper.setAllowDynamicMethodCalls("true");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
        assertEquals("edit", mapping.getMethod());
    }

    public void testShouldBlockDynamicMethodInvocationAnsUseShow() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido!edit");
        req.setServletPath("/animals/dog/fido!edit");
        req.setMethod("GET");

        mapper.setAllowDynamicMethodCalls("false");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
        assertEquals("show", mapping.getMethod());
    }

    public void testShouldBlockDynamicMethodInvocationAnsUseDestroy() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido!destroy");
        req.setServletPath("/animals/dog/fido!destroy");
        req.setMethod("DELETE");

        mapper.setAllowDynamicMethodCalls("false");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
        assertEquals("destroy", mapping.getMethod());
    }

    public void testShouldBlockDynamicMethodInvocationAndUseUpdate() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido!update");
        req.setServletPath("/animals/dog/fido!update");
        req.setMethod("PUT");

        mapper.setAllowDynamicMethodCalls("false");
        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
        assertEquals("update", mapping.getMethod());
    }
    
    private void tryUri(String uri, String expectedNamespace, String expectedName) {
        ActionMapping mapping = new ActionMapping();
        mapper.setAllowDynamicMethodCalls(allowDynamicMethodInvocation);
        mapper.parseNameAndNamespace(uri, mapping, configManager);
        assertEquals(expectedName, mapping.getName());
        assertEquals(expectedNamespace, mapping.getNamespace());
    }

    public void testOptionsMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog");
        req.setServletPath("/animals/dog");
        req.setMethod("OPTIONS");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("options", mapping.getMethod());
    }

    public void testPostContinueMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog");
        req.setServletPath("/animals/dog");
        req.setMethod("POST");
        req.addHeader("Expect", "100-continue");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("createContinue", mapping.getMethod());
    }

    public void testPutContinueMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("PUT");
        req.addHeader("Expect", "100-continue");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("updateContinue", mapping.getMethod());
        assertEquals("fido", ((String[]) mapping.getParams().get("id"))[0]);
    }

    public void testDynamicMethodInvocation() throws Exception {
        // given
        req.setRemoteAddr("/myapp/animals/dog/23!edit");
        req.setServletPath("/animals/dog/23!edit");
        req.setMethod("GET");

        mapper.setAllowDynamicMethodCalls("true");

        // when
        ActionMapping actionMapping = mapper.getMapping(req, configManager);

        // then
        assertEquals("dog", actionMapping.getName());
        assertEquals("edit", actionMapping.getMethod());
        assertEquals("/animals", actionMapping.getNamespace());
        assertEquals("23", ((String[]) actionMapping.getParams().get("id"))[0]);
    }

}
