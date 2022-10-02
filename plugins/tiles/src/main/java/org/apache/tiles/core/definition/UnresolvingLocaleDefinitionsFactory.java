/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.core.definition;

import org.apache.tiles.api.Definition;
import org.apache.tiles.request.Request;
import org.apache.tiles.core.definition.dao.DefinitionDAO;
import org.apache.tiles.core.locale.LocaleResolver;

import java.util.Locale;

/**
 * {@link DefinitionsFactory DefinitionsFactory} implementation that manages
 * Definitions configuration data from URLs, without resolving definition
 * inheritance when a definition is returned.<p/>
 * <p>
 * The Definition objects are read from the
 * {@link org.apache.tiles.core.definition.digester.DigesterDefinitionsReader}
 * class unless another implementation is specified.
 * </p>
 * @since 2.2.1
 */
public class UnresolvingLocaleDefinitionsFactory implements DefinitionsFactory {

    /**
     * The definition DAO that extracts the definitions from the sources.
     *
     * @since 2.2.1
     */
    protected DefinitionDAO<Locale> definitionDao;

    /**
     * The locale resolver object.
     *
     * @since 2.2.1
     */
    protected LocaleResolver localeResolver;

    /**
     * Sets the locale resolver to use.
     *
     * @param localeResolver The locale resolver.
     * @since 2.2.1
     */
    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    /**
     * Sets the definition DAO to use. It must be locale-based.
     *
     * @param definitionDao The definition DAO.
     * @since 2.2.1
     */
    public void setDefinitionDAO(DefinitionDAO<Locale> definitionDao) {
        this.definitionDao = definitionDao;
    }

    /** {@inheritDoc} */
    public Definition getDefinition(String name, Request tilesContext) {
        Locale locale = null;

        if (tilesContext != null) {
            locale = localeResolver.resolveLocale(tilesContext);
        }

        return definitionDao.getDefinition(name, locale);
    }
}
