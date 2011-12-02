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

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders an HTML file input element.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:file name="anUploadFile" accept="text/*" /&gt;
 * &lt;s:file name="anohterUploadFIle" accept="text/html,text/plain" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(
    name="file",
    tldTagClass="org.apache.struts2.views.jsp.ui.FileTag",
    description="Render a file input field",
    allowDynamicAttributes=true)
public class File extends UIBean {
    private final static Logger LOG = LoggerFactory.getLogger(File.class);

    final public static String TEMPLATE = "file";

    protected String accept;
    protected String size;

    public File(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        super.evaluateParams();

        Form form = (Form) findAncestor(Form.class);
        if (form != null) {
            String encType = (String) form.getParameters().get("enctype");
            if (!"multipart/form-data".equals(encType)) {
                // uh oh, this isn't good! Let's warn the developer
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Struts has detected a file upload UI tag (s:file) being used without a form set to enctype 'multipart/form-data'. This is probably an error!");
                }
            }

            String method = (String) form.getParameters().get("method");
            if (!"post".equalsIgnoreCase(method)) {
                // uh oh, this isn't good! Let's warn the developer
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Struts has detected a file upload UI tag (s:file) being used without a form set to method 'POST'. This is probably an error!");
                }
            }
        }

        if (accept != null) {
            addParameter("accept", findString(accept));
        }

        if (size != null) {
            addParameter("size", findString(size));
        }
    }

    @StrutsTagAttribute(description="HTML accept attribute to indicate accepted file mimetypes")
    public void setAccept(String accept) {
        this.accept = accept;
    }

    @StrutsTagAttribute(description="HTML size attribute", required=false, type="Integer")
    public void setSize(String size) {
        this.size = size;
    }
}
