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
package org.apache.struts2.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationUtil;
import org.apache.struts2.config.entities.InterceptorConfig;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.interceptor.Interceptor;
import org.apache.struts2.interceptor.InterceptorParams;
import org.apache.struts2.interceptor.WithLazyParams;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.apache.struts2.util.reflection.ReflectionException;
import org.apache.struts2.util.reflection.ReflectionProvider;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation
 */
public class DefaultInterceptorFactory implements InterceptorFactory {

    private static final Logger LOG = LogManager.getLogger(DefaultInterceptorFactory.class);

    private ObjectFactory objectFactory;
    private ReflectionProvider reflectionProvider;
    private ProviderAllowlist providerAllowlist;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    @Inject
    public void setProviderAllowlist(ProviderAllowlist providerAllowlist) {
        this.providerAllowlist = providerAllowlist;
    }

    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
        String interceptorClassName = interceptorConfig.getClassName();
        Map<String, String> thisInterceptorClassParams = interceptorConfig.getParams();
        Map<String, String> params = (thisInterceptorClassParams == null) ? new HashMap<>() : new HashMap<>(thisInterceptorClassParams);
        params.putAll(interceptorRefParams);

        String message;
        Throwable cause;

        try {
            // interceptor instances are long-lived and used across user sessions, so don't try to pass in any extra context
            Object o = objectFactory.buildBean(interceptorClassName, null);

            if (!(o instanceof Interceptor interceptor)) {
                throw new ConfigurationException("Class [" + interceptorClassName + "] does not implement Interceptor", interceptorConfig);
            }

            // A ${...} param belongs to the invocation, not to the interceptor: it is resolved per
            // request into the params holder. Applying its raw text here would seed the interceptor
            // with an unevaluated literal, or fail conversion outright for a typed property such as
            // maximumSize. Static params still apply, and go on to seed the holder.
            reflectionProvider.setProperties(
                    interceptor instanceof WithLazyParams<?> ? withoutLazyExpressions(params) : params,
                    interceptor);

            interceptor.init();

            if (interceptor instanceof WithLazyParams<?> lazyInterceptor) {
                LOG.debug("Interceptor {} implements {} - expression parameters will be re-evaluated during action invocation",
                        interceptorClassName, WithLazyParams.class.getName());
                InterceptorParams lazyParams = lazyInterceptor.newLazyParams();
                validateLazyParamNames(interceptorConfig, lazyParams, interceptorRefParams);
                allowlistLazyParamsHolder(lazyInterceptor, lazyParams);
            }

            return interceptor;
        } catch (ConfigurationException e) {
            // already carries its own message and location, don't bury it in a generic wrapper
            throw e;
        } catch (InstantiationException e) {
            cause = e;
            message = "Unable to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        } catch (IllegalAccessException e) {
            cause = e;
            message = "IllegalAccessException while attempting to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        } catch (ClassCastException e) {
            cause = e;
            message = "Class [" + interceptorClassName + "] does not implement " + Interceptor.class.getName();
        } catch (Exception e) {
            cause = e;
            message = "Caught Exception while registering Interceptor class " + interceptorClassName;
        } catch (NoClassDefFoundError e) {
            cause = e;
            message = "Could not load class " + interceptorClassName + ". Perhaps it exists but certain dependencies are not available?";
        }

