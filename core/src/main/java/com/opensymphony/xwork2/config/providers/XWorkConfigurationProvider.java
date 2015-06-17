package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.factory.DefaultUnknownHandlerFactory;
import com.opensymphony.xwork2.factory.UnknownHandlerFactory;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.DefaultAcceptedPatternsChecker;
import com.opensymphony.xwork2.security.DefaultExcludedPatternsChecker;
import com.opensymphony.xwork2.DefaultLocaleProvider;
import com.opensymphony.xwork2.DefaultTextProvider;
import com.opensymphony.xwork2.DefaultUnknownHandlerManager;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderSupport;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.impl.ArrayConverter;
import com.opensymphony.xwork2.conversion.impl.CollectionConverter;
import com.opensymphony.xwork2.conversion.impl.DateConverter;
import com.opensymphony.xwork2.conversion.impl.DefaultConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.impl.DefaultConversionFileProcessor;
import com.opensymphony.xwork2.conversion.impl.DefaultConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverterCreator;
import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverterHolder;
import com.opensymphony.xwork2.conversion.impl.InstantiatingNullHandler;
import com.opensymphony.xwork2.conversion.impl.NumberConverter;
import com.opensymphony.xwork2.conversion.impl.StringConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkBasicConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.factory.ActionFactory;
import com.opensymphony.xwork2.factory.ConverterFactory;
import com.opensymphony.xwork2.factory.DefaultActionFactory;
import com.opensymphony.xwork2.factory.DefaultConverterFactory;
import com.opensymphony.xwork2.factory.DefaultInterceptorFactory;
import com.opensymphony.xwork2.factory.DefaultResultFactory;
import com.opensymphony.xwork2.factory.InterceptorFactory;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.ognl.ObjectProxy;
import com.opensymphony.xwork2.ognl.OgnlReflectionContextFactory;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.ognl.OgnlValueStackFactory;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.ognl.accessor.ObjectAccessor;
import com.opensymphony.xwork2.ognl.accessor.ObjectProxyPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkCollectionPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkEnumerationAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkIteratorPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkListPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkMapPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkMethodAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.OgnlTextParser;
import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.WildcardHelper;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.AnnotationActionValidatorManager;
import com.opensymphony.xwork2.validator.DefaultActionValidatorManager;
import com.opensymphony.xwork2.validator.DefaultValidatorFactory;
import com.opensymphony.xwork2.validator.DefaultValidatorFileParser;
import com.opensymphony.xwork2.validator.ValidatorFactory;
import com.opensymphony.xwork2.validator.ValidatorFileParser;
import ognl.MethodAccessor;
import ognl.PropertyAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XWorkConfigurationProvider implements ConfigurationProvider {

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public void loadPackages() throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {

        builder
                .factory(ObjectFactory.class)
                .factory(ActionFactory.class, DefaultActionFactory.class)
                .factory(ResultFactory.class, DefaultResultFactory.class)
                .factory(InterceptorFactory.class, DefaultInterceptorFactory.class)
                .factory(com.opensymphony.xwork2.factory.ValidatorFactory.class, com.opensymphony.xwork2.factory.DefaultValidatorFactory.class)
                .factory(ConverterFactory.class, DefaultConverterFactory.class)
                .factory(UnknownHandlerFactory.class, DefaultUnknownHandlerFactory.class)

                .factory(ActionProxyFactory.class, DefaultActionProxyFactory.class, Scope.SINGLETON)
                .factory(ObjectTypeDeterminer.class, DefaultObjectTypeDeterminer.class, Scope.SINGLETON)

                .factory(XWorkConverter.class, Scope.SINGLETON)
                .factory(XWorkBasicConverter.class, Scope.SINGLETON)
                .factory(ConversionPropertiesProcessor.class, DefaultConversionPropertiesProcessor.class, Scope.SINGLETON)
                .factory(ConversionFileProcessor.class, DefaultConversionFileProcessor.class, Scope.SINGLETON)
                .factory(ConversionAnnotationProcessor.class, DefaultConversionAnnotationProcessor.class, Scope.SINGLETON)
                .factory(TypeConverterCreator.class, DefaultTypeConverterCreator.class, Scope.SINGLETON)
                .factory(TypeConverterHolder.class, DefaultTypeConverterHolder.class, Scope.SINGLETON)

                .factory(FileManager.class, "system", DefaultFileManager.class, Scope.SINGLETON)
                .factory(FileManagerFactory.class, DefaultFileManagerFactory.class, Scope.SINGLETON)
                .factory(ValueStackFactory.class, OgnlValueStackFactory.class, Scope.SINGLETON)
                .factory(ValidatorFactory.class, DefaultValidatorFactory.class, Scope.SINGLETON)
                .factory(ValidatorFileParser.class, DefaultValidatorFileParser.class, Scope.SINGLETON)
                .factory(PatternMatcher.class, WildcardHelper.class, Scope.SINGLETON)
                .factory(ReflectionProvider.class, OgnlReflectionProvider.class, Scope.SINGLETON)
                .factory(ReflectionContextFactory.class, OgnlReflectionContextFactory.class, Scope.SINGLETON)
                .factory(PropertyAccessor.class, CompoundRoot.class.getName(), CompoundRootAccessor.class, Scope.SINGLETON)
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
                .factory(MethodAccessor.class, Object.class.getName(), XWorkMethodAccessor.class, Scope.SINGLETON)
                .factory(MethodAccessor.class, CompoundRoot.class.getName(), CompoundRootAccessor.class, Scope.SINGLETON)

                .factory(TextParser.class, OgnlTextParser.class, Scope.SINGLETON)

                .factory(NullHandler.class, Object.class.getName(), InstantiatingNullHandler.class, Scope.SINGLETON)
                .factory(ActionValidatorManager.class, AnnotationActionValidatorManager.class, Scope.SINGLETON)
                .factory(ActionValidatorManager.class, "no-annotations", DefaultActionValidatorManager.class, Scope.SINGLETON)
                .factory(TextProvider.class, "system", DefaultTextProvider.class, Scope.SINGLETON)
                .factory(TextProvider.class, TextProviderSupport.class, Scope.SINGLETON)
                .factory(LocaleProvider.class, DefaultLocaleProvider.class, Scope.SINGLETON)
                .factory(OgnlUtil.class, Scope.SINGLETON)
                .factory(CollectionConverter.class, Scope.SINGLETON)
                .factory(ArrayConverter.class, Scope.SINGLETON)
                .factory(DateConverter.class, Scope.SINGLETON)
                .factory(NumberConverter.class, Scope.SINGLETON)
                .factory(StringConverter.class, Scope.SINGLETON)

                .factory(ExcludedPatternsChecker.class, DefaultExcludedPatternsChecker.class, Scope.DEFAULT)
                .factory(AcceptedPatternsChecker.class, DefaultAcceptedPatternsChecker.class, Scope.DEFAULT)
        ;

        props.setProperty(XWorkConstants.DEV_MODE, Boolean.FALSE.toString());
        props.setProperty(XWorkConstants.LOG_MISSING_PROPERTIES, Boolean.FALSE.toString());
        props.setProperty(XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE, Boolean.TRUE.toString());
        props.setProperty(XWorkConstants.ENABLE_OGNL_EVAL_EXPRESSION, Boolean.FALSE.toString());
        props.setProperty(XWorkConstants.RELOAD_XML_CONFIGURATION, Boolean.FALSE.toString());
    }

}
