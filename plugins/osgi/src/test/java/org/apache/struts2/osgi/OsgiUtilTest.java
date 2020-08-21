/*
 * Copyright 2020 Apache Software Foundation.
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
package org.apache.struts2.osgi;

import org.junit.Assert;
import org.junit.Test;

/**
 *  Basic tests for OsgiUtil
 */
public class OsgiUtilTest  {

    /*
     * An independent unit test for OsgiUtil.translateBundleURLToJarURL() would be nice, but appears
     * complicated to do without either creating and installing a custom URL handler or using
     * one of the Mock frameworks that can mock final classes like URL.
     */

    @Test
    public void testGetBean() {
        MockBeanFactory mockBeanFactory = new MockBeanFactory();

        Object mockBean = OsgiUtil.getBean(mockBeanFactory, "1000");
        Assert.assertNotNull("OsgiUtil getBean() on mock bean factory returned null ?", mockBean);
        Assert.assertTrue("Mock bean from mock bean factory has mismatched id ?", mockBeanFactory.beanMatchesId(mockBean, "1000"));

        mockBean = OsgiUtil.getBean(new Object(), "1000");  // Logs an error (coverage only).
        Assert.assertNull("OsgiUtil getBean() on normal object found a getBean() method ?", mockBean);
    }

    @Test
    public void testContainsBean() {
        MockBeanFactory mockBeanFactory = new MockBeanFactory();

        Assert.assertTrue("OsgiUtil containsBean() on mock bean factory with numeric id is false ?", OsgiUtil.containsBean(mockBeanFactory, "1000"));
        Assert.assertFalse("OsgiUtil containsBean() on mock bean factory with nonnumeric id is true ?", OsgiUtil.containsBean(mockBeanFactory, "NotANumber"));
        Assert.assertFalse("OsgiUtil containsBean() on mock bean factory with null id is true ?", OsgiUtil.containsBean(mockBeanFactory, null));
    }

