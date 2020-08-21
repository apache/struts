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
package org.apache.struts2.osgi.host;

import com.opensymphony.xwork2.config.ConfigurationException;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.mock.web.MockServletContext;

public class FelixOsgiHostTest extends TestCase {
    private static final Logger LOG = LogManager.getLogger(FelixOsgiHostTest.class);
    private FelixOsgiHost felixHost;
    private MockServletContext servletContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Use non-public API to explicitly set Log4j2 logging levels for the test.
        Configurator.setRootLevel(Level.INFO);
        Configurator.setLevel("org.apache.felix.framework.Felix.", Level.INFO);
        Configurator.setLevel("org.apache.struts2.osgi.host.BaseOsgiHost", Level.INFO);
        Configurator.setLevel("org.apache.struts2.osgi.host.FelixOsgiHost", Level.INFO);

        // Set up mock ServletContext for Felix OSGi testing.
        servletContext = new MockServletContext();
        servletContext.setInitParameter("struts.osgi.clearBundleCache", "true");   // Same as default (true).
        servletContext.setInitParameter("struts.osgi.felixCacheLocking", "true");  // Same as default (true).  If testing a start-after-init, value must be false (otherwise the second start fails).
        servletContext.setInitParameter("struts.osgi.runLevel", "0");  // Different from default (3).  Setting to run level above 0 causes error complaints in the Felix initialization for the unit test.
        servletContext.setInitParameter("struts.osgi.logLevel", "3");  // INFO instead of default (1) for ERROR.
        servletContext.setInitParameter("struts.osgi.searchForPropertiesFilesInRelativePath", "true");      // Different from default (false).  Must be true for unit tests.
        servletContext.setInitParameter("struts.osgi.felixPropertiesPath", "default.properties");           // Same as default.
        servletContext.setInitParameter("struts.osgi.strutsOSGiPropertiesPath", "struts-osgi.properties");  // Same as default.

