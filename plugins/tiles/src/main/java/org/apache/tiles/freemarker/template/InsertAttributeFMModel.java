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
import org.apache.tiles.api.Attribute;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.freemarker.autotag.FreemarkerAutotagRuntime;
import org.apache.tiles.template.InsertAttributeModel;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * <strong>Inserts the value of an attribute into the page.</strong>
 * </p>
 * <p>
 * This tag can be flexibly used to insert the value of an attribute into a
 * page. As in other usages in Tiles, every attribute can be determined to have
 * a "type", either set explicitly when it was defined, or "computed". If the
 * type is not explicit, then if the attribute value is a valid definition, it
 * will be inserted as such. Otherwise, if it begins with a "/" character, it
 * will be treated as a "template". Finally, if it has not otherwise been
 * assigned a type, it will be treated as a String and included without any
 * special handling.
 * </p>
 *
 * <p>
 * <strong>Example : </strong>
 * </p>
 *
 * <pre>
 * &lt;code&gt;
 *           &lt;tiles:insertAttribute name=&quot;body&quot; /&gt;
 *         &lt;/code&gt;
 * </pre>
 */
public class InsertAttributeFMModel implements TemplateDirectiveModel {

    /**
     * The template model.
     */
    private final InsertAttributeModel model;

    /**
     * Constructor.
     *
     * @param model The template model.
     */
    public InsertAttributeFMModel(InsertAttributeModel model) {
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
            runtime.getParameter("ignore", Boolean.class, false),
            runtime.getParameter("preparer", String.class, null),
            runtime.getParameter("role", String.class, null),
            runtime.getParameter("defaultValue", Object.class, null),
            runtime.getParameter("defaultValueRole", String.class, null),
            runtime.getParameter("defaultValueType", String.class, null),
            runtime.getParameter("name", String.class, null),
            runtime.getParameter("value", Attribute.class, null),
            runtime.getParameter("flush", Boolean.class, false),
            request,
            modelBody
        );
    }
}
