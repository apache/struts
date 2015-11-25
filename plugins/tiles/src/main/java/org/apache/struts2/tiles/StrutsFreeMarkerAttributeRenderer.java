package org.apache.struts2.tiles;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Container;
import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.freemarker.FreemarkerResult;
import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.renderer.impl.AbstractTypeDetectingAttributeRenderer;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class StrutsFreeMarkerAttributeRenderer extends AbstractTypeDetectingAttributeRenderer {

    private static Logger LOG = LogManager.getLogger(StrutsFreeMarkerAttributeRenderer.class);

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
                ActionInvocation invocation = ctx.getActionInvocation();

                String include = (String) value;
                FreemarkerResult result = new FreemarkerResult(include);
                result.setWriter(request.getWriter());

                Container container = ctx.getContainer();
                container.inject(result);

                try {
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

}
