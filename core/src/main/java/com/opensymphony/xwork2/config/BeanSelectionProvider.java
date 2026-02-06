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
 * A {@link ConfigurationProvider} that selects and aliases bean implementations.
 * <p>
 * Implementations of this interface are responsible for selecting which bean implementation
 * to use for a given interface type. The selection is typically based on configuration properties
 * that specify the bean name or class name.
 * </p>
 * <p>
 * The aliasing mechanism works as follows:
 * </p>
 * <ol>
 *   <li>Look for a bean by the name specified in the configuration property</li>
 *   <li>If found, alias it to the default name so it becomes the default implementation</li>
 *   <li>If not found, try to load the value as a class name and register it as a factory</li>
 *   <li>If class loading fails, delegate to {@link org.apache.struts2.ObjectFactory} at runtime
 *       (useful for Spring bean names)</li>
 * </ol>
 *
 * @see AbstractBeanSelectionProvider
 * @see StrutsBeanSelectionProvider
 */
public interface BeanSelectionProvider extends ConfigurationProvider {

}
