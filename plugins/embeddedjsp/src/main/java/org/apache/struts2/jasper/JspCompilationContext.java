/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.struts2.jasper;

import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.jasper.compiler.Compiler;
import org.apache.struts2.jasper.compiler.*;
import org.apache.struts2.jasper.servlet.JasperLoader;
import org.apache.struts2.jasper.servlet.JspServletWrapper;
import org.apache.commons.lang.xwork.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.jsp.tagext.TagInfo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Set;

/**
 * A place holder for various things that are used through out the JSP
 * engine. This is a per-request/per-context data structure. Some of
 * the instance variables are set at different points.
 * <p/>
 * Most of the path-related stuff is here - mangling names, versions, dirs,
 * loading resources and dealing with uris.
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Pierre Delisle
 * @author Costin Manolache
 * @author Kin-man Chung
 */
public class JspCompilationContext {

    private Logger log = LoggerFactory.getLogger(JspCompilationContext.class);

    private Hashtable tagFileJarUrls;
    private boolean isPackagedTagFile;

    private String className;
    private String jspUri;
    private boolean isErrPage;
    private String basePackageName;
    private String derivedPackageName;
    private String servletJavaFileName;
    private String javaPath;
    private String classFileName;
    private String contentType;
    private ServletWriter writer;
    private Options options;
    private JspServletWrapper jsw;
    private Compiler jspCompiler;
    private String classPath;


    private ServletContext context;
    private URLClassLoader loader;

    private JspRuntimeContext rctxt;

    private int removed = 0;

    private URLClassLoader jspLoader;
    private URL baseUrl;
    private Class servletClass;

    private boolean isTagFile;
    private boolean protoTypeMode;
    private TagInfo tagInfo;
    private URL tagFileJarUrl;

    private String sourceCode;
    private ClassLoaderInterface classLoaderInterface;

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public JspCompilationContext(String jspUri,
                                 boolean isErrPage,
                                 Options options,
                                 ServletContext context,
                                 JspServletWrapper jsw,
                                 JspRuntimeContext rctxt,
                                 ClassLoaderInterface classLoaderInterface) {

        this.jspUri = canonicalURI(jspUri);
        this.isErrPage = isErrPage;
        this.options = options;
        this.jsw = jsw;
        this.context = context;

        this.rctxt = rctxt;
        this.tagFileJarUrls = new Hashtable();
        this.basePackageName = Constants.JSP_PACKAGE_NAME;
        this.classLoaderInterface = classLoaderInterface;
    }

    public JspCompilationContext(String tagfile,
                                 TagInfo tagInfo,
                                 Options options,
                                 ServletContext context,
                                 JspServletWrapper jsw,
                                 JspRuntimeContext rctxt,
                                 URL tagFileJarUrl) {
        this(tagfile, false, options, context, jsw, rctxt, null);
        this.isTagFile = true;
        this.tagInfo = tagInfo;
        this.tagFileJarUrl = tagFileJarUrl;
        if (tagFileJarUrl != null) {
            isPackagedTagFile = true;
        }
    }

    /* ==================== Methods to override ==================== */

    /** ---------- Class path and loader ---------- */

    /**
     * The classpath that is passed off to the Java compiler.
     */
    public String getClassPath() {
        if (classPath != null)
            return classPath;
        return rctxt.getClassPath();
    }

    /**
     * The classpath that is passed off to the Java compiler.
     */
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    /**
     * What class loader to use for loading classes while compiling
     * this JSP?
     */
    public ClassLoader getClassLoader() {
        if (loader != null)
            return loader;
        return rctxt.getParentClassLoader();
    }

    public void setClassLoader(URLClassLoader loader) {
        this.loader = loader;
    }

    public ClassLoader getJspLoader() {
        if (jspLoader == null) {
            jspLoader = new JasperLoader
                    (new URL[]{baseUrl},
                            getClassLoader(),
                            rctxt.getPermissionCollection(),
                            rctxt.getCodeSource());
        }
        return jspLoader;
    }

    /** ---------- Input/Output  ---------- */

    /**
     * Create a "Compiler" object based on some init param data. This
     * is not done yet. Right now we're just hardcoding the actual
     * compilers that are created.
     */
    public Compiler createCompiler() throws JasperException {
        jspCompiler = new CustomCompiler();
        jspCompiler.init(this, jsw);
        return jspCompiler;
    }

