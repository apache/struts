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
     * @throws MalformedURLException if url is malformed
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
     * Calls getBean() on the passed object using refelection. Used on Spring context
     * because they are loaded from bundles (in anothe class loader)
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
     * Calls containsBean on the passed object using refelection. Used on Spring context
     * because they are loaded from bundles (in anothe class loader)
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
}