        throw new ConfigurationException(message, cause, interceptorConfig);
    }

    /**
     * Fails configuration when an interceptor-ref param of a {@link WithLazyParams} interceptor is
     * not writable on its params holder.
     * <p>
     * <strong>Only the interceptor-ref params are checked, and that is exactly the set that reaches
     * the holder.</strong> {@link org.apache.struts2.config.providers.InterceptorBuilder} passes the
     * very map it hands to this factory on to the {@code InterceptorMapping} it creates, and
     * {@code DefaultActionInvocation.mergedParams} feeds that map to
     * {@link WithLazyParams.LazyParamInjector#resolveInto}. The params declared on the
     * {@code <interceptor>} definition are deliberately not checked: they never reach the mapping,
     * they are only applied to the interceptor instance a few lines above, and requiring them to
     * exist on the holder too would reject working configurations - {@code <param name="disabled">}
     * on an interceptor definition, for instance, is honoured through
     * {@link org.apache.struts2.interceptor.AbstractInterceptor#shouldIntercept} alone.
     * <p>
     * Without this check an unknown name is only noticed per request, where
     * {@code resolveInto} logs a WARN and notifies the holder - which for
     * {@link org.apache.struts2.interceptor.UploadPolicy} means rejecting every upload of every
     * request. The names are fully known at configuration-parse time, so a typo belongs to startup.
     * The runtime handling stays in place as defence in depth for holders built outside this factory.
     * <p>
     * A param whose name is a compound OGNL expression (it contains a {@code .}) is not checked:
     * only its leading segment would have to be readable rather than writable on the holder, which
     * this simple property check cannot decide. Such names are left to the runtime path.
     */
    private void validateLazyParamNames(InterceptorConfig interceptorConfig, InterceptorParams lazyParams, Map<String, String> interceptorRefParams) {
        if (lazyParams == null || interceptorRefParams == null || interceptorRefParams.isEmpty()) {
            return;
        }
        Class<?> holderClass = lazyParams.getClass();
        for (String paramName : interceptorRefParams.keySet()) {
            if (paramName == null || paramName.indexOf('.') >= 0) {
                LOG.debug("Skipping configuration-time check of compound lazy param name [{}] on holder [{}]",
                        paramName, holderClass.getName());
                continue;
            }
            if (!isWritableOnHolder(holderClass, paramName)) {
                throw new ConfigurationException(String.format(
                        "Param [%s] of interceptor [%s] (%s) is not a writable property of its lazy params holder [%s]."
                                + " Params of a %s interceptor-ref are resolved onto that holder for each invocation,"
                                + " so this one could never be applied - check the interceptor configuration for a typo.",
                        paramName, interceptorConfig.getName(), interceptorConfig.getClassName(),
                        holderClass.getName(), WithLazyParams.class.getSimpleName()), interceptorConfig);
            }
        }
    }

    /**
     * @return true when OGNL could write {@code paramName} on the holder, i.e. there is a setter for
     * it anywhere in the holder's hierarchy, or failing that a public field of that name
     */
    private boolean isWritableOnHolder(Class<?> holderClass, String paramName) {
        try {
            if (reflectionProvider.getSetMethod(holderClass, paramName) != null) {
                return true;
            }
        } catch (IntrospectionException | ReflectionException e) {
            LOG.debug("Could not introspect setter [{}] on lazy params holder [{}], falling back to field lookup",
                    paramName, holderClass.getName(), e);
        }
        Field field = reflectionProvider.getField(holderClass, paramName);
        return field != null && Modifier.isPublic(field.getModifiers());
    }

    /**
     * Allowlists the params holder of a {@link WithLazyParams} interceptor for OGNL member access.
     * <p>
     * {@link WithLazyParams.LazyParamInjector#resolveInto} writes the resolved {@code ${...}} values
     * onto the holder with OGNL. With {@code struts.allowlist.enable=true} (the default) that write
     * is refused unless the holder's class is allowlisted, and the fail-closed handling in
     * {@code resolveInto} would then mark every lazy param unresolved. The interceptor itself is
     * allowlisted by {@code XmlDocConfigurationProvider.allowAndLoadClass} when its config is read;
     * the holder is never named in any configuration, so it has to be registered here.
     * <p>
     * Only the holder's own class hierarchy is registered - its class, its superclasses and the
     * interfaces it implements - because the setter being written may be declared on any of them
     * ({@code disabled} for instance is declared on {@link org.apache.struts2.interceptor.DisableParams}),
     * and {@code SecurityMemberAccess} requires both the target class and the declaring class of the
     * member to be allowlisted. {@link Object} is deliberately dropped from that set: it is a
     * universal supertype that would say nothing about this holder, and it is excluded by default
     * anyway ({@code struts.excludedClasses}), so registering it could only ever mislead a reader.
     * No package is allowlisted and nothing beyond the hierarchy is added.
     * <p>
     * The holder class is used as the registration key so repeated registrations - the factory is a
     * prototype and several interceptors may share a holder type - collapse onto one entry.
     */
    private void allowlistLazyParamsHolder(WithLazyParams<?> lazyInterceptor, InterceptorParams lazyParams) {
        if (providerAllowlist == null) {
            LOG.warn("No ProviderAllowlist available, cannot allowlist the lazy params holder of [{}];" +
                    " lazy params will fail to resolve if the OGNL allowlist is enabled", lazyInterceptor.getClass().getName());
            return;
        }
        if (lazyParams == null) {
            LOG.warn("Interceptor [{}] returned no lazy params holder, nothing to allowlist", lazyInterceptor.getClass().getName());
            return;
        }
        Class<?> holderClass = lazyParams.getClass();
        Set<Class<?>> holderTypes = ConfigurationUtil.getAllClassTypes(holderClass).stream()
                .filter(type -> type != Object.class)
                .collect(Collectors.toSet());
        LOG.debug("Allowlisting lazy params holder [{}] and its supertypes {}", holderClass.getName(), holderTypes);
        providerAllowlist.registerAllowlist(holderClass, holderTypes);
    }

    /**
     * Drops the params carrying a {@code ${...}} expression, which {@link WithLazyParams} resolves per
     * invocation into its params holder instead.
     * <p>
     * The predicate matches {@code WithLazyParams.LazyParamInjector}'s, so a value treated as an
     * expression at resolution time is the same one withheld here.
     *
     * @return the params to apply to the interceptor at configuration time
     */
    private static Map<String, String> withoutLazyExpressions(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(entry -> entry.getValue() == null || !entry.getValue().contains("${"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, second) -> second, LinkedHashMap::new));
    }

}
