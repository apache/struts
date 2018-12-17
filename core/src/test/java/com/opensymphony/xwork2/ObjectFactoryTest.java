package com.opensymphony.xwork2;

import com.opensymphony.xwork2.mock.DummyTextProvider;
import com.opensymphony.xwork2.mock.InjectableAction;
import org.apache.struts2.StrutsInternalTestCase;

import java.util.HashMap;

public class ObjectFactoryTest extends StrutsInternalTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.loadButAdd(TextProvider.class, new DummyTextProvider());
    }

    public void testCreatingActionsWithInjectableParametersInConstructor() throws Exception {
        // given
        ObjectFactory of = container.getInstance(ObjectFactory.class);

        // when
        InjectableAction action = (InjectableAction) of.buildBean(InjectableAction.class, new HashMap<String, Object>());

        // then
        assertNotNull(action.getTextProvider());
        assertTrue(action.getTextProvider() instanceof DummyTextProvider);
    }
}