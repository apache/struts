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

import org.apache.struts2.inject.Container;
import org.junit.After;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ContainerHolderTest {

    @After
    public void tearDown() {
        ContainerHolder.clear();
    }

    @Test
    public void storeAndGet() {
        Container c = mock(Container.class);
        ContainerHolder.store(c);
        assertThat(ContainerHolder.get()).isSameAs(c);
    }

    @Test
    public void clearRemovesCurrentThread() {
        ContainerHolder.store(mock(Container.class));
        ContainerHolder.clear();
        assertThat(ContainerHolder.get()).isNull();
    }

    @Test
    public void invalidateAllMakesOtherThreadsSeeNull() throws Exception {
        Container c = mock(Container.class);

        // Another thread stores a container
        Thread t = new Thread(() -> ContainerHolder.store(c));
        t.start();
        t.join();

        // Invalidate on main thread
        ContainerHolder.invalidateAll();

        // Other thread's cached value should now be stale
        AtomicReference<Container> otherThreadResult = new AtomicReference<>();
        Thread t2 = new Thread(() -> otherThreadResult.set(ContainerHolder.get()));
        t2.start();
        t2.join();

        assertThat(otherThreadResult.get()).isNull();
    }

    @Test
    public void invalidateAllClearsCallingThread() {
        ContainerHolder.store(mock(Container.class));
        ContainerHolder.invalidateAll();
        assertThat(ContainerHolder.get()).isNull();
    }
}
