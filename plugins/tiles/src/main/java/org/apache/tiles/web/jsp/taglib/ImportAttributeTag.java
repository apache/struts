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

import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.jsp.autotag.JspAutotagRuntime;
import org.apache.tiles.template.ImportAttributeModel;

/**
 * <p>
 * Import attribute(s) in specified context.
 * </p>
 * <p>
 * Import attribute(s) to requested scope. Attribute name and scope are
 * optional. If not specified, all attributes are imported in page scope. Once
 * imported, an attribute can be used as any other beans from jsp contexts.
 * </p>
 */
public class ImportAttributeTag extends SimpleTagSupport {

    /**
     * The template model.
     */
    private ImportAttributeModel model = new ImportAttributeModel();

    /**
     * The name of the attribute to import. If it is null, all the attributes will
     * be imported.
     */
    private String name;

    /**
     * The scope into which the attribute(s) will be imported. If null, the import
     * will go in page scope.
     */
    private String scope;

    /**
     * The name of the attribute into which the attribute will be imported. To be
     * used in conjunction to name. If null, the value of name will be used.
     */
    private String toName;

    /**
     * If true, if the attribute is not present, the problem will be ignored.
     */
    private boolean ignore;

    /**
     * Getter for name property.
     *
     * @return The name of the attribute to import. If it is null, all the
     *         attributes will be imported.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name property.
     *
     * @param name The name of the attribute to import. If it is null, all the
     *             attributes will be imported.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for scope property.
     *
     * @return The scope into which the attribute(s) will be imported. If null, the
     *         import will go in page scope.
     */
    public String getScope() {
        return scope;
    }

    /**
     * Setter for scope property.
     *
     * @param scope The scope into which the attribute(s) will be imported. If null,
     *              the import will go in page scope.
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Getter for toName property.
     *
     * @return The name of the attribute into which the attribute will be imported.
     *         To be used in conjunction to name. If null, the value of name will be
     *         used.
     */
    public String getToName() {
        return toName;
    }

    /**
     * Setter for toName property.
     *
     * @param toName The name of the attribute into which the attribute will be
     *               imported. To be used in conjunction to name. If null, the value
     *               of name will be used.
     */
    public void setToName(String toName) {
        this.toName = toName;
    }

    /**
     * Getter for ignore property.
     *
     * @return If true, if the attribute is not present, the problem will be
     *         ignored.
     */
    public boolean isIgnore() {
        return ignore;
    }

    /**
     * Setter for ignore property.
     *
     * @param ignore If true, if the attribute is not present, the problem will be
     *               ignored.
     */
    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
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
        model.execute(name, scope, toName, ignore, request);
    }
}
