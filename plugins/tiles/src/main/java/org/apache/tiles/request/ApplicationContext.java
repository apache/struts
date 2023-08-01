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
package org.apache.tiles.request;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Defines a set of methods which tiles use to communicate to
 * the tiles container and runtime environment.  There is only
 * one application context per container.
 */
public interface ApplicationContext {

    /**
     * Returns the original, technology-dependent, context.
     *
     * @return The original application context.
     */
    Object getContext();

    /**
     * Returns a mutable Map that maps application scope attribute names to
     * their values.
     *
     * @return Map of key value pairs.
     */
    Map<String, Object> getApplicationScope();

    /**
     * Return an immutable Map that maps context application initialization
     * parameters to their values.
     *
     * @return initialization parameters
     */
    Map<String, String> getInitParams();

    /**
     * Return the application resource mapped to the specified path.
     *
     * @param localePath path to the desired resource, including the Locale suffix.
     * @return the first located resource which matches the given path or null if no such resource exists.
     */
    ApplicationResource getResource(String localePath);

    /**
     * Return a localized version of an ApplicationResource.
     *
     * @param base   the ApplicationResource.
     * @param locale the desired Locale.
     * @return the first located resource which matches the given path or null if no such resource exists.
     */
    ApplicationResource getResource(ApplicationResource base, Locale locale);

    /**
     * Return the application resources mapped to the specified path.
     *
     * @param path to the desired resource.
     * @return all resources which match the given path.
     */
    Collection<ApplicationResource> getResources(String path);
}
