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
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;

import java.util.Set;

/**
 * Mock implementation to be used in unittests
 */
public class MockContainer implements Container {

    public void inject(Object o) {

    }

    public <T> T inject(Class<T> implementation) {
        return null;
    }

    public <T> T getInstance(Class<T> type, String name) {
        return null;
    }

    public <T> T getInstance(Class<T> type) {
        return null;
    }

    public Set<String> getInstanceNames(Class<?> type) {
        return null;
    }

    public void setScopeStrategy(Scope.Strategy scopeStrategy) {

    }

    public void removeScopeStrategy() {

    }

}
