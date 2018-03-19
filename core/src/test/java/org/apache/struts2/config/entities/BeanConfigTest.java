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
package org.apache.struts2.config.entities;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.TestBean;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;

public class BeanConfigTest {
    @Test
    public void testConstructor() throws Exception {
        Class<TestBean> expectedClass = TestBean.class;

        BeanConfig beanConfig = new BeanConfig(expectedClass);

        Assert.assertEquals(expectedClass, beanConfig.getClazz());
        Assert.assertEquals(Container.DEFAULT_NAME, beanConfig.getName());
        Assert.assertEquals(Scope.SINGLETON, beanConfig.getScope());
        Assert.assertEquals(expectedClass, beanConfig.getType());
        Assert.assertFalse(beanConfig.isOnlyStatic());
        Assert.assertFalse(beanConfig.isOptional());
    }

    @Test
    public void testConstructor2() throws Exception {
        Class<TestBean> expectedClass = TestBean.class;
        String expectedName = "expectedBeanName";
        Class<Object> expectedType = Object.class;
        Scope expectedScope = Scope.PROTOTYPE;
        boolean expectedOnlyStatic = true;
        boolean expectedOptional = true;

        BeanConfig beanConfig = new BeanConfig(expectedClass, expectedName, expectedType, expectedScope,
                expectedOnlyStatic, expectedOptional);

        Assert.assertEquals(expectedClass, beanConfig.getClazz());
        Assert.assertEquals(expectedName, beanConfig.getName());
        Assert.assertEquals(expectedScope, beanConfig.getScope());
        Assert.assertEquals(expectedType, beanConfig.getType());
        Assert.assertEquals(expectedOnlyStatic, beanConfig.isOnlyStatic());
        Assert.assertEquals(expectedOptional, beanConfig.isOptional());
    }
}
