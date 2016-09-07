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
import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.context.ChainedTilesRequestContextFactory;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.definition.pattern.DefinitionPatternMatcherFactory;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PrefixedPatternDefinitionResolver;
import org.apache.tiles.definition.pattern.regexp.RegexpDefinitionPatternMatcherFactory;
import org.apache.tiles.definition.pattern.wildcard.WildcardDefinitionPatternMatcherFactory;
import org.apache.tiles.el.ELAttributeEvaluator;
import org.apache.tiles.el.JspExpressionFactoryFactory;
import org.apache.tiles.el.TilesContextBeanELResolver;
import org.apache.tiles.el.TilesContextELResolver;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.factory.TilesContainerFactoryException;
import org.apache.tiles.freemarker.context.FreeMarkerTilesRequestContextFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.ognl.ApplicationScopeNestedObjectExtractor;
import org.apache.tiles.ognl.DelegatePropertyAccessor;
import org.apache.tiles.ognl.NestedObjectDelegatePropertyAccessor;
import org.apache.tiles.ognl.OGNLAttributeEvaluator;
import org.apache.tiles.ognl.PropertyAccessorDelegateFactory;
import org.apache.tiles.ognl.RequestScopeNestedObjectExtractor;
import org.apache.tiles.ognl.SessionScopeNestedObjectExtractor;
import org.apache.tiles.ognl.TilesApplicationContextNestedObjectExtractor;
import org.apache.tiles.ognl.TilesContextPropertyAccessorDelegateFactory;
import org.apache.tiles.renderer.AttributeRenderer;
import org.apache.tiles.renderer.TypeDetectingAttributeRenderer;
import org.apache.tiles.renderer.impl.BasicRendererFactory;
import org.apache.tiles.renderer.impl.ChainedDelegateAttributeRenderer;
import org.apache.tiles.servlet.context.ServletUtil;
import org.apache.tiles.util.URLUtil;

import com.opensymphony.xwork2.util.TextParseUtil;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dedicated Struts factory to build Tiles container with support for:
 * - Freemarker
 * - I18N using Struts resource bundles
 * - S2 ro access Struts' ValueStack
 * - OGNL
 * - EL
 *
 * If you need additional features create your own listener and factory,
 * you can base on code from Tiles' CompleteAutoloadTilesContainerFactory
 */
public class StrutsTilesContainerFactory extends BasicTilesContainerFactory {

    /**
     * The freemarker renderer name.
     */
    public static final String FREEMARKER_RENDERER_NAME = "freemarker";

    /**
     * Supported pattern types
     */
    public static final String PATTERN_WILDCARD = "WILDCARD";
    public static final String PATTERN_REGEXP = "REGEXP";

    /**
     * Default pattern to be used to collect Tiles definitions if user didn't configure any
     */
    public static final String TILES_DEFAULT_PATTERN = "tiles*.xml";

    /**
     * Supported expression languages
     */
    public static final String OGNL = "OGNL";
    public static final String EL = "EL";
    public static final String S2 = "S2";
    public static final String I18N = "I18N";

    @Override
    protected BasicTilesContainer instantiateContainer(TilesApplicationContext applicationContext) {
        CachingTilesContainer tilesContainer = new CachingTilesContainer();
        ServletContext servletContext = (ServletContext) applicationContext.getContext();
        ServletUtil.setContainer(servletContext, tilesContainer);
        return tilesContainer;
    }

    @Override
    protected List<TilesRequestContextFactory> getTilesRequestContextFactoriesToBeChained(ChainedTilesRequestContextFactory parent) {

        List<TilesRequestContextFactory> factories = super.getTilesRequestContextFactoriesToBeChained(parent);

        registerRequestContextFactory(FreeMarkerTilesRequestContextFactory.class.getName(), factories, parent);

        return factories;
    }

