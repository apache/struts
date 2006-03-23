/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 2/10/2003
 *
 */
package com.opensymphony.webwork.dispatcher;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.opensymphony.webwork.WebWorkTestCase;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.dispatcher.mapper.ActionMapping;
import com.opensymphony.webwork.dispatcher.mapper.DefaultActionMapper;

import java.util.HashMap;


/**
 * @author roughley
 */
public class DefautActionMapperTest extends WebWorkTestCase {
    private MockHttpServletRequest req;

    protected void setUp() throws Exception {
        super.setUp();
        req = new MockHttpServletRequest();
        req.setupGetParameterMap(new HashMap());
        req.setupGetContextPath("/my/namespace");
    }

    public void testGetMapping() throws Exception {
        setUp();
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req);

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
        ActionMapping mapping = mapper.getMapping(req);

        assertEquals("/my/namespace", mapping.getNamespace());
        assertEquals("actionName", mapping.getName());
        assertEquals("add", mapping.getMethod());
    }

    public void testGetUri() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName.action");
        req.setupGetServletPath("/my/namespace/actionName.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req);
        assertEquals("/my/namespace/actionName.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetUriWithMethod() throws Exception {
        req.setupGetParameterMap(new HashMap());
        req.setupGetRequestURI("/my/namespace/actionName!add.action");
        req.setupGetServletPath("/my/namespace/actionName!add.action");
        req.setupGetAttribute(null);
        req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping mapping = mapper.getMapping(req);

        assertEquals("/my/namespace/actionName!add.action", mapper.getUriFromActionMapping(mapping));
    }

    public void testGetMappingWithNoExtension() throws Exception {
        Object old = Configuration.get(WebWorkConstants.WEBWORK_ACTION_EXTENSION);
        Configuration.set(WebWorkConstants.WEBWORK_ACTION_EXTENSION, "");
        try {
            req.setupGetParameterMap(new HashMap());
            req.setupGetRequestURI("/my/namespace/actionName");
            req.setupGetServletPath("/my/namespace/actionName");
            req.setupGetAttribute(null);
            req.addExpectedGetAttributeName("javax.servlet.include.servlet_path");

            DefaultActionMapper mapper = new DefaultActionMapper();
            ActionMapping mapping = mapper.getMapping(req);

            assertEquals("/my/namespace", mapping.getNamespace());
            assertEquals("actionName", mapping.getName());
            assertNull(mapping.getMethod());
        }
        finally {
            Configuration.set(WebWorkConstants.WEBWORK_ACTION_EXTENSION, old);
        }
    }
}
