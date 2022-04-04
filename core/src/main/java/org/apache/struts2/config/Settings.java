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

package org.apache.struts2.config;

import com.opensymphony.xwork2.util.location.Location;

import java.util.Iterator;

/**
 * Settings retrieves and exposes default values used by the framework.
 */
interface Settings {

    /**
     * Retrieve value for provided name
     *
     * @param name The name of the setting value to retrieve
     * @return The setting value as a String or null
     */
    String get(String name);

    /**
     * Returns {@link com.opensymphony.xwork2.util.location.Location} of given setting
     *
     * @param name Name of the setting to locate
     * @return The location  of the setting or null
     */
    Location getLocation(String name);

    /**
     * Returns {@link java.util.Iterator} with all values
     *
     * @return A list of the settings as an iterator
     */
    Iterator list();

}
