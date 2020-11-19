/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.views.freemarker;

import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.WrappingTemplateModel;
import org.apache.struts2.views.TagAttribute;

public class TagAttributeAdapter extends WrappingTemplateModel implements AdapterTemplateModel, TemplateScalarModel {

    private final TagAttribute attribute;

    public TagAttributeAdapter(TagAttribute attribute, ObjectWrapper ow) {
        super(ow);
        this.attribute = attribute;
    }

    @Override
    public Object getAdaptedObject(Class<?> hint) {
        return attribute;
    }

    @Override
    public String getAsString() throws TemplateModelException {
        return attribute.getValue();
    }

}
