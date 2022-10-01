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
package org.apache.tiles.core.evaluator;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Expression;
import org.apache.tiles.request.Request;

/**
 * Abstract class to link a correct evaluation of an attribute, by evaluating
 * {@link Attribute#getValue()} and then {@link Attribute#getExpressionObject()}.
 *
 * @since 2.1.2
 */
public abstract class AbstractAttributeEvaluator implements AttributeEvaluator {

    /** {@inheritDoc} */
    public Object evaluate(Attribute attribute, Request request) {
        if (attribute == null) {
            throw new IllegalArgumentException("The attribute cannot be null");
        }

        Object retValue = attribute.getValue();

        if (retValue == null) {
            Expression expression = attribute.getExpressionObject();
            if (expression != null) {
                retValue = evaluate(attribute.getExpressionObject().getExpression(), request);
            }
        }

        return retValue;
    }
}
