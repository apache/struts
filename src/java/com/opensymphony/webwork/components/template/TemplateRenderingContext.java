/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.components.template;

import com.opensymphony.webwork.components.UIBean;
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
