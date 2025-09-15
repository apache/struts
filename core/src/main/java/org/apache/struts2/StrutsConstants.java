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
package org.apache.struts2;

import org.apache.struts2.components.date.DateFormatter;
import org.apache.struts2.dispatcher.mapper.CompositeActionMapper;

/**
 * This class provides a central location for framework configuration keys
 * used to retrieve and store Struts configuration settings.
 */
public final class StrutsConstants {

    /** Whether Struts is in development mode or not */
    public static final String STRUTS_DEVMODE = "struts.devMode";

    /** Whether the localization messages should automatically be reloaded */
    public static final String STRUTS_I18N_RELOAD = "struts.i18n.reload";

    /** The encoding to use for localization messages */
    public static final String STRUTS_I18N_ENCODING = "struts.i18n.encoding";

    /**
     * Whether the default bundles should be searched for messages first.  Can be used to modify the
     * standard processing order for message lookup in TextProvider implementations.
     * <p>
     * Note: This control flag may not be meaningful to all provider implementations, and should be false by default.
     * </p>
     *
     * @since 6.0.0
     */
    public static final String STRUTS_I18N_SEARCH_DEFAULTBUNDLES_FIRST = "struts.i18n.search.defaultbundles.first";

    /** Whether to reload the XML configuration or not */
    public static final String STRUTS_CONFIGURATION_XML_RELOAD = "struts.configuration.xml.reload";

    /** The URL extension to use to determine if the request is meant for a Struts action */
    public static final String STRUTS_ACTION_EXTENSION = "struts.action.extension";

    /** Comma separated list of patterns (java.util.regex.Pattern) to be excluded from Struts2-processing */
    public static final String STRUTS_ACTION_EXCLUDE_PATTERN = "struts.action.excludePattern";

    /** A custom separator used to split list of patterns (java.util.regex.Pattern) to be excluded from Struts2-processing */
    public static final String STRUTS_ACTION_EXCLUDE_PATTERN_SEPARATOR = "struts.action.excludePattern.separator";

    /** Whether to use the response encoding (JSP page encoding) for s:include tag processing (false - use STRUTS_I18N_ENCODING - by default) */
    public static final String STRUTS_TAG_INCLUDETAG_USERESPONSEENCODING = "struts.tag.includetag.useResponseEncoding";

    /** The HTTP port used by Struts URLs */
    public static final String STRUTS_URL_HTTP_PORT = "struts.url.http.port";

    /** The HTTPS port used by Struts URLs */
    public static final String STRUTS_URL_HTTPS_PORT = "struts.url.https.port";

    /** The default includeParams method to generate Struts URLs */
    public static final String STRUTS_URL_INCLUDEPARAMS = "struts.url.includeParams";

    public static final String STRUTS_URL_RENDERER = "struts.urlRenderer";

    /** The com.opensymphony.xwork2.ObjectFactory implementation class */
    public static final String STRUTS_OBJECTFACTORY = "struts.objectFactory";
    public static final String STRUTS_OBJECTFACTORY_ACTIONFACTORY = "struts.objectFactory.actionFactory";
    public static final String STRUTS_OBJECTFACTORY_RESULTFACTORY = "struts.objectFactory.resultFactory";
    public static final String STRUTS_OBJECTFACTORY_CONVERTERFACTORY = "struts.objectFactory.converterFactory";
    public static final String STRUTS_OBJECTFACTORY_INTERCEPTORFACTORY = "struts.objectFactory.interceptorFactory";
    public static final String STRUTS_OBJECTFACTORY_VALIDATORFACTORY = "struts.objectFactory.validatorFactory";
    public static final String STRUTS_OBJECTFACTORY_UNKNOWNHANDLERFACTORY = "struts.objectFactory.unknownHandlerFactory";

    /** The com.opensymphony.xwork2.util.FileManager implementation class */
    public static final String STRUTS_FILE_MANAGER_FACTORY = "struts.fileManagerFactory";

    /** The com.opensymphony.xwork2.util.fs.FileManager implementation class */
    public static final String STRUTS_FILE_MANAGER = "struts.fileManager";

    /** The com.opensymphony.xwork2.util.ObjectTypeDeterminer implementation class */
    public static final String STRUTS_OBJECTTYPEDETERMINER = "struts.objectTypeDeterminer";

