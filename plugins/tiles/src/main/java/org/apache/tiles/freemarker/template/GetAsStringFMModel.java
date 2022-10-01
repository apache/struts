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
import org.apache.tiles.template.GetAsStringModel;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * <strong> Render the value of the specified template attribute to the current
 * Writer</strong>
 * </p>
 *
 * <p>
 * Retrieve the value of the specified template attribute property, and render
 * it to the current Writer as a String. The usual toString() conversions is
 * applied on found value.
 * </p>
 */
public class GetAsStringFMModel implements TemplateDirectiveModel {

    /**
     * The template model.
     */
    private final GetAsStringModel model;

    /**
     * Constructor.
     *
     * @param model The template model.
     */
    public GetAsStringFMModel(GetAsStringModel model) {
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
            request,
            modelBody
        );
    }
}
