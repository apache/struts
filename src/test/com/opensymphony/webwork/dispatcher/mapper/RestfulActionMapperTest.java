/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.dispatcher.mapper;

import junit.framework.TestCase;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

import com.opensymphony.webwork.views.jsp.WebWorkMockHttpServletRequest;

/**
 * Unit test for {@link RestfulActionMapper}.
 *
 * @author Claus Ibsen
 */
public class RestfulActionMapperTest extends TestCase {

    private RestfulActionMapper mapper;

    public void testGetUri() {
        ActionMapping am = new ActionMapping();
        am.setName("view");
        am.setNamespace("secure");
        am.setParams(Collections.EMPTY_MAP);

        assertEquals("secureview", mapper.getUriFromActionMapping(am));
    }

    public void testGetUriParam() {
        Map param = new HashMap();
        param.put("article", "123");
        ActionMapping am = new ActionMapping();
        am.setName("view");
        am.setNamespace("secure");
        am.setParams(param);

        assertEquals("secureview", mapper.getUriFromActionMapping(am));
    }

    public void testGetUriParamId() {
        Map param = new HashMap();
        param.put("article", "123");
        param.put("viewId", "456");
        ActionMapping am = new ActionMapping();
        am.setName("view");
        am.setNamespace("secure");
        am.setParams(param);

        assertEquals("secureview/456", mapper.getUriFromActionMapping(am));
    }

    public void testGetMappingNoSlash() throws Exception {
        WebWorkMockHttpServletRequest request = new WebWorkMockHttpServletRequest();
        request.setupGetServletPath("noslash");

        assertNull(mapper.getMapping(request));
    }

    public void testGetMapping() throws Exception {
        WebWorkMockHttpServletRequest request = new WebWorkMockHttpServletRequest();
        request.setupGetServletPath("/myapp/view/12");

        ActionMapping am = mapper.getMapping(request);
        assertEquals("myapp", am.getName());
        assertEquals(1, am.getParams().size());
        assertEquals("12", am.getParams().get("view"));
    }

    public void testGetMapping2() throws Exception {
        WebWorkMockHttpServletRequest request = new WebWorkMockHttpServletRequest();
        request.setupGetServletPath("/myapp/12/region/europe");

        ActionMapping am = mapper.getMapping(request);
        assertEquals("myapp", am.getName());
        assertEquals(2, am.getParams().size());
        assertEquals("12", am.getParams().get("myappId"));
        assertEquals("europe", am.getParams().get("region"));
    }

    public void testGetMapping3() throws Exception {
        WebWorkMockHttpServletRequest request = new WebWorkMockHttpServletRequest();
        request.setupGetServletPath("/myapp/view/12/region/europe");

        ActionMapping am = mapper.getMapping(request);
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
