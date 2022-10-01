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
package org.apache.tiles.request.freemarker.autotag;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.freemarker.FreemarkerRequest;
import org.apache.tiles.request.freemarker.FreemarkerRequestUtil;

import java.util.Map;

/**
 * A Runtime for implementing a Freemarker Template Directive.
 */
public class FreemarkerAutotagRuntime implements AutotagRuntime<Request>, TemplateDirectiveModel {

    private Environment env;
    private TemplateDirectiveBody body;
    private Map<String, TemplateModel> params;

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) {
        this.env = env;
        this.body = body;
        this.params = params;
    }

    /** {@inheritDoc} */
    @Override
    public Request createRequest() {
        return FreemarkerRequest.createServletFreemarkerRequest(FreemarkerRequestUtil.getApplicationContext(env), env);
    }

    /** {@inheritDoc} */
    @Override
    public ModelBody createModelBody() {
        return new FreemarkerModelBody(env.getOut(), body);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getParameter(String name, Class<T> type, T defaultValue) {
        return FreemarkerUtil.getAsObject(params.get(name), type, defaultValue);
    }
}
