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

import javax.servlet.ServletContext;

/**
 * Dedicated interface to allow inject {@link ServletContext} and perform initialisation based in it
 */
public interface ServletContextAwareConfigurationProvider extends ConfigurationProvider {

    /**
     * This is a lazy constructor, called just after class was initiated
     *
     * @param servletContext current instance of {@link ServletContext}
     */
    void initWithContext(ServletContext servletContext);

}
