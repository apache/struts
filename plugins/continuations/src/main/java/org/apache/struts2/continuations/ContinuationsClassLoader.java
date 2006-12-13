/*
 * $Id: Dispatcher.java 484733 2006-12-08 20:16:16Z mrdon $
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
package org.apache.struts2.continuations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Uses the Rife continuations classloader to bytecode enhance actions.  Only
 * enhances actions in the configured package.
 */
public class ContinuationsClassLoader extends ClassLoader {
    
    private String base;
    private ClassLoader parent;
    
    private static final Log LOG = LogFactory.getLog(ContinuationsClassLoader.class);

    @Inject(value=StrutsConstants.STRUTS_CONTINUATIONS_PACKAGE, required=false)
    public void setContinuationPackage(String continuationPackage) {
        
        // This reflection silliness is to ensure Rife is optional
        Class contConfig = null;
        try {
            contConfig = Class.forName("com.uwyn.rife.continuations.ContinuationConfig");
        } catch (ClassNotFoundException ex) {
            throw new XWorkException("Unable to use continuations package, as the Rife " +
                    "continuations jar is missing", ex);
        }
        try {
            Method m = contConfig.getMethod("setInstance", contConfig);
            m.invoke(contConfig, new StrutsContinuationConfig());
        } catch (NoSuchMethodException ex) {
            throw new XWorkException("Incorrect version of the Rife continuation library", ex);
        } catch (IllegalAccessException ex) {
            throw new XWorkException("Incorrect version of the Rife continuation library", ex);
        } catch (InvocationTargetException ex) {
            throw new XWorkException("Unable to initialize the Rife continuation library", ex);
        }
        this.base = continuationPackage;
        this.parent = Thread.currentThread().getContextClassLoader();
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        if (validName(name)) {
            Class clazz = findLoadedClass(name);
            if (clazz == null) {
                try {
                    byte[] bytes = com.uwyn.rife.continuations.util.ClassByteUtil.getBytes(name, parent);
                    if (bytes == null) {
                        throw new ClassNotFoundException(name);
                    }

                    byte[] resume_bytes = null;
                    try {
                        resume_bytes = com.uwyn.rife.continuations.ContinuationInstrumentor.instrument(bytes, name, false);
                    } catch (ClassNotFoundException e) {
                        // this can happen when the Rife Continuations code gets broken (there are bugs in it still, ya know!)
                        // rather than making a big deal, we'll quietly log this and move on
                        // when more people are using continuations, perhaps we'll raise the log level
                        LOG.debug("Error instrumenting with RIFE/Continuations, " +
                                "loading class normally without continuation support", e);
                    }

                    if (resume_bytes == null) {
                        return parent.loadClass(name);
                    } else {
                        return defineClass(name, resume_bytes, 0, resume_bytes.length);
                    }
                } catch (IOException e) {
                    throw new XWorkException("Continuation error", e);
                }
            } else {
                return clazz;
            }
        } else {
            return parent.loadClass(name);
        }
    }

    private boolean validName(String name) {
        return name.startsWith(base + ".");
    }
}
