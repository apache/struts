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
package org.apache.tiles.ognl;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.tiles.core.evaluator.AbstractAttributeEvaluator;
import org.apache.tiles.core.evaluator.EvaluationException;
import org.apache.tiles.request.Request;

/**
 * Evaluates attribute expressions and expressions with OGNL language.
 *
 * @since 2.2.0
 */
public class OGNLAttributeEvaluator extends AbstractAttributeEvaluator {

    /** {@inheritDoc} */
    public Object evaluate(String expression, Request request) {
        if (expression == null) {
            throw new IllegalArgumentException("The expression parameter cannot be null");
        }
        try {
            return Ognl.getValue(expression, request);
        } catch (OgnlException e) {
            throw new EvaluationException("Cannot evaluate OGNL expression '" + expression + "'", e);
        }
    }
}
