package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Contribed by: Ruben Inoto
 */
public class ProxyInvocationTest extends XWorkTestCase {

    /**
     * Sets a ProxyObjectFactory as ObjectFactory (so the FooAction will always be retrieved
     * as a FooProxy), and it tries to call invokeAction on the TestActionInvocation.
     * 
     * It should fail, because the Method got from the action (actually a FooProxy) 
     * will be executed on the InvocationHandler of the action (so, in the action itself). 
     */
    public void testProxyInvocation() throws Exception {

        ActionProxy proxy = actionProxyFactory
            .createActionProxy("", "ProxyInvocation", createDummyContext());
        ActionInvocation invocation = proxy.getInvocation();
        
        String result = invocation.invokeActionOnly();
        assertEquals("proxyResult", result);

    }

    /** 
     * Needed for the creation of the action proxy
     */
    private Map<String, Object> createDummyContext() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("blah", "this is blah");
        Map<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);
        return extraContext;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-proxyinvoke.xml"));
    }
}
