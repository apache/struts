/*
 * $Id$
 *
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

package org.apache.struts2.components.template;

import java.io.Writer;
import java.util.Map;

import org.apache.struts2.components.UIBean;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Context used when rendering templates.
 */
public class TemplateRenderingContext {
    Template template;
    ValueStack stack;
    Map parameters;
    UIBean tag;
    Writer writer;

    /**
     * Constructor
     *
     * @param template  the template.
     * @param writer    the writer.
     * @param stack     OGNL value stack.
     * @param params    parameters to this template.
     * @param tag       the tag UI component.
     */
    public TemplateRenderingContext(Template template, Writer writer, ValueStack stack, Map params, UIBean tag) {
        this.template = template;
        this.writer = writer;
        this.stack = stack;
        this.parameters = params;
        this.tag = tag;
    }

    public Template getTemplate() {
        return template;
    }

    public ValueStack getStack() {
        return stack;
    }

    public Map getParameters() {
        return parameters;
    }

    public UIBean getTag() {
        return tag;
    }

    public Writer getWriter() {
        return writer;
    }
}
