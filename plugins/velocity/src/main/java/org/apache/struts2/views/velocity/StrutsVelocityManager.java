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
package org.apache.struts2.views.velocity;

import org.apache.struts2.ObjectFactory;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ValueStack;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.views.TagLibraryDirectiveProvider;
import org.apache.struts2.views.util.ContextUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNullElse;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.struts2.views.util.ContextUtil.STRUTS;
import static org.apache.velocity.runtime.DeprecatedRuntimeConstants.OLD_CUSTOM_DIRECTIVES;

/**
 * Manages the environment for Velocity result types
 *
 * @since 7.0
 */
public class StrutsVelocityManager implements VelocityManager {

    private static final Logger LOG = LogManager.getLogger(StrutsVelocityManager.class);

    private ObjectFactory objectFactory;

    public static final String DEFAULT_CONFIG_FILE = "velocity.properties";
    public static final String KEY_VELOCITY_STRUTS_CONTEXT = ".KEY_velocity.struts2.context";

    private VelocityEngine velocityEngine;

    private VelocityTools velocityTools;

    /**
     * Names of contexts that will be chained on every request
     */
    private List<String> chainedContextNames = emptyList();

    private Properties velocityProperties;

    private String customConfigFile;

    private List<TagLibraryDirectiveProvider> tagLibraries;

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setContainer(Container container) {
        this.tagLibraries = container.getInstanceNames(TagLibraryDirectiveProvider.class).stream()
                .map(prefix -> container.getInstance(TagLibraryDirectiveProvider.class, prefix)).toList();
    }

