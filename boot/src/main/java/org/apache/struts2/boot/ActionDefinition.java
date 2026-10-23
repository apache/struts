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
package org.apache.struts2.boot;

import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.interceptor.httpmethod.HttpMethod;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ActionDefinition {

    public record ResultDefinition(Kind kind, String value) {

        public enum Kind {
            TEMPLATE, REDIRECT_ACTION
        }
    }

    private final String name;
    private final Class<?> actionClass;
    private final Map<String, ResultDefinition> results = new LinkedHashMap<>();
    private final Set<HttpMethod> httpMethods = EnumSet.noneOf(HttpMethod.class);
    private String methodName = "execute";
    private String interceptorStack;

    ActionDefinition(String name, Class<?> actionClass) {
        this.name = name;
        this.actionClass = actionClass;
    }

    public ActionDefinition method(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public ActionDefinition success(String templatePath) {
        return result("success", templatePath);
    }

    public ActionDefinition result(String name, String templatePath) {
        if (templatePath == null || !templatePath.endsWith(".ftl")) {
            throw new ConfigurationException("Action '" + this.name + "' result '" + name + "' uses '" + templatePath
                    + "': Struts Boot renders FreeMarker templates only, the path must end with .ftl");
        }
        results.put(name, new ResultDefinition(ResultDefinition.Kind.TEMPLATE, templatePath));
        return this;
    }

    public ActionDefinition redirectAction(String resultName, String actionName) {
        results.put(resultName, new ResultDefinition(ResultDefinition.Kind.REDIRECT_ACTION, actionName));
        return this;
    }

    public ActionDefinition httpMethods(HttpMethod... methods) {
        Collections.addAll(httpMethods, methods);
        return this;
    }

    public ActionDefinition interceptors(String stackName) {
        this.interceptorStack = stackName;
        return this;
    }

    public String name() {
        return name;
    }

    public Class<?> actionClass() {
        return actionClass;
    }

    public String methodName() {
        return methodName;
    }

    public Map<String, ResultDefinition> results() {
        return Collections.unmodifiableMap(results);
    }

    public Set<HttpMethod> httpMethodSet() {
        return Collections.unmodifiableSet(httpMethods);
    }

    public Optional<String> interceptorStack() {
        return Optional.ofNullable(interceptorStack);
    }
}
