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
package org.apache.struts2;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;
import com.opensymphony.xwork2.util.finder.UrlSet;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.compiler.MemoryClassLoader;
import org.apache.struts2.compiler.MemoryJavaFileObject;
import org.apache.struts2.jasper.JasperException;
import org.apache.struts2.jasper.JspC;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspPage;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses jasper to extract a JSP from the classpath to a file and compile it. The classpath used for
 * compilation is built by finding all the jar files using the current class loader (Thread), plus
 * directories.
 */
public class JSPLoader {
    private static final Logger LOG = LogManager.getLogger(JSPLoader.class);

    private static MemoryClassLoader classLoader = new MemoryClassLoader();
    private static final String DEFAULT_PACKAGE = "org.apache.struts2.jsp";

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package (.*?);");
    private static final Pattern CLASS_PATTERN = Pattern.compile("public final class (.*?) ");

    public Servlet load(String location) throws Exception {
        location = StringUtils.substringBeforeLast(location, "?");

        LOG.debug("Compiling JSP [{}]", location);

        //use Jasper to compile the JSP into java code
        JspC jspC = compileJSP(location);
        String source = jspC.getSourceCode();

        //System.out.print(source);

        String className = extractClassName(source);

        //use Java Compiler API to compile the java code into a class
        //the tlds that were discovered are added (their jars) to the classpath
        compileJava(className, source, jspC.getTldAbsolutePaths());

        //load the class that was just built
        Class clazz = Class.forName(className, false, classLoader);
        return createServlet(clazz);
    }

    private String extractClassName(String source) {
        Matcher matcher = PACKAGE_PATTERN.matcher(source);
        matcher.find();
        String packageName = matcher.group(1);

        matcher = CLASS_PATTERN.matcher(source);
        matcher.find();
        String className = matcher.group(1);

        return packageName + "." + className;
    }

    /**
     * Creates and inits a servlet
     */
    private Servlet createServlet(Class clazz) throws IllegalAccessException, InstantiationException, ServletException {
        JSPServletConfig config = new JSPServletConfig(ServletActionContext.getServletContext());

        Servlet servlet = (Servlet) clazz.newInstance();
        servlet.init(config);

        /*
         there is no need to call JspPage.init explicitly because Jasper's
         JSP base classe HttpJspBase.init(ServletConfig) calls:
         jspInit();
         _jspInit();
         */

        return servlet;
    }

    /**
     * Compiles the given source code into java bytecode
     */
    private void compileJava(String className, final String source, Set<String> extraClassPath) throws IOException {
        LOG.trace("Compiling [{}], source: [{}]", className, source);

        JavaCompiler compiler =ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        //the generated bytecode is fed to the class loader
        JavaFileManager jfm = new
                ForwardingJavaFileManager<StandardJavaFileManager>(
                        compiler.getStandardFileManager(diagnostics, null, null)) {

                    @Override
                    public JavaFileObject getJavaFileForOutput(Location location,
                                                               String name,
                                                               JavaFileObject.Kind kind,
                                                               FileObject sibling) throws IOException {
                        MemoryJavaFileObject fileObject = new MemoryJavaFileObject(name, kind);
                        classLoader.addMemoryJavaFileObject(name, fileObject);
                        return fileObject;
                    }
                };

        //read java source code from memory
        String fileName = className.replace('.', '/') + ".java";
        SimpleJavaFileObject sourceCodeObject = new SimpleJavaFileObject(toURI(fileName), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean
                    ignoreEncodingErrors)
                    throws IOException, IllegalStateException,
                    UnsupportedOperationException {
                return source;
            }

        };

        //build classpath
        //some entries will be added multiple times, hence the set
        List<String> optionList = new ArrayList<String>();
        Set<String> classPath = new HashSet<String>();

        FileManager fileManager = ServletActionContext.getContext().getInstance(FileManagerFactory.class).getFileManager();

        //find available jars
        ClassLoaderInterface classLoaderInterface = getClassLoaderInterface();
        UrlSet urlSet = new UrlSet(classLoaderInterface);

        //find jars
        List<URL> urls = urlSet.getUrls();

        for (URL url : urls) {
            URL normalizedUrl = fileManager.normalizeToFileProtocol(url);
            File file = FileUtils.toFile(ObjectUtils.defaultIfNull(normalizedUrl, url));
            if (file.exists())
                classPath.add(file.getAbsolutePath());
        }

        //these should be in the list already, but I am feeling paranoid
        //this jar
        classPath.add(getJarUrl(EmbeddedJSPResult.class));
        //servlet api
        classPath.add(getJarUrl(Servlet.class));
        //jsp api
        classPath.add(getJarUrl(JspPage.class));

        try {
            Class annotationsProcessor = Class.forName("org.apache.AnnotationProcessor");
            classPath.add(getJarUrl(annotationsProcessor));
        } catch (ClassNotFoundException e) {
            //ok ignore
        }

        //add extra classpath entries (jars where tlds were found will be here)
        for (Iterator<String> iterator = extraClassPath.iterator(); iterator.hasNext();) {
            String entry = iterator.next();
            classPath.add(entry);
        }

        String classPathString = StringUtils.join(classPath, File.pathSeparator);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Compiling [#0] with classpath [#1]", className, classPathString);
        }

        optionList.addAll(Arrays.asList("-classpath", classPathString));

        //compile
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, jfm, diagnostics, optionList, null,
                Arrays.asList(sourceCodeObject));

        if (!task.call()) {
            throw new StrutsException("Compilation failed:" + diagnostics.getDiagnostics().get(0).toString());
        }
    }

    protected String getJarUrl(Class clazz) {
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL loc = codeSource.getLocation();
        File file = FileUtils.toFile(loc);
        return file.getAbsolutePath();
    }

    private JspC compileJSP(String location) throws JasperException {
        JspC jspC = new JspC();
        jspC.setClassLoaderInterface(getClassLoaderInterface());
        jspC.setCompile(false);
        jspC.setJspFiles(location);
        jspC.setPackage(DEFAULT_PACKAGE);
        jspC.execute();
        return jspC;
    }

    private ClassLoaderInterface getClassLoaderInterface() {
        ClassLoaderInterface classLoaderInterface = null;
        ServletContext ctx = ServletActionContext.getServletContext();
        if (ctx != null)
            classLoaderInterface = (ClassLoaderInterface) ctx.getAttribute(ClassLoaderInterface.CLASS_LOADER_INTERFACE);

        return (ClassLoaderInterface) ObjectUtils.defaultIfNull(classLoaderInterface, new ClassLoaderInterfaceDelegate(JSPLoader.class.getClassLoader()));
    }

    private static URI toURI(String name) {
        try {
            return new URI(name);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
