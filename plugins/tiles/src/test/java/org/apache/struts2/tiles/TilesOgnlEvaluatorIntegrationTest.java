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
package org.apache.struts2.tiles;

import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Container;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Expression;
import org.apache.tiles.core.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.core.evaluator.EvaluationException;
import org.apache.tiles.core.impl.BasicTilesContainer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.StringRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Regression coverage for the two Tiles expression-language registrations.
 */
@SuppressWarnings("removal")
public class TilesOgnlEvaluatorIntegrationTest {

    private org.apache.struts2.dispatcher.Dispatcher dispatcher;
    private Container strutsContainer;
    private ValueStack valueStack;
    private Request tilesRequest;
    private BasicTilesContainer tilesContainer;
    private BasicRendererFactory rendererFactory;
    private StringWriter renderedOutput;

    @Before
    public void setUp() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        dispatcher = StrutsTestCaseHelper.initDispatcher(servletContext, Map.of(
            StrutsConstants.STRUTS_ALLOWLIST_ENABLE, Boolean.TRUE.toString()
        ));
        strutsContainer = dispatcher.getContainer();
        valueStack = strutsContainer.getInstance(ValueStackFactory.class).createValueStack();

        MockHttpServletRequest servletRequest = new MockHttpServletRequest(servletContext);
        servletRequest.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, valueStack);
        ApplicationContext applicationContext = new ServletApplicationContext(servletContext);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        renderedOutput = new StringWriter();
        org.mockito.Mockito.when(servletResponse.getWriter()).thenReturn(new PrintWriter(renderedOutput));
        tilesRequest = new ServletRequest(applicationContext, servletRequest, servletResponse);

        StrutsTilesContainerFactory factory = new StrutsTilesContainerFactory();
        AttributeEvaluatorFactory evaluators = createAttributeEvaluatorFactoryWithoutEl(factory, applicationContext);
        rendererFactory = new BasicRendererFactory();
        rendererFactory.registerRenderer("string", new StringRenderer());
        tilesContainer = new BasicTilesContainer();
        tilesContainer.setAttributeEvaluatorFactory(evaluators);
        tilesContainer.setRendererFactory(rendererFactory);
    }

    @After
    public void tearDown() {
        StrutsTestCaseHelper.tearDown(dispatcher);
    }

    @Test
    public void ognlLanguageFailsClosedWithoutEvaluatingExpression() {
        Marker marker = new Marker();
        tilesRequest.getContext("request").put("marker", marker);

        Attribute attribute = expression("marker.touch()", StrutsTilesContainerFactory.OGNL);

        EvaluationException exception = assertThrows(EvaluationException.class,
            () -> tilesContainer.evaluate(attribute, tilesRequest));

        assertEquals(DisabledOgnlAttributeEvaluator.DISABLED_MESSAGE, exception.getMessage());
        assertFalse("The disabled evaluator must not invoke a method from the expression", marker.touched);
        assertFalse("The exception must not disclose the expression", exception.getMessage().contains("marker.touch()"));
    }

    @Test
    public void legacyOgnlLanguagePreservesRawEvaluationWhenExplicitlyEnabled() throws OgnlException {
        PropertyAccessor originalAccessor = getRequestAccessorOrNull();
        org.apache.struts2.dispatcher.Dispatcher legacyDispatcher = null;
        try {
            EvaluationException disabled = assertThrows(EvaluationException.class, () -> tilesContainer.evaluate(
                expression("marker.touch()", StrutsTilesContainerFactory.OGNL), tilesRequest));
            assertEquals(DisabledOgnlAttributeEvaluator.DISABLED_MESSAGE, disabled.getMessage());

            MockServletContext legacyServletContext = new MockServletContext();
            legacyDispatcher = StrutsTestCaseHelper.initDispatcher(legacyServletContext, Map.of(
                "config", "struts-default.xml,org/apache/struts2/tiles/struts-tiles-legacy.xml",
                StrutsConstants.STRUTS_ALLOWLIST_ENABLE, Boolean.TRUE.toString()));
            assertEquals(Boolean.TRUE.toString(), legacyDispatcher.getConfigurationManager().getConfiguration()
                .getContainer().getInstance(
                String.class, TilesConstants.STRUTS_TILES_OGNL_LEGACY_ENABLED));
            ApplicationContext legacyApplicationContext = new ServletApplicationContext(legacyServletContext);
            MockHttpServletRequest legacyServletRequest = new MockHttpServletRequest(legacyServletContext);
            Request legacyTilesRequest = new ServletRequest(
                legacyApplicationContext, legacyServletRequest, mock(HttpServletResponse.class));
            Marker marker = new Marker();
            legacyTilesRequest.getContext("request").put("marker", marker);
            TrackingFactory legacyFactory = new TrackingFactory();
            BasicTilesContainer legacyTilesContainer = new BasicTilesContainer();
            legacyTilesContainer.setAttributeEvaluatorFactory(createAttributeEvaluatorFactoryWithoutEl(
                legacyFactory, legacyApplicationContext));

            assertEquals("The raw evaluator must not be built during Tiles construction",
                0, legacyFactory.rawEvaluatorCreations);
            assertEquals("touched", legacyTilesContainer.evaluate(
                expression("marker.touch()", StrutsTilesContainerFactory.OGNL), legacyTilesRequest));
            assertEquals("touched", legacyTilesContainer.evaluate(
                expression("marker.touch()", StrutsTilesContainerFactory.OGNL), legacyTilesRequest));
            assertTrue("The explicitly enabled legacy evaluator must preserve existing behavior", marker.touched);
            assertEquals("The compatibility constant must be resolved once per evaluator", 1,
                legacyFactory.configurationResolutions);
            assertEquals("The raw evaluator must be constructed once per evaluator", 1,
                legacyFactory.rawEvaluatorCreations);
            assertEquals("The migration warning must be emitted once per evaluator lifecycle", 1,
                legacyFactory.legacyWarnings);

            EvaluationException stillDisabled = assertThrows(EvaluationException.class, () -> tilesContainer.evaluate(
                expression("marker.touch()", StrutsTilesContainerFactory.OGNL), tilesRequest));
            assertEquals(DisabledOgnlAttributeEvaluator.DISABLED_MESSAGE, stillDisabled.getMessage());
        } finally {
            StrutsTestCaseHelper.tearDown(legacyDispatcher);
            OgnlRuntime.setPropertyAccessor(Request.class, originalAccessor);
        }
    }

    @Test
    public void missingDispatcherFailsClosedAndCachesTheDecision() {
        MockServletContext servletContext = new MockServletContext();
        ApplicationContext applicationContext = new ServletApplicationContext(servletContext);
        Request request = new ServletRequest(
            applicationContext, new MockHttpServletRequest(servletContext), mock(HttpServletResponse.class));
        TrackingFactory factory = new TrackingFactory();
        AttributeEvaluatorFactory evaluators = createAttributeEvaluatorFactoryWithoutEl(factory, applicationContext);

        EvaluationException first = assertThrows(EvaluationException.class,
            () -> evaluators.getAttributeEvaluator(StrutsTilesContainerFactory.OGNL).evaluate("ignored", request));
        EvaluationException second = assertThrows(EvaluationException.class,
            () -> evaluators.getAttributeEvaluator(StrutsTilesContainerFactory.OGNL).evaluate("ignored", request));

        assertEquals(DisabledOgnlAttributeEvaluator.DISABLED_MESSAGE, first.getMessage());
        assertEquals(DisabledOgnlAttributeEvaluator.DISABLED_MESSAGE, second.getMessage());
        assertEquals(1, factory.configurationResolutions);
        assertEquals(0, factory.rawEvaluatorCreations);
    }

    @Test
    public void nonServletRequestFailsClosedWithoutExposingEnvironmentFailure() {
        TrackingFactory factory = new TrackingFactory();
        AttributeEvaluatorFactory evaluators = createAttributeEvaluatorFactoryWithoutEl(
            factory, tilesRequest.getApplicationContext());

        EvaluationException exception = assertThrows(EvaluationException.class,
            () -> evaluators.getAttributeEvaluator(StrutsTilesContainerFactory.OGNL)
                .evaluate("sensitive-expression", mock(Request.class)));

        assertEquals(DisabledOgnlAttributeEvaluator.DISABLED_MESSAGE, exception.getMessage());
        assertFalse(exception.getMessage().contains("sensitive-expression"));
        assertEquals(1, factory.configurationResolutions);
        assertEquals(0, factory.rawEvaluatorCreations);
    }

    @Test
    public void ordinaryDirectTilesAttributeRemainsUnaffected() throws Exception {
        Attribute attribute = new Attribute("ordinary value");
        attribute.setRenderer("string");

        assertEquals("ordinary value", tilesContainer.evaluate(attribute, tilesRequest));
        tilesContainer.render(attribute, tilesRequest);
        assertEquals("ordinary value", renderedOutput.toString());
    }

    @Test
    public void s2LanguageStillEvaluatesAgainstSecuredValueStack() {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "secured title");
        valueStack.push(model);

        assertEquals(
            "secured title",
            tilesContainer.evaluate(expression("title", StrutsTilesContainerFactory.S2), tilesRequest)
        );
    }

    private static PropertyAccessor getRequestAccessorOrNull() {
        try {
            return OgnlRuntime.getPropertyAccessor(Request.class);
        } catch (OgnlException ignored) {
            return null;
        }
    }

    private static AttributeEvaluatorFactory createAttributeEvaluatorFactoryWithoutEl(
            StrutsTilesContainerFactory factory, ApplicationContext applicationContext) {
        JspFactory originalJspFactory = JspFactory.getDefaultFactory();
        try {
            JspFactory.setDefaultFactory(null);
            return factory.createAttributeEvaluatorFactory(
                applicationContext, factory.createLocaleResolver(applicationContext));
        } finally {
            JspFactory.setDefaultFactory(originalJspFactory);
        }
    }

    private Attribute expression(String value, String language) {
        Attribute attribute = new Attribute();
        attribute.setExpressionObject(new Expression(value, language));
        return attribute;
    }

    public static class Marker {
        private boolean touched;

        public String touch() {
            touched = true;
            return "touched";
        }
    }

    private static class TrackingFactory extends StrutsTilesContainerFactory {
        private int configurationResolutions;
        private int rawEvaluatorCreations;
        private int legacyWarnings;

        @Override
        boolean isLegacyOgnlEnabled(Request request) {
            configurationResolutions++;
            return super.isLegacyOgnlEnabled(request);
        }

        @Override
        protected org.apache.tiles.ognl.OGNLAttributeEvaluator createOGNLEvaluator() {
            rawEvaluatorCreations++;
            return super.createOGNLEvaluator();
        }

        @Override
        void logLegacyOgnlWarning() {
            legacyWarnings++;
        }
    }
}
