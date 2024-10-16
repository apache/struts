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
package org.apache.struts2.validator;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.TestBean;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * VisitorValidatorTestAction
 *
 * @author Jason Carreira
 *         Created Aug 4, 2003 1:00:04 AM
 */
public class VisitorValidatorTestAction extends ActionSupport {

    private List<TestBean> testBeanList = new ArrayList<>();
    private String context;
    private TestBean bean = new TestBean();
    private TestBean[] testBeanArray;
    private Date birthday;

    public VisitorValidatorTestAction() {
        testBeanArray = new TestBean[5];

        for (int i = 0; i < 5; i++) {
            testBeanArray[i] = new TestBean();
            testBeanList.add(new TestBean());
        }
    }

    public void setBean(TestBean bean) {
        this.bean = bean;
    }

    @StrutsParameter(depth = 2)
    public TestBean getBean() {
        return bean;
    }

    @StrutsParameter
    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setTestBeanArray(TestBean[] testBeanArray) {
        this.testBeanArray = testBeanArray;
    }

    @StrutsParameter(depth = 3)
    public TestBean[] getTestBeanArray() {
        return testBeanArray;
    }

    public void setTestBeanList(List<TestBean> testBeanList) {
        this.testBeanList = testBeanList;
    }

    @StrutsParameter(depth = 3)
    public List<TestBean> getTestBeanList() {
        return testBeanList;
    }

    public Date getBirthday() {
        return birthday;
    }

    @StrutsParameter
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
