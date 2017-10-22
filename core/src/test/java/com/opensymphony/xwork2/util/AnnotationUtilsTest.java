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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.annotation.Dummy2Class;
import com.opensymphony.xwork2.util.annotation.DummyClass;
import com.opensymphony.xwork2.util.annotation.MyAnnotation;

import junit.framework.TestCase;

/**
 * @author Dan Oxlade, dan d0t oxlade at gmail d0t c0m
 */
public class AnnotationUtilsTest extends TestCase {

    public void testFindAnnotationOnClass() {
        MyAnnotation a1 = AnnotationUtils.findAnnotation(DummyClass.class, MyAnnotation.class);
        assertNotNull(a1);
        assertEquals("class-test", a1.value());
    }

    public void testFindAnnotationOnPackage() {
        MyAnnotation ns = AnnotationUtils.findAnnotation(Dummy2Class.class, MyAnnotation.class);
        assertNotNull(ns);
        assertEquals("package-test", ns.value());
    }

}
