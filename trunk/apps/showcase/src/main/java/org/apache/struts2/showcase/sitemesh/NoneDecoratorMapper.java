package org.apache.struts2.showcase.sitemesh;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 */
public class NoneDecoratorMapper extends AbstractDecoratorMapper {
    public Decorator getDecorator(HttpServletRequest req, Page page) {
        if ("none".equals(req.getAttribute("decorator"))) {
            return null;
        }

        return super.getDecorator(req, page);
    }
}
