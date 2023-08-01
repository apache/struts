/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.ognl;

import ognl.OgnlContext;
import ognl.PropertyAccessor;
import org.apache.tiles.request.Request;

import java.util.Map;

/**
 * Accesses attributes in any scope.
 */
public class AnyScopePropertyAccessor implements PropertyAccessor {

    @Override
    public Object getProperty(Map context, Object target, Object name) {
        Request request = (Request) target;
        String attributeName = (String) name;
        for (String scopeName : request.getAvailableScopes()) {
            Map<String, Object> scope = request.getContext(scopeName);
            if (scope.containsKey(attributeName)) {
                return scope.get(attributeName);
            }
        }
        return null;
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        Request request = (Request) target;
        String attributeName = (String) index;
        for (String scopeName : request.getAvailableScopes()) {
            Map<String, Object> scope = request.getContext(scopeName);
            if (scope.containsKey(attributeName)) {
                return ".getContext(\"" + scopeName + "\").get(index)";
            }
        }
        return null;
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        Request request = (Request) target;
        String attributeName = (String) index;
        String[] availableScopes = request.getAvailableScopes().toArray(new String[0]);
        for (String scopeName : availableScopes) {
            Map<String, Object> scope = request.getContext(scopeName);
            if (scope.containsKey(attributeName)) {
                return ".getContext(\"" + scopeName + "\").put(index, target)";
            }
        }
        return ".getContext(\"" + availableScopes[0] + "\").put(index, target)";
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) {
        Request request = (Request) target;
        String attributeName = (String) name;
        String[] availableScopes = request.getAvailableScopes().toArray(new String[0]);
        for (String scopeName : availableScopes) {
            Map<String, Object> scope = request.getContext(scopeName);
            if (scope.containsKey(attributeName)) {
                scope.put(attributeName, value);
                return;
            }
        }
        if (availableScopes.length > 0) {
            request.getContext(availableScopes[0]).put(attributeName, value);
        }
    }

}
