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
import org.apache.tiles.template.InsertDefinitionModel;

import java.io.IOException;
import java.util.Map;

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
 *           &lt;tiles:insertDefinition name=&quot;.my.tiles.definition flush=&quot;true&quot;&gt;
 *              &lt;tiles:putAttribute name=&quot;title&quot; value=&quot;My first page&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;header&quot; value=&quot;/common/header.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;footer&quot; value=&quot;/common/footer.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;menu&quot; value=&quot;/basic/menu.jsp&quot; /&gt;
 *              &lt;tiles:putAttribute name=&quot;body&quot; value=&quot;/basic/helloBody.jsp&quot; /&gt;
 *           &lt;/tiles:insertDefinition&gt;
 *         &lt;/code&gt;
 * </pre>
 */
public class InsertDefinitionFMModel implements TemplateDirectiveModel {

    /**
     * The template model.
     */
    private final InsertDefinitionModel model;

    /**
     * Constructor.
     *
     * @param model The template model.
     */
    public InsertDefinitionFMModel(InsertDefinitionModel model) {
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
            runtime.getParameter("name", String.class, null),
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