    @Test
    public void testGenerateJavaVersionForSystemPackages() {
        // Some test patterns copied from OgnlRuntimeTest.testMajorJavaVersionParse() in OGNL 3.2.x.
        // Pre-JDK 9 version strings.
        Assert.assertEquals("JDK 5 generate java version failed ?", "1.5", OsgiUtil.generateJavaVersionForSystemPackages("1.5"));
        Assert.assertEquals("JDK 5 generate java version failed ?", "1.5", OsgiUtil.generateJavaVersionForSystemPackages("1.5.0"));
        Assert.assertEquals("JDK 5 generate java version failed ?", "1.5", OsgiUtil.generateJavaVersionForSystemPackages("1.5.0_21-b11"));
        Assert.assertEquals("JDK 6 generate java version failed ?", "1.6", OsgiUtil.generateJavaVersionForSystemPackages("1.6"));
        Assert.assertEquals("JDK 6 generate java version failed ?", "1.6", OsgiUtil.generateJavaVersionForSystemPackages("1.6.0"));
        Assert.assertEquals("JDK 6 generate java version failed ?", "1.6", OsgiUtil.generateJavaVersionForSystemPackages("1.6.0_43-b19"));
        Assert.assertEquals("JDK 7 generate java version failed ?", "1.7", OsgiUtil.generateJavaVersionForSystemPackages("1.7"));
        Assert.assertEquals("JDK 7 generate java version failed ?", "1.7", OsgiUtil.generateJavaVersionForSystemPackages("1.7.0"));
        Assert.assertEquals("JDK 7 generate java version failed ?", "1.7", OsgiUtil.generateJavaVersionForSystemPackages("1.7.0_79-b15"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "1.8", OsgiUtil.generateJavaVersionForSystemPackages("1.8"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "1.8", OsgiUtil.generateJavaVersionForSystemPackages("1.8.0"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "1.8", OsgiUtil.generateJavaVersionForSystemPackages("1.8.0_201-b20"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "1.8", OsgiUtil.generateJavaVersionForSystemPackages("1.8.0-someopenjdkstyle"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "1.8", OsgiUtil.generateJavaVersionForSystemPackages("1.8.0_201-someopenjdkstyle"));
        // JDK 9 and later version strings.
        Assert.assertEquals("JDK 9 generate java version failed ?", "9.0", OsgiUtil.generateJavaVersionForSystemPackages("9"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "9.0", OsgiUtil.generateJavaVersionForSystemPackages("9-ea+19"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "9.0", OsgiUtil.generateJavaVersionForSystemPackages("9+100"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "9.0", OsgiUtil.generateJavaVersionForSystemPackages("9-ea+19"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "9.1", OsgiUtil.generateJavaVersionForSystemPackages("9.1.3+15"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "9.0", OsgiUtil.generateJavaVersionForSystemPackages("9-someopenjdkstyle"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "10.0", OsgiUtil.generateJavaVersionForSystemPackages("10"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "10.0", OsgiUtil.generateJavaVersionForSystemPackages("10-ea+11"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "10.0", OsgiUtil.generateJavaVersionForSystemPackages("10+10"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "10.0", OsgiUtil.generateJavaVersionForSystemPackages("10-ea+11"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "10.1", OsgiUtil.generateJavaVersionForSystemPackages("10.1.3+15"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "10.0", OsgiUtil.generateJavaVersionForSystemPackages("10-someopenjdkstyle"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "11.0", OsgiUtil.generateJavaVersionForSystemPackages("11"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "11.0", OsgiUtil.generateJavaVersionForSystemPackages("11-ea+22"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "11.0", OsgiUtil.generateJavaVersionForSystemPackages("11+33"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "11.0", OsgiUtil.generateJavaVersionForSystemPackages("11-ea+19"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "11.1", OsgiUtil.generateJavaVersionForSystemPackages("11.1.3+15"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "11.0", OsgiUtil.generateJavaVersionForSystemPackages("11-someopenjdkstyle"));
    }

    @Test
    public void testGenerateJava_SE_SystemPackageVersionString() {
        // Some test patterns copied from OgnlRuntimeTest.testMajorJavaVersionParse() in OGNL 3.2.x.
        // Pre-JDK 9 version strings.
        Assert.assertEquals("JDK 5 generate java version failed ?", "0.0.0.JavaSE_001_005", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.5"));
        Assert.assertEquals("JDK 5 generate java version failed ?", "0.0.0.JavaSE_001_005", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.5.0"));
        Assert.assertEquals("JDK 5 generate java version failed ?", "0.0.0.JavaSE_001_005", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.5.0_21-b11"));
        Assert.assertEquals("JDK 6 generate java version failed ?", "0.0.0.JavaSE_001_006", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.6"));
        Assert.assertEquals("JDK 6 generate java version failed ?", "0.0.0.JavaSE_001_006", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.6.0"));
        Assert.assertEquals("JDK 6 generate java version failed ?", "0.0.0.JavaSE_001_006", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.6.0_43-b19"));
        Assert.assertEquals("JDK 7 generate java version failed ?", "0.0.0.JavaSE_001_007", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.7"));
        Assert.assertEquals("JDK 7 generate java version failed ?", "0.0.0.JavaSE_001_007", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.7.0"));
        Assert.assertEquals("JDK 7 generate java version failed ?", "0.0.0.JavaSE_001_007", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.7.0_79-b15"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "0.0.0.JavaSE_001_008", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.8"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "0.0.0.JavaSE_001_008", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.8.0"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "0.0.0.JavaSE_001_008", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.8.0_201-b20"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "0.0.0.JavaSE_001_008", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.8.0-someopenjdkstyle"));
        Assert.assertEquals("JDK 8 generate java version failed ?", "0.0.0.JavaSE_001_008", OsgiUtil.generateJava_SE_SystemPackageVersionString("1.8.0_201-someopenjdkstyle"));
        // JDK 9 and later version strings.
        Assert.assertEquals("JDK 9 generate java version failed ?", "0.0.0.JavaSE_009", OsgiUtil.generateJava_SE_SystemPackageVersionString("9"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "0.0.0.JavaSE_009", OsgiUtil.generateJava_SE_SystemPackageVersionString("9-ea+19"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "0.0.0.JavaSE_009", OsgiUtil.generateJava_SE_SystemPackageVersionString("9+100"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "0.0.0.JavaSE_009", OsgiUtil.generateJava_SE_SystemPackageVersionString("9-ea+19"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "0.0.0.JavaSE_009", OsgiUtil.generateJava_SE_SystemPackageVersionString("9.1.3+15"));
        Assert.assertEquals("JDK 9 generate java version failed ?", "0.0.0.JavaSE_009", OsgiUtil.generateJava_SE_SystemPackageVersionString("9-someopenjdkstyle"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "0.0.0.JavaSE_010", OsgiUtil.generateJava_SE_SystemPackageVersionString("10"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "0.0.0.JavaSE_010", OsgiUtil.generateJava_SE_SystemPackageVersionString("10-ea+11"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "0.0.0.JavaSE_010", OsgiUtil.generateJava_SE_SystemPackageVersionString("10+10"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "0.0.0.JavaSE_010", OsgiUtil.generateJava_SE_SystemPackageVersionString("10-ea+11"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "0.0.0.JavaSE_010", OsgiUtil.generateJava_SE_SystemPackageVersionString("10.1.3+15"));
        Assert.assertEquals("JDK 10 generate java version failed ?", "0.0.0.JavaSE_010", OsgiUtil.generateJava_SE_SystemPackageVersionString("10-someopenjdkstyle"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "0.0.0.JavaSE_011", OsgiUtil.generateJava_SE_SystemPackageVersionString("11"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "0.0.0.JavaSE_011", OsgiUtil.generateJava_SE_SystemPackageVersionString("11-ea+22"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "0.0.0.JavaSE_011", OsgiUtil.generateJava_SE_SystemPackageVersionString("11+33"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "0.0.0.JavaSE_011", OsgiUtil.generateJava_SE_SystemPackageVersionString("11-ea+19"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "0.0.0.JavaSE_011", OsgiUtil.generateJava_SE_SystemPackageVersionString("11.1.3+15"));
        Assert.assertEquals("JDK 11 generate java version failed ?", "0.0.0.JavaSE_011", OsgiUtil.generateJava_SE_SystemPackageVersionString("11-someopenjdkstyle"));
    }

    private class MockBeanFactory {

        public Object getBean(String beanId) {
            return "MockBean: " + beanId;
        }

        public boolean containsBean(String beanId) {
            boolean result = false;

            if (beanId != null) {
                try {
                    Integer.parseInt(beanId);
                    result = true;
                } catch (NumberFormatException nfe) {
                    result = false;
                }
            }

            return result;
        }

        public final boolean beanMatchesId(Object mockBean, String beanId) {
            return mockBean.equals("MockBean: " + beanId);
        }

    }

}
