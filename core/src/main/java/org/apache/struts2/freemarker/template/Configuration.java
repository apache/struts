/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.freemarker.template;

import freemarker.cache.TemplateLoader;
import freemarker.core.BugException;
import freemarker.template.Version;
import freemarker.template.utility.ClassUtil;

public class Configuration extends freemarker.template.Configuration {

    public Configuration() {
        super(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public Configuration(Version version) {
        super(version);
    }

    @Override
    public void setServletContextForTemplateLoading(Object servletContext, String path) {
        try {
            // Don't introduce linking-time dependency on servlets
            final Class webappTemplateLoaderClass = ClassUtil.forName("org.apache.struts2.freemarker.cache.WebappTemplateLoader");

            // Don't introduce linking-time dependency on servlets
            final Class servletContextClass = ClassUtil.forName("jakarta.servlet.ServletContext");

            final Class[] constructorParamTypes;
            final Object[] constructorParams;
            if (path == null) {
                constructorParamTypes = new Class[] { servletContextClass };
                constructorParams = new Object[] { servletContext };
            } else {
                constructorParamTypes = new Class[] { servletContextClass, String.class };
                constructorParams = new Object[] { servletContext, path };
            }

            setTemplateLoader((TemplateLoader)webappTemplateLoaderClass
                            .getConstructor(constructorParamTypes)
                            .newInstance(constructorParams));
        } catch (Exception e) {
            throw new BugException(e);
        }
    }
}
