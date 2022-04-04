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
package org.apache.struts2.json;

import java.util.ArrayList;
import java.util.List;

public class TestAction5 {
    private List<Bean> beans;

    private AnotherBean anotherBean;

    public List<Bean> getBeans() {
        if (this.beans == null) {
            this.beans = new ArrayList<Bean>();
        }
        return this.beans;
    }

    public void setBeans(List<Bean> beans) {
        this.beans = beans;
    }

    public AnotherBean getAnotherBean() {
        if(this.anotherBean == null) {
            this.anotherBean = new AnotherBean();
        }
        return anotherBean;
    }

    public void setAnotherBean(AnotherBean anotherBean) {
        this.anotherBean = anotherBean;
    }
}
