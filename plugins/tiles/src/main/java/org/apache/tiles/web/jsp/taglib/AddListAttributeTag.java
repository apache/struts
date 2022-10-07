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
import org.apache.tiles.template.AddListAttributeModel;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;

/**
 * <p>
 * Declare a list that will be pass as an attribute.
 * </p>
 * <p>
 * Declare a list that will be pass as an attribute . List elements are added
 * using the tag 'addAttribute' or 'addListAttribute'. This tag can only be used
 * inside 'insertTemplate', 'insertDefinition' or 'definition' tag.
 * </p>
 */
public class AddListAttributeTag extends SimpleTagSupport {

    /**
     * The template model.
     */
    private AddListAttributeModel model = new AddListAttributeModel();

    /**
     * The comma-separated list of roles that can use the list attribute.
     */
    private String role;

    /**
     * Getter for role property.
     *
     * @return The comma-separated list of roles that can use the list attribute.
     */
    public String getRole() {
        return role;
    }

    /**
     * Setter for role property.
     *
     * @param role The comma-separated list of roles that can use the list
     *             attribute.
     */
    public void setRole(String role) {
        this.role = role;
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
        model.execute(role, request, modelBody);
    }
}