    /**
     * @return a reference to the VelocityEngine used by <strong>all</strong> Struts Velocity results except directly
     * accessed *.vm pages (unless otherwise configured)
     */
    @Override
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * This method is responsible for creating the standard VelocityContext used by all Struts Velocity views.
     *
     * @param stack the current {@link ValueStack}
     * @param req   the current HttpServletRequest
     * @param res   the current HttpServletResponse
     * @return a new StrutsVelocityContext
     */
    @Override
    public Context createContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        Context context = null;
        if (velocityTools != null) {
            context = velocityTools.createContext();
        }
        if (context == null) {
            context = buildContext(stack, req, res);
        }
        req.setAttribute(KEY_VELOCITY_STRUTS_CONTEXT, context);
        return context;
    }

    protected Context buildContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        List<VelocityContext> chainedContexts = prepareChainedContexts(req, res, stack.getContext());
        Context context = new StrutsVelocityContext(chainedContexts, stack);
        ContextUtil.getStandardContext(stack, req, res).forEach(context::put);
        VelocityStrutsUtil util = new VelocityStrutsUtil(velocityEngine, context, stack, req, res);
        context.put(STRUTS, util);
        return context;
    }

    /**
     * Constructs contexts for chaining on this request. This method does not perform any initialization of the
     * contexts. All that must be done in the context itself.
     *
     * @param servletRequest  the servlet request object
     * @param servletResponse the servlet response object
     * @param extraContext    map with extra context
     * @return a List of contexts to chain or an empty list
     */
    protected List<VelocityContext> prepareChainedContexts(HttpServletRequest servletRequest,
                                                           HttpServletResponse servletResponse,
                                                           Map<String, Object> extraContext) {
        List<VelocityContext> contextList = new ArrayList<>();
        for (String className : chainedContextNames) {
            try {
                VelocityContext velocityContext = (VelocityContext) objectFactory.buildBean(className, extraContext);
                contextList.add(velocityContext);
            } catch (Exception e) {
                LOG.warn("Unable to instantiate chained VelocityContext {}, skipping", className, e);
            }
        }
        return contextList;
    }

    /**
     * initializes the StrutsVelocityManager.  this should be called during the initialization process, say by
     * ServletDispatcher.  this may be called multiple times safely although calls beyond the first won't do anything
     *
     * @param context the current servlet context
     */
    @Override
    public synchronized void init(ServletContext context) {
        if (velocityEngine != null) {
            return;
        }
        velocityEngine = newVelocityEngine(context);
        if (velocityTools != null) {
            velocityTools.init(context, velocityEngine);
        }
    }

    protected Properties loadConfiguration(ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Error attempting to create a loadConfiguration from a null ServletContext!");
        }
        Properties properties = new Properties();
        applyDefaultConfiguration(context, properties); // Apply defaults before loading user overrides
        String defaultUserDirective = properties.getProperty("userdirective");

        applyUserConfiguration(context, properties);

        if (velocityProperties != null) { // Apply additional overriding properties if any
            velocityProperties.stringPropertyNames().forEach(k -> properties.setProperty(k, velocityProperties.getProperty(k)));
        }

        String userDirective = properties.getProperty(OLD_CUSTOM_DIRECTIVES);
        String newDirective = isBlank(userDirective) ? defaultUserDirective : userDirective.strip() + "," + defaultUserDirective;
        properties.setProperty(OLD_CUSTOM_DIRECTIVES, newDirective);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing Velocity with the following properties ...");
            properties.stringPropertyNames().forEach(k -> LOG.debug("    '{}' = '{}'", k, properties.getProperty(k)));
        }
        return properties;
    }

    /**
     * Load optional velocity properties using the following loading strategy
     * <ul>
     *     <li>relative to the servlet context path</li>
     *     <li>relative to the WEB-INF directory</li>
     *     <li>on the classpath</li>
     * </ul>
     */
    private void applyUserConfiguration(ServletContext context, Properties properties) {
        String configFile = requireNonNullElse(customConfigFile, DEFAULT_CONFIG_FILE).trim();
        try {
            if (loadFile(properties, context.getRealPath(configFile))) {
                return;
            }
        } catch (IOException e) {
            LOG.warn("Unable to load Velocity configuration from servlet context path", e);
        }
        try {
            if (loadFile(properties, context.getRealPath("/WEB-INF/" + configFile))) {
                return;
            }
        } catch (IOException e) {
            LOG.warn("Unable to load Velocity configuration from WEB-INF path", e);
        }
        try {
            loadClassPathFile(properties, configFile);
        } catch (IOException e) {
            LOG.warn("Unable to load Velocity configuration from classpath", e);
        }
    }

    private boolean loadClassPathFile(Properties properties, String configFile) throws IOException {
        try (InputStream is = StrutsVelocityManager.class.getClassLoader().getResourceAsStream(configFile)) {
            if (is == null) {
                return false;
            }
            properties.load(is);
            LOG.info("Initializing Velocity using {} from classpath", configFile);
            return true;
        }
    }

    private boolean loadFile(Properties properties, String fileName) throws IOException {
        if (fileName == null) {
            return false;
        }
        File file = new File(fileName);
        if (!file.isFile()) {
            return false;
        }
        try (InputStream is = new FileInputStream(file)) {
            properties.load(is);
            LOG.info("Initializing Velocity using {}", file.getCanonicalPath() + " from file system");
            return true;
        }
    }

    @Inject(StrutsConstants.STRUTS_VELOCITY_CONFIGFILE)
    public void setCustomConfigFile(String customConfigFile) {
        this.customConfigFile = customConfigFile;
    }

    @Inject(StrutsConstants.STRUTS_VELOCITY_TOOLBOXLOCATION)
    public void setToolBoxLocation(String toolboxLocation) {
        if (!isBlank(toolboxLocation)) {
            this.velocityTools = new VelocityTools(toolboxLocation);
        }
    }

    public VelocityTools getVelocityTools() {
        return velocityTools;
    }

    /**
     * Allow users to specify via the struts.properties file a set of additional VelocityContexts to chain to the
     * StrutsVelocityContext. The intent is to allow these contexts to store helper objects that the ui developer may
     * want access to. Examples of reasonable VelocityContexts would be an IoCVelocityContext, a
     * SpringReferenceVelocityContext, and a ToolboxVelocityContext
     *
     * @param contexts comma separated velocity context's
     */
    @Inject(StrutsConstants.STRUTS_VELOCITY_CONTEXTS)
    public void setChainedContexts(String contexts) {
        this.chainedContextNames = Arrays.stream(contexts.split(",")).filter(StringUtils::isNotBlank).collect(toList());
    }

    /**
     * Instantiates a new VelocityEngine.
     * <p>
     * The following is the default Velocity configuration
     * <pre>
     *  resource.loader = file, class
     *  file.resource.loader.path = real path of webapp
     *  class.resource.loader.description = Velocity Classpath Resource Loader
     *  class.resource.loader.class = org.apache.struts2.views.velocity.StrutsResourceLoader
     * </pre>
     * This default configuration can be overridden by specifying a struts.velocity.configfile property in the
     * struts.properties file.  the specified config file will be searched for in the following order:
     * <ul>
     * <li>relative to the servlet context path</li>
     * <li>relative to the WEB-INF directory</li>
     * <li>on the classpath</li>
     * </ul>
     *
     * @param context the current ServletContext. may <b>not</b> be null
     * @return the new velocity engine
     */
    protected VelocityEngine newVelocityEngine(ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Error attempting to create a new VelocityEngine from a null ServletContext!");
        }
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setApplicationAttribute(ServletContext.class.getName(), context); // Required for webapp loader
        try {
            velocityEngine.init(loadConfiguration(context));
        } catch (Exception e) {
            throw new StrutsException("Unable to instantiate VelocityEngine!", e);
        }
        return velocityEngine;
    }

    /**
     * Once we've loaded up the user defined configurations, we will want to apply Struts specification configurations.
     * <ul>
     * <li>if Velocity.RESOURCE_LOADER has not been defined, then we will use the defaults which is a joined file,
     * class loader for unpackaed wars and a straight class loader otherwise</li>
     * <li>we need to define the various Struts custom user directives such as #param, #tag, and #bodytag</li>
     * </ul>
     *
     * @param context    the servlet context
     * @param properties velocity properties
     */
    private void applyDefaultConfiguration(ServletContext context, Properties properties) {
        LOG.debug("Load a default resource loader definition if there isn't one present.");
        if (properties.getProperty(Velocity.RESOURCE_LOADER) == null) {
            properties.setProperty(Velocity.RESOURCE_LOADER, "strutsfile, strutsclass");
        }

        // If there's a "real" path add it for the strutsfile resource loader. If there's no real path, and they haven't
        // configured a loader then we change resource loader property to just use the strutsclass loader
        String realPath = context.getRealPath("");
        if (realPath != null) {
            setStrutsFileResourceLoader(properties, realPath);
        } else {
            clearStrutsFileResourceLoader(properties);
        }

        setStrutsClasspathResourceLoader(properties);

        String directives = tagLibraries.stream().map(TagLibraryDirectiveProvider::getDirectiveClasses)
                .flatMap(Collection::stream).map(directive -> directive.getName() + ",").collect(joining());

        String userDirective = properties.getProperty(OLD_CUSTOM_DIRECTIVES);
        String newDirective = isBlank(userDirective) ? directives : userDirective.strip() + "," + directives;
        properties.setProperty(OLD_CUSTOM_DIRECTIVES, newDirective);
    }

    private void setStrutsFileResourceLoader(Properties properties, String realPath) {
        properties.setProperty("strutsfile.resource.loader.description", "Velocity File Resource Loader");
        properties.setProperty("strutsfile.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        properties.setProperty("strutsfile.resource.loader.path", realPath);
        properties.setProperty("strutsfile.resource.loader.modificationCheckInterval", "2");
        properties.setProperty("strutsfile.resource.loader.cache", "true");
    }

    private void clearStrutsFileResourceLoader(Properties properties) {
        String prop = properties.getProperty(Velocity.RESOURCE_LOADER)
                .replace("strutsfile,", "")
                .replace(", strutsfile", "")
                .replace("strutsfile", "");
        properties.setProperty(Velocity.RESOURCE_LOADER, prop);
    }

    /**
     * Refactored the Velocity templates for the Struts taglib into the classpath from the web path.  This will
     * enable Struts projects to have access to the templates by simply including the Struts jar file.
     * Unfortunately, there does not appear to be a macro for the class loader keywords
     */
    private void setStrutsClasspathResourceLoader(Properties properties) {
        properties.setProperty("strutsclass.resource.loader.description", "Velocity Classpath Resource Loader");
        properties.setProperty("strutsclass.resource.loader.class", "org.apache.struts2.views.velocity.StrutsResourceLoader");
        properties.setProperty("strutsclass.resource.loader.modificationCheckInterval", "2");
        properties.setProperty("strutsclass.resource.loader.cache", "true");
    }

    /**
     * @return the velocityProperties
     */
    public Properties getVelocityProperties() {
        return velocityProperties;
    }

    /**
     * @param velocityProperties the velocityProperties to set
     */
    public void setVelocityProperties(Properties velocityProperties) {
        this.velocityProperties = velocityProperties;
    }
}
