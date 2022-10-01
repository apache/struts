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

import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.Definition;
import org.apache.tiles.api.ListAttribute;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.core.runtime.annotation.Parameter;
import org.apache.tiles.request.Request;

import java.io.IOException;
import java.util.Deque;

/**
 * <p>
 * <strong>Declare a list that will be pass as attribute to tile. </strong>
 * </p>
 * <p>
 * Declare a list that will be pass as attribute to tile. List elements are
 * added using the tags 'addAttribute' or 'addListAttribute'. This tag can only
 * be used inside 'insertTemplate', 'insertDefinition', 'definition' tags.
 * </p>
 *
 * @since 2.2.0
 */
public class PutListAttributeModel {

    /**
     * Executes the model.
     *
     * @param name      The name of the attribute to put.
     * @param role      A comma-separated list of roles. If present, the attribute
     *                  will be rendered only if the current user belongs to one of the roles.
     * @param inherit   If <code>true</code>, the list attribute will use, as first elements, the
     *                  list contained in the list attribute, put with the same name, of the containing definition.
     * @param cascade   If <code>true</code> the attribute will be cascaded to all nested attributes.
     * @param request   The request.
     * @param modelBody The body.
     * @throws IOException If the body cannot be evaluated.
     */
    public void execute(
        @Parameter(required = true) String name,
        String role,
        boolean inherit,
        boolean cascade,
        Request request,
        ModelBody modelBody
    ) throws IOException {
        Deque<Object> composeStack = ComposeStackUtil.getComposeStack(request);
        ListAttribute listAttribute = new ListAttribute();
        listAttribute.setRole(role);
        listAttribute.setInherit(inherit);
        composeStack.push(listAttribute);
        modelBody.evaluateWithoutWriting();
        TilesContainer container = TilesAccess.getCurrentContainer(request);
        listAttribute = (ListAttribute) composeStack.pop();
        AttributeContext attributeContext = null;
        if (!composeStack.isEmpty()) {
            Object obj = composeStack.peek();
            if (obj instanceof Definition) {
                attributeContext = (AttributeContext) obj;
            }
        }
        if (attributeContext == null) {
            attributeContext = container.getAttributeContext(request);
        }
        attributeContext.putAttribute(name, listAttribute, cascade);
    }
}
