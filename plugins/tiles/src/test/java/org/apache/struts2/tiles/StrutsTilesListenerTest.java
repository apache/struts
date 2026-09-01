/*
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
package org.apache.struts2.tiles;

import jakarta.servlet.ServletContextEvent;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.apache.tiles.core.startup.TilesInitializer;
import org.apache.tiles.request.ApplicationContext;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("removal")
public class StrutsTilesListenerTest {

    @Test
    public void missingConfigurationUsesSecureDefault() {
        assertConfiguredValue(Collections.emptyMap(), false);
    }

    @Test
    public void explicitFalseUsesSecureDefault() {
        assertConfiguredValue(Map.of(TilesConstants.STRUTS_TILES_OGNL_LEGACY_ENABLED, "false"), false);
    }

    @Test
    public void explicitTrueEnablesLegacyMode() {
        assertConfiguredValue(Map.of(TilesConstants.STRUTS_TILES_OGNL_LEGACY_ENABLED, "true"), true);
    }

    @Test
    public void mixedCaseTrueUsesNormalBooleanParsing() {
        assertConfiguredValue(Map.of(TilesConstants.STRUTS_TILES_OGNL_LEGACY_ENABLED, "TrUe"), true);
    }

    @Test
    public void invalidValueUsesNormalFalseBooleanParsing() {
        assertConfiguredValue(Map.of(TilesConstants.STRUTS_TILES_OGNL_LEGACY_ENABLED, "not-a-boolean"), false);
    }

    @Test
    public void dispatcherFirstUsesExistingLegacyConfigurationAndSingleLifecycle() {
        MockServletContext servletContext = new MockServletContext();
        ServletContextEvent event = new ServletContextEvent(servletContext);
        Dispatcher dispatcher = StrutsTestCaseHelper.initDispatcher(servletContext,
            Map.of(TilesConstants.STRUTS_TILES_OGNL_LEGACY_ENABLED, "true"));
        CapturingListener listener = new CapturingListener();
        try {
            listener.contextInitialized(event);

            assertTrue(listener.legacyOgnlEnabled);
            assertEquals(1, listener.initializations);
            assertFalse("Tiles must remain active until servlet teardown", listener.destroyed);
        } finally {
            StrutsTestCaseHelper.tearDown(dispatcher);
            listener.contextDestroyed(event);
        }
        assertTrue("Tiles must be destroyed", listener.destroyed);
        assertEquals("Dispatcher and servlet teardown must destroy Tiles exactly once", 1, listener.destructions);
    }

    private void assertConfiguredValue(Map<String, String> constants, boolean expected) {
        MockServletContext servletContext = new MockServletContext();
        ServletContextEvent event = new ServletContextEvent(servletContext);
        CapturingListener listener = new CapturingListener();
        Dispatcher dispatcher = null;
        listener.contextInitialized(event);
        try {
            dispatcher = StrutsTestCaseHelper.initDispatcher(servletContext, constants);

            assertEquals(expected, listener.legacyOgnlEnabled);
            assertEquals(1, listener.initializations);
            assertFalse("Tiles must remain active until dispatcher or servlet teardown", listener.destroyed);
        } finally {
            StrutsTestCaseHelper.tearDown(dispatcher);
            listener.contextDestroyed(event);
        }
        assertTrue("Tiles must be destroyed exactly once", listener.destroyed);
        assertEquals(1, listener.destructions);
    }

    private static class CapturingListener extends StrutsTilesListener {
        private boolean legacyOgnlEnabled;
        private boolean destroyed;
        private int initializations;
        private int destructions;

        @Override
        protected TilesInitializer createTilesInitializer() {
            legacyOgnlEnabled = isLegacyOgnlEnabled();
            return new TilesInitializer() {
                @Override
                public void initialize(ApplicationContext preliminaryContext) {
                    initializations++;
                }

                @Override
                public void destroy() {
                    destroyed = true;
                    destructions++;
                }
            };
        }
    }
}
