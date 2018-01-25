package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.SomeUnknownHandler;

import java.util.ArrayList;

/**
 * Partial test to the DefaultUnknownHandlerManager to understand the relationship between Manager and Handlers.
 *
 * @author Ziyad Alsaeed
 */
public class DefaultUnknownHandlerManagerTest extends XWorkTestCase {

    ActionConfig.Builder actionConfigBuilder = new ActionConfig.Builder( "com", "someAction", "someClass");
    ActionConfig actionConfig = actionConfigBuilder.build();
    SomeUnknownHandler someUnknownHandler = new SomeUnknownHandler();

    /**
     * Relationshsip when UnknownAction method is called.
     *
     * @author Ziyad Alsaeed
     */
    public void testHandleUnknownAction() {

        DefaultUnknownHandlerManager defaultUnknownHandlerManager = new DefaultUnknownHandlerManager();
        defaultUnknownHandlerManager.unknownHandlers = new ArrayList<>();
        defaultUnknownHandlerManager.unknownHandlers.add(someUnknownHandler);

        ActionConfig newActionConfig = defaultUnknownHandlerManager.handleUnknownAction("arbitraryNameSpace", "arbitraryActionName");

        assertEquals(newActionConfig, actionConfig);
    }

    /**
     * Relationship when UnknownActionMethod method called.
     *
     * @author Ziyad Alsaeed
     */
    public void testHandelUnknownActionMethod() throws Exception {
        DefaultUnknownHandlerManager defaultUnknownHandlerManager = new DefaultUnknownHandlerManager();
        defaultUnknownHandlerManager.unknownHandlers = new ArrayList<>();
        defaultUnknownHandlerManager.unknownHandlers.add(someUnknownHandler);

        String result = null;

        for (int i = 0; i < 10 ; i++) {
            result = (String) defaultUnknownHandlerManager.handleUnknownMethod(this, "someMethodName");
            assertEquals(result, "specialActionMethod");
        }

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Make sure we are using the actionConfig we initialized.
        someUnknownHandler.setActionConfig(actionConfig);
        someUnknownHandler.setActionMethodResult("specialActionMethod");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }

}
