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
package org.apache.struts2.conversion;

import org.apache.struts2.XWorkTestCase;

import java.lang.reflect.Field;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class StrutsTypeConverterHolderTest extends XWorkTestCase {

    private static final int THREADS = 16;
    private static final int PER_THREAD = 200;

    private static TypeConverter stubConverter() {
        return (context, target, member, propertyName, value, toType) -> null;
    }

    public void testConcurrentDefaultMappingRegistrationLosesNothing() throws Exception {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < PER_THREAD; i++) {
                    String className = "stub.Class" + threadId + "_" + i;
                    holder.addDefaultMapping(className, stubConverter());
                    // interleave reads with writes to provoke the race
                    holder.containsDefaultMapping("stub.Class0_0");
                    holder.getDefaultMapping(className);
                }
                return null;
            }));
        }

        start.countDown();
        for (Future<?> future : futures) {
            future.get(60, TimeUnit.SECONDS);
        }
        pool.shutdown();

        for (int t = 0; t < THREADS; t++) {
            for (int i = 0; i < PER_THREAD; i++) {
                String className = "stub.Class" + t + "_" + i;
                assertThat(holder.getDefaultMapping(className))
                        .as("lost registration for %s", className)
                        .isNotNull();
            }
        }
    }

    public void testConcurrentUnknownMappingRegistrationLosesNothing() throws Exception {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < PER_THREAD; i++) {
                    holder.addUnknownMapping("stub.Unknown" + threadId + "_" + i);
                    holder.containsUnknownMapping("stub.Unknown0_0");
                }
                return null;
            }));
        }

        start.countDown();
        for (Future<?> future : futures) {
            future.get(60, TimeUnit.SECONDS);
        }
        pool.shutdown();

        for (int t = 0; t < THREADS; t++) {
            for (int i = 0; i < PER_THREAD; i++) {
                String className = "stub.Unknown" + t + "_" + i;
                assertThat(holder.containsUnknownMapping(className))
                        .as("lost unknown mapping for %s", className)
                        .isTrue();
            }
        }
    }

    public void testAddDefaultMappingIgnoresNullConverter() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();

        holder.addDefaultMapping("stub.NullConverter", null);

        assertThat(holder.containsDefaultMapping("stub.NullConverter")).isFalse();
        assertThat(holder.getDefaultMapping("stub.NullConverter")).isNull();
    }

    public void testAddDefaultMappingClearsUnknownMapping() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();

        holder.addUnknownMapping("stub.Later");
        assertThat(holder.containsUnknownMapping("stub.Later")).isTrue();

        holder.addDefaultMapping("stub.Later", stubConverter());

        assertThat(holder.containsUnknownMapping("stub.Later")).isFalse();
        assertThat(holder.getDefaultMapping("stub.Later")).isNotNull();
    }

    /**
     * A {@link Map} that pauses the call to {@code put} for one specific key immediately after the
     * underlying write has taken effect, so a test can deterministically observe state exactly
     * between two writes without depending on lucky thread scheduling.
     */
    private static final class PausingAfterPutMap extends ConcurrentHashMap<String, TypeConverter> {
        private final String watchedKey;
        private final CountDownLatch writeStarted;
        private final CountDownLatch release;

        PausingAfterPutMap(String watchedKey, CountDownLatch writeStarted, CountDownLatch release) {
            this.watchedKey = watchedKey;
            this.writeStarted = writeStarted;
            this.release = release;
        }

        @Override
        public TypeConverter put(String key, TypeConverter value) {
            TypeConverter result = super.put(key, value);
            pauseIfWatched(key, watchedKey, writeStarted, release);
            return result;
        }
    }

    /**
     * A {@link Set} that pauses the call to {@code remove} for one specific key immediately after
     * the underlying write has taken effect, mirroring {@link PausingAfterPutMap} for the unknown-
     * mappings collection.
     */
    private static final class PausingAfterRemoveSet extends AbstractSet<String> {
        private final Set<String> delegate = ConcurrentHashMap.newKeySet();
        private final String watchedKey;
        private final CountDownLatch writeStarted;
        private final CountDownLatch release;

        PausingAfterRemoveSet(String watchedKey, CountDownLatch writeStarted, CountDownLatch release) {
            this.watchedKey = watchedKey;
            this.writeStarted = writeStarted;
            this.release = release;
        }

        @Override
        public boolean add(String s) {
            return delegate.add(s);
        }

        @Override
        public boolean remove(Object o) {
            boolean result = delegate.remove(o);
            pauseIfWatched(o, watchedKey, writeStarted, release);
            return result;
        }

        @Override
        public boolean contains(Object o) {
            return delegate.contains(o);
        }

        @Override
        public Iterator<String> iterator() {
            return delegate.iterator();
        }

        @Override
        public int size() {
            return delegate.size();
        }
    }

    /**
     * Signals {@code writeStarted} and blocks on {@code release} the first time it is called for
     * {@code watchedKey}. Safe to wire into both backing collections with the same latch pair:
     * whichever write executes first pauses here for the test to inspect state; the other write's
     * call is a no-op (both latches are already at zero by the time it runs).
     */
    private static void pauseIfWatched(Object key, String watchedKey, CountDownLatch writeStarted, CountDownLatch release) {
        if (!watchedKey.equals(key)) {
            return;
        }
        writeStarted.countDown();
        try {
            if (!release.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("release latch was never counted down");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private static void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = StrutsTypeConverterHolder.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Pins the write ordering inside {@code addDefaultMapping}: registering the converter must
     * become visible before the unknown-mapping flag is cleared. If the order were inverted, a
     * concurrent reader could observe {@code (containsUnknownMapping == false && containsDefaultMapping
     * == false)} for a class that started out flagged unknown - a state that sends
     * {@code XWorkConverter.lookup} down the {@code lookupSuper()} path, letting it overwrite the more
     * specific converter this method is in the middle of registering with a broader one.
     *
     * <p>The window between the two writes is a couple of instructions wide, so rather than relying
     * on a lucky interleaving, this replaces the holder's two backing collections with variants that
     * deterministically pause immediately after whichever one is written first, letting the test
     * inspect the exact intermediate state {@code addDefaultMapping} produces.</p>
     */
    public void testAddDefaultMappingNeverExposesForbiddenIntermediateStateToReaders() throws Exception {
        String className = "stub.OrderingProbe";
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        holder.addUnknownMapping(className);
        assertThat(holder.containsUnknownMapping(className)).isTrue();
        assertThat(holder.containsDefaultMapping(className)).isFalse();

        CountDownLatch writeStarted = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        injectField(holder, "defaultMappings", new PausingAfterPutMap(className, writeStarted, release));
        injectField(holder, "unknownMappingsInternal", new PausingAfterRemoveSet(className, writeStarted, release));

        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Future<?> writer = pool.submit(() -> holder.addDefaultMapping(className, stubConverter()));

            assertThat(writeStarted.await(10, TimeUnit.SECONDS))
                    .as("addDefaultMapping's first write did not happen in time")
                    .isTrue();

            // Snapshot exactly between the two writes, whichever order they run in.
            boolean unknownFlagged = holder.containsUnknownMapping(className);
            boolean defaultFlagged = holder.containsDefaultMapping(className);

            release.countDown();
            writer.get(10, TimeUnit.SECONDS);

            assertThat(unknownFlagged || defaultFlagged)
                    .as("a concurrent reader observed (unknown=false, default=false) mid-registration for "
                            + "[%s] - addDefaultMapping must register the converter before clearing the "
                            + "unknown flag", className)
                    .isTrue();
        } finally {
            pool.shutdown();
        }

        assertThat(holder.containsUnknownMapping(className)).isFalse();
        assertThat(holder.getDefaultMapping(className)).isNotNull();
    }

    public void testComputeMappingIfAbsentBuildsOnceAndCaches() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        AtomicInteger builds = new AtomicInteger();

        Map<String, Object> first = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            Map<String, Object> built = new HashMap<>();
            built.put("someProperty", "someConverter");
            return built;
        });
        Map<String, Object> second = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            return new HashMap<>();
        });

        assertThat(builds.get()).isEqualTo(1);
        assertThat(first).containsEntry("someProperty", "someConverter");
        assertThat(second).isSameAs(first);
    }

    public void testComputeMappingIfAbsentNegativeCachesEmptyResult() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        AtomicInteger builds = new AtomicInteger();

        Map<String, Object> first = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            return Collections.emptyMap();
        });
        Map<String, Object> second = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            return Collections.emptyMap();
        });

        assertThat(first).isEmpty();
        assertThat(second).isEmpty();
        assertThat(builds.get()).as("empty result must be negative cached").isEqualTo(1);
        assertThat(holder.containsNoMapping(String.class)).isTrue();
        assertThat(holder.getMapping(String.class)).isNull();
    }

    public void testAddNoMappingOverridesPreviouslyCachedMapping() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        Map<String, Object> real = new HashMap<>();
        real.put("someProperty", "someConverter");
        holder.addMapping(String.class, real);

        holder.addNoMapping(String.class);

        assertThat(holder.containsNoMapping(String.class)).isTrue();
        assertThat(holder.getMapping(String.class)).isNull();
        assertThat(holder.computeMappingIfAbsent(String.class, clazz -> {
            throw new AssertionError("builder must not run when no mapping is set");
        })).isNotNull().isEmpty();
    }

    public void testComputeMappingIfAbsentNegativeCachesNullResult() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();

        Map<String, Object> result = holder.computeMappingIfAbsent(String.class, clazz -> null);

        assertThat(result).isNotNull().isEmpty();
        assertThat(holder.containsNoMapping(String.class)).isTrue();
    }

    public void testComputeMappingIfAbsentShortCircuitsOnKnownNoMapping() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        holder.addNoMapping(String.class);

        Map<String, Object> result = holder.computeMappingIfAbsent(String.class, clazz -> {
            throw new AssertionError("builder must not run for a negative-cached class");
        });

        assertThat(result).isNotNull().isEmpty();
    }

    /**
     * {@code computeMappingIfAbsent} deliberately does not run the builder inside a lock (see the
     * comment on the production method), so under concurrent first access the builder may run more
     * than once. What must still hold - and is the property callers actually depend on - is that
     * every caller converges on the same cached mapping instance, with the correct content.
     */
    public void testComputeMappingIfAbsentConvergesOnSameInstanceUnderConcurrency() throws Exception {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            futures.add(pool.submit(() -> {
                start.await();
                return holder.computeMappingIfAbsent(String.class, clazz -> {
                    Map<String, Object> built = new HashMap<>();
                    built.put("someProperty", "someConverter");
                    return built;
                });
            }));
        }

        start.countDown();
        Map<String, Object> expected = futures.get(0).get(60, TimeUnit.SECONDS);
        for (Future<Map<String, Object>> future : futures) {
            assertThat(future.get(60, TimeUnit.SECONDS)).isSameAs(expected);
        }
        pool.shutdown();

        assertThat(expected).containsEntry("someProperty", "someConverter");
    }

    /**
     * Same property as above, for the negative-cache path: every caller must converge on the same
     * cached empty mapping instance, and the class must end up flagged as having no mapping.
     */
    public void testComputeMappingIfAbsentConvergesOnSameInstanceUnderConcurrencyForUnmappedClass() throws Exception {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            futures.add(pool.submit(() -> {
                start.await();
                return holder.computeMappingIfAbsent(String.class, clazz -> Collections.emptyMap());
            }));
        }

        start.countDown();
        Map<String, Object> expected = futures.get(0).get(60, TimeUnit.SECONDS);
        for (Future<Map<String, Object>> future : futures) {
            assertThat(future.get(60, TimeUnit.SECONDS)).isSameAs(expected).isEmpty();
        }
        pool.shutdown();

        assertThat(holder.containsNoMapping(String.class)).isTrue();
    }
}
