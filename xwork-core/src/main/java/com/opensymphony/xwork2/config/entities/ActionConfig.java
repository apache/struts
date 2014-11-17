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
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Contains everything needed to configure and execute an action:
 * <ul>
 * <li>methodName - the method name to execute on the action. If this is null, the Action will be cast to the Action
 * Interface and the execute() method called</li>
 * <li>clazz - the class name for the action</li>
 * <li>params - the params to be set for this action just before execution</li>
 * <li>results - the result map {String -> View class}</li>
 * <li>resultParameters - params for results {String -> Map}</li>
 * <li>typeConverter - the Ognl TypeConverter to use when getting/setting properties</li>
 * </ul>
 *
 * @author Mike
 * @author Rainer Hermanns
 * @version $Revision$
 */
public class ActionConfig extends Located implements Serializable {

    public static final String DEFAULT_METHOD = "execute";
    public static final String WILDCARD = "*";

    protected List<InterceptorMapping> interceptors; // a list of interceptorMapping Objects eg. List<InterceptorMapping>
    protected Map<String,String> params;
    protected Map<String, ResultConfig> results;
    protected List<ExceptionMappingConfig> exceptionMappings;
    protected String className;
    protected String methodName;
    protected String packageName;
    protected String name;
    protected Set<String> allowedMethods;

    protected ActionConfig(String packageName, String name, String className) {
        this.packageName = packageName;
        this.name = name;
        this.className = className;
        params = new LinkedHashMap<String, String>();
        results = new LinkedHashMap<String, ResultConfig>();
        interceptors = new ArrayList<InterceptorMapping>();
        exceptionMappings = new ArrayList<ExceptionMappingConfig>();
        allowedMethods = new HashSet<String>();
    }

