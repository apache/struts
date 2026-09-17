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

import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationManager;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.inject.Container;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.core.evaluator.AttributeEvaluator;
import org.apache.tiles.core.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.core.evaluator.EvaluationException;
import org.apache.tiles.core.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.core.locale.LocaleResolver;
import org.apache.tiles.core.prepare.factory.BasicPreparerFactory;
import org.apache.tiles.core.prepare.factory.PreparerFactory;
import org.apache.tiles.ognl.OGNLAttributeEvaluator;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.Renderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspFactory;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("removal")
public class StrutsTilesContainerFactoryTest {

    private StrutsTilesContainerFactory factory;
    private ApplicationContext applicationContext;
    private JspFactory originalJspFactory;

    @Before
    public void setUp() throws Exception {
        originalJspFactory = JspFactory.getDefaultFactory();
        applicationContext = mock(ApplicationContext.class);
        factory = new StrutsTilesContainerFactory();
    }

    @After
    public void tearDown() {
        JspFactory.setDefaultFactory(originalJspFactory);
    }

    @Test
    public void getSources() {
        ApplicationResource pathResource = new URLApplicationResource(
                "/org/apache/tiles/core/config/tiles-defs.xml",
                Objects.requireNonNull(getClass().getResource("/org/apache/tiles/core/config/tiles-defs.xml"))
        );
        ApplicationResource classpathResource = new URLApplicationResource(
                "/org/apache/tiles/core/config/tiles_defs1.xml",
                Objects.requireNonNull(getClass().getResource("/org/apache/tiles/core/config/tiles_defs1.xml"))
        );
        when(applicationContext.getInitParams()).thenReturn(Collections.emptyMap());
        when(applicationContext.getResources("*tiles*.xml")).thenReturn(Arrays.asList(pathResource, classpathResource));

        List<ApplicationResource> resources = factory.getSources(applicationContext);
        assertEquals("The urls list is not two-sized", 2, resources.size());
        assertEquals("The URL is not correct", pathResource, resources.get(0));
        assertEquals("The URL is not correct", classpathResource, resources.get(1));
    }

