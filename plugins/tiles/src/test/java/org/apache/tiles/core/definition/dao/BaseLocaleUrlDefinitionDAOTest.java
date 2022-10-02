/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.core.definition.dao;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Definition;
import org.apache.tiles.core.definition.DefinitionsReader;
import org.apache.tiles.core.definition.RefreshMonitor;
import org.apache.tiles.core.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.locale.PostfixedApplicationResource;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link BaseLocaleUrlDefinitionDAO}.
 */
public class BaseLocaleUrlDefinitionDAOTest {

    private static final class MutableApplicationResource extends PostfixedApplicationResource {
        private long lastModified = System.currentTimeMillis();
        private String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n"
            + "<!DOCTYPE tiles-definitions PUBLIC "
            + "\"-//Apache Software Foundation//DTD Tiles Configuration 3.0//EN\" "
            + "\"http://tiles.apache.org/dtds/tiles-config_3_0.dtd\">\n\n" + "<tiles-definitions>"
            + "<definition name=\"rewrite.test\" template=\"/test.jsp\">"
            + "<put-attribute name=\"testparm\" value=\"testval\"/>" + "</definition>" //
            + "</tiles-definitions>";

        private MutableApplicationResource(String localePath) {
            super(localePath);
        }

        public void modify(String xml) {
            lastModified = System.currentTimeMillis();
            this.xml = xml;
        }

        @Override
        public long getLastModified() {
            return lastModified;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(xml.getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    /**
     * The time (in milliseconds) to wait to be sure that the system updates the
     * modify date of a file.
     */
    private static final int SLEEP_MILLIS = 2000;

    private BaseLocaleUrlDefinitionDAO dao;
    private MutableApplicationResource resource;

    @Before
    public void setUp() throws IOException {
        resource = new MutableApplicationResource("org/apache/tiles/core/config/temp-defs.xml");

        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(applicationContext.getResource("org/apache/tiles/core/config/temp-defs.xml")).andReturn(resource).anyTimes();
        replay(applicationContext);
        dao = createMockBuilder(BaseLocaleUrlDefinitionDAO.class).withConstructor(applicationContext).createMock();
    }

    /**
     * Test method for {@link org.apache.tiles.core.definition.dao.BaseLocaleUrlDefinitionDAO#refreshRequired()}.
     *
     * @throws InterruptedException If something goes wrong.
     */
    @Test
    public void testRefreshRequired() throws InterruptedException {
        // Set up multiple data sources.
        Map<String, Attribute> attribs = new HashMap<>();
        attribs.put("testparm", new Attribute("testval"));
        Definition rewriteTest = new Definition("rewrite.test", Attribute.createTemplateAttribute("/test.jsp"), attribs);
        expect(dao.getDefinition("rewrite.test", null)).andReturn(rewriteTest);

        replay(dao);

        List<ApplicationResource> sources = new ArrayList<>();
        sources.add(resource);
        dao.setSources(sources);
        DefinitionsReader reader = new DigesterDefinitionsReader();
        dao.setReader(reader);

        Request context = createMock(Request.class);
        expect(context.getContext("session")).andReturn(new HashMap<>()).anyTimes();
        expect(context.getRequestLocale()).andReturn(null).anyTimes();
        replay(context);

        Definition definition = dao.getDefinition("rewrite.test", null);
        assertNotNull("rewrite.test definition not found.", definition);
        assertEquals("Incorrect initial template value", "/test.jsp", definition.getTemplateAttribute().getValue());

        RefreshMonitor reloadable = dao;
        dao.loadDefinitionsFromResource(resource);
        assertFalse("Factory should be fresh.", reloadable.refreshRequired());

        // Make sure the system actually updates the timestamp.
        Thread.sleep(SLEEP_MILLIS);

        // Set up multiple data sources.
        resource.modify("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" + "<!DOCTYPE tiles-definitions PUBLIC "
            + "\"-//Apache Software Foundation//DTD Tiles Configuration 3.0//EN\" "
            + "\"http://tiles.apache.org/dtds/tiles-config_3_0.dtd\">\n\n" + "<tiles-definitions>"
            + "<definition name=\"rewrite.test\" template=\"/newtest.jsp\">"
            + "<put-attribute name=\"testparm\" value=\"testval\"/>" + "</definition>" //
            + "</tiles-definitions>");

        assertTrue("Factory should be stale.", reloadable.refreshRequired());

        verify(context, dao);
    }

}
