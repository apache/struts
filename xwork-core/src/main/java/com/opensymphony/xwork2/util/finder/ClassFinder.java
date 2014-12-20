package com.opensymphony.xwork2.util.finder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
 */
public interface ClassFinder {

    boolean isAnnotationPresent(Class<? extends Annotation> annotation);

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
    List<String> getClassesNotLoaded();

    List<Package> findAnnotatedPackages(Class<? extends Annotation> annotation);

    List<Class> findAnnotatedClasses(Class<? extends Annotation> annotation);

    List<Method> findAnnotatedMethods(Class<? extends Annotation> annotation);

    List<Constructor> findAnnotatedConstructors(Class<? extends Annotation> annotation);

    List<Field> findAnnotatedFields(Class<? extends Annotation> annotation);

    List<Class> findClassesInPackage(String packageName, boolean recursive);

    List<Class> findClasses(Test<ClassInfo> test);

    List<Class> findClasses();

    ClassLoaderInterface getClassLoaderInterface();

    public static interface Info {
        String getName();

        List<AnnotationInfo> getAnnotations();
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

        public PackageInfo(String name, ClassFinder classFinder) {
            info = new ClassInfo(name, null, classFinder);
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
        private ClassFinder classFinder;
        private ClassNotFoundException notFound;

        public ClassInfo(Class clazz, ClassFinder classFinder) {
            super(clazz);
            this.clazz = clazz;
            this.classFinder = classFinder;
            this.name = clazz.getName();
            Class superclass = clazz.getSuperclass();
            this.superType = superclass != null ? superclass.getName(): null;
        }

        public ClassInfo(String name, String superType, ClassFinder classFinder) {
            this.name = name;
            this.superType = superType;
            this.classFinder = classFinder;
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
                this.clazz = classFinder.getClassLoaderInterface().loadClass(name);
                return clazz;
            } catch (ClassNotFoundException notFound) {
                classFinder.getClassesNotLoaded().add(name);
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

}
