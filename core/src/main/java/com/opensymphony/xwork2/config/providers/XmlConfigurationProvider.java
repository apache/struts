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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.ConfigurationUtil;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.impl.LocatableFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ClassPathFinder;
import com.opensymphony.xwork2.util.DomHelper;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * Looks in the classpath for an XML file, "xwork.xml" by default,
 * and uses it for the XWork configuration.
 *
 * @author tmjee
 * @author Rainer Hermanns
 * @author Neo
 * @version $Revision$
 */
public class XmlConfigurationProvider implements ConfigurationProvider {

    private static final Logger LOG = LogManager.getLogger(XmlConfigurationProvider.class);

    private List<Document> documents;
    private Set<String> includedFileNames;
    private String configFileName;
    private ObjectFactory objectFactory;

    private Set<String> loadedFileUrls = new HashSet<>();
    private boolean errorIfMissing;
    private Map<String, String> dtdMappings;
    private Configuration configuration;
    private boolean throwExceptionOnDuplicateBeans = true;
    private Map<String, Element> declaredPackages = new HashMap<>();

    private FileManager fileManager;
    private ValueSubstitutor valueSubstitutor;

    public XmlConfigurationProvider() {
        this("xwork.xml", true);
    }

    public XmlConfigurationProvider(String filename) {
        this(filename, true);
    }

    public XmlConfigurationProvider(String filename, boolean errorIfMissing) {
        this.configFileName = filename;
        this.errorIfMissing = errorIfMissing;

        Map<String, String> mappings = new HashMap<>();
        mappings.put("-//Apache Struts//XWork 2.5//EN", "xwork-2.5.dtd");
        mappings.put("-//Apache Struts//XWork 2.3//EN", "xwork-2.3.dtd");
        mappings.put("-//Apache Struts//XWork 2.1.3//EN", "xwork-2.1.3.dtd");
        mappings.put("-//Apache Struts//XWork 2.1//EN", "xwork-2.1.dtd");
        mappings.put("-//Apache Struts//XWork 2.0//EN", "xwork-2.0.dtd");
        mappings.put("-//Apache Struts//XWork 1.1.1//EN", "xwork-1.1.1.dtd");
        mappings.put("-//Apache Struts//XWork 1.1//EN", "xwork-1.1.dtd");
        mappings.put("-//Apache Struts//XWork 1.0//EN", "xwork-1.0.dtd");
        setDtdMappings(mappings);
    }

    public void setThrowExceptionOnDuplicateBeans(boolean val) {
        this.throwExceptionOnDuplicateBeans = val;
    }

