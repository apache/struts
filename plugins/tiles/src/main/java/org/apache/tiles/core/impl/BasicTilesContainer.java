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
package org.apache.tiles.core.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.BasicAttributeContext;
import org.apache.tiles.api.Definition;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.preparer.ViewPreparer;
import org.apache.tiles.core.definition.DefinitionsFactory;
import org.apache.tiles.core.definition.NoSuchDefinitionException;
import org.apache.tiles.core.evaluator.AttributeEvaluator;
import org.apache.tiles.core.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.core.evaluator.AttributeEvaluatorFactoryAware;
import org.apache.tiles.core.prepare.factory.NoSuchPreparerException;
import org.apache.tiles.core.prepare.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.CannotRenderException;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.render.RendererFactory;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

/**
 * Basic implementation of the tiles container interface.
 * In most cases, this container will be customized by
 * injecting customized services, not necessarily by
 * override the container
 *
 * @since 2.0
 */
public class BasicTilesContainer implements TilesContainer, AttributeEvaluatorFactoryAware {

    /**
     * Name used to store attribute context stack.
     */
    private static final String ATTRIBUTE_CONTEXT_STACK =
        "org.apache.tiles.AttributeContext.STACK";

    /**
     * Log instance for all BasicTilesContainer
     * instances.
     */
    private static final Logger LOG = LogManager.getLogger(BasicTilesContainer.class);

    /**
     * The Tiles application context object.
     */
    private ApplicationContext context;

    /**
     * The definitions factory.
     */
    private DefinitionsFactory definitionsFactory;

    /**
     * The preparer factory.
     */
    private PreparerFactory preparerFactory;

    /**
     * The renderer factory.
     */
    private RendererFactory rendererFactory;

    /**
     * The attribute evaluator.
     */
    private AttributeEvaluatorFactory attributeEvaluatorFactory;

