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

package org.apache.struts2.config;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.factory.UnknownHandlerFactory;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.impl.ArrayConverter;
import com.opensymphony.xwork2.conversion.impl.CollectionConverter;
import com.opensymphony.xwork2.conversion.impl.DateConverter;
import com.opensymphony.xwork2.conversion.impl.NumberConverter;
import com.opensymphony.xwork2.conversion.impl.StringConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.factory.ActionFactory;
import com.opensymphony.xwork2.factory.ConverterFactory;
import com.opensymphony.xwork2.factory.InterceptorFactory;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.factory.ValidatorFactory;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.dispatcher.DispatcherErrorHandler;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.util.ContentTypeMatcher;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.util.UrlHelper;
import org.apache.struts2.views.velocity.VelocityManager;

import java.util.StringTokenizer;

/**
 * Selects the implementations of key framework extension points, using the loaded
 * property constants.  The implementations are selected from the container builder
 * using the name defined in its associated property.  The default implementation name will
 * always be "struts".
 *
 * <p>
 * The following is a list of the allowed extension points:
 *
 * <!-- START SNIPPET: extensionPoints -->
 * <table border="1">
 *   <tr>
 *     <th>Type</th>
 *     <th>Property</th>
 *     <th>Scope</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.ObjectFactory</td>
 *     <td>struts.objectFactory</td>
 *     <td>singleton</td>
 *     <td>Creates actions, results, and interceptors</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.factory.ActionFactory</td>
 *     <td>struts.objectFactory.actionFactory</td>
 *     <td>singleton</td>
 *     <td>Dedicated factory used to create Actions, you can implement/extend existing one instead of defining new ObjectFactory</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.factory.ResultFactory</td>
 *     <td>struts.objectFactory.resultFactory</td>
 *     <td>singleton</td>
 *     <td>Dedicated factory used to create Results, you can implement/extend existing one instead of defining new ObjectFactory</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.factory.InterceptorFactory</td>
 *     <td>struts.objectFactory.interceptorFactory</td>
 *     <td>singleton</td>
 *     <td>Dedicated factory used to create Interceptors, you can implement/extend existing one instead of defining new ObjectFactory</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.factory.ConverterFactory</td>
 *     <td>struts.objectFactory.converterFactory</td>
 *     <td>singleton</td>
 *     <td>Dedicated factory used to create TypeConverters, you can implement/extend existing one instead of defining new ObjectFactory</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.factory.ValidatorFactory</td>
 *     <td>struts.objectFactory.validatorFactory</td>
 *     <td>singleton</td>
 *     <td>Dedicated factory used to create Validators, you can implement/extend existing one instead of defining new ObjectFactory</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.ActionProxyFactory</td>
 *     <td>struts.actionProxyFactory</td>
 *     <td>singleton</td>
 *     <td>Creates the ActionProxy</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.util.ObjectTypeDeterminer</td>
 *     <td>struts.objectTypeDeterminer</td>
 *     <td>singleton</td>
 *     <td>Determines what the key and element class of a Map or Collection should be</td>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.dispatcher.mapper.ActionMapper</td>
 *     <td>struts.mapper.class</td>
 *     <td>singleton</td>
 *     <td>Determines the ActionMapping from a request and a URI from an ActionMapping</td>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.dispatcher.multipart.MultiPartRequest</td>
 *     <td>struts.multipart.parser</td>
 *     <td>per request</td>
 *     <td>Parses a multipart request (file upload)</td>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.views.freemarker.FreemarkerManager</td>
 *     <td>struts.freemarker.manager.classname</td>
 *     <td>singleton</td>
 *     <td>Loads and processes Freemarker templates</td>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.views.velocity.VelocityManager</td>
 *     <td>struts.velocity.manager.classname</td>
 *     <td>singleton</td>
 *     <td>Loads and processes Velocity templates</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.validator.ActionValidatorManager</td>
 *     <td>struts.actionValidatorManager</td>
 *     <td>singleton</td>
 *     <td>Main interface for validation managers (regular and annotation based).  Handles both the loading of
 *         configuration and the actual validation (since 2.1)</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.util.ValueStackFactory</td>
 *     <td>struts.valueStackFactory</td>
 *     <td>singleton</td>
 *     <td>Creates value stacks (since 2.1)</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.reflection.ReflectionProvider</td>
 *     <td>struts.reflectionProvider</td>
 *     <td>singleton</td>
 *     <td>Provides reflection services, key place to plug in a custom expression language (since 2.1)</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.reflection.ReflectionContextFactory</td>
 *     <td>struts.reflectionContextFactory</td>
 *     <td>singleton</td>
 *     <td>Creates reflection context maps used for reflection and expression language operations (since 2.1)</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.config.PackageProvider</td>
 *     <td>N/A</td>
 *     <td>singleton</td>
 *     <td>All beans registered as PackageProvider implementations will be automatically included in configuration building (since 2.1)</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.util.PatternMatcher</td>
 *     <td>struts.patternMatcher</td>
 *     <td>singleton</td>
 *     <td>Matches patterns, such as action names, generally used in configuration (since 2.1)</td>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.views.dispatcher.DefaultStaticContentLoader</td>
 *     <td>struts.staticContentLoader</td>
 *     <td>singleton</td>
 *     <td>Loads static resources (since 2.1)</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.impl.XWorkConverter</td>
 *     <td>struts.xworkConverter</td>
 *     <td>singleton</td>
 *     <td>Handles conversion logic and allows to load custom converters per class or per action</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.TextProvider</td>
 *     <td>struts.xworkTextProvider</td>
 *     <td>default</td>
 *     <td>Allows provide custom TextProvider for whole application</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.LocaleProvider</td>
 *     <td>struts.localeProvider</td>
 *     <td>singleton</td>
 *     <td>Allows provide custom TextProvider for whole application</td>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.components.UrlRenderer</td>
 *     <td>struts.urlRenderer</td>
 *     <td>singleton</td>
 *     <td>Allows provide custom implementation of environment specific URL rendering/creating class</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.UnknownHandlerManager</td>
 *     <td>struts.unknownHandlerManager</td>
 *     <td>singleton</td>
 *     <td>Implementation of this interface allows handle logic of unknown Actions, Methods or Results</td>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.views.util.UrlHelper</td>
 *     <td>struts.view.urlHelper</td>
 *     <td>singleton</td>
 *     <td>Helper class used with URLRenderer to provide exact logic for building URLs</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.FileManagerFactory</td>
 *     <td>struts.fileManagerFactory</td>
 *     <td>singleton</td>
 *     <td>Used to create {@link FileManager} instance to access files on the File System as also to monitor if reload is needed,
 *     can be implemented / overwritten to meet specific an application server needs
 *     </td>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.impl.CollectionConverter</td>
 *     <td>struts.converter.collection</td>
 *     <td>singleton</td>
 *     <td>Converter used to convert any object to Collection and back</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.impl.ArrayConverter</td>
 *     <td>struts.converter.array</td>
 *     <td>singleton</td>
 *     <td>Converter used to convert any object to Array and back</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.impl.DateConverter</td>
 *     <td>struts.converter.date</td>
 *     <td>singleton</td>
 *     <td>Converter used to convert any object to Date and back</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.impl.NumberConverter</td>
 *     <td>struts.converter.number</td>
 *     <td>singleton</td>
 *     <td>Converter used to convert any object to Number and back</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.impl.StringConverter</td>
 *     <td>struts.converter.string</td>
 *     <td>singleton</td>
 *     <td>Converter used to convert any object to String and back</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor</td>
 *     <td>struts.conversion.properties.processor</td>
 *     <td>singleton</td>
 *     <td>Process Properties to create converters</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor</td>
 *     <td>struts.converter.file.processor</td>
 *     <td>singleton</td>
 *     <td>Process <class>-conversion.properties file create converters</class></td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor</td>
 *     <td>struts.converter.annotation.processor</td>
 *     <td>singleton</td>
 *     <td>Process TypeConversion annotation to create converters</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.TypeConverterCreator</td>
 *     <td>struts.converter.creator</td>
 *     <td>singleton</td>
 *     <td>Creates user converters</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.conversion.TypeConverterHolder</td>
 *     <td>struts.converter.holder</td>
 *     <td>singleton</td>
 *     <td>Holds user converters' instances</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.util.TextParser</td>
 *     <td>struts.expression.parser</td>
 *     <td>singleton</td>
 *     <td>Used to parse expressions like ${foo.bar} or %{bar.foo} but it is up tp the TextParser's
 *         implementation what kind of opening char to use (#, $, %, etc)</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.ExcludedPatternsChecker</td>
 *     <td>struts.excludedPatterns.checker</td>
 *     <td>request</td>
 *     <td>Used across different interceptors to check if given string matches one of the excluded patterns</td>
 *   </tr>
 *   <tr>
 *     <td>com.opensymphony.xwork2.AcceptedPatternsChecker</td>
 *     <td>struts.acceptedPatterns.checker</td>
 *     <td>request</td>
 *     <td>Used across different interceptors to check if given string matches one of the accepted patterns</td>
 *   </tr>
 *   <tr>
 *     <td>org.apache.struts2.util.ContentTypeMatcher</td>
 *     <td>struts.contentTypeMatcher</td>
 *     <td>singleton</td>
 *     <td>Matches content type of uploaded files (since 2.3.22)</td>
 *   </tr>
 * </table>
 *
 * <!-- END SNIPPET: extensionPoints -->
 * </p>
 * <p>
 * Implementations are selected using the value of its associated property.  That property is
 * used to determine the implementation by:
 * </p>
 * <ol>
 *   <li>Trying to find an existing bean by that name in the container</li>
 *   <li>Trying to find a class by that name, then creating a new bean factory for it</li>
 *   <li>Creating a new delegation bean factory that delegates to the configured ObjectFactory at runtime</li>
 * </ol>
 * <p>
 * Finally, this class overrides certain properties if dev mode is enabled:
 * </p>
 * <ul>
 *   <li><code>struts.i18n.reload = true</code></li>
 *   <li><code>struts.configuration.xml.reload = true</code></li>
 * </ul>
 */
