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
package com.opensymphony.xwork2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * ObjectFactory that returns a FooProxy in the buildBean if the clazz is FooAction 
 */
public class ProxyObjectFactory extends ObjectFactory {

    public ProxyObjectFactory() {
    }

    /**
     * It returns an instance of the bean except if the class is FooAction. 
     * In this case, it returns a FooProxy of it.
     */
    @Override
    public Object buildBean(Class clazz, Map<String, Object> extraContext)
        throws Exception {
        Object bean = super.buildBean(clazz, extraContext);
        if(clazz.equals(ProxyInvocationAction.class)) {
            return Proxy.newProxyInstance(bean.getClass()
                .getClassLoader(), bean.getClass().getInterfaces(),
                new ProxyInvocationProxy(bean));

        }
        return bean;
    }
    
    /**
     * Simple proxy that just invokes the method on the target on the invoke method
     */
    public class ProxyInvocationProxy implements InvocationHandler {

        private Object target;

        public ProxyInvocationProxy(Object target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
            return m.invoke(target, args);
        }
    }
}
