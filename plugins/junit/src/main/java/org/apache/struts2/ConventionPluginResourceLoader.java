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
package org.apache.struts2;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.URL;

public class ConventionPluginResourceLoader extends DefaultResourceLoader {

    private static final Logger log = LogManager.getLogger(ConventionPluginResourceLoader.class);

    @Override
    public Resource getResource(String location) {
        if (StringUtils.startsWith(location, "/WEB-INF/")) {
            try {
                URL url = new URL("file:/" + System.getProperty("user.dir") + "/src/main/webapp" + location);
                return new UrlResource(url);
            } catch (Exception e) {
                log.error("Error occurred during get resource for location: {}", location, e);
            }
        }

        return super.getResource(location);
    }
}
