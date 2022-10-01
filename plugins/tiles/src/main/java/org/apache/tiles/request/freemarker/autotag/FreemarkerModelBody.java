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

import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import org.apache.tiles.autotag.core.runtime.AbstractModelBody;

import java.io.IOException;
import java.io.Writer;

/**
 * Body implementation of a Freemarker model body.
 */
public class FreemarkerModelBody extends AbstractModelBody {

    /**
     * The real body.
     */
    private final TemplateDirectiveBody templateDirectiveBody;

    /**
     * Constructor.
     *
     * @param defaultWriter The default writer.
     * @param templateDirectiveBody The real body.
     */
    public FreemarkerModelBody(Writer defaultWriter, TemplateDirectiveBody templateDirectiveBody) {
        super(defaultWriter);
        this.templateDirectiveBody = templateDirectiveBody;
    }

    @Override
    public void evaluate(Writer writer) throws IOException {
        if (templateDirectiveBody == null) {
            return;
        }

        try {
            templateDirectiveBody.render(writer);
        } catch (TemplateException e) {
            throw new IOException("TemplateException when rendering body", e);
        }
    }

}
