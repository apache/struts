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

import org.apache.struts2.json.annotations.SMD;
import org.apache.struts2.json.annotations.SMDMethod;
import org.apache.struts2.json.annotations.SMDMethodParameter;

@SMD(objectName = "testaction", serviceType = "service", version = "10.0")
public class SMDActionTest2 {
    private boolean doSomethingInvoked;

    @SMDMethod
    public void add(@SMDMethodParameter(name = "a")
    int a, @SMDMethodParameter(name = "b")
    int b) {
    }

    @SMDMethod(name = "doSomethingElse")
    public void doSomething() {
        doSomethingInvoked = true;
    }

    @SMDMethod
    public Bean getBean() {
        Bean bean = new Bean();
        bean.setStringField("str");
        bean.setBooleanField(true);
        bean.setCharField('s');
        bean.setDoubleField(10.1);
        bean.setFloatField(1.5f);
        bean.setIntField(10);
        bean.setLongField(100);
        return bean;
    }

    public boolean isDoSomethingInvoked() {
        return doSomethingInvoked;
    }
}
