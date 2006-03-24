/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.components.template;

import java.util.Map;

/**
 * Any template language which wants to support UI tag templating needs to provide an implementation of this interface
 * to handle rendering the template
 *
 * @author jcarreira
 */
public interface TemplateEngine {

    /**
     * Renders the template
     * @param templateContext  context for the given template.
     * @throws Exception is thrown if there is a failure when rendering.
     */
    void renderTemplate(TemplateRenderingContext templateContext) throws Exception;

    /**
     * Get's the properties for the given template.
     *
     * @param template   the template.
     * @return  the properties as key value pairs.
     */
    Map getThemeProps(Template template);
    
}