    private Compiler createCompiler(String className) {
        Compiler compiler = null;
        try {
            compiler = (Compiler) Class.forName(className).newInstance();
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug(Localizer.getMessage("jsp.error.compiler"), t);
            }
        }
        return compiler;
    }

    public Compiler getCompiler() {
        return jspCompiler;
    }

    /** ---------- Access resources in the webapp ---------- */

    /**
     * Gets a resource as a stream, relative to the meanings of this
     * context's implementation.
     *
     * @return a null if the resource cannot be found or represented
     *         as an InputStream.
     */
    public java.io.InputStream getResourceAsStream(String res) {
        try {
            return classLoaderInterface.getResourceAsStream(canonicalURI(StringUtils.removeStart(res, "/")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public URL getResource(String res) throws MalformedURLException {
        return classLoaderInterface.getResource(canonicalURI(StringUtils.removeStart(res, "/")));
    }

    public Set getResourcePaths(String path) {
        return context.getResourcePaths(canonicalURI(path));
    }

    /**
     * Gets the actual path of a URI relative to the context of
     * the compilation.
     */
    public String getRealPath(String path) {
        if (context != null) {
            return context.getRealPath(path);
        }
        return path;
    }

    /**
     * Returns the tag-file-name-to-JAR-file map of this compilation unit,
     * which maps tag file names to the JAR files in which the tag files are
     * packaged.
     * <p/>
     * The map is populated when parsing the tag-file elements of the TLDs
     * of any imported taglibs.
     */
    public Hashtable getTagFileJarUrls() {
        return this.tagFileJarUrls;
    }

    /**
     * Returns the JAR file in which the tag file for which this
     * JspCompilationContext was created is packaged, or null if this
     * JspCompilationContext does not correspond to a tag file, or if the
     * corresponding tag file is not packaged in a JAR.
     */
    public URL getTagFileJarUrl() {
        return this.tagFileJarUrl;
    }

    /* ==================== Common implementation ==================== */

    /**
     * Just the class name (does not include package name) of the
     * generated class.
     */
    public String getServletClassName() {

        if (className != null) {
            return className;
        }

        if (isTagFile) {
            className = tagInfo.getTagClassName();
            int lastIndex = className.lastIndexOf('.');
            if (lastIndex != -1) {
                className = className.substring(lastIndex + 1);
            }
        } else {
            int iSep = jspUri.lastIndexOf('/') + 1;
            className = JspUtil.makeJavaIdentifier(jspUri.substring(iSep));
        }
        return className;
    }

    public void setServletClassName(String className) {
        this.className = className;
    }

    /**
     * Path of the JSP URI. Note that this is not a file name. This is
     * the context rooted URI of the JSP file.
     */
    public String getJspFile() {
        return jspUri;
    }

    /**
     * Are we processing something that has been declared as an
     * errorpage?
     */
    public boolean isErrorPage() {
        return isErrPage;
    }

    public void setErrorPage(boolean isErrPage) {
        this.isErrPage = isErrPage;
    }

    public boolean isTagFile() {
        return isTagFile;
    }

    public TagInfo getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(TagInfo tagi) {
        tagInfo = tagi;
    }

    /**
     * True if we are compiling a tag file in prototype mode.
     * ie we only generate codes with class for the tag handler with empty
     * method bodies.
     */
    public boolean isPrototypeMode() {
        return protoTypeMode;
    }

    public void setPrototypeMode(boolean pm) {
        protoTypeMode = pm;
    }

    /**
     * Package name for the generated class is make up of the base package
     * name, which is user settable, and the derived package name.  The
     * derived package name directly mirrors the file heirachy of the JSP page.
     */
    public String getServletPackageName() {
        if (isTagFile()) {
            String className = tagInfo.getTagClassName();
            int lastIndex = className.lastIndexOf('.');
            String pkgName = "";
            if (lastIndex != -1) {
                pkgName = className.substring(0, lastIndex);
            }
            return pkgName;
        } else {
            String dPackageName = getDerivedPackageName();
            if (dPackageName.length() == 0) {
                return basePackageName;
            }
            return basePackageName + '.' + getDerivedPackageName();
        }

    }

    private String getDerivedPackageName() {
        if (derivedPackageName == null) {
            int iSep = jspUri.lastIndexOf('/');
            derivedPackageName = (iSep > 0) ?
                    JspUtil.makeJavaPackage(jspUri.substring(0, iSep)) : "";
        }
        return derivedPackageName;
    }

    /**
     * The package name into which the servlet class is generated.
     */
    public void setServletPackageName(String servletPackageName) {
        this.basePackageName = servletPackageName;
    }

    /**
     * Full path name of the Java file into which the servlet is being
     * generated.
     */
    public String getServletJavaFileName() {

        if (servletJavaFileName == null) {
            servletJavaFileName =
                    getServletClassName() + ".java";
        }
        return servletJavaFileName;
    }

    public void setServletJavaFileName(String servletJavaFileName) {
        this.servletJavaFileName = servletJavaFileName;
    }

    /**
     * Get hold of the Options object for this context.
     */
    public Options getOptions() {
        return options;
    }

    public ServletContext getServletContext() {
        return context;
    }

    public JspRuntimeContext getRuntimeContext() {
        return rctxt;
    }

    /**
     * Path of the Java file relative to the work directory.
     */
    public String getJavaPath() {

        if (javaPath != null) {
            return javaPath;
        }

        if (isTagFile()) {
            String tagName = tagInfo.getTagClassName();
            javaPath = tagName.replace('.', '/') + ".java";
        } else {
            javaPath = getServletPackageName().replace('.', '/') + '/' +
                    getServletClassName() + ".java";
        }
        return javaPath;
    }

    public String getClassFileName() {

        if (classFileName == null) {
            classFileName = getServletClassName() + ".class";
        }
        return classFileName;
    }

    /**
     * Get the content type of this JSP.
     * <p/>
     * Content type includes content type and encoding.
     */
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Where is the servlet being generated?
     */
    public ServletWriter getWriter() {
        return writer;
    }

    public void setWriter(ServletWriter writer) {
        this.writer = writer;
    }

    /**
     * Gets the 'location' of the TLD associated with the given taglib 'uri'.
     *
     * @return An array of two Strings: The first element denotes the real
     *         path to the TLD. If the path to the TLD points to a jar file, then the
     *         second element denotes the name of the TLD entry in the jar file.
     *         Returns null if the given uri is not associated with any tag library
     *         'exposed' in the web application.
     */
    public String[] getTldLocation(String uri) throws JasperException {
        String[] location =
                getOptions().getTldLocationsCache().getLocation(uri);
        return location;
    }

    /**
     * Are we keeping generated code around?
     */
    public boolean keepGenerated() {
        return getOptions().getKeepGenerated();
    }

    // ==================== Removal ==================== 

    public void incrementRemoved() {
        if (removed > 1) {
            jspCompiler.removeGeneratedFiles();
            if (rctxt != null)
                rctxt.removeWrapper(jspUri);
        }
        removed++;
    }

    public boolean isRemoved() {
        if (removed > 1) {
            return true;
        }
        return false;
    }

    // ==================== Compile and reload ====================

    public void compile() throws JasperException, FileNotFoundException {
        createCompiler();
        if (isPackagedTagFile || jspCompiler.isOutDated()) {
            try {
                jspLoader = null;
                jspCompiler.compile();
                jsw.setReload(true);
                jsw.setCompilationException(null);
            } catch (JasperException ex) {
                // Cache compilation exception
                jsw.setCompilationException(ex);
                throw ex;
            } catch (Exception ex) {
                ex.printStackTrace();
                JasperException je = new JasperException(
                        Localizer.getMessage("jsp.error.unable.compile"),
                        ex);
                // Cache compilation exception
                jsw.setCompilationException(je);
                throw je;
            }
        }
    }

    public String getJspUri() {
        return jspUri;
    }

// ==================== Manipulating the class ====================

    public Class load()
            throws JasperException, FileNotFoundException {
        try {
            getJspLoader();

            String name;
            if (isTagFile()) {
                name = tagInfo.getTagClassName();
            } else {
                name = getServletPackageName() + "." + getServletClassName();
            }
            servletClass = jspLoader.loadClass(name);
        } catch (ClassNotFoundException cex) {
            throw new JasperException(Localizer.getMessage("jsp.error.unable.load"),
                    cex);
        } catch (Exception ex) {
            throw new JasperException(Localizer.getMessage("jsp.error.unable.compile"),
                    ex);
        }
        removed = 0;
        return servletClass;
    }

    // ==================== Private methods ==================== 

    static Object outputDirLock = new Object();

    private static final boolean isPathSeparator(char c) {
        return (c == '/' || c == '\\');
    }

    public static final String canonicalURI(String s) {
        if (s == null) return null;
        StringBuffer result = new StringBuffer();
        final int len = s.length();
        int pos = 0;
        while (pos < len) {
            char c = s.charAt(pos);
            if (isPathSeparator(c)) {
                /*
                * multiple path separators.
                * 'foo///bar' -> 'foo/bar'
                */
                while (pos + 1 < len && isPathSeparator(s.charAt(pos + 1))) {
                    ++pos;
                }

                if (pos + 1 < len && s.charAt(pos + 1) == '.') {
                    /*
                    * a single dot at the end of the path - we are done.
                    */
                    if (pos + 2 >= len) break;

                    switch (s.charAt(pos + 2)) {
                        /*
                        * self directory in path
                        * foo/./bar -> foo/bar
                        */
                        case '/':
                        case '\\':
                            pos += 2;
                            continue;

                            /*
                            * two dots in a path: go back one hierarchy.
                            * foo/bar/../baz -> foo/baz
                            */
                        case '.':
                            // only if we have exactly _two_ dots.
                            if (pos + 3 < len && isPathSeparator(s.charAt(pos + 3))) {
                                pos += 3;
                                int separatorPos = result.length() - 1;
                                while (separatorPos >= 0 &&
                                        !isPathSeparator(result
                                                .charAt(separatorPos))) {
                                    --separatorPos;
                                }
                                if (separatorPos >= 0)
                                    result.setLength(separatorPos);
                                continue;
                            }
                    }
                }
            }
            result.append(c);
            ++pos;
        }
        return result.toString();
    }
}

