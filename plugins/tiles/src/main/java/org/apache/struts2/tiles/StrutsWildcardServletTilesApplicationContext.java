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
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.tiles.servlet.context.ServletTilesApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class StrutsWildcardServletTilesApplicationContext extends ServletTilesApplicationContext {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsWildcardServletTilesApplicationContext.class);

    private ResourceFinder finder;

    public StrutsWildcardServletTilesApplicationContext(ServletContext context) {
        super(context);

        Set<URL> urls = new HashSet<URL>();

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

    public Set<URL> getResources(String path) throws IOException {
        Set<URL> resources = new HashSet<URL>();

        if (path.startsWith("/")) {
            LOG.trace("Using ServletContext to load resource #0", path);
            URL resource = getResource(path);
            if (resource != null) {
                resources.add(resource);
            }
        }
        resources.addAll(findResources(path));

        return resources;
    }

    protected Set<URL> findResources(String path) throws IOException {
        Set<URL> resources = new HashSet<URL>();

        LOG.trace("Using ResourceFinder to find matches for #0", path);

        Pattern pattern = WildcardUtil.compileWildcardPattern(path);
        Map<String, URL> matches = finder.getResourcesMap("");

        for (String resource : matches.keySet()) {
            if (pattern.matcher(resource).matches()) {
                resources.add(matches.get(resource));
            }
        }

        LOG.trace("Found resources #0 for path #1", resources, path);
        return resources;
    }

}
