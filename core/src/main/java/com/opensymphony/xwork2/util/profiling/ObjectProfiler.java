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

/*
 * Copyright (c) 2002-2003, Atlassian Software Systems Pty Ltd All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *     * Neither the name of Atlassian Software Systems Pty Ltd nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.opensymphony.xwork2.util.profiling;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @deprecated will be dropped with next major release (2.6)
 */
@Deprecated
public class ObjectProfiler {

    /**
     * <p>
     * Given a class, and an interface that it implements, return a proxied version of the class that implements
     * the interface.
     * </p>
     *
     * <p>
     * The usual use of this is to profile methods from Factory objects:
     * </p>
     *
     * <pre>
     * public PersistenceManager getPersistenceManager()
     * {
     *   return new DefaultPersistenceManager();
     * }
     *
     * instead write:
     * public PersistenceManager getPersistenceManager()
     * {
     *   return ObjectProfiler.getProfiledObject(PersistenceManager.class, new DefaultPersistenceManager());
     * }
     * </pre>
     *
     * <p>
     * A side effect of this is that you will no longer be able to downcast to DefaultPersistenceManager.  This is probably a *good* thing.
     * </p>
     *
     * @param interfaceClazz The interface to implement.
     * @param o              The object to proxy
     * @return A proxied object, or the input object if the interfaceClazz wasn't an interface.
     */
    public static Object getProfiledObject(Class interfaceClazz, Object o) {
        //if we are not active - then do nothing
        if (!UtilTimerStack.isActive()) {
            return o;
        }

        //this should always be true - you shouldn't be passing something that isn't an interface
        if (interfaceClazz.isInterface()) {
            InvocationHandler timerHandler = new TimerInvocationHandler(o);
            return Proxy.newProxyInstance(interfaceClazz.getClassLoader(),
                    new Class[]{interfaceClazz}, timerHandler);
        } else {
            return o;
        }
    }

    /**
     * A profiled call {@link Method#invoke(java.lang.Object, java.lang.Object[])}. If {@link UtilTimerStack#isActive() }
     * returns false, then no profiling is performed.
     *
     * @param target target method
     * @param value value
     * @param args arguments
     *
     * @return target object
     *
     * @throws IllegalAccessException in case of access errors
     * @throws InvocationTargetException in case of invocation errors
     */
    public static Object profiledInvoke(Method target, Object value, Object[] args) throws IllegalAccessException, InvocationTargetException {
        //if we are not active - then do nothing
        if (!UtilTimerStack.isActive()) {
            return target.invoke(value, args);
        }

        String logLine = getTrimmedClassName(target) + "." + target.getName() + "()";

        UtilTimerStack.push(logLine);
        try {
            Object returnValue = target.invoke(value, args);

            //if the return value is an interface then we should also proxy it!
            if (returnValue != null && target.getReturnType().isInterface()) {
                InvocationHandler timerHandler = new TimerInvocationHandler(returnValue);
                return Proxy.newProxyInstance(returnValue.getClass().getClassLoader(),
                        new Class[]{target.getReturnType()}, timerHandler);
            } else {
                return returnValue;
            }
        } finally {
            UtilTimerStack.pop(logLine);
        }
    }

    /**
     * Given a method, get the Method name, with no package information.
     *
     * @param method method
     *
     * @return method name, with no package information
     */
    public static String getTrimmedClassName(Method method) {
        String classname = method.getDeclaringClass().getName();
        return classname.substring(classname.lastIndexOf('.') + 1);
    }

}

/**
 * @deprecated will be dropped with next major release (2.6)
 */
@Deprecated
class TimerInvocationHandler implements InvocationHandler {
    protected Object target;

    public TimerInvocationHandler(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("Target Object passed to timer cannot be null");
        }
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return ObjectProfiler.profiledInvoke(method, target, args);
    }

}
