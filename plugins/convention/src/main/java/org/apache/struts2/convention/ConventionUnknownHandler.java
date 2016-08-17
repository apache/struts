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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.providers.InterceptorBuilder;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class is the default unknown handler for all of the Convention
 * plugin integration with XWork. This handles instances when a URL doesn't
 * have an action associated with it but does a viable result and also the
 * instance where an action returns a result code that isn't already
 * configured but there is a viable result for the code.
 * </p>
 *
 * <p>
 * This class also handles all of the index actions using redirects
 * and actions in nested packages. For example, if there is an action
 * <strong>/foo/index</strong> and the URL <strong>/foo</strong> is used,
 * this will render the index action in the /foo namespace.
 * </p>
 */
public class ConventionUnknownHandler implements UnknownHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ConventionUnknownHandler.class);

    protected Configuration configuration;
    protected ObjectFactory objectFactory;
    protected ServletContext servletContext;
    protected ResultMapBuilder resultMapBuilder;
    protected String defaultParentPackageName;
    protected PackageConfig parentPackage;

    private boolean redirectToSlash;
    private ConventionsService conventionsService;
    private String nameSeparator;

    /**
     * Constructs the unknown handler.
     *
     * @param configuration            The XWork configuration.
     * @param objectFactory            The XWork object factory used to create result instances.
     * @param servletContext           The servlet context used to help build the action configurations.
     * @param container                The Xwork container
     * @param defaultParentPackageName The default XWork package that the unknown handler will use as
     *                                 the parent package for new actions and results.
     * @param redirectToSlash          A boolean parameter that controls whether or not this will handle
     *                                 unknown actions in the same manner as Apache, Tomcat and other web servers. This
     *                                 handling will send back a redirect for URLs such as /foo to /foo/ if there doesn't
     *                                 exist an action that responds to /foo.
     * @param nameSeparator            The character used as word separator in the action names. "-" by default
     */
    @Inject
    public ConventionUnknownHandler(Configuration configuration, ObjectFactory objectFactory,
                                    ServletContext servletContext, Container container,
                                    @Inject("struts.convention.default.parent.package") String defaultParentPackageName,
                                    @Inject("struts.convention.redirect.to.slash") String redirectToSlash,
                                    @Inject("struts.convention.action.name.separator") String nameSeparator) {
        this.configuration = configuration;
        this.objectFactory = objectFactory;
        this.servletContext = servletContext;
        this.resultMapBuilder = container.getInstance(ResultMapBuilder.class, container.getInstance(String.class, ConventionConstants.CONVENTION_RESULT_MAP_BUILDER));
        this.conventionsService = container.getInstance(ConventionsService.class, container.getInstance(String.class, ConventionConstants.CONVENTION_CONVENTIONS_SERVICE));
        this.defaultParentPackageName = defaultParentPackageName;
        this.nameSeparator = nameSeparator;

        this.parentPackage = configuration.getPackageConfig(defaultParentPackageName);
        if (parentPackage == null) {
            throw new ConfigurationException("Unknown default parent package [" + defaultParentPackageName + "]");
        }

        this.redirectToSlash = Boolean.parseBoolean(redirectToSlash);
    }

    public ActionConfig handleUnknownAction(String namespace, String actionName)
            throws XWorkException {
        // Strip the namespace if it is just a slash
        if (namespace == null || "/".equals(namespace)) {
            namespace = "";
        }

        Map<String, ResultTypeConfig> resultsByExtension = conventionsService.getResultTypesByExtension(parentPackage);
        String pathPrefix = determinePath(null, namespace);
        ActionConfig actionConfig = null;

        // Try /idx/action.jsp if actionName is not empty, otherwise it will just be /.jsp
        if (!actionName.equals("")) {
            Resource resource = findResource(resultsByExtension, pathPrefix, actionName);
            if (resource != null) {
                actionConfig = buildActionConfig(resource.path, resultsByExtension.get(resource.ext));
            }
        }

        if (actionConfig == null) {
            Resource resource = findResource(resultsByExtension, pathPrefix, actionName, "/index");

            // If the URL is /foo and there is an action we can redirect to, send the redirect to /foo/.
            // However, if that action is not in the same namespace, it is the default, so I'm not going
            // to return that.
            if (!actionName.equals("") && redirectToSlash) {
                ResultTypeConfig redirectResultTypeConfig = parentPackage.getAllResultTypeConfigs().get("redirect");
                String redirectNamespace = namespace + "/" + actionName;
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Checking if there is an action named index in the namespace [#0]",
                            redirectNamespace);
                }

                actionConfig = configuration.getRuntimeConfiguration().getActionConfig(redirectNamespace, "index");
                if (actionConfig != null) {
                    if (LOG.isTraceEnabled())
                        LOG.trace("Found action config");

                    PackageConfig packageConfig = configuration.getPackageConfig(actionConfig.getPackageName());
                    if (redirectNamespace.equals(packageConfig.getNamespace())) {
                        if (LOG.isTraceEnabled())
                            LOG.trace("Action is not a default - redirecting");
                        return buildActionConfig(redirectNamespace + "/", redirectResultTypeConfig);
                    }

                    if (LOG.isTraceEnabled())
                        LOG.trace("Action was a default - NOT redirecting");
                }

                if (resource != null) {
                    return buildActionConfig(redirectNamespace + "/", redirectResultTypeConfig);
                }
            }

            if (resource != null) {
                // Otherwise, if the URL is /foo or /foo/ look for index pages in /foo/
                actionConfig = buildActionConfig(resource.path, resultsByExtension.get(resource.ext));
            }

            // try to find index action in current namespace or in default one
            if (actionConfig == null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Looking for action named [index] in namespace [#0] or in default namespace", namespace);
                }
                actionConfig = configuration.getRuntimeConfiguration().getActionConfig(namespace, "index");
            }
        }

        return actionConfig;
    }

    /**
     * Finds a resource using the given path parts and all of the extensions in the map.
     *
     * @param resultsByExtension Map of extension to result type config objects.
     * @param parts              The parts of the resource.
     * @return The resource path or null.
     */
    protected Resource findResource(Map<String, ResultTypeConfig> resultsByExtension, String... parts) {
        for (String ext : resultsByExtension.keySet()) {
            String canonicalPath = canonicalize(string(parts) + "." + ext);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Checking for [#0]", canonicalPath);
            }

            try {
                if (servletContext.getResource(canonicalPath) != null) {
                    return new Resource(canonicalPath, ext);
                }
            } catch (MalformedURLException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to parse path to the web application resource [#0] skipping...", canonicalPath);
                }
            }
        }

        return null;
    }

    protected String canonicalize(final String path) {
        if (path == null) {
            return null;
        }

        return path.replaceAll("/+", "/");
    }

    protected ActionConfig buildActionConfig(String path, ResultTypeConfig resultTypeConfig) {
        Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();
        HashMap<String, String> params = new HashMap<String, String>();
        if (resultTypeConfig.getParams() != null) {
            params.putAll(resultTypeConfig.getParams());
        }
        params.put(resultTypeConfig.getDefaultResultParam(), path);

        PackageConfig pkg = configuration.getPackageConfig(defaultParentPackageName);
        List<InterceptorMapping> interceptors = InterceptorBuilder.constructInterceptorReference(pkg, pkg.getFullDefaultInterceptorRef(), Collections.<String, String>emptyMap(), null, objectFactory);
        ResultConfig config = new ResultConfig.Builder(Action.SUCCESS, resultTypeConfig.getClassName()).
                addParams(params).build();
        results.put(Action.SUCCESS, config);

        return new ActionConfig.Builder(defaultParentPackageName, "execute", ActionSupport.class.getName()).
                addInterceptors(interceptors).addResultConfigs(results).build();
    }

    private Result scanResultsByExtension(String ns, String actionName, String pathPrefix,
                                          String resultCode, ActionContext actionContext) {
        Map<String, ResultTypeConfig> resultsByExtension = conventionsService.getResultTypesByExtension(parentPackage);
        Result result = null;
        for (String ext : resultsByExtension.keySet()) {
            if (LOG.isTraceEnabled()) {
                String fqan = ns + "/" + actionName;
                LOG.trace("Trying to locate the correct result for the FQ action [#0]"
                        + " with an file extension of [#1] in the directory [#2] " + "and a result code of [#3]",
                        fqan, ext, pathPrefix, resultCode);
            }

            String path = string(pathPrefix, actionName, nameSeparator, resultCode, ".", ext);
            result = findResult(path, resultCode, ext, actionContext, resultsByExtension);
            if (result != null) {
                break;
            }

            path = string(pathPrefix, actionName, ".", ext);
            result = findResult(path, resultCode, ext, actionContext, resultsByExtension);
            if (result != null) {
                break;
            }

            // Issue #6 - Scan for result-code as page name
            path = string(pathPrefix, resultCode, ".", ext);
            result = findResult(path, resultCode, ext, actionContext, resultsByExtension);
            if (result != null) {
                break;
            }

        }
        return result;
    }

    public Result handleUnknownResult(ActionContext actionContext, String actionName,
                                      ActionConfig actionConfig, String resultCode) throws XWorkException {

        PackageConfig pkg = configuration.getPackageConfig(actionConfig.getPackageName());
        String ns = pkg.getNamespace();
        String pathPrefix = determinePath(actionConfig, ns);

        Result result = scanResultsByExtension(ns, actionName, pathPrefix, resultCode, actionContext);

        if (result == null) {
            // Try /idx/action/index.jsp
            Map<String, ResultTypeConfig> resultsByExtension = conventionsService.getResultTypesByExtension(pkg);
            for (String ext : resultsByExtension.keySet()) {
                if (LOG.isTraceEnabled()) {
                    String fqan = ns + "/" + actionName;
                    LOG.trace("Checking for [#0/index.#1]", fqan, ext);
                }

                String path = string(pathPrefix, actionName, "/index", nameSeparator, resultCode, ".", ext);
                result = findResult(path, resultCode, ext, actionContext, resultsByExtension);
                if (result != null) {
                    break;
                }

                path = string(pathPrefix, actionName, "/index.", ext);
                result = findResult(path, resultCode, ext, actionContext, resultsByExtension);
                if (result != null) {
                    break;
                }
            }
        }

        if (result == null && resultCode != null) {
            //try to find an action to chain to. If the source action is "foo" and
            //the result is "bar", we will try to find an action called "foo-bar"
            //in the same package
            String chainedTo = actionName + nameSeparator + resultCode;
            ActionConfig chainedToConfig = pkg.getActionConfigs().get(chainedTo);
            if (chainedToConfig != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Action [#0] used as chain result for [#1] and result [#2]", chainedTo, actionName, resultCode);
                }

                ResultTypeConfig chainResultType = pkg.getAllResultTypeConfigs().get("chain");
                result = buildResult(chainedTo, resultCode, chainResultType, actionContext);
            }
        }

        return result;
    }

    protected Result findResult(String path, String resultCode, String ext, ActionContext actionContext,
                                Map<String, ResultTypeConfig> resultsByExtension) {
        try {
            boolean traceEnabled = LOG.isTraceEnabled();
            if (traceEnabled) {
                LOG.trace("Checking ServletContext for [#0]", path);
            }

            URL resource = servletContext.getResource(path);
            if (resource != null && resource.getPath().endsWith(path)) {
                if (traceEnabled) {
                    LOG.trace("Found resource #0", resource);
                }
                return buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
            }

            if (traceEnabled) {
                LOG.trace("Checking ClassLoader for #0", path);
            }

            String classLoaderPath = path.startsWith("/") ? path.substring(1, path.length()) : path;
            resource = ClassLoaderUtil.getResource(classLoaderPath, getClass());
            if (resource != null && resource.getPath().endsWith(classLoaderPath)) {
                if (traceEnabled) {
                    LOG.trace("Found resource #0", resource);
                }
                return buildResult(path, resultCode, resultsByExtension.get(ext), actionContext);
            }
        } catch (MalformedURLException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Unable to parse template path: [#0] skipping...", path);
            }
        }

        return null;
    }

    protected Result buildResult(String path, String resultCode, ResultTypeConfig config, ActionContext invocationContext) {
        String resultClass = config.getClassName();

        Map<String, String> params = new LinkedHashMap<String, String>();
        if (config.getParams() != null) {
            params.putAll(config.getParams());
        }
        params.put(config.getDefaultResultParam(), path);

        ResultConfig resultConfig = new ResultConfig.Builder(resultCode, resultClass).addParams(params).build();
        try {
            return objectFactory.buildResult(resultConfig, invocationContext.getContextMap());
        } catch (Exception e) {
            throw new XWorkException("Unable to build convention result", e, resultConfig);
        }
    }

    protected String string(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
        }
        return sb.toString();
    }

    /**
     * Determines the result path prefix that the request URL is for, minus the action name. This includes
     * the base result location and the namespace, with all the slashes handled.
     *
     * @param actionConfig (Optional) The might be a ConventionActionConfig, from which we can get the
     *                     default base result location of that specific action.
     * @param namespace    The current URL namespace.
     * @return The path prefix and never null.
     */
    protected String determinePath(ActionConfig actionConfig, String namespace) {
        String finalPrefix = conventionsService.determineResultPath(actionConfig);

        if (!finalPrefix.endsWith("/")) {
            finalPrefix += "/";
        }

        if (namespace == null || "/".equals(namespace)) {
            namespace = "";
        }

        if (namespace.length() > 0) {
            if (namespace.startsWith("/")) {
                namespace = namespace.substring(1);
            }

            if (!namespace.endsWith("/")) {
                namespace += "/";
            }
        }

        return finalPrefix + namespace;
    }

    /**
     * Not used
     */
    public Object handleUnknownActionMethod(Object action, String methodName) throws NoSuchMethodException {
        return null;
    }

    public static class Resource {
        final String path;
        final String ext;

        public Resource(String path, String ext) {
            this.path = path;
            this.ext = ext;
        }
    }
}
