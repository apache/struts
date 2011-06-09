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

package org.apache.struts2.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Override Xwork class so we can use an arbitrary config file
 */
public class StrutsXmlConfigurationProvider extends XmlConfigurationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsXmlConfigurationProvider.class);
    private File baseDir = null;
    private String filename;
    private String reloadKey;
    private ServletContext servletContext;

    /**
     * Constructs the configuration provider
     *
     * @param errorIfMissing If we should throw an exception if the file can't be found
     */
    public StrutsXmlConfigurationProvider(boolean errorIfMissing) {
        this("struts.xml", errorIfMissing, null);
    }

    /**
     * Constructs the configuration provider
     *
     * @param filename The filename to look for
     * @param errorIfMissing If we should throw an exception if the file can't be found
     * @param ctx Our ServletContext
     */
    public StrutsXmlConfigurationProvider(String filename, boolean errorIfMissing, ServletContext ctx) {
        super(filename, errorIfMissing);
        this.servletContext = ctx;
        this.filename = filename;
        reloadKey = "configurationReload-"+filename;
        Map<String,String> dtdMappings = new HashMap<String,String>(getDtdMappings());
        dtdMappings.put("-//Apache Software Foundation//DTD Struts Configuration 2.0//EN", "struts-2.0.dtd");
        dtdMappings.put("-//Apache Software Foundation//DTD Struts Configuration 2.1//EN", "struts-2.1.dtd");
        dtdMappings.put("-//Apache Software Foundation//DTD Struts Configuration 2.1.7//EN", "struts-2.1.7.dtd");
        dtdMappings.put("-//Apache Software Foundation//DTD Struts Configuration 2.3//EN", "struts-2.3.dtd");
        setDtdMappings(dtdMappings);
        File file = new File(filename);
        if (file.getParent() != null) {
            this.baseDir = file.getParentFile();
        }
    }
    
    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.config.providers.XmlConfigurationProvider#register(com.opensymphony.xwork2.inject.ContainerBuilder, java.util.Properties)
     */
    @Override
    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        if (servletContext != null && !containerBuilder.contains(ServletContext.class)) {
            containerBuilder.factory(ServletContext.class, new Factory<ServletContext>() {
                public ServletContext create(Context context) throws Exception {
                    return servletContext;
                }
            });
        }
        super.register(containerBuilder, props);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.config.providers.XmlConfigurationProvider#init(com.opensymphony.xwork2.config.Configuration)
     */
    @Override
    public void loadPackages() {
        ActionContext ctx = ActionContext.getContext();
        ctx.put(reloadKey, Boolean.TRUE);
        super.loadPackages();
    }

    /**
     * Look for the configuration file on the classpath and in the file system
     *
     * @param fileName The file name to retrieve
     * @see com.opensymphony.xwork2.config.providers.XmlConfigurationProvider#getConfigurationUrls
     */
    @Override
    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
        URL url = null;
        if (baseDir != null) {
            url = findInFileSystem(fileName);
            if (url == null) {
                return super.getConfigurationUrls(fileName);
            }
        }
        if (url != null) {
            List<URL> list = new ArrayList<URL>();
            list.add(url);
            return list.iterator();
        } else {
            return super.getConfigurationUrls(fileName);
        }
    }

    protected URL findInFileSystem(String fileName) throws IOException {
        URL url = null;
        File file = new File(fileName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Trying to load file " + file);
        }

        // Trying relative path to original file
        if (!file.exists()) {
            file = new File(baseDir, fileName);
        }
        if (file.exists()) {
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new IOException("Unable to convert "+file+" to a URL");
            }
        }
        return url;
    }

    /**
     * Overrides needs reload to ensure it is only checked once per request
     */
    @Override
    public boolean needsReload() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.get(reloadKey) == null && super.needsReload();
        } else {
            return super.needsReload();
        }

    }
    
    public String toString() {
        return ("Struts XML configuration provider ("+filename+")");
    }
}
