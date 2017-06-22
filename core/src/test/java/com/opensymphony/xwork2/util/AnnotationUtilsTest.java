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
