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
package com.opensymphony.xwork2.config;

/**
 * Provides configuration packages.  The separate init and loadPackages calls are due to the need to 
 * preserve backwards compatibility with the 2.0 {@link ConfigurationProvider} interface
 * 
 * @since 2.1
 */
public interface PackageProvider {
    
    /**
     * Initializes with the configuration
     * @param configuration The configuration
     * @throws ConfigurationException If anything goes wrong
     */
    void init(Configuration configuration) throws ConfigurationException;
    
    /**
     * Tells whether the PackageProvider should reload its configuration
     *
     * @return <tt>true</tt>, whether the PackageProvider should reload its configuration, <tt>false</tt>otherwise.
     */
    boolean needsReload();

    /**
     * Loads the packages for the configuration.
     * @throws ConfigurationException in case of configuration errors
     */
    void loadPackages() throws ConfigurationException;
    
}
