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
 * Simple class to hold Container instance per thread to minimise number of attempts
 * to read configuration and build each time a new configuration.
 *
 * As ContainerHolder operates just per thread (which means per request) there is no need
 * to check if configuration changed during the same request. If changed between requests,
 * first call to store Container in ContainerHolder will be with the new configuration.
 */
class ContainerHolder {

    private static ThreadLocal<Container> instance = new ThreadLocal<>();

    public static void store(Container instance) {
        ContainerHolder.instance.set(instance);
    }

    public static Container get() {
        return ContainerHolder.instance.get();
    }

    public static void clear() {
        ContainerHolder.instance.remove();
    }

}
