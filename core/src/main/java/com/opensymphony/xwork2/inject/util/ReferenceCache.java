/**
 * Copyright (C) 2006 Google Inc.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * </p>
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.opensymphony.xwork2.inject.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.*;

import static com.opensymphony.xwork2.inject.util.ReferenceType.STRONG;

/**
 * Extends {@link ReferenceMap} to support lazy loading values by overriding
 * {@link #create(Object)}.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public abstract class ReferenceCache<K, V> extends ReferenceMap<K, V> {

    private static final long serialVersionUID = 0;

    transient ConcurrentMap<Object, Future<V>> futures = new ConcurrentHashMap<>();
    transient ThreadLocal<Future<V>> localFuture = new ThreadLocal<>();

    public ReferenceCache(ReferenceType keyReferenceType, ReferenceType valueReferenceType) {
        super(keyReferenceType, valueReferenceType);
    }

    /**
     * Equivalent to {@code new ReferenceCache(STRONG, STRONG)}.
     */
    public ReferenceCache() {
        super(STRONG, STRONG);
    }

    /**
     * Override to lazy load values. Use as an alternative to {@link
     * #put(Object, Object)}. Invoked by getter if value isn't already cached.
     * Must not return {@code null}. This method will not be called again until
     * the garbage collector reclaims the returned value.
     *
     * @param key the key
     *
     * @return returned value
     */
    protected abstract V create(K key);

    V internalCreate(K key) {
        try {
            FutureTask<V> futureTask = new FutureTask<>(new CallableCreate(key));

            // use a reference so we get the same equality semantics.
            Object keyReference = referenceKey(key);
            Future<V> future = futures.putIfAbsent(keyReference, futureTask);
            if (future == null) {
                // winning thread.
                try {
                    if (localFuture.get() != null) {
                        throw new IllegalStateException("Nested creations within the same cache are not allowed.");
                    }
                    localFuture.set(futureTask);
                    futureTask.run();
                    V value = futureTask.get();
                    putStrategy().execute(this, keyReference, referenceValue(keyReference, value));
                    return value;
                } finally {
                    localFuture.remove();
                    futures.remove(keyReference);
                }
            } else {
                // wait for winning thread.
                return future.get();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException(cause);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * If this map does not contain an entry for the given key and {@link
     * #create(Object)} has been overridden, this method will create a new
     * value, put it in the map, and return it.
     * </p>
     *
     * @param key the key
     * @return return new created value
     *
     * @throws NullPointerException                       if {@link #create(Object)} returns null.
     * @throws java.util.concurrent.CancellationException if the creation is
     *                                                    cancelled. See {@link #cancel()}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public V get(final Object key) {
        V value = super.get(key);
        return (value == null) ? internalCreate((K) key) : value;
    }

    /**
     * Cancels the current {@link #create(Object)}. Throws {@link
     * java.util.concurrent.CancellationException} to all clients currently
     * blocked on {@link #get(Object)}.
     */
    protected void cancel() {
        Future<V> future = localFuture.get();
        if (future == null) {
            throw new IllegalStateException("Not in create().");
        }
        future.cancel(false);
    }

    class CallableCreate implements Callable<V> {

        K key;

        public CallableCreate(K key) {
            this.key = key;
        }

        public V call() {
            // try one more time (a previous future could have come and gone.)
            V value = internalGet(key);
            if (value != null) {
                return value;
            }

            // create value.
            value = create(key);
            if (value == null) {
                throw new NullPointerException("create(K) returned null for: " + key);
            }
            return value;
        }
    }

    /**
     * Returns a {@code ReferenceCache} delegating to the specified {@code
     * function}. The specified function must not return {@code null}.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @param function function
     * @param keyReferenceType key reference type
     * @param valueReferenceType  value reference type
     *
     * @return reference cache
     */
    public static <K, V> ReferenceCache<K, V> of(
            ReferenceType keyReferenceType,
            ReferenceType valueReferenceType,
            final Function<? super K, ? extends V> function) {
        ensureNotNull(function);
        return new ReferenceCache<K, V>(keyReferenceType, valueReferenceType) {
            @Override
            protected V create(K key) {
                return function.apply(key);
            }

            private static final long serialVersionUID = 0;
        };
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        this.futures = new ConcurrentHashMap<>();
        this.localFuture = new ThreadLocal<>();
    }

}
