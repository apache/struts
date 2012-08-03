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

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesException;
import org.apache.tiles.context.TilesContextFactory;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.factory.TilesContainerFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.preparer.PreparerFactory;

import java.util.Map;


public class StrutsTilesContainerFactory extends TilesContainerFactory {


    @Override
    protected void storeContainerDependencies(Object context, Map<String, String> initParameters, Map<String, String> configuration, BasicTilesContainer container) throws TilesException {
        TilesContextFactory contextFactory =
            (TilesContextFactory) createFactory(configuration,
                CONTEXT_FACTORY_INIT_PARAM);

        contextFactory = new StrutsTilesContextFactory(contextFactory);

        DefinitionsFactory defsFactory =
            (DefinitionsFactory) createFactory(configuration,
                DEFINITIONS_FACTORY_INIT_PARAM);

        PreparerFactory prepFactory =
            (PreparerFactory) createFactory(configuration,
                PREPARER_FACTORY_INIT_PARAM);

        contextFactory.init(configuration);
        TilesApplicationContext tilesContext =
            contextFactory.createApplicationContext(context);

        container.setDefinitionsFactory(defsFactory);
        container.setContextFactory(contextFactory);
        container.setPreparerFactory(prepFactory);
        container.setApplicationContext(tilesContext);
    }

    /**
     * Wrapper factory, used to decorate the TilesRequestContext with a
     * FreemarkerResult aware version.
     * 
     */
    class StrutsTilesContextFactory implements TilesContextFactory {

        private TilesContextFactory factory;

        public StrutsTilesContextFactory(TilesContextFactory factory) {
            this.factory = factory;
        }

        public void init(Map<String, String> map) {
            factory.init(map);
        }

        public TilesApplicationContext createApplicationContext(Object context) {
            return factory.createApplicationContext(context);
        }

        public TilesRequestContext createRequestContext(
                TilesApplicationContext tilesApplicationContext,
                Object... requestItems) {
            TilesRequestContext context = factory.createRequestContext(tilesApplicationContext, requestItems);
            return new StrutsTilesRequestContext(context);
        }
    }
}
