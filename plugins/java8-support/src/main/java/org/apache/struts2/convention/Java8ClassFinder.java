/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.util.finder.ClassFinder;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Copy of {@link com.opensymphony.xwork2.util.finder.DefaultClassFinder} with proper support for Java8
 */
public class Java8ClassFinder implements ClassFinder {

    private static final Logger LOG = LogManager.getLogger(Java8ClassFinder.class);

    private final Map<String, List<Info>> annotated = new HashMap<String, List<Info>>();
    private final Map<String, ClassInfo> classInfos = new LinkedHashMap<String, ClassInfo>();

    private final List<String> classesNotLoaded = new ArrayList<String>();

    private boolean extractBaseInterfaces;
    private ClassLoaderInterface classLoaderInterface;
    private FileManager fileManager;

    public Java8ClassFinder(ClassLoaderInterface classLoaderInterface, Collection<URL> urls, boolean extractBaseInterfaces, Set<String> protocols, Test<String> classNameFilter) {
        this.classLoaderInterface = classLoaderInterface;
        this.extractBaseInterfaces = extractBaseInterfaces;
        this.fileManager = ActionContext.getContext().getInstance(FileManagerFactory.class).getFileManager();

        List<String> classNames = new ArrayList<String>();
        for (URL location : urls) {
            try {
                if (protocols.contains(location.getProtocol())) {
                    classNames.addAll(jar(location));
                } else if ("file".equals(location.getProtocol())) {
                    try {
                        // See if it's actually a jar
                        URL jarUrl = new URL("jar", "", location.toExternalForm() + "!/");
                        JarURLConnection juc = (JarURLConnection) jarUrl.openConnection();
                        juc.getJarFile();
                        classNames.addAll(jar(jarUrl));
                    } catch (IOException e) {
                        classNames.addAll(file(location));
                    }
                }
            } catch (Exception e) {
                LOG.error("Unable to read URL [{}]", location.toExternalForm(), e);
            }
        }

        for (String className : classNames) {
            try {
                if (classNameFilter.test(className))
                    readClassDef(className);
            } catch (Throwable e) {
                LOG.error("Unable to read class [{}]", className, e);
            }
        }
    }

    public Java8ClassFinder(Class... classes){
        this(Arrays.asList(classes));
    }

    public Java8ClassFinder(List<Class> classes){
        this.classLoaderInterface = null;
        List<Info> infos = new ArrayList<Info>();
        List<Package> packages = new ArrayList<Package>();
        for (Class clazz : classes) {

            Package aPackage = clazz.getPackage();
            if (aPackage != null && !packages.contains(aPackage)){
                infos.add(new PackageInfo(aPackage));
                packages.add(aPackage);
            }

            ClassInfo classInfo = new ClassInfo(clazz, this);
            infos.add(classInfo);
            classInfos.put(classInfo.getName(), classInfo);
            for (Method method : clazz.getDeclaredMethods()) {
                infos.add(new MethodInfo(classInfo, method));
            }

            for (Constructor constructor : clazz.getConstructors()) {
                infos.add(new MethodInfo(classInfo, constructor));
            }

            for (Field field : clazz.getDeclaredFields()) {
                infos.add(new FieldInfo(classInfo, field));
            }
        }

        for (Info info : infos) {
            for (AnnotationInfo annotation : info.getAnnotations()) {
                List<Info> annotationInfos = getAnnotationInfos(annotation.getName());
                annotationInfos.add(info);
            }
        }
    }

    public ClassLoaderInterface getClassLoaderInterface() {
        return classLoaderInterface;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        List<Info> infos = annotated.get(annotation.getName());
        return infos != null && !infos.isEmpty();
    }

    public List<String> getClassesNotLoaded() {
        return Collections.unmodifiableList(classesNotLoaded);
    }

