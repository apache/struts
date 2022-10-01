/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request;

import java.io.IOException;
import java.util.Map;

/**
 * Base class for "client" requests, i.e. requests that come unchanged by the
 * container, such as ServletRequest and PortletRequest.
 */
public abstract class AbstractClientRequest extends AbstractRequest {

    /**
     * The application context.
     */
    private final ApplicationContext applicationContext;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     */
    public AbstractClientRequest(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void dispatch(String path) throws IOException {
        if (isForceInclude()) {
            doInclude(path);
        } else {
            setForceInclude(true);
            doForward(path);
        }
    }

    @Override
    public void include(String path) throws IOException {
        setForceInclude(true);
        doInclude(path);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Returns the application scope.
     *
     * @return The application scope.
     */
    public Map<String, Object> getApplicationScope() {
        return applicationContext.getApplicationScope();
    }

    /**
     * Forwards to a path.
     *
     * @param path The path to forward to.
     * @throws IOException If something goes wrong when forwarding.
     */
    protected abstract void doForward(String path) throws IOException;

    /**
     * Includes the result of a path.
     *
     * @param path The path to forward to.
     * @throws IOException If something goes wrong when forwarding.
     */
    protected abstract void doInclude(String path) throws IOException;

}
