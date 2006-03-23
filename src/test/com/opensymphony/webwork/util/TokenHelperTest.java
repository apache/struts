/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.opensymphony.xwork.ActionContext;


/**
 * TokenHelperTest
 *
 * @author Jason Carreira
 *         Created Apr 3, 2003 10:13:08 AM
 */
public class TokenHelperTest extends TestCase {

    private Map session;


    public void testSetToken() {
        String token = TokenHelper.setToken();
        assertEquals(token, session.get(TokenHelper.DEFAULT_TOKEN_NAME));
    }

    public void testSetTokenWithName() {
        String tokenName = "myTestToken";
        String token = TokenHelper.setToken(tokenName);
        assertEquals(token, session.get(tokenName));
    }

    public void testValidToken() {
        String tokenName = "validTokenTest";
        Map params = new HashMap();

        String token = TokenHelper.setToken(tokenName);
        assertEquals(token, session.get(tokenName));
        ActionContext.getContext().getParameters().put(TokenHelper.TOKEN_NAME_FIELD, new String[]{tokenName});
        ActionContext.getContext().getParameters().put(tokenName, new String[]{token});
        assertTrue(TokenHelper.validToken());
    }

    protected void setUp() throws Exception {
        session = new HashMap();
        Map params = new HashMap();
        Map ctxMap = new HashMap();
        ctxMap.put(ActionContext.SESSION, session);
        ctxMap.put(ActionContext.PARAMETERS, params);
        ActionContext ctx = new ActionContext(ctxMap);
        ActionContext.setContext(ctx);
    }

    protected void tearDown() {
        ActionContext.setContext(null);
    }
}

