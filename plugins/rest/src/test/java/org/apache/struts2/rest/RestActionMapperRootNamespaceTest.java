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
package org.apache.struts2.rest;

import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * WW-5688: an action declared in the root namespace has to stay reachable whether or not the
 * request URI carries an id, so that {@code index} and {@code show} resolve to the same action.
 *
 * <p>These assertions go all the way to the {@code ActionConfig} rather than stopping at the
 * mapping, because the reported symptom is a 404 - the mapper handing back a namespace that
 * the configuration cannot resolve.</p>
 */
public class RestActionMapperRootNamespaceTest extends XWorkTestCase {

    private RestActionMapper mapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new StrutsXmlConfigurationProvider("ww-5688.xml"));
        mapper = new RestActionMapper();
    }

    private ActionMapping map(String servletPath, String httpMethod) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/myapp");
        request.setMethod(httpMethod);
        request.setRequestURI("/myapp" + servletPath);
        request.setServletPath(servletPath);
        return mapper.getMapping(request, configurationManager);
    }

    private void assertResolves(String servletPath, String httpMethod, String expectedMethod) {
        ActionMapping mapping = map(servletPath, httpMethod);
        assertNotNull(httpMethod + " " + servletPath + " produced no mapping", mapping);
        assertEquals("dog", mapping.getName());
        assertEquals(expectedMethod, mapping.getMethod());
        assertNotNull(httpMethod + " " + servletPath + " must resolve to the action declared in the root namespace,"
                        + " but namespace '" + mapping.getNamespace() + "' does not hold it",
                configurationManager.getConfiguration().getRuntimeConfiguration()
                        .getActionConfig(mapping.getNamespace(), mapping.getName()));
    }

    public void testIndexResolvesInRootNamespace() {
        assertResolves("/dog", "GET", "index");
    }

    public void testShowResolvesInRootNamespace() {
        assertResolves("/dog/1", "GET", "show");
    }

    public void testUpdateResolvesInRootNamespace() {
        assertResolves("/dog/1", "PUT", "update");
    }

    public void testDestroyResolvesInRootNamespace() {
        assertResolves("/dog/1", "DELETE", "destroy");
    }

    public void testIdIsStillExtracted() {
        ActionMapping mapping = map("/dog/1", "GET");
        assertEquals("1", ((String[]) mapping.getParams().get("id"))[0]);
    }
}
