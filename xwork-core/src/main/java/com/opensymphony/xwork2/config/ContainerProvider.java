/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;


/**
 * Provides beans and constants/properties for the Container
 * 
 * @since 2.1
 */
public interface ContainerProvider {

    /**
     * Called before removed from the configuration manager
     */
    public void destroy();
    
    /**
     * Initializes with the configuration
     * @param configuration The configuration
     * @throws ConfigurationException If anything goes wrong
     */
    public void init(Configuration configuration) throws ConfigurationException;
    
    /**
     * Tells whether the ContainerProvider should reload its configuration
     *
     * @return <tt>true</tt>, whether the ContainerProvider should reload its configuration, <tt>false</tt>otherwise.
     */
    public boolean needsReload();
    
    /**
     * Registers beans and properties for the Container
     * 
     * @param builder The builder to register beans with
     * @param props The properties to register constants with
     * @throws ConfigurationException If anything goes wrong
     */
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException;
    
}
