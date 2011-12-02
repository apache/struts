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
package com.opensymphony.xwork2.util.finder;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.URLUtil;
import com.opensymphony.xwork2.XWorkException;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * ClassFinder searches the classpath of the specified ClassLoaderInterface for
 * packages, classes, constructors, methods, or fields with specific annotations.
 *
 * For security reasons ASM is used to find the annotations.  Classes are not
 * loaded unless they match the requirements of a called findAnnotated* method.
 * Once loaded, these classes are cached.
 *
 * The getClassesNotLoaded() method can be used immediately after any find*
 * method to get a list of classes which matched the find requirements (i.e.
 * contained the annotation), but were unable to be loaded.
 *
 * @author David Blevins
 * @version $Rev$ $Date$
 */
public class ClassFinder {
    private static final Logger LOG = LoggerFactory.getLogger(ClassFinder.class);

    private final Map<String, List<Info>> annotated = new HashMap<String, List<Info>>();
    private final Map<String, ClassInfo> classInfos = new LinkedHashMap<String, ClassInfo>();

    private final List<String> classesNotLoaded = new ArrayList<String>();

    private boolean extractBaseInterfaces;
    private ClassLoaderInterface classLoaderInterface;

    /**
     * Creates a ClassFinder that will search the urls in the specified ClassLoaderInterface
     * excluding the urls in the ClassLoaderInterface's parent.
     *
     * To include the parent ClassLoaderInterface, use:
     *
     *    new ClassFinder(ClassLoaderInterface, false);
     *
     * To exclude the parent's parent, use:
     *
     *    new ClassFinder(ClassLoaderInterface, ClassLoaderInterface.getParent().getParent());
     *
     * @param classLoader source of classes to scan
     * @throws Exception if something goes wrong
     */
    public ClassFinder(ClassLoaderInterface classLoader) throws Exception {
        this(classLoader, true);
    }

    /**
     * Creates a ClassFinder that will search the urls in the specified ClassLoaderInterface.
     *
     * @param classLoader source of classes to scan
     * @param excludeParent Allegedly excludes classes from parent ClassLoaderInterface, whatever that might mean
     * @throws Exception if something goes wrong.
     */
    public ClassFinder(ClassLoaderInterface classLoader, boolean excludeParent) throws Exception {
        this(classLoader, getUrls(classLoader, excludeParent));
    }

    /**
     * Creates a ClassFinder that will search the urls in the specified classloader excluding
     * the urls in the 'exclude' ClassLoaderInterface.
     *
     * @param classLoader source of classes to scan
     * @param exclude source of classes to exclude from scanning
     * @throws Exception if something goes wrong
     */
    public ClassFinder(ClassLoaderInterface classLoader, ClassLoaderInterface exclude) throws Exception {
        this(classLoader, getUrls(classLoader, exclude));
    }

    public ClassFinder(ClassLoaderInterface classLoader, URL url) {
        this(classLoader, Arrays.asList(url));
    }

    public ClassFinder(ClassLoaderInterface classLoader, String... dirNames) {
        this(classLoader, getURLs(classLoader, dirNames));
    }

    public ClassFinder(ClassLoaderInterface classLoaderInterface, Collection<URL> urls) {
        this(classLoaderInterface, urls, false);
    }

    public ClassFinder(ClassLoaderInterface classLoaderInterface, Collection<URL> urls, boolean extractBaseInterfaces) {
        this(classLoaderInterface, urls, extractBaseInterfaces, new HashSet(){
            {
                add("jar");
            }
        });
    }

    public ClassFinder(ClassLoaderInterface classLoaderInterface, Collection<URL> urls, boolean extractBaseInterfaces, Set<String> protocols) {
	    this(classLoaderInterface,urls,extractBaseInterfaces,protocols,new DefaultClassnameFilterImpl());
    }

