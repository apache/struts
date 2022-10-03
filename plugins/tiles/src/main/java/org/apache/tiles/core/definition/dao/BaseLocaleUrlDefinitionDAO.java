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
package org.apache.tiles.core.definition.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.api.Definition;
import org.apache.tiles.core.definition.DefinitionsFactoryException;
import org.apache.tiles.core.definition.DefinitionsReader;
import org.apache.tiles.core.definition.RefreshMonitor;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Base abstract class for a DAO that is based on URLs and locale as a
 * customization key.
 *
 * @since 2.1.0
 */
public abstract class BaseLocaleUrlDefinitionDAO implements DefinitionDAO<Locale>, RefreshMonitor {

    /**
     * The logging object.
     */
    private static final Logger LOG = LogManager.getLogger(BaseLocaleUrlDefinitionDAO.class);

    /**
     * Contains the URL objects identifying where configuration data is found.
     *
     * @since 2.1.0
     */
    protected List<ApplicationResource> sources;

    /**
     * Contains the dates that the URL sources were last modified.
     *
     * @since 2.1.0
     */
    protected Map<String, Long> lastModifiedDates;

    /**
     * Reader used to get definitions from the sources.
     *
     * @since 2.1.0
     */
    protected DefinitionsReader reader;

    /**
     * ApplicationContext to locate the source files.
     *
     * @since 3.0.0
     */
    protected ApplicationContext applicationContext;

    /**
     * Constructor.
     */
    public BaseLocaleUrlDefinitionDAO(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        lastModifiedDates = new HashMap<>();
    }

    public void setSources(List<ApplicationResource> sources) {
        // filter out any sources that are already localized
        ArrayList<ApplicationResource> defaultSources = new ArrayList<>();
        for (ApplicationResource source : sources) {
            if (Locale.ROOT.equals(source.getLocale())) {
                defaultSources.add(source);
            }
        }
        this.sources = defaultSources;
    }

    public void setReader(DefinitionsReader reader) {
        this.reader = reader;
    }

    /**
     * {@inheritDoc}
     */
    public boolean refreshRequired() {
        boolean status = false;

        Set<String> paths = lastModifiedDates.keySet();

        try {
            for (String path : paths) {
                Long lastModifiedDate = lastModifiedDates.get(path);
                ApplicationResource resource = applicationContext.getResource(path);
                long newModDate = resource.getLastModified();
                if (newModDate != lastModifiedDate) {
                    status = true;
                    break;
                }
            }
        } catch (IOException e) {
            LOG.warn("Exception while monitoring update times.", e);
            return true;
        }
        return status;
    }

    /**
     * Loads definitions from an URL without loading from "parent" URLs.
     *
     * @param resource The URL to read.
     * @return The definition map that has been read.
     */
    protected Map<String, Definition> loadDefinitionsFromResource(ApplicationResource resource) {
        Map<String, Definition> defsMap = null;

        InputStream stream = null;
        try {
            lastModifiedDates.put(resource.getLocalePath(), resource
                .getLastModified());

            // Definition must be collected, starting from the base
            // source up to the last localized file.
            stream = resource.getInputStream();
            defsMap = reader.read(stream);
        } catch (FileNotFoundException e) {
            // File not found. continue.
            LOG.debug("File {} not found, continue", resource);
        } catch (IOException e) {
            throw new DefinitionsFactoryException("I/O error processing configuration.", e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ignore) {
            }
        }

        return defsMap;
    }
}