        felixHost = new FelixOsgiHost();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        try {
            felixHost.destroy();
        } catch (Exception ex) {
            // Nothing to be done if destruction fails.
        }
    }

    public void testGetVersionFromString() {
        assertEquals("2.1.1", felixHost.getVersionFromString("2.1.1-SNAPSHOT"));
        assertEquals("2.1.1", felixHost.getVersionFromString("2.1.1.SNAPSHOT"));
        assertEquals("2.1.1", felixHost.getVersionFromString("something-2.1.1.SNAPSHOT"));
        assertEquals("2.1.1", felixHost.getVersionFromString("something-2-1-1.SNAPSHOT"));
        assertEquals("2.1.0", felixHost.getVersionFromString("something-2-1.SNAPSHOT"));
        assertEquals("2.0.0", felixHost.getVersionFromString("something-2.SNAPSHOT"));
        assertEquals("1.0.0", felixHost.getVersionFromString("something"));
    }

    public void testInitAndDestroy() {
        try {
            // Calling init will call start as part of the flow.  Calling startFelix() afterwards
            // will produce errors, unless destroy has been called first
            felixHost.init(servletContext);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException && ex.getMessage().contains("Unable to create cache directory.")) {
                LOG.warn("Felix cache directory could not be created (possible environment issue).  Skipping rest of the test.  Exception message: [{}]", ex.getMessage());
                return;  // Prevent Struts build from failing due to inability to create Felix cache directory.
            } else if (ex instanceof ConfigurationException && ex.getCause() instanceof BundleException && ex.getCause().getMessage().contains("Error creating bundle cache.")) {
                LOG.warn("Felix cache directory could not be populated (possible environment issue).  Skipping rest of the test.  Exception message: [{}], Cause message: [{}]", ex.getMessage(), ex.getCause().getMessage());
                return;  // Prevent Struts build from failing due to inability to create bundles within the Felix cache directory.
            }
            fail("Unable to initialize Felix OSGi container.  Exception: " + ex );
        }

        try {
            felixHost.destroy();
        } catch (Exception ex) {
            fail("Unable to destroy Felix OSGi container after init.  Exception: " + ex );
        }

        // Test to confirm a Felix restart is possible (with cache locking true), using the previously initialized values.
        try {
            felixHost.startFelix();
        } catch (Exception ex) {
            fail("Unable to start Felix OSGi container after destroy.  Exception: " + ex );
        }

        try {
            felixHost.destroy();
        } catch (Exception ex) {
            fail("Unable to destroy Felix OSGi container after start.  Exception: " + ex );
        }
    }

    public void testGetBundles() {
        try {
            Map<String, Bundle> bundles = felixHost.getBundles();
            fail("Expected an IllegalStateException since Felix was never initialized or started");
        } catch (IllegalStateException isex) {
            // Expected state
        } catch (Exception ex) {
            fail("Unable to get Felix bundles.  Exception: " + ex );
        }

        try {
            felixHost.init(servletContext);
            Map<String, Bundle> bundles = felixHost.getBundles();
            assertNotNull("Bundles is null ?", bundles);
            assertFalse("Bundles is empty ?", bundles.isEmpty());
            LOG.info("OSGi Bundles: " + bundles.toString());
        } catch (Exception ex) {
            if (ex instanceof RuntimeException && ex.getMessage().contains("Unable to create cache directory.")) {
                LOG.warn("Felix cache directory could not be created (possible environment issue).  Skipping rest of the test.  Exception message: [{}]", ex.getMessage());
                return;  // Prevent Struts build from failing due to inability to create Felix cache directory.
            } else if (ex instanceof ConfigurationException && ex.getCause() instanceof BundleException && ex.getCause().getMessage().contains("Error creating bundle cache.")) {
                LOG.warn("Felix cache directory could not be populated (possible environment issue).  Skipping rest of the test.  Exception message: [{}], Cause message: [{}]", ex.getMessage(), ex.getCause().getMessage());
                return;  // Prevent Struts build from failing due to inability to create bundles within the Felix cache directory.
            }
            fail("Unable to get Felix bundles.  Exception: " + ex );
        } finally {
            try {
                felixHost.destroy();
            } catch (Exception ex) {
                // Nothing to be done if destruction fails.
            }
        }
    }

    public void testGetActiveBundles() {
        try {
            Map<String, Bundle> bundles = felixHost.getActiveBundles();
            fail("Expected an IllegalStateException since Felix was never initialized or started");
        } catch (IllegalStateException isex) {
            // Expected state
        } catch (Exception ex) {
            fail("Unable to get Felix active bundles.  Exception: " + ex );
        }

        try {
            felixHost.init(servletContext);
            Map<String, Bundle> bundles = felixHost.getActiveBundles();
            assertNotNull("Bundles is null ?", bundles);
            assertFalse("Bundles is empty ?", bundles.isEmpty());
            LOG.info("OSGi Active Bundles: " + bundles.toString());
        } catch (Exception ex) {
            if (ex instanceof RuntimeException && ex.getMessage().contains("Unable to create cache directory.")) {
                LOG.warn("Felix cache directory could not be created (possible environment issue).  Skipping rest of the test.  Exception message: [{}]", ex.getMessage());
                return;  // Prevent Struts build from failing due to inability to create Felix cache directory.
            } else if (ex instanceof ConfigurationException && ex.getCause() instanceof BundleException && ex.getCause().getMessage().contains("Error creating bundle cache.")) {
                LOG.warn("Felix cache directory could not be populated (possible environment issue).  Skipping rest of the test.  Exception message: [{}], Cause message: [{}]", ex.getMessage(), ex.getCause().getMessage());
                return;  // Prevent Struts build from failing due to inability to create bundles within the Felix cache directory.
            }
            fail("Unable to get Felix active bundles.  Exception: " + ex );
        } finally {
            try {
                felixHost.destroy();
            } catch (Exception ex) {
                // Nothing to be done if destruction fails.
            }
        }
    }

    public void testGetBundleContext() {
        try {
            BundleContext bundleContext = felixHost.getBundleContext();
            fail("Expected an IllegalStateException since Felix was never initialized or started");
        } catch (IllegalStateException isex) {
            // Expected state
        } catch (Exception ex) {
            fail("Unable to get Felix bundle context.  Exception: " + ex );
        }

        try {
            felixHost.init(servletContext);
            BundleContext bundleContext = felixHost.getBundleContext();
            assertNotNull("Bundle context is null ?", bundleContext);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException && ex.getMessage().contains("Unable to create cache directory.")) {
                LOG.warn("Felix cache directory could not be created (possible environment issue).  Skipping rest of the test.  Exception message: [{}]", ex.getMessage());
                return;  // Prevent Struts build from failing due to inability to create Felix cache directory.
            } else if (ex instanceof ConfigurationException && ex.getCause() instanceof BundleException && ex.getCause().getMessage().contains("Error creating bundle cache.")) {
                LOG.warn("Felix cache directory could not be populated (possible environment issue).  Skipping rest of the test.  Exception message: [{}], Cause message: [{}]", ex.getMessage(), ex.getCause().getMessage());
                return;  // Prevent Struts build from failing due to inability to create bundles within the Felix cache directory.
            }
            fail("Unable to get Felix bundle context.  Exception: " + ex );
        } finally {
            try {
                felixHost.destroy();
            } catch (Exception ex) {
                // Nothing to be done if destruction fails.
            }
        }
    }
}
