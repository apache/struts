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
package org.apache.struts2.conversion;

/**
 * Interface for processors that support late initialization of user conversion properties.
 * <p>
 * Implementations provide user conversion properties processing after the full container
 * is built, allowing Spring bean name resolution for type converters.
 * </p>
 *
 * @see <a href="https://issues.apache.org/jira/browse/WW-4291">WW-4291</a>
 * @since 6.9.0
 */
public interface UserConversionPropertiesProvider {

    /**
     * Process user conversion properties (struts-conversion.properties, xwork-conversion.properties).
     * Called during late initialization when SpringObjectFactory is available.
     */
    void initUserConversions();
}
