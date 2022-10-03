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

package org.apache.tiles.api;

import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.tiles.request.Request;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Common implementation of attribute definition.
 */
public class Attribute {

    /**
     * The name of the template renderer.
     */
    private static final String TEMPLATE_RENDERER = "template";

    /**
     * The roles that can render this attribute.
     *
     * @since 2.0.6
     */
    private Set<String> roles = null;

    /**
     * The value of the attribute.
     */
    private Object value = null;

    /**
     * The expression to evaluate. Ignored if {@link #value} is not <code>null</code>.
     *
     * @since 2.2.0
     */
    private Expression expressionObject = null;

    /**
     * The renderer name of the attribute. Default names are <code>string</code>,
     * <code>template</code>, <code>definition</code>, <code>object</code>.
     */
    private String renderer = null;

    /**
     * Constructor.
     */
    public Attribute() {
    }

    /**
     * Constructor.
     *
     * @param value Object to store.
     */
    public Attribute(Object value) {
        this.value = value;
    }

    /**
     * Copy constructor.
     *
     * @param attribute The attribute to copy from.
     */
    public Attribute(Attribute attribute) {
        this.roles = attribute.roles;
        this.value = attribute.getValue();
        if (attribute.expressionObject != null) {
            this.expressionObject = new Expression(attribute.expressionObject);
        } else {
            this.expressionObject = null;
        }
        this.renderer = attribute.renderer;
    }

    /**
     * Constructor.
     *
     * @param value Object to store.
     * @param role  Asociated role.
     */
    public Attribute(Object value, String role) {
        this.value = value;
        setRole(role);
    }

    /**
     * Constructor.
     *
     * @param value        Object to store. If specified, the <code>expression</code>
     *                     parameter will be ignored.
     * @param expression   The expression to be evaluated. Ignored if the
     *                     <code>value</code> is not null.
     * @param role         Associated role.
     * @param rendererName The renderer name.
     * @since 2.2.0
     */
    public Attribute(Object value, Expression expression, String role, String rendererName) {
        this.value = value;
        this.expressionObject = expression;
        this.renderer = rendererName;
        setRole(role);
    }

    /**
     * Creates a template attribute, starting from the name of the template.
     *
     * @param template The template that will be rendered.
     * @return The template attribute.
     * @since 2.1.2
     */
    public static Attribute createTemplateAttribute(String template) {
        Attribute attribute = new Attribute();
        attribute.setValue(template);
        attribute.setRenderer(TEMPLATE_RENDERER);
        return attribute;
    }

    /**
     * Creates a template attribute, starting from the name of the template.
     *
     * @param template           The template that will be rendered.
     * @param templateExpression The template expression that will be evaluated
     *                           to a template.
     * @param templateType       The type, or renderer, of the template. If null, the
     *                           default <code>template</code> will be used.
     * @param role               The comma-separated roles for which the template is
     *                           authorized to be rendered.
     * @return The template attribute.
     * @since 2.2.2
     */
    public static Attribute createTemplateAttribute(String template,
                                                    String templateExpression, String templateType, String role) {
        Attribute templateAttribute = createTemplateAttribute(template);
        templateAttribute.setRole(role);
        if (templateType != null) {
            templateAttribute.setRenderer(templateType);
        }
        templateAttribute
            .setExpressionObject(Expression
                .createExpressionFromDescribedExpression(templateExpression));
        return templateAttribute;
    }

    /**
     * Get role.
     *
     * @return the name of the required role(s)
     */
    public String getRole() {
        String retValue = null;

        if (roles != null && !roles.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            Iterator<String> roleIt = roles.iterator();
            if (roleIt.hasNext()) {
                builder.append(roleIt.next());
                while (roleIt.hasNext()) {
                    builder.append(",");
                    builder.append(roleIt.next());
                }
                retValue = builder.toString();
            }
        }

        return retValue;
    }

    /**
     * Returns the roles that can render this attribute.
     *
     * @return The enabled roles.
     * @since 2.0.6
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Set role.
     *
     * @param role Associated role.
     */
    public void setRole(String role) {
        if (role != null && role.trim().length() > 0) {
            roles = TextParseUtil.commaDelimitedStringToSet(role);
        } else {
            roles = null;
        }
    }

    /**
     * Sets the roles that can render this attribute.
     *
     * @param roles The enabled roles.
     * @since 2.0.6
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * Get value.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set value.
     *
     * @param value New value.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Returns The expression to evaluate. Ignored if {@link #value} is not
     * <code>null</code>.
     *
     * @return The expression to be evaluated.
     * @since 2.2.0
     */
    public Expression getExpressionObject() {
        return expressionObject;
    }

    /**
     * Sets The expression to evaluate. Ignored if {@link #value} is not
     * <code>null</code>.
     *
     * @param expressionObject The expression to be evaluated.
     * @since 2.2.0
     */
    public void setExpressionObject(Expression expressionObject) {
        this.expressionObject = expressionObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Objects.toString(value);
    }

    /**
     * Returns the renderer name to use.
     *
     * @return The renderer name.
     * @since 2.1.0
     */
    public String getRenderer() {
        return renderer;
    }

    /**
     * Sets the renderer name to use.
     *
     * @param rendererName The renderer.
     * @since 2.1.0
     */
    public void setRenderer(String rendererName) {
        this.renderer = rendererName;
    }

    /**
     * Inherits an attribute, i.e. overwrites null properties with the ones
     * provided by the attribute.
     *
     * @param attribute The attribute to inherit.
     * @since 2.1.2
     */
    public void inherit(Attribute attribute) {
        if (value == null) {
            value = attribute.getValue();
        }
        Expression targetExpressionObject = attribute.getExpressionObject();
        if (targetExpressionObject != null
            && (expressionObject == null || expressionObject
            .getExpression() == null)) {
            expressionObject = new Expression(targetExpressionObject);
        }
        if (roles == null || roles.isEmpty()) {
            roles = attribute.getRoles();
        }
        if (renderer == null) {
            renderer = attribute.getRenderer();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null && value == null) {
            return true;
        }
        if (!(obj instanceof Attribute)) {
            return false;
        }
        Attribute attribute = (Attribute) obj;
        return Objects.equals(value, attribute.value)
            && Objects.equals(renderer, attribute.renderer)
            && Objects.equals(roles, attribute.roles)
            && Objects.equals(expressionObject, attribute.expressionObject);
    }

    /**
     * Checks if the current user can use this attribute.
     *
     * @param request The request context.
     * @return <code>true</code> if the current user can see this attribute.
     * @since 3.0.0
     */
    public boolean isPermitted(Request request) {
        if (roles == null || roles.isEmpty()) {
            return true;
        }

        boolean retValue = false;

        for (Iterator<String> roleIt = roles.iterator(); roleIt.hasNext()
            && !retValue; ) {
            retValue = request.isUserInRole(roleIt.next());
        }

        return retValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value) + Objects.hashCode(renderer)
            + Objects.hashCode(roles) + Objects.hashCode(expressionObject);
    }

    public Attribute copy() {
        return new Attribute(this);
    }
}
