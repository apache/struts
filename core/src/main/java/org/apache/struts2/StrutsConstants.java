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

package org.apache.struts2;

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

    /** Whether to reload the XML configuration or not */
    public static final String STRUTS_CONFIGURATION_XML_RELOAD = "struts.configuration.xml.reload";

    /** The URL extension to use to determine if the request is meant for a Struts action */
    public static final String STRUTS_ACTION_EXTENSION = "struts.action.extension";

	/** Comma separated list of patterns (java.util.regex.Pattern) to be excluded from Struts2-processing */
	public static final String STRUTS_ACTION_EXCLUDE_PATTERN = "struts.action.excludePattern";

    /** Whether to use the alterative syntax for the tags or not */
    public static final String STRUTS_TAG_ALTSYNTAX = "struts.tag.altSyntax";

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
    
    @Deprecated
    /** Cache Freemarker templates, this cache is managed by struts2,instead of native freemarker cache,set STRUTS_FREEMARKER_MRU_MAX_STRONG_SIZE >0&&STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY>0*/
    public static final String STRUTS_FREEMARKER_TEMPLATES_CACHE = "struts.freemarker.templatesCache";
    
    /** Update freemarker templates cache in seconds*/
    public static final String STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY = "struts.freemarker.templatesCache.updateDelay";
    
    /** Cache model instances at BeanWrapper level */
    public static final String STRUTS_FREEMARKER_BEANWRAPPER_CACHE = "struts.freemarker.beanwrapperCache";
    
    /** Maximum strong sizing for MruCacheStorage for freemarker */
    public static final String STRUTS_FREEMARKER_MRU_MAX_STRONG_SIZE = "struts.freemarker.mru.max.strong.size";
    
    /** org.apache.struts2.views.velocity.VelocityManager implementation class */
    public static final String STRUTS_VELOCITY_MANAGER_CLASSNAME = "struts.velocity.manager.classname";

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

    /** The maximize size of a multipart request (file upload) */
    public static final String STRUTS_MULTIPART_MAXSIZE = "struts.multipart.maxSize";

    /** The directory to use for storing uploaded files */
    public static final String STRUTS_MULTIPART_SAVEDIR = "struts.multipart.saveDir";

    /** Declares the buffer size to be used during streaming multipart content to disk. Used only with {@link org.apache.struts2.dispatcher.multipart.JakartaStreamMultiPartRequest} */
    public static final String STRUTS_MULTIPART_BUFFERSIZE = "struts.multipart.bufferSize";

    /**
     * The org.apache.struts2.dispatcher.multipart.MultiPartRequest parser implementation
     * for a multipart request (file upload)
     */
    public static final String STRUTS_MULTIPART_PARSER = "struts.multipart.parser";

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
    public static final String STRUTS_OBJECTFACTORY_SPRING_ENABLE_AOP_SUPPORT = "struts.objectFactory.spring.enableAopSupport";

    /** Whether or not XSLT templates should not be cached */
    public static final String STRUTS_XSLT_NOCACHE = "struts.xslt.nocache";

    /** Location of additional configuration properties files to load */
    public static final String STRUTS_CUSTOM_PROPERTIES = "struts.custom.properties";

    /** Location of additional localization properties files to load */
    public static final String STRUTS_CUSTOM_I18N_RESOURCES = "struts.custom.i18n.resources";

    /** The org.apache.struts2.dispatcher.mapper.ActionMapper implementation class */
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

    /** The name of the xwork converter implementation */
    public static final String STRUTS_XWORKCONVERTER = "struts.xworkConverter";

    public static final String STRUTS_ALWAYS_SELECT_FULL_NAMESPACE = "struts.mapper.alwaysSelectFullNamespace";

    /** XWork default text provider */
    public static final String STRUTS_XWORKTEXTPROVIDER = "struts.xworkTextProvider";

    /** The {@link com.opensymphony.xwork2.LocaleProvider} implementation class */
    public static final String STRUTS_LOCALE_PROVIDER = "struts.localeProvider";

    /** The name of the parameter to create when mapping an id (used by some action mappers) */
	public static final String STRUTS_ID_PARAMETER_NAME = "struts.mapper.idParameterName";
	
	/** The name of the parameter to determine whether static method access will be allowed in OGNL expressions or not */
	public static final String STRUTS_ALLOW_STATIC_METHOD_ACCESS = "struts.ognl.allowStaticMethodAccess";

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

    /** Throw RuntimeException when a property is not found, or the evaluation of the espression fails*/
    public static final String STRUTS_EL_THROW_EXCEPTION = "struts.el.throwExceptionOnFailure";

    /** Logs properties that are not found (very verbose) **/
    public static final String STRUTS_LOG_MISSING_PROPERTIES = "struts.ognl.logMissingProperties";

    /** Enables caching of parsed OGNL expressions **/
    public static final String STRUTS_ENABLE_OGNL_EXPRESSION_CACHE = "struts.ognl.enableExpressionCache";

    /** Enables evaluation of OGNL expressions **/
    public static final String STRUTS_ENABLE_OGNL_EVAL_EXPRESSION = "struts.ognl.enableOGNLEvalExpression";

    /** Disables {@link org.apache.struts2.dispatcher.StrutsRequestWrapper} request attribute value stack lookup (JSTL accessibility) **/
    public static final String STRUTS_DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP = "struts.disableRequestAttributeValueStackLookup";

    /** The{@link org.apache.struts2.views.util.UrlHelper} implementation class **/
    public static final String STRUTS_URL_HELPER = "struts.view.urlHelper";

    /** {@link com.opensymphony.xwork2.conversion.impl.XWorkBasicConverter} **/
    public static final String STRUTS_CONVERTER_COLLECTION = "struts.converter.collection";
    public static final String STRUTS_CONVERTER_ARRAY = "struts.converter.array";
    public static final String STRUTS_CONVERTER_DATE = "struts.converter.date";
    public static final String STRUTS_CONVERTER_NUMBER = "struts.converter.number";
    public static final String STRUTS_CONVERTER_STRING = "struts.converter.string";

    /** Enable handling exceptions by Dispatcher - true by default **/
    public static final String STRUTS_HANDLE_EXCEPTION = "struts.handle.exception";

    public static final String STRUTS_CONVERTER_PROPERTIES_PROCESSOR = "struts.converter.properties.processor";
    public static final String STRUTS_CONVERTER_FILE_PROCESSOR = "struts.converter.file.processor";
    public static final String STRUTS_CONVERTER_ANNOTATION_PROCESSOR = "struts.converter.annotation.processor";
    public static final String STRUTS_CONVERTER_CREATOR = "struts.converter.creator";
    public static final String STRUTS_CONVERTER_HOLDER = "struts..converter.holder";

    public static final String STRUTS_EXPRESSION_PARSER = "struts.expression.parser";

    /** namespaces names' whitelist **/
    public static final String STRUTS_ALLOWED_NAMESPACE_NAMES = "struts.allowed.namespace.names";
    /** default namespace name to use when namespace didn't match the whitelist **/
    public static final String STRUTS_DEFAULT_NAMESPACE_NAME = "struts.default.namespace.name";

    /** actions names' whitelist **/
    public static final String STRUTS_ALLOWED_ACTION_NAMES = "struts.allowed.action.names";
    /** default action name to use when action didn't match the whitelist **/
    public static final String STRUTS_DEFAULT_ACTION_NAME = "struts.default.action.name";

    /** methods names' whitelist **/
    public static final String STRUTS_ALLOWED_METHOD_NAMES = "struts.allowed.method.names";
    /** default method name to use when method didn't match the whitelist **/
    public static final String STRUTS_DEFAULT_METHOD_NAME = "struts.default.method.name";

    /** enables action: prefix **/
    public static final String STRUTS_MAPPER_ACTION_PREFIX_ENABLED = "struts.mapper.action.prefix.enabled";

    /** enables access to actions in other namespaces than current with action: prefix **/
    public static final String STRUTS_MAPPER_ACTION_PREFIX_CROSSNAMESPACES = "struts.mapper.action.prefix.crossNamespaces";

    public static final String DEFAULT_TEMPLATE_TYPE_CONFIG_KEY = "struts.ui.templateSuffix";

    /** Allows override default DispatcherErrorHandler **/
    public static final String STRUTS_DISPATCHER_ERROR_HANDLER = "struts.dispatcher.errorHandler";

    /** Comma delimited set of excluded classes and package names which cannot be accessed via expressions **/
    public static final String STRUTS_EXCLUDED_CLASSES = "struts.excludedClasses";
    public static final String STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS = "struts.excludedPackageNamePatterns";
    public static final String STRUTS_EXCLUDED_PACKAGE_NAMES = "struts.excludedPackageNames";

    /** Dedicated services to check if passed string is excluded/accepted **/
    public static final String STRUTS_EXCLUDED_PATTERNS_CHECKER = "struts.excludedPatterns.checker";
    public static final String STRUTS_ACCEPTED_PATTERNS_CHECKER = "struts.acceptedPatterns.checker";

    /** Constant is used to override framework's default excluded patterns **/
    public static final String STRUTS_OVERRIDE_EXCLUDED_PATTERNS = "struts.override.excludedPatterns";
    public static final String STRUTS_OVERRIDE_ACCEPTED_PATTERNS = "struts.override.acceptedPatterns";

    public static final String STRUTS_ADDITIONAL_EXCLUDED_PATTERNS = "struts.additional.excludedPatterns";
    public static final String STRUTS_ADDITIONAL_ACCEPTED_PATTERNS = "struts.additional.acceptedPatterns";

    public static final String STRUTS_CONTENT_TYPE_MATCHER = "struts.contentTypeMatcher";

}
