/*
 * $Id: Restful2ActionMapper.java 540819 2007-05-23 02:48:36Z mrdon $
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
package org.apache.struts2.rest;

import org.apache.struts2.config.ClasspathPackageProvider;

import com.opensymphony.xwork2.util.ResolverUtil.ClassTest;

/**
 * Checks for actions ending in Controller indicating a Rest controller
 */
public class ControllerClasspathPackageProvider extends ClasspathPackageProvider {
    
    @Override
    protected ClassTest createActionClassTest() {
        return new ClassTest() {
            // Match Action implementations and classes ending with "Controller"
            public boolean matches(Class type) {
                return (type.getSimpleName().endsWith("Controller"));
            }
        };
    }
    
    @Override
    protected String getClassSuffix() {
        return "Controller";
    }

}
