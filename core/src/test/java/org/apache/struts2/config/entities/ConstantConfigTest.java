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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.StrutsConstants;
import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.TestBean;
import com.opensymphony.xwork2.inject.Container;

public class ConstantConfigTest {
    @Test
    public void testBeanConfToString() throws Exception {
        ConstantConfig constantConfig = new ConstantConfig();

        String actual = constantConfig.beanConfToString(null);
        Assert.assertEquals(null, actual);

        actual = constantConfig.beanConfToString(new BeanConfig(TestBean.class));
        Assert.assertEquals(Container.DEFAULT_NAME, actual);

        String expectedName = "expectedTestBeanName";
        actual = constantConfig.beanConfToString(new BeanConfig(TestBean.class, expectedName));
        Assert.assertEquals(expectedName, actual);
    }

    @Test
    public void testGetAllAsStringsMap() throws Exception {
        ConstantConfig constantConfig = new ConstantConfig();

        boolean expectedDevMode = true;
        constantConfig.setDevMode(expectedDevMode);

        String expectedActionExtensions = ".action,.some,.another";
        constantConfig.setActionExtension(Arrays.asList(expectedActionExtensions.split(",")));

        String expectedLanguage = "fr";
        constantConfig.setLocale(new Locale(expectedLanguage));

        Map<String, String> map = constantConfig.getAllAsStringsMap();

        Assert.assertEquals(String.valueOf(expectedDevMode), map.get(StrutsConstants.STRUTS_DEVMODE));
        Assert.assertEquals(expectedActionExtensions, map.get(StrutsConstants.STRUTS_ACTION_EXTENSION));
        Assert.assertEquals(null, map.get(StrutsConstants.STRUTS_I18N_RELOAD));
        Assert.assertEquals(expectedLanguage, map.get(StrutsConstants.STRUTS_LOCALE));
    }

    @Test
    public void testEmptyClassesToString() throws Exception {
        ConstantConfig constantConfig = new ConstantConfig();

        constantConfig.setExcludedClasses(new HashSet<Class<?>>());

        Map<String, String> map = constantConfig.getAllAsStringsMap();
        Assert.assertEquals(null, map.get(StrutsConstants.STRUTS_EXCLUDED_CLASSES));
    }

    @Test
    public void testClassesToString() throws Exception {
        ConstantConfig constantConfig = new ConstantConfig();

        Set<Class<?>> excludedClasses = new LinkedHashSet<>();
        excludedClasses.add(Object.class);
        excludedClasses.add(Runtime.class);
        excludedClasses.add(System.class);

        constantConfig.setExcludedClasses(excludedClasses);

        Map<String, String> map = constantConfig.getAllAsStringsMap();
        Assert.assertEquals("java.lang.Object,java.lang.Runtime,java.lang.System",
                map.get(StrutsConstants.STRUTS_EXCLUDED_CLASSES));
    }
}
