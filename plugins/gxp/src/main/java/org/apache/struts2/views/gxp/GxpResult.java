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
package org.apache.struts2.views.gxp;

import com.google.gxp.base.GxpContext;
import com.google.gxp.html.HtmlClosure;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

import java.io.IOException;

/**
 * Struts 2 GXP result type implementation. Outputs GXP. Pulls GXP parameters
 * from Struts 2's value stack.
 * <p/>
 * <p>Declare the GXP result type for your package in the xwork.xml file:</p>
 * <p/>
 * <pre>
 *     &lt;result-types>
 *       &lt;result-type name="gxp" class="org.apache.struts2.views.gxp.GxpResult"/>
 *     &lt;/result-types>
 * </pre>
 * <p/>
 * <p>Or if you want to output XML instead of HTML:</p>
 * <p/>
 * <pre>
 *     &lt;result-types>
 *       &lt;result-type name="gxp" class="org.apache.struts2.views.gxp.GxpResult">
 *         &lt;param name="outputXml">true&lt;/param>
 *       &lt;/result-type>
 *     &lt;/result-types>
 * </pre>
 * <p/>
 * <p>Outputting XML changes the content type from text/html to application/xml
 * and configures the {@link GxpContext} to output XML. This is useful in
 * situations like specifying the doctype of your GXP to be 'mobile'.</p>
 * <p/>
 * <p>Use the GXP result type for the result of an action. For example:</p>
 * <p/>
 * <pre>
 *   &lt;result name="success" type="gxp">/myPackage/MyGxp.gxp&lt;/result>
 * </pre>
 *
 * @author Bob Lee
 * @see org.apache.struts2.views.gxp.AbstractGxpResult
 */
public class GxpResult extends AbstractGxpResult {

    /**
     * Tells Struts 2 which parameter name to use if it's not already specified.
     */
    public static final String DEFAULT_PARAM = "gxpName";

    private Container container;
    private boolean outputXml = false;

    /**
     * Whether or not this GXP should output XML.
     */
    public void setOutputXml(boolean outputXml) {
        this.outputXml = outputXml;
    }

    protected HtmlClosure getGxpClosure() {
        final Gxp gxp = getUseInstances() ? GxpInstance.getInstance(getGxpName()) : Gxp.getInstance(getGxpName());

        if (null == gxp) {
            // TODO(lwerner): OGNL or Struts 2 seems to be swallowing this exception
            // rather than logging or rethrowing it, so you never see this message
            // TODO(dpb): Is this true now that this work is not done in a setter?
            throw new NullPointerException("The GXP " + getGxpName()
                    + " could not be loaded.  This is probably because you have"
                    + " a typo in your config.");
        }
        container.inject(gxp);
        return new HtmlClosure() {
            public void write(Appendable out, GxpContext gxpContext) throws IOException {
                gxp.write(out, gxpContext);
            }
        };
    }

    /**
     * Tells the GXP to write itself to the output stream.
     */
    public void execute(ActionInvocation actionInvocation) {
        GxpResourceProvider provider = getProvider();
        try {
            getGxpClosure().write(provider.getWriter(), new GxpContext(provider.getLocale(), outputXml));
        } catch (Exception e) {
            throw new RuntimeException("Exception while rendering "
                    + getGxpName()
                    + " coming from "
                    + actionInvocation.getAction().getClass().getName() + ".",
                    e);
        }
    }

    /**
     * Gets appropriate provider.
     */
    GxpResourceProvider getProvider() {
        return new HtmlOrXmlProvider(outputXml);
    }

    /**
     * Uses reasonable defaults to provide resources.
     */
    private static class HtmlOrXmlProvider extends DefaultProvider {

        /**
         * Default content-type to use for responses.
         */
        private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
        private static final String XML_CONTENT_TYPE = "application/xml; charset=UTF-8";

        HtmlOrXmlProvider(boolean outputXml) {
            super(outputXml ? XML_CONTENT_TYPE : HTML_CONTENT_TYPE);
        }
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
}
