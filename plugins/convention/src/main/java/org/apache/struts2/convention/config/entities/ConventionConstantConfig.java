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
package org.apache.struts2.convention.config.entities;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.config.entities.BeanConfig;
import org.apache.struts2.config.entities.ConstantConfig;
import org.apache.struts2.convention.ConventionConstants;

public class ConventionConstantConfig extends ConstantConfig {
    private BeanConfig conventionActionConfigBuilder;
    private BeanConfig conventionActionNameBuilder;
    private BeanConfig conventionResultMapBuilder;
    private BeanConfig conventionInterceptorMapBuilder;
    private BeanConfig conventionConventionsService;
    private Boolean conventionActionNameLowercase;
    private String conventionActionNameSeparator;
    private Set<String> conventionActionSuffix;
    private Boolean conventionClassesReload;
    private String conventionResultPath;
    private String conventionDefaultParentPackage;
    private Boolean conventionRedirectToSlash;
    private Set<String> conventionRelativeResultTypes;
    private Boolean conventionExcludeParentClassLoader;
    private Boolean conventionActionAlwaysMapExecute;
    private Set<String> conventionActionFileProtocols;
    private Boolean conventionActionDisableScanning;
    private List<String> conventionActionIncludeJars;
    private Boolean conventionPackageLocatorsDisable;
    private List<String> conventionActionPackages;
    private Boolean conventionActionCheckImplementsAction;
    private List<String> conventionExcludePackages;
    private List<String> conventionPackageLocators;
    private String conventionPackageLocatorsBasePackage;
    private Boolean conventionActionMapAllMatches;
    private Boolean conventionActionEagerLoading;
    private Boolean conventionResultFlatLayout;

    @Override
    public Map<String, String> getAllAsStringsMap() {
        Map<String, String> map = super.getAllAsStringsMap();

        map.put(ConventionConstants.CONVENTION_ACTION_CONFIG_BUILDER, beanConfToString(conventionActionConfigBuilder));
        map.put(ConventionConstants.CONVENTION_ACTION_NAME_BUILDER, beanConfToString(conventionActionNameBuilder));
        map.put(ConventionConstants.CONVENTION_RESULT_MAP_BUILDER, beanConfToString(conventionResultMapBuilder));
        map.put(ConventionConstants.CONVENTION_INTERCEPTOR_MAP_BUILDER, beanConfToString(conventionInterceptorMapBuilder));
        map.put(ConventionConstants.CONVENTION_CONVENTIONS_SERVICE, beanConfToString(conventionConventionsService));
        map.put(ConventionConstants.CONVENTION_ACTION_NAME_LOWERCASE, Objects.toString(conventionActionNameLowercase, null));
        map.put(ConventionConstants.CONVENTION_ACTION_NAME_SEPARATOR, conventionActionNameSeparator);
        map.put(ConventionConstants.CONVENTION_ACTION_SUFFIX, StringUtils.join(conventionActionSuffix, ','));
        map.put(ConventionConstants.CONVENTION_CLASSES_RELOAD, Objects.toString(conventionClassesReload, null));
        map.put(ConventionConstants.CONVENTION_RESULT_PATH, conventionResultPath);
        map.put(ConventionConstants.CONVENTION_DEFAULT_PARENT_PACKAGE, conventionDefaultParentPackage);
        map.put(ConventionConstants.CONVENTION_REDIRECT_TO_SLASH, Objects.toString(conventionRedirectToSlash, null));
        map.put(ConventionConstants.CONVENTION_RELATIVE_RESULT_TYPES, StringUtils.join(conventionRelativeResultTypes, ','));
        map.put(ConventionConstants.CONVENTION_EXCLUDE_PARENT_CLASS_LOADER, Objects.toString(conventionExcludeParentClassLoader, null));
        map.put(ConventionConstants.CONVENTION_ACTION_ALWAYS_MAP_EXECUTE, Objects.toString(conventionActionAlwaysMapExecute, null));
        map.put(ConventionConstants.CONVENTION_ACTION_FILE_PROTOCOLS, StringUtils.join(conventionActionFileProtocols, ','));
        map.put(ConventionConstants.CONVENTION_ACTION_DISABLE_SCANNING, Objects.toString(conventionActionDisableScanning, null));
        map.put(ConventionConstants.CONVENTION_ACTION_INCLUDE_JARS, StringUtils.join(conventionActionIncludeJars, ','));
        map.put(ConventionConstants.CONVENTION_PACKAGE_LOCATORS_DISABLE, Objects.toString(conventionPackageLocatorsDisable, null));
        map.put(ConventionConstants.CONVENTION_ACTION_PACKAGES, StringUtils.join(conventionActionPackages, ','));
        map.put(ConventionConstants.CONVENTION_ACTION_CHECK_IMPLEMENTS_ACTION, Objects.toString(conventionActionCheckImplementsAction, null));
        map.put(ConventionConstants.CONVENTION_EXCLUDE_PACKAGES, StringUtils.join(conventionExcludePackages, ','));
        map.put(ConventionConstants.CONVENTION_PACKAGE_LOCATORS, StringUtils.join(conventionPackageLocators, ','));
        map.put(ConventionConstants.CONVENTION_PACKAGE_LOCATORS_BASE_PACKAGE, conventionPackageLocatorsBasePackage);
        map.put(ConventionConstants.CONVENTION_ACTION_MAP_ALL_MATCHES, Objects.toString(conventionActionMapAllMatches, null));
        map.put(ConventionConstants.CONVENTION_ACTION_EAGER_LOADING, Objects.toString(conventionActionEagerLoading, null));
        map.put(ConventionConstants.CONVENTION_RESULT_FLAT_LAYOUT, Objects.toString(conventionResultFlatLayout, null));

        return map;
    }

