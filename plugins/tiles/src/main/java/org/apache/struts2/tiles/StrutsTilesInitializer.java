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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.startup.AbstractTilesInitializer;

import javax.servlet.ServletContext;

public class StrutsTilesInitializer extends AbstractTilesInitializer {

    public static final String STRUTS_TILES_ALTERNATE_FILERESOURCEHANDLING = "struts.tiles.alternateFileResourceHandling";

    private static final Logger LOG = LogManager.getLogger(StrutsTilesInitializer.class);

    @Override
    protected ApplicationContext createTilesApplicationContext(ApplicationContext preliminaryContext) {
        ServletContext servletContext = (ServletContext) preliminaryContext.getContext();

        // Opt-in alternate file resource handling for WW-5011
        if (Boolean.parseBoolean(servletContext.getInitParameter(STRUTS_TILES_ALTERNATE_FILERESOURCEHANDLING))) {
            StrutsWildcardServletApplicationContext.setUseAlternateFileResourceHandling(true);
        } else {
            StrutsWildcardServletApplicationContext.setUseAlternateFileResourceHandling(false);
        }
        LOG.trace("Tiles alternate file resourcehandling: " + StrutsWildcardServletApplicationContext.isUseAlternateFileResourceHandling());

        if (servletContext.getInitParameter(DefinitionsFactory.DEFINITIONS_CONFIG) != null) {
            LOG.trace("Found definitions config in web.xml, using standard Servlet support ....");
            return new ServletApplicationContext(servletContext);
        } else {
            LOG.trace("Initializing Tiles wildcard support ...");
            return new StrutsWildcardServletApplicationContext(servletContext);
        }
    }

    @Override
    protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
        LOG.trace("Creating dedicated Struts factory to create Tiles container");
        return new StrutsTilesContainerFactory();
    }

}
