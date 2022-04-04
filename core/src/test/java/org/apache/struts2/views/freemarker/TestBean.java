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

package org.apache.struts2.views.freemarker;


/**
 */
public class TestBean {

    private String pName = null;
    private String pValue = null;


    public TestBean(String name, String value) {
        super();
        setName(name);
        setValue(value);
    }


    public void setName(String aName) {
        pName = aName;
    }

    /**
     * Bean Property String Name
     */
    public String getName() {
        return pName;
    }

    public void setValue(String aValue) {
        pValue = aValue;
    }

    /**
     * Bean Property String Value
     */
    public String getValue() {
        return pValue;
    }
}
