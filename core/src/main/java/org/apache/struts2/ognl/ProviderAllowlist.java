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
package org.apache.struts2.ognl;

import com.opensymphony.xwork2.config.ConfigurationProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Allows registration of classes that should be allowed to be used in OGNL expressions, using a key to identify the
 * source of the allowlist.
 *
 * @since 6.4.0
 */
public class ProviderAllowlist {

    private final Map<Object, Set<Class<?>>> allowlistMap;
    private Set<Class<?>> allowlistClasses;

    public ProviderAllowlist() {
        allowlistMap = new HashMap<>();
        reconstructAllowlist();
    }

    public synchronized void registerAllowlist(Object key, Set<Class<?>> allowlist) {
        Set<Class<?>> existingAllowlist = allowlistMap.get(key);
        if (existingAllowlist != null) {
            clearAllowlist(key);
        }
        this.allowlistMap.put(key, new HashSet<>(allowlist));
        this.allowlistClasses.addAll(allowlist);
    }

    /**
     * @deprecated since 6.6.0, use {@link #registerAllowlist(Object, Set)}
     */
    @Deprecated
    public synchronized void registerAllowlist(ConfigurationProvider configurationProvider, Set<Class<?>> allowlist) {
        registerAllowlist((Object) configurationProvider, allowlist);
    }

    public synchronized void clearAllowlist(Object key) {
        Set<Class<?>> allowlist = allowlistMap.get(key);
        if (allowlist == null) {
            return;
        }
        this.allowlistMap.remove(key);
        reconstructAllowlist();
    }

    /**
     * @deprecated since 6.6.0, use {@link #clearAllowlist(Object)}
     */
    @Deprecated
    public synchronized void clearAllowlist(ConfigurationProvider configurationProvider) {
        clearAllowlist((Object) configurationProvider);
    }

    public Set<Class<?>> getProviderAllowlist() {
        return unmodifiableSet(allowlistClasses);
    }

    private void reconstructAllowlist() {
        this.allowlistClasses = allowlistMap.values().stream().reduce(new HashSet<>(), (a, b) -> {
            a.addAll(b);
            return a;
        });
    }
}
