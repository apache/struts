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
package org.apache.struts2.interceptor;

import static org.apache.struts2.interceptor.ResourceIsolationPolicy.DEST_EMBED;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.DEST_OBJECT;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.DEST_SCRIPT;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.MODE_NAVIGATE;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_DEST_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_MODE_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_SITE_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_USER_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SITE_NONE;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SITE_SAME_ORIGIN;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SITE_SAME_SITE;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.VARY_HEADER;
import static org.junit.Assert.assertNotEquals;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.http.HttpServletResponse;

public class FetchMetadataInterceptorTest extends XWorkTestCase {

    private final FetchMetadataInterceptor interceptor = new FetchMetadataInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private static final String ACCEPT_ENCODING_VALUE = "Accept-Encoding";
    private static final String VARY_HEADER_VALUE = String.format("%s,%s,%s,%s", SEC_FETCH_DEST_HEADER, SEC_FETCH_MODE_HEADER, SEC_FETCH_SITE_HEADER, SEC_FETCH_USER_HEADER);
    private static final String SC_FORBIDDEN = String.valueOf(HttpServletResponse.SC_FORBIDDEN);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
        interceptor.setExemptedPaths("/foo,/bar");
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);
    }

    public void testNoSite() throws Exception {
        request.removeHeader(SEC_FETCH_SITE_HEADER);

        assertNotEquals("Expected interceptor to accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
    }

    public void testValidSite() throws Exception {
        for (String header : Arrays.asList(SITE_SAME_ORIGIN, SITE_SAME_SITE, SITE_NONE)){
            request.addHeader(SEC_FETCH_SITE_HEADER, header);

            assertNotEquals("Expected interceptor to accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
        }

    }

    public void testValidTopLevelNavigation() throws Exception {
        request.addHeader(SEC_FETCH_MODE_HEADER, MODE_NAVIGATE);
        request.addHeader(SEC_FETCH_DEST_HEADER, DEST_SCRIPT);
        request.setMethod("GET");

        assertNotEquals("Expected interceptor to accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
    }

    public void testInvalidTopLevelNavigation() throws Exception {
        for (String header : Arrays.asList(DEST_OBJECT, DEST_EMBED)) {
            request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
            request.addHeader(SEC_FETCH_MODE_HEADER, MODE_NAVIGATE);
            request.addHeader(SEC_FETCH_DEST_HEADER, header);
            request.setMethod("GET");

            assertEquals("Expected interceptor to NOT accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
        }
    }

    public void testPathInExemptedPaths() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        request.setContextPath("/foo");

        assertNotEquals("Expected interceptor to accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
    }

    public void testPathNotInExemptedPaths() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        request.setContextPath("/foobar");

        assertEquals("Expected interceptor to NOT accept this request", SC_FORBIDDEN, interceptor.intercept(mai));
    }

    public void testVaryHeaderAcceptedReq() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        request.setContextPath("/foo");

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(VARY_HEADER), VARY_HEADER_VALUE);
    }

    public void testVaryHeaderRejectedReq() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(VARY_HEADER));
        assertEquals("Expected different vary header value", response.getHeader(VARY_HEADER), VARY_HEADER_VALUE);
    }

    public void testVaryHeaderReplaced() throws Exception {
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        response.addHeader(VARY_HEADER, ACCEPT_ENCODING_VALUE);  // Simulate Vary header present due to processing before this interceptor.
        assertEquals("Initial vary response header addition failed ?", response.getHeader(VARY_HEADER), ACCEPT_ENCODING_VALUE);

        interceptor.intercept(mai);

        assertTrue("Expected vary header to be included", response.containsHeader(VARY_HEADER));
        assertFalse("Expected original vary header content to be replaced", response.getHeader(VARY_HEADER).contains(ACCEPT_ENCODING_VALUE));
        assertTrue("Expected added vary header content to be present", response.getHeader(VARY_HEADER).contains(VARY_HEADER_VALUE));
    }

    public void testSetExemptedPathsInjectionIndirectly() throws Exception {
        // Perform a multi-step test to confirm (indirectly) that the method parameter injection of setExemptedPaths() for
        // the FetchMetadataInterceptor is functioning as expected, when configured appropriately.
        // Ensure we're using the specific test configuration, not the default simple configuration.
        XmlConfigurationProvider configurationProvider = new StrutsXmlConfigurationProvider("struts-testing.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);

        // The test configuration in "struts-testing.xml" should define a "default" package.  That "default" package should contain a "defaultInterceptorStack" containing
        // a "fetchMetadata" interceptor parameter "fetchMetadata.setExemptedPaths".  If the parameter method injection is working correctly for the FetchMetadataInterceptor,
        // the exempted paths should be set appropriately for the interceptor instances, once the configuration is loaded into the container.
        final PackageConfig defaultPackageConfig = configuration.getPackageConfig("default");
        final InterceptorStackConfig defaultInterceptorStackConfig = (InterceptorStackConfig) defaultPackageConfig.getInterceptorConfig("defaultInterceptorStack");
        final Collection<InterceptorMapping> defaultInterceptorStackInterceptors = defaultInterceptorStackConfig.getInterceptors();
        assertFalse("'defaultInterceptorStack' interceptors in struts-testing.xml is empty ?", defaultInterceptorStackInterceptors.isEmpty());
        InterceptorMapping configuredFetchMetadataInterceptorMapping = null;
        Iterator<InterceptorMapping> interceptorIterator = defaultInterceptorStackInterceptors.iterator();
        while (interceptorIterator.hasNext()) {
            InterceptorMapping currentMapping = interceptorIterator.next();
            if (currentMapping != null && "fetchMetadata".equals(currentMapping.getName())) {
                configuredFetchMetadataInterceptorMapping = currentMapping;
                break;
            }
        }
        assertNotNull("'fetchMetadata' interceptor mapping not present after loading 'struts-testing.xml' ?", configuredFetchMetadataInterceptorMapping);
        assertTrue("'fetchMetadata' interceptor mapping loaded from 'struts-testing.xml' produced a non-FetchMetadataInterceptor type ?", configuredFetchMetadataInterceptorMapping.getInterceptor() instanceof FetchMetadataInterceptor);
        FetchMetadataInterceptor configuredFetchMetadataInterceptor = (FetchMetadataInterceptor) configuredFetchMetadataInterceptorMapping.getInterceptor();
        request.removeHeader(SEC_FETCH_SITE_HEADER);
        request.addHeader(SEC_FETCH_SITE_HEADER, "foo");
        request.setContextPath("/foo");
        assertEquals("Expected interceptor to NOT accept this request [/foo]", SC_FORBIDDEN, configuredFetchMetadataInterceptor.intercept(mai));
        request.setContextPath("/fetchMetadataExemptedGlobal");
        assertNotEquals("Expected interceptor to accept this request [/fetchMetadataExemptedGlobal]", SC_FORBIDDEN, configuredFetchMetadataInterceptor.intercept(mai));
        request.setContextPath("/someOtherPath");
        assertNotEquals("Expected interceptor to accept this request [/someOtherPath]", SC_FORBIDDEN, configuredFetchMetadataInterceptor.intercept(mai));

        // The test configuration in "struts-testing.xml" should also contain three actions configured differently for the "fetchMetadata" interceptor.
        // "fetchMetadataExempted" has an override exemption matching its action name, "fetchMetadataNotExempted" has an override exemption NOT matching its action name,
        // and "fetchMetadataExemptedGlobal" has an action name matching an exemption defined in "defaultInterceptorStack".
        final RuntimeConfiguration runtimeConfiguration = configuration.getRuntimeConfiguration();
        final ActionConfig fetchMetadataExemptedActionConfig = runtimeConfiguration.getActionConfig("/", "fetchMetadataExempted");
        final ActionConfig fetchMetadataNotExemptedActionConfig = runtimeConfiguration.getActionConfig("/", "fetchMetadataNotExempted");
        final ActionConfig fetchMetadataExemptedGlobalActionConfig = runtimeConfiguration.getActionConfig("/", "fetchMetadataExemptedGlobal");
        assertNotNull("'fetchMetadataExempted' action config not present in 'struts-testing.xml' ?", fetchMetadataExemptedActionConfig);
        assertNotNull("'fetchMetadataNotExempted' action config not present in 'struts-testing.xml' ?", fetchMetadataExemptedActionConfig);
        assertNotNull("'fetchMetadataExemptedGlobal' action config not present in 'struts-testing.xml' ?", fetchMetadataExemptedActionConfig);

        // Test fetchMetadata interceptor for the "fetchMetadataExempted" action.
        Collection<InterceptorMapping> currentActionInterceptors = fetchMetadataExemptedActionConfig.getInterceptors();
        assertFalse("'fetchMetadataExempted' interceptors in struts-testing.xml is empty ?", currentActionInterceptors.isEmpty());
        configuredFetchMetadataInterceptorMapping = null;
        interceptorIterator = currentActionInterceptors.iterator();
        while (interceptorIterator.hasNext()) {
            InterceptorMapping currentMapping = interceptorIterator.next();
            if (currentMapping != null && "fetchMetadata".equals(currentMapping.getName())) {
                configuredFetchMetadataInterceptorMapping = currentMapping;
                break;
            }
        }
        assertNotNull("'fetchMetadata' interceptor mapping for action 'fetchMetadataExempted' not present in 'struts-testing.xml' ?", configuredFetchMetadataInterceptorMapping);
        assertTrue("'fetchMetadata' interceptor mapping for action 'fetchMetadataExempted' in 'struts-testing.xml' produced a non-FetchMetadataInterceptor type ?", configuredFetchMetadataInterceptorMapping.getInterceptor() instanceof FetchMetadataInterceptor);
        configuredFetchMetadataInterceptor = (FetchMetadataInterceptor) configuredFetchMetadataInterceptorMapping.getInterceptor();
        request.removeHeader(SEC_FETCH_SITE_HEADER);
        request.addHeader(SEC_FETCH_SITE_HEADER, fetchMetadataExemptedActionConfig.getName());
        request.setContextPath("/" + fetchMetadataExemptedActionConfig.getName());
        assertNotEquals("Expected interceptor to accept this request [" + "/" + fetchMetadataExemptedActionConfig.getName() + "]", SC_FORBIDDEN, configuredFetchMetadataInterceptor.intercept(mai));

        // Test fetchMetadata interceptor for the "fetchMetadataNotExempted" action.
        currentActionInterceptors = fetchMetadataNotExemptedActionConfig.getInterceptors();
        assertFalse("'fetchMetadataNotExempted' interceptors in struts-testing.xml is empty ?", currentActionInterceptors.isEmpty());
        configuredFetchMetadataInterceptorMapping = null;
        interceptorIterator = currentActionInterceptors.iterator();
        while (interceptorIterator.hasNext()) {
            InterceptorMapping currentMapping = interceptorIterator.next();
            if (currentMapping != null && "fetchMetadata".equals(currentMapping.getName())) {
                configuredFetchMetadataInterceptorMapping = currentMapping;
                break;
            }
        }
        assertNotNull("'fetchMetadata' interceptor mapping for action 'fetchMetadataNotExempted' not present in 'struts-testing.xml' ?", configuredFetchMetadataInterceptorMapping);
        assertTrue("'fetchMetadata' interceptor mapping 'fetchMetadataExempted' in 'struts-testing.xml' produced a non-FetchMetadataInterceptor type ?", configuredFetchMetadataInterceptorMapping.getInterceptor() instanceof FetchMetadataInterceptor);
        configuredFetchMetadataInterceptor = (FetchMetadataInterceptor) configuredFetchMetadataInterceptorMapping.getInterceptor();
        request.removeHeader(SEC_FETCH_SITE_HEADER);
        request.addHeader(SEC_FETCH_SITE_HEADER, fetchMetadataNotExemptedActionConfig.getName());
        request.setContextPath("/" + fetchMetadataNotExemptedActionConfig.getName());
        assertEquals("Expected interceptor to NOT accept this request [" + "/" + fetchMetadataNotExemptedActionConfig.getName() + "]", SC_FORBIDDEN, configuredFetchMetadataInterceptor.intercept(mai));

        // Test fetchMetadata interceptor for the "fetchMetadataExemptedGlobal" action.
        currentActionInterceptors = fetchMetadataExemptedGlobalActionConfig.getInterceptors();
        assertFalse("'fetchMetadataExemptedGlobal' interceptors in struts-testing.xml is empty ?", currentActionInterceptors.isEmpty());
        configuredFetchMetadataInterceptorMapping = null;
        interceptorIterator = currentActionInterceptors.iterator();
        while (interceptorIterator.hasNext()) {
            InterceptorMapping currentMapping = interceptorIterator.next();
            if (currentMapping != null && "fetchMetadata".equals(currentMapping.getName())) {
                configuredFetchMetadataInterceptorMapping = currentMapping;
                break;
            }
        }
        assertNotNull("'fetchMetadata' interceptor mapping for action 'fetchMetadataExemptedGlobal' not present in 'struts-testing.xml' ?", configuredFetchMetadataInterceptorMapping);
        assertTrue("'fetchMetadata' interceptor mapping 'fetchMetadataExemptedGlobal' in 'struts-testing.xml' produced a non-FetchMetadataInterceptor type ?", configuredFetchMetadataInterceptorMapping.getInterceptor() instanceof FetchMetadataInterceptor);
        configuredFetchMetadataInterceptor = (FetchMetadataInterceptor) configuredFetchMetadataInterceptorMapping.getInterceptor();
        request.removeHeader(SEC_FETCH_SITE_HEADER);
        request.addHeader(SEC_FETCH_SITE_HEADER, fetchMetadataExemptedGlobalActionConfig.getName());
        request.setContextPath("/" + fetchMetadataExemptedGlobalActionConfig.getName());
        assertNotEquals("Expected interceptor to accept this request [" + "/" + fetchMetadataExemptedGlobalActionConfig.getName() + "]", SC_FORBIDDEN, configuredFetchMetadataInterceptor.intercept(mai));
    }

}