    public List<Package> findAnnotatedPackages(Class<? extends Annotation> annotation) {
        classesNotLoaded.clear();
        List<Package> packages = new ArrayList<Package>();
        List<Info> infos = getAnnotationInfos(annotation.getName());
        for (Info info : infos) {
            if (info instanceof PackageInfo) {
                PackageInfo packageInfo = (PackageInfo) info;
                try {
                    Package pkg = packageInfo.get();
                    // double check via proper reflection
                    if (pkg.isAnnotationPresent(annotation)) {
                        packages.add(pkg);
                    }
                } catch (ClassNotFoundException e) {
                    classesNotLoaded.add(packageInfo.getName());
                }
            }
        }
        return packages;
    }

    public List<Class> findAnnotatedClasses(Class<? extends Annotation> annotation) {
        classesNotLoaded.clear();
        List<Class> classes = new ArrayList<Class>();
        List<Info> infos = getAnnotationInfos(annotation.getName());
        for (Info info : infos) {
            if (info instanceof ClassInfo) {
                ClassInfo classInfo = (ClassInfo) info;
                try {
                    Class clazz = classInfo.get();
                    // double check via proper reflection
                    if (clazz.isAnnotationPresent(annotation)) {
                        classes.add(clazz);
                    }
                } catch (Throwable e) {
                    LOG.error("Error loading class [{}]", classInfo.getName(), e);
                    classesNotLoaded.add(classInfo.getName());
                }
            }
        }
        return classes;
    }

