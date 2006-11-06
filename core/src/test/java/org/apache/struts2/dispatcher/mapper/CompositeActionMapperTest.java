/*
 * $Id: $
 *
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
package org.apache.struts2.dispatcher.mapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.Settings;
import org.apache.struts2.dispatcher.mapper.CompositeActionMapper.IndividualActionMapperEntry;
import org.springframework.mock.web.MockHttpServletRequest;

import com.opensymphony.xwork2.config.ConfigurationManager;

import junit.framework.TestCase;

/**
 *
 * @version $Date$ $Id$
 */
public class CompositeActionMapperTest extends TestCase {

    /**
     * Test with empty settings (settings with no entries of interest)
     *
     * @throws Exception
     */
    public void testGetOrderActionMapperEntries1() throws Exception {
        CompositeActionMapper compositeActionMapper = new CompositeActionMapper();
        List<IndividualActionMapperEntry> result =
            compositeActionMapper.getOrderedActionMapperEntries();

        assertEquals(result.size(), 0);
    }

    /**
     * Test with a normal settings.
     *
     * @throws Exception
     */
    public void testGetOrderActionMapperEntries2() throws Exception {
        CompositeActionMapper compositeActionMapper = new CompositeActionMapper();

        Settings old = Settings.getInstance();
        try {
            Settings.setInstance(new InnerSettings());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1", InnerActionMapper1.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"2", InnerActionMapper2.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3", InnerActionMapper3.class.getName());

            List<IndividualActionMapperEntry> result =
                compositeActionMapper.getOrderedActionMapperEntries();

            assertEquals(result.size(), 3);

            IndividualActionMapperEntry e = null;
            Iterator<IndividualActionMapperEntry> i = result.iterator();

            // 1
            e = i.next();

            assertEquals(e.order, new Integer(1));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1");
            assertEquals(e.propertyValue, InnerActionMapper1.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper1.class);

            // 2
            e = i.next();

            assertEquals(e.order, new Integer(2));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"2");
            assertEquals(e.propertyValue, InnerActionMapper2.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper2.class);

