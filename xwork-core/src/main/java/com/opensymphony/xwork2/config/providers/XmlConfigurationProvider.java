/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.opensymphony.xwork2.config.entities.*;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.impl.LocatableFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;


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

    private static final Logger LOG = LoggerFactory.getLogger(XmlConfigurationProvider.class);

    private List<Document> documents;
    private Set<String> includedFileNames;
    private String configFileName;
    private ObjectFactory objectFactory;

    private Set<String> loadedFileUrls = new HashSet<String>();
    private boolean errorIfMissing;
    private Map<String, String> dtdMappings;
    private Configuration configuration;
    private boolean throwExceptionOnDuplicateBeans = true;
    private Map<String, Element> declaredPackages = new HashMap<String, Element>();

    private FileManager fileManager;

    public XmlConfigurationProvider() {
        this("xwork.xml", true);
    }

    public XmlConfigurationProvider(String filename) {
        this(filename, true);
    }

    public XmlConfigurationProvider(String filename, boolean errorIfMissing) {
        this.configFileName = filename;
        this.errorIfMissing = errorIfMissing;

        Map<String, String> mappings = new HashMap<String, String>();
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

    /**
     * Returns an unmodifiable map of DTD mappings
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
        if (LOG.isInfoEnabled()) {
            LOG.info("Parsing configuration file [" + configFileName + "]");
        }
        Map<String, Node> loadedBeans = new HashMap<String, Node>();
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
                        if ("default".equals(scopeStr)) {
                            scope = Scope.DEFAULT;
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
                            Class cimpl = ClassLoaderUtil.loadClass(impl, getClass());
                            Class ctype = cimpl;
                            if (StringUtils.isNotEmpty(type)) {
                                ctype = ClassLoaderUtil.loadClass(type, getClass());
                            }
                            if ("true".equals(onlyStatic)) {
                                // Force loading of class to detect no class def found exceptions
                                cimpl.getDeclaredClasses();
                                containerBuilder.injectStatics(cimpl);
                            } else {
                                if (containerBuilder.contains(ctype, name)) {
                                    Location loc = LocationUtils.getLocation(loadedBeans.get(ctype.getName() + name));
                                    if (throwExceptionOnDuplicateBeans) {
                                        throw new ConfigurationException("Bean type " + ctype + " with the name " +
                                                name + " has already been loaded by " + loc, child);
                                    }
                                }

                                // Force loading of class to detect no class def found exceptions
                                cimpl.getDeclaredConstructors();

                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Loaded type:" + type + " name:" + name + " impl:" + impl);
                                }
                                containerBuilder.factory(ctype, name, new LocatableFactory(name, ctype, cimpl, scope, childNode), scope);
                            }
                            loadedBeans.put(ctype.getName() + name, child);
                        } catch (Throwable ex) {
                            if (!optional) {
                                throw new ConfigurationException("Unable to load bean: type:" + type + " class:" + impl, ex, childNode);
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Unable to load optional class: #0", impl);
                                }
                            }
                        }
                    } else if ("constant".equals(nodeName)) {
                        String name = child.getAttribute("name");
                        String value = child.getAttribute("value");
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
        DirectedGraph<String> graph = new DirectedGraph<String>();

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

        CycleDetector<String> detector = new CycleDetector<String>(graph);
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
            List<Element> result = new ArrayList<Element>();
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

            // Print out error messages for all misconfigured inheritence packages
            if (result.size() > 0) {
                for (Element rp : result) {
                    String parent = rp.getAttribute("extends");
                    if (parent != null) {
                        List<PackageConfig> parents = ConfigurationUtil.buildParentsFromString(configuration, parent);
                        if (parents != null && parents.size() <= 0) {
                            LOG.error("Unable to find parent packages " + parent);
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
        String methodName = actionElement.getAttribute("method");
        Location location = DomHelper.getLocationObject(actionElement);

        if (location == null) {
            if (LOG.isWarnEnabled()) {
            LOG.warn("location null for " + className);
            }
        }
        //methodName should be null if it's not set
        methodName = (methodName.trim().length() > 0) ? methodName.trim() : null;

        // if there isnt a class name specified for an <action/> then try to
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
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to verify action [#0] with class [#1], from [#2]", name, className, location);
                }
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
                .addAllowedMethod(allowedMethods)
                .location(location)
                .build();
        packageContext.addActionConfig(name, actionConfig);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Loaded " + (StringUtils.isNotEmpty(packageContext.getNamespace()) ? (packageContext.getNamespace() + "/") : "") + name + " in '" + packageContext.getName() + "' package:" + actionConfig);
        }
    }

    protected boolean verifyAction(String className, String name, Location loc) {
        if (className.indexOf('{') > -1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action class [" + className + "] contains a wildcard " +
                        "replacement value, so it can't be verified");
            }
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("Class not found for action [#0]", e, className);
            }
            throw new ConfigurationException("Action class [" + className + "] not found", loc);
        } catch (NoSuchMethodException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No constructor found for action [#0]", e, className);
            }
            throw new ConfigurationException("Action class [" + className + "] does not have a public no-arg constructor", e, loc);
        } catch (RuntimeException ex) {
            // Probably not a big deal, like request or session-scoped Spring 2 beans that need a real request
            if (LOG.isInfoEnabled()) {
                LOG.info("Unable to verify action class [#0] exists at initialization", className);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action verification cause", ex);
            }
        } catch (Exception ex) {
            // Default to failing fast
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to verify action class [#0]", ex, className);
            }
            throw new ConfigurationException(ex, loc);
        }
        return true;
    }

    /**
     * Create a PackageConfig from an XML element representing it.
     */
    protected PackageConfig addPackage(Element packageElement) throws ConfigurationException {
        String packageName = packageElement.getAttribute("name");
        PackageConfig packageConfig = configuration.getPackageConfig(packageName);
        if (packageConfig != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Package [#0] already loaded, skipping re-loading it and using existing PackageConfig [#1]", packageName, packageConfig);
            }
            return packageConfig;
        }

        PackageConfig.Builder newPackage = buildPackageContext(packageElement);

        if (newPackage.isNeedsRefresh()) {
            return newPackage.build();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Loaded " + newPackage);
        }

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

        // load the global exception handler list for this package
        loadGobalExceptionMappings(newPackage, packageElement);

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
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("The result type [#0] doesn't have a default param [DEFAULT_PARAM] defined!", t, className);
                    }
                }
                ResultTypeConfig.Builder resultType = new ResultTypeConfig.Builder(name, className).defaultResultParam(paramName)
                        .location(DomHelper.getLocationObject(resultTypeElement));

                Map<String, String> params = XmlHelper.getParams(resultTypeElement);

                if (!params.isEmpty()) {
                    resultType.addParams(params);
                }
                packageContext.addResultTypeConfig(resultType.build());

                // set the default result type
                if ("true".equals(def)) {
                    packageContext.defaultResultType(name);
                }
            }
        }
    }

    protected Class verifyResultType(String className, Location loc) {
        try {
            return objectFactory.getClassInstance(className);
        } catch (ClassNotFoundException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Result class [#0] doesn't exist (ClassNotFoundException) at #1, ignoring", e, className, loc.toString());
            }
        } catch (NoClassDefFoundError e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Result class [#0] doesn't exist (NoClassDefFoundError) at #1, ignoring", e, className, loc.toString());
            }
        }

        return null;
    }

    protected List<InterceptorMapping> buildInterceptorList(Element element, PackageConfig.Builder context) throws ConfigurationException {
        List<InterceptorMapping> interceptorList = new ArrayList<InterceptorMapping>();
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
     * This method builds a package context by looking for the parents of this new package.
     * <p/>
     * If no parents are found, it will return a root package.
     */
    protected PackageConfig.Builder buildPackageContext(Element packageElement) {
        String parent = packageElement.getAttribute("extends");
        String abstractVal = packageElement.getAttribute("abstract");
        boolean isAbstract = Boolean.parseBoolean(abstractVal);
        String name = StringUtils.defaultString(packageElement.getAttribute("name"));
        String namespace = StringUtils.defaultString(packageElement.getAttribute("namespace"));
        String strictDMIVal = StringUtils.defaultString(packageElement.getAttribute("strict-method-invocation"));
        boolean strictDMI = Boolean.parseBoolean(strictDMIVal);

        if (StringUtils.isNotEmpty(packageElement.getAttribute("externalReferenceResolver"))) {
            throw new ConfigurationException("The 'externalReferenceResolver' attribute has been removed.  Please use " +
                    "a custom ObjectFactory or Interceptor.", packageElement);
        }

        PackageConfig.Builder cfg = new PackageConfig.Builder(name)
                .namespace(namespace)
                .isAbstract(isAbstract)
                .strictMethodInvocation(strictDMI)
                .location(DomHelper.getLocationObject(packageElement));

        if (StringUtils.isNotEmpty(StringUtils.defaultString(parent))) { // has parents, let's look it up
            List<PackageConfig> parents = new ArrayList<PackageConfig>();
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
     */
    protected Map<String, ResultConfig> buildResults(Element element, PackageConfig.Builder packageContext) {
        NodeList resultEls = element.getElementsByTagName("result");

        Map<String, ResultConfig> results = new LinkedHashMap<String, ResultConfig>();

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
                        resultParams = new LinkedHashMap<String, String>();

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
                            if (LOG.isWarnEnabled()) {
                                LOG.warn("No default parameter defined for result [#0] of type [#1] ", config.getName(), config.getClassName());
                            }
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

                ResultConfig resultConfig = new ResultConfig.Builder(resultName, resultClass)
                        .addParams(params)
                        .location(DomHelper.getLocationObject(element))
                        .build();
                results.put(resultConfig.getName(), resultConfig);
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
     * Build a map of ResultConfig objects from below a given XML element.
     */
    protected List<ExceptionMappingConfig> buildExceptionMappings(Element element, PackageConfig.Builder packageContext) {
        NodeList exceptionMappingEls = element.getElementsByTagName("exception-mapping");

        List<ExceptionMappingConfig> exceptionMappings = new ArrayList<ExceptionMappingConfig>();

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

        Set<String> allowedMethods = null;

        if (allowedMethodsEls.getLength() > 0) {
            allowedMethods = new HashSet<String>();
            Node n = allowedMethodsEls.item(0).getFirstChild();
            if (n != null) {
                String s = n.getNodeValue().trim();
                if (s.length() > 0) {
                    allowedMethods = TextParseUtil.commaDelimitedStringToSet(s);
                }
            }
        } else if (packageContext.isStrictMethodInvocation()) {
            allowedMethods = new HashSet<String>();
        }

        return allowedMethods;
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
     */
    protected void loadGlobalResults(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalResultList = packageElement.getElementsByTagName("global-results");

        if (globalResultList.getLength() > 0) {
            Element globalResultElement = (Element) globalResultList.item(0);
            Map<String, ResultConfig> results = buildResults(globalResultElement, packageContext);
            packageContext.addGlobalResultConfigs(results);
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
     */
    protected void loadGobalExceptionMappings(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalExceptionMappingList = packageElement.getElementsByTagName("global-exception-mappings");

        if (globalExceptionMappingList.getLength() > 0) {
            Element globalExceptionMappingElement = (Element) globalExceptionMappingList.item(0);
            List<ExceptionMappingConfig> exceptionMappings = buildExceptionMappings(globalExceptionMappingElement, packageContext);
            packageContext.addGlobalExceptionMappingConfigs(exceptionMappings);
        }
    }

    //    protected void loadIncludes(Element rootElement, DocumentBuilder db) throws Exception {
    //        NodeList includeList = rootElement.getElementsByTagName("include");
    //
    //        for (int i = 0; i < includeList.getLength(); i++) {
    //            Element includeElement = (Element) includeList.item(i);
    //            String fileName = includeElement.getAttribute("file");
    //            includedFileNames.add(fileName);
    //            loadConfigurationFile(fileName, db);
    //        }
    //    }
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

    //    protected void loadPackages(Element rootElement) throws ConfigurationException {
    //        NodeList packageList = rootElement.getElementsByTagName("package");
    //
    //        for (int i = 0; i < packageList.getLength(); i++) {
    //            Element packageElement = (Element) packageList.item(i);
    //            addPackage(packageElement);
    //        }
    //    }
    private List<Document> loadConfigurationFiles(String fileName, Element includeElement) {
        List<Document> docs = new ArrayList<Document>();
        List<Document> finalDocs = new ArrayList<Document>();
        if (!includedFileNames.contains(fileName)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Loading action configurations from: " + fileName);
            }

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
                    if (LOG.isInfoEnabled()) {
                    LOG.info("Unable to locate configuration files of the name "
                            + fileName + ", skipping");
                    }
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

            if (LOG.isDebugEnabled()) {
                LOG.debug("Loaded action configuration from: " + fileName);
            }
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
     * @param interceptorRefElement Element to pull interceptor ref data from
     * @param context               The PackageConfig to lookup the interceptor from
     * @return A list of Interceptor objects
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
