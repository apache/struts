/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.util.classloader.stores;


/**
 * @author tcurdt
 */
public abstract class TransactionalResourceStore implements ResourceStore {

    private final ResourceStore store;

    public TransactionalResourceStore(final ResourceStore pStore) {
        store = pStore;
    }

    public abstract void onStart();

    public abstract void onStop();

    public byte[] read(String resourceName) {
        return store.read(resourceName);
    }

    public void remove(String resourceName) {
        store.remove(resourceName);
    }

    public void write(String resourceName, byte[] resourceData) {
        store.write(resourceName, resourceData);
    }

    public String toString() {
        return store.toString();
    }
}
