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

package org.apache.struts2.tiles.el;

import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import org.apache.tiles.evaluator.AbstractAttributeEvaluator;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;

public class ELAttributeEvaluator extends AbstractAttributeEvaluator {
    public static final String EXPRESSION_FACTORY_FACTORY_INIT_PARAM = "org.apache.tiles.evaluator.el.ExpressionFactoryFactory";
    protected ExpressionFactory expressionFactory;
    protected ELResolver resolver;

    public ELAttributeEvaluator() {
    }

    public void setExpressionFactory(ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
    }

    public void setResolver(ELResolver resolver) {
        this.resolver = resolver;
    }

    public Object evaluate(String expression, Request request) {
        ELContextImpl context = new ELContextImpl(this.resolver);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, request.getApplicationContext());
        ValueExpression valueExpression = this.expressionFactory.createValueExpression(context, expression, Object.class);
        return valueExpression.getValue(context);
    }
}
