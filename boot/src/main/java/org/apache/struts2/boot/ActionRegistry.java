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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ActionRegistry {

    public static final String SERVLET_CONTEXT_ATTRIBUTE = ActionRegistry.class.getName();

    private final Map<String, NamespaceActions> namespaces = new LinkedHashMap<>();

    public ActionDefinition action(String name, Class<?> actionClass) {
        return namespaceActions("").action(name, actionClass);
    }

    public ActionRegistry namespace(String namespace, Consumer<NamespaceActions> body) {
        body.accept(namespaceActions(namespace));
        return this;
    }

    public Collection<NamespaceActions> namespaces() {
        return Collections.unmodifiableCollection(namespaces.values());
    }

    private NamespaceActions namespaceActions(String namespace) {
        return namespaces.computeIfAbsent(namespace, NamespaceActions::new);
    }
}
