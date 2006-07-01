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
package org.apache.struts2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders an HTML file input element.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:file name="anUploadFile" accept="text/*" /&gt;
 * &lt;a:file name="anohterUploadFIle" accept="text/html,text/plain" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @a2.tag name="file" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.FileTag"
 * description="Render a file input field"
 */
public class File extends UIBean {
    private final static Log log = LogFactory.getLog(File.class);

    final public static String TEMPLATE = "file";

    protected String accept;
    protected String size;

    public File(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
                log.warn("Struts Action Framework has detected a file upload UI tag (a:file) being used without a form set to enctype 'multipart/form-data'. This is probably an error!");
            }

            String method = (String) form.getParameters().get("method");
            if (!"post".equalsIgnoreCase(method)) {
                // uh oh, this isn't good! Let's warn the developer
                log.warn("Struts Action Framework has detected a file upload UI tag (a:file) being used without a form set to method 'POST'. This is probably an error!");
            }
        }

        if (accept != null) {
            addParameter("accept", findString(accept));
        }

        if (size != null) {
            addParameter("size", findString(size));
        }
    }

    /**
     * HTML accept attribute to indicate accepted file mimetypes
     * @a2.tagattribute required="false"
     */
    public void setAccept(String accept) {
        this.accept = accept;
    }

    /**
     * HTML size attribute
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setSize(String size) {
        this.size = size;
    }
}