    @Override
    protected void registerAttributeRenderers(
            BasicRendererFactory rendererFactory,
            TilesApplicationContext applicationContext,
            TilesRequestContextFactory contextFactory,
            TilesContainer container,
            AttributeEvaluatorFactory attributeEvaluatorFactory) {

        super.registerAttributeRenderers(
                rendererFactory,
                applicationContext,
                contextFactory,
                container,
                attributeEvaluatorFactory);

        StrutsFreeMarkerAttributeRenderer freemarkerRenderer = new StrutsFreeMarkerAttributeRenderer();
        freemarkerRenderer.setApplicationContext(applicationContext);
        freemarkerRenderer.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        freemarkerRenderer.setRequestContextFactory(contextFactory);

        rendererFactory.registerRenderer(FREEMARKER_RENDERER_NAME, freemarkerRenderer);
    }

    @Override
    protected AttributeRenderer createDefaultAttributeRenderer(
            BasicRendererFactory rendererFactory,
            TilesApplicationContext applicationContext,
            TilesRequestContextFactory contextFactory,
            TilesContainer container,
            AttributeEvaluatorFactory attributeEvaluatorFactory) {

        ChainedDelegateAttributeRenderer retValue = new ChainedDelegateAttributeRenderer();

        retValue.addAttributeRenderer((TypeDetectingAttributeRenderer) rendererFactory
                .getRenderer(DEFINITION_RENDERER_NAME));
        retValue.addAttributeRenderer((TypeDetectingAttributeRenderer) rendererFactory
                .getRenderer(FREEMARKER_RENDERER_NAME));
        retValue.addAttributeRenderer((TypeDetectingAttributeRenderer) rendererFactory
                .getRenderer(TEMPLATE_RENDERER_NAME));
        retValue.addAttributeRenderer((TypeDetectingAttributeRenderer) rendererFactory
                .getRenderer(STRING_RENDERER_NAME));

        retValue.setApplicationContext(applicationContext);
        retValue.setRequestContextFactory(contextFactory);
        retValue.setAttributeEvaluatorFactory(attributeEvaluatorFactory);

        return retValue;
    }

    @Override
    protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(
            TilesApplicationContext applicationContext,
            TilesRequestContextFactory contextFactory,
            LocaleResolver resolver) {

        BasicAttributeEvaluatorFactory attributeEvaluatorFactory = new BasicAttributeEvaluatorFactory(new DirectAttributeEvaluator());
        attributeEvaluatorFactory.registerAttributeEvaluator(S2, createStrutsEvaluator());
        attributeEvaluatorFactory.registerAttributeEvaluator(I18N, createI18NEvaluator());
        attributeEvaluatorFactory.registerAttributeEvaluator(OGNL, createOGNLEvaluator());

        ELAttributeEvaluator elEvaluator = createELEvaluator(applicationContext);
        if (elEvaluator != null) {
            attributeEvaluatorFactory.registerAttributeEvaluator(EL, elEvaluator);
        }

        return attributeEvaluatorFactory;
    }

    @Override
    protected <T> PatternDefinitionResolver<T> createPatternDefinitionResolver(Class<T> customizationKeyClass) {

        DefinitionPatternMatcherFactory wildcardFactory = new WildcardDefinitionPatternMatcherFactory();
        DefinitionPatternMatcherFactory regexpFactory = new RegexpDefinitionPatternMatcherFactory();

        PrefixedPatternDefinitionResolver<T> resolver = new PrefixedPatternDefinitionResolver<T>();
        resolver.registerDefinitionPatternMatcherFactory(PATTERN_WILDCARD, wildcardFactory);
        resolver.registerDefinitionPatternMatcherFactory(PATTERN_REGEXP, regexpFactory);

        return resolver;
    }

    @Override
    protected List<URL> getSourceURLs(TilesApplicationContext applicationContext, TilesRequestContextFactory contextFactory) {
        try {
            Set<URL> finalSet = new HashSet<URL>();

            Set<String> definitions = getTilesDefinitions(applicationContext.getInitParams());
            for (String definition : definitions) {
                finalSet.addAll(applicationContext.getResources(definition));
            }

            return URLUtil.getBaseTilesDefinitionURLs(finalSet);
        } catch (IOException e) {
            throw new DefinitionsFactoryException("Cannot load definition URLs", e);
        }
    }

