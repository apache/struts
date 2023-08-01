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
import org.apache.tiles.api.AttributeContext;
import org.apache.tiles.api.TilesContainer;
import org.apache.tiles.api.access.TilesAccess;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.core.runtime.annotation.Parameter;
import org.apache.tiles.request.Request;

import java.io.IOException;

/**
 * <p>
 * <strong>Insert a definition.</strong>
 * </p>
 * <p>
 * Insert a definition with the possibility to override and specify parameters
 * (called attributes). A definition can be seen as a (partially or totally)
 * filled template that can override or complete attribute values.
 * <code>&lt;tiles:insertDefinition&gt;</code> allows to define these attributes
 * and pass them to the inserted jsp page, called template. Attributes are
 * defined using nested tag <code>&lt;tiles:putAttribute&gt;</code> or
 * <code>&lt;tiles:putListAttribute&gt;</code>.
 * </p>
 * <p>
 * You must specify <code>name</code> tag attribute, for inserting a definition
 * from definitions factory.
 * </p>
 * <p>
 * <strong>Example : </strong>
 * </p>
 *
 * <pre>
 * &lt;code&gt;
 *           &lt;tiles:insertDefinition name=&quot;.my.tiles.defininition flush=&quot;true&quot;&gt;
 *              &lt;tiles:putAttribute name=&quot;title&quot; value=&quot;My first page&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;header&quot; value=&quot;/common/header.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;footer&quot; value=&quot;/common/footer.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;menu&quot; value=&quot;/basic/menu.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;body&quot; value=&quot;/basic/helloBody.jsp&quot; /&gt;
 *           &lt;/tiles:insertDefinition&gt;
 *         &lt;/code&gt;
 * </pre>
 * @since 2.2.0
 */
public class InsertDefinitionModel {

    /**
     * Executes the operation.
     *
     * @param definitionName     The name of the definition to render.
     * @param template           If specified, this template will be used instead of the
     *                           one used by the definition.
     * @param templateType       The type of the template attribute.
     * @param templateExpression The expression to evaluate to get the value of the template.
     * @param role               A comma-separated list of roles. If present, the definition
     *                           will be rendered only if the current user belongs to one of the roles.
     * @param preparer           The preparer to use to invoke before the definition is
     *                           rendered. If specified, it overrides the preparer specified in the
     *                           definition itself.
     * @param flush              If <code>true</code>, the response will be flushed after the insert.
     * @param request            The request.
     * @param modelBody          The body.
     * @throws IOException If something goes wrong.
     * @since 2.2.0
     */
    public void execute(
        @Parameter(name = "name", required = true) String definitionName,
        String template,
        String templateType,
        String templateExpression,
        String role,
        String preparer,
        boolean flush,
        Request request,
        ModelBody modelBody
    ) throws IOException {
        TilesContainer container = TilesAccess.getCurrentContainer(request);
        container.startContext(request);
        modelBody.evaluateWithoutWriting();
        container = TilesAccess.getCurrentContainer(request);
        renderDefinition(container, definitionName, template, templateType, templateExpression, role, preparer, flush, request);
    }

    /**
     * Renders a definition.
     *
     * @param container          The container to use.
     * @param definitionName     The name of the definition to render.
     * @param template           If specified, this template will be used instead of the
     *                           one used by the definition.
     * @param templateType       The type of the template attribute.
     * @param templateExpression The expression to evaluate to get the value of the template.
     * @param role               A comma-separated list of roles. If present, the definition
     *                           will be rendered only if the current user belongs to one of the roles.
     * @param preparer           The preparer to use to invoke before the definition is
     *                           rendered. If specified, it overrides the preparer specified in the
     *                           definition itself.
     * @param flush              If <code>true</code>, the response will be flushed after the insert.
     * @param request            The request.
     * @throws IOException If something goes wrong.
     */
    private void renderDefinition(
        TilesContainer container,
        String definitionName,
        String template,
        String templateType,
        String templateExpression,
        String role,
        String preparer,
        boolean flush,
        Request request
    ) throws IOException {
        try {
            AttributeContext attributeContext = container.getAttributeContext(request);
            Attribute templateAttribute = Attribute.createTemplateAttribute(template, templateExpression, templateType, role);
            attributeContext.setPreparer(preparer);
            attributeContext.setTemplateAttribute(templateAttribute);
            container.render(definitionName, request);
            if (flush) {
                request.getWriter().flush();
            }
        } finally {
            container.endContext(request);
        }
    }
}
