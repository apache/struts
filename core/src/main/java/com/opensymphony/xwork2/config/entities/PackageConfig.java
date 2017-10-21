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
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;


/**
 * Configuration for Package.
 *
 * <p>
 * In the xml configuration file this is defined as the <code>package</code> tag.
 * </p>
 *
 * @author Rainer Hermanns
 * @version $Revision$
 */
public class PackageConfig extends Located implements Comparable, Serializable, InterceptorLocator {

    private static final Logger LOG = LogManager.getLogger(PackageConfig.class);

    protected Map<String, ActionConfig> actionConfigs;
    protected Map<String, ResultConfig> globalResultConfigs;
    protected Set<String> globalAllowedMethods;
    protected Map<String, Object> interceptorConfigs;
    protected Map<String, ResultTypeConfig> resultTypeConfigs;
    protected List<ExceptionMappingConfig> globalExceptionMappingConfigs;
    protected List<PackageConfig> parents;
    protected String defaultInterceptorRef;
    protected String defaultActionRef;
    protected String defaultResultType;
    protected String defaultClassRef;
    protected String name;
    protected String namespace = "";
    protected boolean isAbstract = false;
    protected boolean needsRefresh;
    protected boolean strictMethodInvocation = true;

    protected PackageConfig(String name) {
        this.name = name;
        actionConfigs = new LinkedHashMap<>();
        globalResultConfigs = new LinkedHashMap<>();
        globalAllowedMethods = new HashSet<>();
        interceptorConfigs = new LinkedHashMap<>();
        resultTypeConfigs = new LinkedHashMap<>();
        globalExceptionMappingConfigs = new ArrayList<>();
        parents = new ArrayList<>();
    }