    @Test
    public void createAttributeEvaluatorFactoryDefersOgnlConfigurationUntilEvaluation() {
        TrackingFactory trackingFactory = new TrackingFactory();
        PropertyAccessor requestAccessorBefore = getRequestAccessorOrNull();
        LocaleResolver resolver = factory.createLocaleResolver(applicationContext);
        // explicitly disables support for EL
        JspFactory.setDefaultFactory(null);

        AttributeEvaluatorFactory attributeEvaluatorFactory = trackingFactory.createAttributeEvaluatorFactory(applicationContext, resolver);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator((String) null) instanceof DirectAttributeEvaluator);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator("S2") instanceof StrutsAttributeEvaluator);
        AttributeEvaluator ognlEvaluator = attributeEvaluatorFactory.getAttributeEvaluator("OGNL");
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator("I18N") instanceof I18NAttributeEvaluator);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator("EL") instanceof DirectAttributeEvaluator);
        assertEquals("The raw evaluator construction path must not run", 0, trackingFactory.rawEvaluatorCreations);
        assertEquals("Configuration must not be resolved during construction", 0, trackingFactory.configurationResolutions);
        assertSame("The default path must not mutate the shared Tiles Request accessor",
            requestAccessorBefore, getRequestAccessorOrNull());

        EvaluationException exception = assertThrows(EvaluationException.class,
            () -> ognlEvaluator.evaluate("ignored", mock(org.apache.tiles.request.Request.class)));
        assertEquals(DisabledOgnlAttributeEvaluator.DISABLED_MESSAGE, exception.getMessage());
        assertEquals(1, trackingFactory.configurationResolutions);
        assertEquals(0, trackingFactory.rawEvaluatorCreations);
        assertSame("The disabled path must not mutate the shared Tiles Request accessor",
            requestAccessorBefore, getRequestAccessorOrNull());
    }

    @Test
    public void createAttributeEvaluatorFactoryEnablesLegacyOgnlExplicitly() throws OgnlException {
        PropertyAccessor originalAccessor = getRequestAccessorOrNull();
        try {
            TrackingFactory trackingFactory = new TrackingFactory(true);
            LocaleResolver resolver = trackingFactory.createLocaleResolver(applicationContext);
            JspFactory.setDefaultFactory(null);

            AttributeEvaluatorFactory attributeEvaluatorFactory = trackingFactory.createAttributeEvaluatorFactory(
                applicationContext, resolver);

            assertTrue(attributeEvaluatorFactory.getAttributeEvaluator("OGNL") instanceof OGNLAttributeEvaluator);
            assertEquals(1, trackingFactory.rawEvaluatorCreations);
            assertTrue(OgnlRuntime.getPropertyAccessor(org.apache.tiles.request.Request.class)
                instanceof org.apache.tiles.ognl.DelegatePropertyAccessor);
        } finally {
            OgnlRuntime.setPropertyAccessor(org.apache.tiles.request.Request.class, originalAccessor);
        }
    }

    @Test
    public void publicFalseConstructorSelectsDisabledEvaluatorWithoutRequestLookup() {
        TrackingFactory trackingFactory = new TrackingFactory(false);
        JspFactory.setDefaultFactory(null);
        AttributeEvaluatorFactory evaluators = trackingFactory.createAttributeEvaluatorFactory(
            applicationContext, trackingFactory.createLocaleResolver(applicationContext));

        EvaluationException exception = assertThrows(EvaluationException.class,
            () -> evaluators.getAttributeEvaluator("OGNL").evaluate(
                "ignored", mock(org.apache.tiles.request.Request.class)));

        assertEquals(DisabledOgnlAttributeEvaluator.DISABLED_MESSAGE, exception.getMessage());
        assertEquals(0, trackingFactory.configurationResolutions);
        assertEquals(0, trackingFactory.rawEvaluatorCreations);
    }

    @Test
    public void lazyConfigurationIsResolvedOnceAndLegacyEvaluatorIsReused() throws OgnlException {
        PropertyAccessor originalAccessor = getRequestAccessorOrNull();
        try {
            TrackingFactory trackingFactory = new TrackingFactory();
            trackingFactory.configuredLegacyOgnlEnabled = true;
            JspFactory.setDefaultFactory(null);
            AttributeEvaluatorFactory evaluators = trackingFactory.createAttributeEvaluatorFactory(
                applicationContext, trackingFactory.createLocaleResolver(applicationContext));
            AttributeEvaluator evaluator = evaluators.getAttributeEvaluator("OGNL");

            assertEquals(0, trackingFactory.configurationResolutions);
            assertEquals(0, trackingFactory.rawEvaluatorCreations);
            assertEquals(1, evaluator.evaluate("1", mock(org.apache.tiles.request.Request.class)));
            assertEquals(2, evaluator.evaluate("2", mock(org.apache.tiles.request.Request.class)));

            assertEquals(1, trackingFactory.configurationResolutions);
            assertEquals(1, trackingFactory.rawEvaluatorCreations);
            assertEquals(1, trackingFactory.legacyWarningCount);
        } finally {
            OgnlRuntime.setPropertyAccessor(org.apache.tiles.request.Request.class, originalAccessor);
        }
    }

    @Test
    public void lazyConfigurationIsResolvedOnceUnderConcurrentFirstUse() throws Exception {
        TrackingFactory trackingFactory = new TrackingFactory();
        trackingFactory.blockConfigurationResolution = true;
        JspFactory.setDefaultFactory(null);
        AttributeEvaluator evaluator = trackingFactory.createAttributeEvaluatorFactory(
            applicationContext, trackingFactory.createLocaleResolver(applicationContext))
            .getAttributeEvaluator("OGNL");
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            Future<?> first = executor.submit(() -> assertThrows(EvaluationException.class,
                () -> evaluator.evaluate("ignored", mock(org.apache.tiles.request.Request.class))));
            assertTrue(trackingFactory.configurationResolutionEntered.await(10, TimeUnit.SECONDS));
            Future<?> second = executor.submit(() -> assertThrows(EvaluationException.class,
                () -> evaluator.evaluate("ignored", mock(org.apache.tiles.request.Request.class))));
            Future<?> third = executor.submit(() -> assertThrows(EvaluationException.class,
                () -> evaluator.evaluate("ignored", mock(org.apache.tiles.request.Request.class))));
            trackingFactory.continueConfigurationResolution.countDown();
            first.get(10, TimeUnit.SECONDS);
            second.get(10, TimeUnit.SECONDS);
            third.get(10, TimeUnit.SECONDS);

            assertEquals(1, trackingFactory.configurationResolutions);
            assertEquals(0, trackingFactory.rawEvaluatorCreations);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void requestScopedConfigurationUsesNormalBooleanParsing() {
        assertResolvedConfiguration("false", false);
        assertResolvedConfiguration("TrUe", true);
        assertResolvedConfiguration("not-a-boolean", false);
    }

    @Test
    public void customInitializerBooleanConstructorsArePublic() throws NoSuchMethodException {
        assertTrue(Modifier.isPublic(StrutsTilesContainerFactory.class.getConstructor(boolean.class).getModifiers()));
        assertTrue(Modifier.isPublic(StrutsTilesInitializer.class.getConstructor(boolean.class).getModifiers()));
    }

    @Test
    public void noArgInitializerPreservesLazyWebApplicationConfiguration() throws OgnlException {
        PropertyAccessor originalAccessor = getRequestAccessorOrNull();
        try {
            StrutsTilesContainerFactory initializedFactory = new ExposedInitializer().createFactory(applicationContext);
            JspFactory.setDefaultFactory(null);
            AttributeEvaluator evaluator = initializedFactory.createAttributeEvaluatorFactory(
                applicationContext, initializedFactory.createLocaleResolver(applicationContext))
                .getAttributeEvaluator("OGNL");

            assertEquals(1, evaluator.evaluate("1", createRequestWithConfiguredValue("true")));
        } finally {
            OgnlRuntime.setPropertyAccessor(org.apache.tiles.request.Request.class, originalAccessor);
        }
    }

    @Test
    public void legacyWarningIsLoggedOncePerFactoryConstruction() throws OgnlException {
        PropertyAccessor originalAccessor = getRequestAccessorOrNull();
        try {
            TrackingFactory trackingFactory = new TrackingFactory(true);
            JspFactory.setDefaultFactory(null);
            LocaleResolver resolver = trackingFactory.createLocaleResolver(applicationContext);
            trackingFactory.createAttributeEvaluatorFactory(applicationContext, resolver);
            trackingFactory.createAttributeEvaluatorFactory(applicationContext, resolver);

            assertEquals(1, trackingFactory.legacyWarningCount);
            assertEquals(StrutsTilesContainerFactory.LEGACY_OGNL_WARNING, trackingFactory.legacyWarningMessage);
        } finally {
            OgnlRuntime.setPropertyAccessor(org.apache.tiles.request.Request.class, originalAccessor);
        }
    }

    @Test
    public void createPreparerFactory() {
        PreparerFactory preparerFactory = factory.createPreparerFactory(applicationContext);
        assertTrue("The class of the preparer factory is not correct", preparerFactory instanceof BasicPreparerFactory);
    }

    @Test
    public void createDefaultAttributeRenderer() {
        TilesContainer container = mock(TilesContainer.class);
        AttributeEvaluatorFactory attributeEvaluatorFactory = mock(AttributeEvaluatorFactory.class);
        BasicRendererFactory rendererFactory = mock(BasicRendererFactory.class);
        Renderer stringRenderer = mock(Renderer.class);
        Renderer templateRenderer = mock(Renderer.class);
        Renderer definitionRenderer = mock(Renderer.class);

        when(rendererFactory.getRenderer("string")).thenReturn(stringRenderer);
        when(rendererFactory.getRenderer("template")).thenReturn(templateRenderer);
        when(rendererFactory.getRenderer("definition")).thenReturn(definitionRenderer);
        when(rendererFactory.getRenderer("freemarker")).thenReturn(definitionRenderer);

        Renderer renderer = factory.createDefaultAttributeRenderer(rendererFactory, applicationContext, container, attributeEvaluatorFactory);

        assertTrue("The default renderer class is not correct", renderer instanceof ChainedDelegateRenderer);
        verify(rendererFactory).getRenderer("string");
        verify(rendererFactory).getRenderer("template");
        verify(rendererFactory).getRenderer("definition");
        verify(rendererFactory).getRenderer("freemarker");
    }

    private static PropertyAccessor getRequestAccessorOrNull() {
        try {
            return OgnlRuntime.getPropertyAccessor(org.apache.tiles.request.Request.class);
        } catch (OgnlException ignored) {
            return null;
        }
    }

    private void assertResolvedConfiguration(String configuredValue, boolean expected) {
        assertEquals(expected, factory.isLegacyOgnlEnabled(createRequestWithConfiguredValue(configuredValue)));
    }

    private ServletRequest createRequestWithConfiguredValue(String configuredValue) {
        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = mock(Dispatcher.class);
        ConfigurationManager configurationManager = mock(ConfigurationManager.class);
        Configuration configuration = mock(Configuration.class);
        Container container = mock(Container.class);
        when(dispatcher.getConfigurationManager()).thenReturn(configurationManager);
        when(configurationManager.getConfiguration()).thenReturn(configuration);
        when(configuration.getContainer()).thenReturn(container);
        when(container.getInstance(String.class, TilesConstants.STRUTS_TILES_OGNL_LEGACY_ENABLED))
            .thenReturn(configuredValue);
        servletContext.setAttribute(StrutsStatics.SERVLET_DISPATCHER, dispatcher);
        ServletApplicationContext servletApplicationContext = new ServletApplicationContext(servletContext);
        return new ServletRequest(servletApplicationContext,
            new MockHttpServletRequest(servletContext), mock(HttpServletResponse.class));
    }

    private static class ExposedInitializer extends StrutsTilesInitializer {

        private StrutsTilesContainerFactory createFactory(ApplicationContext context) {
            return (StrutsTilesContainerFactory) createContainerFactory(context);
        }
    }

    private static class TrackingFactory extends StrutsTilesContainerFactory {
        private int rawEvaluatorCreations;
        private int legacyWarningCount;
        private String legacyWarningMessage;
        private final AtomicInteger resolutionCounter = new AtomicInteger();
        private final CountDownLatch configurationResolutionEntered = new CountDownLatch(1);
        private final CountDownLatch continueConfigurationResolution = new CountDownLatch(1);
        private boolean configuredLegacyOgnlEnabled;
        private boolean blockConfigurationResolution;
        private int configurationResolutions;

        private TrackingFactory() {
            super();
        }

        private TrackingFactory(boolean legacyOgnlEnabled) {
            super(legacyOgnlEnabled);
        }

        @Override
        boolean isLegacyOgnlEnabled(org.apache.tiles.request.Request request) {
            configurationResolutions = resolutionCounter.incrementAndGet();
            configurationResolutionEntered.countDown();
            if (blockConfigurationResolution) {
                try {
                    assertTrue(continueConfigurationResolution.await(10, TimeUnit.SECONDS));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                }
            }
            return configuredLegacyOgnlEnabled;
        }

        @Override
        protected OGNLAttributeEvaluator createOGNLEvaluator() {
            rawEvaluatorCreations++;
            return super.createOGNLEvaluator();
        }

        @Override
        void logLegacyOgnlWarning() {
            legacyWarningCount++;
            legacyWarningMessage = LEGACY_OGNL_WARNING;
        }
    }

}
