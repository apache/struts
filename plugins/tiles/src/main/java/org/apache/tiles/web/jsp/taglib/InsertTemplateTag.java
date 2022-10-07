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
import org.apache.tiles.template.InsertTemplateModel;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;

/**
 * <p>
 * Insert a template.
 * </p>
 * <p>
 * Insert a template with the possibility to pass parameters (called
 * attributes). A template can be seen as a procedure that can take parameters
 * or attributes. &lt;tiles:insertTemplate&gt; allows to define these attributes
 * and pass them to the inserted jsp page, called template. Attributes are
 * defined using nested tag &lt;tiles:putAttribute&gt; or
 * &lt;tiles:putListAttribute&gt;.
 * </p>
 * <p>
 * You must specify template attribute, for inserting a template
 * </p>
 * 
 * <p>
 * Example :
 * </p>
 * 
 * <pre>
 *   &lt;code&gt;
 *     &lt;tiles:insertTemplate template=&quot;/basic/myLayout.jsp&quot; flush=&quot;true&quot;&gt;
 *       &lt;tiles:putAttribute name=&quot;title&quot; value=&quot;My first page&quot; /&gt;
 *       &lt;tiles:putAttribute name=&quot;header&quot; value=&quot;/common/header.jsp&quot; /&gt;
 *       &lt;tiles:putAttribute name=&quot;footer&quot; value=&quot;/common/footer.jsp&quot; /&gt;
 *       &lt;tiles:putAttribute name=&quot;menu&quot; value=&quot;/basic/menu.jsp&quot; /&gt;
 *       &lt;tiles:putAttribute name=&quot;body&quot; value=&quot;/basic/helloBody.jsp&quot; /&gt;
 *     &lt;/tiles:insertTemplate&gt;
 *   &lt;/code&gt;
 * </pre>
 */
public class InsertTemplateTag extends SimpleTagSupport {

    /**
     * The template model.
     */
    private InsertTemplateModel model = new InsertTemplateModel();

    /**
     * The template to render.
     */
    private String template;

    /**
     * The type of the template attribute.
     */
    private String templateType;

    /**
     * The expression to evaluate to get the value of the template.
     */
    private String templateExpression;

    /**
     * A comma-separated list of roles. If present, the template will be rendered
     * only if the current user belongs to one of the roles.
     */
    private String role;

    /**
     * The preparer to use to invoke before the definition is rendered. If
     * specified, it overrides the preparer specified in the definition itself.
     */
    private String preparer;

    /**
     * If true, the response will be flushed after the insert.
     */
    private boolean flush;

    /**
     * Getter for template property.
     *
     * @return The template to render.
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Setter for template property.
     *
     * @param template The template to render.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Getter for templateType property.
     *
     * @return The type of the template attribute.
     */
    public String getTemplateType() {
        return templateType;
    }

    /**
     * Setter for templateType property.
     *
     * @param templateType The type of the template attribute.
     */
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    /**
     * Getter for templateExpression property.
     *
     * @return The expression to evaluate to get the value of the template.
     */
    public String getTemplateExpression() {
        return templateExpression;
    }

    /**
     * Setter for templateExpression property.
     *
     * @param templateExpression The expression to evaluate to get the value of the
     *                           template.
     */
    public void setTemplateExpression(String templateExpression) {
        this.templateExpression = templateExpression;
    }

    /**
     * Getter for role property.
     *
     * @return A comma-separated list of roles. If present, the template will be
     *         rendered only if the current user belongs to one of the roles.
     */
    public String getRole() {
        return role;
    }

    /**
     * Setter for role property.
     *
     * @param role A comma-separated list of roles. If present, the template will be
     *             rendered only if the current user belongs to one of the roles.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Getter for preparer property.
     *
     * @return The preparer to use to invoke before the definition is rendered. If
     *         specified, it overrides the preparer specified in the definition
     *         itself.
     */
    public String getPreparer() {
        return preparer;
    }

    /**
     * Setter for preparer property.
     *
     * @param preparer The preparer to use to invoke before the definition is
     *                 rendered. If specified, it overrides the preparer specified
     *                 in the definition itself.
     */
    public void setPreparer(String preparer) {
        this.preparer = preparer;
    }

    /**
     * Getter for flush property.
     *
     * @return If true, the response will be flushed after the insert.
     */
    public boolean isFlush() {
        return flush;
    }

    /**
     * Setter for flush property.
     *
     * @param flush If true, the response will be flushed after the insert.
     */
    public void setFlush(boolean flush) {
        this.flush = flush;
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
        model.execute(template, templateType, templateExpression, role, preparer, flush, request, modelBody);
    }
}
