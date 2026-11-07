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
package org.apache.struts2.result.xslt;

import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class BeanAdapterTest {

    @Test
    public void testConcurrentAdaptersCacheEachBeanClassOnce() throws Exception {
        Method method = BeanAdapter.class.getDeclaredMethod("getPropertyDescriptors", Object.class);
        method.setAccessible(true);
        method.invoke(new BeanAdapter(), "warm up");
        Field field = BeanAdapter.class.getDeclaredField("propertyDescriptorCache");
        field.setAccessible(true);
        Map<?, ?> cache = (Map<?, ?>) field.get(null);
        int sizeBefore = cache.size();

        Class<?>[] interfaces = {Runnable.class, Comparable.class, Cloneable.class, Serializable.class,
                AutoCloseable.class, Iterable.class, CharSequence.class, Callable.class, Supplier.class,
                Consumer.class, Function.class, Predicate.class};
        List<Object> beans = new ArrayList<>();
        for (int mask = 1; mask < (1 << interfaces.length); mask++) {
            List<Class<?>> subset = new ArrayList<>();
            for (int i = 0; i < interfaces.length; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(interfaces[i]);
                }
            }
            beans.add(Proxy.newProxyInstance(BeanAdapterTest.class.getClassLoader(),
                    subset.toArray(new Class<?>[0]), (proxy, m, args) -> null));
        }
        int beanCount = beans.size();

        int threads = 16;
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    for (Object bean : beans) {
                        method.invoke(new BeanAdapter(), bean);
                    }
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdownNow();
        }

        assertEquals(beanCount, cache.size() - sizeBefore);
    }
}
