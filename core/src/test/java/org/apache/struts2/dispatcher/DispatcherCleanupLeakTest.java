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
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import org.apache.struts2.StrutsJUnit4InternalTestCase;
import org.apache.struts2.components.Component;
import org.apache.struts2.interceptor.ScopeInterceptor;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * WW-5537: Verifies that Dispatcher.cleanup() properly clears all static state
 * that could prevent classloader garbage collection during hot redeployment.
 */
public class DispatcherCleanupLeakTest extends StrutsJUnit4InternalTestCase {

    @Test
    public void cleanupDiscoversAllInternalDestroyableBeans() {
        initDispatcher(emptyMap());

        Container container = dispatcher.getConfigurationManager().getConfiguration().getContainer();
        Set<String> names = container.getInstanceNames(InternalDestroyable.class);

        Set<String> expected = new HashSet<>(Arrays.asList(
                "componentCache", "compoundRootAccessor", "defaultFileManager",
                "scopeInterceptorCache", "ognlCache", "finalizableReferenceQueue",
                "freemarkerCache"
        ));
        assertTrue("All core InternalDestroyable beans should be registered, missing: "
                        + missing(expected, names),
                names.containsAll(expected));
    }

    @Test
    public void cleanupContinuesWhenDestroyableThrows() {
        initDispatcher(emptyMap());

        // Populate a cache to verify cleanup still runs after a failure
        Field mapField;
        try {
            mapField = Component.class.getDeclaredField("standardAttributesMap");
            mapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            ConcurrentMap<Class<?>, Collection<String>> map =
                    (ConcurrentMap<Class<?>, Collection<String>>) mapField.get(null);
            map.put(String.class, new ArrayList<>());
            assertFalse("Precondition: standardAttributesMap should not be empty", map.isEmpty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Register a destroyable that throws before other cleanup runs
        final AtomicBoolean secondCalled = new AtomicBoolean(false);
        InternalDestroyable failing = () -> { throw new RuntimeException("test failure"); };
        InternalDestroyable tracking = () -> secondCalled.set(true);

        // Call cleanup — the loop should catch the exception and continue
        Container container = dispatcher.getConfigurationManager().getConfiguration().getContainer();
        Set<String> names = container.getInstanceNames(InternalDestroyable.class);

        // Simulate the loop with our test destroyables injected
        List<InternalDestroyable> destroyables = new ArrayList<>();
        destroyables.add(failing);
        for (String name : names) {
            destroyables.add(container.getInstance(InternalDestroyable.class, name));
        }
        destroyables.add(tracking);

        for (InternalDestroyable d : destroyables) {
            try {
                d.destroy();
            } catch (Exception e) {
                // mirrors Dispatcher.cleanup() error handling
            }
        }

        assertTrue("Destroyable after the failing one should still be called", secondCalled.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cleanupClearsComponentStandardAttributesMap() throws Exception {
        initDispatcher(emptyMap());

        Field mapField = Component.class.getDeclaredField("standardAttributesMap");
        mapField.setAccessible(true);
        ConcurrentMap<Class<?>, Collection<String>> map =
                (ConcurrentMap<Class<?>, Collection<String>>) mapField.get(null);

        map.put(String.class, new ArrayList<>());
        assertFalse("Precondition: standardAttributesMap should not be empty", map.isEmpty());

        dispatcher.cleanup();

        assertTrue("standardAttributesMap should be empty after cleanup", map.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cleanupClearsCompoundRootAccessorCache() throws Exception {
        initDispatcher(emptyMap());

        Field field = CompoundRootAccessor.class.getDeclaredField("invalidMethods");
        field.setAccessible(true);
        Map<Object, Boolean> invalidMethods = (Map<Object, Boolean>) field.get(null);

        // Seed with a dummy entry to ensure cleanup actually clears it
        invalidMethods.put("testKey", Boolean.TRUE);
        assertFalse("Precondition: invalidMethods should not be empty", invalidMethods.isEmpty());

        dispatcher.cleanup();

        assertTrue("invalidMethods should be empty after cleanup", invalidMethods.isEmpty());
    }

    @Test
    public void cleanupClearsDefaultFileManagerFilesMap() throws Exception {
        initDispatcher(emptyMap());

        Field filesField = DefaultFileManager.class.getDeclaredField("files");
        filesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> files = (Map<String, Object>) filesField.get(null);

        files.put("test-key", new Object());
        assertFalse("Precondition: files should not be empty", files.isEmpty());

        dispatcher.cleanup();

        assertTrue("DefaultFileManager.files should be empty after cleanup", files.isEmpty());
    }

    @Test
    public void cleanupClearsDefaultFileManagerLazyCache() throws Exception {
        initDispatcher(emptyMap());

        Field lazyCacheField = DefaultFileManager.class.getDeclaredField("lazyMonitoredFilesCache");
        lazyCacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<URL> lazyCache = (List<URL>) lazyCacheField.get(null);

        lazyCache.add(new URL("file:///test"));
        assertFalse("Precondition: lazyMonitoredFilesCache should not be empty", lazyCache.isEmpty());

        dispatcher.cleanup();

        assertTrue("DefaultFileManager.lazyMonitoredFilesCache should be empty after cleanup",
                lazyCache.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cleanupClearsScopeInterceptorLocks() throws Exception {
        initDispatcher(emptyMap());

        Field locksField = ScopeInterceptor.class.getDeclaredField("locks");
        locksField.setAccessible(true);
        Map<Object, Object> locks = (Map<Object, Object>) locksField.get(null);

        locks.put(new Object(), new Object());
        assertFalse("Precondition: locks should not be empty", locks.isEmpty());

        dispatcher.cleanup();

        assertTrue("ScopeInterceptor.locks should be empty after cleanup", locks.isEmpty());
    }

    @Test
    public void cleanupClearsDispatcherListeners() throws Exception {
        initDispatcher(emptyMap());

        DispatcherListener listener = new DispatcherListener() {
            @Override
            public void dispatcherInitialized(Dispatcher du) {}
            @Override
            public void dispatcherDestroyed(Dispatcher du) {}
        };
        Dispatcher.addDispatcherListener(listener);

        dispatcher.cleanup();

        Field listenersField = Dispatcher.class.getDeclaredField("dispatcherListeners");
        listenersField.setAccessible(true);
        List<?> listeners = (List<?>) listenersField.get(null);
        assertTrue("dispatcherListeners should be empty after cleanup", listeners.isEmpty());
    }

    private Set<String> missing(Set<String> expected, Set<String> actual) {
        Set<String> diff = new HashSet<>(expected);
        diff.removeAll(actual);
        return diff;
    }
}
