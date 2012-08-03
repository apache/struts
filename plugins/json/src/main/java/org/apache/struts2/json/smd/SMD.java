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
package org.apache.struts2.json.smd;

import java.util.Set;
import java.util.TreeSet;

/*
 * HOlds SMD declarations for a class
 */
public class SMD {
    public static final String DEFAULT_VERSION = ".1";
    public static final String DEFAULT_SERVICE_TYPE = "JSON-RPC";

    private String version = DEFAULT_VERSION;
    private String objectName;
    private String serviceType = DEFAULT_SERVICE_TYPE;
    private String serviceUrl;
    private Set<SMDMethod> methods = new TreeSet<SMDMethod>();

    public void addSMDMethod(SMDMethod method) {
        this.methods.add(method);
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public Set<SMDMethod> getMethods() {
        return this.methods;
    }
}
