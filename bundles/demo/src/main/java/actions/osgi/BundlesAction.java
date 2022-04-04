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
package actions.osgi;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.osgi.interceptor.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * This action shows how to interact with the OSGi container, using the OSGi interceptor
 */
@ResultPath("/content")
public class BundlesAction extends ActionSupport implements BundleContextAware {
    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Bundle[] getBundles() {
        return bundleContext.getBundles();
    }
}
