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
package org.apache.tiles.el;

import org.apache.tiles.core.evaluator.AbstractAttributeEvaluator;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;

import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;

/**
 * Evaluates string expression with typical EL syntax.<br>
 * You can use normal EL syntax, knowing the root objects are
 * {@link Request}, {@link ApplicationContext} and beans
 * contained in request, session and application scope.
 *
 * @since 2.2.1
 */
public class ELAttributeEvaluator extends AbstractAttributeEvaluator {

    /**
     * The EL expression factory.
     *
     * @since 2.2.1
     */
    protected ExpressionFactory expressionFactory;

    /**
     * The EL resolver to use.
     *
     * @since 2.2.1
     */
    protected ELResolver resolver;

    /**
     * Constructor.
     *
     * @since 2.2.1
     */
    public ELAttributeEvaluator() {
    }

    /**
     * Sets the expression factory to use.
     *
     * @param expressionFactory The expression factory.
     * @since 2.2.1
     */
    public void setExpressionFactory(ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
    }

    /**
     * Sets the EL resolver to use.
     *
     * @param resolver The EL resolver.
     * @since 2.2.1
     */
    public void setResolver(ELResolver resolver) {
        this.resolver = resolver;
    }

    /** {@inheritDoc} */
    public Object evaluate(String expression, Request request) {
        ELContextImpl context = new ELContextImpl(resolver);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class,
                request.getApplicationContext());
        ValueExpression valueExpression = expressionFactory
                .createValueExpression(context, expression, Object.class);

        return valueExpression.getValue(context);
    }
}
