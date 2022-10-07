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
package org.apache.tiles.web.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.jsp.autotag.JspAutotagRuntime;
import org.apache.tiles.template.AddAttributeModel;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;

/**
 * <p>
 * Add an element to the surrounding list. Equivalent to 'putAttribute', but for
 * list element.
 * </p>
 * 
 * <p>
 * Add an element to the surrounding list. This tag can only be used inside
 * 'putListAttribute' or 'addListAttribute' tags. Value can come from a direct
 * assignment (value="aValue")
 * </p>
 */
public class AddAttributeTag extends SimpleTagSupport {

    /**
     * The template model.
     */
    private AddAttributeModel model = new AddAttributeModel();

    /**
     * The value of the attribute. Use this parameter, or expression, or body.
     */
    private Object value;

    /**
     * The expression to calculate the value from. Use this parameter, or value, or
     * body.
     */
    private String expression;

    /**
     * A comma-separated list of roles. If present, the attribute will be rendered
     * only if the current user belongs to one of the roles.
     */
    private String role;

    /**
     * The type (renderer) of the attribute.
     */
    private String type;

    /**
     * Getter for value property.
     *
     * @return The value of the attribute. Use this parameter, or expression, or
     *         body.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Setter for value property.
     *
     * @param value The value of the attribute. Use this parameter, or expression,
     *              or body.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Getter for expression property.
     *
     * @return The expression to calculate the value from. Use this parameter, or
     *         value, or body.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Setter for expression property.
     *
     * @param expression The expression to calculate the value from. Use this
     *                   parameter, or value, or body.
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Getter for role property.
     *
     * @return A comma-separated list of roles. If present, the attribute will be
     *         rendered only if the current user belongs to one of the roles.
     */
    public String getRole() {
        return role;
    }

    /**
     * Setter for role property.
     *
     * @param role A comma-separated list of roles. If present, the attribute will
     *             be rendered only if the current user belongs to one of the roles.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Getter for type property.
     *
     * @return The type (renderer) of the attribute.
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for type property.
     *
     * @param type The type (renderer) of the attribute.
     */
    public void setType(String type) {
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public void doTag() throws JspException, IOException {
        AutotagRuntime<Request> runtime = new JspAutotagRuntime();
        if (runtime instanceof SimpleTagSupport) {
            SimpleTagSupport tag = (SimpleTagSupport) runtime;
            tag.setJspContext(getJspContext());
            tag.setJspBody(getJspBody());
            tag.setParent(getParent());
            tag.doTag();
        }
        Request request = runtime.createRequest();
        ModelBody modelBody = runtime.createModelBody();
        model.execute(value, expression, role, type, request, modelBody);
    }
}
