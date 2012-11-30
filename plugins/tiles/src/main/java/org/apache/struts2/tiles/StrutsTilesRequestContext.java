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

package org.apache.struts2.tiles;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.freemarker.FreemarkerResult;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Default implementation of TilesUtil.
 * This class contains default implementation of utilities. This implementation
 * is intended to be used without Struts.
 * <p/>
 * TilesUtilImpl implementation used to intercept .ftl requests and
 * ensure that they are setup properly to take advantage of the
 * {@link FreemarkerResult}.
 *
 * @version $Id$
 */
public class StrutsTilesRequestContext extends TilesRequestContextWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsTilesRequestContext.class);

    /**
     * The mask used to detect requests which should be intercepted.
     */
    private String mask;

    /**
     * Default constructor.
     * Sets the mask to '.ftl'
     *
     * @param context
     */
    public StrutsTilesRequestContext(TilesRequestContext context) {
        this(context, ".ftl");
    }

    /**
     * Optional constructor used to specify a specific mask.
     *
     * @param mask
     * @param context
     */
    public StrutsTilesRequestContext(TilesRequestContext context, String mask) {
        super(context);
        this.mask = mask;
    }

    public void dispatch(String include) throws IOException {
    	if (include.endsWith(mask)) {
            // FIXME This way FreeMarker results still don't have a content-type!
    	    include(include);
        } else {
            super.dispatch(include);
        }
    }

    /**
     * Enhancement of the default include which allows for freemarker
     * templates to be intercepted so that the FreemarkerResult can
     * be used in order to setup the appropriate model.
     *
     * @throws IOException
     */
    public void include(String include) throws IOException {
        if (include.endsWith(mask)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Intercepting tiles include '" + include + "'. Processing as freemarker result.");
            }
            HttpServletRequest request = (HttpServletRequest) getRequest();
            HttpServletResponse response = (HttpServletResponse) getResponse();

            ActionContext ctx = ServletActionContext.getActionContext(request);
            ActionInvocation invocation = ctx.getActionInvocation();

            try {
                FreemarkerResult result = new FreemarkerResult();
                result.setWriter(response.getWriter());

                Container container = ctx.getContainer();
                container.inject(result);

                result.doExecute(include, invocation);
            } catch (Exception e) {
                LOG.error("Error invoking Freemarker template", e);
                throw new IOException("Error invoking Freemarker template." + e.getMessage());
            }
        } else {
            super.include(include);
        }
    }

}
