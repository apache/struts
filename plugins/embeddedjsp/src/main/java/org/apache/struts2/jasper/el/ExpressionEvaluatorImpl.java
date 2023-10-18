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
package org.apache.struts2.jasper.el;

import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import jakarta.servlet.jsp.el.ELException;
import jakarta.servlet.jsp.el.ELParseException;
import jakarta.servlet.jsp.el.Expression;
import jakarta.servlet.jsp.el.ExpressionEvaluator;
import jakarta.servlet.jsp.el.FunctionMapper;
import jakarta.servlet.jsp.el.VariableResolver;


public final class ExpressionEvaluatorImpl extends ExpressionEvaluator {

	private final ExpressionFactory factory;
	
	public ExpressionEvaluatorImpl(ExpressionFactory factory) {
		this.factory = factory;
	}

	public Expression parseExpression(String expression, Class expectedType,
			FunctionMapper fMapper) throws ELException {
		try {
			ELContextImpl ctx = new ELContextImpl(ELResolverImpl.DefaultResolver);
            if (fMapper != null) {
                ctx.setFunctionMapper(new FunctionMapperImpl(fMapper));
            }
			ValueExpression ve = this.factory.createValueExpression(ctx, expression, expectedType);
			return new ExpressionImpl(ve);
		} catch (jakarta.el.ELException e) {
			throw new ELParseException(e.getMessage());
		}
	}

	public Object evaluate(String expression, Class expectedType,
			VariableResolver vResolver, FunctionMapper fMapper)
			throws ELException {
		return this.parseExpression(expression, expectedType, fMapper).evaluate(vResolver);
	}

}
