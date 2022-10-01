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

import java.util.HashMap;
import java.util.Map;

/**
 * Basic implementation of {@link AttributeEvaluatorFactory}. It supports a
 * default attribute evaluator, in case the language is not recognized.
 *
 * @since 2.2.0
 */
public class BasicAttributeEvaluatorFactory implements AttributeEvaluatorFactory {

    /**
     * The default evaluator to return if it is not found in the map of known
     * languages.
     */
    private final AttributeEvaluator defaultEvaluator;

    /**
     * Maps names of expression languages to their attribute evaluator.
     *
     * @since 2.2.0
     */
    private final Map<String, AttributeEvaluator> language2evaluator;

    /**
     * Constructor.
     *
     * @param defaultEvaluator The default evaluator to return if it is not
     * found in the map of known languages.
     * @since 2.2.0
     */
    public BasicAttributeEvaluatorFactory(AttributeEvaluator defaultEvaluator) {
        this.defaultEvaluator = defaultEvaluator;
        language2evaluator = new HashMap<>();
    }

    /**
     * Registers a known expression language with its attribute evaluator.
     *
     * @param language The name of the expression language.
     * @param evaluator The associated attribute evaluator.
     * @since 2.2.0
     */
    public void registerAttributeEvaluator(String language, AttributeEvaluator evaluator) {
        language2evaluator.put(language, evaluator);
    }

    /** {@inheritDoc} */
    public AttributeEvaluator getAttributeEvaluator(String language) {
        AttributeEvaluator retValue = language2evaluator.get(language);
        if (retValue == null) {
            retValue = defaultEvaluator;
        }
        return retValue;
    }

    /** {@inheritDoc} */
    public AttributeEvaluator getAttributeEvaluator(Attribute attribute) {
        Expression expression = attribute.getExpressionObject();
        if (expression != null) {
            return getAttributeEvaluator(expression.getLanguage());
        }
        return defaultEvaluator;
    }
}
