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
package org.apache.struts2.convention;

import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.RuntimeConfiguration;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultTypeConfig;
import org.apache.struts2.convention.actions.NoAnnotationAction;
import org.apache.struts2.inject.Container;
import junit.framework.TestCase;
import org.apache.struts2.result.ServletDispatcherResult;

import jakarta.servlet.ServletContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.*;

public class ConventionUnknownHandlerTest extends TestCase {

    private static final String DEFAULT_PARENT = "convention-default";

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

    public void testUnknownActionUsesScopedResultPathOfDefaultParentPackage() throws Exception {
        List<String> requested = new ArrayList<>();
        ConventionUnknownHandler handler = scopedHandler(recordingServletContext(requested),
                packageWithResultPath(DEFAULT_PARENT, "", "/WEB-INF/scoped/"), null);

        handler.handleUnknownAction("", "does-not-exist");

        assertFalse(requested.isEmpty());
        for (String path : requested) {
            assertTrue(path, path.startsWith("/WEB-INF/scoped/does-not-exist"));
        }
    }

    public void testUnknownActionWithoutOverrideUsesGlobalResultPath() throws Exception {
        List<String> requested = new ArrayList<>();
        ConventionUnknownHandler handler = scopedHandler(recordingServletContext(requested),
                new PackageConfig.Builder(DEFAULT_PARENT).build(), null);

        handler.handleUnknownAction("", "does-not-exist");

        assertFalse(requested.isEmpty());
        for (String path : requested) {
            assertTrue(path, path.startsWith("/WEB-INF/content/does-not-exist"));
        }
    }

    public void testUnknownResultUsesScopedResultPathOfActionPackage() throws Exception {
        List<String> requested = new ArrayList<>();
        PackageConfig actionPackage = packageWithResultPath("admin", "/admin", "/WEB-INF/scoped/");
        ConventionUnknownHandler handler = scopedHandler(recordingServletContext(requested),
                new PackageConfig.Builder(DEFAULT_PARENT).build(), actionPackage);
        ActionConfig actionConfig = new ActionConfig.Builder("admin", "foo", NoAnnotationAction.class.getName()).build();

        handler.handleUnknownResult(null, "foo", actionConfig, "success");

        assertFalse(requested.isEmpty());
        for (String path : requested) {
            assertTrue(path, path.startsWith("/WEB-INF/scoped/admin/"));
        }
    }

    public void testDeterminePathAppendsSlashToScopedResultPath() {
        PackageConfig parent = packageWithResultPath(DEFAULT_PARENT, "", "/WEB-INF/scoped");
        ConventionUnknownHandler handler = scopedHandler(null, parent, null);

        assertEquals("/WEB-INF/scoped/admin/", handler.determinePath(null, parent, "/admin"));
    }

    private ServletContext recordingServletContext(List<String> requested) throws Exception {
        ServletContext servletContext = createNiceMock(ServletContext.class);
        expect(servletContext.getResource(anyString())).andAnswer(() -> {
            requested.add((String) getCurrentArguments()[0]);
            return null;
        }).anyTimes();
        replay(servletContext);
        return servletContext;
    }

    private static PackageConfig packageWithResultPath(String name, String namespace, String resultPath) {
        PackageConfig parent = new PackageConfig.Builder(name + "-base")
                .addScopedConstant(ConventionConstants.CONVENTION_RESULT_PATH, resultPath)
                .build();
        return new PackageConfig.Builder(name).namespace(namespace).addParent(parent).build();
    }

    private ConventionUnknownHandler scopedHandler(ServletContext servletContext, PackageConfig defaultParent,
                                                   PackageConfig actionPackage) {
        Configuration configuration = createNiceMock(Configuration.class);
        expect(configuration.getPackageConfig(DEFAULT_PARENT)).andStubReturn(defaultParent);
        if (actionPackage != null) {
            expect(configuration.getPackageConfig(actionPackage.getName())).andStubReturn(actionPackage);
        }
        RuntimeConfiguration runtime = createNiceMock(RuntimeConfiguration.class);
        expect(configuration.getRuntimeConfiguration()).andStubReturn(runtime);

        ConventionsServiceImpl service = new ConventionsServiceImpl("/WEB-INF/content/");
        service.setScopedConstantProvider(name -> "/WEB-INF/content/");
        Container container = createNiceMock(Container.class);
        expect(container.getInstance(String.class, ConventionConstants.CONVENTION_CONVENTIONS_SERVICE)).andStubReturn("test");
        expect(container.getInstance(ConventionsService.class, "test")).andStubReturn(service);

        replay(configuration, runtime, container);

        return new ConventionUnknownHandler(configuration, null, servletContext, container, DEFAULT_PARENT,
                "false", "-");
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
        ConventionsService service = createNiceMock(ConventionsService.class);

        expect(mock.getInstance(String.class, ConventionConstants.CONVENTION_CONVENTIONS_SERVICE)).andReturn("test");
        expect(mock.getInstance(ConventionsService.class, "test")).andStubReturn(service);

        expect(service.determineResultPath((ActionConfig) null, packageConfiguration)).andReturn("");
        Map<String, ResultTypeConfig> results = new HashMap<>();
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
