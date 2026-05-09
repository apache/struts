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
package org.apache.struts2.rest;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

/**
 * Test fixture for ContentTypeInterceptor integration tests.
 * Has annotated and unannotated properties to exercise authorization filtering.
 */
public class SecureRestAction extends ActionSupport {

    private String name;
    private String role;
    private Address address;
    private Address shallowAddress;

    public String getName() { return name; }

    @StrutsParameter
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Both annotations needed for REST: setter authorizes the top-level "address" (depth 0),
    // getter(depth=1) authorizes nested "address.city" (depth 1). Note: ParametersInterceptor
    // only requires the getter annotation — REST's recursive copy authorizes each path level
    // independently. This divergence is tracked for the Approach C refactor.
    @StrutsParameter(depth = 1)
    public Address getAddress() { return address; }

    @StrutsParameter
    public void setAddress(Address address) { this.address = address; }

    // shallowAddress: depth-0 authorized (setter annotated), but nested fields rejected
    // because the getter has no depth>=1 annotation.
    public Address getShallowAddress() { return shallowAddress; }

    @StrutsParameter
    public void setShallowAddress(Address shallowAddress) { this.shallowAddress = shallowAddress; }

    public static class Address {
        private String city;
        private String zip;

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
    }
}
