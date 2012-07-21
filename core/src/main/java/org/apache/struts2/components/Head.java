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

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.annotations.StrutsTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Renders parts of the HEAD section for an HTML file. Encoding can be set using this tag.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;head&gt;
 *   &lt;title&gt;My page&lt;/title&gt;
 *   &lt;s:head/&gt;
 * &lt;/head&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 */
@StrutsTag(name="head",
        tldBodyContent="empty",
        tldTagClass="org.apache.struts2.views.jsp.ui.HeadTag",
        description="Render a chunk of HEAD for your HTML file",
        allowDynamicAttributes = true)
public class Head extends UIBean {
    public static final String TEMPLATE = "head";

    private String encoding;

    public Head(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void evaluateParams() {
        super.evaluateParams();

        addParameter("encoding", encoding);
    }
}
