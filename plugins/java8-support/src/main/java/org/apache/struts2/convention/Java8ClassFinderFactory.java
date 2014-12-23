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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.util.finder.ClassFinder;
import com.opensymphony.xwork2.util.finder.ClassFinderFactory;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.Test;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

public class Java8ClassFinderFactory implements ClassFinderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(Java8ClassFinderFactory.class);

    public Java8ClassFinderFactory() {
        try {
            LOG.trace("Checking if ASM5 is on the classpath....");
            Class.forName("org.objectweb.asm.MethodVisitor");
            LOG.trace("Proper version of ASM5 is in use!");
        } catch (ClassNotFoundException e) {
            LOG.warn("ASM5 is missing or older version is used! If you use Maven, please exclude asm.jar and asm-commons.jar version 3 from xwork!");
        }
    }

    public ClassFinder buildClassFinder(ClassLoaderInterface classLoaderInterface, Collection<URL> urls, boolean extractBaseInterfaces, Set<String> protocols, Test<String> classNameFilter) {
        LOG.debug("Creating new instance of Java8ClassFinder");
        return new Java8ClassFinder(classLoaderInterface, urls, extractBaseInterfaces, protocols, classNameFilter);
    }

}
