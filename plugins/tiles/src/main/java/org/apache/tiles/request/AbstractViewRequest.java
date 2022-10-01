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

/**
 * Base class for "view" requests, i.e. requests created into view technologies,
 * such as JSP, Velocity and Freemarker. In particular, all calls to
 * {@link #dispatch(String)} will cause an inclusion and never a forward.
 */
public class AbstractViewRequest extends DispatchRequestWrapper {

    /**
     * Constructor.
     *
     * @param request The base request.
     */
    public AbstractViewRequest(DispatchRequest request) {
        super(request);
    }

    @Override
    public void dispatch(String path) throws IOException {
        setForceInclude(true);
        doInclude(path);
    }

    @Override
    public void include(String path) throws IOException {
        setForceInclude(true);
        doInclude(path);
    }

    /**
     * Includes the result. By default, uses the wrapped request for the inclusion.
     *
     * @param path The path whose result will be included.
     * @throws IOException If something goes wrong.
     */
    protected void doInclude(String path) throws IOException {
        getWrappedRequest().include(path);
    }
}
