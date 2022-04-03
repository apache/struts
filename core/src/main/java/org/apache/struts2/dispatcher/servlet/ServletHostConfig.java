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

import org.apache.struts2.util.MakeIterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Iterator;

import org.apache.struts2.dispatcher.HostConfig;

/**
 * Host configuration that wraps a ServletConfig
 */
public class ServletHostConfig implements HostConfig {
    private ServletConfig config;

    public ServletHostConfig(ServletConfig config) {
        this.config = config;
    }
    public String getInitParameter(String key) {
        return config.getInitParameter(key);
    }

    public Iterator<String> getInitParameterNames() {
        return MakeIterator.convert(config.getInitParameterNames());
    }

    public ServletContext getServletContext() {
        return config.getServletContext();
    }
}
