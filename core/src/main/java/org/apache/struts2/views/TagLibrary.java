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

package org.apache.struts2.views;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Provides Velocity and Freemarker implementation classes for a tag library
 *
 * @deprecated use two other interfaces: {@link TagLibraryDirectiveProvider}, {@link TagLibraryModelProvider}
 */
@Deprecated
public interface TagLibrary {

    /**
     * Gets a Java object that contains getters for the tag library's Freemarker models.  
     * Called once per Freemarker template processing.
     *
     * @param stack The current value stack
     * @param req The HTTP request
     * @param res The HTTP response
     * @return The Java object containing the Freemarker model getter methods
     */
    public Object getFreemarkerModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res);

    /**
     * Gets a list of Velocity directive classes for the tag library.  Called once on framework
     * startup when initializing Velocity.
     *
     * @return A list of Velocity directive classes
     */
    public List<Class> getVelocityDirectiveClasses();
}
