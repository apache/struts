/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.TestBean;

import java.util.ArrayList;
import java.util.List;


/**
 * VisitorValidatorTestAction
 *
 * @author Jason Carreira
 *         Created Aug 4, 2003 1:00:04 AM
 */
public class VisitorValidatorTestAction extends ActionSupport {

    private List<TestBean> testBeanList = new ArrayList<TestBean>();
    private String context;
    private TestBean bean = new TestBean();
    private TestBean[] testBeanArray;


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

    public TestBean getBean() {
        return bean;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setTestBeanArray(TestBean[] testBeanArray) {
        this.testBeanArray = testBeanArray;
    }

    public TestBean[] getTestBeanArray() {
        return testBeanArray;
    }

    public void setTestBeanList(List<TestBean> testBeanList) {
        this.testBeanList = testBeanList;
    }

    public List<TestBean> getTestBeanList() {
        return testBeanList;
    }
}
