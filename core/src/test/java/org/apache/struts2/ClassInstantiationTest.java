package org.apache.struts2;

import com.opensymphony.xwork2.inject.Container;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;

import java.util.HashMap;

/**
 * Test class instantiation with Container
 */
public class ClassInstantiationTest extends StrutsInternalTestCase {

    public void testCompositeActionMapperInstantiationWithList() throws Exception {
        // given
        initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
            put(StrutsConstants.STRUTS_MAPPER_COMPOSITE, "struts,restful");
        }});

        // when
        ActionMapper instance = container.getInstance(ActionMapper.class, "composite");

        // then
        assertNotNull(instance);
    }

    public void testCompositeActionMapperInstantiationWithoutList() throws Exception {
        // given
        initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
        }});

        // when
        try {
            container.getInstance(ActionMapper.class, "composite");
            fail();
        }catch (Exception e) {
            // then
            // You cannot use CompositeActionMapper without defined list of "struts.mapper.composite"
            assertTrue(e.getMessage().contains("No mapping found for dependency [type=java.lang.String, name='struts.mapper.composite']"));
        }
    }

}