    /** The package containing actions that use Rife continuations */
    public static final String STRUTS_CONTINUATIONS_PACKAGE = "struts.continuations.package";

    /** The org.apache.struts2.config.Configuration implementation class */
    public static final String STRUTS_CONFIGURATION = "struts.configuration";

    /** The default locale for the Struts application */
    public static final String STRUTS_LOCALE = "struts.locale";

    /** Whether to use a Servlet request parameter workaround necessary for some versions of WebLogic */
    public static final String STRUTS_DISPATCHER_PARAMETERSWORKAROUND = "struts.dispatcher.parametersWorkaround";

    /** The org.apache.struts2.views.freemarker.FreemarkerManager implementation class */
    public static final String STRUTS_FREEMARKER_MANAGER_CLASSNAME = "struts.freemarker.manager.classname";

    /** Update freemarker templates cache in seconds */
    public static final String STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY = "struts.freemarker.templatesCache.updateDelay";

    /** Cache model instances at BeanWrapper level */
    public static final String STRUTS_FREEMARKER_BEANWRAPPER_CACHE = "struts.freemarker.beanwrapperCache";

    /** Maximum strong sizing for MruCacheStorage for freemarker */
    public static final String STRUTS_FREEMARKER_MRU_MAX_STRONG_SIZE = "struts.freemarker.mru.max.strong.size";

    /** The Velocity configuration file path */
    public static final String STRUTS_VELOCITY_CONFIGFILE = "struts.velocity.configfile";

    /** The location of the Velocity toolbox */
    public static final String STRUTS_VELOCITY_TOOLBOXLOCATION = "struts.velocity.toolboxlocation";

    /** List of Velocity context names */
    public static final String STRUTS_VELOCITY_CONTEXTS = "struts.velocity.contexts";

    /** The directory containing UI templates.  All templates must reside in this directory. */
    public static final String STRUTS_UI_TEMPLATEDIR = "struts.ui.templateDir";

    /** The default UI template theme */
    public static final String STRUTS_UI_THEME = "struts.ui.theme";

    /** Token to use to indicate start of theme to be expanded. */
    public static final String STRUTS_UI_THEME_EXPANSION_TOKEN = "struts.ui.theme.expansion.token";

    /** A path to static content, by default and from historical point of view it's /static. */
    public static final String STRUTS_UI_STATIC_CONTENT_PATH = "struts.ui.staticContentPath";

    /** A global flag to enable/disable html body escaping in tags, can be overwritten per tag */
    public static final String STRUTS_UI_ESCAPE_HTML_BODY = "struts.ui.escapeHtmlBody";

    /** The maximum size of a multipart request (file upload) */
    public static final String STRUTS_MULTIPART_MAXSIZE = "struts.multipart.maxSize";

    /** The maximum number of files allowed in a multipart request */
    public static final String STRUTS_MULTIPART_MAXFILES = "struts.multipart.maxFiles";

    /** The maximum length of a string parameter in a multipart request. */
    public static final String STRUTS_MULTIPART_MAX_STRING_LENGTH = "struts.multipart.maxStringLength";

    /** The maximum size per file in a multipart request */
    public static final String STRUTS_MULTIPART_MAXFILESIZE = "struts.multipart.maxFileSize";
    /** The directory to use for storing uploaded files */
    public static final String STRUTS_MULTIPART_SAVEDIR = "struts.multipart.saveDir";

    /** Declares the buffer size to be used during streaming multipart content to disk. Used only with {@link org.apache.struts2.dispatcher.multipart.JakartaStreamMultiPartRequest} */
    public static final String STRUTS_MULTIPART_BUFFERSIZE = "struts.multipart.bufferSize";

    /**
     * The org.apache.struts2.dispatcher.multipart.MultiPartRequest parser implementation
     * for a multipart request (file upload)
     */
    public static final String STRUTS_MULTIPART_PARSER = "struts.multipart.parser";

    /**
     * A global switch to disable support for multipart requests
     */
    public static final String STRUTS_MULTIPART_ENABLED = "struts.multipart.enabled";

    public static final String STRUTS_MULTIPART_VALIDATION_REGEX = "struts.multipart.validationRegex";

    /** How Spring should autowire.  Valid values are 'name', 'type', 'auto', and 'constructor' */
    public static final String STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE = "struts.objectFactory.spring.autoWire";

