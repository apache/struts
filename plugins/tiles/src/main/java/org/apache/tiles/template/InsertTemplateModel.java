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
 * <strong>Insert a template.</strong>
 * </p>
 * <p>
 * Insert a template with the possibility to pass parameters (called
 * attributes). A template can be seen as a procedure that can take parameters
 * or attributes. <code>&lt;tiles:insertTemplate&gt;</code> allows to define
 * these attributes and pass them to the inserted jsp page, called template.
 * Attributes are defined using nested tag
 * <code>&lt;tiles:putAttribute&gt;</code> or
 * <code>&lt;tiles:putListAttribute&gt;</code>.
 * </p>
 * <p>
 * You must specify <code>template</code> attribute, for inserting a template
 * </p>
 *
 * <p>
 * <strong>Example : </strong>
 * </p>
 *
 * <pre>
 * &lt;code&gt;
 *           &lt;tiles:insertTemplate template=&quot;/basic/myLayout.jsp&quot; flush=&quot;true&quot;&gt;
 *              &lt;tiles:putAttribute name=&quot;title&quot; value=&quot;My first page&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;header&quot; value=&quot;/common/header.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;footer&quot; value=&quot;/common/footer.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;menu&quot; value=&quot;/basic/menu.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;body&quot; value=&quot;/basic/helloBody.jsp&quot; /&gt;
 *           &lt;/tiles:insertTemplate&gt;
 *         &lt;/code&gt;
 * </pre>
 *
 * @since 2.2.0
 */
public class InsertTemplateModel {

    /**
     * Executes the operation.
     *
     * @param template           The template to render.
     * @param templateType       The type of the template attribute.
     * @param templateExpression The expression to evaluate to get the value of the template.
     * @param role               A comma-separated list of roles. If present, the template
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
        @Parameter(required = true) String template,
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
        renderTemplate(container, template, templateType, templateExpression, role, preparer, flush, request);
    }

    /**
     * Renders a template.
     *
     * @param container          The container to use.
     * @param template           The template to render.
     * @param templateType       The type of the template attribute.
     * @param templateExpression The expression to evaluate to get the value of the template.
     * @param role               A comma-separated list of roles. If present, the template
     *                           will be rendered only if the current user belongs to one of the roles.
     * @param preparer           The preparer to use to invoke before the definition is
     *                           rendered. If specified, it overrides the preparer specified in the
     *                           definition itself.
     * @param flush              If <code>true</code>, the response will be flushed after the insert.
     * @param request            The request.
     * @throws IOException If something goes wrong.
     */
    private void renderTemplate(
        TilesContainer container,
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
            container.renderContext(request);
            if (flush) {
                request.getWriter().flush();
            }
        } finally {
            container.endContext(request);
        }
    }
}
