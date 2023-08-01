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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * A (localized) resource accessible through the ApplicationContext.
 * Typically, this is a file inside the web application's war.
 */
public interface ApplicationResource {

    /**
     * Get the path name for this resource.
     * You can access this resource by passing the path to
     * {@link ApplicationContext#getResource(String) getResource}.
     *
     * @return the path including localization.
     */
    String getLocalePath();

    /**
     * Get the path name for this resource. Multiple versions of
     * a resource can share the same path if the locale part is different.
     *
     * @return the path excluding localization.
     */
    String getPath();

    /**
     * Get the Locale for this resource.
     *
     * @return the Locale.
     */
    Locale getLocale();

    /**
     * Get the path name of another version of the resource.
     *
     * @param locale the Locale for the new version.
     * @return the path including localization.
     */
    String getLocalePath(Locale locale);

    /**
     * Get a java.io.InputStream to read the contents of this resource.
     *
     * @return the InputStream.
     * @throws IOException if the contents cannot be read.
     */
    InputStream getInputStream() throws IOException;

    /**
     * Get the last modification date for this resource.
     *
     * @return the difference, measured in milliseconds, between the current
     * time and midnight, January 1, 1970 UTC.
     * @throws IOException if the last modification date cannot be found.
     */
    long getLastModified() throws IOException;
}
