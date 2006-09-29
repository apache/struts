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

import org.apache.tiles.listener.TilesListener;
import org.apache.tiles.TilesUtilImpl;
import org.apache.tiles.TilesUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

/**
 * Custom TilesListener which can be used to allow freemarker
 * invocation from tiles components.
 *
 * @version $Rev: 397992 $ $Date$
 */
public class StrutsTilesListener extends TilesListener {

    /**
     * The key used to identify the freemarker mask.
     */
    public static final String MASK_INIT_PARAM = "freemarker-mask";

    /**
     * Configured mask;
     */
    private String mask;

    /**
     * Initialize the tiles system, overriding the TilesUtilImpl
     * @param servletContextEvent
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        mask = context.getInitParameter(MASK_INIT_PARAM);

        if(mask == null) {
            mask = ".ftl";
        }

        TilesUtilImpl tilesUtil = new StrutsTilesUtilImpl();
        TilesUtil.setTilesUtil(tilesUtil);
        super.contextInitialized(servletContextEvent);
    }
}
