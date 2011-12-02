package org.apache.struts2.xwork2.config.providers;

import org.apache.struts2.xwork2.ActionProxyFactory;
import org.apache.struts2.xwork2.DefaultActionProxyFactory;
import org.apache.struts2.xwork2.DefaultTextProvider;
import org.apache.struts2.xwork2.DefaultUnknownHandlerManager;
import org.apache.struts2.xwork2.ObjectFactory;
import org.apache.struts2.xwork2.TextProvider;
import org.apache.struts2.xwork2.TextProviderSupport;
import org.apache.struts2.xwork2.UnknownHandlerManager;
import org.apache.struts2.xwork2.config.Configuration;
import org.apache.struts2.xwork2.config.ConfigurationException;
import org.apache.struts2.xwork2.config.ConfigurationProvider;
import org.apache.struts2.xwork2.conversion.NullHandler;
import org.apache.struts2.xwork2.conversion.ObjectTypeDeterminer;
import org.apache.struts2.xwork2.conversion.impl.DefaultObjectTypeDeterminer;
import org.apache.struts2.xwork2.conversion.impl.InstantiatingNullHandler;
import org.apache.struts2.xwork2.conversion.impl.XWorkBasicConverter;
import org.apache.struts2.xwork2.conversion.impl.XWorkConverter;
import org.apache.struts2.xwork2.inject.ContainerBuilder;
import org.apache.struts2.xwork2.inject.Scope;
import org.apache.struts2.xwork2.ognl.ObjectProxy;
import org.apache.struts2.xwork2.ognl.OgnlReflectionContextFactory;
import org.apache.struts2.xwork2.ognl.OgnlReflectionProvider;
import org.apache.struts2.xwork2.ognl.OgnlUtil;
import org.apache.struts2.xwork2.ognl.OgnlValueStackFactory;
import org.apache.struts2.xwork2.ognl.accessor.CompoundRootAccessor;
import org.apache.struts2.xwork2.ognl.accessor.ObjectAccessor;
import org.apache.struts2.xwork2.ognl.accessor.ObjectProxyPropertyAccessor;
import org.apache.struts2.xwork2.ognl.accessor.XWorkCollectionPropertyAccessor;
import org.apache.struts2.xwork2.ognl.accessor.XWorkEnumerationAccessor;
import org.apache.struts2.xwork2.ognl.accessor.XWorkIteratorPropertyAccessor;
import org.apache.struts2.xwork2.ognl.accessor.XWorkListPropertyAccessor;
import org.apache.struts2.xwork2.ognl.accessor.XWorkMapPropertyAccessor;
import org.apache.struts2.xwork2.ognl.accessor.XWorkMethodAccessor;
import org.apache.struts2.xwork2.util.CompoundRoot;
import org.apache.struts2.xwork2.util.PatternMatcher;
import org.apache.struts2.xwork2.util.ValueStackFactory;
import org.apache.struts2.xwork2.util.WildcardHelper;
import org.apache.struts2.xwork2.util.location.LocatableProperties;
import org.apache.struts2.xwork2.util.reflection.ReflectionContextFactory;
import org.apache.struts2.xwork2.util.reflection.ReflectionProvider;
import org.apache.struts2.xwork2.validator.ActionValidatorManager;
import org.apache.struts2.xwork2.validator.AnnotationActionValidatorManager;
import org.apache.struts2.xwork2.validator.DefaultActionValidatorManager;
import org.apache.struts2.xwork2.validator.DefaultValidatorFactory;
import org.apache.struts2.xwork2.validator.DefaultValidatorFileParser;
import org.apache.struts2.xwork2.validator.ValidatorFactory;
import org.apache.struts2.xwork2.validator.ValidatorFileParser;
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

        builder.factory(ObjectFactory.class)
                .factory(ActionProxyFactory.class, DefaultActionProxyFactory.class, Scope.SINGLETON)
                .factory(ObjectTypeDeterminer.class, DefaultObjectTypeDeterminer.class, Scope.SINGLETON)
                .factory(XWorkConverter.class, Scope.SINGLETON)
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
                .factory(NullHandler.class, Object.class.getName(), InstantiatingNullHandler.class, Scope.SINGLETON)
                .factory(ActionValidatorManager.class, AnnotationActionValidatorManager.class, Scope.SINGLETON)
                .factory(ActionValidatorManager.class, "no-annotations", DefaultActionValidatorManager.class, Scope.SINGLETON)
                .factory(TextProvider.class, "system", DefaultTextProvider.class, Scope.SINGLETON)
                .factory(TextProvider.class, TextProviderSupport.class, Scope.SINGLETON)
                .factory(OgnlUtil.class, Scope.SINGLETON)
                .factory(XWorkBasicConverter.class, Scope.SINGLETON);
        props.setProperty("devMode", Boolean.FALSE.toString());
        props.setProperty("logMissingProperties", Boolean.FALSE.toString());
        props.setProperty("enableOGNLExpressionCache", Boolean.TRUE.toString());
    }

}
