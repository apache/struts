/*
 * $Id$
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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Container;
import junit.framework.TestCase;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.easymock.EasyMock;

import javax.servlet.ServletContext;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

public class ConventionUnknownHandlerTest extends TestCase {

    private PackageConfig packageConfiguration;

    public void testCanonicalizeShouldReturnNullWhenPathIsNull() throws Exception {
        final ConventionUnknownHandler handler = conventionUnknownHandler();

        assertEquals(null, handler.canonicalize(null));
    }

    public void testCanonicalizeShouldCollapseMultipleConsecutiveSlashesIntoJustOne() throws Exception {
        final ConventionUnknownHandler handler = conventionUnknownHandler();

        assertEquals("/should/condense/multiple/consecutive/slashes/into/just-one.ext",
                handler.canonicalize("//should///condense////multiple/////consecutive////slashes///into//just-one.ext"));
    }

    public void testCanonicalizeShouldNotModifySingleSlashes() throws Exception {
        final ConventionUnknownHandler handler = conventionUnknownHandler();

        assertEquals("/should/not/modify/single/slashes.ext",
                handler.canonicalize("/should/not/modify/single/slashes.ext"));
    }

    public void testHandleUnknownActionPointToIndex() throws Exception {
        // given
        ServletContext servletContext = createStrictMock(ServletContext.class);
        expect(servletContext.getResource("/does-not-exist.jsp")).andReturn(null);
        expect(servletContext.getResource("/does-not-exist/index.jsp")).andReturn(null);
        replay(servletContext);

        ConventionUnknownHandler handler = conventionUnknownHandler(servletContext);

        // when
        ActionConfig config = handler.handleUnknownAction("", "/does-not-exist");

        // then
        assertNotNull(config);
        assertEquals("", config.getPackageName());
        assertEquals("index", config.getName());
    }

    public void testFindResourceShouldReturnNullAfterTryingEveryExtensionWithoutSuccess() throws Exception {
        final ServletContext servletContext = createStrictMock(ServletContext.class);  // Verifies method call order

        expect(servletContext.getResource("/some/path/which/does/not/exist/for/any/file/with.default_extension"))
                .andReturn(null);
        expect(servletContext.getResource("/some/path/which/does/not/exist/for/any/file/with.non_default_extension"))
                .andReturn(null);
        expect(servletContext.getResource("/some/path/which/does/not/exist/for/any/file/with.some_other_extension"))
                .andReturn(null);

        replay(servletContext);

        final ConventionUnknownHandler handler = conventionUnknownHandler(servletContext);

        final ConventionUnknownHandler.Resource resource = handler.findResource(defaultResultsByExtension(),
                "/some/path/which/does/not/exist/for/any/file/with");

        verify(servletContext);

        assertNull(resource);
    }

    public void testFindResourceShouldLookupResourceWithCanonicalPath() throws Exception {
        final ServletContext servletContext = createStrictMock(ServletContext.class);  // Verifies method call order

        final URL url = new URL("http://localhost");
        expect(servletContext.getResource("/canonicalized/path.default_extension")).andReturn(url);

        replay(servletContext);

        final ConventionUnknownHandler handler = conventionUnknownHandler(servletContext);
        handler.findResource(defaultResultsByExtension(), "///canonicalized//path");

        verify(servletContext);
    }

    public void testFindResourceShouldSetCanonicalizedPathOnResource() throws Exception {
        final ServletContext servletContext = createMock(ServletContext.class);

        final URL url = new URL("http://localhost");
        expect(servletContext.getResource("/canonicalized/path.default_extension")).andReturn(url);

        replay(servletContext);

        final ConventionUnknownHandler handler = conventionUnknownHandler(servletContext);

        final ConventionUnknownHandler.Resource resource = handler.findResource(defaultResultsByExtension(),
                "///canonicalized//path");

        assertEquals("/canonicalized/path.default_extension", resource.path);
    }

    private Configuration configuration(final String packageName) {
        final Configuration mock = createNiceMock(Configuration.class);

        packageConfiguration = packageConfiguration();
        expect(mock.getPackageConfig(packageName)).andStubReturn(packageConfiguration);
        RuntimeConfiguration runtime = createNiceMock(RuntimeConfiguration.class);
        expect(runtime.getActionConfig("", "index")).andStubReturn(new ActionConfig.Builder("", "index", "").build());
        expect(mock.getRuntimeConfiguration()).andStubReturn(runtime);

        replay(mock, runtime);

        return mock;
    }

    private Container container() {
        final Container mock = createNiceMock(Container.class);
        ConventionsService service = EasyMock.createNiceMock(ConventionsService.class);

        expect(mock.getInstance(String.class, ConventionConstants.CONVENTION_CONVENTIONS_SERVICE)).andReturn("test");
        expect(mock.getInstance(ConventionsService.class, "test")).andStubReturn(service);

        ActionConfig actionConfig = null;
        expect(service.determineResultPath(actionConfig)).andReturn("");
        Map<String, ResultTypeConfig> results = new HashMap<String, ResultTypeConfig>();
        results.put("jsp", new ResultTypeConfig.Builder("dispatcher", ServletDispatcherResult.class.getName()).build());
        expect(service.getResultTypesByExtension(packageConfiguration)).andReturn(results);

        replay(mock, service);

        return mock;
    }

    private ConventionUnknownHandler conventionUnknownHandler() {
        return conventionUnknownHandler(null);
    }

    private ConventionUnknownHandler conventionUnknownHandler(final ServletContext servletContext) {
        final String defaultParentPackageName = "DEFAULT PARENT PACKAGE NAME";

        final Configuration configuration = configuration(defaultParentPackageName);
        final Container container = container();

        return new ConventionUnknownHandler(configuration, null, servletContext, container, defaultParentPackageName,
                null, null);
    }

    private Map<String, ResultTypeConfig> defaultResultsByExtension() {
        final Iterator<String> extensions = createMock(Iterator.class);
        final Set<String> keys = createMock(Set.class);
        final Map<String, ResultTypeConfig> mock = createMock(Map.class);

        expect(extensions.hasNext()).andReturn(true).times(3).andReturn(false);
        expect(extensions.next()).andReturn("default_extension").andReturn("non_default_extension")
                .andReturn("some_other_extension");

        expect(keys.iterator()).andReturn(extensions);

        expect(mock.keySet()).andReturn(keys);

        replay(extensions);
        replay(keys);
        replay(mock);

        return mock;
    }

    private PackageConfig packageConfiguration() {
        final PackageConfig mock = createNiceMock(PackageConfig.class);

        replay(mock);

        return mock;
    }
}
