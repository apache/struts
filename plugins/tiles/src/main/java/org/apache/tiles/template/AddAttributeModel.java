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
package org.apache.tiles.template;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Expression;
import org.apache.tiles.api.ListAttribute;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.Request;

import java.io.IOException;
import java.util.Deque;

/**
 * <p>
 * <strong>Add an element to the surrounding list. Equivalent to 'putAttribute',
 * but for list element.</strong>
 * </p>
 *
 * <p>
 * Add an element to the surrounding list. This tag can only be used inside
 * 'putListAttribute' or 'addListAttribute' tags. Value can come from a direct
 * assignment (value="aValue")
 * </p>
 *
 * @since 2.2.0
 */
public class AddAttributeModel {

    /**
     * Executes the operation.
     *
     * @param value      The value of the attribute. Use this parameter, or
     *                   expression, or body.
     * @param expression The expression to calculate the value from. Use this
     *                   parameter, or value, or body.
     * @param role       A comma-separated list of roles. If present, the attribute
     *                   will be rendered only if the current user belongs to one of the roles.
     * @param type       The type (renderer) of the attribute.
     * @param request    The request.
     * @param modelBody  The body.
     * @throws IOException If the body cannot be correctly evaluated.
     * @since 2.2.0
     */
    public void execute(
        Object value,
        String expression,
        String role,
        String type,
        Request request,
        ModelBody modelBody
    ) throws IOException {
        Attribute attribute = new Attribute();
        Deque<Object> composeStack = ComposeStackUtil.getComposeStack(request);
        composeStack.push(attribute);
        String body = modelBody.evaluateAsString();
        attribute = (Attribute) composeStack.pop();
        addAttributeToList(attribute, composeStack, value, expression, body, role, type);
    }

    /**
     * Adds the attribute to the containing list attribute.
     *
     * @param attribute    The attribute to add to the list attribute.
     * @param composeStack The composing stack.
     * @param value        The value of the attribute. Use this parameter, or
     *                     expression, or body.
     * @param expression   The expression to calculate the value from. Use this
     *                     parameter, or value, or body.
     * @param body         The body of the tag. Use this parameter, or value, or
     *                     expression.
     * @param role         A comma-separated list of roles. If present, the attribute
     *                     will be rendered only if the current user belongs to one of the roles.
     * @param type         The type (renderer) of the attribute.
     * @since 2.2.0
     */
    private void addAttributeToList(
        Attribute attribute,
        Deque<Object> composeStack,
        Object value,
        String expression,
        String body,
        String role,
        String type
    ) {
        ListAttribute listAttribute = (ListAttribute) ComposeStackUtil.findAncestorWithClass(composeStack, ListAttribute.class);

        if (listAttribute == null) {
            throw new NullPointerException("There is no ListAttribute in the stack");
        }
        if (value != null) {
            attribute.setValue(value);
        } else if (attribute.getValue() == null && body != null) {
            attribute.setValue(body);
        }
        if (expression != null) {
            attribute.setExpressionObject(Expression
                .createExpressionFromDescribedExpression(expression));
        }
        if (role != null) {
            attribute.setRole(role);
        }
        if (type != null) {
            attribute.setRenderer(type);
        }
        listAttribute.add(attribute);
    }
}
