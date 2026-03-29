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

import javax.servlet.ServletContext;

/**
 * Extension of {@link InternalDestroyable} for components that require
 * {@link ServletContext} during cleanup (e.g. clearing servlet-scoped caches).
 *
 * <p>During {@link Dispatcher#cleanup()}, the discovery loop checks each
 * {@code InternalDestroyable} bean: if it implements this subinterface,
 * {@link #destroy(ServletContext)} is called instead of {@link #destroy()}.</p>
 *
 * @since 6.9.0
 * @see InternalDestroyable
 * @see Dispatcher#cleanup()
 */
public interface ContextAwareDestroyable extends InternalDestroyable {

    /**
     * Releases state that requires access to the {@link ServletContext}.
     *
     * @param servletContext the current servlet context, may be {@code null}
     *                       if the Dispatcher was created without one
     */
    void destroy(ServletContext servletContext);

    /**
     * Default no-op — {@link Dispatcher} calls
     * {@link #destroy(ServletContext)} instead when it recognises this type.
     */
    @Override
    default void destroy() {
        // no-op: context-aware variant is the real entry point
    }
}
