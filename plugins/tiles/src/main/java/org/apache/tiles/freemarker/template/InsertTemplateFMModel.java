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
package org.apache.tiles.freemarker.template;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.freemarker.autotag.FreemarkerAutotagRuntime;
import org.apache.tiles.template.InsertTemplateModel;

import java.io.IOException;
import java.util.Map;

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
 */
public class InsertTemplateFMModel implements TemplateDirectiveModel {

    /**
     * The template model.
     */
    private final InsertTemplateModel model;

    /**
     * Constructor.
     *
     * @param model The template model.
     */
    public InsertTemplateFMModel(InsertTemplateModel model) {
        this.model = model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(
        Environment env,
        Map params,
        TemplateModel[] loopVars,
        TemplateDirectiveBody body
    ) throws TemplateException, IOException {
        AutotagRuntime<Request> runtime = new FreemarkerAutotagRuntime();
        ((TemplateDirectiveModel) runtime).execute(env, params, loopVars, body);
        Request request = runtime.createRequest();
        ModelBody modelBody = runtime.createModelBody();
        model.execute(
            runtime.getParameter("template", String.class, null),
            runtime.getParameter("templateType", String.class, null),
            runtime.getParameter("templateExpression", String.class, null),
            runtime.getParameter("role", String.class, null),
            runtime.getParameter("preparer", String.class, null),
            runtime.getParameter("flush", Boolean.class, false),
            request,
            modelBody
        );
    }
}