            // 3
            e = i.next();
            assertEquals(e.order, new Integer(3));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3");
            assertEquals(e.propertyValue, InnerActionMapper3.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper3.class);
        }
        finally {
            Settings.setInstance(old);
        }
    }

    /**
     * Test with settings where entries are out-of-order, it needs to be able to retrieve them
     * back in proper order.
     *
     * @throws Exception
     */
    public void testGetOrderActionMapperEntries3() throws Exception {
        CompositeActionMapper compositeActionMapper = new CompositeActionMapper();

        Settings old = Settings.getInstance();
        try {
            Settings.setInstance(new InnerSettings());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3", InnerActionMapper3.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"2", InnerActionMapper2.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1", InnerActionMapper1.class.getName());

            List<IndividualActionMapperEntry> result =
                compositeActionMapper.getOrderedActionMapperEntries();

            assertEquals(result.size(), 3);

            IndividualActionMapperEntry e = null;
            Iterator<IndividualActionMapperEntry> i = result.iterator();

            // 1
            e = i.next();

            assertEquals(e.order, new Integer(1));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1");
            assertEquals(e.propertyValue, InnerActionMapper1.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper1.class);

            // 2
            e = i.next();

            assertEquals(e.order, new Integer(2));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"2");
            assertEquals(e.propertyValue, InnerActionMapper2.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper2.class);

            // 3
            e = i.next();
            assertEquals(e.order, new Integer(3));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3");
            assertEquals(e.propertyValue, InnerActionMapper3.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper3.class);
        }
        finally {
            Settings.setInstance(old);
        }
    }

    /**
     * Test with a bad entry
     *
     * @throws Exception
     */
    public void testGetOrderActionMapperEntries4() throws Exception {
        CompositeActionMapper compositeActionMapper = new CompositeActionMapper();

        Settings old = Settings.getInstance();
        try {
            Settings.setInstance(new InnerSettings());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1", InnerActionMapper1.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"NotANumber", InnerActionMapper2.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3", InnerActionMapper3.class.getName());

            List<IndividualActionMapperEntry> result =
                compositeActionMapper.getOrderedActionMapperEntries();

            assertEquals(result.size(), 2);

            IndividualActionMapperEntry e = null;
            Iterator<IndividualActionMapperEntry> i = result.iterator();

            // 1
            e = i.next();

            assertEquals(e.order, new Integer(1));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1");
            assertEquals(e.propertyValue, InnerActionMapper1.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper1.class);

            // 2
            e = i.next();
            assertEquals(e.order, new Integer(3));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3");
            assertEquals(e.propertyValue, InnerActionMapper3.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper3.class);
        }
        finally {
            Settings.setInstance(old);
        }
    }

    /**
     * Test with an entry where the action mapper class is bogus.
     * @throws Exception
     */
    public void testGetOrderActionMapperEntries5() throws Exception {
        CompositeActionMapper compositeActionMapper = new CompositeActionMapper();

        Settings old = Settings.getInstance();
        try {
            Settings.setInstance(new InnerSettings());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1", InnerActionMapper1.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"2", "bogus.class.name");
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3", InnerActionMapper3.class.getName());

            List<IndividualActionMapperEntry> result =
                compositeActionMapper.getOrderedActionMapperEntries();

            assertEquals(result.size(), 2);

            IndividualActionMapperEntry e = null;
            Iterator<IndividualActionMapperEntry> i = result.iterator();

            // 1
            e = i.next();

            assertEquals(e.order, new Integer(1));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1");
            assertEquals(e.propertyValue, InnerActionMapper1.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper1.class);


            // 2
            e = i.next();
            assertEquals(e.order, new Integer(3));
            assertEquals(e.propertyName, StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3");
            assertEquals(e.propertyValue, InnerActionMapper3.class.getName());
            assertEquals(e.actionMapper.getClass(), InnerActionMapper3.class);
        }
        finally {
            Settings.setInstance(old);
        }
    }



    public void testGetActionMappingAndUri1() throws Exception {
        CompositeActionMapper compositeActionMapper = new CompositeActionMapper();

        Settings old = Settings.getInstance();
        try {
            Settings.setInstance(new InnerSettings());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1", InnerActionMapper1.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"2", InnerActionMapper2.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"3", InnerActionMapper3.class.getName());


            ActionMapping actionMapping = compositeActionMapper.getMapping(new MockHttpServletRequest(), new ConfigurationManager());
            String uri = compositeActionMapper.getUriFromActionMapping(new ActionMapping());

            assertNotNull(actionMapping);
            assertNotNull(uri);
            assertTrue(actionMapping == InnerActionMapper3.actionMapping);
            assertTrue(uri == InnerActionMapper3.uri);
        }
        finally {
            Settings.setInstance(old);
        }
    }

    public void testGetActionMappingAndUri2() throws Exception {
        CompositeActionMapper compositeActionMapper = new CompositeActionMapper();

        Settings old = Settings.getInstance();
        try {
            Settings.setInstance(new InnerSettings());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"1", InnerActionMapper1.class.getName());
            Settings.set(StrutsConstants.STRUTS_MAPPER_COMPOSITE+"2", InnerActionMapper2.class.getName());


            ActionMapping actionMapping = compositeActionMapper.getMapping(new MockHttpServletRequest(), new ConfigurationManager());
            String uri = compositeActionMapper.getUriFromActionMapping(new ActionMapping());

            assertNull(actionMapping);
            assertNull(uri);
        }
        finally {
            Settings.setInstance(old);
        }
    }


    public static class InnerActionMapper1 implements ActionMapper {
        public static ActionMapping actionMapping = new ActionMapping();
        public static String uri="uri1";

        public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
            return null;
        }
        public String getUriFromActionMapping(ActionMapping mapping) {
            return null;
        }
    }
    public static class InnerActionMapper2 implements ActionMapper {
        public static ActionMapping actionMapping = new ActionMapping();
        public static String uri="uri2";

        public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
            return null;
        }
        public String getUriFromActionMapping(ActionMapping mapping) {
            return null;
        }
    }
    public static class InnerActionMapper3 implements ActionMapper {
        public static ActionMapping actionMapping = new ActionMapping();
        public static String uri = "uri3";

        public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
            return actionMapping;
        }
        public String getUriFromActionMapping(ActionMapping mapping) {
            return uri;
        }
    }

    class InnerSettings extends Settings {
        private Map<String, String> _impl = new LinkedHashMap<String, String>();

        @Override
        public boolean isSetImpl(String name) {
            return _impl.containsKey(name);
        }
        @Override
        public void setImpl(String name, String value) throws IllegalArgumentException, UnsupportedOperationException {
            _impl.put(name, value);
        }
        @Override
        public String getImpl(String name) throws IllegalArgumentException {
            return (String) _impl.get(name);
        }
        @Override
        public Iterator listImpl() {
            return _impl.keySet().iterator();
        }
    }

}