    public void setDtdMappings(Map<String, String> mappings) {
        this.dtdMappings = Collections.unmodifiableMap(mappings);
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject(required = false)
    public void setValueSubstitutor(ValueSubstitutor valueSubstitutor) {
        this.valueSubstitutor = valueSubstitutor;
    }

    /**
     * Returns an unmodifiable map of DTD mappings
     *
     * @return map of DTD mappings
     */
    public Map<String, String> getDtdMappings() {
        return dtdMappings;
    }

    public void init(Configuration configuration) {
        this.configuration = configuration;
        this.includedFileNames = configuration.getLoadedFileNames();
        loadDocuments(configFileName);
    }

    public void destroy() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof XmlConfigurationProvider)) {
            return false;
        }

        final XmlConfigurationProvider xmlConfigurationProvider = (XmlConfigurationProvider) o;

        if ((configFileName != null) ? (!configFileName.equals(xmlConfigurationProvider.configFileName)) : (xmlConfigurationProvider.configFileName != null)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return ((configFileName != null) ? configFileName.hashCode() : 0);
    }

    private void loadDocuments(String configFileName) {
        try {
            loadedFileUrls.clear();
            documents = loadConfigurationFiles(configFileName, null);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Error loading configuration file " + configFileName, e);
        }
    }

    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        LOG.trace("Parsing configuration file [{}]", configFileName);
        Map<String, Node> loadedBeans = new HashMap<>();
        for (Document doc : documents) {
            Element rootElement = doc.getDocumentElement();
            NodeList children = rootElement.getChildNodes();
            int childSize = children.getLength();

            for (int i = 0; i < childSize; i++) {
                Node childNode = children.item(i);

                if (childNode instanceof Element) {
                    Element child = (Element) childNode;

                    final String nodeName = child.getNodeName();

                    if ("bean".equals(nodeName)) {
                        String type = child.getAttribute("type");
                        String name = child.getAttribute("name");
                        String impl = child.getAttribute("class");
                        String onlyStatic = child.getAttribute("static");
                        String scopeStr = child.getAttribute("scope");
                        boolean optional = "true".equals(child.getAttribute("optional"));
                        Scope scope = Scope.SINGLETON;
                        if ("prototype".equals(scopeStr)) {
                            scope = Scope.PROTOTYPE;
                        } else if ("request".equals(scopeStr)) {
                            scope = Scope.REQUEST;
                        } else if ("session".equals(scopeStr)) {
                            scope = Scope.SESSION;
                        } else if ("singleton".equals(scopeStr)) {
                            scope = Scope.SINGLETON;
                        } else if ("thread".equals(scopeStr)) {
                            scope = Scope.THREAD;
                        }

                        if (StringUtils.isEmpty(name)) {
                            name = Container.DEFAULT_NAME;
                        }

                        try {
                            Class classImpl = ClassLoaderUtil.loadClass(impl, getClass());
                            Class classType = classImpl;
                            if (StringUtils.isNotEmpty(type)) {
                                classType = ClassLoaderUtil.loadClass(type, getClass());
                            }
                            if ("true".equals(onlyStatic)) {
                                // Force loading of class to detect no class def found exceptions
                                classImpl.getDeclaredClasses();
                                containerBuilder.injectStatics(classImpl);
                            } else {
                                if (containerBuilder.contains(classType, name)) {
                                    Location loc = LocationUtils.getLocation(loadedBeans.get(classType.getName() + name));
                                    if (throwExceptionOnDuplicateBeans) {
                                        throw new ConfigurationException("Bean type " + classType + " with the name " +
                                                name + " has already been loaded by " + loc, child);
                                    }
                                }

                                // Force loading of class to detect no class def found exceptions
                                classImpl.getDeclaredConstructors();

                                LOG.debug("Loaded type: {} name: {} impl: {}", type, name, impl);
                                containerBuilder.factory(classType, name, new LocatableFactory(name, classType, classImpl, scope, childNode), scope);
                            }
                            loadedBeans.put(classType.getName() + name, child);
                        } catch (Throwable ex) {
                            if (!optional) {
                                throw new ConfigurationException("Unable to load bean: type:" + type + " class:" + impl, ex, childNode);
                            } else {
                                LOG.debug("Unable to load optional class: {}", impl);
                            }
                        }
                    } else if ("constant".equals(nodeName)) {
                        String name = child.getAttribute("name");
                        String value = child.getAttribute("value");

                        if (valueSubstitutor != null) {
                            LOG.debug("Substituting value [{}] using [{}]", value, valueSubstitutor.getClass().getName());
                            value = valueSubstitutor.substitute(value);
                        }

                        props.setProperty(name, value, childNode);
                    } else if (nodeName.equals("unknown-handler-stack")) {
                        List<UnknownHandlerConfig> unknownHandlerStack = new ArrayList<UnknownHandlerConfig>();
                        NodeList unknownHandlers = child.getElementsByTagName("unknown-handler-ref");
                        int unknownHandlersSize = unknownHandlers.getLength();

                        for (int k = 0; k < unknownHandlersSize; k++) {
                            Element unknownHandler = (Element) unknownHandlers.item(k);
                            Location location = LocationUtils.getLocation(unknownHandler);
                            unknownHandlerStack.add(new UnknownHandlerConfig(unknownHandler.getAttribute("name"), location));
                        }

                        if (!unknownHandlerStack.isEmpty())
                            configuration.setUnknownHandlerStack(unknownHandlerStack);
                    }
                }
            }
        }
    }

    public void loadPackages() throws ConfigurationException {
        List<Element> reloads = new ArrayList<Element>();
        verifyPackageStructure();

        for (Document doc : documents) {
            Element rootElement = doc.getDocumentElement();
            NodeList children = rootElement.getChildNodes();
            int childSize = children.getLength();

            for (int i = 0; i < childSize; i++) {
                Node childNode = children.item(i);

                if (childNode instanceof Element) {
                    Element child = (Element) childNode;

                    final String nodeName = child.getNodeName();

                    if ("package".equals(nodeName)) {
                        PackageConfig cfg = addPackage(child);
                        if (cfg.isNeedsRefresh()) {
                            reloads.add(child);
                        }
                    }
                }
            }
            loadExtraConfiguration(doc);
        }

        if (reloads.size() > 0) {
            reloadRequiredPackages(reloads);
        }

        for (Document doc : documents) {
            loadExtraConfiguration(doc);
        }

        documents.clear();
        declaredPackages.clear();
        configuration = null;
    }

    private void verifyPackageStructure() {
        DirectedGraph<String> graph = new DirectedGraph<>();

        for (Document doc : documents) {
            Element rootElement = doc.getDocumentElement();
            NodeList children = rootElement.getChildNodes();
            int childSize = children.getLength();
            for (int i = 0; i < childSize; i++) {
                Node childNode = children.item(i);
                if (childNode instanceof Element) {
                    Element child = (Element) childNode;

                    final String nodeName = child.getNodeName();

                    if ("package".equals(nodeName)) {
                        String packageName = child.getAttribute("name");
                        declaredPackages.put(packageName, child);
                        graph.addNode(packageName);

                        String extendsAttribute = child.getAttribute("extends");
                        List<String> parents = ConfigurationUtil.buildParentListFromString(extendsAttribute);
                        for (String parent : parents) {
                            graph.addNode(parent);
                            graph.addEdge(packageName, parent);
                        }
                    }
                }
            }
        }

        CycleDetector<String> detector = new CycleDetector<>(graph);
        if (detector.containsCycle()) {
            StringBuilder builder = new StringBuilder("The following packages participate in cycles:");
            for (String packageName : detector.getVerticesInCycles()) {
                builder.append(" ");
                builder.append(packageName);
            }
            throw new ConfigurationException(builder.toString());
        }
    }

    private void reloadRequiredPackages(List<Element> reloads) {
        if (reloads.size() > 0) {
            List<Element> result = new ArrayList<>();
            for (Element pkg : reloads) {
                PackageConfig cfg = addPackage(pkg);
                if (cfg.isNeedsRefresh()) {
                    result.add(pkg);
                }
            }
            if ((result.size() > 0) && (result.size() != reloads.size())) {
                reloadRequiredPackages(result);
                return;
            }

            // Print out error messages for all misconfigured inheritance packages
            if (result.size() > 0) {
                for (Element rp : result) {
                    String parent = rp.getAttribute("extends");
                    if (parent != null) {
                        List<PackageConfig> parents = ConfigurationUtil.buildParentsFromString(configuration, parent);
                        if (parents != null && parents.size() <= 0) {
                            LOG.error("Unable to find parent packages {}", parent);
                        }
                    }
                }
            }
        }
    }

    /**
     * Tells whether the ConfigurationProvider should reload its configuration. This method should only be called
     * if ConfigurationManager.isReloadingConfigs() is true.
     *
     * @return true if the file has been changed since the last time we read it
     */
    public boolean needsReload() {

        for (String url : loadedFileUrls) {
            if (fileManager.fileNeedsReloading(url)) {
                return true;
            }
        }
        return false;
    }

    protected void addAction(Element actionElement, PackageConfig.Builder packageContext) throws ConfigurationException {
        String name = actionElement.getAttribute("name");
        String className = actionElement.getAttribute("class");
        //methodName should be null if it's not set
        String methodName = StringUtils.trimToNull(actionElement.getAttribute("method"));
        Location location = DomHelper.getLocationObject(actionElement);

        if (location == null) {
            LOG.warn("Location null for {}", className);
        }

        // if there isn't a class name specified for an <action/> then try to
        // use the default-class-ref from the <package/>
        if (StringUtils.isEmpty(className)) {
            // if there is a package default-class-ref use that, otherwise use action support
           /* if (StringUtils.isNotEmpty(packageContext.getDefaultClassRef())) {
                className = packageContext.getDefaultClassRef();
            } else {
                className = ActionSupport.class.getName();
            }*/

        } else {
            if (!verifyAction(className, name, location)) {
                LOG.error("Unable to verify action [{}] with class [{}], from [{}]", name, className, location);
                return;
            }
        }

        Map<String, ResultConfig> results;
        try {
            results = buildResults(actionElement, packageContext);
        } catch (ConfigurationException e) {
            throw new ConfigurationException("Error building results for action " + name + " in namespace " + packageContext.getNamespace(), e, actionElement);
        }

        List<InterceptorMapping> interceptorList = buildInterceptorList(actionElement, packageContext);

        List<ExceptionMappingConfig> exceptionMappings = buildExceptionMappings(actionElement, packageContext);

        Set<String> allowedMethods = buildAllowedMethods(actionElement, packageContext);

        ActionConfig actionConfig = new ActionConfig.Builder(packageContext.getName(), name, className)
                .methodName(methodName)
                .addResultConfigs(results)
                .addInterceptors(interceptorList)
                .addExceptionMappings(exceptionMappings)
                .addParams(XmlHelper.getParams(actionElement))
                .setStrictMethodInvocation(packageContext.isStrictMethodInvocation())
                .addAllowedMethod(allowedMethods)
                .location(location)
                .build();
        packageContext.addActionConfig(name, actionConfig);

        LOG.debug("Loaded {}{} in '{}' package: {}",
                StringUtils.isNotEmpty(packageContext.getNamespace()) ? (packageContext.getNamespace() + "/") : "",
                name, packageContext.getName(), actionConfig);
    }

    protected boolean verifyAction(String className, String name, Location loc) {
        if (className.contains("{")) {
            LOG.debug("Action class [{}] contains a wildcard replacement value, so it can't be verified", className);
            return true;
        }
        try {
            if (objectFactory.isNoArgConstructorRequired()) {
                Class clazz = objectFactory.getClassInstance(className);
                if (!Modifier.isPublic(clazz.getModifiers())) {
                    throw new ConfigurationException("Action class [" + className + "] is not public", loc);
                }
                clazz.getConstructor(new Class[]{});
            }
        } catch (ClassNotFoundException e) {
            LOG.debug("Class not found for action [{}]", className, e);
            throw new ConfigurationException("Action class [" + className + "] not found", loc);
        } catch (NoSuchMethodException e) {
            LOG.debug("No constructor found for action [{}]", className, e);
            throw new ConfigurationException("Action class [" + className + "] does not have a public no-arg constructor", e, loc);
        } catch (RuntimeException ex) {
            // Probably not a big deal, like request or session-scoped Spring beans that need a real request
            LOG.info("Unable to verify action class [{}] exists at initialization", className);
            LOG.debug("Action verification cause", ex);
        } catch (Exception ex) {
            // Default to failing fast
            LOG.debug("Unable to verify action class [{}]", className, ex);
            throw new ConfigurationException(ex, loc);
        }
        return true;
    }

    /**
     * Create a PackageConfig from an XML element representing it.
     *
     * @param packageElement the given XML element
     * @return the package config
     * @throws ConfigurationException in case of configuration errors
     */
    protected PackageConfig addPackage(Element packageElement) throws ConfigurationException {
        String packageName = packageElement.getAttribute("name");
        PackageConfig packageConfig = configuration.getPackageConfig(packageName);
        if (packageConfig != null) {
            LOG.debug("Package [{}] already loaded, skipping re-loading it and using existing PackageConfig [{}]", packageName, packageConfig);
            return packageConfig;
        }

        PackageConfig.Builder newPackage = buildPackageContext(packageElement);

        if (newPackage.isNeedsRefresh()) {
            return newPackage.build();
        }

        LOG.debug("Loaded {}", newPackage);

        // add result types (and default result) to this package
        addResultTypes(newPackage, packageElement);

        // load the interceptors and interceptor stacks for this package
        loadInterceptors(newPackage, packageElement);

        // load the default interceptor reference for this package
        loadDefaultInterceptorRef(newPackage, packageElement);

        // load the default class ref for this package
        loadDefaultClassRef(newPackage, packageElement);

        // load the global result list for this package
        loadGlobalResults(newPackage, packageElement);

        loadGlobalAllowedMethods(newPackage, packageElement);

        // load the global exception handler list for this package
        loadGlobalExceptionMappings(newPackage, packageElement);

        // get actions
        NodeList actionList = packageElement.getElementsByTagName("action");

        for (int i = 0; i < actionList.getLength(); i++) {
            Element actionElement = (Element) actionList.item(i);
            addAction(actionElement, newPackage);
        }

        // load the default action reference for this package
        loadDefaultActionRef(newPackage, packageElement);

        PackageConfig cfg = newPackage.build();
        configuration.addPackageConfig(cfg.getName(), cfg);
        return cfg;
    }

    protected void addResultTypes(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("result-type");

        for (int i = 0; i < resultTypeList.getLength(); i++) {
            Element resultTypeElement = (Element) resultTypeList.item(i);
            String name = resultTypeElement.getAttribute("name");
            String className = resultTypeElement.getAttribute("class");
            String def = resultTypeElement.getAttribute("default");

            Location loc = DomHelper.getLocationObject(resultTypeElement);

            Class clazz = verifyResultType(className, loc);
            if (clazz != null) {
                String paramName = null;
                try {
                    paramName = (String) clazz.getField("DEFAULT_PARAM").get(null);
                } catch (Throwable t) {
                    LOG.debug("The result type [{}] doesn't have a default param [DEFAULT_PARAM] defined!", className, t);
                }
                ResultTypeConfig.Builder resultType = new ResultTypeConfig.Builder(name, className).defaultResultParam(paramName)
                        .location(DomHelper.getLocationObject(resultTypeElement));

                Map<String, String> params = XmlHelper.getParams(resultTypeElement);

                if (!params.isEmpty()) {
                    resultType.addParams(params);
                }
                packageContext.addResultTypeConfig(resultType.build());

                // set the default result type
                if (BooleanUtils.toBoolean(def)) {
                    packageContext.defaultResultType(name);
                }
            }
        }
    }

    protected Class verifyResultType(String className, Location loc) {
        try {
            return objectFactory.getClassInstance(className);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOG.warn("Result class [{}] doesn't exist ({}) at {}, ignoring", className, e.getClass().getSimpleName(), loc, e);
        }

        return null;
    }

    protected List<InterceptorMapping> buildInterceptorList(Element element, PackageConfig.Builder context) throws ConfigurationException {
        List<InterceptorMapping> interceptorList = new ArrayList<>();
        NodeList interceptorRefList = element.getElementsByTagName("interceptor-ref");

        for (int i = 0; i < interceptorRefList.getLength(); i++) {
            Element interceptorRefElement = (Element) interceptorRefList.item(i);

            if (interceptorRefElement.getParentNode().equals(element) || interceptorRefElement.getParentNode().getNodeName().equals(element.getNodeName())) {
                List<InterceptorMapping> interceptors = lookupInterceptorReference(context, interceptorRefElement);
                interceptorList.addAll(interceptors);
            }
        }

        return interceptorList;
    }

    /**
     * <p>
     * This method builds a package context by looking for the parents of this new package.
     * </p>
     *
     * <p>
     * If no parents are found, it will return a root package.
     * </p>
     *
     * @param packageElement the package element
     *
     * @return the package config builder
     */
    protected PackageConfig.Builder buildPackageContext(Element packageElement) {
        String parent = packageElement.getAttribute("extends");
        String abstractVal = packageElement.getAttribute("abstract");
        boolean isAbstract = Boolean.parseBoolean(abstractVal);
        String name = StringUtils.defaultString(packageElement.getAttribute("name"));
        String namespace = StringUtils.defaultString(packageElement.getAttribute("namespace"));

        // Strict DMI is enabled by default, it can disabled by user
        boolean strictDMI = true;
        if (packageElement.hasAttribute("strict-method-invocation")) {
            strictDMI = Boolean.parseBoolean(packageElement.getAttribute("strict-method-invocation"));
        }

        PackageConfig.Builder cfg = new PackageConfig.Builder(name)
                .namespace(namespace)
                .isAbstract(isAbstract)
                .strictMethodInvocation(strictDMI)
                .location(DomHelper.getLocationObject(packageElement));

        if (StringUtils.isNotEmpty(StringUtils.defaultString(parent))) { // has parents, let's look it up
            List<PackageConfig> parents = new ArrayList<>();
            for (String parentPackageName : ConfigurationUtil.buildParentListFromString(parent)) {
                if (configuration.getPackageConfigNames().contains(parentPackageName)) {
                    parents.add(configuration.getPackageConfig(parentPackageName));
                } else if (declaredPackages.containsKey(parentPackageName)) {
                    if (configuration.getPackageConfig(parentPackageName) == null) {
                        addPackage(declaredPackages.get(parentPackageName));
                    }
                    parents.add(configuration.getPackageConfig(parentPackageName));
                } else {
                    throw new ConfigurationException("Parent package is not defined: " + parentPackageName);
                }

            }

            if (parents.size() <= 0) {
                cfg.needsRefresh(true);
            } else {
                cfg.addParents(parents);
            }
        }

        return cfg;
    }

    /**
     * Build a map of ResultConfig objects from below a given XML element.
     *
     * @param element the given XML element
     * @param packageContext the package context
     *
     * @return map of result config objects
     */
    protected Map<String, ResultConfig> buildResults(Element element, PackageConfig.Builder packageContext) {
        NodeList resultEls = element.getElementsByTagName("result");

        Map<String, ResultConfig> results = new LinkedHashMap<>();

        for (int i = 0; i < resultEls.getLength(); i++) {
            Element resultElement = (Element) resultEls.item(i);

            if (resultElement.getParentNode().equals(element) || resultElement.getParentNode().getNodeName().equals(element.getNodeName())) {
                String resultName = resultElement.getAttribute("name");
                String resultType = resultElement.getAttribute("type");

                // if you don't specify a name on <result/>, it defaults to "success"
                if (StringUtils.isEmpty(resultName)) {
                    resultName = Action.SUCCESS;
                }

                // there is no result type, so let's inherit from the parent package
                if (StringUtils.isEmpty(resultType)) {
                    resultType = packageContext.getFullDefaultResultType();

                    // now check if there is a result type now
                    if (StringUtils.isEmpty(resultType)) {
                        // uh-oh, we have a problem
                        throw new ConfigurationException("No result type specified for result named '"
                                + resultName + "', perhaps the parent package does not specify the result type?", resultElement);
                    }
                }


                ResultTypeConfig config = packageContext.getResultType(resultType);

                if (config == null) {
                    throw new ConfigurationException("There is no result type defined for type '" + resultType
                            + "' mapped with name '" + resultName + "'."
                            + "  Did you mean '" + guessResultType(resultType) + "'?", resultElement);
                }

                String resultClass = config.getClassName();

                // invalid result type specified in result definition
                if (resultClass == null) {
                    throw new ConfigurationException("Result type '" + resultType + "' is invalid");
                }

                Map<String, String> resultParams = XmlHelper.getParams(resultElement);

                if (resultParams.size() == 0) // maybe we just have a body - therefore a default parameter
                {
                    // if <result ...>something</result> then we add a parameter of 'something' as this is the most used result param
                    if (resultElement.getChildNodes().getLength() >= 1) {
                        resultParams = new LinkedHashMap<>();

                        String paramName = config.getDefaultResultParam();
                        if (paramName != null) {
                            StringBuilder paramValue = new StringBuilder();
                            for (int j = 0; j < resultElement.getChildNodes().getLength(); j++) {
                                if (resultElement.getChildNodes().item(j).getNodeType() == Node.TEXT_NODE) {
                                    String val = resultElement.getChildNodes().item(j).getNodeValue();
                                    if (val != null) {
                                        paramValue.append(val);
                                    }
                                }
                            }
                            String val = paramValue.toString().trim();
                            if (val.length() > 0) {
                                resultParams.put(paramName, val);
                            }
                        } else {
                            LOG.debug("No default parameter defined for result [{}] of type [{}] ", config.getName(), config.getClassName());
                        }
                    }
                }

                // create new param map, so that the result param can override the config param
                Map<String, String> params = new LinkedHashMap<String, String>();
                Map<String, String> configParams = config.getParams();
                if (configParams != null) {
                    params.putAll(configParams);
                }
                params.putAll(resultParams);

                Set<String> resultNamesSet = TextParseUtil.commaDelimitedStringToSet(resultName);
                if (resultNamesSet.isEmpty()) {
                    resultNamesSet.add(resultName);
                }

                for (String name : resultNamesSet) {
                    ResultConfig resultConfig = new ResultConfig.Builder(name, resultClass)
                            .addParams(params)
                            .location(DomHelper.getLocationObject(element))
                            .build();
                    results.put(resultConfig.getName(), resultConfig);
                }
            }
        }

        return results;
    }

    protected String guessResultType(String type) {
        StringBuilder sb = null;
        if (type != null) {
            sb = new StringBuilder();
            boolean capNext = false;
            for (int x=0; x<type.length(); x++) {
                char c = type.charAt(x);
                if (c == '-') {
                    capNext = true;
                    continue;
                } else if (Character.isLowerCase(c) && capNext) {
                    c = Character.toUpperCase(c);
                    capNext = false;
                }
                sb.append(c);
            }
        }
        return (sb != null ? sb.toString() : null);
    }

    /**
     * Build a list of exception mapping objects from below a given XML element.
     *
     * @param element the given XML element
     * @param packageContext the package context
     *
     * @return list of exception mapping config objects
     */
    protected List<ExceptionMappingConfig> buildExceptionMappings(Element element, PackageConfig.Builder packageContext) {
        NodeList exceptionMappingEls = element.getElementsByTagName("exception-mapping");

        List<ExceptionMappingConfig> exceptionMappings = new ArrayList<>();

        for (int i = 0; i < exceptionMappingEls.getLength(); i++) {
            Element ehElement = (Element) exceptionMappingEls.item(i);

            if (ehElement.getParentNode().equals(element) || ehElement.getParentNode().getNodeName().equals(element.getNodeName())) {
                String emName = ehElement.getAttribute("name");
                String exceptionClassName = ehElement.getAttribute("exception");
                String exceptionResult = ehElement.getAttribute("result");

                Map<String, String> params = XmlHelper.getParams(ehElement);

                if (StringUtils.isEmpty(emName)) {
                    emName = exceptionResult;
                }

                ExceptionMappingConfig ehConfig = new ExceptionMappingConfig.Builder(emName, exceptionClassName, exceptionResult)
                        .addParams(params)
                        .location(DomHelper.getLocationObject(ehElement))
                        .build();
                exceptionMappings.add(ehConfig);
            }
        }

        return exceptionMappings;
    }

    protected Set<String> buildAllowedMethods(Element element, PackageConfig.Builder packageContext) {
        NodeList allowedMethodsEls = element.getElementsByTagName("allowed-methods");

        Set<String> allowedMethods;
        if (allowedMethodsEls.getLength() > 0) {
            // user defined 'allowed-methods' so used them whatever Strict DMI was enabled or not
            allowedMethods = new HashSet<>(packageContext.getGlobalAllowedMethods());

            if (allowedMethodsEls.getLength() > 0) {
                Node n = allowedMethodsEls.item(0).getFirstChild();
                if (n != null) {
                    String s = n.getNodeValue().trim();
                    if (s.length() > 0) {
                        allowedMethods.addAll(TextParseUtil.commaDelimitedStringToSet(s));
                    }
                }
            }
        } else if (packageContext.isStrictMethodInvocation()) {
            // user enabled Strict DMI but didn't defined action specific 'allowed-methods' so we use 'global-allowed-methods' only
            allowedMethods = new HashSet<>(packageContext.getGlobalAllowedMethods());
        } else {
            // Strict DMI is disabled so any method can be called
            allowedMethods = new HashSet<>();
            allowedMethods.add(ActionConfig.WILDCARD);
        }

        LOG.debug("Collected allowed methods: {}", allowedMethods);

        return Collections.unmodifiableSet(allowedMethods);
    }

    protected void loadDefaultInterceptorRef(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("default-interceptor-ref");

        if (resultTypeList.getLength() > 0) {
            Element defaultRefElement = (Element) resultTypeList.item(0);
            packageContext.defaultInterceptorRef(defaultRefElement.getAttribute("name"));
        }
    }

    protected void loadDefaultActionRef(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("default-action-ref");

        if (resultTypeList.getLength() > 0) {
            Element defaultRefElement = (Element) resultTypeList.item(0);
            packageContext.defaultActionRef(defaultRefElement.getAttribute("name"));
        }
    }

    /**
     * Load all of the global results for this package from the XML element.
     *
     * @param packageContext the package context
     * @param packageElement the given XML element
     */
    protected void loadGlobalResults(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalResultList = packageElement.getElementsByTagName("global-results");

        if (globalResultList.getLength() > 0) {
            Element globalResultElement = (Element) globalResultList.item(0);
            Map<String, ResultConfig> results = buildResults(globalResultElement, packageContext);
            packageContext.addGlobalResultConfigs(results);
        }
    }

    protected void loadGlobalAllowedMethods(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalAllowedMethodsElms = packageElement.getElementsByTagName("global-allowed-methods");

        if (globalAllowedMethodsElms.getLength() > 0) {
            Set<String> globalAllowedMethods = new HashSet<>();
            Node n = globalAllowedMethodsElms.item(0).getFirstChild();
            if (n != null) {
                String s = n.getNodeValue().trim();
                if (s.length() > 0) {
                    globalAllowedMethods = TextParseUtil.commaDelimitedStringToSet(s);
                }
            }
            packageContext.addGlobalAllowedMethods(globalAllowedMethods);
        }
    }

    protected void loadDefaultClassRef(PackageConfig.Builder packageContext, Element element) {
        NodeList defaultClassRefList = element.getElementsByTagName("default-class-ref");
        if (defaultClassRefList.getLength() > 0) {
            Element defaultClassRefElement = (Element) defaultClassRefList.item(0);
            packageContext.defaultClassRef(defaultClassRefElement.getAttribute("class"));
        }
    }

    /**
     * Load all of the global results for this package from the XML element.
     *
     * @param packageContext the package context
     * @param packageElement the given XML element
     */
    protected void loadGlobalExceptionMappings(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalExceptionMappingList = packageElement.getElementsByTagName("global-exception-mappings");

        if (globalExceptionMappingList.getLength() > 0) {
            Element globalExceptionMappingElement = (Element) globalExceptionMappingList.item(0);
            List<ExceptionMappingConfig> exceptionMappings = buildExceptionMappings(globalExceptionMappingElement, packageContext);
            packageContext.addGlobalExceptionMappingConfigs(exceptionMappings);
        }
    }

    protected InterceptorStackConfig loadInterceptorStack(Element element, PackageConfig.Builder context) throws ConfigurationException {
        String name = element.getAttribute("name");

        InterceptorStackConfig.Builder config = new InterceptorStackConfig.Builder(name)
                .location(DomHelper.getLocationObject(element));
        NodeList interceptorRefList = element.getElementsByTagName("interceptor-ref");

        for (int j = 0; j < interceptorRefList.getLength(); j++) {
            Element interceptorRefElement = (Element) interceptorRefList.item(j);
            List<InterceptorMapping> interceptors = lookupInterceptorReference(context, interceptorRefElement);
            config.addInterceptors(interceptors);
        }

        return config.build();
    }

    protected void loadInterceptorStacks(Element element, PackageConfig.Builder context) throws ConfigurationException {
        NodeList interceptorStackList = element.getElementsByTagName("interceptor-stack");

        for (int i = 0; i < interceptorStackList.getLength(); i++) {
            Element interceptorStackElement = (Element) interceptorStackList.item(i);

            InterceptorStackConfig config = loadInterceptorStack(interceptorStackElement, context);

            context.addInterceptorStackConfig(config);
        }
    }

    protected void loadInterceptors(PackageConfig.Builder context, Element element) throws ConfigurationException {
        NodeList interceptorList = element.getElementsByTagName("interceptor");

        for (int i = 0; i < interceptorList.getLength(); i++) {
            Element interceptorElement = (Element) interceptorList.item(i);
            String name = interceptorElement.getAttribute("name");
            String className = interceptorElement.getAttribute("class");

            Map<String, String> params = XmlHelper.getParams(interceptorElement);
            InterceptorConfig config = new InterceptorConfig.Builder(name, className)
                    .addParams(params)
                    .location(DomHelper.getLocationObject(interceptorElement))
                    .build();

            context.addInterceptorConfig(config);
        }

        loadInterceptorStacks(element, context);
    }

    private List<Document> loadConfigurationFiles(String fileName, Element includeElement) {
        List<Document> docs = new ArrayList<>();
        List<Document> finalDocs = new ArrayList<>();
        if (!includedFileNames.contains(fileName)) {
            LOG.debug("Loading action configurations from: {}", fileName);

            includedFileNames.add(fileName);

            Iterator<URL> urls = null;
            InputStream is = null;

            IOException ioException = null;
            try {
                urls = getConfigurationUrls(fileName);
            } catch (IOException ex) {
                ioException = ex;
            }

            if (urls == null || !urls.hasNext()) {
                if (errorIfMissing) {
                    throw new ConfigurationException("Could not open files of the name " + fileName, ioException);
                } else {
                    LOG.trace("Unable to locate configuration files of the name {}, skipping", fileName);
                    return docs;
                }
            }

            URL url = null;
            while (urls.hasNext()) {
                try {
                    url = urls.next();
                    is = fileManager.loadFile(url);

                    InputSource in = new InputSource(is);

                    in.setSystemId(url.toString());

                    docs.add(DomHelper.parse(in, dtdMappings));
                    loadedFileUrls.add(url.toString());
                } catch (XWorkException e) {
                    if (includeElement != null) {
                        throw new ConfigurationException("Unable to load " + url, e, includeElement);
                    } else {
                        throw new ConfigurationException("Unable to load " + url, e);
                    }
                } catch (Exception e) {
                    throw new ConfigurationException("Caught exception while loading file " + fileName, e, includeElement);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            LOG.error("Unable to close input stream", e);
                        }
                    }
                }
            }

            //sort the documents, according to the "order" attribute
            Collections.sort(docs, new Comparator<Document>() {
                public int compare(Document doc1, Document doc2) {
                    return XmlHelper.getLoadOrder(doc1).compareTo(XmlHelper.getLoadOrder(doc2));
                }
            });

            for (Document doc : docs) {
                Element rootElement = doc.getDocumentElement();
                NodeList children = rootElement.getChildNodes();
                int childSize = children.getLength();

                for (int i = 0; i < childSize; i++) {
                    Node childNode = children.item(i);

                    if (childNode instanceof Element) {
                        Element child = (Element) childNode;

                        final String nodeName = child.getNodeName();

                        if ("include".equals(nodeName)) {
                            String includeFileName = child.getAttribute("file");
                            if (includeFileName.indexOf('*') != -1) {
                                // handleWildCardIncludes(includeFileName, docs, child);
                                ClassPathFinder wildcardFinder = new ClassPathFinder();
                                wildcardFinder.setPattern(includeFileName);
                                Vector<String> wildcardMatches = wildcardFinder.findMatches();
                                for (String match : wildcardMatches) {
                                    finalDocs.addAll(loadConfigurationFiles(match, child));
                                }
                            } else {
                                finalDocs.addAll(loadConfigurationFiles(includeFileName, child));
                            }
                        }
                    }
                }
                finalDocs.add(doc);
            }

            LOG.debug("Loaded action configuration from: {}", fileName);
        }
        return finalDocs;
    }

    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
        return ClassLoaderUtil.getResources(fileName, XmlConfigurationProvider.class, false);
    }

    /**
     * Allows subclasses to load extra information from the document
     *
     * @param doc The configuration document
     */
    protected void loadExtraConfiguration(Document doc) {
        // no op
    }

    /**
     * Looks up the Interceptor Class from the interceptor-ref name and creates an instance, which is added to the
     * provided List, or, if this is a ref to a stack, it adds the Interceptor instances from the List to this stack.
     *
     * @param context               The PackageConfig to lookup the interceptor from
     * @param interceptorRefElement Element to pull interceptor ref data from
     * @return A list of Interceptor objects
     * @throws ConfigurationException in case of configuration errors
     */
    private List<InterceptorMapping> lookupInterceptorReference(PackageConfig.Builder context, Element interceptorRefElement) throws ConfigurationException {
        String refName = interceptorRefElement.getAttribute("name");
        Map<String, String> refParams = XmlHelper.getParams(interceptorRefElement);

        Location loc = LocationUtils.getLocation(interceptorRefElement);
        return InterceptorBuilder.constructInterceptorReference(context, refName, refParams, loc, objectFactory);
    }

    List<Document> getDocuments() {
        return documents;
    }

    @Override
    public String toString() {
        return "XmlConfigurationProvider{" +
                "configFileName='" + configFileName + '\'' +
                '}';
    }
}
