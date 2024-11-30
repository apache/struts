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
package org.apache.tiles.request.velocity.render;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.servlet.ServletUtil;
import org.apache.velocity.tools.view.JeeConfig;

/**
 * Implements JeeConfig to use parameters set through
 */
public class ApplicationContextJeeConfig implements JeeConfig {

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * The initialization parameters for VelocityView.
     */
    private Map<String, String> params;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param params Configuration parameters.
     */
    public ApplicationContextJeeConfig(ApplicationContext applicationContext, Map<String, String> params) {
        this.applicationContext = applicationContext;
        this.params = new HashMap<String, String>(params);
    }

    public String getInitParameter(String name) {
        return params.get(name);
    }

    public String findInitParameter(String key) {
        return params.get(key);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    public String getName() {
        return "Application Context JEE Config";
    }

    public ServletContext getServletContext() {
        return ServletUtil.getServletContext(applicationContext);
    }
}
