package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.annotation.Dummy2Class;
import com.opensymphony.xwork2.util.annotation.DummyClass;
import com.opensymphony.xwork2.util.annotation.DummyClassExt;
import com.opensymphony.xwork2.util.annotation.MyAnnotation;
import com.opensymphony.xwork2.util.annotation.MyAnnotation2;
import com.opensymphony.xwork2.util.annotation.MyAnnotationI;

import junit.framework.TestCase;

import java.lang.annotation.Retention;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

/**
 * @author Dan Oxlade, dan d0t oxlade at gmail d0t c0m
 */
public class AnnotationUtilsTest extends TestCase {

    public void testGetAnnotationMeta() throws Exception {
        assertNotNull(AnnotationUtils.getAnnotation(DummyClass.class.getMethod("methodWithAnnotation"), Retention.class));
    }

    public void testGetAnnotation() throws Exception {
        assertNull(AnnotationUtils.getAnnotation(DummyClass.class.getMethod("methodWithAnnotation"), Deprecated.class));
        assertNotNull(AnnotationUtils.getAnnotation(DummyClass.class.getMethod("methodWithAnnotation"), MyAnnotation.class));
    }

    public void testFindAnnotationFromSuperclass() throws Exception {
        assertNotNull(AnnotationUtils.findAnnotation(DummyClassExt.class.getMethod("methodWithAnnotation"), MyAnnotation.class));
    }

    public void testFindAnnotationFromInterface() throws Exception {
        assertNotNull(AnnotationUtils.findAnnotation(DummyClass.class.getMethod("interfaceMethodWithAnnotation"), MyAnnotationI.class));
    }

    public void testFindAnnotation() throws Exception {
        assertNotNull(AnnotationUtils.findAnnotation(DummyClassExt.class.getMethod("anotherAnnotatedMethod"), MyAnnotation2.class));
    }

    @SuppressWarnings("unchecked")
    public void testGetAnnotatedMethodsIncludingSuperclassAndInterface() throws Exception {

        Collection<? extends AnnotatedElement> ans = AnnotationUtils.getAnnotatedMethods(DummyClassExt.class, Deprecated.class, MyAnnotation.class, MyAnnotation2.class, MyAnnotationI.class);
        assertEquals(5, ans.size());
    }

    @SuppressWarnings("unchecked")
    public void testGetAnnotatedMethodsWithoutAnnotationArgs() throws Exception {
        Collection<? extends AnnotatedElement> ans = AnnotationUtils.getAnnotatedMethods(DummyClass.class);
        assertTrue(ans.size() == 2);
        assertTrue(ans.contains(DummyClass.class.getMethod("methodWithAnnotation")));
        assertTrue(ans.contains(DummyClass.class.getDeclaredMethod("privateMethodWithAnnotation")));
    }

    @SuppressWarnings("unchecked")
    public void testGetAnnotatedMethodsWithAnnotationArgs() throws Exception {
        Collection<? extends AnnotatedElement> ans = AnnotationUtils.getAnnotatedMethods(DummyClass.class, Deprecated.class);
        assertTrue(ans.isEmpty());

        ans = AnnotationUtils.getAnnotatedMethods(DummyClass.class, Deprecated.class, MyAnnotation.class);
        assertEquals(1, ans.size());

        ans = AnnotationUtils.getAnnotatedMethods(DummyClass.class, MyAnnotation.class);
        assertEquals(1, ans.size());

        ans = AnnotationUtils.getAnnotatedMethods(DummyClass.class, MyAnnotation.class, MyAnnotation2.class);
        assertEquals(2, ans.size());

        ans = AnnotationUtils.getAnnotatedMethods(DummyClassExt.class, MyAnnotation.class, MyAnnotation2.class);
        assertEquals(4, ans.size());
    }

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
