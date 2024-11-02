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
package org.apache.struts2.config.providers;

import org.apache.struts2.action.Action;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.config.BeanSelectionProvider;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.config.ConfigurationUtil;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.ExceptionMappingConfig;
import org.apache.struts2.config.entities.InterceptorConfig;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.InterceptorStackConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.config.entities.ResultTypeConfig;
import org.apache.struts2.config.entities.UnknownHandlerConfig;
import org.apache.struts2.config.impl.LocatableFactory;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.inject.Scope;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.DomHelper;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.struts2.util.location.Location;
import org.apache.struts2.util.location.LocationUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.apache.struts2.util.TextParseUtil.commaDelimitedStringToSet;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.toUpperCase;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * This is a base Struts {@link ConfigurationProvider} for loading configuration from a parsed
 * {@link Document XML document}. By extending this class, configuration can be loaded from any source that an XML
 * document can be parsed from. Note that this class does not validate the document against any provided DTDs. For
 * loading configuration from an XML file with DTD validation, please see
 * {@link org.apache.struts2.config.StrutsXmlConfigurationProvider StrutsXmlConfigurationProvider}.
 *
 * @since 6.2.0
 */
public abstract class XmlDocConfigurationProvider implements ConfigurationProvider {

    private static final Logger LOG = LogManager.getLogger(XmlConfigurationProvider.class);

    protected final Map<String, Element> declaredPackages = new HashMap<>();
    protected List<Document> documents;
    protected ObjectFactory objectFactory;
    protected Map<String, String> dtdMappings = new HashMap<>();
    protected Configuration configuration;
    protected ProviderAllowlist providerAllowlist;
    protected boolean throwExceptionOnDuplicateBeans = true;
    protected ValueSubstitutor valueSubstitutor;
    protected Set<Class<?>> allowlistClasses = new HashSet<>();

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject(required = false)
    public void setValueSubstitutor(ValueSubstitutor valueSubstitutor) {
        this.valueSubstitutor = valueSubstitutor;
    }

    @Inject
    public void setProviderAllowlist(ProviderAllowlist providerAllowlist) {
        this.providerAllowlist = providerAllowlist;
    }

    public XmlDocConfigurationProvider(Document... documents) {
        this.documents = Arrays.asList(documents);
    }

    public void setThrowExceptionOnDuplicateBeans(boolean val) {
        this.throwExceptionOnDuplicateBeans = val;
    }

    public void setDtdMappings(Map<String, String> mappings) {
        this.dtdMappings = Collections.unmodifiableMap(mappings);
    }

