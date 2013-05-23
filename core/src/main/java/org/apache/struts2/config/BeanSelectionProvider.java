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
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
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
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.util.UrlHelper;
import org.apache.struts2.views.velocity.VelocityManager;

import java.util.Properties;
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
public class BeanSelectionProvider implements ConfigurationProvider {

    public static final String DEFAULT_BEAN_NAME = "struts";

    private static final Logger LOG = LoggerFactory.getLogger(BeanSelectionProvider.class);

    public void destroy() {
        // NO-OP
    }

    public void loadPackages() throws ConfigurationException {
        // NO-OP
    }

    public void init(Configuration configuration) throws ConfigurationException {
        // NO-OP
    }

    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props) {
        alias(ObjectFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY, builder, props);
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
        alias(StaticContentLoader.class, StrutsConstants.STRUTS_STATIC_CONTENT_LOADER, builder, props);
        alias(UnknownHandlerManager.class, StrutsConstants.STRUTS_UNKNOWN_HANDLER_MANAGER, builder, props);
        alias(UrlHelper.class, StrutsConstants.STRUTS_URL_HELPER, builder, props);

        alias(TextParser.class, StrutsConstants.STRUTS_EXPRESSION_PARSER, builder, props);

        if ("true".equalsIgnoreCase(props.getProperty(StrutsConstants.STRUTS_DEVMODE))) {
            props.setProperty(StrutsConstants.STRUTS_I18N_RELOAD, "true");
            props.setProperty(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "true");
            props.setProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE, "false");
            props.setProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY, "0");
            // Convert struts properties into ones that xwork expects
            props.setProperty(XWorkConstants.DEV_MODE, "true");
        } else {
            props.setProperty(XWorkConstants.DEV_MODE, "false");
        }

        // Convert Struts properties into XWork properties
        convertIfExist(props, StrutsConstants.STRUTS_LOG_MISSING_PROPERTIES, XWorkConstants.LOG_MISSING_PROPERTIES);
        convertIfExist(props, StrutsConstants.STRUTS_ENABLE_OGNL_EXPRESSION_CACHE, XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE);
        convertIfExist(props, StrutsConstants.STRUTS_ENABLE_OGNL_EVAL_EXPRESSION, XWorkConstants.ENABLE_OGNL_EVAL_EXPRESSION);
        convertIfExist(props, StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS);
        convertIfExist(props, StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, XWorkConstants.RELOAD_XML_CONFIGURATION);

        LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
        loadCustomResourceBundles(props);
    }

    private void convertIfExist(LocatableProperties props, String fromKey, String toKey) {
        if (props.containsKey(fromKey)) {
            props.setProperty(toKey, props.getProperty(fromKey));
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

    void alias(Class type, String key, ContainerBuilder builder, Properties props) {
        alias(type, key, builder, props, Scope.SINGLETON);
    }

    void alias(Class type, String key, ContainerBuilder builder, Properties props, Scope scope) {
        if (!builder.contains(type)) {
            String foundName = props.getProperty(key, DEFAULT_BEAN_NAME);
            if (builder.contains(type, foundName)) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Choosing bean (#0) for (#1)", foundName, type.getName());
                }
                builder.alias(type, foundName, Container.DEFAULT_NAME);
            } else {
                try {
                    Class cls = ClassLoaderUtil.loadClass(foundName, this.getClass());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Choosing bean (#0) for (#1)", cls.getName(), type.getName());
                    }
                    builder.factory(type, cls, scope);
                } catch (ClassNotFoundException ex) {
                    // Perhaps a spring bean id, so we'll delegate to the object factory at runtime
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Choosing bean (#0) for (#1) to be loaded from the ObjectFactory", foundName, type.getName());
                    }
                    if (DEFAULT_BEAN_NAME.equals(foundName)) {
                        // Probably an optional bean, will ignore
                    } else {
                        if (ObjectFactory.class != type) {
                            builder.factory(type, new ObjectFactoryDelegateFactory(foundName, type), scope);
                        } else {
                            throw new ConfigurationException("Cannot locate the chosen ObjectFactory implementation: " + foundName);
                        }
                    }
                }
            }
        } else {
            if (LOG.isWarnEnabled()) {
        	    LOG.warn("Unable to alias bean type (#0), default mapping already assigned.", type.getName());
            }
        }
    }

    static class ObjectFactoryDelegateFactory implements Factory {

        String name;
        Class type;

        ObjectFactoryDelegateFactory(String name, Class type) {
            this.name = name;
            this.type = type;
        }

        public Object create(Context context) throws Exception {
            ObjectFactory objFactory = context.getContainer().getInstance(ObjectFactory.class);
            try {
                return objFactory.buildBean(name, null, true);
            } catch (ClassNotFoundException ex) {
                throw new ConfigurationException("Unable to load bean "+type.getName()+" ("+name+")");
            }
        }

    }

}
