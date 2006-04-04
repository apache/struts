/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.components.template;

import org.apache.struts.action2.components.UIBean;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Writer;
import java.util.Map;

/**
 * Context used when rendering templates.
 *
 * @author jcarreira
 */
public class TemplateRenderingContext {
    Template template;
    OgnlValueStack stack;
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
    public TemplateRenderingContext(Template template, Writer writer, OgnlValueStack stack, Map params, UIBean tag) {
        this.template = template;
        this.writer = writer;
        this.stack = stack;
        this.parameters = params;
        this.tag = tag;
    }

    public Template getTemplate() {
        return template;
    }

    public OgnlValueStack getStack() {
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
