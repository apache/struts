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
package org.apache.struts2.json;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import junit.framework.TestCase;

import org.apache.struts2.json.annotations.SMDMethod;

/**
 * Tests that the SMDMethod annotation can be found in interfaces when
 * ignoreSMDMethodInterface is false
 */
public class SMDMethodInterfaceTest extends TestCase {

    public interface InterfaceA {
        String getA();
    }

    public interface InterfaceB {
        @SMDMethod
        String getB();
    }

    public interface InterfaceC {
        String getC();
    }

    public interface InterfaceD {
        String getD();
    }

    public interface InterfaceE {
        String getE();
    }

    public static class ClassA extends ClassB implements InterfaceA {

        private String a;
        private String z;

        public ClassA(String a, String b, String c, String d, String e, String x, String y, String z) {
            super(b, c, d, e, x, y);
            this.a = a;
            this.z = z;
        }

        public String getA() {
            return a;
        }

        @SMDMethod
        public String getZ() {
            return z;
        }
    }

    public static class ClassB extends ClassC implements InterfaceB, InterfaceC {

        private String b;
        private String c;
        private String y;

        public ClassB(String b, String c, String d, String e, String x, String y) {
            super(d, e, x);
            this.b = b;
            this.c = c;
            this.y = y;
        }

        public String getC() {
            return c;
        }

        public String getB() {
            return b;
        }

        public String getY() {
            return y;
        }
    }

    public static class ClassC implements InterfaceD, InterfaceE {

        private String d;
        private String e;
        private String x;

        public ClassC(String d, String e, String x) {
            this.d = d;
            this.e = e;
            this.x = x;
        }

        public String getD() {
            return d;
        }

        public String getE() {
            return e;
        }

        @SMDMethod
        public String getX() {
            return x;
        }
    }

    /**
     * Asserts that the SMDMethod annotation is only detected on the classes
     * when ignoreSMDMethodInterfaces is true
     */
    public void testBaseClassOnly() {
        Method[] smdMethodsA = JSONUtil.listSMDMethods(ClassA.class, true);
        assertEquals(2, smdMethodsA.length);
        assertEquals("getZ", smdMethodsA[0].getName());
        assertEquals("getX", smdMethodsA[1].getName());

        Method[] smdMethodsB = JSONUtil.listSMDMethods(ClassB.class, true);
        assertEquals(1, smdMethodsB.length);
        assertEquals("getX", smdMethodsB[0].getName());

        Method[] smdMethodsC = JSONUtil.listSMDMethods(ClassC.class, true);
        assertEquals(1, smdMethodsC.length);
        assertEquals("getX", smdMethodsC[0].getName());
    }

    /**
     * Asserts that the SMDMethod annotation is also detected on the interfaces
     * and superclasses when ignoreSMDMethodInterfaces is false
     */
    public void testInterfaces() {
        Method[] smdMethodsA = JSONUtil.listSMDMethods(ClassA.class, false);
        assertEquals(3, smdMethodsA.length);
        assertEquals("getZ", smdMethodsA[0].getName());
        assertEquals("getX", smdMethodsA[1].getName());
        assertEquals("getB", smdMethodsA[2].getName());

        Method[] smdMethodsB = JSONUtil.listSMDMethods(ClassB.class, false);
        assertEquals(2, smdMethodsB.length);
        assertEquals("getX", smdMethodsB[0].getName());
        assertEquals("getB", smdMethodsB[1].getName());

        Method[] smdMethodsC = JSONUtil.listSMDMethods(ClassC.class, false);
        assertEquals(1, smdMethodsC.length);
        assertEquals("getX", smdMethodsC[0].getName());
    }

    /**
     * This is the important case: detects the SMDMethod annotation on a proxy
     */
    public void testWithProxy() {

        InvocationHandler handler = new InvocationHandler() {
            // dummy implementation
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        };
        // proxy is proxy to an impl of ClassA
        InterfaceA proxy = (InterfaceA) Proxy.newProxyInstance(ClassA.class.getClassLoader(), new Class[] {
                InterfaceA.class, InterfaceB.class, InterfaceC.class }, handler);

        // first, without the recursion
        Method[] smdMethodsA = JSONUtil.listSMDMethods(proxy.getClass(), true);
        assertEquals(0, smdMethodsA.length);

        // now with the recursion
        Method[] smdMethodsB = JSONUtil.listSMDMethods(proxy.getClass(), false);
        assertEquals(1, smdMethodsB.length);
        assertEquals("getB", smdMethodsB[0].getName());
    }
}
