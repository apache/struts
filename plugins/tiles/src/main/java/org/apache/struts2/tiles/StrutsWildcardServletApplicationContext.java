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

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.WildcardUtil;
import com.opensymphony.xwork2.util.finder.ResourceFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.servlet.ServletApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class StrutsWildcardServletApplicationContext extends ServletApplicationContext {

    private static final Logger LOG = LogManager.getLogger(StrutsWildcardServletApplicationContext.class);

    private ResourceFinder finder;

    public StrutsWildcardServletApplicationContext(ServletContext context) {
        super(context);

        Set<URL> urls = new HashSet<>();

        for (Object path : context.getResourcePaths("/")) {
            try {
                String realPath = context.getRealPath(String.valueOf(path));

                if (realPath != null) {
                    URL url = new File(realPath).toURI().toURL();
                    urls.add(url);
                }
            } catch (MalformedURLException e) {
                throw new ConfigurationException(e);
            }
        }

        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("/");
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                urls.add(resource);
            }
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }

        finder = new ResourceFinder(urls.toArray(new URL[urls.size()]));
    }

    public Collection<ApplicationResource> getResources(String path) {
        Set<ApplicationResource> resources = new HashSet<>();

        if (path.startsWith("/")) {
            LOG.trace("Using ServletContext to load resource {}", path);
            ApplicationResource resource = getResource(path);
            if (resource != null) {
                resources.add(resource);
            }
        }

        try {
            resources.addAll(findResources(path));
        } catch (IOException e) {
            LOG.error("Cannot find resources for [{}]", path, e);
        }

        return resources;
    }

    public ApplicationResource getResource(ApplicationResource base, Locale locale) {
        String localePath = base.getLocalePath(locale);
        File localFile = new File(localePath);
        if (localFile.exists()) {
            try {
                return new StrutsApplicationResource(localFile.toURI().toURL());
            } catch (MalformedURLException e) {
                LOG.warn("Cannot access [{}]", localePath, e);
                return null;
            }
        }
        return null;
    }

    protected Set<ApplicationResource> findResources(String path) throws IOException {
        Set<ApplicationResource> resources = new HashSet<>();

        LOG.trace("Using ResourceFinder to find matches for {}", path);

        Pattern pattern = WildcardUtil.compileWildcardPattern(path);
        Map<String, URL> matches = finder.getResourcesMap("");

        for (Map.Entry<String, URL> entry : matches.entrySet()) {
            if (pattern.matcher(entry.getKey()).matches()) {
                resources.add(new StrutsApplicationResource(entry.getValue()));
            }
        }

        LOG.trace("Found resources {} for path {}", resources, path);
        return resources;
    }

}
