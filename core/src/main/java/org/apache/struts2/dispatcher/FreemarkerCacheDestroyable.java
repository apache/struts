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
package org.apache.struts2.dispatcher;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.views.freemarker.FreemarkerManager;

import jakarta.servlet.ServletContext;

/**
 * WW-5537: Clears FreeMarker's template and class introspection caches
 * stored in {@link ServletContext} during application undeploy, preventing
 * classloader leaks.
 *
 * @since 7.1.0
 */
public class FreemarkerCacheDestroyable implements ContextAwareDestroyable {

    private static final Logger LOG = LogManager.getLogger(FreemarkerCacheDestroyable.class);

    @Override
    public void destroy(ServletContext servletContext) {
        if (servletContext == null) {
            return;
        }
        Object fmConfig = servletContext.getAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY);
        if (fmConfig instanceof Configuration cfg) {
            cfg.clearTemplateCache();
            cfg.clearEncodingMap();
            if (cfg.getObjectWrapper() instanceof BeansWrapper bw) {
                bw.clearClassIntrospectionCache();
            }
            servletContext.removeAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY);
            LOG.debug("FreeMarker configuration cleaned up");
        }
    }
}
