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
package org.apache.struts2.config.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsConstants;

public class ConstantConfig {
    private Boolean devMode;
    private Boolean i18nReload;
    private String i18nEncoding;
    private Boolean configurationXmlReload;
    private List<String> actionExtension;
    private List<Pattern> actionExcludePattern;
    private Boolean tagAltSyntax;
    private Integer urlHttpPort;
    private Integer urlHttpsPort;
    private String urlIncludeParams;
    private BeanConfig urlRenderer;
    private BeanConfig objectFactory;
    private BeanConfig objectFactoryActionFactory;
    private BeanConfig objectFactoryResultFactory;
    private BeanConfig objectFactoryConverterFactory;
    private BeanConfig objectFactoryInterceptorFactory;
    private BeanConfig objectFactoryValidatorFactory;
    private BeanConfig objectFactoryUnknownHandlerFactory;
    private BeanConfig objectTypeDeterminer;
    private Locale locale;
    private Boolean dispatcherParametersWorkaround;
    private BeanConfig freemarkerManagerClassname;
    private String freemarkerTemplatesCacheUpdateDelay;
    private Boolean freemarkerBeanwrapperCache;
    private Integer freemarkerMruMaxStrongSize;
    private BeanConfig velocityManagerClassname;
    private String velocityConfigfile;
    private String velocityToolboxlocation;
    private List<String> velocityContexts;
    private String uiTemplateDir;
    private String uiTheme;
    private String uiThemeExpansionToken;
    private Long multipartMaxSize;
    private String multipartSaveDir;
    private Integer multipartBufferSize;
    private BeanConfig multipartParser;
    private Boolean multipartEnabled;
    private Pattern multipartValidationRegex;
    private String objectFactorySpringAutoWire;
    private Boolean objectFactorySpringAutoWireAlwaysRespect;
    private Boolean objectFactorySpringUseClassCache;
    private Boolean objectFactorySpringEnableAopSupport;
    private Boolean xsltNocache;
    private List<String> customProperties;
    private List<String> customI18nResources;
    private BeanConfig mapperClass;
    private List<String> mapperPrefixMapping;
    private Boolean serveStatic;
    private Boolean serveStaticBrowserCache;
    private Boolean enableDynamicMethodInvocation;
    private Boolean enableSlashesInActionNames;
    private List<String> mapperComposite;
    private BeanConfig actionProxyFactory;
    private Boolean freemarkerWrapperAltMap;
    private BeanConfig xworkConverter;
    private Boolean mapperAlwaysSelectFullNamespace;
    private BeanConfig xworkTextProvider;
    private BeanConfig localeProvider;
    private BeanConfig localeProviderFactory;
    private String mapperIdParameterName;
    private Boolean ognlAllowStaticMethodAccess;
    private BeanConfig actionValidatorManager;
    private BeanConfig valueStackFactory;
    private BeanConfig reflectionProvider;
    private BeanConfig reflectionContextFactory;
    private BeanConfig patternMatcher;
    private BeanConfig staticContentLoader;
    private BeanConfig unknownHandlerManager;
    private Boolean elThrowExceptionOnFailure;
    private Boolean ognlLogMissingProperties;
    private Boolean ognlEnableExpressionCache;
    private Boolean ognlEnableOGNLEvalExpression;
    private Boolean disableRequestAttributeValueStackLookup;
    private BeanConfig viewUrlHelper;
    private BeanConfig converterCollection;
    private BeanConfig converterArray;
    private BeanConfig converterDate;
    private BeanConfig converterNumber;
    private BeanConfig converterString;
    private Boolean handleException;
    private BeanConfig converterPropertiesProcessor;
    private BeanConfig converterFileProcessor;
    private BeanConfig converterAnnotationProcessor;
    private BeanConfig converterCreator;
    private BeanConfig ConverterHolder;
    private BeanConfig expressionParser;
    private Pattern allowedActionNames;
    private String defaultActionName;
    private Pattern allowedMethodNames;
    private String defaultMethodName;
    private Boolean mapperActionPrefixEnabled;
    private Boolean mapperActionPrefixCrossNamespaces;
    private String uiTemplateSuffix;
    private BeanConfig dispatcherErrorHandler;
    private Set<Class<?>> excludedClasses;
    private List<Pattern> excludedPackageNamePatterns;
    private Set<String> excludedPackageNames;
    private BeanConfig excludedPatternsChecker;
    private BeanConfig acceptedPatternsChecker;
    private Set<Pattern> overrideExcludedPatterns;
    private Set<Pattern> overrideAcceptedPatterns;
    private Set<Pattern> additionalExcludedPatterns;
    private Set<Pattern> additionalAcceptedPatterns;
    private BeanConfig contentTypeMatcher;
    private String strictMethodInvocationMethodRegex;
    private BeanConfig textProviderFactory;
    private BeanConfig localizedTextProvider;
    private Boolean disallowProxyMemberAccess;
    private Integer ognlAutoGrowthCollectionLimit;

    protected String beanConfToString(BeanConfig beanConf) {
        return beanConf == null ? null : beanConf.getName();
    }

    private String classesToString(Set<Class<?>> classes) {
        List<String> list = null;
        if (classes != null && !classes.isEmpty()) {
            list = new ArrayList<String>();
            for (Class<?> c : classes) {
                list.add(c.getName());
            }
        }
        return StringUtils.join(list, ',');
    }