    /**
     * {@inheritDoc}
     */
    public AttributeContext startContext(Request request) {
        AttributeContext context = new BasicAttributeContext();
        Deque<AttributeContext> stack = getContextStack(request);
        if (!stack.isEmpty()) {
            AttributeContext parent = stack.peek();
            context.inheritCascadedAttributes(parent);
        }
        stack.push(context);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    public void endContext(Request request) {
        popContext(request);
    }

    /**
     * {@inheritDoc}
     */
    public void renderContext(Request request) {
        AttributeContext attributeContext = getAttributeContext(request);

        render(request, attributeContext);
    }

    /**
     * Returns the Tiles application context used by this container.
     *
     * @return the application context for this container.
     */
    public ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * Sets the Tiles application context to use.
     *
     * @param context The Tiles application context.
     */
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public AttributeContext getAttributeContext(Request request) {
        AttributeContext context = getContext(request);
        if (context == null) {
            context = new BasicAttributeContext();
            pushContext(context, request);
        }
        return context;

    }

    /**
     * Set the definitions factory. This method first ensures
     * that the container has not yet been initialized.
     *
     * @param definitionsFactory the definitions factory for this instance.
     */
    public void setDefinitionsFactory(DefinitionsFactory definitionsFactory) {
        this.definitionsFactory = definitionsFactory;
    }

    /**
     * Set the preparerInstance factory.  This method first ensures
     * that the container has not yet been initialized.
     *
     * @param preparerFactory the preparerInstance factory for this conainer.
     */
    public void setPreparerFactory(PreparerFactory preparerFactory) {
        this.preparerFactory = preparerFactory;
    }

    /**
     * Sets the renderer instance factory.
     *
     * @param rendererFactory the renderer instance factory for this container.
     * @since 2.1.0
     */
    public void setRendererFactory(RendererFactory rendererFactory) {
        this.rendererFactory = rendererFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributeEvaluatorFactory(
        AttributeEvaluatorFactory attributeEvaluatorFactory) {
        this.attributeEvaluatorFactory = attributeEvaluatorFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void prepare(String preparer, Request request) {
        prepare(request, preparer, false);
    }

    /**
     * {@inheritDoc}
     */
    public void render(String definitionName, Request request) {
        LOG.debug("Render request received for definition '{}'", definitionName);

        Definition definition = getDefinition(definitionName, request);

        if (definition == null) {
            throw new NoSuchDefinitionException("Unable to find the definition '" + definitionName + "'");
        }

        render(definition, request);
    }

    /**
     * Renders the specified definition.
     *
     * @param definition The definition to render.
     * @param request    The request context.
     * @since 2.1.3
     */
    public void render(Definition definition, Request request) {
        AttributeContext originalContext = getAttributeContext(request);
        BasicAttributeContext subContext = new BasicAttributeContext(originalContext);
        subContext.inherit(definition);

        pushContext(subContext, request);

        try {
            render(request, subContext);
        } finally {
            popContext(request);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void render(Attribute attr, Request request)
        throws IOException {
        if (attr == null) {
            throw new CannotRenderException("Cannot render a null attribute");
        }

        if (attr.isPermitted(request)) {
            Renderer renderer = rendererFactory.getRenderer(attr.getRenderer());
            Object value = evaluate(attr, request);
            if (!(value instanceof String)) {
                throw new CannotRenderException(
                    "Cannot render an attribute that is not a string, toString returns: "
                        + value);
            }
            renderer.render((String) value, request);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(Attribute attribute, Request request) {
        AttributeEvaluator evaluator = attributeEvaluatorFactory.getAttributeEvaluator(attribute);
        return evaluator.evaluate(attribute, request);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValidDefinition(String definitionName, Request request) {
        try {
            Definition definition = getDefinition(definitionName, request);
            return definition != null;
        } catch (NoSuchDefinitionException nsde) {
            LOG.debug("Cannot find definition '{}'", definitionName);
            LOG.debug("Exception related to the not found definition", nsde);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Definition getDefinition(String definitionName, Request request) {
        return definitionsFactory.getDefinition(definitionName, request);
    }

    /**
     * Returns the context stack.
     *
     * @param tilesContext The Tiles context object to use.
     * @return The needed stack of contexts.
     * @since 2.0.6
     */
    @SuppressWarnings("unchecked")
    protected Deque<AttributeContext> getContextStack(Request tilesContext) {
        Map<String, Object> requestScope = tilesContext.getContext("request");
        Deque<AttributeContext> contextStack = (Deque<AttributeContext>) requestScope
            .get(ATTRIBUTE_CONTEXT_STACK);
        if (contextStack == null) {
            contextStack = new LinkedList<>();
            requestScope.put(ATTRIBUTE_CONTEXT_STACK, contextStack);
        }

        return contextStack;
    }

    /**
     * Pushes a context object in the stack.
     *
     * @param context      The context to push.
     * @param tilesContext The Tiles context object to use.
     * @since 2.0.6
     */
    protected void pushContext(AttributeContext context,
                               Request tilesContext) {
        Deque<AttributeContext> contextStack = getContextStack(tilesContext);
        contextStack.push(context);
    }

    /**
     * Pops a context object out of the stack.
     *
     * @param tilesContext The Tiles context object to use.
     * @return The popped context object.
     * @since 2.0.6
     */
    protected AttributeContext popContext(Request tilesContext) {
        Deque<AttributeContext> contextStack = getContextStack(tilesContext);
        return contextStack.pop();
    }

    /**
     * Get attribute context from request.
     *
     * @param tilesContext current Tiles application context.
     * @return BasicAttributeContext or null if context is not found.
     * @since 2.0.6
     */
    protected AttributeContext getContext(Request tilesContext) {
        Deque<AttributeContext> contextStack = getContextStack(tilesContext);
        if (!contextStack.isEmpty()) {
            return contextStack.peek();
        }
        return null;
    }

    /**
     * Execute a preparer.
     *
     * @param context       The request context.
     * @param preparerName  The name of the preparer.
     * @param ignoreMissing If <code>true</code> if the preparer is not found,
     *                      it ignores the problem.
     * @throws NoSuchPreparerException If the preparer is not found (and
     *                                 <code>ignoreMissing</code> is not set) or if the preparer itself threw an
     *                                 exception.
     */
    private void prepare(Request context, String preparerName, boolean ignoreMissing) {

        LOG.debug("Prepare request received for '{}'", preparerName);

        ViewPreparer preparer = preparerFactory.getPreparer(preparerName, context);
        if (preparer == null && ignoreMissing) {
            return;
        }

        if (preparer == null) {
            throw new NoSuchPreparerException("Preparer '" + preparerName + " not found");
        }

        AttributeContext attributeContext = getContext(context);

        preparer.execute(context, attributeContext);
    }

    /**
     * Renders the specified attribute context.
     *
     * @param request          The request context.
     * @param attributeContext The context to render.
     * @throws InvalidTemplateException If the template is not valid.
     * @throws CannotRenderException    If something goes wrong during rendering.
     * @since 2.1.3
     */
    protected void render(Request request,
                          AttributeContext attributeContext) {

        try {
            if (attributeContext.getPreparer() != null) {
                prepare(request, attributeContext.getPreparer(), true);
            }

            render(attributeContext.getTemplateAttribute(), request);
        } catch (IOException e) {
            throw new CannotRenderException(e.getMessage(), e);
        }
    }
}