    /**
     * Returns an unmodifiable map of DTD mappings
     *
     * @return map of DTD mappings
     */
    public Map<String, String> getDtdMappings() {
        return dtdMappings;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void destroy() {
        if (providerAllowlist != null) {
            providerAllowlist.clearAllowlist(this);
        }
    }

    protected Class<?> allowAndLoadClass(String className) throws ClassNotFoundException {
        Class<?> clazz = loadClass(className);
        allowlistClasses.addAll(ConfigurationUtil.getAllClassTypes(clazz));
        providerAllowlist.registerAllowlist(this, allowlistClasses);
        return clazz;
    }

    protected Class<?> loadClass(String className) throws ClassNotFoundException {
        return objectFactory.getClassInstance(className);
    }

    public static void iterateElementChildren(Document doc, Consumer<Element> function) {
        iterateElementChildren(doc.getDocumentElement(), function);
    }

    public static void iterateElementChildren(Node node, Consumer<Element> function) {
        iterateChildren(node, childNode -> {
            if (!(childNode instanceof Element)) {
                return;
            }
            function.accept((Element) childNode);
        });
    }

    public static void iterateChildren(Node node, Consumer<Node> function) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            function.accept(children.item(i));
        }
    }

    public static void iterateChildrenByTagName(Element el, String tagName, Consumer<Element> function) {
        NodeList childrenByTag = el.getElementsByTagName(tagName);
        for (int i = 0; i < childrenByTag.getLength(); i++) {
            Element childEl = (Element) childrenByTag.item(i);
            function.accept(childEl);
        }
    }

    @Override
    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        allowlistClasses.clear();
        Map<String, Node> loadedBeans = new HashMap<>();
        for (Document doc : documents) {
            iterateElementChildren(doc, child -> {
                switch (child.getNodeName()) {
                    case "bean-selection": {
                        registerBeanSelection(child, containerBuilder, props);
                        break;
                    }
                    case "bean": {
                        registerBean(child, loadedBeans, containerBuilder);
                        break;
                    }
                    case "constant": {
                        registerConstant(child, props);
                        break;
                    }
                    case "unknown-handler-stack":
                        registerUnknownHandlerStack(child);
                        break;
                }
            });
        }
    }

    protected void registerBeanSelection(Element child, ContainerBuilder containerBuilder, LocatableProperties props) {
        String name = child.getAttribute("name");
        String impl = child.getAttribute("class");
        try {
            Class<?> classImpl = ClassLoaderUtil.loadClass(impl, getClass());
            if (BeanSelectionProvider.class.isAssignableFrom(classImpl)) {
                BeanSelectionProvider provider = (BeanSelectionProvider) classImpl.getDeclaredConstructor().newInstance();
                provider.register(containerBuilder, props);
            } else {
                throw new ConfigurationException(format("The bean-provider: name:%s class:%s does not implement %s", name, impl, BeanSelectionProvider.class.getName()), child);
            }
        } catch (ReflectiveOperationException e) {
            throw new ConfigurationException(format("Unable to load bean-provider: name:%s class:%s", name, impl), e, child);
        }
    }

    protected void registerBean(Element child, Map<String, Node> loadedBeans, ContainerBuilder containerBuilder) {
        String type = child.getAttribute("type");
        String name = child.getAttribute("name");
        String impl = child.getAttribute("class");
        String onlyStatic = child.getAttribute("static");
        String scopeStr = child.getAttribute("scope");
        boolean optional = "true".equals(child.getAttribute("optional"));
        Scope scope = Scope.fromString(scopeStr);

        if (name.isEmpty()) {
            name = Container.DEFAULT_NAME;
        }

        try {
            Class<?> classImpl = ClassLoaderUtil.loadClass(impl, getClass());
            Class<?> classType = classImpl;
            if (!type.isEmpty()) {
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
                        throw new ConfigurationException(format("Bean type %s with the name %s has already been loaded by %s", classType, name, loc), child);
                    }
                }

                // Force loading of class to detect no class def found exceptions
                classImpl.getDeclaredConstructors();

                LOG.debug("Loaded type: {} name: {} impl: {}", type, name, impl);
                containerBuilder.factory(classType, name, new LocatableFactory<>(name, classType, classImpl, scope, child), scope);
            }
            loadedBeans.put(classType.getName() + name, child);
        } catch (Throwable ex) {
            if (!optional) {
                throw new ConfigurationException("Unable to load bean: type:" + type + " class:" + impl, ex, child);
            } else {
                LOG.debug("Unable to load optional class: {}", impl);
            }
        }
    }

    protected void registerConstant(Element child, LocatableProperties props) {
        String name = child.getAttribute("name");
        String value = child.getAttribute("value");

        if (valueSubstitutor != null) {
            LOG.debug("Substituting value [{}] using [{}]", value, valueSubstitutor.getClass().getName());
            value = valueSubstitutor.substitute(value);
        }

        props.setProperty(name, value, child);
    }

    protected void registerUnknownHandlerStack(Element child) {
        List<UnknownHandlerConfig> unknownHandlerStack = new ArrayList<>();

        iterateChildrenByTagName(child, "unknown-handler-ref", unknownHandler -> {
            Location location = LocationUtils.getLocation(unknownHandler);
            unknownHandlerStack.add(new UnknownHandlerConfig(unknownHandler.getAttribute("name"), location));
        });

        if (!unknownHandlerStack.isEmpty()) {
            configuration.setUnknownHandlerStack(unknownHandlerStack);
        }
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void loadPackages() throws ConfigurationException {
        List<Element> reloads = new ArrayList<>();
        verifyPackageStructure();

        for (Document doc : documents) {
            iterateElementChildren(doc, child -> {
                if ("package".equals(child.getNodeName())) {
                    PackageConfig cfg = addPackage(child);
                    if (cfg.isNeedsRefresh()) {
                        reloads.add(child);
                    }
                }
            });
            loadExtraConfiguration(doc);
        }

        if (!reloads.isEmpty()) {
            reloadRequiredPackages(reloads);
        }

        for (Document doc : documents) {
            loadExtraConfiguration(doc);
        }

        declaredPackages.clear();
        configuration = null;
    }

    private void verifyPackageStructure() {
        DirectedGraph<String> graph = new DirectedGraph<>();

        for (Document doc : documents) {
            iterateElementChildren(doc, child -> {
                if (!"package".equals(child.getNodeName())) {
                    return;
                }

                String packageName = child.getAttribute("name");
                declaredPackages.put(packageName, child);
                graph.addNode(packageName);

                String extendsAttribute = child.getAttribute("extends");
                for (String parent : ConfigurationUtil.buildParentListFromString(extendsAttribute)) {
                    graph.addNode(parent);
                    graph.addEdge(packageName, parent);
                }
            });
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

    /**
     * Allows subclasses to load extra information from the document
     *
     * @param doc The configuration document
     */
    protected void loadExtraConfiguration(Document doc) {
        // no op
    }

    private void reloadRequiredPackages(List<Element> reloads) {
        if (reloads.isEmpty()) {
            return;
        }

        List<Element> result = new ArrayList<>();
        for (Element pkg : reloads) {
            PackageConfig cfg = addPackage(pkg);
            if (cfg.isNeedsRefresh()) {
                result.add(pkg);
            }
        }
        if (!result.isEmpty() && result.size() != reloads.size()) {
            reloadRequiredPackages(result);
            return;
        }

        // Print out error messages for all misconfigured inheritance packages
        for (Element rp : result) {
            String parent = rp.getAttribute("extends");
            if (!parent.isEmpty() && ConfigurationUtil.buildParentsFromString(configuration, parent).isEmpty()) {
                LOG.error("Unable to find parent packages {}", parent);
            }
        }
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
        iterateChildrenByTagName(packageElement, "action", actionElement -> addAction(actionElement, newPackage));

        // load the default action reference for this package
        loadDefaultActionRef(newPackage, packageElement);

        PackageConfig cfg = newPackage.build();
        configuration.addPackageConfig(cfg.getName(), cfg);
        return cfg;
    }

    protected void addAction(Element actionElement, PackageConfig.Builder packageContext) throws ConfigurationException {
        String name = actionElement.getAttribute("name");
        String className = actionElement.getAttribute("class");

        Location location = DomHelper.getLocationObject(actionElement);

        if (!className.isEmpty()) {
            verifyAction(className, location);
        }

        Map<String, ResultConfig> results;
        try {
            results = buildResults(actionElement, packageContext);
        } catch (ConfigurationException e) {
            throw new ConfigurationException(
                    format("Error building results for action %s in namespace %s", name, packageContext.getNamespace()),
                    e,
                    actionElement);
        }

        ActionConfig actionConfig = buildActionConfig(actionElement, location, packageContext, results);
        packageContext.addActionConfig(actionConfig.getName(), actionConfig);

        LOG.debug("Loaded {}{} in '{}' package: {}",
                isNotEmpty(packageContext.getNamespace()) ? (packageContext.getNamespace() + "/") : "",
                name, packageContext.getName(), actionConfig);
    }

    protected ActionConfig buildActionConfig(Element actionElement,
                                             Location location,
                                             PackageConfig.Builder packageContext,
                                             Map<String, ResultConfig> results) {
        String actionName = actionElement.getAttribute("name");
        String className = actionElement.getAttribute("class");
        // methodName should be null if it's not set
        String methodName = trimToNull(actionElement.getAttribute("method"));

        List<InterceptorMapping> interceptorList = buildInterceptorList(actionElement, packageContext);
        List<ExceptionMappingConfig> exceptionMappings = buildExceptionMappings(actionElement);
        Set<String> allowedMethods = buildAllowedMethods(actionElement, packageContext);

        return new ActionConfig.Builder(packageContext.getName(), actionName, className)
                .methodName(methodName)
                .addResultConfigs(results)
                .addInterceptors(interceptorList)
                .addExceptionMappings(exceptionMappings)
                .addParams(XmlHelper.getParams(actionElement))
                .setStrictMethodInvocation(packageContext.isStrictMethodInvocation())
                .addAllowedMethod(allowedMethods)
                .location(location)
                .build();
    }

    protected void verifyAction(String className, Location loc) {
        if (className.contains("{")) {
            LOG.debug("Action class [{}] contains a wildcard replacement value, so it can't be verified", className);
            return;
        }
        try {
            Class<?> clazz = allowAndLoadClass(className);
            if (objectFactory.isNoArgConstructorRequired()) {
                if (!Modifier.isPublic(clazz.getModifiers())) {
                    throw new ConfigurationException("Action class [" + className + "] is not public", loc);
                }
                clazz.getConstructor();
            }
        } catch (ClassNotFoundException e) {
            if (objectFactory.isNoArgConstructorRequired()) {
                throw new ConfigurationException("Action class [" + className + "] not found", e, loc);
            }
            LOG.warn("Action class [{}] not found", className);
            LOG.debug("Action class [{}] not found", className, e);
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("Action class [" + className + "] does not have a public no-arg constructor", e, loc);
        } catch (RuntimeException ex) {
            // Probably not a big deal, like request or session-scoped Spring beans that need a real request
            LOG.info("Unable to verify action class [{}] exists at initialization", className);
            LOG.debug("Action verification cause", ex);
        } catch (Exception ex) {
            // Default to failing fast
            throw new ConfigurationException("Unable to verify action class [" + className + "]", ex, loc);
        }
    }

    protected void addResultTypes(PackageConfig.Builder packageContext, Element element) {
        iterateChildrenByTagName(element, "result-type", resultTypeElement -> {
            String name = resultTypeElement.getAttribute("name");
            String className = resultTypeElement.getAttribute("class");
            String def = resultTypeElement.getAttribute("default");

            Location loc = DomHelper.getLocationObject(resultTypeElement);
            Class<?> clazz = verifyResultType(className, loc);

            String paramName = null;
            try {
                paramName = (String) clazz.getField("DEFAULT_PARAM").get(null);
            } catch (Throwable t) {
                LOG.debug("The result type [{}] doesn't have a default param [DEFAULT_PARAM] defined!", className, t);
            }
            packageContext.addResultTypeConfig(buildResultTypeConfig(resultTypeElement, loc, paramName));

            if (BooleanUtils.toBoolean(def)) {
                packageContext.defaultResultType(name);
            }
        });
    }

    protected ResultTypeConfig buildResultTypeConfig(Element resultTypeElement, Location location, String paramName) {
        String name = resultTypeElement.getAttribute("name");
        String className = resultTypeElement.getAttribute("class");
        ResultTypeConfig.Builder resultType = new ResultTypeConfig.Builder(name,
                className).defaultResultParam(paramName).location(location);
        Map<String, String> params = XmlHelper.getParams(resultTypeElement);
        if (!params.isEmpty()) {
            resultType.addParams(params);
        }
        return resultType.build();
    }

    protected Class<?> verifyResultType(String className, Location loc) {
        try {
            return allowAndLoadClass(className);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            throw new ConfigurationException("Result class [" + className + "] not found", e, loc);
        }
    }

    /**
     * <p>This method builds a package context by looking for the parents of this new package.</p>
     * <p>If no parents are found, it will return a root package.</p>
     *
     * @param packageElement the package element
     * @return the package config builder
     */
    protected PackageConfig.Builder buildPackageContext(Element packageElement) {
        String parent = packageElement.getAttribute("extends");
        boolean isAbstract = parseBoolean(packageElement.getAttribute("abstract"));
        boolean isFinal = parseBoolean(packageElement.getAttribute("final"));
        String name = defaultString(packageElement.getAttribute("name"));
        String namespace = defaultString(packageElement.getAttribute("namespace"));

        // Strict DMI is enabled by default, it can be disabled by user
        boolean strictDMI = true;
        if (packageElement.hasAttribute("strict-method-invocation")) {
            strictDMI = parseBoolean(packageElement.getAttribute("strict-method-invocation"));
        }

        PackageConfig.Builder cfg = new PackageConfig.Builder(name)
                .namespace(namespace)
                .isAbstract(isAbstract)
                .isFinal(isFinal)
                .strictMethodInvocation(strictDMI)
                .location(DomHelper.getLocationObject(packageElement));

        if (parent.isEmpty()) {
            return cfg;
        }

        // has parents, let's look it up
        List<PackageConfig> parents = new ArrayList<>();
        for (String parentPackageName : ConfigurationUtil.buildParentListFromString(parent)) {
            boolean isParentPackageConfigDefined = false;
            if (configuration.getPackageConfigNames().contains(parentPackageName)) { // parent package already added to configuration
                isParentPackageConfigDefined = true;
            } else if (declaredPackages.containsKey(parentPackageName)) { // parent package declared but yet added to configuration
                addPackage(declaredPackages.get(parentPackageName));
                isParentPackageConfigDefined = true;
            }

            if (isParentPackageConfigDefined) {
                PackageConfig parentPackageConfig = configuration.getPackageConfig(parentPackageName);
                if (parentPackageConfig.isFinal()) {
                    throw new ConfigurationException("Parent package is final and unextendable: " + parentPackageName);
                }
                parents.add(parentPackageConfig);
            } else {
                throw new ConfigurationException("Parent package is not defined: " + parentPackageName);
            }
        }

        if (parents.isEmpty()) {
            cfg.needsRefresh(true);
        } else {
            cfg.addParents(parents);
        }

        return cfg;
    }

    /**
     * Build a map of ResultConfig objects from below a given XML element.
     *
     * @param element        the given XML element
     * @param packageContext the package context
     * @return map of result config objects
     */
    protected Map<String, ResultConfig> buildResults(Element element, PackageConfig.Builder packageContext) {
        Map<String, ResultConfig> results = new LinkedHashMap<>();

        iterateChildrenByTagName(element, "result", resultElement -> {
            Node parNode = resultElement.getParentNode();
            if (!parNode.equals(element) && !parNode.getNodeName().equals(element.getNodeName())) {
                return;
            }

            String resultName = resultElement.getAttribute("name");
            String resultType = resultElement.getAttribute("type");

            // if you don't specify a name on <result/>, it defaults to "success"
            if (StringUtils.isEmpty(resultName)) {
                resultName = Action.SUCCESS;
            }

            // there is no result type, so let's inherit from the parent package
            if (resultType.isEmpty()) {
                resultType = packageContext.getFullDefaultResultType();
                // now check if there is a result type now
                if (resultType.isEmpty()) {
                    throw new ConfigurationException("No result type specified for result named '"
                            + resultName + "', perhaps the parent package does not specify the result type?", resultElement);
                }
            }

            ResultTypeConfig config = packageContext.getResultType(resultType);
            if (config == null) {
                throw new ConfigurationException(format("There is no result type defined for type '%s' mapped with name '%s'. Did you mean '%s'?", resultType, resultName, guessResultType(resultType)), resultElement);
            }

            String resultClass = config.getClassName();
            if (resultClass == null) {
                throw new ConfigurationException("Result type '" + resultType + "' is invalid");
            }

            Set<String> resultNamesSet = commaDelimitedStringToSet(resultName);
            if (resultNamesSet.isEmpty()) {
                resultNamesSet.add(resultName);
            }

            Map<String, String> params = buildResultParams(resultElement, config);
            Location location = DomHelper.getLocationObject(element);

            for (String name : resultNamesSet) {
                ResultConfig resultConfig = buildResultConfig(name, config, location, params);
                results.put(resultConfig.getName(), resultConfig);
            }
        });

        return results;
    }

    protected ResultConfig buildResultConfig(String name,
                                             ResultTypeConfig config,
                                             Location location,
                                             Map<String, String> params) {
        return new ResultConfig.Builder(name, config.getClassName()).location(location).addParams(params).build();
    }

    protected Map<String, String> buildResultParams(Element resultElement, ResultTypeConfig config) {
        Map<String, String> resultParams = XmlHelper.getParams(resultElement);

        // maybe we just have a body - therefore a default parameter
        if (resultParams.isEmpty() && resultElement.getChildNodes().getLength() > 0) {
            // if <result ...>something</result> then we add a parameter of 'something' as this is the most used result param
            resultParams = new LinkedHashMap<>();

            String paramName = config.getDefaultResultParam();
            if (paramName != null) {
                StringBuilder paramValue = new StringBuilder();
                iterateChildren(resultElement, child -> {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        String val = child.getNodeValue();
                        if (val != null) {
                            paramValue.append(val);
                        }
                    }
                });
                String val = paramValue.toString().trim();
                if (!val.isEmpty()) {
                    resultParams.put(paramName, val);
                }
            } else {
                LOG.debug(
                        "No default parameter defined for result [{}] of type [{}] ",
                        config.getName(),
                        config.getClassName());
            }
        }

        // create new param map, so that the result param can override the config param
        Map<String, String> params = new LinkedHashMap<>();
        Map<String, String> configParams = config.getParams();
        if (configParams != null) {
            params.putAll(configParams);
        }
        params.putAll(resultParams);
        return params;
    }

    protected static String guessResultType(String type) {
        if (type == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean capNext = false;
        for (int x = 0; x < type.length(); x++) {
            char c = type.charAt(x);
            if (c == '-') {
                capNext = true;
                continue;
            } else if (isLowerCase(c) && capNext) {
                c = toUpperCase(c);
                capNext = false;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Build a list of exception mapping objects from below a given XML element.
     *
     * @param element the given XML element
     * @return list of exception mapping config objects
     */
    protected List<ExceptionMappingConfig> buildExceptionMappings(Element element) {
        List<ExceptionMappingConfig> exceptionMappings = new ArrayList<>();

        iterateChildrenByTagName(element, "exception-mapping", ehElement -> {
            Node parNode = ehElement.getParentNode();
            if (!parNode.equals(element) && !parNode.getNodeName().equals(element.getNodeName())) {
                return;
            }

            String emName = ehElement.getAttribute("name");
            String exceptionClassName = ehElement.getAttribute("exception");
            String exceptionResult = ehElement.getAttribute("result");
            Map<String, String> params = XmlHelper.getParams(ehElement);
            if (emName.isEmpty()) {
                emName = exceptionResult;
            }

            ExceptionMappingConfig ehConfig = new ExceptionMappingConfig.Builder(emName, exceptionClassName, exceptionResult)
                    .addParams(params)
                    .location(DomHelper.getLocationObject(ehElement))
                    .build();
            exceptionMappings.add(ehConfig);
        });

        return exceptionMappings;
    }

    protected Set<String> buildAllowedMethods(Element element, PackageConfig.Builder packageContext) {
        NodeList allowedMethodsEls = element.getElementsByTagName("allowed-methods");

        Set<String> allowedMethods;
        if (allowedMethodsEls.getLength() > 0) {
            // user defined 'allowed-methods' so used them whatever Strict DMI was enabled or not
            allowedMethods = new HashSet<>(packageContext.getGlobalAllowedMethods());
            // Fix for WW-5029 (concatenate all possible text node children)
            Node allowedMethodsNode = allowedMethodsEls.item(0);
            addAllowedMethodsToSet(allowedMethodsNode, allowedMethods);
        } else if (packageContext.isStrictMethodInvocation()) {
            // user enabled Strict DMI but didn't define action specific 'allowed-methods' so we use 'global-allowed-methods' only
            allowedMethods = new HashSet<>(packageContext.getGlobalAllowedMethods());
        } else {
            // Strict DMI is disabled so any method can be called
            allowedMethods = new HashSet<>();
            allowedMethods.add(ActionConfig.WILDCARD);
        }

        LOG.debug("Collected allowed methods: {}", allowedMethods);

        return Collections.unmodifiableSet(allowedMethods);
    }

    protected void loadDefaultActionRef(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("default-action-ref");

        if (resultTypeList.getLength() > 0) {
            Element defaultRefElement = (Element) resultTypeList.item(0);
            packageContext.defaultActionRef(defaultRefElement.getAttribute("name"));
        }
    }

    /**
     * Load all the global results for this package from the XML element.
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
            // Fix for WW-5029 (concatenate all possible text node children)
            Node globalAllowedMethodsNode = globalAllowedMethodsElms.item(0);
            addAllowedMethodsToSet(globalAllowedMethodsNode, globalAllowedMethods);
            packageContext.addGlobalAllowedMethods(globalAllowedMethods);
        }
    }

    protected static void addAllowedMethodsToSet(Node allowedMethodsNode, Set<String> allowedMethodsSet) {
        if (allowedMethodsNode == null) {
            return;
        }
        StringBuilder allowedMethodsSB = new StringBuilder();
        iterateChildren(allowedMethodsNode, allowedMethodsChildNode -> {
            if (allowedMethodsChildNode != null && allowedMethodsChildNode.getNodeType() == Node.TEXT_NODE) {
                String childNodeValue = allowedMethodsChildNode.getNodeValue();
                childNodeValue = (childNodeValue != null ? childNodeValue.trim() : "");
                if (!childNodeValue.isEmpty()) {
                    allowedMethodsSB.append(childNodeValue);
                }
            }
        });
        if (!allowedMethodsSB.isEmpty()) {
            allowedMethodsSet.addAll(commaDelimitedStringToSet(allowedMethodsSB.toString()));
        }
    }

    protected void loadDefaultClassRef(PackageConfig.Builder packageContext, Element element) {
        NodeList defaultClassRefList = element.getElementsByTagName("default-class-ref");
        if (defaultClassRefList.getLength() > 0) {
            Element defaultClassRefElement = (Element) defaultClassRefList.item(0);

            String className = defaultClassRefElement.getAttribute("class");
            Location location = DomHelper.getLocationObject(defaultClassRefElement);
            verifyAction(className, location);

            packageContext.defaultClassRef(className);
        }
    }

    /**
     * Load all the global results for this package from the XML element.
     *
     * @param packageContext the package context
     * @param packageElement the given XML element
     */
    protected void loadGlobalExceptionMappings(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalExceptionMappingList = packageElement.getElementsByTagName("global-exception-mappings");

        if (globalExceptionMappingList.getLength() > 0) {
            Element globalExceptionMappingElement = (Element) globalExceptionMappingList.item(0);
            List<ExceptionMappingConfig> exceptionMappings = buildExceptionMappings(globalExceptionMappingElement);
            packageContext.addGlobalExceptionMappingConfigs(exceptionMappings);
        }
    }

    protected List<InterceptorMapping> buildInterceptorList(Element element, PackageConfig.Builder context) throws ConfigurationException {
        List<InterceptorMapping> interceptorList = new ArrayList<>();

        iterateChildrenByTagName(element, "interceptor-ref", interceptorRefElement -> {
            Node parNode = interceptorRefElement.getParentNode();
            if (!parNode.equals(element) && !parNode.getNodeName().equals(element.getNodeName())) {
                return;
            }
            List<InterceptorMapping> interceptors = lookupInterceptorReference(context, interceptorRefElement);
            interceptorList.addAll(interceptors);
        });

        return interceptorList;
    }

    protected void loadInterceptors(PackageConfig.Builder context, Element element) throws ConfigurationException {
        iterateChildrenByTagName(
                element,
                "interceptor",
                interceptorElement -> {
                    String className = interceptorElement.getAttribute("class");
                    Location location = DomHelper.getLocationObject(interceptorElement);

                    verifyInterceptor(className, location);

                    context.addInterceptorConfig(buildInterceptorConfig(interceptorElement));
                });
        loadInterceptorStacks(element, context);
    }

    protected void verifyInterceptor(String className, Location loc) {
        try {
            allowAndLoadClass(className);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOG.warn("Interceptor class [{}] at location {} not found", className, loc);
            LOG.debug("Interceptor class [{}] not found", className, e);
        }
    }

    protected InterceptorConfig buildInterceptorConfig(Element interceptorElement) {
        String interceptorName = interceptorElement.getAttribute("name");
        String className = interceptorElement.getAttribute("class");

        Map<String, String> params = XmlHelper.getParams(interceptorElement);
        return new InterceptorConfig.Builder(interceptorName, className)
                .addParams(params)
                .location(DomHelper.getLocationObject(interceptorElement))
                .build();
    }

    protected void loadInterceptorStacks(Element element, PackageConfig.Builder context) throws ConfigurationException {
        iterateChildrenByTagName(element, "interceptor-stack", interceptorStackElement -> {
            InterceptorStackConfig config = loadInterceptorStack(interceptorStackElement, context);
            context.addInterceptorStackConfig(config);
        });
    }

    protected InterceptorStackConfig loadInterceptorStack(Element element, PackageConfig.Builder context) throws ConfigurationException {
        String name = element.getAttribute("name");
        InterceptorStackConfig.Builder config = new InterceptorStackConfig.Builder(name)
                .location(DomHelper.getLocationObject(element));

        iterateChildrenByTagName(element, "interceptor-ref", interceptorRefElement -> {
            List<InterceptorMapping> interceptors = lookupInterceptorReference(context, interceptorRefElement);
            config.addInterceptors(interceptors);
        });

        return config.build();
    }

    /**
     * Looks up the Interceptor Class from the interceptor-ref name and creates an instance, which is added to the
     * provided List, or, if this is a ref to a stack, it adds the Interceptor instances from the List to this stack.
     *
     * @param context               The PackageConfig to look up the interceptor from
     * @param interceptorRefElement Element to pull interceptor ref data from
     * @return A list of Interceptor objects
     * @throws ConfigurationException in case of configuration errors
     */
    protected List<InterceptorMapping> lookupInterceptorReference(PackageConfig.Builder context, Element interceptorRefElement) throws ConfigurationException {
        String refName = interceptorRefElement.getAttribute("name");
        Map<String, String> refParams = XmlHelper.getParams(interceptorRefElement);

        Location loc = LocationUtils.getLocation(interceptorRefElement);
        return InterceptorBuilder.constructInterceptorReference(context, refName, refParams, loc, objectFactory);
    }

    protected void loadDefaultInterceptorRef(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("default-interceptor-ref");

        if (resultTypeList.getLength() > 0) {
            Element defaultRefElement = (Element) resultTypeList.item(0);
            packageContext.defaultInterceptorRef(defaultRefElement.getAttribute("name"));
        }
    }
}
