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
import org.apache.tiles.template.PutAttributeModel;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * <strong>Put an attribute in enclosing attribute container tag.</strong>
 * </p>
 * <p>
 * Enclosing attribute container tag can be :
 * <ul>
 * <li>&lt;initContainer&gt;</li>
 * <li>&lt;definition&gt;</li>
 * <li>&lt;insertAttribute&gt;</li>
 * <li>&lt;insertDefinition&gt;</li>
 * <li>&lt;putListAttribute&gt;</li>
 * </ul>
 * (or any other tag which implements the <code>PutAttributeTagParent</code>
 * interface. Exception is thrown if no appropriate tag can be found.
 * </p>
 * <p>
 * Put tag can have following attributes :
 * <ul>
 * <li>name : Name of the attribute</li>
 * <li>value : value to put as attribute</li>
 * <li>type : value type. Possible type are : string (value is used as direct
 * string), template (value is used as a page url to insert), definition (value
 * is used as a definition name to insert), object (value is used as it is)</li>
 * <li>role : Role to check when 'insertAttribute' will be called.</li>
 * </ul>
 * </p>
 * <p>
 * Value can also come from tag body. Tag body is taken into account only if
 * value is not set by one of the tag attributes. In this case Attribute type is
 * "string", unless tag body define another type.
 * </p>
 */
public class PutAttributeFMModel implements TemplateDirectiveModel {

    /**
     * The template model.
     */
    private final PutAttributeModel model;

    /**
     * Constructor.
     *
     * @param model The template model.
     */
    public PutAttributeFMModel(PutAttributeModel model) {
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
            runtime.getParameter("value", Object.class, null),
            runtime.getParameter("expression", String.class, null),
            runtime.getParameter("role", String.class, null),
            runtime.getParameter("type", String.class, null),
            runtime.getParameter("cascade", Boolean.class, false),
            request,
            modelBody
        );
    }
}
