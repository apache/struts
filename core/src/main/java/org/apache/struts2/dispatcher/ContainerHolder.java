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

import com.opensymphony.xwork2.inject.Container;

/**
 * Per-thread cache for the Container instance, minimising repeated reads from
 * {@link com.opensymphony.xwork2.config.ConfigurationManager}.
 * <p>
 * WW-5537: Uses a ThreadLocal for per-request isolation with a volatile generation
 * counter for cross-thread invalidation during app undeploy. When
 * {@link #invalidateAll()} is called, all threads see the updated generation on their
 * next {@link #get()} and return {@code null}, forcing a fresh read from
 * ConfigurationManager. This prevents classloader leaks caused by idle pool threads
 * retaining stale Container references after hot redeployment.
 */
class ContainerHolder {

    private static final ThreadLocal<CachedContainer> instance = new ThreadLocal<>();

    /**
     * Incremented on each {@link #invalidateAll()} call. Threads compare their cached
     * generation against this value to detect staleness.
     */
    private static volatile long generation = 0;

    public static void store(Container newInstance) {
        instance.set(new CachedContainer(newInstance, generation));
    }

    public static Container get() {
        CachedContainer cached = instance.get();
        if (cached == null) {
            return null;
        }
        if (cached.generation != generation) {
            instance.remove();
            return null;
        }
        return cached.container;
    }

    /**
     * Clears the current thread's cached container reference.
     * Used for per-request cleanup.
     */
    public static void clear() {
        instance.remove();
    }

    /**
     * Invalidates all threads' cached container references by advancing the generation
     * counter. Each thread will detect the stale generation on its next {@link #get()}
     * call and clear its own ThreadLocal. Also clears the calling thread immediately.
     * <p>
     * Used during application undeploy ({@link Dispatcher#cleanup()}) to ensure idle
     * pool threads do not pin the webapp classloader via retained Container references.
     */
    public static void invalidateAll() {
        generation++;
        instance.remove();
    }

    private static class CachedContainer {
        final Container container;
        final long generation;

        CachedContainer(Container container, long generation) {
            this.container = container;
            this.generation = generation;
        }
    }
}