    /** Whether the autowire strategy chosen by STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE is always respected.  Defaults
     * to false, which is the legacy behavior that tries to determine the best strategy for the situation.
     * @since 2.1.3
     */
    public static final String STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE_ALWAYS_RESPECT = "struts.objectFactory.spring.autoWire.alwaysRespect";

    /** Whether Spring should use its class cache or not */
    public static final String STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE = "struts.objectFactory.spring.useClassCache";

    /** Uses different logic to construct beans, see https://issues.apache.org/jira/browse/WW-4110 */
    @Deprecated
    public static final String STRUTS_OBJECTFACTORY_SPRING_ENABLE_AOP_SUPPORT = "struts.objectFactory.spring.enableAopSupport";

    /** Location of additional configuration properties files to load */
    public static final String STRUTS_CUSTOM_PROPERTIES = "struts.custom.properties";

    /** Location of additional localization properties files to load */
    public static final String STRUTS_CUSTOM_I18N_RESOURCES = "struts.custom.i18n.resources";

    /** A name of a bean implementing org.apache.struts2.dispatcher.mapper.ActionMapper interface */
    public static final String STRUTS_MAPPER_CLASS = "struts.mapper.class";

    /**
     * A prefix based action mapper that is capable of delegating to other
     * {@link org.apache.struts2.dispatcher.mapper.ActionMapper}s based on the request's prefix
     * You can specify different prefixes that will be handled by different mappers
     */
    public static final String PREFIX_BASED_MAPPER_CONFIGURATION = "struts.mapper.prefixMapping";

    /** Whether the Struts filter should serve static content or not */
    public static final String STRUTS_SERVE_STATIC_CONTENT = "struts.serve.static";

    /** If static content served by the Struts filter should set browser caching header properties or not */
    public static final String STRUTS_SERVE_STATIC_BROWSER_CACHE = "struts.serve.static.browserCache";

    /** Allows one to disable dynamic method invocation from the URL */
    public static final String STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION = "struts.enable.DynamicMethodInvocation";

    /** Whether slashes in action names are allowed or not */
    public static final String STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES = "struts.enable.SlashesInActionNames";

    /** Prefix used by {@link CompositeActionMapper} to identify its containing {@link org.apache.struts2.dispatcher.mapper.ActionMapper} class. */
    public static final String STRUTS_MAPPER_COMPOSITE = "struts.mapper.composite";

    public static final String STRUTS_ACTIONPROXYFACTORY = "struts.actionProxyFactory";

    public static final String STRUTS_FREEMARKER_WRAPPER_ALT_MAP = "struts.freemarker.wrapper.altMap";

    /** Extension point for the Struts CompoundRootAccessor */
    public static final String STRUTS_COMPOUND_ROOT_ACCESSOR = "struts.compoundRootAccessor";

    /** Extension point for the Struts MethodAccessor */
    public static final String STRUTS_METHOD_ACCESSOR = "struts.methodAccessor";

    /** The name of the xwork converter implementation */
    public static final String STRUTS_XWORKCONVERTER = "struts.xworkConverter";

    public static final String STRUTS_ALWAYS_SELECT_FULL_NAMESPACE = "struts.mapper.alwaysSelectFullNamespace";
    /** Fallback to empty namespace when request namespace didn't match any in action configuration */
    public static final String STRUTS_ACTION_CONFIG_FALLBACK_TO_EMPTY_NAMESPACE = "struts.actionConfig.fallbackToEmptyNamespace";

    /** The {@link com.opensymphony.xwork2.LocaleProviderFactory} implementation class */
    public static final String STRUTS_LOCALE_PROVIDER_FACTORY = "struts.localeProviderFactory";

    /** The name of the parameter to create when mapping an id (used by some action mappers) */
    public static final String STRUTS_ID_PARAMETER_NAME = "struts.mapper.idParameterName";

    /** The name of the parameter to determine whether static field access will be allowed in OGNL expressions or not */
    public static final String STRUTS_ALLOW_STATIC_FIELD_ACCESS = "struts.ognl.allowStaticFieldAccess";

    public static final String STRUTS_DISALLOW_CUSTOM_OGNL_MAP = "struts.ognl.disallowCustomOgnlMap";

    public static final String STRUTS_MEMBER_ACCESS = "struts.securityMemberAccess";