public class DefaultBeanSelectionProvider extends AbstractBeanSelectionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBeanSelectionProvider.class);

    public void register(ContainerBuilder builder, LocatableProperties props) {
        alias(ObjectFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY, builder, props);
        alias(ActionFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_ACTIONFACTORY, builder, props);
        alias(ResultFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_RESULTFACTORY, builder, props);
        alias(ConverterFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_CONVERTERFACTORY, builder, props);
        alias(InterceptorFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_INTERCEPTORFACTORY, builder, props);
        alias(ValidatorFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_VALIDATORFACTORY, builder, props);
        alias(UnknownHandlerFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY_UNKNOWNHANDLERFACTORY, builder, props);

        alias(FileManagerFactory.class, StrutsConstants.STRUTS_FILE_MANAGER_FACTORY, builder, props, Scope.SINGLETON);

        alias(XWorkConverter.class, StrutsConstants.STRUTS_XWORKCONVERTER, builder, props);
        alias(CollectionConverter.class, StrutsConstants.STRUTS_CONVERTER_COLLECTION, builder, props);
        alias(ArrayConverter.class, StrutsConstants.STRUTS_CONVERTER_ARRAY, builder, props);
        alias(DateConverter.class, StrutsConstants.STRUTS_CONVERTER_DATE, builder, props);
        alias(NumberConverter.class, StrutsConstants.STRUTS_CONVERTER_NUMBER, builder, props);
        alias(StringConverter.class, StrutsConstants.STRUTS_CONVERTER_STRING, builder, props);

        alias(ConversionPropertiesProcessor.class, StrutsConstants.STRUTS_CONVERTER_PROPERTIES_PROCESSOR, builder, props);
        alias(ConversionFileProcessor.class, StrutsConstants.STRUTS_CONVERTER_FILE_PROCESSOR, builder, props);
        alias(ConversionAnnotationProcessor.class, StrutsConstants.STRUTS_CONVERTER_ANNOTATION_PROCESSOR, builder, props);
        alias(TypeConverterCreator.class, StrutsConstants.STRUTS_CONVERTER_CREATOR, builder, props);
        alias(TypeConverterHolder.class, StrutsConstants.STRUTS_CONVERTER_HOLDER, builder, props);

        alias(TextProvider.class, StrutsConstants.STRUTS_XWORKTEXTPROVIDER, builder, props, Scope.DEFAULT);

        alias(LocaleProvider.class, StrutsConstants.STRUTS_LOCALE_PROVIDER, builder, props);
        alias(ActionProxyFactory.class, StrutsConstants.STRUTS_ACTIONPROXYFACTORY, builder, props);
        alias(ObjectTypeDeterminer.class, StrutsConstants.STRUTS_OBJECTTYPEDETERMINER, builder, props);
        alias(ActionMapper.class, StrutsConstants.STRUTS_MAPPER_CLASS, builder, props);
        alias(MultiPartRequest.class, StrutsConstants.STRUTS_MULTIPART_PARSER, builder, props, Scope.DEFAULT);
        alias(FreemarkerManager.class, StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME, builder, props);
        alias(VelocityManager.class, StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME, builder, props);
        alias(UrlRenderer.class, StrutsConstants.STRUTS_URL_RENDERER, builder, props);
        alias(ActionValidatorManager.class, StrutsConstants.STRUTS_ACTIONVALIDATORMANAGER, builder, props);
        alias(ValueStackFactory.class, StrutsConstants.STRUTS_VALUESTACKFACTORY, builder, props);
        alias(ReflectionProvider.class, StrutsConstants.STRUTS_REFLECTIONPROVIDER, builder, props);
        alias(ReflectionContextFactory.class, StrutsConstants.STRUTS_REFLECTIONCONTEXTFACTORY, builder, props);
        alias(PatternMatcher.class, StrutsConstants.STRUTS_PATTERNMATCHER, builder, props);
        alias(ContentTypeMatcher.class, StrutsConstants.STRUTS_CONTENT_TYPE_MATCHER, builder, props);
        alias(StaticContentLoader.class, StrutsConstants.STRUTS_STATIC_CONTENT_LOADER, builder, props);
        alias(UnknownHandlerManager.class, StrutsConstants.STRUTS_UNKNOWN_HANDLER_MANAGER, builder, props);
        alias(UrlHelper.class, StrutsConstants.STRUTS_URL_HELPER, builder, props);

        alias(TextParser.class, StrutsConstants.STRUTS_EXPRESSION_PARSER, builder, props);

        alias(DispatcherErrorHandler.class, StrutsConstants.STRUTS_DISPATCHER_ERROR_HANDLER, builder, props);

        /** Checker is used mostly in interceptors, so there be one instance of checker per interceptor with Scope.DEFAULT **/
        alias(ExcludedPatternsChecker.class, StrutsConstants.STRUTS_EXCLUDED_PATTERNS_CHECKER, builder, props, Scope.DEFAULT);
        alias(AcceptedPatternsChecker.class, StrutsConstants.STRUTS_ACCEPTED_PATTERNS_CHECKER, builder, props, Scope.DEFAULT);

        switchDevMode(props);

        // Convert Struts properties into XWork properties
        convertIfExist(props, StrutsConstants.STRUTS_LOG_MISSING_PROPERTIES, XWorkConstants.LOG_MISSING_PROPERTIES);
        convertIfExist(props, StrutsConstants.STRUTS_ENABLE_OGNL_EXPRESSION_CACHE, XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE);
        convertIfExist(props, StrutsConstants.STRUTS_ENABLE_OGNL_EVAL_EXPRESSION, XWorkConstants.ENABLE_OGNL_EVAL_EXPRESSION);
        convertIfExist(props, StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS);
        convertIfExist(props, StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, XWorkConstants.RELOAD_XML_CONFIGURATION);

        convertIfExist(props, StrutsConstants.STRUTS_EXCLUDED_CLASSES, XWorkConstants.OGNL_EXCLUDED_CLASSES);
        convertIfExist(props, StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS, XWorkConstants.OGNL_EXCLUDED_PACKAGE_NAME_PATTERNS);
        convertIfExist(props, StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, XWorkConstants.OGNL_EXCLUDED_PACKAGE_NAMES);

        convertIfExist(props, StrutsConstants.STRUTS_ADDITIONAL_EXCLUDED_PATTERNS, XWorkConstants.ADDITIONAL_EXCLUDED_PATTERNS);
        convertIfExist(props, StrutsConstants.STRUTS_ADDITIONAL_ACCEPTED_PATTERNS, XWorkConstants.ADDITIONAL_ACCEPTED_PATTERNS);
        convertIfExist(props, StrutsConstants.STRUTS_OVERRIDE_EXCLUDED_PATTERNS, XWorkConstants.OVERRIDE_EXCLUDED_PATTERNS);
        convertIfExist(props, StrutsConstants.STRUTS_OVERRIDE_ACCEPTED_PATTERNS, XWorkConstants.OVERRIDE_ACCEPTED_PATTERNS);

        LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
        loadCustomResourceBundles(props);
    }

    /**
     * Enables/disables devMode and related settings if they aren't explicit set in struts.xml/struts.properties
     *
     * @param props configured properties
     */
    private void switchDevMode(LocatableProperties props) {
        if ("true".equalsIgnoreCase(props.getProperty(StrutsConstants.STRUTS_DEVMODE))) {
            if (props.getProperty(StrutsConstants.STRUTS_I18N_RELOAD) == null) {
                props.setProperty(StrutsConstants.STRUTS_I18N_RELOAD, "true");
            }
            if (props.getProperty(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD) == null) {
                props.setProperty(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "true");
            }
            if (props.getProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE) == null) {
                props.setProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE, "false");
            }
            if (props.getProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY) == null) {
                props.setProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY, "0");
            }
            // Convert struts properties into ones that xwork expects
            props.setProperty(XWorkConstants.DEV_MODE, "true");
        } else {
            props.setProperty(XWorkConstants.DEV_MODE, "false");
        }
    }

    private void loadCustomResourceBundles(LocatableProperties props) {
        String bundles = props.getProperty(StrutsConstants.STRUTS_CUSTOM_I18N_RESOURCES);
        if (bundles != null && bundles.length() > 0) {
            StringTokenizer customBundles = new StringTokenizer(bundles, ", ");

            while (customBundles.hasMoreTokens()) {
                String name = customBundles.nextToken();
                try {
                    if (LOG.isInfoEnabled()) {
                	    LOG.info("Loading global messages from [#0]", name);
                    }
                    LocalizedTextUtil.addDefaultResourceBundle(name);
                } catch (Exception e) {
                    LOG.error("Could not find messages file #0.properties. Skipping", name);
                }
            }
        }
    }

}
