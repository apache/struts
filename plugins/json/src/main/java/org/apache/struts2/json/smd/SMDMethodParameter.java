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

public class SMDMethodParameter implements Comparable {
    private String name;

    public SMDMethodParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Object o) {
        if (!(o instanceof SMDMethodParameter))
            return 1;
        if (o == null)
            return 1;
        if ((name == null) && (((SMDMethodParameter) o).name == null))
            return 0;
        if (name == null)
            return -1;
        return name.compareTo(((SMDMethodParameter) o).name);
    }

    public boolean equals(Object o) {
        if (!(o instanceof SMDMethodParameter))
            return false;
        if ((name == null) && (((SMDMethodParameter) o).name == null))
            return true;
        return (name != null) && name.equals(((SMDMethodParameter) o).name);
    }
}
