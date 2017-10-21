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
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.interceptor.Interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>InterceptorMapping</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class InterceptorMapping implements Serializable {

    private String name;
    private Interceptor interceptor;
    private final Map<String, String> params;

    public InterceptorMapping(String name, Interceptor interceptor) {
        this(name, interceptor, new HashMap<String, String>());
    }

    public InterceptorMapping(String name, Interceptor interceptor, Map<String, String> params) {
        this.name = name;
        this.interceptor = interceptor;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InterceptorMapping that = (InterceptorMapping) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InterceptorMapping: [" + name + "] => [" + interceptor.getClass().getName() + "] with params [" + params + "]" ;
    }

}
