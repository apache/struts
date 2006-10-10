/*
 * $Id: StrutsSpringObjectFactory.java 439747 2006-09-03 09:22:46Z mrdon $
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
package org.apache.struts2.tiles;

import org.apache.tiles.TilesUtilImpl;
import org.apache.struts2.views.freemarker.FreemarkerResult;
import org.apache.struts2.ServletActionContext;

import javax.servlet.jsp.PageContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.opensymphony.xwork2.ActionInvocation;
import freemarker.template.TemplateException;

/**
 *
 * Default implementation of TilesUtil.
 * This class contains default implementation of utilities. This implementation
 * is intended to be used without Struts.
 *
 * TilesUtilImpl implementation used to intercept .ftl requests and
 * ensure that they are setup properly to take advantage of the
 * {@link FreemarkerResult}.
 *
 * @version $Id$
 *
 */
public class StrutsTilesUtilImpl extends TilesUtilImpl {

    /**
     * The mask used to detect requests which should be intercepted.
     */
    private String mask;

    /**
     * Default constructor.
     * Sets the mask to '.ftl'
     */
    public StrutsTilesUtilImpl() {
        mask = ".ftl";
    }

    /**
     * Optional constructor used to specify a specific mask.
     * @param mask
     */
    public StrutsTilesUtilImpl(String mask) {
        this.mask = mask;
    }

    /**
     * Enhancement of the default include which allows for freemarker
     * templates to be intercepted so that the FreemarkerResult can
     * be used in order to setup the appropriate model.
     *
     * @param string the included resource
     * @param pageContext the current page context
     * @param b whether or not a flush should occur
     * @throws IOException
     * @throws ServletException
     * @throws Exception 
     */
    public void doInclude(String string, PageContext pageContext, boolean b) throws Exception {
        if(string.endsWith(".ftl")) {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            ActionInvocation invocation = ServletActionContext.getActionContext(request).getActionInvocation();
            FreemarkerResult result = new FreemarkerResult();
            try {
                result.doExecute(string, invocation);
            } catch (TemplateException e) {
                log.error("Error invoking Freemarker template", e);
                throw new ServletException("Error invoking Freemarker template.", e);
            }
        }
        else {
            super.doInclude(string, pageContext, b);
        }
    }
}
