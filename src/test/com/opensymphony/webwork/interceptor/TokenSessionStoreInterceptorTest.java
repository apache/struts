/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.interceptor;

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.Action;
import com.opensymphony.webwork.TestConfigurationProvider;

/**
 * Unit test for {@link TokenSessionStoreInterceptor}.
 *
 * @author Claus Ibsen
 */
public class TokenSessionStoreInterceptorTest extends TokenInterceptorTest {

    public void testCAllExecute2Times() throws Exception {
        ActionProxy proxy = buildProxy(getActionName());
        setToken(request);
        assertEquals(Action.SUCCESS, proxy.execute());

        ActionProxy proxy2 = buildProxy(getActionName());
        // must not call setToken
        // double post will just return success and render the same view as the first execute
        // see TokenInterceptor where a 2nd call will return invalid.token code instead
        assertEquals(Action.SUCCESS, proxy2.execute());
    }

    protected String getActionName() {
        return TestConfigurationProvider.TOKEN_SESSION_ACTION_NAME;
    }

}