    public static final String STRUTS_OGNL_GUARD = "struts.ognlGuard";

    /** The com.opensymphony.xwork2.validator.ActionValidatorManager implementation class */
    public static final String STRUTS_ACTIONVALIDATORMANAGER = "struts.actionValidatorManager";

    /** The {@link com.opensymphony.xwork2.util.ValueStackFactory} implementation class */
    public static final String STRUTS_VALUESTACKFACTORY = "struts.valueStackFactory";

    /** The {@link com.opensymphony.xwork2.util.reflection.ReflectionProvider} implementation class */
    public static final String STRUTS_REFLECTIONPROVIDER = "struts.reflectionProvider";

    /** The {@link com.opensymphony.xwork2.util.reflection.ReflectionContextFactory} implementation class */
    public static final String STRUTS_REFLECTIONCONTEXTFACTORY = "struts.reflectionContextFactory";

    /** The {@link com.opensymphony.xwork2.util.PatternMatcher} implementation class */
    public static final String STRUTS_PATTERNMATCHER = "struts.patternMatcher";

    /** The {@link org.apache.struts2.dispatcher.StaticContentLoader} implementation class */
    public static final String STRUTS_STATIC_CONTENT_LOADER = "struts.staticContentLoader";

    /** The {@link com.opensymphony.xwork2.UnknownHandlerManager} implementation class */
    public static final String STRUTS_UNKNOWN_HANDLER_MANAGER = "struts.unknownHandlerManager";

    /** Throw RuntimeException when a property is not found, or the evaluation of the expression fails */
    public static final String STRUTS_EL_THROW_EXCEPTION = "struts.el.throwExceptionOnFailure";

    /**
     * Specifies an OGNL expression cache factory implementation.  A default implementation is provided, but
     * could be replaced by a custom one if desired.
     *
     * @since 6.0.0
     */
    public static final String STRUTS_OGNL_EXPRESSION_CACHE_FACTORY = "struts.ognl.expressionCacheFactory";

    /**
     * Specifies an OGNL BeanInfo cache factory implementation.  A default implementation is provided, but
     * could be replaced by a custom one if desired.
     *
     * @since 6.0.0
     */
    public static final String STRUTS_OGNL_BEANINFO_CACHE_FACTORY = "struts.ognl.beanInfoCacheFactory";

    /**
     * Specifies the type of cache to use for BeanInfo objects.
     * @since 6.4.0
     * @see StrutsConstants#STRUTS_OGNL_EXPRESSION_CACHE_TYPE
     */
    public static final String STRUTS_OGNL_BEANINFO_CACHE_TYPE = "struts.ognl.beanInfoCacheType";

    /**
     * Specifies the maximum cache size for BeanInfo objects. This should be configured based on the cache type chosen
     * and application-specific needs.
     *
     * @since 6.0.0
     */
    public static final String STRUTS_OGNL_BEANINFO_CACHE_MAXSIZE = "struts.ognl.beanInfoCacheMaxSize";

    /**
     * @since 6.0.0
     * @deprecated since 6.4.0, use {@link StrutsConstants#STRUTS_OGNL_BEANINFO_CACHE_TYPE} instead.
     */
    @Deprecated
    public static final String STRUTS_OGNL_BEANINFO_CACHE_LRU_MODE = "struts.ognl.beanInfoCacheLRUMode";

    /**
     * Logs properties that are not found (very verbose)
     * @since 6.0.0
     */
    public static final String STRUTS_OGNL_LOG_MISSING_PROPERTIES = "struts.ognl.logMissingProperties";

    /**
     * Determines whether lookups on the ValueStack should fallback to looking in the context if the OGNL expression
     * fails or returns null.
     *
     * @since 6.4.0
     */
    public static final String STRUTS_OGNL_VALUE_STACK_FALLBACK_TO_CONTEXT = "struts.ognl.valueStackFallbackToContext";

    /**
     * Logs properties that are not found (very verbose)
     * @deprecated as of 6.0.0.  Use {@link #STRUTS_OGNL_LOG_MISSING_PROPERTIES} instead.
     */
    @Deprecated
    public static final String STRUTS_LOG_MISSING_PROPERTIES = STRUTS_OGNL_LOG_MISSING_PROPERTIES;

