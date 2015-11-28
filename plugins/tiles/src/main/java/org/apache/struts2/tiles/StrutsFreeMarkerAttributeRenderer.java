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
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import freemarker.ext.beans.BeanModel;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.JspSupportServlet;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.FreemarkerResult;
import org.apache.struts2.views.freemarker.StrutsBeanWrapper;
import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.freemarker.template.TilesFMModelRepository;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.renderer.impl.AbstractTypeDetectingAttributeRenderer;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class StrutsFreeMarkerAttributeRenderer extends AbstractTypeDetectingAttributeRenderer {

    private static Logger LOG = LoggerFactory.getLogger(StrutsFreeMarkerAttributeRenderer.class);

    @Override
    public void write(Object value, Attribute attribute, TilesRequestContext request) throws IOException {
        if (value != null) {
            if (value instanceof String) {
                LOG.trace("Rendering freemarker tile ...");

                ServletTilesRequestContext servletRequest = ServletUtil.getServletRequest(request);
                HttpServletRequest httpRequest = servletRequest.getRequest();

                ActionContext ctx = ServletActionContext.getActionContext(httpRequest);
                if (ctx == null) {
                    throw new ConfigurationException("There is no ActionContext for current request!");
                }

                registerTilesBeanModel(ctx);

                String include = (String) value;
                FreemarkerResult result = new FreemarkerResult(include);
                result.setWriter(request.getWriter());

                Container container = ctx.getContainer();
                container.inject(result);

                try {
                    ActionInvocation invocation = ctx.getActionInvocation();
                    result.doExecute(include, invocation);
                } catch (TemplateException e) {
                    LOG.error("Exception was thrown during rendering value {}: {}", value, e.getMessage());
                    throw new InvalidTemplateException(e);
                }
            } else {
                LOG.error("Value {} is not a String, cannot render template!", value);
                throw new InvalidTemplateException("Cannot render a template that is not a string: " + String.valueOf(value));
            }
        } else {
            LOG.error("Value is null, cannot render template!");
            throw new InvalidTemplateException("Cannot render a null template");
        }
    }

    public boolean isRenderable(Object value, Attribute attribute, TilesRequestContext request) {
        if (value instanceof String) {
            String string = (String) value;
            return string.startsWith("/") && string.endsWith(".ftl");
        }
        return false;
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