    protected Set<String> getTilesDefinitions(Map<String, String> params) {
        if (params.containsKey(DefinitionsFactory.DEFINITIONS_CONFIG)) {
            return TextParseUtil.commaDelimitedStringToSet(params.get(DefinitionsFactory.DEFINITIONS_CONFIG));
        }
        if (params.containsKey(BasicTilesContainer.DEFINITIONS_CONFIG)) {
            return TextParseUtil.commaDelimitedStringToSet(params.get(BasicTilesContainer.DEFINITIONS_CONFIG));
        }
        return TextParseUtil.commaDelimitedStringToSet(TILES_DEFAULT_PATTERN);
    }

    protected ELAttributeEvaluator createELEvaluator(TilesApplicationContext applicationContext) {

        ELAttributeEvaluator evaluator = new ELAttributeEvaluator();
        evaluator.setApplicationContext(applicationContext);
        JspExpressionFactoryFactory efFactory = new JspExpressionFactoryFactory();
        efFactory.setApplicationContext(applicationContext);
        evaluator.setExpressionFactory(efFactory.getExpressionFactory());

        ELResolver elResolver = new CompositeELResolver() {
            {
                add(new TilesContextELResolver());
                add(new TilesContextBeanELResolver());
                add(new ArrayELResolver(false));
                add(new ListELResolver(false));
                add(new MapELResolver(false));
                add(new ResourceBundleELResolver());
                add(new BeanELResolver(false));
            }
        };

        evaluator.setResolver(elResolver);

        return evaluator;
    }

    protected StrutsAttributeEvaluator createStrutsEvaluator() {
        return new StrutsAttributeEvaluator();
    }

    protected I18NAttributeEvaluator createI18NEvaluator() {
        return new I18NAttributeEvaluator();
    }

    protected OGNLAttributeEvaluator createOGNLEvaluator() {
        try {
            PropertyAccessor objectPropertyAccessor = OgnlRuntime.getPropertyAccessor(Object.class);
            PropertyAccessor mapPropertyAccessor = OgnlRuntime.getPropertyAccessor(Map.class);
            PropertyAccessor applicationContextPropertyAccessor =
                    new NestedObjectDelegatePropertyAccessor<TilesRequestContext>(
                            new TilesApplicationContextNestedObjectExtractor(),
                            objectPropertyAccessor);
            PropertyAccessor requestScopePropertyAccessor =
                    new NestedObjectDelegatePropertyAccessor<TilesRequestContext>(
                            new RequestScopeNestedObjectExtractor(), mapPropertyAccessor);
            PropertyAccessor sessionScopePropertyAccessor =
                    new NestedObjectDelegatePropertyAccessor<TilesRequestContext>(
                            new SessionScopeNestedObjectExtractor(), mapPropertyAccessor);
            PropertyAccessor applicationScopePropertyAccessor =
                    new NestedObjectDelegatePropertyAccessor<TilesRequestContext>(
                            new ApplicationScopeNestedObjectExtractor(), mapPropertyAccessor);
            PropertyAccessorDelegateFactory<TilesRequestContext> factory =
                    new TilesContextPropertyAccessorDelegateFactory(
                            objectPropertyAccessor, applicationContextPropertyAccessor,
                            requestScopePropertyAccessor, sessionScopePropertyAccessor,
                            applicationScopePropertyAccessor);
            PropertyAccessor tilesRequestAccessor = new DelegatePropertyAccessor<TilesRequestContext>(factory);
            OgnlRuntime.setPropertyAccessor(TilesRequestContext.class, tilesRequestAccessor);
            return new OGNLAttributeEvaluator();
        } catch (OgnlException e) {
            throw new TilesContainerFactoryException("Cannot initialize OGNL evaluator", e);
        }
    }

}