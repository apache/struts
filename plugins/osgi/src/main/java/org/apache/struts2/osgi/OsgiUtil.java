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
package org.apache.struts2.osgi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.osgi.framework.Bundle;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class OsgiUtil {

    private static final Logger LOG = LogManager.getLogger(OsgiUtil.class);

    /**
     * A bundle is a jar, and a bundle URL will be useless to clients, this method translates
     * a URL to a resource inside a bundle from "bundle:something/path" to "jar:file:bundlelocation!/path"
     *
     * @param bundleUrl URL to translate
     * @param bundle the bundle
     *
     * @return translated URL
     *
     * @throws MalformedURLException if URL is malformed
     */
    public static URL translateBundleURLToJarURL(URL bundleUrl, Bundle bundle) throws MalformedURLException {
        if (bundleUrl != null && "bundle".equalsIgnoreCase(bundleUrl.getProtocol())) {
            StringBuilder sb = new StringBuilder("jar:");
            sb.append(bundle.getLocation());
            sb.append("!");
            sb.append(bundleUrl.getFile());
            return new URL(sb.toString());
        }

        return bundleUrl;
    }

    /**
     * Calls getBean() on the passed object using reflection. Used on Spring context
     * because they are loaded from bundles (in another class loader)
     *
     * @param beanFactory bean factory
     * @param beanId id of bean
     *
     * @return the object found
     */
    public static Object getBean(Object beanFactory, String beanId) {
        try {
            Method getBeanMethod = beanFactory.getClass().getMethod("getBean", String.class);
            return getBeanMethod.invoke(beanFactory, beanId);
        } catch (Exception ex) {
            LOG.error("Unable to call getBean() on object of type [{}], with bean id [{}]",  beanFactory.getClass().getName(), beanId, ex);
        }

        return null;
    }

    /**
     * Calls containsBean on the passed object using reflection. Used on Spring context
     * because they are loaded from bundles (in another class loader)
     *
     * @param beanFactory bean factory
     * @param beanId id of bean
     *
     * @return true if bean factory contains bean with bean id
     */
    public static boolean containsBean(Object beanFactory, String beanId) {
        try {
            Method getBeanMethod = beanFactory.getClass().getMethod("containsBean", String.class);
            return (Boolean) getBeanMethod.invoke(beanFactory, beanId);
        } catch (Exception ex) {
            LOG.error("Unable to call containsBean() on object of type [{}], with bean id [{}]", beanFactory.getClass().getName(), beanId, ex);
        }

        return false;
    }

    /**
     * Attempt to generate an OSGi compatible Java version String from the System "java.version" property,
     * with the form (where x is the major version number): 1.x for Java 8 and earlier, x.y for Java 9+.
     * See <a href="https://openjdk.java.net/jeps/223">JEP 223: New Version-String Scheme</a> for details
     * on version naming changes.
     * 
     * @param systemJavaVersion a Java version string from System.getProperty("java.version") or equivalent.
     * @return a String of the form (where x is the major version number): 1.x for Java 8 and earlier, x.y for Java 9+.
     */
    public static String generateJavaVersionForSystemPackages(String systemJavaVersion) {
        if (systemJavaVersion == null || systemJavaVersion.isEmpty()) {
            throw new IllegalArgumentException("Cannot parse Java version from null or empty string");
        } else {
            String parsedResult;
            final int dotIndex1 = systemJavaVersion.indexOf('.');
            final int dotIndex2 = (dotIndex1 > 0 ? systemJavaVersion.indexOf('.', dotIndex1 + 1) : -1);
            if (dotIndex1 > 0 && dotIndex2 > 0) {
                parsedResult = systemJavaVersion.substring(0, dotIndex2);  // Assuming Java 8 or older style, or Java 9+ with a minor or security update.
            } else if (dotIndex1 > 0) {
                parsedResult = systemJavaVersion;  // Assuming a truncated Java 8 or older style, or Java 9+ with a minor or security update.
            } else if (dotIndex1 == -1) {
                final int minusIndex = systemJavaVersion.indexOf('-');
                final int plusIndex = systemJavaVersion.indexOf('+');
                if (minusIndex > 0) {
                    parsedResult = systemJavaVersion.substring(0, minusIndex);  // Assuming Java 9+ early-access
                } else if (plusIndex > 0) {
                    parsedResult = systemJavaVersion.substring(0, plusIndex);  // Assuming Java 9+ java.runime.version or java.vm.version string
                } else {
                    parsedResult = systemJavaVersion;
                }
                try {
                    final int firstNumber = Integer.parseInt(parsedResult);
                    if (firstNumber >= 9) {
                        parsedResult = parsedResult + ".0";  // Assuming Java 9+ and no minor or security update, append standard value.
                    } else {
                        throw new IllegalArgumentException("Single digit Java version string less than 9 (nonsense)");
                    }
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Cannot parse Java version string (probable non-numeric start)", ex);
                }
            } else {
                throw new IllegalArgumentException("Cannot parse Java version from a string starting with a '.'");
            }
            return parsedResult;
        }
    }

    /**
     * Attempt to generate an OSGi compatible Java SE system package version String from the System "java.version" property,
     * with the form (where x or xx is the major version number): 0.0.0.JavaSE_001_00x for Java 8 and earlier (e.g. 0.0.0.JavaSE_001_008),
     * 0.0.0.JavaSE_0xx for Java 9+ (e.g. 0.0.0.JavaSE_009 for Java 9, 0.0.0.JavaSE_011 for Java 11).
     * 
     * @param systemJavaVersion a Java version string from System.getProperty("java.version") or equivalent.
     * @return a String of the form (where x or xx is the major version number): JavaSE_001_00x for Java 8 and earlier, JavaSE_0xx for Java 9+.
     */
    public static String generateJava_SE_SystemPackageVersionString(String systemJavaVersion) {
        final String javaVersionForSystemPackages = generateJavaVersionForSystemPackages(systemJavaVersion);
        final int dotIndex = javaVersionForSystemPackages.indexOf('.');
        if (dotIndex > 0) {
            final String generatedResult;
            try {
                final String prefix = javaVersionForSystemPackages.substring(0, dotIndex);
                final String suffix = javaVersionForSystemPackages.substring(dotIndex + 1);
                final int firstNumber = Integer.parseInt(prefix);
                final int secondNumber = Integer.parseInt(suffix);
                if (firstNumber >= 9) {
                    generatedResult = String.format("0.0.0.JavaSE_%03d", firstNumber);  // Assuming Java 9+
                } else {
                    generatedResult = String.format("0.0.0.JavaSE_001_%03d", secondNumber);  // Assuming Java 8 or earlier
                }
                return generatedResult;
            } catch (Exception ex) {
                throw new IllegalArgumentException("Cannot parse Java version string (probable non-numeric start)", ex);
            }
        } else {
                throw new IllegalArgumentException("Cannot parse Java version from a system packages string missing or starting with a '.'");
        }
    }
}
