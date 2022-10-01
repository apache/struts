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

/**
 * Entry point to get information about the application.
 */
public final class ApplicationAccess {

    /**
     * The attribute name that will be used to store the application context itself.
     */
    public static final String APPLICATION_CONTEXT_ATTRIBUTE = ApplicationContext.class.getName() + ".ATTRIBUTE";

    /**
     * Constructor.
     */
    private ApplicationAccess() {
    }

    /**
     * Registers an application context. It will be registered into itself as an
     * attribute, using the {@link #APPLICATION_CONTEXT_ATTRIBUTE} name.
     *
     * @param applicationContext The application context to register.
     */
    public static void register(ApplicationContext applicationContext) {
        applicationContext.getApplicationScope().put(APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
    }

}