    public ClassFinder(ClassLoaderInterface classLoaderInterface, Collection<URL> urls, boolean extractBaseInterfaces, Set<String> protocols, Test<String> classNameFilter) {
        this.classLoaderInterface = classLoaderInterface;
        this.extractBaseInterfaces = extractBaseInterfaces;

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
                if (LOG.isErrorEnabled())
                    LOG.error("Unable to read URL [#0]", e, location.toExternalForm());
            }
        }

        for (String className : classNames) {
            try {
                if (classNameFilter.test(className))
                    readClassDef(className);
            } catch (Throwable e) {
                if (LOG.isErrorEnabled())
                    LOG.error("Unable to read class [#0]", e, className);
            }
        }
    }

    public ClassFinder(Class... classes){
        this(Arrays.asList(classes));
    }

    public ClassFinder(List<Class> classes){
        this.classLoaderInterface = null;
        List<Info> infos = new ArrayList<Info>();
        List<Package> packages = new ArrayList<Package>();
        for (Class clazz : classes) {

            Package aPackage = clazz.getPackage();
            if (aPackage != null && !packages.contains(aPackage)){
                infos.add(new PackageInfo(aPackage));
                packages.add(aPackage);
            }

            ClassInfo classInfo = new ClassInfo(clazz);
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

    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        List<Info> infos = annotated.get(annotation.getName());
        return infos != null && !infos.isEmpty();
    }

    /**
     * Returns a list of classes that could not be loaded in last invoked findAnnotated* method.
     * <p/>
     * The list will only contain entries of classes whose byte code matched the requirements
     * of last invoked find* method, but were unable to be loaded and included in the results.
     * <p/>
     * The list returned is unmodifiable.  Once obtained, the returned list will be a live view of the
     * results from the last findAnnotated* method call.
     * <p/>
     * This method is not thread safe.
     * @return an unmodifiable live view of classes that could not be loaded in previous findAnnotated* call.
     */
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
                    if (LOG.isErrorEnabled())
                        LOG.error("Error loading class [#0]", e, classInfo.getName());
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
                    if (LOG.isErrorEnabled())
                        LOG.error("Error loading class [#0]", e, classInfo.getName());
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
                    if (LOG.isErrorEnabled())
                        LOG.error("Error loading class [#0]", e, classInfo.getName());
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
                    if (LOG.isErrorEnabled())
                        LOG.error("Error loading class [#0]", e, classInfo.getName());
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
                if (LOG.isErrorEnabled())
                    LOG.error("Error loading class [#0]", e, classInfo.getName());
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
                if (LOG.isErrorEnabled())
                    LOG.error("Error loading class [#0]", e, classInfo.getName());
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
                if (LOG.isErrorEnabled())
                    LOG.error("Error loading class [#0]", e, classInfo.getName());
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
                if (LOG.isErrorEnabled())
                    LOG.error("Could not read driectory [#0]", ioe, dirName);
            }
        }

        return urls;
    }

    private static Collection<URL> getUrls(ClassLoaderInterface classLoaderInterface, boolean excludeParent) throws IOException {
        return getUrls(classLoaderInterface, excludeParent? classLoaderInterface.getParent() : null);
    }

    private static Collection<URL> getUrls(ClassLoaderInterface classLoader, ClassLoaderInterface excludeParent) throws IOException {
        UrlSet urlSet = new UrlSet(classLoader);
        if (excludeParent != null){
            urlSet = urlSet.exclude(excludeParent);
        }
        return urlSet.getUrls();
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
                classNames.add(packageName + name);
            }
        }
    }

    private List<String> jar(URL location) throws IOException {
        URL url = URLUtil.normalizeToFileProtocol(location);
        if (url != null) {
            InputStream in = url.openStream();
            try {
                JarInputStream jarStream = new JarInputStream(in);
                return jar(jarStream);
            } finally {
                in.close();
            }
        } else if (LOG.isDebugEnabled())
            LOG.debug("Unable to read [#0]", location.toExternalForm());
        
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

    public class Annotatable {
        private final List<AnnotationInfo> annotations = new ArrayList<AnnotationInfo>();

        public Annotatable(AnnotatedElement element) {
            for (Annotation annotation : element.getAnnotations()) {
                annotations.add(new AnnotationInfo(annotation.annotationType().getName()));
            }
        }

        public Annotatable() {
        }

        public List<AnnotationInfo> getAnnotations() {
            return annotations;
        }

    }

    public static interface Info {
        String getName();

        List<AnnotationInfo> getAnnotations();
    }

    public class PackageInfo extends Annotatable implements Info {
        private final String name;
        private final ClassInfo info;
        private final Package pkg;

        public PackageInfo(Package pkg){
            super(pkg);
            this.pkg = pkg;
            this.name = pkg.getName();
            this.info = null;
        }

        public PackageInfo(String name) {
            info = new ClassInfo(name, null);
            this.name = name;
            this.pkg = null;
        }

        public String getName() {
            return name;
        }

        public Package get() throws ClassNotFoundException {
            return (pkg != null)?pkg:info.get().getPackage();
        }
    }

    public class ClassInfo extends Annotatable implements Info {
        private final String name;
        private final List<MethodInfo> methods = new ArrayList<MethodInfo>();
        private final List<MethodInfo> constructors = new ArrayList<MethodInfo>();
        private final String superType;
        private final List<String> interfaces = new ArrayList<String>();
        private final List<String> superInterfaces = new ArrayList<String>();
        private final List<FieldInfo> fields = new ArrayList<FieldInfo>();
        private Class<?> clazz;
        private ClassNotFoundException notFound;

        public ClassInfo(Class clazz) {
            super(clazz);
            this.clazz = clazz;
            this.name = clazz.getName();
            Class superclass = clazz.getSuperclass();
            this.superType = superclass != null ? superclass.getName(): null;
        }

        public ClassInfo(String name, String superType) {
            this.name = name;
            this.superType = superType;
        }

        public String getPackageName(){
            return name.indexOf(".") > 0 ? name.substring(0, name.lastIndexOf(".")) : "" ;
        }

        public List<MethodInfo> getConstructors() {
            return constructors;
        }

        public List<String> getInterfaces() {
            return interfaces;
        }

        public List<String> getSuperInterfaces() {
            return superInterfaces;
        }

        public List<FieldInfo> getFields() {
            return fields;
        }

        public List<MethodInfo> getMethods() {
            return methods;
        }

        public String getName() {
            return name;
        }

        public String getSuperType() {
            return superType;
        }

        public Class get() throws ClassNotFoundException {
            if (clazz != null) return clazz;
            if (notFound != null) throw notFound;
            try {
                this.clazz = classLoaderInterface.loadClass(name);
                return clazz;
            } catch (ClassNotFoundException notFound) {
                classesNotLoaded.add(name);
                this.notFound = notFound;
                throw notFound;
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public class MethodInfo extends Annotatable implements Info {
        private final ClassInfo declaringClass;
        private final String returnType;
        private final String name;
        private final List<List<AnnotationInfo>> parameterAnnotations = new ArrayList<List<AnnotationInfo>>();

        public MethodInfo(ClassInfo info, Constructor constructor){
            super(constructor);
            this.declaringClass = info;
            this.name = "<init>";
            this.returnType = Void.TYPE.getName();
        }

        public MethodInfo(ClassInfo info, Method method){
            super(method);
            this.declaringClass = info;
            this.name = method.getName();
            this.returnType = method.getReturnType().getName();
        }

        public MethodInfo(ClassInfo declarignClass, String name, String returnType) {
            this.declaringClass = declarignClass;
            this.name = name;
            this.returnType = returnType;
        }

        public List<List<AnnotationInfo>> getParameterAnnotations() {
            return parameterAnnotations;
        }

        public List<AnnotationInfo> getParameterAnnotations(int index) {
            if (index >= parameterAnnotations.size()) {
                for (int i = parameterAnnotations.size(); i <= index; i++) {
                    List<AnnotationInfo> annotationInfos = new ArrayList<AnnotationInfo>();
                    parameterAnnotations.add(i, annotationInfos);
                }
            }
            return parameterAnnotations.get(index);
        }

        public String getName() {
            return name;
        }

        public ClassInfo getDeclaringClass() {
            return declaringClass;
        }

        public String getReturnType() {
            return returnType;
        }

        @Override
        public String toString() {
            return declaringClass + "@" + name;
        }
    }

    public class FieldInfo extends Annotatable implements Info {
        private final String name;
        private final String type;
        private final ClassInfo declaringClass;

        public FieldInfo(ClassInfo info, Field field){
            super(field);
            this.declaringClass = info;
            this.name = field.getName();
            this.type = field.getType().getName();
        }

        public FieldInfo(ClassInfo declaringClass, String name, String type) {
            this.declaringClass = declaringClass;
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public ClassInfo getDeclaringClass() {
            return declaringClass;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return declaringClass + "#" + name;
        }
    }

    public class AnnotationInfo extends Annotatable implements Info {
        private final String name;

        public AnnotationInfo(Annotation annotation){
            this(annotation.getClass().getName());
        }

        public AnnotationInfo(Class<? extends Annotation> annotation) {
            this.name = annotation.getName().intern();
        }

        public AnnotationInfo(String name) {
            name = name.replaceAll("^L|;$", "");
            name = name.replace('/', '.');
            this.name = name.intern();
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
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
                InputStream in = resource.openStream();
                try {
                    ClassReader classReader = new ClassReader(in);
                    classReader.accept(new InfoBuildingVisitor(), ClassReader.SKIP_DEBUG);
                } finally {
                    in.close();
                }
            } else {
                throw new XWorkException("Could not load " + className);
            }
        } catch (IOException e) {
            throw new XWorkException("Could not load " + className, e);
        }

    }

    public class InfoBuildingVisitor extends EmptyVisitor {
        private Info info;

        public InfoBuildingVisitor() {
        }

        public InfoBuildingVisitor(Info info) {
            this.info = info;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (name.endsWith("package-info")) {
                info = new PackageInfo(javaName(name));
            } else {
                ClassInfo classInfo = new ClassInfo(javaName(name), javaName(superName));

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
            return new InfoBuildingVisitor(annotationInfo);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            ClassInfo classInfo = ((ClassInfo) info);
            FieldInfo fieldInfo = new FieldInfo(classInfo, name, desc);
            classInfo.getFields().add(fieldInfo);
            return new InfoBuildingVisitor(fieldInfo);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            ClassInfo classInfo = ((ClassInfo) info);
            MethodInfo methodInfo = new MethodInfo(classInfo, name, desc);
            classInfo.getMethods().add(methodInfo);
            return new InfoBuildingVisitor(methodInfo);
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int param, String desc, boolean visible) {
            MethodInfo methodInfo = ((MethodInfo) info);
            List<AnnotationInfo> annotationInfos = methodInfo.getParameterAnnotations(param);
            AnnotationInfo annotationInfo = new AnnotationInfo(desc);
            annotationInfos.add(annotationInfo);
            return new InfoBuildingVisitor(annotationInfo);
        }
    }

    private static final class DefaultClassnameFilterImpl implements Test<String> {
        public boolean test(String className) {
            return true;
        }
    }
}

