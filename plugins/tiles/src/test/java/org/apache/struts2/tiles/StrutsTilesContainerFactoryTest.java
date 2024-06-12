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

import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.core.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.core.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.core.locale.LocaleResolver;
import org.apache.tiles.core.prepare.factory.BasicPreparerFactory;
import org.apache.tiles.core.prepare.factory.PreparerFactory;
import org.apache.tiles.ognl.OGNLAttributeEvaluator;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.Renderer;
import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StrutsTilesContainerFactoryTest {

    private StrutsTilesContainerFactory factory;
    private ApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {
        applicationContext = mock(ApplicationContext.class);
        factory = new StrutsTilesContainerFactory();
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
    public void createAttributeEvaluatorFactory() {
        LocaleResolver resolver = factory.createLocaleResolver(applicationContext);
        // explicitly disables support for EL
        JspFactory.setDefaultFactory(null);

        AttributeEvaluatorFactory attributeEvaluatorFactory = factory.createAttributeEvaluatorFactory(applicationContext, resolver);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator((String) null) instanceof DirectAttributeEvaluator);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator("S2") instanceof StrutsAttributeEvaluator);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator("OGNL") instanceof OGNLAttributeEvaluator);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator("I18N") instanceof I18NAttributeEvaluator);
        assertTrue("The class of the evaluator is not correct",
                attributeEvaluatorFactory.getAttributeEvaluator("EL") instanceof DirectAttributeEvaluator);
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

}