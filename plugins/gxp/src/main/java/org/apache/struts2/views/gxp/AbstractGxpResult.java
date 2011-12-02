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

import com.opensymphony.xwork2.Result;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * The abstract base class for our Struts 2 GXP result type implementation. It
 * outputs GXP, and pulls GXP parameters from Struts 2's value stack. Implementing
 * classes have to:
 * <ol>
 * <li>Implement <code>execute(ActionInvocation)</code>, which must instruct the
 * GXP to write itself to the output stream. See {@link GxpResult} for a
 * sample implementation.</li>
 * <li>Add a <code>public static final</code> field <b>DEFAULT_PARAM</b> with
 * the value 'gxpName'. Struts 2 needs this to set the name of your
 * template into this object.</li>
 * </ol>
 * <p/>
 * <p>If you want to use instantiated GXPs (using the nested
 * {@code Interface}), you can set the use{@code useInstances} parameter to
 * {@code true}:
 * <pre>
 *     &lt;result-types>
 *       &lt;result-type name="gxp" class="org.apache.struts2.views.gxp.GxpResult">
 *         &lt;param name="useInstances">true&lt;/param>
 *       &lt;/result-type>
 *     &lt;/result-types>
 * </pre>
 * This means that Struts 2 will attempt to instantiate the {@code Interface}
 * using the {@link com.opensymphony.xwork2.ObjectFactory}. If
 * {@link com.google.webwork.GuiceWebWorkIntegrationModule} is installed, or
 * {@link com.google.webwork.ContainerObjectFactory} is set as the static
 * {@code ObjectFactory} instance, then Guice will be used to instantiate the
 * GXP instance; otherwise, only GXPs with no constructor parameters will work.
 *
 * @author Bob Lee
 */
public abstract class AbstractGxpResult implements Result {

    private boolean useInstances = false;
    private String gxpName;

    public void setGxpName(String gxpName) {
        this.gxpName = gxpName;
    }

    protected final String getGxpName() {
        return gxpName;
    }

    public void setUseInstances(boolean useInstances) {
        this.useInstances = useInstances;
    }

    protected final boolean getUseInstances() {
        return useInstances;
    }

    /**
     * Provides resources necessary to execute a GXP.
     */
    protected interface GxpResourceProvider {
        Writer getWriter() throws IOException;

        Locale getLocale();
    }

    /**
     * Uses reasonable defaults to provide resources.
     */
    protected static class DefaultProvider implements GxpResourceProvider {

        private final String contentType;

        public DefaultProvider(String contentType) {
            this.contentType = contentType;
        }

        public Writer getWriter() throws IOException {
            setContentType();
            return ServletActionContext.getResponse().getWriter();
        }

        public Locale getLocale() {
            return ServletActionContext.getRequest().getLocale();
        }

        void setContentType() {
            HttpServletResponse response = ServletActionContext.getResponse();
            // set content type if it hasn't already been set.
            if (response.getContentType() == null || response.getContentType().isEmpty()) {
                response.setContentType(contentType);
            }
            // If no character encoding was set in the content type, default to UTF-8.
            if (response.getCharacterEncoding() == null || response.getCharacterEncoding().isEmpty()) {
                response.setCharacterEncoding("UTF-8");
            }
        }
    }

}
