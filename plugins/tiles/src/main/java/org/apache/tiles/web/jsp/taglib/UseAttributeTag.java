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

import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.jsp.autotag.JspAutotagRuntime;
import org.apache.tiles.template.ImportAttributeModel;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import java.io.IOException;

/**
 * Exposes am attribute as a scripting variable within the page.
 *
 * @since Tiles 1.0
 */
public class UseAttributeTag extends SimpleTagSupport {

    /**
     * The template model.
     */
    private final ImportAttributeModel model = new ImportAttributeModel();

    /**
     * The id of the imported scripting variable.
     */
    private String id;

    /**
     * The scope name.
     */
    private String scopeName = null;

    /**
     * The name of the attribute.
     */
    private String name = null;

    /**
     * Flag that, if true, ignores exceptions.
     */
    private boolean ignore = false;

    /**
     * Class name of object.
     */
    private String classname = null;

    /**
     * Returns the id of the imported scripting variable.
     *
     * @return The id of the imported scripting variable.
     * @since 2.2.0
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the imported scripting variable.
     *
     * @param id The id of the imported scripting variable.
     * @since 2.2.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the scope.
     *
     * @param scope Scope.
     */
    public void setScope(String scope) {
        this.scopeName = scope;
    }

    /**
     * Get scope.
     *
     * @return Scope.
     */
    public String getScope() {
        return scopeName;
    }

    /**
     * Get the name.
     *
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set ignore flag.
     *
     * @param ignore default: false: Exception is thrown when attribute is not
     *               found, set to true to ignore missing attributes silently
     */
    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    /**
     * Get ignore flag.
     *
     * @return default: false: Exception is thrown when attribute is not found, set
     *         to true to ignore missing attributes silently
     */
    public boolean isIgnore() {
        return ignore;
    }

    /**
     * Get class name.
     *
     * @return class name
     */
    public String getClassname() {
        return classname;

    }

    /**
     * Set the class name.
     *
     * @param name The new class name.
     */
    public void setClassname(String name) {
        this.classname = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        AutotagRuntime<Request> runtime = new JspAutotagRuntime();
        SimpleTagSupport tag = (SimpleTagSupport) runtime;
        tag.setJspContext(getJspContext());
        tag.setJspBody(getJspBody());
        tag.setParent(getParent());
        tag.doTag();
        Request request = runtime.createRequest();
        model.execute(name, scopeName, id, ignore, request);
    }

    /**
     * Returns the scripting variable to use.
     *
     * @return The scripting variable.
     */
    public String getScriptingVariable() {
        return id == null ? getName() : id;
    }

    /**
     * Implementation of TagExtraInfo which identifies the scripting object(s) to be
     * made visible.
     */
    public static class Tei extends TagExtraInfo {

        /**
         * {@inheritDoc}
         */
        @Override
        public VariableInfo[] getVariableInfo(TagData data) {
            String classname = data.getAttributeString("classname");
            if (classname == null) {
                classname = "java.lang.Object";
            }

            String id = data.getAttributeString("id");
            if (id == null) {
                id = data.getAttributeString("name");
            }

            return new VariableInfo[] { new VariableInfo(id, classname, true, VariableInfo.AT_END) };

        }
    }
}
