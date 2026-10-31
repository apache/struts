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
package org.apache.tiles.core.prepare.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.api.preparer.ViewPreparer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.reflect.ClassUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the {@link org.apache.tiles.core.prepare.factory.PreparerFactory}.
 * This factory provides no contextual configuration.  It
 * simply instantiates the named preparerInstance and returns it.
 *
 * @since Tiles 2.0
 */
public class BasicPreparerFactory implements PreparerFactory {

    /**
     * The logging object.
     */
    private static final Logger LOG = LogManager.getLogger(BasicPreparerFactory.class);

    /**
     * Maps a preparer name to the instantiated preparer.
     */
    protected Map<String, ViewPreparer> preparers;

    /**
     * Constructor.
     */
    public BasicPreparerFactory() {
        this.preparers = new ConcurrentHashMap<>();
    }


    /**
     * Create a new instance of the named preparerInstance.  This factory
     * expects all names to be qualified class names.
     *
     * @param name    the named preparerInstance
     * @param context current context
     * @return ViewPreparer instance
     */
    public ViewPreparer getPreparer(String name, Request context) {
        ViewPreparer preparer = preparers.get(name);
        if (preparer != null) {
            return preparer;
        }

        preparer = createPreparer(name);
        if (preparer == null) {
            // A lookup that did not produce a preparer is not recorded. Caching it would make a
            // single failure permanent for the lifetime of the factory, even when the cause was
            // transient, and a cached null was previously reported as resolved by containsKey().
            return null;
        }

        // Not computeIfAbsent: createPreparer may load classes and call back into the container,
        // which must not run while a bin of this map is locked.
        ViewPreparer existing = preparers.putIfAbsent(name, preparer);
        return existing != null ? existing : preparer;
    }

    /**
     * Creates a view preparer for the given name.
     *
     * @param name The name of the preparer.
     * @return The created preparer.
     */
    protected ViewPreparer createPreparer(String name) {
        LOG.debug("Creating ViewPreparer '{}'", name);
        return ClassUtil.instantiate(name, ViewPreparer.class, true);
    }
}