    public Map<String, String> getAllAsStringsMap() {
        Map<String, String> map = new HashMap<>();

        map.put(StrutsConstants.STRUTS_DEVMODE, Objects.toString(devMode, null));
        map.put(StrutsConstants.STRUTS_I18N_RELOAD, Objects.toString(i18nReload, null));
        map.put(StrutsConstants.STRUTS_I18N_ENCODING, i18nEncoding);
        map.put(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, Objects.toString(configurationXmlReload, null));
        map.put(StrutsConstants.STRUTS_ACTION_EXTENSION, StringUtils.join(actionExtension, ','));
        map.put(StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN, StringUtils.join(actionExcludePattern, ','));
        map.put(StrutsConstants.STRUTS_TAG_ALTSYNTAX, Objects.toString(tagAltSyntax, null));
        map.put(StrutsConstants.STRUTS_URL_HTTP_PORT, Objects.toString(urlHttpPort, null));
        map.put(StrutsConstants.STRUTS_URL_HTTPS_PORT, Objects.toString(urlHttpsPort, null));
        map.put(StrutsConstants.STRUTS_URL_INCLUDEPARAMS, urlIncludeParams);
        map.put(StrutsConstants.STRUTS_URL_RENDERER, beanConfToString(urlRenderer));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY, beanConfToString(objectFactory));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_ACTIONFACTORY, beanConfToString(objectFactoryActionFactory));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_RESULTFACTORY, beanConfToString(objectFactoryResultFactory));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_CONVERTERFACTORY, beanConfToString(objectFactoryConverterFactory));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_INTERCEPTORFACTORY, beanConfToString(objectFactoryInterceptorFactory));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_VALIDATORFACTORY, beanConfToString(objectFactoryValidatorFactory));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_UNKNOWNHANDLERFACTORY, beanConfToString(objectFactoryUnknownHandlerFactory));
        map.put(StrutsConstants.STRUTS_OBJECTTYPEDETERMINER, beanConfToString(objectTypeDeterminer));
        map.put(StrutsConstants.STRUTS_LOCALE, locale == null ? null : locale.getLanguage());
        map.put(StrutsConstants.STRUTS_DISPATCHER_PARAMETERSWORKAROUND, Objects.toString(dispatcherParametersWorkaround, null));
        map.put(StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME, beanConfToString(freemarkerManagerClassname));
        map.put(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY, freemarkerTemplatesCacheUpdateDelay);
        map.put(StrutsConstants.STRUTS_FREEMARKER_BEANWRAPPER_CACHE, Objects.toString(freemarkerBeanwrapperCache, null));
        map.put(StrutsConstants.STRUTS_FREEMARKER_MRU_MAX_STRONG_SIZE, Objects.toString(freemarkerMruMaxStrongSize, null));
        map.put(StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME, beanConfToString(velocityManagerClassname));
        map.put(StrutsConstants.STRUTS_VELOCITY_CONFIGFILE, velocityConfigfile);
        map.put(StrutsConstants.STRUTS_VELOCITY_TOOLBOXLOCATION, velocityToolboxlocation);
        map.put(StrutsConstants.STRUTS_VELOCITY_CONTEXTS, StringUtils.join(velocityContexts, ','));
        map.put(StrutsConstants.STRUTS_UI_TEMPLATEDIR, uiTemplateDir);
        map.put(StrutsConstants.STRUTS_UI_THEME, uiTheme);
        map.put(StrutsConstants.STRUTS_UI_THEME_EXPANSION_TOKEN, uiThemeExpansionToken);
        map.put(StrutsConstants.STRUTS_MULTIPART_MAXSIZE, Objects.toString(multipartMaxSize, null));
        map.put(StrutsConstants.STRUTS_MULTIPART_SAVEDIR, multipartSaveDir);
        map.put(StrutsConstants.STRUTS_MULTIPART_BUFFERSIZE, Objects.toString(multipartBufferSize, null));
        map.put(StrutsConstants.STRUTS_MULTIPART_PARSER, beanConfToString(multipartParser));
        map.put(StrutsConstants.STRUTS_MULTIPART_ENABLED, Objects.toString(multipartEnabled, null));
        map.put(StrutsConstants.STRUTS_MULTIPART_VALIDATION_REGEX, Objects.toString(multipartValidationRegex, null));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE, objectFactorySpringAutoWire);
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE_ALWAYS_RESPECT, Objects.toString(objectFactorySpringAutoWireAlwaysRespect, null));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE, Objects.toString(objectFactorySpringUseClassCache, null));
        map.put(StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_ENABLE_AOP_SUPPORT, Objects.toString(objectFactorySpringEnableAopSupport, null));
        map.put(StrutsConstants.STRUTS_XSLT_NOCACHE, Objects.toString(xsltNocache, null));
        map.put(StrutsConstants.STRUTS_CUSTOM_PROPERTIES, StringUtils.join(customProperties, ','));
        map.put(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES, StringUtils.join(customI18nResources, ','));
        map.put(StrutsConstants.STRUTS_MAPPER_CLASS, beanConfToString(mapperClass));
        map.put(StrutsConstants.PREFIX_BASED_MAPPER_CONFIGURATION, StringUtils.join(mapperPrefixMapping, ','));
        map.put(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT, Objects.toString(serveStatic, null));
        map.put(StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE, Objects.toString(serveStaticBrowserCache, null));
        map.put(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION, Objects.toString(enableDynamicMethodInvocation, null));
        map.put(StrutsConstants.STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES, Objects.toString(enableSlashesInActionNames, null));
        map.put(StrutsConstants.STRUTS_MAPPER_COMPOSITE, StringUtils.join(mapperComposite, ','));
        map.put(StrutsConstants.STRUTS_ACTIONPROXYFACTORY, beanConfToString(actionProxyFactory));
        map.put(StrutsConstants.STRUTS_FREEMARKER_WRAPPER_ALT_MAP, Objects.toString(freemarkerWrapperAltMap, null));
        map.put(StrutsConstants.STRUTS_XWORKCONVERTER, beanConfToString(xworkConverter));
        map.put(StrutsConstants.STRUTS_ALWAYS_SELECT_FULL_NAMESPACE, Objects.toString(mapperAlwaysSelectFullNamespace, null));
        map.put(StrutsConstants.STRUTS_XWORKTEXTPROVIDER, beanConfToString(xworkTextProvider));
        map.put(StrutsConstants.STRUTS_LOCALE_PROVIDER, beanConfToString(localeProvider));
        map.put(StrutsConstants.STRUTS_LOCALE_PROVIDER_FACTORY, beanConfToString(localeProviderFactory));
        map.put(StrutsConstants.STRUTS_ID_PARAMETER_NAME, mapperIdParameterName);
        map.put(StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS, Objects.toString(ognlAllowStaticMethodAccess, null));
        map.put(StrutsConstants.STRUTS_ACTIONVALIDATORMANAGER, beanConfToString(actionValidatorManager));
        map.put(StrutsConstants.STRUTS_VALUESTACKFACTORY, beanConfToString(valueStackFactory));
        map.put(StrutsConstants.STRUTS_REFLECTIONPROVIDER, beanConfToString(reflectionProvider));
        map.put(StrutsConstants.STRUTS_REFLECTIONCONTEXTFACTORY, beanConfToString(reflectionContextFactory));
        map.put(StrutsConstants.STRUTS_PATTERNMATCHER, beanConfToString(patternMatcher));
        map.put(StrutsConstants.STRUTS_STATIC_CONTENT_LOADER, beanConfToString(staticContentLoader));
        map.put(StrutsConstants.STRUTS_UNKNOWN_HANDLER_MANAGER, beanConfToString(unknownHandlerManager));
        map.put(StrutsConstants.STRUTS_EL_THROW_EXCEPTION, Objects.toString(elThrowExceptionOnFailure, null));
        map.put(StrutsConstants.STRUTS_LOG_MISSING_PROPERTIES, Objects.toString(ognlLogMissingProperties, null));
        map.put(StrutsConstants.STRUTS_ENABLE_OGNL_EXPRESSION_CACHE, Objects.toString(ognlEnableExpressionCache, null));
        map.put(StrutsConstants.STRUTS_ENABLE_OGNL_EVAL_EXPRESSION, Objects.toString(ognlEnableOGNLEvalExpression, null));
        map.put(StrutsConstants.STRUTS_DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP, Objects.toString(disableRequestAttributeValueStackLookup, null));
        map.put(StrutsConstants.STRUTS_URL_HELPER, beanConfToString(viewUrlHelper));
        map.put(StrutsConstants.STRUTS_CONVERTER_COLLECTION, beanConfToString(converterCollection));
        map.put(StrutsConstants.STRUTS_CONVERTER_ARRAY, beanConfToString(converterArray));
        map.put(StrutsConstants.STRUTS_CONVERTER_DATE, beanConfToString(converterDate));
        map.put(StrutsConstants.STRUTS_CONVERTER_NUMBER, beanConfToString(converterNumber));
        map.put(StrutsConstants.STRUTS_CONVERTER_STRING, beanConfToString(converterString));
        map.put(StrutsConstants.STRUTS_HANDLE_EXCEPTION, Objects.toString(handleException, null));
        map.put(StrutsConstants.STRUTS_CONVERTER_PROPERTIES_PROCESSOR, beanConfToString(converterPropertiesProcessor));
        map.put(StrutsConstants.STRUTS_CONVERTER_FILE_PROCESSOR, beanConfToString(converterFileProcessor));
        map.put(StrutsConstants.STRUTS_CONVERTER_ANNOTATION_PROCESSOR, beanConfToString(converterAnnotationProcessor));
        map.put(StrutsConstants.STRUTS_CONVERTER_CREATOR, beanConfToString(converterCreator));
        map.put(StrutsConstants.STRUTS_CONVERTER_HOLDER, beanConfToString(ConverterHolder));
        map.put(StrutsConstants.STRUTS_EXPRESSION_PARSER, beanConfToString(expressionParser));
        map.put(StrutsConstants.STRUTS_ALLOWED_ACTION_NAMES, Objects.toString(allowedActionNames, null));
        map.put(StrutsConstants.STRUTS_DEFAULT_ACTION_NAME, defaultActionName);
        map.put(StrutsConstants.STRUTS_ALLOWED_METHOD_NAMES, Objects.toString(allowedMethodNames, null));
        map.put(StrutsConstants.STRUTS_DEFAULT_METHOD_NAME, defaultMethodName);
        map.put(StrutsConstants.STRUTS_MAPPER_ACTION_PREFIX_ENABLED, Objects.toString(mapperActionPrefixEnabled, null));
        map.put(StrutsConstants.STRUTS_MAPPER_ACTION_PREFIX_CROSSNAMESPACES, Objects.toString(mapperActionPrefixCrossNamespaces, null));
        map.put(StrutsConstants.DEFAULT_TEMPLATE_TYPE_CONFIG_KEY, uiTemplateSuffix);
        map.put(StrutsConstants.STRUTS_DISPATCHER_ERROR_HANDLER, beanConfToString(dispatcherErrorHandler));
        map.put(StrutsConstants.STRUTS_EXCLUDED_CLASSES, classesToString(excludedClasses));
        map.put(StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS, StringUtils.join(excludedPackageNamePatterns, ','));
        map.put(StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, StringUtils.join(excludedPackageNames, ','));
        map.put(StrutsConstants.STRUTS_EXCLUDED_PATTERNS_CHECKER, beanConfToString(excludedPatternsChecker));
        map.put(StrutsConstants.STRUTS_ACCEPTED_PATTERNS_CHECKER, beanConfToString(acceptedPatternsChecker));
        map.put(StrutsConstants.STRUTS_OVERRIDE_EXCLUDED_PATTERNS, StringUtils.join(overrideExcludedPatterns, ','));
        map.put(StrutsConstants.STRUTS_OVERRIDE_ACCEPTED_PATTERNS, StringUtils.join(overrideAcceptedPatterns, ','));
        map.put(StrutsConstants.STRUTS_ADDITIONAL_EXCLUDED_PATTERNS, StringUtils.join(additionalExcludedPatterns, ','));
        map.put(StrutsConstants.STRUTS_ADDITIONAL_ACCEPTED_PATTERNS, StringUtils.join(additionalAcceptedPatterns, ','));
        map.put(StrutsConstants.STRUTS_CONTENT_TYPE_MATCHER, beanConfToString(contentTypeMatcher));
        map.put(StrutsConstants.STRUTS_SMI_METHOD_REGEX, strictMethodInvocationMethodRegex);
        map.put(StrutsConstants.STRUTS_TEXT_PROVIDER_FACTORY, beanConfToString(textProviderFactory));
        map.put(StrutsConstants.STRUTS_LOCALIZED_TEXT_PROVIDER, beanConfToString(localizedTextProvider));
        map.put(StrutsConstants.STRUTS_DISALLOW_PROXY_MEMBER_ACCESS, Objects.toString(disallowProxyMemberAccess, null));
        map.put(StrutsConstants.STRUTS_OGNL_AUTO_GROWTH_COLLECTION_LIMIT, Objects.toString(ognlAutoGrowthCollectionLimit, null));

        return map;
    }

    public Boolean getDevMode() {
        return devMode;
    }

    public void setDevMode(Boolean devMode) {
        this.devMode = devMode;
    }

    public Boolean getI18nReload() {
        return i18nReload;
    }

    public void setI18nReload(Boolean i18nReload) {
        this.i18nReload = i18nReload;
    }

    public String getI18nEncoding() {
        return i18nEncoding;
    }

    public void setI18nEncoding(String i18nEncoding) {
        this.i18nEncoding = i18nEncoding;
    }

    public Boolean getConfigurationXmlReload() {
        return configurationXmlReload;
    }

    public void setConfigurationXmlReload(Boolean configurationXmlReload) {
        this.configurationXmlReload = configurationXmlReload;
    }

    public List<String> getActionExtension() {
        return actionExtension;
    }

    public void setActionExtension(List<String> actionExtension) {
        this.actionExtension = actionExtension;
    }

    public List<Pattern> getActionExcludePattern() {
        return actionExcludePattern;
    }

    public void setActionExcludePattern(List<Pattern> actionExcludePattern) {
        this.actionExcludePattern = actionExcludePattern;
    }

    public Boolean getTagAltSyntax() {
        return tagAltSyntax;
    }

    public void setTagAltSyntax(Boolean tagAltSyntax) {
        this.tagAltSyntax = tagAltSyntax;
    }

    public Integer getUrlHttpPort() {
        return urlHttpPort;
    }

    public void setUrlHttpPort(Integer urlHttpPort) {
        this.urlHttpPort = urlHttpPort;
    }

    public Integer getUrlHttpsPort() {
        return urlHttpsPort;
    }

    public void setUrlHttpsPort(Integer urlHttpsPort) {
        this.urlHttpsPort = urlHttpsPort;
    }

    public String getUrlIncludeParams() {
        return urlIncludeParams;
    }

    public void setUrlIncludeParams(String urlIncludeParams) {
        this.urlIncludeParams = urlIncludeParams;
    }

    public BeanConfig getUrlRenderer() {
        return urlRenderer;
    }

    public void setUrlRenderer(BeanConfig urlRenderer) {
        this.urlRenderer = urlRenderer;
    }

    public void setUrlRenderer(Class<?> clazz) {
        this.urlRenderer = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(BeanConfig objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setObjectFactory(Class<?> clazz) {
        this.objectFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryActionFactory() {
        return objectFactoryActionFactory;
    }

    public void setObjectFactoryActionFactory(BeanConfig objectFactoryActionFactory) {
        this.objectFactoryActionFactory = objectFactoryActionFactory;
    }

    public void setObjectFactoryActionFactory(Class<?> clazz) {
        this.objectFactoryActionFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryResultFactory() {
        return objectFactoryResultFactory;
    }

    public void setObjectFactoryResultFactory(BeanConfig objectFactoryResultFactory) {
        this.objectFactoryResultFactory = objectFactoryResultFactory;
    }

    public void setObjectFactoryResultFactory(Class<?> clazz) {
        this.objectFactoryResultFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryConverterFactory() {
        return objectFactoryConverterFactory;
    }

    public void setObjectFactoryConverterFactory(BeanConfig objectFactoryConverterFactory) {
        this.objectFactoryConverterFactory = objectFactoryConverterFactory;
    }

    public void setObjectFactoryConverterFactory(Class<?> clazz) {
        this.objectFactoryConverterFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryInterceptorFactory() {
        return objectFactoryInterceptorFactory;
    }

    public void setObjectFactoryInterceptorFactory(BeanConfig objectFactoryInterceptorFactory) {
        this.objectFactoryInterceptorFactory = objectFactoryInterceptorFactory;
    }

    public void setObjectFactoryInterceptorFactory(Class<?> clazz) {
        this.objectFactoryInterceptorFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryValidatorFactory() {
        return objectFactoryValidatorFactory;
    }

    public void setObjectFactoryValidatorFactory(BeanConfig objectFactoryValidatorFactory) {
        this.objectFactoryValidatorFactory = objectFactoryValidatorFactory;
    }

    public void setObjectFactoryValidatorFactory(Class<?> clazz) {
        this.objectFactoryValidatorFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryUnknownHandlerFactory() {
        return objectFactoryUnknownHandlerFactory;
    }

    public void setObjectFactoryUnknownHandlerFactory(BeanConfig objectFactoryUnknownHandlerFactory) {
        this.objectFactoryUnknownHandlerFactory = objectFactoryUnknownHandlerFactory;
    }

    public void setObjectFactoryUnknownHandlerFactory(Class<?> clazz) {
        this.objectFactoryUnknownHandlerFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectTypeDeterminer() {
        return objectTypeDeterminer;
    }

    public void setObjectTypeDeterminer(BeanConfig objectTypeDeterminer) {
        this.objectTypeDeterminer = objectTypeDeterminer;
    }

    public void setObjectTypeDeterminer(Class<?> clazz) {
        this.objectTypeDeterminer = new BeanConfig(clazz, clazz.getName());
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Boolean getDispatcherParametersWorkaround() {
        return dispatcherParametersWorkaround;
    }

    public void setDispatcherParametersWorkaround(Boolean dispatcherParametersWorkaround) {
        this.dispatcherParametersWorkaround = dispatcherParametersWorkaround;
    }

    public BeanConfig getFreemarkerManagerClassname() {
        return freemarkerManagerClassname;
    }

    public void setFreemarkerManagerClassname(BeanConfig freemarkerManagerClassname) {
        this.freemarkerManagerClassname = freemarkerManagerClassname;
    }

    public void setFreemarkerManagerClassname(Class<?> clazz) {
        this.freemarkerManagerClassname = new BeanConfig(clazz, clazz.getName());
    }

    public String getFreemarkerTemplatesCacheUpdateDelay() {
        return freemarkerTemplatesCacheUpdateDelay;
    }

    public void setFreemarkerTemplatesCacheUpdateDelay(String freemarkerTemplatesCacheUpdateDelay) {
        this.freemarkerTemplatesCacheUpdateDelay = freemarkerTemplatesCacheUpdateDelay;
    }

    public Boolean getFreemarkerBeanwrapperCache() {
        return freemarkerBeanwrapperCache;
    }

    public void setFreemarkerBeanwrapperCache(Boolean freemarkerBeanwrapperCache) {
        this.freemarkerBeanwrapperCache = freemarkerBeanwrapperCache;
    }

    public Integer getFreemarkerMruMaxStrongSize() {
        return freemarkerMruMaxStrongSize;
    }

    public void setFreemarkerMruMaxStrongSize(Integer freemarkerMruMaxStrongSize) {
        this.freemarkerMruMaxStrongSize = freemarkerMruMaxStrongSize;
    }

    public BeanConfig getVelocityManagerClassname() {
        return velocityManagerClassname;
    }

    public void setVelocityManagerClassname(BeanConfig velocityManagerClassname) {
        this.velocityManagerClassname = velocityManagerClassname;
    }

    public void setVelocityManagerClassname(Class<?> clazz) {
        this.velocityManagerClassname = new BeanConfig(clazz, clazz.getName());
    }

    public String getVelocityConfigfile() {
        return velocityConfigfile;
    }

    public void setVelocityConfigfile(String velocityConfigfile) {
        this.velocityConfigfile = velocityConfigfile;
    }

    public String getVelocityToolboxlocation() {
        return velocityToolboxlocation;
    }

    public void setVelocityToolboxlocation(String velocityToolboxlocation) {
        this.velocityToolboxlocation = velocityToolboxlocation;
    }

    public List<String> getVelocityContexts() {
        return velocityContexts;
    }

    public void setVelocityContexts(List<String> velocityContexts) {
        this.velocityContexts = velocityContexts;
    }

    public String getUiTemplateDir() {
        return uiTemplateDir;
    }

    public void setUiTemplateDir(String uiTemplateDir) {
        this.uiTemplateDir = uiTemplateDir;
    }

    public String getUiTheme() {
        return uiTheme;
    }

    public void setUiTheme(String uiTheme) {
        this.uiTheme = uiTheme;
    }

    public String getUiThemeExpansionToken() {
        return uiThemeExpansionToken;
    }

    public void setUiThemeExpansionToken(String uiThemeExpansionToken) {
        this.uiThemeExpansionToken = uiThemeExpansionToken;
    }

    public Long getMultipartMaxSize() {
        return multipartMaxSize;
    }

    public void setMultipartMaxSize(Long multipartMaxSize) {
        this.multipartMaxSize = multipartMaxSize;
    }

    public String getMultipartSaveDir() {
        return multipartSaveDir;
    }

    public void setMultipartSaveDir(String multipartSaveDir) {
        this.multipartSaveDir = multipartSaveDir;
    }

    public Integer getMultipartBufferSize() {
        return multipartBufferSize;
    }

    public void setMultipartBufferSize(Integer multipartBufferSize) {
        this.multipartBufferSize = multipartBufferSize;
    }

    public BeanConfig getMultipartParser() {
        return multipartParser;
    }

    public void setMultipartParser(BeanConfig multipartParser) {
        this.multipartParser = multipartParser;
    }

    public void setMultipartParser(Class<?> clazz) {
        this.multipartParser = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getMultipartEnabled() {
        return multipartEnabled;
    }

    public void setMultipartEnabled(Boolean multipartEnabled) {
        this.multipartEnabled = multipartEnabled;
    }

    public Pattern getMultipartValidationRegex() {
        return multipartValidationRegex;
    }

    public void setMultipartValidationRegex(Pattern multipartValidationRegex) {
        this.multipartValidationRegex = multipartValidationRegex;
    }

    public String getObjectFactorySpringAutoWire() {
        return objectFactorySpringAutoWire;
    }

    public void setObjectFactorySpringAutoWire(String objectFactorySpringAutoWire) {
        this.objectFactorySpringAutoWire = objectFactorySpringAutoWire;
    }

    public Boolean getObjectFactorySpringAutoWireAlwaysRespect() {
        return objectFactorySpringAutoWireAlwaysRespect;
    }

    public void setObjectFactorySpringAutoWireAlwaysRespect(Boolean objectFactorySpringAutoWireAlwaysRespect) {
        this.objectFactorySpringAutoWireAlwaysRespect = objectFactorySpringAutoWireAlwaysRespect;
    }

    public Boolean getObjectFactorySpringUseClassCache() {
        return objectFactorySpringUseClassCache;
    }

    public void setObjectFactorySpringUseClassCache(Boolean objectFactorySpringUseClassCache) {
        this.objectFactorySpringUseClassCache = objectFactorySpringUseClassCache;
    }

    public Boolean getObjectFactorySpringEnableAopSupport() {
        return objectFactorySpringEnableAopSupport;
    }

    public void setObjectFactorySpringEnableAopSupport(Boolean objectFactorySpringEnableAopSupport) {
        this.objectFactorySpringEnableAopSupport = objectFactorySpringEnableAopSupport;
    }

    public Boolean getXsltNocache() {
        return xsltNocache;
    }

    public void setXsltNocache(Boolean xsltNocache) {
        this.xsltNocache = xsltNocache;
    }

    public List<String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(List<String> customProperties) {
        this.customProperties = customProperties;
    }

    public List<String> getCustomI18nResources() {
        return customI18nResources;
    }

    public void setCustomI18nResources(List<String> customI18nResources) {
        this.customI18nResources = customI18nResources;
    }

    public BeanConfig getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(BeanConfig mapperClass) {
        this.mapperClass = mapperClass;
    }

    public void setMapperClass(Class<?> clazz) {
        this.mapperClass = new BeanConfig(clazz, clazz.getName());
    }

    public List<String> getMapperPrefixMapping() {
        return mapperPrefixMapping;
    }

    public void setMapperPrefixMapping(List<String> mapperPrefixMapping) {
        this.mapperPrefixMapping = mapperPrefixMapping;
    }

    public Boolean getServeStatic() {
        return serveStatic;
    }

    public void setServeStatic(Boolean serveStatic) {
        this.serveStatic = serveStatic;
    }

    public Boolean getServeStaticBrowserCache() {
        return serveStaticBrowserCache;
    }

    public void setServeStaticBrowserCache(Boolean serveStaticBrowserCache) {
        this.serveStaticBrowserCache = serveStaticBrowserCache;
    }

    public Boolean getEnableDynamicMethodInvocation() {
        return enableDynamicMethodInvocation;
    }

    public void setEnableDynamicMethodInvocation(Boolean enableDynamicMethodInvocation) {
        this.enableDynamicMethodInvocation = enableDynamicMethodInvocation;
    }

    public Boolean getEnableSlashesInActionNames() {
        return enableSlashesInActionNames;
    }

    public void setEnableSlashesInActionNames(Boolean enableSlashesInActionNames) {
        this.enableSlashesInActionNames = enableSlashesInActionNames;
    }

    public List<String> getMapperComposite() {
        return mapperComposite;
    }

    public void setMapperComposite(List<String> mapperComposite) {
        this.mapperComposite = mapperComposite;
    }

    public BeanConfig getActionProxyFactory() {
        return actionProxyFactory;
    }

    public void setActionProxyFactory(BeanConfig actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    public void setActionProxyFactory(Class<?> clazz) {
        this.actionProxyFactory = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getFreemarkerWrapperAltMap() {
        return freemarkerWrapperAltMap;
    }

    public void setFreemarkerWrapperAltMap(Boolean freemarkerWrapperAltMap) {
        this.freemarkerWrapperAltMap = freemarkerWrapperAltMap;
    }

    public BeanConfig getXworkConverter() {
        return xworkConverter;
    }

    public void setXworkConverter(BeanConfig xworkConverter) {
        this.xworkConverter = xworkConverter;
    }

    public void setXworkConverter(Class<?> clazz) {
        this.xworkConverter = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getMapperAlwaysSelectFullNamespace() {
        return mapperAlwaysSelectFullNamespace;
    }

    public void setMapperAlwaysSelectFullNamespace(Boolean mapperAlwaysSelectFullNamespace) {
        this.mapperAlwaysSelectFullNamespace = mapperAlwaysSelectFullNamespace;
    }

    public BeanConfig getXworkTextProvider() {
        return xworkTextProvider;
    }

    public void setXworkTextProvider(BeanConfig xworkTextProvider) {
        this.xworkTextProvider = xworkTextProvider;
    }

    public void setXworkTextProvider(Class<?> clazz) {
        this.xworkTextProvider = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getLocaleProvider() {
        return localeProvider;
    }

    public void setLocaleProvider(BeanConfig localeProvider) {
        this.localeProvider = localeProvider;
    }

    public void setLocaleProvider(Class<?> clazz) {
        this.localeProvider = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getLocaleProviderFactory() {
        return localeProviderFactory;
    }

    public void setLocaleProviderFactory(BeanConfig localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    public void setLocaleProviderFactory(Class<?> clazz) {
        this.localeProviderFactory = new BeanConfig(clazz, clazz.getName());
    }

    public String getMapperIdParameterName() {
        return mapperIdParameterName;
    }

    public void setMapperIdParameterName(String mapperIdParameterName) {
        this.mapperIdParameterName = mapperIdParameterName;
    }

    public Boolean getOgnlAllowStaticMethodAccess() {
        return ognlAllowStaticMethodAccess;
    }

    public void setOgnlAllowStaticMethodAccess(Boolean ognlAllowStaticMethodAccess) {
        this.ognlAllowStaticMethodAccess = ognlAllowStaticMethodAccess;
    }

    public BeanConfig getActionValidatorManager() {
        return actionValidatorManager;
    }

    public void setActionValidatorManager(BeanConfig actionValidatorManager) {
        this.actionValidatorManager = actionValidatorManager;
    }

    public void setActionValidatorManager(Class<?> clazz) {
        this.actionValidatorManager = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getValueStackFactory() {
        return valueStackFactory;
    }

    public void setValueStackFactory(BeanConfig valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    public void setValueStackFactory(Class<?> clazz) {
        this.valueStackFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getReflectionProvider() {
        return reflectionProvider;
    }

    public void setReflectionProvider(BeanConfig reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    public void setReflectionProvider(Class<?> clazz) {
        this.reflectionProvider = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getReflectionContextFactory() {
        return reflectionContextFactory;
    }

    public void setReflectionContextFactory(BeanConfig reflectionContextFactory) {
        this.reflectionContextFactory = reflectionContextFactory;
    }

    public void setReflectionContextFactory(Class<?> clazz) {
        this.reflectionContextFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getPatternMatcher() {
        return patternMatcher;
    }

    public void setPatternMatcher(BeanConfig patternMatcher) {
        this.patternMatcher = patternMatcher;
    }

    public void setPatternMatcher(Class<?> clazz) {
        this.patternMatcher = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getStaticContentLoader() {
        return staticContentLoader;
    }

    public void setStaticContentLoader(BeanConfig staticContentLoader) {
        this.staticContentLoader = staticContentLoader;
    }

    public void setStaticContentLoader(Class<?> clazz) {
        this.staticContentLoader = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getUnknownHandlerManager() {
        return unknownHandlerManager;
    }

    public void setUnknownHandlerManager(BeanConfig unknownHandlerManager) {
        this.unknownHandlerManager = unknownHandlerManager;
    }

    public void setUnknownHandlerManager(Class<?> clazz) {
        this.unknownHandlerManager = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getElThrowExceptionOnFailure() {
        return elThrowExceptionOnFailure;
    }

    public void setElThrowExceptionOnFailure(Boolean elThrowExceptionOnFailure) {
        this.elThrowExceptionOnFailure = elThrowExceptionOnFailure;
    }

    public Boolean getOgnlLogMissingProperties() {
        return ognlLogMissingProperties;
    }

    public void setOgnlLogMissingProperties(Boolean ognlLogMissingProperties) {
        this.ognlLogMissingProperties = ognlLogMissingProperties;
    }

    public Boolean getOgnlEnableExpressionCache() {
        return ognlEnableExpressionCache;
    }

    public void setOgnlEnableExpressionCache(Boolean ognlEnableExpressionCache) {
        this.ognlEnableExpressionCache = ognlEnableExpressionCache;
    }

    public Boolean getOgnlEnableOGNLEvalExpression() {
        return ognlEnableOGNLEvalExpression;
    }

    public void setOgnlEnableOGNLEvalExpression(Boolean ognlEnableOGNLEvalExpression) {
        this.ognlEnableOGNLEvalExpression = ognlEnableOGNLEvalExpression;
    }

    public Boolean getDisableRequestAttributeValueStackLookup() {
        return disableRequestAttributeValueStackLookup;
    }

    public void setDisableRequestAttributeValueStackLookup(Boolean disableRequestAttributeValueStackLookup) {
        this.disableRequestAttributeValueStackLookup = disableRequestAttributeValueStackLookup;
    }

    public BeanConfig getViewUrlHelper() {
        return viewUrlHelper;
    }

    public void setViewUrlHelper(BeanConfig viewUrlHelper) {
        this.viewUrlHelper = viewUrlHelper;
    }

    public void setViewUrlHelper(Class<?> clazz) {
        this.viewUrlHelper = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterCollection() {
        return converterCollection;
    }

    public void setConverterCollection(BeanConfig converterCollection) {
        this.converterCollection = converterCollection;
    }

    public void setConverterCollection(Class<?> clazz) {
        this.converterCollection = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterArray() {
        return converterArray;
    }

    public void setConverterArray(BeanConfig converterArray) {
        this.converterArray = converterArray;
    }

    public void setConverterArray(Class<?> clazz) {
        this.converterArray = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterDate() {
        return converterDate;
    }

    public void setConverterDate(BeanConfig converterDate) {
        this.converterDate = converterDate;
    }

    public void setConverterDate(Class<?> clazz) {
        this.converterDate = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterNumber() {
        return converterNumber;
    }

    public void setConverterNumber(BeanConfig converterNumber) {
        this.converterNumber = converterNumber;
    }

    public void setConverterNumber(Class<?> clazz) {
        this.converterNumber = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterString() {
        return converterString;
    }

    public void setConverterString(BeanConfig converterString) {
        this.converterString = converterString;
    }

    public void setConverterString(Class<?> clazz) {
        this.converterString = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getHandleException() {
        return handleException;
    }

    public void setHandleException(Boolean handleException) {
        this.handleException = handleException;
    }

    public BeanConfig getConverterPropertiesProcessor() {
        return converterPropertiesProcessor;
    }

    public void setConverterPropertiesProcessor(BeanConfig converterPropertiesProcessor) {
        this.converterPropertiesProcessor = converterPropertiesProcessor;
    }

    public void setConverterPropertiesProcessor(Class<?> clazz) {
        this.converterPropertiesProcessor = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterFileProcessor() {
        return converterFileProcessor;
    }

    public void setConverterFileProcessor(BeanConfig converterFileProcessor) {
        this.converterFileProcessor = converterFileProcessor;
    }

    public void setConverterFileProcessor(Class<?> clazz) {
        this.converterFileProcessor = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterAnnotationProcessor() {
        return converterAnnotationProcessor;
    }

    public void setConverterAnnotationProcessor(BeanConfig converterAnnotationProcessor) {
        this.converterAnnotationProcessor = converterAnnotationProcessor;
    }

    public void setConverterAnnotationProcessor(Class<?> clazz) {
        this.converterAnnotationProcessor = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterCreator() {
        return converterCreator;
    }

    public void setConverterCreator(BeanConfig converterCreator) {
        this.converterCreator = converterCreator;
    }

    public void setConverterCreator(Class<?> clazz) {
        this.converterCreator = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterHolder() {
        return ConverterHolder;
    }

    public void setConverterHolder(BeanConfig ConverterHolder) {
        this.ConverterHolder = ConverterHolder;
    }

    public void setConverterHolder(Class<?> clazz) {
        this.ConverterHolder = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getExpressionParser() {
        return expressionParser;
    }

    public void setExpressionParser(BeanConfig expressionParser) {
        this.expressionParser = expressionParser;
    }

    public void setExpressionParser(Class<?> clazz) {
        this.expressionParser = new BeanConfig(clazz, clazz.getName());
    }

    public Pattern getAllowedActionNames() {
        return allowedActionNames;
    }

    public void setAllowedActionNames(Pattern allowedActionNames) {
        this.allowedActionNames = allowedActionNames;
    }

    public String getDefaultActionName() {
        return defaultActionName;
    }

    public void setDefaultActionName(String defaultActionName) {
        this.defaultActionName = defaultActionName;
    }

    public Pattern getAllowedMethodNames() {
        return allowedMethodNames;
    }

    public void setAllowedMethodNames(Pattern allowedMethodNames) {
        this.allowedMethodNames = allowedMethodNames;
    }

    public String getDefaultMethodName() {
        return defaultMethodName;
    }

    public void setDefaultMethodName(String defaultMethodName) {
        this.defaultMethodName = defaultMethodName;
    }

    public Boolean getMapperActionPrefixEnabled() {
        return mapperActionPrefixEnabled;
    }

    public void setMapperActionPrefixEnabled(Boolean mapperActionPrefixEnabled) {
        this.mapperActionPrefixEnabled = mapperActionPrefixEnabled;
    }

    public Boolean getMapperActionPrefixCrossNamespaces() {
        return mapperActionPrefixCrossNamespaces;
    }

    public void setMapperActionPrefixCrossNamespaces(Boolean mapperActionPrefixCrossNamespaces) {
        this.mapperActionPrefixCrossNamespaces = mapperActionPrefixCrossNamespaces;
    }

    public String getUiTemplateSuffix() {
        return uiTemplateSuffix;
    }

    public void setUiTemplateSuffix(String uiTemplateSuffix) {
        this.uiTemplateSuffix = uiTemplateSuffix;
    }

    public BeanConfig getDispatcherErrorHandler() {
        return dispatcherErrorHandler;
    }

    public void setDispatcherErrorHandler(BeanConfig dispatcherErrorHandler) {
        this.dispatcherErrorHandler = dispatcherErrorHandler;
    }

    public void setDispatcherErrorHandler(Class<?> clazz) {
        this.dispatcherErrorHandler = new BeanConfig(clazz, clazz.getName());
    }

    public Set<Class<?>> getExcludedClasses() {
        return excludedClasses;
    }

    public void setExcludedClasses(Set<Class<?>> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }

    public List<Pattern> getExcludedPackageNamePatterns() {
        return excludedPackageNamePatterns;
    }

    public void setExcludedPackageNamePatterns(List<Pattern> excludedPackageNamePatterns) {
        this.excludedPackageNamePatterns = excludedPackageNamePatterns;
    }

    public Set<String> getExcludedPackageNames() {
        return excludedPackageNames;
    }

    public void setExcludedPackageNames(Set<String> excludedPackageNames) {
        this.excludedPackageNames = excludedPackageNames;
    }

    public BeanConfig getExcludedPatternsChecker() {
        return excludedPatternsChecker;
    }

    public void setExcludedPatternsChecker(BeanConfig excludedPatternsChecker) {
        this.excludedPatternsChecker = excludedPatternsChecker;
    }

    public void setExcludedPatternsChecker(Class<?> clazz) {
        this.excludedPatternsChecker = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getAcceptedPatternsChecker() {
        return acceptedPatternsChecker;
    }

    public void setAcceptedPatternsChecker(BeanConfig acceptedPatternsChecker) {
        this.acceptedPatternsChecker = acceptedPatternsChecker;
    }

    public void setAcceptedPatternsChecker(Class<?> clazz) {
        this.acceptedPatternsChecker = new BeanConfig(clazz, clazz.getName());
    }

    public Set<Pattern> getOverrideExcludedPatterns() {
        return overrideExcludedPatterns;
    }

    public void setOverrideExcludedPatterns(Set<Pattern> overrideExcludedPatterns) {
        this.overrideExcludedPatterns = overrideExcludedPatterns;
    }

    public Set<Pattern> getOverrideAcceptedPatterns() {
        return overrideAcceptedPatterns;
    }

    public void setOverrideAcceptedPatterns(Set<Pattern> overrideAcceptedPatterns) {
        this.overrideAcceptedPatterns = overrideAcceptedPatterns;
    }

    public Set<Pattern> getAdditionalExcludedPatterns() {
        return additionalExcludedPatterns;
    }

    public void setAdditionalExcludedPatterns(Set<Pattern> additionalExcludedPatterns) {
        this.additionalExcludedPatterns = additionalExcludedPatterns;
    }

    public Set<Pattern> getAdditionalAcceptedPatterns() {
        return additionalAcceptedPatterns;
    }

    public void setAdditionalAcceptedPatterns(Set<Pattern> additionalAcceptedPatterns) {
        this.additionalAcceptedPatterns = additionalAcceptedPatterns;
    }

    public BeanConfig getContentTypeMatcher() {
        return contentTypeMatcher;
    }

    public void setContentTypeMatcher(BeanConfig contentTypeMatcher) {
        this.contentTypeMatcher = contentTypeMatcher;
    }

    public void setContentTypeMatcher(Class<?> clazz) {
        this.contentTypeMatcher = new BeanConfig(clazz, clazz.getName());
    }

    public String getStrictMethodInvocationMethodRegex() {
        return strictMethodInvocationMethodRegex;
    }

    public void setStrictMethodInvocationMethodRegex(String strictMethodInvocationMethodRegex) {
        this.strictMethodInvocationMethodRegex = strictMethodInvocationMethodRegex;
    }

    public BeanConfig getTextProviderFactory() {
        return textProviderFactory;
    }

    public void setTextProviderFactory(BeanConfig textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    public void setTextProviderFactory(Class<?> clazz) {
        this.textProviderFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getLocalizedTextProvider() {
        return localizedTextProvider;
    }

    public void setLocalizedTextProvider(BeanConfig localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    public void setLocalizedTextProvider(Class<?> clazz) {
        this.localizedTextProvider = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getDisallowProxyMemberAccess() {
        return disallowProxyMemberAccess;
    }

    public void setDisallowProxyMemberAccess(Boolean disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = disallowProxyMemberAccess;
    }

    public Integer getOgnlAutoGrowthCollectionLimit() {
        return ognlAutoGrowthCollectionLimit;
    }

    public void setOgnlAutoGrowthCollectionLimit(Integer ognlAutoGrowthCollectionLimit) {
        this.ognlAutoGrowthCollectionLimit = ognlAutoGrowthCollectionLimit;
    }
}
