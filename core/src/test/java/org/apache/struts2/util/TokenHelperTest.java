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
package org.apache.struts2.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.dispatcher.HttpParameters;


/**
 * TokenHelperTest
 *
 */
public class TokenHelperTest extends TestCase {

    private Map session;

	public void testTokenSessionNameBuilding() throws Exception {
		String name = "foo";
		String sessionName = TokenHelper.buildTokenSessionAttributeName(name);
		assertEquals(TokenHelper.TOKEN_NAMESPACE + "." + name, sessionName);
	}

    public void testSetToken() {
        String token = TokenHelper.setToken();
		final String defaultSessionTokenName = TokenHelper.buildTokenSessionAttributeName(TokenHelper.DEFAULT_TOKEN_NAME);
		assertEquals(token, session.get(defaultSessionTokenName));
    }

    public void testSetTokenWithName() {
        String tokenName = "myTestToken";
        String token = TokenHelper.setToken(tokenName);
		final String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(tokenName);
		assertEquals(token, session.get(sessionTokenName));
    }

	public void testSetSessionToken() {
		String tokenName = "myOtherTestToken";
		String token = "foobar";
		TokenHelper.setSessionToken(tokenName, token);
		final String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(tokenName);
		assertEquals(token, session.get(sessionTokenName));
	}

	public void testValidToken() {
        String tokenName = "validTokenTest";
        String token = TokenHelper.setToken(tokenName);
		final String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(tokenName);
		assertEquals(token, session.get(sessionTokenName));

        Map<String, String[]> params = new HashMap<>();
        params.put(TokenHelper.TOKEN_NAME_FIELD, new String[]{tokenName});
        params.put(tokenName, new String[]{token});

        ActionContext.getContext().setParameters(HttpParameters.create(params).build());

        assertTrue(TokenHelper.validToken());
    }

    public void testGetTokenDoesNotNpe() {
        String token = TokenHelper.getToken(null);
        assertTrue(token == null);

        String token2 = TokenHelper.getToken("");
        assertTrue(token2 == null);
    }

    protected void setUp() throws Exception {
        session = new HashMap();
        Map ctxMap = new TreeMap();
        ctxMap.put(ActionContext.SESSION, session);
        ctxMap.put(ActionContext.PARAMETERS, HttpParameters.create().build());
        ActionContext ctx = new ActionContext(ctxMap);
        ActionContext.setContext(ctx);
    }

    protected void tearDown() {
        ActionContext.setContext(null);
    }
}

