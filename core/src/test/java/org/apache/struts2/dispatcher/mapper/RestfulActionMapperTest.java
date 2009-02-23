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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;

/**
 * Unit test for {@link RestfulActionMapper}.
 *
 */
public class RestfulActionMapperTest extends TestCase {

    private RestfulActionMapper mapper;

    public void testGetUri() {
        ActionMapping am = new ActionMapping();
        am.setName("view");
        am.setNamespace("secure");
        am.setParams(Collections.<String, Object>emptyMap());

        assertEquals("secureview", mapper.getUriFromActionMapping(am));
    }

    public void testGetUriParam() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("article", "123");
        ActionMapping am = new ActionMapping();
        am.setName("view");
        am.setNamespace("secure");
        am.setParams(param);

        assertEquals("secureview", mapper.getUriFromActionMapping(am));
    }

    public void testGetUriParamId() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("article", "123");
        param.put("viewId", "456");
        ActionMapping am = new ActionMapping();
        am.setName("view");
        am.setNamespace("secure");
        am.setParams(param);

        assertEquals("secureview/456", mapper.getUriFromActionMapping(am));
    }

    public void testGetMappingNoSlash() throws Exception {
        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("noslash");

        assertNull(mapper.getMapping(request, null));
    }

    public void testGetMapping() throws Exception {
        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/myapp/view/12");

        ActionMapping am = mapper.getMapping(request, null);
        assertEquals("myapp", am.getName());
        assertEquals(1, am.getParams().size());
        assertEquals("12", am.getParams().get("view"));
    }

    public void testGetMapping2() throws Exception {
        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/myapp/12/region/europe");

        ActionMapping am = mapper.getMapping(request, null);
        assertEquals("myapp", am.getName());
        assertEquals(2, am.getParams().size());
        assertEquals("12", am.getParams().get("myappId"));
        assertEquals("europe", am.getParams().get("region"));
    }

    public void testGetMapping3() throws Exception {
        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/myapp/view/12/region/europe");

        ActionMapping am = mapper.getMapping(request, null);
        assertEquals("myapp", am.getName());
        assertEquals(2, am.getParams().size());
        assertEquals("12", am.getParams().get("view"));
        assertEquals("europe", am.getParams().get("region"));
    }

    protected void setUp() throws Exception {
        mapper = new RestfulActionMapper();
    }

    protected void tearDown() throws Exception {
        mapper = null;
    }

}
