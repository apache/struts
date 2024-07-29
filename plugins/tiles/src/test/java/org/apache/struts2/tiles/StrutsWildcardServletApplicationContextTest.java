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
package org.apache.struts2.tiles;

import org.apache.tiles.request.ApplicationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class StrutsWildcardServletApplicationContextTest {

    private ServletContext context;

    @Before
    public void setUp() throws Exception {
        URL resource = getClass().getResource("/");
        context = new MockServletContext(resource.getPath(), new ResourceLoader() {
            @Override
            public Resource getResource(String location) {
                try {
                    String finalLocation = location.replaceAll("//", "/");
                    if (finalLocation.endsWith("/")) {
                        return new FileSystemResource(finalLocation);
                    }
                    return new FileUrlResource(finalLocation);
                } catch (MalformedURLException e) {
                    return null;
                }
            }

            @Override
            public ClassLoader getClassLoader() {
                return StrutsWildcardServletApplicationContextTest.class.getClassLoader();
            }
        });
    }

    @Test
    public void wildcardSupport() {
        StrutsWildcardServletApplicationContext applicationContext = new StrutsWildcardServletApplicationContext(context);

        Collection<ApplicationResource> resources = applicationContext.getResources("*tiles*.xml");

        assertThat(resources)
                .hasSize(1)
                .extracting(ApplicationResource::getLocalePath)
                .first().asString()
                .endsWith("/WEB-INF/tiles.xml");
    }

}