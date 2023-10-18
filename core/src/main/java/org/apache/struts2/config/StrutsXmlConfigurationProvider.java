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
package org.apache.struts2.config;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Override Xwork class so we can use an arbitrary config file
 */
public class StrutsXmlConfigurationProvider extends XmlConfigurationProvider {

    private static final Logger LOG = LogManager.getLogger(StrutsXmlConfigurationProvider.class);
    private static final Map<String, String> STRUTS_DTD_MAPPINGS = unmodifiableMap(new HashMap<String, String>() {{
        put("-//Apache Software Foundation//DTD Struts Configuration 2.0//EN", "struts-2.0.dtd");
        put("-//Apache Software Foundation//DTD Struts Configuration 2.1//EN", "struts-2.1.dtd");
        put("-//Apache Software Foundation//DTD Struts Configuration 2.1.7//EN", "struts-2.1.7.dtd");
        put("-//Apache Software Foundation//DTD Struts Configuration 2.3//EN", "struts-2.3.dtd");
        put("-//Apache Software Foundation//DTD Struts Configuration 2.5//EN", "struts-2.5.dtd");
        put("-//Apache Software Foundation//DTD Struts Configuration 6.0//EN", "struts-6.0.dtd");
    }});
    private File baseDir = null;
    private final String filename;
    private final String reloadKey;
    private final ServletContext servletContext;

    /**
     * Constructs the Struts configuration provider using the default struts.xml and no ServletContext
     */
    public StrutsXmlConfigurationProvider() {
        this("struts.xml", null);
    }

    /**
     * Constructs the configuration provider
     *
     * @param errorIfMissing If we should throw an exception if the file can't be found
     */
    @Deprecated
    public StrutsXmlConfigurationProvider(boolean errorIfMissing) {
        this("struts.xml", null);
    }

    /**
     * Constructs the configuration provider based on the provided config file
     *
     * @param filename file with Struts configuration
     */
    public StrutsXmlConfigurationProvider(String filename) {
        this(filename, null);
    }

    /**
     * Constructs the Struts configuration provider
     *
     * @param filename The filename to look for
     * @param ctx Our ServletContext
     */
    public StrutsXmlConfigurationProvider(String filename, ServletContext ctx) {
        super(filename);
        this.servletContext = ctx;
        this.filename = filename;
        this.reloadKey = "configurationReload-" + filename;
        setDtdMappings(STRUTS_DTD_MAPPINGS);
        File file = new File(filename);
        if (file.getParent() != null) {
            this.baseDir = file.getParentFile();
        }
    }

    /**
     * @deprecated since 6.2.0, use {@link #StrutsXmlConfigurationProvider(String, ServletContext)}
     */
    @Deprecated
    public StrutsXmlConfigurationProvider(String filename, @Deprecated boolean errorIfMissing, ServletContext ctx) {
        this(filename, ctx);
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
                public Class<? extends ServletContext> type() {
                    return servletContext.getClass();
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
            List<URL> list = new ArrayList<>();
            list.add(url);
            return list.iterator();
        } else {
            return super.getConfigurationUrls(fileName);
        }
    }

    protected URL findInFileSystem(String fileName) throws IOException {
        URL url = null;
        File file = new File(fileName);
        LOG.debug("Trying to load file: {}", file);

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
