/*
 * $Id$
 *
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

package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.Result;

import java.util.Map;

/**
 * Simple class that holds the action mapping information used to invoke a
 * Struts action. The name and namespace are required, but the params map
 * is optional, and as such may be null. If a params map is supplied,
 * it <b>must</b> be a mutable map, such as a HashMap.
 *
 */
public class ActionMapping {

    private String name;
    private String namespace;
    private String method;
    private String extension;
    private Map<String, Object> params;
    private Result result;

    /**
     * Constructs an ActionMapping
     */
    public ActionMapping() {}

    /**
     * Constructs an ActionMapping with a default result
     *
     * @param result The default result
     */
    public ActionMapping(Result result) {
        this.result = result;
    }

    /**
     * Constructs an ActionMapping with its values
     *
     * @param name The action name
     * @param namespace The action namespace
     * @param method The method
     * @param params The extra parameters
     */
    public ActionMapping(String name, String namespace, String method, Map<String, Object> params) {
        this.name = name;
        this.namespace = namespace;
        this.method = method;
        this.params = params;
    }

    /**
     * @return The action name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The action namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return The extra parameters
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * @return The method
     */
    public String getMethod() {
        if (null != method && "".equals(method)) {
            return null;
        } else {
            return method;
        }
    }

    /**
     * @return The default result
     */
    public Result getResult() {
        return result;
    }
    
    /**
     * @return The extension used during this request
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param result The result
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * @param name The action name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param namespace The action namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @param method The method name to call on the action
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @param params The extra parameters for this mapping
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
    /**
     * @param extension The extension used in the request
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "ActionMapping{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", method='" + method + '\'' +
                ", extension='" + extension + '\'' +
                ", params=" + params +
                ", result=" + (result != null ? result.getClass().getName() : "null") +
                '}';
    }

}
