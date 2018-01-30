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
import com.opensymphony.xwork2.util.annotation.Dummy3Class;
import com.opensymphony.xwork2.util.annotation.DummyClass;
import com.opensymphony.xwork2.util.annotation.DummyClassExt;
import com.opensymphony.xwork2.util.annotation.MyAnnotation;
import com.opensymphony.xwork2.util.annotation.MyAnnotation2;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

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

    public void testFindAnnotationOnParents() {
        MyAnnotation2 ns = AnnotationUtils.findAnnotation(Dummy3Class.class, MyAnnotation2.class);
        assertNotNull(ns);
        assertEquals("abstract-abstract", ns.value());
    }

    public void testFindAnnotationsOnAll() {
        List<MyAnnotation> annotations = AnnotationUtils.findAnnotations(DummyClassExt.class, MyAnnotation.class);

        assertThat(annotations)
                .isNotNull()
                .isNotEmpty()
                .hasSize(5);

        Set<String> values = new HashSet<>();
        for (MyAnnotation annotation : annotations) {
            values.add(annotation.value());
        }
        assertThat(values).contains("class-test", "package-test", "interface-test", "package2-test");
    }

}
