/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>AnnotationUtils</code>
 *
 * Various utility methods dealing with annotations
 *
 * @author Rainer Hermanns
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Dan Oxlade, dan d0t oxlade at gmail d0t c0m
 * @version $Id$
 */
public class AnnotationUtils {

    private static final Pattern SETTER_PATTERN = Pattern.compile("set([A-Z][A-Za-z0-9]*)$");
    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is|has)([A-Z][A-Za-z0-9]*)$");



    /**
     * Adds all fields with the specified Annotation of class clazz and its superclasses to allFields
     *
     * @param annotationClass
     * @param clazz
     * @param allFields
     */
    public static void addAllFields(Class annotationClass, Class clazz, List<Field> allFields) {

        if (clazz == null) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Annotation ann = field.getAnnotation(annotationClass);
            if (ann!=null) {
                allFields.add(field);
            }
        }
        addAllFields(annotationClass, clazz.getSuperclass(), allFields);
    }

    /**
     * Adds all methods with the specified Annotation of class clazz and its superclasses to allFields
     *
     * @param annotationClass
     * @param clazz
     * @param allMethods
     */
    public static void addAllMethods(Class annotationClass, Class clazz, List<Method> allMethods) {

        if (clazz == null) {
            return;
        }

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Annotation ann = method.getAnnotation(annotationClass);
            if (ann!=null) {
                allMethods.add(method);
            }
        }
        addAllMethods(annotationClass, clazz.getSuperclass(), allMethods);
    }

    /**
     *
     * @param clazz
     * @param allInterfaces
     */
    public static void addAllInterfaces(Class clazz, List<Class> allInterfaces) {
        if (clazz == null) {
            return;
        }

        Class[] interfaces = clazz.getInterfaces();
        allInterfaces.addAll(Arrays.asList(interfaces));
        addAllInterfaces(clazz.getSuperclass(), allInterfaces);
    }

	/**
	 * For the given <code>Class</code> get a collection of the the {@link AnnotatedElement}s 
	 * that match the given <code>annotation</code>s or if no <code>annotation</code>s are 
	 * specified then return all of the annotated elements of the given <code>Class</code>. 
	 * Includes only the method level annotations.
	 * 
	 * @param clazz The {@link Class} to inspect
	 * @param annotation the {@link Annotation}s to find
	 * @return A {@link Collection}&lt;{@link AnnotatedElement}&gt; containing all of the
	 *  method {@link AnnotatedElement}s matching the specified {@link Annotation}s
	 */
	public static final Collection<Method> getAnnotatedMethods(Class clazz, Class<? extends Annotation>... annotation){
		Collection<Method> toReturn = new HashSet<Method>();
		
		for(Method m : clazz.getMethods()){
			if( ArrayUtils.isNotEmpty(annotation) && isAnnotatedBy(m,annotation) ){
				toReturn.add(m);
			}else if( ArrayUtils.isEmpty(annotation) && ArrayUtils.isNotEmpty(m.getAnnotations())){
				toReturn.add(m);
			}
		}
		
		return toReturn;
	}

	/**
	 * Varargs version of <code>AnnotatedElement.isAnnotationPresent()</code>
	 * @see AnnotatedElement
	 */
	public static final boolean isAnnotatedBy(AnnotatedElement annotatedElement, Class<? extends Annotation>... annotation) {
		if(ArrayUtils.isEmpty(annotation)) return false;
		
		for( Class<? extends Annotation> c : annotation ){
			if( annotatedElement.isAnnotationPresent(c) ) return true;
		}
		
		return false;
	}    
    
	/**
	 * 
	 * @deprecated since 2.0.4 use getAnnotatedMethods
	 */
    @Deprecated
    public static List<Method> findAnnotatedMethods(Class clazz, Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<Method>();
        findRecursively(clazz, annotationClass, methods);
        return methods;
    }

    /**
     * 
     * @deprecated since 2.0.4 use getAnnotatedMethods
     */
    @Deprecated
    public static void findRecursively(Class clazz, Class<? extends Annotation> annotationClass, List<Method> methods) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getAnnotation(annotationClass) != null) { methods.add(0, m); }
        }
        if (clazz.getSuperclass() != Object.class) {
            findRecursively(clazz.getSuperclass(), annotationClass, methods);
        }
    }

    /**
     * Returns the property name for a method.
     * This method is independant from property fields.
     *
     * @param method The method to get the property name for.
     * @return the property name for given method; null if non could be resolved.
     */
    public static String resolvePropertyName(Method method) {

        Matcher matcher = SETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 1) {
            String raw = matcher.group(1);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        matcher = GETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 0) {
            String raw = matcher.group(2);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        return null;
    }


    /**
     * Retrieves all classes within a packages.
     * TODO: this currently does not work with jars.
     *
     * @param pckgname
     * @return Array of full qualified class names from this package.
     */
    public static String[] find(Class clazz, final String pckgname) {

        List<String> classes = new ArrayList<String>();
        String name = new String(pckgname);
        if (!name.startsWith("/")) {
            name = "/" + name;
        }

        name = name.replace('.', File.separatorChar);

        final URL url = clazz.getResource(name);
        final File directory = new File(url.getFile());

        if (directory.exists()) {
            final String[] files = directory.list();
            for (String file : files) {
                if (file.endsWith(".class")) {
                    classes.add(pckgname + "." + file.substring(0, file.length() - 6));
                }
            }
        }
        return classes.toArray(new String[classes.size()]);
    }
}
