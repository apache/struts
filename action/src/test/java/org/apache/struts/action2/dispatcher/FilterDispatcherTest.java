/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.dispatcher;

import com.opensymphony.xwork.ObjectFactory;
import org.apache.struts.action2.StrutsConstants;
import org.apache.struts.action2.StrutsTestCase;
import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.util.ObjectFactoryDestroyable;
import org.apache.struts.action2.util.ObjectFactoryInitializable;
import org.apache.struts.action2.util.ObjectFactoryLifecycle;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * FilterDispatcher TestCase.
 *
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class FilterDispatcherTest extends StrutsTestCase {


    public void testParsePackages() throws Exception {
        FilterDispatcher filterDispatcher = new FilterDispatcher();
        String[] result1 = filterDispatcher.parse("foo.bar.package1 foo.bar.package2 foo.bar.package3");
        String[] result2 = filterDispatcher.parse("foo.bar.package1\tfoo.bar.package2\tfoo.bar.package3");
        String[] result3 = filterDispatcher.parse("foo.bar.package1,foo.bar.package2,foo.bar.package3");
        String[] result4 = filterDispatcher.parse("foo.bar.package1    foo.bar.package2  \t foo.bar.package3   , foo.bar.package4");

        assertEquals(result1[0], "foo/bar/package1/");
        assertEquals(result1[1], "foo/bar/package2/");
        assertEquals(result1[2], "foo/bar/package3/");

        assertEquals(result2[0], "foo/bar/package1/");
        assertEquals(result2[1], "foo/bar/package2/");
        assertEquals(result2[2], "foo/bar/package3/");

        assertEquals(result3[0], "foo/bar/package1/");
        assertEquals(result3[1], "foo/bar/package2/");
        assertEquals(result3[2], "foo/bar/package3/");

        assertEquals(result4[0], "foo/bar/package1/");
        assertEquals(result4[1], "foo/bar/package2/");
        assertEquals(result4[2], "foo/bar/package3/");
        assertEquals(result4[3], "foo/bar/package4/");
    }

    public void testObjectFactoryDestroy() throws Exception {
        Configuration.reset();
        DispatcherUtils.setInstance(null);

        DispatcherUtils.initialize(new MockServletContext());

        FilterDispatcher filterDispatcher = new FilterDispatcher();
        InnerDestroyableObjectFactory destroyedObjectFactory = new InnerDestroyableObjectFactory();
        ObjectFactory.setObjectFactory(destroyedObjectFactory);

        assertFalse(destroyedObjectFactory.destroyed);
        filterDispatcher.destroy();
        assertTrue(destroyedObjectFactory.destroyed);
    }


    public void testObjectFactoryInitializable() throws Exception {
        Configuration.reset();
        DispatcherUtils.setInstance(null);

        Map configMap = new HashMap();
        configMap.put(StrutsConstants.STRUTS_OBJECTFACTORY, "org.apache.struts.action2.dispatcher.FilterDispatcherTest$InnerInitializableObjectFactory");
        configMap.put(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "false");
        Configuration.setConfiguration(new InnerConfiguration(configMap));

        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);


        FilterDispatcher filterDispatcher = new FilterDispatcher();
        filterDispatcher.init(filterConfig);

        assertTrue(ObjectFactory.getObjectFactory() instanceof InnerInitializableObjectFactory);
        assertTrue(((InnerInitializableObjectFactory) ObjectFactory.getObjectFactory()).initializable);
    }

    public void testObjectFactoryLifecycle() throws Exception {
        Configuration.reset();
        DispatcherUtils.setInstance(null);

        Map configMap = new HashMap();
        configMap.put(StrutsConstants.STRUTS_OBJECTFACTORY, "org.apache.struts.action2.dispatcher.FilterDispatcherTest$InnerInitailizableDestroyableObjectFactory");
        configMap.put(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "false");
        Configuration.setConfiguration(new InnerConfiguration(configMap));

        MockServletContext servletContext = new MockServletContext();
        MockFilterConfig filterConfig = new MockFilterConfig(servletContext);


        FilterDispatcher filterDispatcher = new FilterDispatcher();
        filterDispatcher.init(filterConfig);

        assertTrue(ObjectFactory.getObjectFactory() instanceof InnerInitailizableDestroyableObjectFactory);
        assertTrue(((InnerInitailizableDestroyableObjectFactory) ObjectFactory.getObjectFactory()).initializable);

        assertFalse(((InnerInitailizableDestroyableObjectFactory) ObjectFactory.getObjectFactory()).destroyable);
        filterDispatcher.destroy();
        assertTrue(((InnerInitailizableDestroyableObjectFactory) ObjectFactory.getObjectFactory()).destroyable);
    }


    // === inner class ========
    public static class InnerConfiguration extends Configuration {
        Map m;

        public InnerConfiguration(Map configMap) {
            m = configMap;
        }

        public boolean isSetImpl(String name) {
            if (!m.containsKey(name))
                return super.isSetImpl(name);
            else
                return true;
        }

        public Object getImpl(String aName) throws IllegalArgumentException {
            if (!m.containsKey(aName))
                return super.getImpl(aName);
            else
                return m.get(aName);
        }
    }


    public static class InnerDestroyableObjectFactory extends ObjectFactory implements ObjectFactoryDestroyable {
        public boolean destroyed = false;

        public void destroy() {
            destroyed = true;
        }
    }

    public static class InnerInitializableObjectFactory extends ObjectFactory implements ObjectFactoryInitializable {
        public boolean initializable = false;

        public void init(ServletContext servletContext) {
            initializable = true;
        }
    }

    public static class InnerInitailizableDestroyableObjectFactory extends ObjectFactory implements ObjectFactoryLifecycle {
        public boolean initializable = false;
        public boolean destroyable = false;

        public void init(ServletContext servletContext) {
            initializable = true;
        }

        public void destroy() {
            destroyable = true;
        }
    }


}