    /**
     * Clones an ActionConfig, copying data into new maps and lists
     * @param orig The ActionConfig to clone
     * @Since 2.1
     */
    protected ActionConfig(ActionConfig orig) {
        this.name = orig.name;
        this.className = orig.className;
        this.methodName = orig.methodName;
        this.packageName = orig.packageName;
        this.params = new LinkedHashMap<String,String>(orig.params);
        this.interceptors = new ArrayList<InterceptorMapping>(orig.interceptors);
        this.results = new LinkedHashMap<String,ResultConfig>(orig.results);
        this.exceptionMappings = new ArrayList<ExceptionMappingConfig>(orig.exceptionMappings);
        this.allowedMethods = new HashSet<String>(orig.allowedMethods);
        this.location = orig.location;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public List<ExceptionMappingConfig> getExceptionMappings() {
        return exceptionMappings;
    }

    public List<InterceptorMapping> getInterceptors() {
        return interceptors;
    }

    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Returns name of the action method
     *
     * @return name of the method to execute
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @return Returns the packageName.
     */
    public String getPackageName() {
        return packageName;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, ResultConfig> getResults() {
        return results;
    }

    public boolean isAllowedMethod(String method) {
        if (allowedMethods.size() == 1 && WILDCARD.equals(allowedMethods.iterator().next())) {
            return true;
        } else {
            return method.equals(methodName != null ? methodName : DEFAULT_METHOD) || allowedMethods.contains(method);
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ActionConfig)) {
            return false;
        }

        final ActionConfig actionConfig = (ActionConfig) o;

        if ((className != null) ? (!className.equals(actionConfig.className)) : (actionConfig.className != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(actionConfig.name)) : (actionConfig.name != null)) {
            return false;
        }

        if ((interceptors != null) ? (!interceptors.equals(actionConfig.interceptors)) : (actionConfig.interceptors != null))
        {
            return false;
        }

        if ((methodName != null) ? (!methodName.equals(actionConfig.methodName)) : (actionConfig.methodName != null)) {
            return false;
        }

        if ((params != null) ? (!params.equals(actionConfig.params)) : (actionConfig.params != null)) {
            return false;
        }

        if ((results != null) ? (!results.equals(actionConfig.results)) : (actionConfig.results != null)) {
            return false;
        }

        if ((allowedMethods != null) ? (!allowedMethods.equals(actionConfig.allowedMethods)) : (actionConfig.allowedMethods != null)) {
            return false;
        }

        return true;
    }

    @Override public int hashCode() {
        int result;
        result = (interceptors != null ? interceptors.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (results != null ? results.hashCode() : 0);
        result = 31 * result + (exceptionMappings != null ? exceptionMappings.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (allowedMethods != null ? allowedMethods.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ActionConfig ");
        sb.append(name).append(" (");
        sb.append(className);
        if (methodName != null) {
            sb.append(".").append(methodName).append("()");
        }
        sb.append(")");
        sb.append(" - ").append(location);
        sb.append("}");
        return sb.toString();
    }

    /**
     * The builder for this object.  An instance of this object is the only way to construct a new instance.  The
     * purpose is to enforce the immutability of the object.  The methods are structured in a way to support chaining.
     * After setting any values you need, call the {@link #build()} method to create the object.
     */
    public static class Builder implements InterceptorListHolder{

        protected ActionConfig target;
        private boolean gotMethods;

        public Builder(ActionConfig toClone) {
            target = new ActionConfig(toClone);
            addAllowedMethod(toClone.getAllowedMethods());
        }

        public Builder(String packageName, String name, String className) {
            target = new ActionConfig(packageName, name, className);
        }

        public Builder packageName(String name) {
            target.packageName = name;
            return this;
        }

        public Builder name(String name) {
            target.name = name;
            return this;
        }

        public Builder className(String name) {
            target.className = name;
            return this;
        }

        public Builder defaultClassName(String name) {
            if (StringUtils.isEmpty(target.className)) {
                target.className = name;
            }
            return this;
        }

        public Builder methodName(String method) {
            target.methodName = method;
            return this;
        }

        public Builder addExceptionMapping(ExceptionMappingConfig exceptionMapping) {
            target.exceptionMappings.add(exceptionMapping);
            return this;
        }

        public Builder addExceptionMappings(Collection<? extends ExceptionMappingConfig> mappings) {
            target.exceptionMappings.addAll(mappings);
            return this;
        }

        public Builder exceptionMappings(Collection<? extends ExceptionMappingConfig> mappings) {
            target.exceptionMappings.clear();
            target.exceptionMappings.addAll(mappings);
            return this;
        }

        public Builder addInterceptor(InterceptorMapping interceptor) {
            target.interceptors.add(interceptor);
            return this;
        }

        public Builder addInterceptors(List<InterceptorMapping> interceptors) {
            target.interceptors.addAll(interceptors);
            return this;
        }

        public Builder interceptors(List<InterceptorMapping> interceptors) {
            target.interceptors.clear();
            target.interceptors.addAll(interceptors);
            return this;
        }

        public Builder addParam(String name, String value) {
            target.params.put(name, value);
            return this;
        }

        public Builder addParams(Map<String,String> params) {
            target.params.putAll(params);
            return this;
        }

        public Builder addResultConfig(ResultConfig resultConfig) {
            target.results.put(resultConfig.getName(), resultConfig);
            return this;
        }

        public Builder addResultConfigs(Collection<ResultConfig> configs) {
            for (ResultConfig rc : configs) {
                target.results.put(rc.getName(), rc);
            }
            return this;
        }

        public Builder addResultConfigs(Map<String,ResultConfig> configs) {
            target.results.putAll(configs);
            return this;
        }

        public Builder addAllowedMethod(String methodName) {
            target.allowedMethods.add(methodName);
            return this;
        }

        public Builder addAllowedMethod(Collection<String> methods) {
            if (methods != null) {
                gotMethods = true;
                target.allowedMethods.addAll(methods);
            }
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public ActionConfig build() {
            embalmTarget();
            ActionConfig result = target;
            target = new ActionConfig(target);
            return result;
        }

        protected void embalmTarget() {
            if (!gotMethods && target.allowedMethods.isEmpty()) {
                target.allowedMethods.add(WILDCARD);
            }

            target.params = Collections.unmodifiableMap(target.params);
            target.results = Collections.unmodifiableMap(target.results);
            target.interceptors = Collections.unmodifiableList(target.interceptors);
            target.exceptionMappings = Collections.unmodifiableList(target.exceptionMappings);
            target.allowedMethods = Collections.unmodifiableSet(target.allowedMethods);
        }
    }
}
