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
package org.apache.struts2.dispatcher.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.apache.struts2.dispatcher.HostConfig;
import org.apache.struts2.util.MakeIterator;

import java.util.Iterator;

/**
 * Host configuration that wraps a ServletConfig
 */
public class ServletHostConfig implements HostConfig {
    private final ServletConfig config;

    public ServletHostConfig(ServletConfig config) {
        this.config = config;
    }

    @Override
    public String getInitParameter(String key) {
        return config.getInitParameter(key);
    }

    @Override
    public Iterator<String> getInitParameterNames() {
        return MakeIterator.convert(config.getInitParameterNames());
    }

    @Override
    public ServletContext getServletContext() {
        return config.getServletContext();
    }
}
