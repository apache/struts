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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    public void testConcurrentNoMappingAndUnknownMappingRegistrationLosesNothing() throws Exception {
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
}