    protected PackageConfig(PackageConfig orig) {
        this.defaultInterceptorRef = orig.defaultInterceptorRef;
        this.defaultActionRef = orig.defaultActionRef;
        this.defaultResultType = orig.defaultResultType;
        this.defaultClassRef = orig.defaultClassRef;
        this.name = orig.name;
        this.namespace = orig.namespace;
        this.isAbstract = orig.isAbstract;
        this.needsRefresh = orig.needsRefresh;
        this.actionConfigs = new LinkedHashMap<>(orig.actionConfigs);
        this.globalResultConfigs = new LinkedHashMap<>(orig.globalResultConfigs);
        this.globalAllowedMethods = new LinkedHashSet<>(orig.globalAllowedMethods);
        this.interceptorConfigs = new LinkedHashMap<>(orig.interceptorConfigs);
        this.resultTypeConfigs = new LinkedHashMap<>(orig.resultTypeConfigs);
        this.globalExceptionMappingConfigs = new ArrayList<>(orig.globalExceptionMappingConfigs);
        this.parents = new ArrayList<>(orig.parents);
        this.location = orig.location;
        this.strictMethodInvocation = orig.strictMethodInvocation;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public Map<String, ActionConfig> getActionConfigs() {
        return actionConfigs;
    }

    /**
     * returns the Map of all the ActionConfigs available in the current package.
     * ActionConfigs defined in ancestor packages will be included in this Map.
     *
     * @return a Map of ActionConfig Objects with the action name as the key
     * @see ActionConfig
     */
    public Map<String, ActionConfig> getAllActionConfigs() {
        Map<String, ActionConfig> retMap = new LinkedHashMap<>();

        if (!parents.isEmpty()) {
            for (PackageConfig parent : parents) {
                retMap.putAll(parent.getAllActionConfigs());
            }
        }

        retMap.putAll(getActionConfigs());

        return retMap;
    }

    /**
     * returns the Map of all the global ResultConfigs available in the current package.
     * Global ResultConfigs defined in ancestor packages will be included in this Map.
     *
     * @return a Map of Result Objects with the result name as the key
     * @see ResultConfig
     */
    public Map<String, ResultConfig> getAllGlobalResults() {
        Map<String, ResultConfig> retMap = new LinkedHashMap<>();

        if (!parents.isEmpty()) {
            for (PackageConfig parentConfig : parents) {
                retMap.putAll(parentConfig.getAllGlobalResults());
            }
        }

        retMap.putAll(getGlobalResultConfigs());

        return retMap;
    }

    /**
     * returns the Map of all InterceptorConfigs and InterceptorStackConfigs available in the current package.
     * InterceptorConfigs defined in ancestor packages will be included in this Map.
     *
     * @return a Map of InterceptorConfig and InterceptorStackConfig Objects with the ref-name as the key
     * @see InterceptorConfig
     * @see InterceptorStackConfig
     */
    public Map<String, Object> getAllInterceptorConfigs() {
        Map<String, Object> retMap = new LinkedHashMap<>();

        if (!parents.isEmpty()) {
            for (PackageConfig parentContext : parents) {
                retMap.putAll(parentContext.getAllInterceptorConfigs());
            }
        }

        retMap.putAll(getInterceptorConfigs());

        return retMap;
    }

    /**
     * returns the Map of all the ResultTypeConfigs available in the current package.
     * ResultTypeConfigs defined in ancestor packages will be included in this Map.
     *
     * @return a Map of ResultTypeConfig Objects with the result type name as the key
     * @see ResultTypeConfig
     */
    public Map<String, ResultTypeConfig> getAllResultTypeConfigs() {
        Map<String, ResultTypeConfig> retMap = new LinkedHashMap<>();

        if (!parents.isEmpty()) {
            for (PackageConfig parentContext : parents) {
                retMap.putAll(parentContext.getAllResultTypeConfigs());
            }
        }

        retMap.putAll(getResultTypeConfigs());

        return retMap;
    }

    /**
     * returns the List of all the ExceptionMappingConfigs available in the current package.
     * ExceptionMappingConfigs defined in ancestor packages will be included in this list.
     *
     * @return a List of ExceptionMappingConfigs Objects with the result type name as the key
     * @see ExceptionMappingConfig
     */
    public List<ExceptionMappingConfig> getAllExceptionMappingConfigs() {
        List<ExceptionMappingConfig> allExceptionMappings = new ArrayList<>();

        if (!parents.isEmpty()) {
            for (PackageConfig parentContext : parents) {
                allExceptionMappings.addAll(parentContext.getAllExceptionMappingConfigs());
            }
        }

        allExceptionMappings.addAll(getGlobalExceptionMappingConfigs());

        return allExceptionMappings;
    }


    public String getDefaultInterceptorRef() {
        return defaultInterceptorRef;
    }

    public String getDefaultActionRef() {
        return defaultActionRef;
    }

    public String getDefaultClassRef() {
        if ((defaultClassRef == null) && !parents.isEmpty()) {
            for (PackageConfig parent : parents) {
                String parentDefault = parent.getDefaultClassRef();
                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }
        return defaultClassRef;
    }

    /**
     * @return the default result type for this package.
     */
    public String getDefaultResultType() {
        return defaultResultType;
    }

    /**
     * @return the default interceptor-ref name. If this is not set on this PackageConfig, it searches the parent
     * PackageConfigs in order until it finds one.
     */
    public String getFullDefaultInterceptorRef() {
        if ((defaultInterceptorRef == null) && !parents.isEmpty()) {
            for (PackageConfig parent : parents) {
                String parentDefault = parent.getFullDefaultInterceptorRef();

                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }

        return defaultInterceptorRef;
    }

    /**
     * @return the default action-ref name. If this is not set on this PackageConfig, it searches the parent
     * PackageConfigs in order until it finds one.
     */
    public String getFullDefaultActionRef() {
        if ((defaultActionRef == null) && !parents.isEmpty()) {
            for (PackageConfig parent : parents) {
                String parentDefault = parent.getFullDefaultActionRef();

                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }
        return defaultActionRef;
    }

    /**
     * <p>
     * Returns the default result type for this package.
     * </p>
     *
     * <p>
     * If there is no default result type, but this package has parents - we will try to
     * look up the default result type of a parent.
     * </p>
     *
     * @return the default result type for this package.
     */
    public String getFullDefaultResultType() {
        if ((defaultResultType == null) && !parents.isEmpty()) {
            for (PackageConfig parent : parents) {
                String parentDefault = parent.getFullDefaultResultType();

                if (parentDefault != null) {
                    return parentDefault;
                }
            }
        }

        return defaultResultType;
    }

    /**
     * gets the global ResultConfigs local to this package
     *
     * @return a Map of ResultConfig objects keyed by result name
     * @see ResultConfig
     */
    public Map<String, ResultConfig> getGlobalResultConfigs() {
        return globalResultConfigs;
    }

    /**
     * gets the InterceptorConfigs and InterceptorStackConfigs local to this package
     *
     * @return a Map of InterceptorConfig and InterceptorStackConfig objects keyed by ref-name
     * @see InterceptorConfig
     * @see InterceptorStackConfig
     */
    public Map<String, Object> getInterceptorConfigs() {
        return interceptorConfigs;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<PackageConfig> getParents() {
        return new ArrayList<>(parents);
    }

    /**
     * gets the ResultTypeConfigs local to this package
     *
     * @return a Map of ResultTypeConfig objects keyed by result name
     * @see ResultTypeConfig
     */
    public Map<String, ResultTypeConfig> getResultTypeConfigs() {
        return resultTypeConfigs;
    }

    public boolean isNeedsRefresh() {
        return needsRefresh;
    }

    /**
     * gets the ExceptionMappingConfigs local to this package
     *
     * @return a Map of ExceptionMappingConfig objects keyed by result name
     * @see ExceptionMappingConfig
     */
    public List<ExceptionMappingConfig> getGlobalExceptionMappingConfigs() {
        return globalExceptionMappingConfigs;
    }

    /**
     * gets the GlobalAllowedMethods local to this package
     *
     * @return a Set of method names allowed to be executed if strict method invocation is enabled
     */
    public Set<String> getGlobalAllowedMethods() { return Collections.unmodifiableSet(globalAllowedMethods); }

    public boolean isStrictMethodInvocation() {
        return strictMethodInvocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageConfig that = (PackageConfig) o;

        if (isAbstract != that.isAbstract) return false;
        if (needsRefresh != that.needsRefresh) return false;
        if (strictMethodInvocation != that.strictMethodInvocation) return false;
        if (actionConfigs != null ? !actionConfigs.equals(that.actionConfigs) : that.actionConfigs != null)
            return false;
        if (globalResultConfigs != null ? !globalResultConfigs.equals(that.globalResultConfigs) : that.globalResultConfigs != null)
            return false;
        if (globalAllowedMethods != null ? !globalAllowedMethods.equals(that.globalAllowedMethods) : that.globalAllowedMethods != null)
            return false;
        if (interceptorConfigs != null ? !interceptorConfigs.equals(that.interceptorConfigs) : that.interceptorConfigs != null)
            return false;
        if (resultTypeConfigs != null ? !resultTypeConfigs.equals(that.resultTypeConfigs) : that.resultTypeConfigs != null)
            return false;
        if (globalExceptionMappingConfigs != null ? !globalExceptionMappingConfigs.equals(that.globalExceptionMappingConfigs) : that.globalExceptionMappingConfigs != null)
            return false;
        if (parents != null ? !parents.equals(that.parents) : that.parents != null) return false;
        if (defaultInterceptorRef != null ? !defaultInterceptorRef.equals(that.defaultInterceptorRef) : that.defaultInterceptorRef != null)
            return false;
        if (defaultActionRef != null ? !defaultActionRef.equals(that.defaultActionRef) : that.defaultActionRef != null)
            return false;
        if (defaultResultType != null ? !defaultResultType.equals(that.defaultResultType) : that.defaultResultType != null)
            return false;
        if (defaultClassRef != null ? !defaultClassRef.equals(that.defaultClassRef) : that.defaultClassRef != null)
            return false;
        if (!name.equals(that.name)) return false;
        return !(namespace != null ? !namespace.equals(that.namespace) : that.namespace != null);

    }

    @Override
    public int hashCode() {
        int result = actionConfigs != null ? actionConfigs.hashCode() : 0;
        result = 31 * result + (globalResultConfigs != null ? globalResultConfigs.hashCode() : 0);
        result = 31 * result + (globalAllowedMethods != null ? globalAllowedMethods.hashCode() : 0);
        result = 31 * result + (interceptorConfigs != null ? interceptorConfigs.hashCode() : 0);
        result = 31 * result + (resultTypeConfigs != null ? resultTypeConfigs.hashCode() : 0);
        result = 31 * result + (globalExceptionMappingConfigs != null ? globalExceptionMappingConfigs.hashCode() : 0);
        result = 31 * result + (parents != null ? parents.hashCode() : 0);
        result = 31 * result + (defaultInterceptorRef != null ? defaultInterceptorRef.hashCode() : 0);
        result = 31 * result + (defaultActionRef != null ? defaultActionRef.hashCode() : 0);
        result = 31 * result + (defaultResultType != null ? defaultResultType.hashCode() : 0);
        result = 31 * result + (defaultClassRef != null ? defaultClassRef.hashCode() : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (isAbstract ? 1 : 0);
        result = 31 * result + (needsRefresh ? 1 : 0);
        result = 31 * result + (strictMethodInvocation ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PackageConfig: [" + name + "] for namespace [" + namespace + "] with parents [" + parents + "]";
    }

    public int compareTo(Object o) {
        PackageConfig other = (PackageConfig) o;
        String full = namespace + "!" + name;
        String otherFull = other.namespace + "!" + other.name;

        // note, this isn't perfect (could come from different parents), but it is "good enough"
        return full.compareTo(otherFull);
    }

    public Object getInterceptorConfig(String name) {
        return getAllInterceptorConfigs().get(name);
    }

    /**
     * The builder for this object.  An instance of this object is the only way to construct a new instance.  The
     * purpose is to enforce the immutability of the object.  The methods are structured in a way to support chaining.
     * After setting any values you need, call the {@link #build()} method to create the object.
     */
    public static class Builder implements InterceptorLocator {

        protected PackageConfig target;
        private boolean strictDMI = true;

        public Builder(String name) {
            target = new PackageConfig(name);
        }

        public Builder(PackageConfig config) {
            target = new PackageConfig(config);
        }

        public Builder name(String name) {
            target.name = name;
            return this;
        }

        public Builder isAbstract(boolean isAbstract) {
            target.isAbstract = isAbstract;
            return this;
        }

        public Builder defaultInterceptorRef(String name) {
            target.defaultInterceptorRef = name;
            return this;
        }

        public Builder defaultActionRef(String name) {
            target.defaultActionRef = name;
            return this;
        }

        public Builder defaultClassRef(String defaultClassRef) {
            target.defaultClassRef = defaultClassRef;
            return this;
        }

        /**
         * sets the default Result type for this package
         *
         * @param defaultResultType set the default result type
         *
         * @return this builder
         */
        public Builder defaultResultType(String defaultResultType) {
            target.defaultResultType = defaultResultType;
            return this;
        }

        public Builder namespace(String namespace) {
            if (namespace == null) {
                target.namespace = "";
            } else {
                target.namespace = namespace;
            }
            return this;
        }

        public Builder needsRefresh(boolean needsRefresh) {
            target.needsRefresh = needsRefresh;
            return this;
        }

        public Builder addActionConfig(String name, ActionConfig action) {
            target.actionConfigs.put(name, action);
            return this;
        }

        public Builder addParents(List<PackageConfig> parents) {
            for (PackageConfig config : parents) {
                addParent(config);
            }
            return this;
        }

        public Builder addGlobalResultConfig(ResultConfig resultConfig) {
            target.globalResultConfigs.put(resultConfig.getName(), resultConfig);
            return this;
        }

        public Builder addGlobalResultConfigs(Map<String, ResultConfig> resultConfigs) {
            target.globalResultConfigs.putAll(resultConfigs);
            return this;
        }

        public Set<String> getGlobalAllowedMethods() {
            Set <String> allowedMethods = target.globalAllowedMethods;
            allowedMethods.addAll(getParentsAllowedMethods(target.parents));
            return Collections.unmodifiableSet(allowedMethods);
        }

        public Set<String> getParentsAllowedMethods(List<PackageConfig> parents) {
            Set<String> allowedMethods = new HashSet<>();
            for (PackageConfig parent : parents) {
                allowedMethods.addAll(parent.globalAllowedMethods);
                allowedMethods.addAll(getParentsAllowedMethods(parent.getParents()));
            }
            return allowedMethods;
        }

        public Builder addGlobalAllowedMethods(Set<String> allowedMethods) {
            target.globalAllowedMethods.addAll(allowedMethods);
            return this;
        }

        public Builder addExceptionMappingConfig(ExceptionMappingConfig exceptionMappingConfig) {
            target.globalExceptionMappingConfigs.add(exceptionMappingConfig);
            return this;
        }

        public Builder addGlobalExceptionMappingConfigs(List<ExceptionMappingConfig> exceptionMappingConfigs) {
            target.globalExceptionMappingConfigs.addAll(exceptionMappingConfigs);
            return this;
        }

        public Builder addInterceptorConfig(InterceptorConfig config) {
            target.interceptorConfigs.put(config.getName(), config);
            return this;
        }

        public Builder addInterceptorStackConfig(InterceptorStackConfig config) {
            target.interceptorConfigs.put(config.getName(), config);
            return this;
        }

        public Builder addParent(PackageConfig parent) {
            target.parents.add(0, parent);
            return this;
        }

        public Builder addResultTypeConfig(ResultTypeConfig config) {
            target.resultTypeConfigs.put(config.getName(), config);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public boolean isNeedsRefresh() {
            return target.needsRefresh;
        }

        public String getDefaultClassRef() {
            return target.defaultClassRef;
        }

        public String getName() {
            return target.name;
        }

        public String getNamespace() {
            return target.namespace;
        }

        public String getFullDefaultResultType() {
            return target.getFullDefaultResultType();
        }

        public ResultTypeConfig getResultType(String type) {
            return target.getAllResultTypeConfigs().get(type);
        }

        public Object getInterceptorConfig(String name) {
            return target.getAllInterceptorConfigs().get(name);
        }

        public Builder strictMethodInvocation(boolean strict) {
            target.strictMethodInvocation = strict;
            return this;
        }

        public boolean isStrictMethodInvocation() {
            return target.strictMethodInvocation;
        }

        public PackageConfig build() {
            target.actionConfigs = Collections.unmodifiableMap(target.actionConfigs);
            target.globalResultConfigs = Collections.unmodifiableMap(target.globalResultConfigs);
            target.interceptorConfigs = Collections.unmodifiableMap(target.interceptorConfigs);
            target.resultTypeConfigs = Collections.unmodifiableMap(target.resultTypeConfigs);
            target.globalExceptionMappingConfigs = Collections.unmodifiableList(target.globalExceptionMappingConfigs);
            target.parents = Collections.unmodifiableList(target.parents);
            PackageConfig result = target;
            target = new PackageConfig(result);
            return result;
        }

        @Override
        public String toString() {
            return "[BUILDER] " + target.toString();
        }
    }

}