    /**
     * Enables caching of parsed OGNL expressions
     * @since 6.0.0
     */
    public static final String STRUTS_OGNL_ENABLE_EXPRESSION_CACHE = "struts.ognl.enableExpressionCache";

    /**
     * Enables caching of parsed OGNL expressions
     * @deprecated as of 6.0.0.  Use {@link #STRUTS_OGNL_ENABLE_EXPRESSION_CACHE} instead.
     */
    public static final String STRUTS_ENABLE_OGNL_EXPRESSION_CACHE = STRUTS_OGNL_ENABLE_EXPRESSION_CACHE;

    /**
     * Specifies the type of cache to use for parsed OGNL expressions. Valid values defined in
     * {@link com.opensymphony.xwork2.ognl.OgnlCacheFactory.CacheType}.
     * <ul>
     *     <li>For the W-TinyLfu cache, the eviction policy is detailed
     *     <a href="https://github.com/ben-manes/caffeine/wiki/Efficiency#window-tinylfu">here.</a></li>
     *     <li>For the basic cache, exceeding the maximum cache size will cause the entire cache to flush.</li>
     *     <li>For the LRU cache, once the maximum cache size is reached, the least-recently-used entry will be removed.
     *     </li>
     * </ul>
     * @since 6.4.0
     */
    public static final String STRUTS_OGNL_EXPRESSION_CACHE_TYPE = "struts.ognl.expressionCacheType";

    /**
     * Specifies the maximum cache size for parsed OGNL expressions. This should be configured based on the cache type
     * chosen and application-specific needs.
     *
     * @since 6.0.0
     */
    public static final String STRUTS_OGNL_EXPRESSION_CACHE_MAXSIZE = "struts.ognl.expressionCacheMaxSize";

    /**
     * @since 6.0.0
     * @deprecated since 6.4.0, use {@link StrutsConstants#STRUTS_OGNL_EXPRESSION_CACHE_TYPE} instead.
     */
    @Deprecated
    public static final String STRUTS_OGNL_EXPRESSION_CACHE_LRU_MODE = "struts.ognl.expressionCacheLRUMode";

    /**
     * Enables evaluation of OGNL expressions
     * @since 6.0.0
     */
    public static final String STRUTS_OGNL_ENABLE_EVAL_EXPRESSION = "struts.ognl.enableEvalExpression";

    /**
     * Enables evaluation of OGNL expressions
     * @deprecated as of 6.0.0.  Use {@link #STRUTS_OGNL_ENABLE_EVAL_EXPRESSION} instead.
     */
    public static final String STRUTS_ENABLE_OGNL_EVAL_EXPRESSION = STRUTS_OGNL_ENABLE_EVAL_EXPRESSION;

    /** The maximum length of an expression (OGNL) */
    public static final String STRUTS_OGNL_EXPRESSION_MAX_LENGTH = "struts.ognl.expressionMaxLength";

    /** Parsed OGNL expressions which contain these node types will be blocked */
    public static final String STRUTS_OGNL_EXCLUDED_NODE_TYPES = "struts.ognl.excludedNodeTypes";

    /** Disables {@link org.apache.struts2.dispatcher.StrutsRequestWrapper} request attribute value stack lookup (JSTL accessibility) */
    public static final String STRUTS_DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP = "struts.disableRequestAttributeValueStackLookup";

    /** The{@link org.apache.struts2.views.util.UrlHelper} implementation class */
    public static final String STRUTS_URL_HELPER = "struts.view.urlHelper";

    /** {@link com.opensymphony.xwork2.conversion.impl.XWorkBasicConverter} */
    public static final String STRUTS_CONVERTER_COLLECTION = "struts.converter.collection";
    public static final String STRUTS_CONVERTER_ARRAY = "struts.converter.array";
    public static final String STRUTS_CONVERTER_DATE = "struts.converter.date";
    public static final String STRUTS_CONVERTER_NUMBER = "struts.converter.number";
    public static final String STRUTS_CONVERTER_STRING = "struts.converter.string";

    /** Enable handling exceptions by Dispatcher - true by default */
    public static final String STRUTS_HANDLE_EXCEPTION = "struts.handle.exception";

