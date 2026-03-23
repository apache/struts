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

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsJUnit4InternalTestCase;
import org.apache.struts2.components.Component;
import org.apache.struts2.inject.Container;
import org.apache.struts2.ognl.accessor.CompoundRootAccessor;
import org.apache.struts2.util.DebugUtils;
import org.apache.struts2.util.fs.DefaultFileManager;
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

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * WW-5537: Verifies that Dispatcher.cleanup() properly clears all static state
 * that could prevent classloader garbage collection during hot redeployment.
 */
public class DispatcherCleanupTest extends StrutsJUnit4InternalTestCase {

    @Test
    public void cleanupDiscoversAllInternalDestroyableBeans() {
        initDispatcher(emptyMap());

        Container container = dispatcher.getConfigurationManager().getConfiguration().getContainer();
        Set<String> names = container.getInstanceNames(InternalDestroyable.class);

        Set<String> expected = new HashSet<>(Arrays.asList(
                "componentCache", "compoundRootAccessor", "defaultFileManager",
                "scopeInterceptorCache", "ognlCache", "finalizableReferenceQueue",
                "freemarkerCache", "debugUtilsCache"
        ));
        assertThat(names).containsAll(expected);
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
        assertThat(map).isNotEmpty();

        dispatcher.cleanup();

        assertThat(map).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cleanupClearsCompoundRootAccessorCache() throws Exception {
        initDispatcher(emptyMap());

        Field field = CompoundRootAccessor.class.getDeclaredField("invalidMethods");
        field.setAccessible(true);
        Map<Object, Boolean> invalidMethods = (Map<Object, Boolean>) field.get(null);

        invalidMethods.put("testKey", Boolean.TRUE);
        assertThat(invalidMethods).isNotEmpty();

        dispatcher.cleanup();

        assertThat(invalidMethods).isEmpty();
    }

    @Test
    public void cleanupClearsDefaultFileManagerFilesMap() throws Exception {
        initDispatcher(emptyMap());

        Field filesField = DefaultFileManager.class.getDeclaredField("files");
        filesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> files = (Map<String, Object>) filesField.get(null);

        files.put("test-key", new Object());
        assertThat(files).isNotEmpty();

        dispatcher.cleanup();

        assertThat(files).isEmpty();
    }

    @Test
    public void cleanupClearsDefaultFileManagerLazyCache() throws Exception {
        initDispatcher(emptyMap());

        Field lazyCacheField = DefaultFileManager.class.getDeclaredField("lazyMonitoredFilesCache");
        lazyCacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<URL> lazyCache = (List<URL>) lazyCacheField.get(null);

        lazyCache.add(new URL("file:///test"));
        assertThat(lazyCache).isNotEmpty();

        dispatcher.cleanup();

        assertThat(lazyCache).isEmpty();
    }

    @Test
    public void cleanupClearsDispatcherListeners() throws Exception {
        initDispatcher(emptyMap());

        Dispatcher.addDispatcherListener(new DispatcherListener() {
            @Override
            public void dispatcherInitialized(Dispatcher du) {}
            @Override
            public void dispatcherDestroyed(Dispatcher du) {}
        });

        dispatcher.cleanup();

        Field listenersField = Dispatcher.class.getDeclaredField("dispatcherListeners");
        listenersField.setAccessible(true);
        List<?> listeners = (List<?>) listenersField.get(null);
        assertThat(listeners).isEmpty();
    }

    @Test
    public void cleanupClearsThreadLocals() {
        assertThat(Dispatcher.getInstance()).isNotNull();
        assertThat(ActionContext.getContext()).isNotNull();

        dispatcher.cleanup();

        assertThat(Dispatcher.getInstance()).isNull();
        assertThat(ActionContext.getContext()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cleanupClearsDebugUtilsCache() throws Exception {
        initDispatcher(emptyMap());

        Field field = DebugUtils.class.getDeclaredField("IS_LOGGED");
        field.setAccessible(true);
        Set<String> isLogged = (Set<String>) field.get(null);

        isLogged.add("test-key");
        assertThat(isLogged).isNotEmpty();

        dispatcher.cleanup();

        assertThat(isLogged).isEmpty();
    }
}
