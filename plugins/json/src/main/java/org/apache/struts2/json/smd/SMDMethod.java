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

public class SMDMethod implements Comparable {
    private String name;
    private Set<SMDMethodParameter> parameters = new TreeSet<SMDMethodParameter>();

    public SMDMethod(String name) {
        this.name = name;
    }

    public void addSMDMethodParameter(SMDMethodParameter parameter) {
        this.parameters.add(parameter);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<SMDMethodParameter> getParameters() {
        return this.parameters;
    }

    public int compareTo(Object o) {
        if (!(o instanceof SMDMethod))
            return 1;
        if (o == null)
            return 1;
        SMDMethod other = (SMDMethod) o;
        if ((name == null) && (other.name == null))
            return 0;
        if (name == null)
            return -1;
        if (name.equals(other.name))
            return parameters.size() - other.parameters.size();

        return name.compareTo(other.name);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SMDMethod))
            return false;
        SMDMethod toCompare = (SMDMethod) obj;
        if ((name == null) && (toCompare.name == null))
            return true;
        return (name != null) && name.equals(toCompare.name)
                && (parameters.size() == toCompare.parameters.size());
    }
}