    public static final String STRUTS_CONVERTER_PROPERTIES_PROCESSOR = "struts.converter.properties.processor";
    public static final String STRUTS_CONVERTER_FILE_PROCESSOR = "struts.converter.file.processor";
    public static final String STRUTS_CONVERTER_ANNOTATION_PROCESSOR = "struts.converter.annotation.processor";
    public static final String STRUTS_CONVERTER_CREATOR = "struts.converter.creator";
    public static final String STRUTS_CONVERTER_HOLDER = "struts.converter.holder";

    public static final String STRUTS_EXPRESSION_PARSER = "struts.expression.parser";

    /** Namespace names' whitelist */
    public static final String STRUTS_ALLOWED_NAMESPACE_NAMES = "struts.allowed.namespace.names";
    /** Default namespace name to use when namespace didn't match the whitelist */
    public static final String STRUTS_DEFAULT_NAMESPACE_NAME = "struts.default.namespace.name";

    /** Action names' whitelist */
    public static final String STRUTS_ALLOWED_ACTION_NAMES = "struts.allowed.action.names";
    /** Default action name to use when action didn't match the whitelist */
    public static final String STRUTS_DEFAULT_ACTION_NAME = "struts.default.action.name";

    /** Method names' whitelist */
    public static final String STRUTS_ALLOWED_METHOD_NAMES = "struts.allowed.method.names";
    /** Default method name to use when method didn't match the whitelist */
    public static final String STRUTS_DEFAULT_METHOD_NAME = "struts.default.method.name";

    /** Enables action: prefix */
    public static final String STRUTS_MAPPER_ACTION_PREFIX_ENABLED = "struts.mapper.action.prefix.enabled";

    public static final String DEFAULT_TEMPLATE_TYPE_CONFIG_KEY = "struts.ui.templateSuffix";

    /** Allows override default DispatcherErrorHandler */
    public static final String STRUTS_DISPATCHER_ERROR_HANDLER = "struts.dispatcher.errorHandler";

    /** Comma delimited set of excluded classes which cannot be accessed via OGNL expressions. Matching is done on both target and member classes of OGNL expression. Note that superclasses of listed classes are also used for matching. */
    public static final String STRUTS_EXCLUDED_CLASSES = "struts.excludedClasses";
    /** Comma delimited set of RegEx to match against package names of target and member classes of OGNL expressions. If matched, they cannot be accessed. */
    public static final String STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS = "struts.excludedPackageNamePatterns";
    /** Comma delimited set of package names, of which all its classes, and all classes in its subpackages, cannot be accessed via OGNL expressions. Matching is done on both target and member classes of OGNL expression. */
    public static final String STRUTS_EXCLUDED_PACKAGE_NAMES = "struts.excludedPackageNames";
    /** Comma delimited set of exempt classes from matching against excludedPackageNames and excludedPackageNamePatterns. As matching for excluded packages is done on both target and member classes of OGNL expression, an exemption must exist for each match. */
    public static final String STRUTS_EXCLUDED_PACKAGE_EXEMPT_CLASSES = "struts.excludedPackageExemptClasses";

    /** Comma delimited set of excluded classes and package names which cannot be accessed via expressions in devMode */
    public static final String STRUTS_DEV_MODE_EXCLUDED_CLASSES = "struts.devMode.excludedClasses";
    public static final String STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAME_PATTERNS = "struts.devMode.excludedPackageNamePatterns";
    public static final String STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAMES = "struts.devMode.excludedPackageNames";
    public static final String STRUTS_DEV_MODE_EXCLUDED_PACKAGE_EXEMPT_CLASSES = "struts.devMode.excludedPackageExemptClasses";

    /** Boolean to enable strict allowlist processing of all OGNL expression calls. */
    public static final String STRUTS_ALLOWLIST_ENABLE = "struts.allowlist.enable";
    /** Comma delimited set of allowed classes which CAN be accessed via OGNL expressions. Both target and member classes of OGNL expression must be allowlisted. */
    public static final String STRUTS_ALLOWLIST_CLASSES = "struts.allowlist.classes";
    /** Comma delimited set of package names, of which all its classes, and all classes in its subpackages, CAN be accessed via OGNL expressions. Both target and member classes of OGNL expression must be allowlisted. */
    public static final String STRUTS_ALLOWLIST_PACKAGE_NAMES = "struts.allowlist.packageNames";

