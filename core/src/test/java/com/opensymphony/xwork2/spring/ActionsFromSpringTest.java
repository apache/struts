/*
 * Created on Jun 12, 2004
 */
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.springframework.context.ApplicationContext;

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
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "simpleAction", null, null);
        Object action = proxy.getAction();

        Action expected = (Action) appContext.getBean("simple-action");

        assertEquals(expected.getClass(), action.getClass());
    }

    public void testLoadActionWithDependencies() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "dependencyAction", null, null);
        SimpleAction action = (SimpleAction) proxy.getAction();

        assertEquals("injected", action.getBlah());
    }

    public void testProxiedActionIsNotStateful() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "proxiedAction", null, null);
        SimpleAction action = (SimpleAction) proxy.getAction();

        action.setBlah("Hello World");

        proxy = actionProxyFactory.createActionProxy(null, "proxiedAction", null, null);
        action = (SimpleAction) proxy.getAction();

        // If the action is a singleton, this test will fail
        SimpleAction sa = new SimpleAction();
        assertEquals(sa.getBlah(), action.getBlah());

        // And if the advice is not being applied, this will be SUCCESS.
        String result = action.execute();
        assertEquals(Action.INPUT, result);
    }

    public void testAutoProxiedAction() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "autoProxiedAction", null, null);

        SimpleAction action = (SimpleAction) proxy.getAction();

        String result = action.execute();
        assertEquals(Action.INPUT, result);
    }
    
    public void testActionWithSpringResult() throws Exception {
    	        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "simpleActionSpringResult", null, null);
    	                
    	        proxy.execute();
    	        
    	        SpringResult springResult = (SpringResult) proxy.getInvocation().getResult();
    	        assertTrue(springResult.isInitialize());
    	        assertNotNull(springResult.getStringParameter());
    }
}
