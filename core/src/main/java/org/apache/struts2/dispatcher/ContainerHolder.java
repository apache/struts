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
 * Simple class to hold Container instance to minimise number of attempts
 * to read configuration and build each time a new configuration.
 * <p>
 * WW-5537: Changed from ThreadLocal to volatile shared reference to prevent
 * classloader leaks during hot redeployment. ThreadLocal values on idle pool
 * threads are not cleared by {@code ThreadLocal.remove()} on other threads,
 * causing the Container (and its classloader) to be retained after undeploy.
 */
class ContainerHolder {

    private static volatile Container instance;

    public static void store(Container newInstance) {
        instance = newInstance;
    }

    public static Container get() {
        return instance;
    }

    /**
     * Clears the shared container reference. Safe to call concurrently with
     * {@link #store(Container)} because {@link Dispatcher#getContainer()} lazily
     * repopulates the holder from the configuration manager when it finds a null value.
     */
    public static void clear() {
        instance = null;
    }

}