    /** Dedicated services to check if passed string is excluded/accepted */
    public static final String STRUTS_EXCLUDED_PATTERNS_CHECKER = "struts.excludedPatterns.checker";
    public static final String STRUTS_ACCEPTED_PATTERNS_CHECKER = "struts.acceptedPatterns.checker";
    public static final String STRUTS_NOT_EXCLUDED_ACCEPTED_PATTERNS_CHECKER = "struts.notExcludedAcceptedPatterns.checker";

    /** Constant is used to override framework's default excluded patterns */
    public static final String STRUTS_OVERRIDE_EXCLUDED_PATTERNS = "struts.override.excludedPatterns";
    public static final String STRUTS_OVERRIDE_ACCEPTED_PATTERNS = "struts.override.acceptedPatterns";

    public static final String STRUTS_ADDITIONAL_EXCLUDED_PATTERNS = "struts.additional.excludedPatterns";
    public static final String STRUTS_ADDITIONAL_ACCEPTED_PATTERNS = "struts.additional.acceptedPatterns";

    public static final String STRUTS_PARAMETERS_REQUIRE_ANNOTATIONS = "struts.parameters.requireAnnotations";
    public static final String STRUTS_PARAMETERS_REQUIRE_ANNOTATIONS_TRANSITION = "struts.parameters.requireAnnotations.transitionMode";

    public static final String STRUTS_CONTENT_TYPE_MATCHER = "struts.contentTypeMatcher";

    public static final String STRUTS_SMI_METHOD_REGEX = "struts.strictMethodInvocation.methodRegex";

    public static final String STRUTS_TEXT_PROVIDER = "struts.textProvider";
    public static final String STRUTS_TEXT_PROVIDER_FACTORY = "struts.textProviderFactory";
    public static final String STRUTS_LOCALIZED_TEXT_PROVIDER = "struts.localizedTextProvider";

    public static final String STRUTS_DISALLOW_PROXY_OBJECT_ACCESS = "struts.disallowProxyObjectAccess";
    public static final String STRUTS_DISALLOW_PROXY_MEMBER_ACCESS = "struts.disallowProxyMemberAccess";
    public static final String STRUTS_DISALLOW_DEFAULT_PACKAGE_ACCESS = "struts.disallowDefaultPackageAccess";

    public static final String STRUTS_OGNL_AUTO_GROWTH_COLLECTION_LIMIT = "struts.ognl.autoGrowthCollectionLimit";

    /** See {@link com.opensymphony.xwork2.config.impl.AbstractMatcher#appendNamedParameters */
    public static final String STRUTS_MATCHER_APPEND_NAMED_PARAMETERS = "struts.matcher.appendNamedParameters";

    public static final String STRUTS_CHAINING_COPY_ERRORS = "struts.chaining.copyErrors";
    public static final String STRUTS_CHAINING_COPY_FIELD_ERRORS = "struts.chaining.copyFieldErrors";
    public static final String STRUTS_CHAINING_COPY_MESSAGES = "struts.chaining.copyMessages";
    public static final String STRUTS_OBJECT_FACTORY_CLASSLOADER = "struts.objectFactory.classloader";

    /** See {@link org.apache.struts2.components.Date#setDateFormatter(DateFormatter)} */
    public static final String STRUTS_DATE_FORMATTER = "struts.date.formatter";

    public static final String STRUTS_URL_QUERY_STRING_BUILDER = "struts.url.queryStringBuilder";
    public static final String STRUTS_URL_QUERY_STRING_PARSER = "struts.url.queryStringParser";
    public static final String STRUTS_URL_ENCODER = "struts.url.encoder";
    public static final String STRUTS_URL_DECODER = "struts.url.decoder";

    /** A global flag to set property {@link org.apache.struts2.components.Checkbox#setSubmitUnchecked(String)} */
    public static final String STRUTS_UI_CHECKBOX_SUBMIT_UNCHECKED = "struts.ui.checkbox.submitUnchecked";

    /** See {@link org.apache.struts2.interceptor.exec.ExecutorProvider} */
    public static final String STRUTS_EXECUTOR_PROVIDER = "struts.executor.provider";

    /**
     * See {@link org.apache.struts2.interceptor.csp.CspNonceReader}
     * @since 6.8.0
     */
    public static final String STRUTS_CSP_NONCE_READER = "struts.csp.nonce.reader";
    public static final String STRUTS_CSP_NONCE_SOURCE = "struts.csp.nonce.source";
}
