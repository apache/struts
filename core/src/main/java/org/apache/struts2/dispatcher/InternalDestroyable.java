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
package org.apache.struts2.dispatcher;

/**
 * Internal framework interface for components that hold static state
 * (caches, daemon threads, etc.) requiring cleanup during application
 * undeploy to prevent classloader leaks.
 *
 * <p>Implementations are registered as named beans in {@code struts-beans.xml}
 * (or plugin descriptors) with type {@code InternalDestroyable}. During
 * {@link Dispatcher#cleanup()}, all registered implementations are discovered
 * via {@code Container.getInstanceNames(InternalDestroyable.class)} and
 * invoked automatically.</p>
 *
 * <p>The order in which implementations are invoked is not guaranteed.
 * Implementations must not depend on other {@code InternalDestroyable}
 * beans having been (or not yet been) destroyed. Ordering can be
 * influenced via the {@code order} attribute in bean registration.</p>
 *
 * <p>This is not part of the public user API. For user/plugin lifecycle
 * callbacks, use {@link DispatcherListener} instead.</p>
 *
 * @since 6.9.0
 * @see Dispatcher#cleanup()
 */
public interface InternalDestroyable {

    /**
     * Releases static state held by this component. Called once during
     * {@link Dispatcher#cleanup()}.
     */
    void destroy();
}