    public BeanConfig getConventionActionConfigBuilder() {
        return conventionActionConfigBuilder;
    }

    public void setConventionActionConfigBuilder(BeanConfig conventionActionConfigBuilder) {
        this.conventionActionConfigBuilder = conventionActionConfigBuilder;
    }

    public void setConventionActionConfigBuilder(Class<?> clazz) {
        this.conventionActionConfigBuilder = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConventionActionNameBuilder() {
        return conventionActionNameBuilder;
    }

    public void setConventionActionNameBuilder(BeanConfig conventionActionNameBuilder) {
        this.conventionActionNameBuilder = conventionActionNameBuilder;
    }

    public void setConventionActionNameBuilder(Class<?> clazz) {
        this.conventionActionNameBuilder = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConventionResultMapBuilder() {
        return conventionResultMapBuilder;
    }

    public void setConventionResultMapBuilder(BeanConfig conventionResultMapBuilder) {
        this.conventionResultMapBuilder = conventionResultMapBuilder;
    }

    public void setConventionResultMapBuilder(Class<?> clazz) {
        this.conventionResultMapBuilder = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConventionInterceptorMapBuilder() {
        return conventionInterceptorMapBuilder;
    }

    public void setConventionInterceptorMapBuilder(BeanConfig conventionInterceptorMapBuilder) {
        this.conventionInterceptorMapBuilder = conventionInterceptorMapBuilder;
    }

    public void setConventionInterceptorMapBuilder(Class<?> clazz) {
        this.conventionInterceptorMapBuilder = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConventionConventionsService() {
        return conventionConventionsService;
    }

    public void setConventionConventionsService(BeanConfig conventionConventionsService) {
        this.conventionConventionsService = conventionConventionsService;
    }

    public void setConventionConventionsService(Class<?> clazz) {
        this.conventionConventionsService = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getConventionActionNameLowercase() {
        return conventionActionNameLowercase;
    }

    public void setConventionActionNameLowercase(Boolean conventionActionNameLowercase) {
        this.conventionActionNameLowercase = conventionActionNameLowercase;
    }

    public String getConventionActionNameSeparator() {
        return conventionActionNameSeparator;
    }

    public void setConventionActionNameSeparator(String conventionActionNameSeparator) {
        this.conventionActionNameSeparator = conventionActionNameSeparator;
    }

    public Set<String> getConventionActionSuffix() {
        return conventionActionSuffix;
    }

    public void setConventionActionSuffix(Set<String> conventionActionSuffix) {
        this.conventionActionSuffix = conventionActionSuffix;
    }

    public Boolean getConventionClassesReload() {
        return conventionClassesReload;
    }

    public void setConventionClassesReload(Boolean conventionClassesReload) {
        this.conventionClassesReload = conventionClassesReload;
    }

    public String getConventionResultPath() {
        return conventionResultPath;
    }

    public void setConventionResultPath(String conventionResultPath) {
        this.conventionResultPath = conventionResultPath;
    }

    public String getConventionDefaultParentPackage() {
        return conventionDefaultParentPackage;
    }

    public void setConventionDefaultParentPackage(String conventionDefaultParentPackage) {
        this.conventionDefaultParentPackage = conventionDefaultParentPackage;
    }

    public Boolean getConventionRedirectToSlash() {
        return conventionRedirectToSlash;
    }

    public void setConventionRedirectToSlash(Boolean conventionRedirectToSlash) {
        this.conventionRedirectToSlash = conventionRedirectToSlash;
    }

    public Set<String> getConventionRelativeResultTypes() {
        return conventionRelativeResultTypes;
    }

    public void setConventionRelativeResultTypes(Set<String> conventionRelativeResultTypes) {
        this.conventionRelativeResultTypes = conventionRelativeResultTypes;
    }

    public Boolean getConventionExcludeParentClassLoader() {
        return conventionExcludeParentClassLoader;
    }

    public void setConventionExcludeParentClassLoader(Boolean conventionExcludeParentClassLoader) {
        this.conventionExcludeParentClassLoader = conventionExcludeParentClassLoader;
    }

    public Boolean getConventionActionAlwaysMapExecute() {
        return conventionActionAlwaysMapExecute;
    }

    public void setConventionActionAlwaysMapExecute(Boolean conventionActionAlwaysMapExecute) {
        this.conventionActionAlwaysMapExecute = conventionActionAlwaysMapExecute;
    }

    public Set<String> getConventionActionFileProtocols() {
        return conventionActionFileProtocols;
    }

    public void setConventionActionFileProtocols(Set<String> conventionActionFileProtocols) {
        this.conventionActionFileProtocols = conventionActionFileProtocols;
    }

    public Boolean getConventionActionDisableScanning() {
        return conventionActionDisableScanning;
    }

    public void setConventionActionDisableScanning(Boolean conventionActionDisableScanning) {
        this.conventionActionDisableScanning = conventionActionDisableScanning;
    }

    public List<String> getConventionActionIncludeJars() {
        return conventionActionIncludeJars;
    }

    public void setConventionActionIncludeJars(List<String> conventionActionIncludeJars) {
        this.conventionActionIncludeJars = conventionActionIncludeJars;
    }

    public Boolean getConventionPackageLocatorsDisable() {
        return conventionPackageLocatorsDisable;
    }

    public void setConventionPackageLocatorsDisable(Boolean conventionPackageLocatorsDisable) {
        this.conventionPackageLocatorsDisable = conventionPackageLocatorsDisable;
    }

    public List<String> getConventionActionPackages() {
        return conventionActionPackages;
    }

    public void setConventionActionPackages(List<String> conventionActionPackages) {
        this.conventionActionPackages = conventionActionPackages;
    }

    public Boolean getConventionActionCheckImplementsAction() {
        return conventionActionCheckImplementsAction;
    }

    public void setConventionActionCheckImplementsAction(Boolean conventionActionCheckImplementsAction) {
        this.conventionActionCheckImplementsAction = conventionActionCheckImplementsAction;
    }

    public List<String> getConventionExcludePackages() {
        return conventionExcludePackages;
    }

    public void setConventionExcludePackages(List<String> conventionExcludePackages) {
        this.conventionExcludePackages = conventionExcludePackages;
    }

    public List<String> getConventionPackageLocators() {
        return conventionPackageLocators;
    }

    public void setConventionPackageLocators(List<String> conventionPackageLocators) {
        this.conventionPackageLocators = conventionPackageLocators;
    }

    public String getConventionPackageLocatorsBasePackage() {
        return conventionPackageLocatorsBasePackage;
    }

    public void setConventionPackageLocatorsBasePackage(String conventionPackageLocatorsBasePackage) {
        this.conventionPackageLocatorsBasePackage = conventionPackageLocatorsBasePackage;
    }

    public Boolean getConventionActionMapAllMatches() {
        return conventionActionMapAllMatches;
    }

    public void setConventionActionMapAllMatches(Boolean conventionActionMapAllMatches) {
        this.conventionActionMapAllMatches = conventionActionMapAllMatches;
    }

    public Boolean getConventionActionEagerLoading() {
        return conventionActionEagerLoading;
    }

    public void setConventionActionEagerLoading(Boolean conventionActionEagerLoading) {
        this.conventionActionEagerLoading = conventionActionEagerLoading;
    }

    public Boolean getConventionResultFlatLayout() {
        return conventionResultFlatLayout;
    }

    public void setConventionResultFlatLayout(Boolean conventionResultFlatLayout) {
        this.conventionResultFlatLayout = conventionResultFlatLayout;
    }
}
