/*
 * $Id$
 *
 * Copyright 2003,2004 The Apache Software Foundation.
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
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.WildcardHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Matches paths against pre-compiled wildcard expressions pulled from
 * action configs. It uses the wildcard matcher from the Apache Cocoon
 * project. Patterns will be matched in the order they exist in the 
 * config file. The first match wins, so more specific patterns should be
 * defined before less specific patterns.
 */
public class ActionConfigMatcher extends AbstractMatcher<ActionConfig> implements Serializable {
   
    /**
     * <p> Finds and precompiles the wildcard patterns from the ActionConfig
     * "path" attributes. ActionConfig's will be evaluated in the order they
     * exist in the config file. Only paths that actually contain a
     * wildcard will be compiled. Patterns will matched strictly.</p>
     *
     * @param configs An array of ActionConfig's to process
     * @deprecated Since 2.1, use {@link #ActionConfigMatcher(PatternMatcher, Map, boolean)} instead
     */
    @Deprecated public ActionConfigMatcher(Map<String, ActionConfig> configs) {
        this(configs, false);
    }
    
    /**
     * <p> Finds and precompiles the wildcard patterns from the ActionConfig
     * "path" attributes. ActionConfig's will be evaluated in the order they
     * exist in the config file. Only paths that actually contain a
     * wildcard will be compiled. </p>
     * 
     * <p>Patterns can optionally be matched "loosely".  When
     * the end of the pattern matches \*[^*]\*$ (wildcard, no wildcard,
     * wildcard), if the pattern fails, it is also matched as if the 
     * last two characters didn't exist.  The goal is to support the 
     * legacy "*!*" syntax, where the "!*" is optional.</p> 
     *
     * @param configs An array of ActionConfig's to process
     * @param looseMatch To loosely match wildcards or not
     * @deprecated Since 2.1, use {@link #ActionConfigMatcher(PatternMatcher, Map, boolean)} instead
     */
    @Deprecated public ActionConfigMatcher(Map<String, ActionConfig> configs,
            boolean looseMatch) {

        this(new WildcardHelper(), configs, looseMatch);
    }
    
    /**
     * <p> Finds and precompiles the wildcard patterns from the ActionConfig
     * "path" attributes. ActionConfig's will be evaluated in the order they
     * exist in the config file. Only paths that actually contain a
     * wildcard will be compiled. </p>
     * 
     * <p>Patterns can optionally be matched "loosely".  When
     * the end of the pattern matches \*[^*]\*$ (wildcard, no wildcard,
     * wildcard), if the pattern fails, it is also matched as if the 
     * last two characters didn't exist.  The goal is to support the 
     * legacy "*!*" syntax, where the "!*" is optional.</p> 
     *
     * @param configs An array of ActionConfig's to process
     * @param looseMatch To loosely match wildcards or not
     */
    public ActionConfigMatcher(PatternMatcher<?> patternMatcher,
            Map<String, ActionConfig> configs,
            boolean looseMatch) {
        super(patternMatcher);
        for (String name : configs.keySet()) {
            addPattern(name, configs.get(name), looseMatch);
        }
    }

    /**
     * <p> Clones the ActionConfig and its children, replacing various
     * properties with the values of the wildcard-matched strings. </p>
     *
     * @param path The requested path
     * @param orig The original ActionConfig
     * @param vars A Map of wildcard-matched strings
     * @return A cloned ActionConfig with appropriate properties replaced with
     *         wildcard-matched values
     */
    @Override public ActionConfig convert(String path, ActionConfig orig,
        Map<String, String> vars) {

        String methodName = convertParam(orig.getMethodName(), vars);
        if (!orig.isAllowedMethod(methodName)) {
            return null;
        }

        String className = convertParam(orig.getClassName(), vars);
        String pkgName = convertParam(orig.getPackageName(), vars);

        Map<String,String> params = replaceParameters(orig.getParams(), vars);

        Map<String,ResultConfig> results = new LinkedHashMap<String,ResultConfig>();
        for (String name : orig.getResults().keySet()) {
            ResultConfig result = orig.getResults().get(name);
            name = convertParam(name, vars);
            ResultConfig r = new ResultConfig.Builder(name, convertParam(result.getClassName(), vars))
                    .addParams(replaceParameters(result.getParams(), vars))
                    .build();
            results.put(name, r);
        }

        List<ExceptionMappingConfig> exs = new ArrayList<ExceptionMappingConfig>();
        for (ExceptionMappingConfig ex : orig.getExceptionMappings()) {
            String name = convertParam(ex.getName(), vars);
            String exClassName = convertParam(ex.getExceptionClassName(), vars);
            String exResult = convertParam(ex.getResult(), vars);
            Map<String,String> exParams = replaceParameters(ex.getParams(), vars);
            ExceptionMappingConfig e = new ExceptionMappingConfig.Builder(name, exClassName, exResult).addParams(exParams).build();
            exs.add(e);
        }

        return new ActionConfig.Builder(pkgName, orig.getName(), className)
                .methodName(methodName)
                .addParams(params)
                .addResultConfigs(results)
                .addInterceptors(orig.getInterceptors())
                .addExceptionMappings(exs)
                .location(orig.getLocation())
                .build();
    }
}
