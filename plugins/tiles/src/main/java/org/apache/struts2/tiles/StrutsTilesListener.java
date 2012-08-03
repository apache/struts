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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.factory.TilesContainerFactory;
import org.apache.tiles.web.startup.TilesListener;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Listener used to automatically inject ServletContext
 * init parameters so that they don't need to be configured
 * explicitly for tiles integration.  This is provided
 * mainly for backwards compatibility with Struts 2.0.1
 * configuration.
 *
 * @since Struts 2.0.2
 * @version $Rev$
 *
 */
public class StrutsTilesListener extends TilesListener {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsTilesListener.class);

    private static final Map<String, String> INIT;

    static {
        INIT = new HashMap<String, String>();
        INIT.put(TilesContainerFactory.CONTAINER_FACTORY_INIT_PARAM,
                 StrutsTilesContainerFactory.class.getName());
    }

    protected TilesContainer createContainer(ServletContext context)
    throws TilesException {
        if(context.getInitParameter(TilesContainerFactory.CONTEXT_FACTORY_INIT_PARAM) == null) {
            context = decorate(context);
        }
        else {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Tiles container factory is explicitly set.  Not injecting struts configuration.");
            }
        }
        return super.createContainer(context);
    }

    protected ServletContext decorate(ServletContext context) {
        return new ConfiguredServletContext(context, INIT);
    }

}
