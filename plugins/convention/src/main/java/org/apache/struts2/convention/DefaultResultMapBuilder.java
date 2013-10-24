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
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;
import com.opensymphony.xwork2.util.finder.ResourceFinder;
import com.opensymphony.xwork2.util.finder.Test;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * This class implements the ResultMapBuilder and traverses the web
 * application content directory looking for reasonably named JSPs and
 * other result types as well as annotations. This naming is in this
 * form:
 * </p>
 *
 * <pre>
 * /resultPath/namespace/action-&lt;result>.jsp
 * </pre>
 *
 * <p>
 * If there are any files in these locations than a result is created
 * for each one and the result names is the last portion of the file
 * name up to the . (dot).
 * </p>
 *
 * <p>
 * When results are found, new ResultConfig instances are created. The
 * result config that is created has a number of thing to be aware of:
 * </p>
 *
 * <ul>
 * <li>The result config contains the location parameter, which is
 *  required by most result classes to figure out where to find the result.
 *  In addition, the config has all the parameters from the default result-type
 *  configuration.</li>
 * </ul>
 *
 * <p>
 * After loading the files in the web application, this class will then
 * use any annotations on the action class to override what was found in
 * the web application files. These annotations are the {@link Result}
 * and {@link Results} annotations. These two annotations allow an action
 * to supply different or non-forward based results for specific return
 * values of an action method.
 * </p>
 *
 * <p>
 * The result path used by this class for locating JSPs and other
 * such result files can be set using the Struts2 constant named
 * <strong>struts.convention.result.path</strong> or using the
 * {@link org.apache.struts2.convention.annotation.ResultPath}
 * annotation.
 * </p>
 *
 * <p>
 * This class will also locate and configure Results in the classpath,
 * including velocity and FreeMarker templates inside the classpath.
 * </p>
 *
 * <p>
 * All results that are configured from resources are given a type corresponding
 * to the resources extension. The extensions and types are given in the
 * table below:
 * </p>
 *
 * <table>
 * <tr><th>Extension</th><th>Type</th></tr>
 * <tr><td>.jsp</td><td>dispatcher</td</tr>
 * <tr><td>.jspx</td><td>dispatcher</td</tr>
 * <tr><td>.html</td><td>dispatcher</td</tr>
 * <tr><td>.htm</td><td>dispatcher</td</tr>
 * <tr><td>.vm</td><td>velocity</td</tr>
 * <tr><td>.ftl</td><td>freemarker</td</tr>
 * </table>
 */
