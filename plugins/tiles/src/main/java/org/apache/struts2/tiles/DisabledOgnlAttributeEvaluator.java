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

import org.apache.tiles.core.evaluator.AbstractAttributeEvaluator;
import org.apache.tiles.core.evaluator.EvaluationException;
import org.apache.tiles.request.Request;

/**
 * Fails closed when the deprecated Tiles OGNL evaluator has not been explicitly enabled.
 */
final class DisabledOgnlAttributeEvaluator extends AbstractAttributeEvaluator {

    static final String DISABLED_MESSAGE = "The Tiles OGNL evaluator is disabled. Migrate the expression to S2:, "
        + "or temporarily enable struts.tiles.ognl.legacy.enabled. Legacy Tiles OGNL support will be removed in "
        + "Struts 8.0.0.";

    @Override
    public Object evaluate(String expression, Request request) {
        throw new EvaluationException(DISABLED_MESSAGE, null);
    }
}
