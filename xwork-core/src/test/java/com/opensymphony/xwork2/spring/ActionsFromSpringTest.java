/*
 * Created on Jun 12, 2004
 */
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Test loading actions from the Spring Application Context.
 *
 * @author Simon Stewart
 */
public class ActionsFromSpringTest extends XWorkTestCase {
    private ApplicationContext appContext;

    @Override public void setUp() throws Exception {
        super.setUp();

        // Set up XWork
        XmlConfigurationProvider provider = new XmlConfigurationProvider("com/opensymphony/xwork2/spring/actionContext-xwork.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        appContext = ((SpringObjectFactory)container.getInstance(ObjectFactory.class)).appContext;
    }

    public void testLoadSimpleAction() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "simpleAction", null);
        Object action = proxy.getAction();

        Action expected = (Action) appContext.getBean("simple-action");

        assertEquals(expected.getClass(), action.getClass());
    }

    public void testLoadActionWithDependencies() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "dependencyAction", null);
        SimpleAction action = (SimpleAction) proxy.getAction();

        assertEquals("injected", action.getBlah());
    }

    public void testProxiedActionIsNotStateful() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "proxiedAction", null);
        SimpleAction action = (SimpleAction) proxy.getAction();

        action.setBlah("Hello World");

        proxy = actionProxyFactory.createActionProxy(null, "proxiedAction", null);
        action = (SimpleAction) proxy.getAction();

        // If the action is a singleton, this test will fail
        SimpleAction sa = new SimpleAction();
        assertEquals(sa.getBlah(), action.getBlah());

        // And if the advice is not being applied, this will be SUCCESS.
        String result = action.execute();
        assertEquals(Action.INPUT, result);
    }

    public void testAutoProxiedAction() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "autoProxiedAction", null);

        SimpleAction action = (SimpleAction) proxy.getAction();

        String result = action.execute();
        assertEquals(Action.INPUT, result);
    }
    
    public void testActionWithSpringResult() throws Exception {
    	        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "simpleActionSpringResult", null);
    	                
    	        proxy.execute();
    	        
    	        SpringResult springResult = (SpringResult) proxy.getInvocation().getResult();
    	        assertTrue(springResult.isInitialize());
    	        assertNotNull(springResult.getStringParameter());
    }

    public void testProxiedActionIsNotAccessible() throws Exception {
        // given
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("exposeProxy", "true");
        params.put("blah", "S2-047");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy(null,
                "paramsAwareProxiedAction", null, extraContext);

        // when
        proxy.execute();
        Object action = proxy.getAction();

        //then
        assertEquals("S2-047", ((SimpleAction) action).getBlah());
        assertFalse("proxied action is accessible!",
                (Boolean) MethodUtils.invokeMethod(action, "isExposeProxy"));
    }
}
