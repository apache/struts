package com.opensymphony.xwork2.util;

import junit.framework.TestCase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

/**
 * @author Dan Oxlade, dan d0t oxlade at gmail d0t c0m
 */
public class AnnotationUtilsTest extends TestCase {


    public void testIsAnnotatedByWithoutAnnotationArgsReturnsFalse() throws Exception {

        assertFalse(AnnotationUtils.isAnnotatedBy(DummyClass.class));
        assertFalse(AnnotationUtils.isAnnotatedBy(DummyClass.class.getMethod("methodWithAnnotation")));

    }

    @SuppressWarnings("unchecked")
    public void testIsAnnotatedByWithSingleAnnotationArgMatchingReturnsTrue() throws Exception {

        assertTrue(AnnotationUtils.isAnnotatedBy(DummyClass.class.getMethod("methodWithAnnotation"), MyAnnotation.class));

    }

    @SuppressWarnings("unchecked")
    public void testIsAnnotatedByWithMultiAnnotationArgMatchingReturnsTrue() throws Exception {

        assertFalse(AnnotationUtils.isAnnotatedBy(DummyClass.class.getMethod("methodWithAnnotation"), Deprecated.class));
        assertTrue(AnnotationUtils.isAnnotatedBy(DummyClass.class.getMethod("methodWithAnnotation"), MyAnnotation.class, Deprecated.class));
        assertTrue(AnnotationUtils.isAnnotatedBy(DummyClass.class.getMethod("methodWithAnnotation"), Deprecated.class, MyAnnotation.class));

    }

    public void testGetAnnotedMethodsWithoutAnnotationArgs() throws Exception {

        Collection<? extends AnnotatedElement> ans = AnnotationUtils.getAnnotatedMethods(DummyClass.class);

        assertTrue(ans.size() == 1);

        assertEquals(ans.iterator().next(), DummyClass.class.getMethod("methodWithAnnotation"));

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
        assertEquals(1, ans.size());

        ans = AnnotationUtils.getAnnotatedMethods(DummyClassExt.class, MyAnnotation.class, MyAnnotation2.class);
        assertEquals(2, ans.size());

    }

    /**
     * *****************************************************************
     * <p/>
     * TEST CLASSES
     */
    private static class DummyClass {

        public DummyClass() {
        }

        @MyAnnotation
        public void methodWithAnnotation() {
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyAnnotation {
    }

    private static final class DummyClassExt extends DummyClass {

        @MyAnnotation2
        public void anotherAnnotatedMethod() {
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyAnnotation2 {
    }

}
