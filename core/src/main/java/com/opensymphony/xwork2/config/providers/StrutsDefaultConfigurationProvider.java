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

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.DefaultUnknownHandlerManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.impl.ArrayConverter;
import com.opensymphony.xwork2.conversion.impl.CollectionConverter;
import com.opensymphony.xwork2.conversion.impl.DateConverter;
import com.opensymphony.xwork2.conversion.impl.InstantiatingNullHandler;
import com.opensymphony.xwork2.conversion.impl.NumberConverter;
import com.opensymphony.xwork2.conversion.impl.StringConverter;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.ognl.ObjectProxy;
import com.opensymphony.xwork2.ognl.OgnlReflectionContextFactory;
import com.opensymphony.xwork2.ognl.accessor.HttpParametersPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.ObjectAccessor;
import com.opensymphony.xwork2.ognl.accessor.ObjectProxyPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.ParameterPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkCollectionPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkEnumerationAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkIteratorPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkListPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkMapPropertyAccessor;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.DefaultAcceptedPatternsChecker;
import com.opensymphony.xwork2.security.DefaultExcludedPatternsChecker;
import com.opensymphony.xwork2.security.DefaultNotExcludedAcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.security.NotExcludedAcceptedPatternsChecker;
import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.WildcardHelper;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.AnnotationActionValidatorManager;
import com.opensymphony.xwork2.validator.DefaultActionValidatorManager;
import com.opensymphony.xwork2.validator.DefaultValidatorFactory;
import com.opensymphony.xwork2.validator.DefaultValidatorFileParser;
import com.opensymphony.xwork2.validator.ValidatorFactory;
import com.opensymphony.xwork2.validator.ValidatorFileParser;
import ognl.PropertyAccessor;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.interceptor.csp.CspNonceReader;
import org.apache.struts2.interceptor.csp.StrutsCspNonceReader;
import org.apache.struts2.interceptor.exec.ExecutorProvider;
import org.apache.struts2.interceptor.exec.StrutsExecutorProvider;
import org.apache.struts2.url.QueryStringBuilder;
import org.apache.struts2.url.QueryStringParser;
import org.apache.struts2.url.StrutsQueryStringBuilder;
import org.apache.struts2.url.StrutsQueryStringParser;
import org.apache.struts2.url.StrutsUrlDecoder;
import org.apache.struts2.url.StrutsUrlEncoder;
import org.apache.struts2.url.UrlDecoder;
import org.apache.struts2.url.UrlEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StrutsDefaultConfigurationProvider implements ConfigurationProvider {

    @Override
    public void destroy() {
    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
    }

    @Override
    public void loadPackages() throws ConfigurationException {
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {

        DefaultConfiguration.bootstrapFactories(builder)
                .factory(FileManagerFactory.class, DefaultFileManagerFactory.class, Scope.SINGLETON)

                .factory(ActionProxyFactory.class, DefaultActionProxyFactory.class, Scope.SINGLETON)

                .factory(ValidatorFactory.class, DefaultValidatorFactory.class, Scope.SINGLETON)
                .factory(ValidatorFileParser.class, DefaultValidatorFileParser.class, Scope.SINGLETON)
                .factory(PatternMatcher.class, WildcardHelper.class, Scope.SINGLETON)

                .factory(ReflectionContextFactory.class, OgnlReflectionContextFactory.class, Scope.SINGLETON)

                .factory(PropertyAccessor.class, Object.class.getName(), ObjectAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Iterator.class.getName(), XWorkIteratorPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Enumeration.class.getName(), XWorkEnumerationAccessor.class, Scope.SINGLETON)

                .factory(UnknownHandlerManager.class, DefaultUnknownHandlerManager.class, Scope.SINGLETON)

                // silly workarounds for ognl since there is no way to flush its caches
                .factory(PropertyAccessor.class, List.class.getName(), XWorkListPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, ArrayList.class.getName(), XWorkListPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, HashSet.class.getName(), XWorkCollectionPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Set.class.getName(), XWorkCollectionPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, HashMap.class.getName(), XWorkMapPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Map.class.getName(), XWorkMapPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Collection.class.getName(), XWorkCollectionPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, ObjectProxy.class.getName(), ObjectProxyPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, HttpParameters.class.getName(), HttpParametersPropertyAccessor.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, Parameter.class.getName(), ParameterPropertyAccessor.class, Scope.SINGLETON)

                .factory(NullHandler.class, Object.class.getName(), InstantiatingNullHandler.class, Scope.SINGLETON)
                .factory(ActionValidatorManager.class, AnnotationActionValidatorManager.class, Scope.SINGLETON)
                .factory(ActionValidatorManager.class, "no-annotations", DefaultActionValidatorManager.class, Scope.SINGLETON)

                .factory(CollectionConverter.class, Scope.SINGLETON)
                .factory(ArrayConverter.class, Scope.SINGLETON)
                .factory(DateConverter.class, Scope.SINGLETON)
                .factory(NumberConverter.class, Scope.SINGLETON)
                .factory(StringConverter.class, Scope.SINGLETON)

                .factory(ExcludedPatternsChecker.class, DefaultExcludedPatternsChecker.class, Scope.PROTOTYPE)
                .factory(AcceptedPatternsChecker.class, DefaultAcceptedPatternsChecker.class, Scope.PROTOTYPE)
                .factory(NotExcludedAcceptedPatternsChecker.class, DefaultNotExcludedAcceptedPatternsChecker.class, Scope.SINGLETON)

                .factory(QueryStringBuilder.class, StrutsQueryStringBuilder.class, Scope.SINGLETON)
                .factory(QueryStringParser.class, StrutsQueryStringParser.class, Scope.SINGLETON)
                .factory(UrlEncoder.class, StrutsUrlEncoder.class, Scope.SINGLETON)
                .factory(UrlDecoder.class, StrutsUrlDecoder.class, Scope.SINGLETON)

                .factory(ExecutorProvider.class, StrutsExecutorProvider.class, Scope.SINGLETON)

                .factory(CspNonceReader.class, StrutsCspNonceReader.class, Scope.SINGLETON);

        for (Map.Entry<String, Object> entry : DefaultConfiguration.BOOTSTRAP_CONSTANTS.entrySet()) {
            props.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }
}