public class DefaultResultMapBuilder implements ResultMapBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultResultMapBuilder.class);
    private final ServletContext servletContext;
    private Set<String> relativeResultTypes;
    private ConventionsService conventionsService;
    private boolean flatResultLayout = true;

    /**
     * Constructs the SimpleResultMapBuilder using the given result location.
     *
     * @param   servletContext The ServletContext for finding the resources of the web application.
     * @param   container The Xwork container
     * @param   relativeResultTypes The list of result types that can have locations that are relative
     *          and the result location (which is the resultPath plus the namespace) prepended to them.
     */
    @Inject
    public DefaultResultMapBuilder(ServletContext servletContext, Container container,
            @Inject("struts.convention.relative.result.types") String relativeResultTypes) {
        this.servletContext = servletContext;
        this.relativeResultTypes = new HashSet<String>(Arrays.asList(relativeResultTypes.split("\\s*[,]\\s*")));
        this.conventionsService = container.getInstance(ConventionsService.class, container.getInstance(String.class, ConventionConstants.CONVENTION_CONVENTIONS_SERVICE));
    }

    /**
     * @param flatResultLayout If 'true' result resources will be expected to be in the form
     *          ${namespace}/${actionName}-${result}.${extension}, otherwise in the form
     *          ${namespace}/${actionName}/${result}.${extension}
     */
    @Inject("struts.convention.result.flatLayout")
    public void setFlatResultLayout(String flatResultLayout) {
        this.flatResultLayout = "true".equals(flatResultLayout);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ResultConfig> build(Class<?> actionClass,
        org.apache.struts2.convention.annotation.Action annotation, String actionName,
            PackageConfig packageConfig) {

        // Get the default result location from the annotation or configuration
        String defaultResultPath = conventionsService.determineResultPath(actionClass);

        // Add a slash
        if (!defaultResultPath.endsWith("/")) {
            defaultResultPath = defaultResultPath + "/";
        }

        // Check for resources with the action name
        final String namespace = packageConfig.getNamespace();
        if (namespace != null && namespace.startsWith("/")) {
             defaultResultPath = defaultResultPath + namespace.substring(1);
        } else if (namespace != null) {
            defaultResultPath = defaultResultPath + namespace;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Using final calculated namespace [#0]", namespace);
        }

        // Add that ending slash for concatenation
        if (!defaultResultPath.endsWith("/")) {
            defaultResultPath += "/";
        }

        String resultPrefix = defaultResultPath + actionName;

        //results from files
        Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();
        Map<String, ResultTypeConfig> resultsByExtension = conventionsService.getResultTypesByExtension(packageConfig);
        createFromResources(actionClass, results, defaultResultPath, resultPrefix, actionName,
            packageConfig, resultsByExtension);

        //get inherited @Results and @Result (class level)
        for (Class<?> clazz : ReflectionTools.getClassHierarchy(actionClass)) {
            createResultsFromAnnotations(clazz, packageConfig, defaultResultPath, results, resultsByExtension);
        }


        //method level
        if (annotation != null && annotation.results() != null && annotation.results().length > 0) {
            createFromAnnotations(results, defaultResultPath, packageConfig, annotation.results(),
                    actionClass, resultsByExtension);
        }
        return results;
    }

    /**
     * Creates results from @Results and @Result annotations
     * @param actionClass  class to check for annotations
     * @param packageConfig packageConfig where the action will be located
     * @param defaultResultPath default result path
     * @param results map of results
     * @param resultsByExtension  map of result types keyed by extension
     */
    protected void createResultsFromAnnotations(Class<?> actionClass, PackageConfig packageConfig, String defaultResultPath,
                                                Map<String, ResultConfig> results, Map<String, ResultTypeConfig> resultsByExtension) {
        Results resultsAnn = actionClass.getAnnotation(Results.class);
        if (resultsAnn != null) {
            createFromAnnotations(results, defaultResultPath, packageConfig, resultsAnn.value(),
                    actionClass, resultsByExtension);
        }

        Result resultAnn = actionClass.getAnnotation(Result.class);
        if (resultAnn != null) {
            createFromAnnotations(results, defaultResultPath, packageConfig, new Result[]{resultAnn},
                    actionClass, resultsByExtension);
        }
    }


    /**
     * Creates any result types from the resources available in the web application. This scans the
     * web application resources using the servlet context.
     *
     * @param   actionClass The action class the results are being built for.
     * @param   results The results map to put the result configs created into.
     * @param   resultPath The calculated path to the resources.
     * @param   resultPrefix The prefix for the result. This is usually <code>/resultPath/actionName</code>.
     * @param   actionName The action name which is used only for logging in this implementation.
     * @param   packageConfig The package configuration which is passed along in order to determine
     * @param   resultsByExtension The map of extensions to result type configuration instances.
     */
    protected void createFromResources(Class<?> actionClass, Map<String, ResultConfig> results,
            final String resultPath, final String resultPrefix, final String actionName,
            PackageConfig packageConfig, Map<String, ResultTypeConfig> resultsByExtension) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Searching for results in the Servlet container at [#0]" +
            		" with result prefix of [#1]", resultPath, resultPrefix);
        }

        // Build from web application using the ServletContext
        @SuppressWarnings("unchecked")
        Set<String> paths = servletContext.getResourcePaths(flatResultLayout ? resultPath : resultPrefix);
        if (paths != null) {
            for (String path : paths) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing resource path [#0]", path);
                }

                String fileName = StringUtils.substringAfterLast(path, "/");
                if (StringUtils.isBlank(fileName) || StringUtils.startsWith(fileName, ".")) {
                    if (LOG.isTraceEnabled())
                        LOG.trace("Ignoring file without name [#0]", path);
                    continue;
                }
                else if(fileName.lastIndexOf(".") > 0){
                    String suffix = fileName.substring(fileName.lastIndexOf(".")+1);

                    if(conventionsService.getResultTypesByExtension(packageConfig).get(suffix) == null) {
                        if (LOG.isDebugEnabled())
                            LOG.debug("No result type defined for file suffix : [#0]. Ignoring file #1", suffix, fileName);
                	continue;
                    }
                }

                makeResults(actionClass, path, resultPrefix, results, packageConfig, resultsByExtension);
            }
        }

        // Building from the classpath
        String classPathLocation = resultPath.startsWith("/") ?
            resultPath.substring(1, resultPath.length()) : resultPath;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Searching for results in the class path at [#0]"
                    + " with a result prefix of [#1] and action name [#2]", classPathLocation, resultPrefix,
                    actionName);
        }

        ResourceFinder finder = new ResourceFinder(classPathLocation, getClassLoaderInterface());
        try {
            Map<String, URL> matches = finder.getResourcesMap("");
            if (matches != null) {
                Test<URL> resourceTest = getResourceTest(resultPath, actionName);
                for (Map.Entry<String, URL> entry : matches.entrySet()) {
                    if (resourceTest.test(entry.getValue())) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Processing URL [#0]", entry.getKey());
                        }

                        String urlStr = entry.getValue().toString();
                        int index = urlStr.lastIndexOf(resultPrefix);
                        String path = urlStr.substring(index);
                        makeResults(actionClass, path, resultPrefix, results, packageConfig, resultsByExtension);
                    }

                }
            }
        } catch (IOException ex) {
           if (LOG.isErrorEnabled())
               LOG.error("Unable to scan directory [#0] for results", ex, classPathLocation);
        }
    }

    protected ClassLoaderInterface getClassLoaderInterface() {

        /*
        if there is a ClassLoaderInterface in the context, use it, otherwise
        default to the default ClassLoaderInterface (a wrapper around the current
        thread classloader)
        using this, other plugins (like OSGi) can plugin their own classloader for a while
        and it will be used by Convention (it cannot be a bean, as Convention is likely to be
        called multiple times, and it need to use the default ClassLoaderInterface during normal startup)
        */
        ClassLoaderInterface classLoaderInterface = null;
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null)
            classLoaderInterface = (ClassLoaderInterface) ctx.get(ClassLoaderInterface.CLASS_LOADER_INTERFACE);

        return (ClassLoaderInterface) ObjectUtils.defaultIfNull(classLoaderInterface, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()));

    }

    private Test<URL> getResourceTest(final String resultPath, final String actionName) {
        return new Test<URL>() {
            public boolean test(URL url) {
                String urlStr = url.toString();
                int index = urlStr.lastIndexOf(resultPath);
                String path = urlStr.substring(index + resultPath.length());
                return path.startsWith(actionName);
            }
        };
    }

    /**
     * Makes all the results for the given path.
     *
     * @param   actionClass The action class the results are being built for.
     * @param   path The path to build the result for.
     * @param   resultPrefix The is the result prefix which is the result location plus the action name.
     *          This is used to determine if the path contains a result code or not.
     * @param   results The Map to place the result(s)
     * @param   packageConfig The package config the results belong to.
     * @param   resultsByExtension The map of extensions to result type configuration instances.
     */
    protected void makeResults(Class<?> actionClass, String path, String resultPrefix,
            Map<String, ResultConfig> results, PackageConfig packageConfig,
            Map<String, ResultTypeConfig> resultsByExtension) {

        if (path.startsWith(resultPrefix)) {
            int indexOfDot = path.indexOf('.', resultPrefix.length());

            // This case is when the path doesn't contain a result code
            if (indexOfDot == resultPrefix.length()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("The result file [#0] has no result code and therefore" +
                        " will be associated with success, input and error by default. This might" +
                        " be overridden by another result file or an annotation.", path);
                }

                addResult(actionClass, path, results, packageConfig, resultsByExtension, Action.SUCCESS);
                addResult(actionClass, path, results, packageConfig, resultsByExtension, Action.INPUT);
                addResult(actionClass, path, results, packageConfig, resultsByExtension, Action.ERROR);

            // This case is when the path contains a result code
            } else if (indexOfDot > resultPrefix.length()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("The result file [#0] has a result code and therefore" +
                        " will be associated with only that result code.", path);
                }

                String resultCode = path.substring(resultPrefix.length() + 1, indexOfDot);
                ResultConfig result = createResultConfig(actionClass,
                    new ResultInfo(resultCode, path, packageConfig, resultsByExtension),
                    packageConfig, null);
                results.put(resultCode, result);
            }
        }
    }

    /**
     * Checks if result was already assigned, if not checks global results first and if exists, adds reference to it.
     * If not, creates package specific result.
     *
     * @param   actionClass The action class the results are being built for.
     * @param   path The path to build the result for.
     * @param   results The Map to place the result(s)
     * @param   packageConfig The package config the results belong to.
     * @param   resultsByExtension The map of extensions to result type configuration instances.
     * @param   resultKey The result name to use
     */
    protected void addResult(Class<?> actionClass, String path, Map<String, ResultConfig> results,
                           PackageConfig packageConfig, Map<String, ResultTypeConfig> resultsByExtension,
                           String resultKey) {

        if (!results.containsKey(resultKey)) {
            Map<String, ResultConfig> globalResults = packageConfig.getAllGlobalResults();
            if (globalResults.containsKey(resultKey)) {
                results.put(resultKey, globalResults.get(resultKey));
            } else {
                ResultConfig resultConfig = createResultConfig(actionClass,
                        new ResultInfo(resultKey, path, packageConfig, resultsByExtension),
                        packageConfig, null);
                results.put(resultKey, resultConfig);
            }
        }
    }

    protected void createFromAnnotations(Map<String, ResultConfig> resultConfigs,
            String resultPath, PackageConfig packageConfig, Result[] results,
            Class<?> actionClass, Map<String, ResultTypeConfig> resultsByExtension) {
        // Check for multiple results on the class
        for (Result result : results) {
            ResultConfig config = createResultConfig(actionClass,
                new ResultInfo(result, packageConfig, resultPath, actionClass, resultsByExtension),
                packageConfig, result);
            if (config != null) {
                resultConfigs.put(config.getName(), config);
            }
        }
    }

    /**
     * Creates the result configuration for the single result annotation. This will use all the
     * information from the annotation and anything that isn't specified will be fetched from the
     * PackageConfig defaults (if they exist).
     *
     * @param   actionClass The action class the results are being built for.
     * @param   info The result info that is used to create the ResultConfig instance.
     * @param   packageConfig The PackageConfig to use to fetch defaults for result and parameters.
     * @param   result (Optional) The result annotation to pull additional information from.
     * @return  The ResultConfig or null if the Result annotation is given and the annotation is
     *          targeted to some other action than this one.
     */
    @SuppressWarnings(value = {"unchecked"})
    protected ResultConfig createResultConfig(Class<?> actionClass, ResultInfo info,
            PackageConfig packageConfig, Result result) {
        // Look up by the type that was determined from the annotation or by the extension in the
        // ResultInfo class
        ResultTypeConfig resultTypeConfig = packageConfig.getAllResultTypeConfigs().get(info.type);
        if (resultTypeConfig == null) {
            throw new ConfigurationException("The Result type [" + info.type + "] which is" +
                " defined in the Result annotation on the class [" + actionClass + "] or determined" +
                " by the file extension or is the default result type for the PackageConfig of the" +
                " action, could not be found as a result-type defined for the Struts/XWork package [" +
                packageConfig.getName() + "]");
        }

        // Add the default parameters for the result type config (if any)
        HashMap<String, String> params = new HashMap<String, String>();
        if (resultTypeConfig.getParams() != null) {
            params.putAll(resultTypeConfig.getParams());
        }

        // Handle the annotation
        if (result != null) {
            params.putAll(StringTools.createParameterMap(result.params()));
        }

        // Map the location to the default param for the result or a param named location
        if (info.location != null) {
            String defaultParamName = resultTypeConfig.getDefaultResultParam();
            if (!params.containsKey(defaultParamName)) {
                params.put(defaultParamName, info.location);
            }
        }

        return new ResultConfig.Builder(info.name, resultTypeConfig.getClassName()).addParams(params).build();
    }

    protected class ResultInfo {
        public final String name;
        public final String location;
        public final String type;

        public ResultInfo(String name, String location, PackageConfig packageConfig,
                Map<String, ResultTypeConfig> resultsByExtension) {
            this.name = name;
            this.location = location;
            this.type = determineType(location, packageConfig, resultsByExtension);
        }

        public ResultInfo(Result result, PackageConfig packageConfig, String resultPath,
                Class<?> actionClass, Map<String, ResultTypeConfig> resultsByExtension) {
            this.name = result.name();
            if (StringUtils.isNotBlank(result.type())) {
                this.type = result.type();
            } else if (StringUtils.isNotBlank(result.location())) {
                this.type = determineType(result.location(), packageConfig, resultsByExtension);
            } else {
                throw new ConfigurationException("The action class [" + actionClass + "] contains a " +
                    "result annotation that has no type parameter and no location parameter. One of " +
                    "these must be defined.");
            }

            // See if we can handle relative locations or not
            if (StringUtils.isNotBlank(result.location())) {
                if (relativeResultTypes.contains(this.type) && !result.location().startsWith("/")) {
                    location = resultPath + result.location();
                } else {
                    location = result.location();
                }
            } else {
                this.location = null;
            }
        }

        String determineType(String location, PackageConfig packageConfig,
                Map<String, ResultTypeConfig> resultsByExtension) {
            int indexOfDot = location.lastIndexOf(".");
            if (indexOfDot > 0) {
                String extension = location.substring(indexOfDot + 1);
                ResultTypeConfig resultTypeConfig = resultsByExtension.get(extension);
                if (resultTypeConfig != null) {
                    return resultTypeConfig.getName();
                } else
                    throw new ConfigurationException("Unable to find a result type for extension [" + extension + "] " +
                    		"in location attribute [" + location + "].");
            } else {
                return packageConfig.getFullDefaultResultType();
            }
        }
    }
}
