/*
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
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Container;
import freemarker.ext.beans.BeanModel;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.JspSupportServlet;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.FreemarkerResult;
import org.apache.struts2.views.freemarker.StrutsBeanWrapper;
import org.apache.tiles.freemarker.template.TilesFMModelRepository;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.servlet.ServletUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class StrutsFreeMarkerAttributeRenderer implements Renderer {

    private static Logger LOG = LogManager.getLogger(StrutsFreeMarkerAttributeRenderer.class);

    @Override
    public void render(String path, Request request) throws IOException {
        if (path != null) {
            LOG.trace("Rendering freemarker tile [{}]", path);

            ActionContext ctx = readActionContext(request);

            registerTilesBeanModel(ctx);

            FreemarkerResult result = new FreemarkerResult(path);
            result.setWriter(request.getWriter());

            Container container = ctx.getContainer();
            container.inject(result);

            try {
                ActionInvocation invocation = ctx.getActionInvocation();
                result.doExecute(path, invocation);
            } catch (TemplateException e) {
                LOG.error("Exception was thrown during rendering value {}: {}", path, e.getMessage());
                throw new InvalidTemplateException(e);
            }
        } else {
            LOG.error("Path is null, cannot render template!");
            throw new InvalidTemplateException("Cannot render a null template");
        }
    }

    /**
     * Depending how Tiles definition was defined, request can an instance of JspRequest (for JSPs)
     * or a ServletRequest (FreeMarker)
     */
    protected ActionContext readActionContext(Request request) {
        LOG.debug("Obtaining HttpServletRequest based on [{}]", request.getClass().getName());

        HttpServletRequest httpRequest = ServletUtil.getServletRequest(request).getRequest();
        ActionContext ctx = ServletActionContext.getActionContext(httpRequest);

        if (ctx == null) {
            LOG.error("Cannot obtain HttpServletRequest from [{}]", request.getClass().getName());
            throw new ConfigurationException("There is no ActionContext for current request!");
        }

        return ctx;
    }

    @Override
    public boolean isRenderable(String path, Request request) {
        LOG.trace("Checking if path [{}] can be rendered", path);
        return path != null && path.startsWith("/") && path.endsWith(".ftl");
    }

    /**
     * This register dedicated BeanModel to support tiles tags.
     * It requires {@link org.apache.struts2.views.JspSupportServlet} to be registered in web.xml
     */
    protected void registerTilesBeanModel(ActionContext ctx) {
        ServletContext servletContext = ServletActionContext.getServletContext();
        Configuration configuration = ctx.getInstance(FreemarkerManager.class).getConfiguration(servletContext);

        StrutsBeanWrapper wrapper = (StrutsBeanWrapper) ctx.getInstance(FreemarkerManager.class).getWrapper();

        LOG.trace("Adding support for Tiles tags, please remember to register {} in web.xml!", JspSupportServlet.class.getName());

        BeanModel tilesBeanModel = new BeanModel(new TilesFMModelRepository(), wrapper);
        configuration.setSharedVariable("tiles", tilesBeanModel);
    }

}