    public List<Method> findAnnotatedMethods(Class<? extends Annotation> annotation) {
        classesNotLoaded.clear();
        List<ClassInfo> seen = new ArrayList<ClassInfo>();
        List<Method> methods = new ArrayList<Method>();
        List<Info> infos = getAnnotationInfos(annotation.getName());
        for (Info info : infos) {
            if (info instanceof MethodInfo && !"<init>".equals(info.getName())) {
                MethodInfo methodInfo = (MethodInfo) info;
                ClassInfo classInfo = methodInfo.getDeclaringClass();

                if (seen.contains(classInfo)) continue;

                seen.add(classInfo);

                try {
                    Class clazz = classInfo.get();
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(annotation)) {
                            methods.add(method);
                        }
                    }
                } catch (Throwable e) {
                    LOG.error("Error loading class [{}]", classInfo.getName(), e);
                    classesNotLoaded.add(classInfo.getName());
                }
            }
        }
        return methods;
    }

    public List<Constructor> findAnnotatedConstructors(Class<? extends Annotation> annotation) {
        classesNotLoaded.clear();
        List<ClassInfo> seen = new ArrayList<ClassInfo>();
        List<Constructor> constructors = new ArrayList<Constructor>();
        List<Info> infos = getAnnotationInfos(annotation.getName());
        for (Info info : infos) {
            if (info instanceof MethodInfo && "<init>".equals(info.getName())) {
                MethodInfo methodInfo = (MethodInfo) info;
                ClassInfo classInfo = methodInfo.getDeclaringClass();

                if (seen.contains(classInfo)) continue;

                seen.add(classInfo);

                try {
                    Class clazz = classInfo.get();
                    for (Constructor constructor : clazz.getConstructors()) {
                        if (constructor.isAnnotationPresent(annotation)) {
                            constructors.add(constructor);
                        }
                    }
                } catch (Throwable e) {
                    LOG.error("Error loading class [{}]", classInfo.getName(), e);
                    classesNotLoaded.add(classInfo.getName());
                }
            }
        }
        return constructors;
    }

    public List<Field> findAnnotatedFields(Class<? extends Annotation> annotation) {
        classesNotLoaded.clear();
        List<ClassInfo> seen = new ArrayList<ClassInfo>();
        List<Field> fields = new ArrayList<Field>();
        List<Info> infos = getAnnotationInfos(annotation.getName());
        for (Info info : infos) {
            if (info instanceof FieldInfo) {
                FieldInfo fieldInfo = (FieldInfo) info;
                ClassInfo classInfo = fieldInfo.getDeclaringClass();

                if (seen.contains(classInfo)) continue;

                seen.add(classInfo);

                try {
                    Class clazz = classInfo.get();
                    for (Field field : clazz.getDeclaredFields()) {
                        if (field.isAnnotationPresent(annotation)) {
                            fields.add(field);
                        }
                    }
                } catch (Throwable e) {
                    LOG.error("Error loading class [{}]", classInfo.getName(), e);
                    classesNotLoaded.add(classInfo.getName());
                }
            }
        }
        return fields;
    }

    public List<Class> findClassesInPackage(String packageName, boolean recursive) {
        classesNotLoaded.clear();
        List<Class> classes = new ArrayList<Class>();
        for (ClassInfo classInfo : classInfos.values()) {
            try {
                if (recursive && classInfo.getPackageName().startsWith(packageName)){
                    classes.add(classInfo.get());
                } else if (classInfo.getPackageName().equals(packageName)){
                    classes.add(classInfo.get());
                }
            } catch (Throwable e) {
                LOG.error("Error loading class [{}]", classInfo.getName(), e);
                classesNotLoaded.add(classInfo.getName());
            }
        }
        return classes;
    }

    public List<Class> findClasses(Test<ClassInfo> test) {
        classesNotLoaded.clear();
        List<Class> classes = new ArrayList<Class>();
        for (ClassInfo classInfo : classInfos.values()) {
            try {
                if (test.test(classInfo)) {
                    classes.add(classInfo.get());
                }
            } catch (Throwable e) {
                LOG.error("Error loading class [{}]", classInfo.getName(), e);
                classesNotLoaded.add(classInfo.getName());
            }
        }
        return classes;
    }

    public List<Class> findClasses() {
        classesNotLoaded.clear();
        List<Class> classes = new ArrayList<Class>();
        for (ClassInfo classInfo : classInfos.values()) {
            try {
                classes.add(classInfo.get());
            } catch (Throwable e) {
                LOG.error("Error loading class [{}]", classInfo.getName(), e);
                classesNotLoaded.add(classInfo.getName());
            }
        }
        return classes;
    }

    private static List<URL> getURLs(ClassLoaderInterface classLoader, String[] dirNames) {
        List<URL> urls = new ArrayList<URL>();
        for (String dirName : dirNames) {
            try {
                Enumeration<URL> classLoaderURLs = classLoader.getResources(dirName);
                while (classLoaderURLs.hasMoreElements()) {
                    URL url = classLoaderURLs.nextElement();
                    urls.add(url);
                }
            } catch (IOException ioe) {
                LOG.error("Could not read directory [{}]", dirName, ioe);
            }
        }

        return urls;
    }

    private List<String> file(URL location) {
        List<String> classNames = new ArrayList<String>();
        File dir = new File(URLDecoder.decode(location.getPath()));
        if ("META-INF".equals(dir.getName())) {
            dir = dir.getParentFile(); // Scrape "META-INF" off
        }
        if (dir.isDirectory()) {
            scanDir(dir, classNames, "");
        }
        return classNames;
    }

    private void scanDir(File dir, List<String> classNames, String packageName) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                scanDir(file, classNames, packageName + file.getName() + ".");
            } else if (file.getName().endsWith(".class")) {
                String name = file.getName();
                name = name.replaceFirst(".class$", "");
                // Classes packaged in an exploded .war (e.g. in a VFS file system) should not
                // have WEB-INF.classes in their package name.
                classNames.add(StringUtils.removeStart(packageName, "WEB-INF.classes.") + name);
            }
        }
    }

    private List<String> jar(URL location) throws IOException {
        URL url = fileManager.normalizeToFileProtocol(location);
        if (url != null) {
            InputStream in = url.openStream();
            try {
                JarInputStream jarStream = new JarInputStream(in);
                return jar(jarStream);
            } finally {
                in.close();
            }
        } else {
            LOG.debug("Unable to read [{}]", location.toExternalForm());
        }
        return Collections.emptyList();
    }

    private List<String> jar(JarInputStream jarStream) throws IOException {
        List<String> classNames = new ArrayList<String>();

        JarEntry entry;
        while ((entry = jarStream.getNextJarEntry()) != null) {
            if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                continue;
            }
            String className = entry.getName();
            className = className.replaceFirst(".class$", "");

            //war files are treated as .jar files, so takeout WEB-INF/classes
            className = StringUtils.removeStart(className, "WEB-INF/classes/"); 

            className = className.replace('/', '.');
            classNames.add(className);
        }

        return classNames;
    }

    private List<Info> getAnnotationInfos(String name) {
        List<Info> infos = annotated.get(name);
        if (infos == null) {
            infos = new ArrayList<Info>();
            annotated.put(name, infos);
        }
        return infos;
    }

    private void readClassDef(String className) {
        if (!className.endsWith(".class")) {
            className = className.replace('.', '/') + ".class";
        }
        try {
            URL resource = classLoaderInterface.getResource(className);
            if (resource != null) {
                try (InputStream in = resource.openStream()) {
                    ClassReader classReader = new ClassReader(in);
                    classReader.accept(new InfoBuildingClassVisitor(this), ClassReader.SKIP_DEBUG);
                }
            } else {
                throw new XWorkException("Could not load " + className);
            }
        } catch (IOException e) {
            throw new XWorkException("Could not load " + className, e);
        }

    }

    public class InfoBuildingClassVisitor extends ClassVisitor {
        private Info info;
        private ClassFinder classFinder;

        public InfoBuildingClassVisitor(ClassFinder classFinder) {
            super(Opcodes.ASM5);
            this.classFinder = classFinder;
        }

        public InfoBuildingClassVisitor(Info info, ClassFinder classFinder) {
            this(classFinder);
            this.info = info;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (name.endsWith("package-info")) {
                info = new PackageInfo(javaName(name), classFinder);
            } else {
                ClassInfo classInfo = new ClassInfo(javaName(name), javaName(superName), classFinder);

                for (String interfce : interfaces) {
                    classInfo.getInterfaces().add(javaName(interfce));
                }
                info = classInfo;
                classInfos.put(classInfo.getName(), classInfo);

                if (extractBaseInterfaces)
                    extractSuperInterfaces(classInfo);
            }
        }

        private void extractSuperInterfaces(ClassInfo classInfo) {
            String superType = classInfo.getSuperType();

            if (superType != null) {
                ClassInfo base = classInfos.get(superType);

                if (base == null) {
                    //try to load base
                    String resource = superType.replace('.', '/') + ".class";
                    readClassDef(resource);
                    base = classInfos.get(superType);
                }

                if (base != null) {
                    List<String> interfaces = classInfo.getSuperInterfaces();
                    interfaces.addAll(base.getSuperInterfaces());
                    interfaces.addAll(base.getInterfaces());
                }
            }
        }

        private String javaName(String name) {
            return (name == null)? null:name.replace('/', '.');
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            AnnotationInfo annotationInfo = new AnnotationInfo(desc);
            info.getAnnotations().add(annotationInfo);
            getAnnotationInfos(annotationInfo.getName()).add(info);
            return null;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            ClassInfo classInfo = ((ClassInfo) info);
            FieldInfo fieldInfo = new FieldInfo(classInfo, name, desc);
            classInfo.getFields().add(fieldInfo);
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            ClassInfo classInfo = ((ClassInfo) info);
            MethodInfo methodInfo = new MethodInfo(classInfo, name, desc);
            classInfo.getMethods().add(methodInfo);
            return new InfoBuildingMethodVisitor(methodInfo);
        }
    }

    public class InfoBuildingMethodVisitor extends MethodVisitor {
        private Info info;

        public InfoBuildingMethodVisitor() {
            super(Opcodes.ASM5);
        }

        public InfoBuildingMethodVisitor(Info info) {
            this();
            this.info = info;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            AnnotationInfo annotationInfo = new AnnotationInfo(desc);
            info.getAnnotations().add(annotationInfo);
            getAnnotationInfos(annotationInfo.getName()).add(info);
            return null;
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int param, String desc, boolean visible) {
            MethodInfo methodInfo = ((MethodInfo) info);
            List<AnnotationInfo> annotationInfos = methodInfo.getParameterAnnotations(param);
            AnnotationInfo annotationInfo = new AnnotationInfo(desc);
            annotationInfos.add(annotationInfo);
            return null;
        }
    }

    private static final class DefaultClassnameFilterImpl implements Test<String> {
        public boolean test(String className) {
            return true;
        }
    }
}

