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
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.jasper.compiler.Compiler;
import org.apache.struts2.jasper.compiler.*;
import org.apache.struts2.jasper.servlet.JspCServletContext;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Shell for the jspc compiler.  Handles all options associated with the
 * command line and creates compilation contexts which it then compiles
 * according to the specified options.
 * <p/>
 * This version can process files from a _single_ webapp at once, i.e.
 * a single docbase can be specified.
 * <p/>
 * It can be used as an Ant task using:
 * <pre>
 *   &lt;taskdef classname="org.apache.struts.jasperJspC" name="jasper2" &gt;
 *      &lt;classpath&gt;
 *          &lt;pathelement location="${java.home}/../lib/tools.jar"/&gt;
 *          &lt;fileset dir="${ENV.CATALINA_HOME}/server/lib"&gt;
 *              &lt;include name="*.jar"/&gt;
 *          &lt;/fileset&gt;
 *          &lt;fileset dir="${ENV.CATALINA_HOME}/common/lib"&gt;
 *              &lt;include name="*.jar"/&gt;
 *          &lt;/fileset&gt;
 *          &lt;path refid="myjars"/&gt;
 *       &lt;/classpath&gt;
 *  &lt;/taskdef&gt;
 * <p/>
 *  &lt;jasper2 verbose="0"
 *           package="my.package"
 *           uriroot="${webapps.dir}/${webapp.name}"
 *           webXmlFragment="${build.dir}/generated_web.xml"
 *           outputDir="${webapp.dir}/${webapp.name}/WEB-INF/src/my/package" /&gt;
 * </pre>
 *
 * @author Danno Ferrin
 * @author Pierre Delisle
 * @author Costin Manolache
 * @author Yoav Shapira
 */
public class JspC implements Options {
    /**
     * The default Microsoft Internet Explorer class ID.
     */
    public static final String DEFAULT_IE_CLASS_ID =
            "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";

    /**
     * Logger (set by constructor.)
     */
    private Logger log;

    private static final String SWITCH_VERBOSE = "-v";
    private static final String SWITCH_HELP = "-help";
    private static final String SWITCH_QUIET = "-q";
    private static final String SWITCH_OUTPUT_DIR = "-d";
    private static final String SWITCH_IE_CLASS_ID = "-ieplugin";
    private static final String SWITCH_PACKAGE_NAME = "-p";
    private static final String SWITCH_CACHE = "-cache";
    private static final String SWITCH_CLASS_NAME = "-c";
    private static final String SWITCH_FULL_STOP = "--";
    private static final String SWITCH_COMPILE = "-compile";
    private static final String SWITCH_SOURCE = "-source";
    private static final String SWITCH_TARGET = "-target";
    private static final String SWITCH_URI_BASE = "-uribase";
    private static final String SWITCH_URI_ROOT = "-uriroot";
    private static final String SWITCH_FILE_WEBAPP = "-webapp";
    private static final String SWITCH_WEBAPP_INC = "-webinc";
    private static final String SWITCH_WEBAPP_XML = "-webxml";
    private static final String SWITCH_MAPPED = "-mapped";
    private static final String SWITCH_XPOWERED_BY = "-xpoweredBy";
    private static final String SWITCH_TRIM_SPACES = "-trimSpaces";
    private static final String SWITCH_CLASSPATH = "-classpath";
    private static final String SWITCH_DIE = "-die";
    private static final String SWITCH_POOLING = "-poolingEnabled";
    private static final String SWITCH_ENCODING = "-javaEncoding";
    private static final String SWITCH_SMAP = "-smap";
    private static final String SWITCH_DUMP_SMAP = "-dumpsmap";

    private static final String SHOW_SUCCESS = "-s";
    private static final String LIST_ERRORS = "-l";
    private static final int NO_WEBXML = 0;
    private static final int INC_WEBXML = 10;
    private static final int ALL_WEBXML = 20;
    private static final int DEFAULT_DIE_LEVEL = 1;
    private static final int NO_DIE_LEVEL = 0;

    private static final String[] insertBefore =
            {"</web-app>", "<servlet-mapping>", "<session-config>",
                    "<mime-mapping>", "<welcome-file-list>", "<error-page>", "<taglib>",
                    "<resource-env-ref>", "<resource-ref>", "<security-constraint>",
                    "<login-config>", "<security-role>", "<env-entry>", "<ejb-ref>",
                    "<ejb-local-ref>"};

    private static int die;
    private String classPath = null;
    private URLClassLoader loader = null;
    private boolean trimSpaces = false;
    private boolean genStringAsCharArray = false;
    private boolean xpoweredBy;
    private boolean mappedFile = false;
    private boolean poolingEnabled = true;
    private File scratchDir;
    private String ieClassId = DEFAULT_IE_CLASS_ID;
    private String targetPackage;
    private String targetClassName;
    private String uriBase;
    //private String uriRoot;
    private int dieLevel;
    private boolean helpNeeded = false;
    private boolean compile = false;
    private boolean smapSuppressed = true;
    private boolean smapDumped = false;
    private boolean caching = true;
    private Map cache = new HashMap();

    private String compiler = null;

    private ClassLoaderInterface classLoaderInterface;

    private String compilerTargetVM = "1.4";
    private String compilerSourceVM = "1.4";

    private boolean classDebugInfo = true;

    /**
     * Throw an exception if there's a compilation error, or swallow it.
     * Default is true to preserve old behavior.
     */
    private boolean failOnError = true;

    /**
     * The file extensions to be handled as JSP files.
     * Default list is .jsp and .jspx.
     */
    private List extensions;

    /**
     * The pages.
     */
    private List pages = new Vector();

    /**
     * Needs better documentation, this data member does.
     * True by default.
     */
    private boolean errorOnUseBeanInvalidClassAttribute = true;

    /**
     * The java file encoding.  Default
     * is UTF-8.  Added per bugzilla 19622.
     */
    private String javaEncoding = "UTF-8";

    // Generation of web.xml fragments
    private String webxmlFile;
    private int webxmlLevel;

    private Writer mapout;
    private CharArrayWriter servletout;
    private CharArrayWriter mappingout;

    /**
     * The servlet context.
     */
    private JspCServletContext context;

    /**
     * The runtime context.
     * Maintain a dummy JspRuntimeContext for compiling tag files.
     */
    private JspRuntimeContext rctxt;

    /**
     * Cache for the TLD locations
     */
    private TldLocationsCache tldLocationsCache = null;

    private JspConfig jspConfig = null;
    private TagPluginManager tagPluginManager = null;

    private boolean verbose = false;
    private boolean listErrors = false;
    private boolean showSuccess = false;
    private int argPos;
    private boolean fullstop = false;
    private String args[];
    private String sourceCode;

    public static void main(String arg[]) {
        if (arg.length == 0) {
            System.out.println(Localizer.getMessage("jspc.usage"));
        } else {
            try {
                JspC jspc = new JspC();
                jspc.setArgs(arg);
                if (jspc.helpNeeded) {
                    System.out.println(Localizer.getMessage("jspc.usage"));
                } else {
                    jspc.execute();
                }
            } catch (JasperException je) {
                System.err.println(je);
                //System.err.println(je.getMessage());
                if (die != NO_DIE_LEVEL) {
                    System.exit(die);
                }
            }
        }
    }

    /**
     * Constructor.
     */
    public JspC() {
        log = LoggerFactory.getLogger(getClass());
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setClassLoaderInterface(ClassLoaderInterface classLoaderInterface) {
        this.classLoaderInterface = classLoaderInterface;
    }

    public void setArgs(String[] arg) throws JasperException {
        args = arg;
        String tok;

        dieLevel = NO_DIE_LEVEL;
        die = dieLevel;

        while ((tok = nextArg()) != null) {
            if (tok.equals(SWITCH_VERBOSE)) {
                verbose = true;
                showSuccess = true;
                listErrors = true;
            } else if (tok.equals(SWITCH_OUTPUT_DIR)) {
                tok = nextArg();
                setOutputDir(tok);
            } else if (tok.equals(SWITCH_PACKAGE_NAME)) {
                targetPackage = nextArg();
            } else if (tok.equals(SWITCH_COMPILE)) {
                compile = true;
            } else if (tok.equals(SWITCH_CLASS_NAME)) {
                targetClassName = nextArg();
            } else if (tok.equals(SWITCH_URI_BASE)) {
                uriBase = nextArg();
            } else if (tok.equals(SHOW_SUCCESS)) {
                showSuccess = true;
            } else if (tok.equals(LIST_ERRORS)) {
                listErrors = true;
            } else if (tok.equals(SWITCH_WEBAPP_INC)) {
                webxmlFile = nextArg();
                if (webxmlFile != null) {
                    webxmlLevel = INC_WEBXML;
                }
            } else if (tok.equals(SWITCH_WEBAPP_XML)) {
                webxmlFile = nextArg();
                if (webxmlFile != null) {
                    webxmlLevel = ALL_WEBXML;
                }
            } else if (tok.equals(SWITCH_MAPPED)) {
                mappedFile = true;
            } else if (tok.equals(SWITCH_XPOWERED_BY)) {
                xpoweredBy = true;
            } else if (tok.equals(SWITCH_TRIM_SPACES)) {
                setTrimSpaces(true);
            } else if (tok.equals(SWITCH_CACHE)) {
                tok = nextArg();
                if ("false".equals(tok)) {
                    caching = false;
                } else {
                    caching = true;
                }
            } else if (tok.equals(SWITCH_CLASSPATH)) {
                setClassPath(nextArg());
            } else if (tok.startsWith(SWITCH_DIE)) {
                try {
                    dieLevel = Integer.parseInt(
                            tok.substring(SWITCH_DIE.length()));
                } catch (NumberFormatException nfe) {
                    dieLevel = DEFAULT_DIE_LEVEL;
                }
                die = dieLevel;
            } else if (tok.equals(SWITCH_HELP)) {
                helpNeeded = true;
            } else if (tok.equals(SWITCH_POOLING)) {
                tok = nextArg();
                if ("false".equals(tok)) {
                    poolingEnabled = false;
                } else {
                    poolingEnabled = true;
                }
            } else if (tok.equals(SWITCH_ENCODING)) {
                setJavaEncoding(nextArg());
            } else if (tok.equals(SWITCH_SOURCE)) {
                setCompilerSourceVM(nextArg());
            } else if (tok.equals(SWITCH_TARGET)) {
                setCompilerTargetVM(nextArg());
            } else if (tok.equals(SWITCH_SMAP)) {
                smapSuppressed = false;
            } else if (tok.equals(SWITCH_DUMP_SMAP)) {
                smapDumped = true;
            } else {
                if (tok.startsWith("-")) {
                    throw new JasperException("Unrecognized option: " + tok +
                            ".  Use -help for help.");
                }
                if (!fullstop) {
                    argPos--;
                }
                // Start treating the rest as JSP Pages
                break;
            }
        }

        // Add all extra arguments to the list of files
        while (true) {
            String file = nextFile();
            if (file == null) {
                break;
            }
            pages.add(file);
        }
    }

    public boolean getKeepGenerated() {
        // isn't this why we are running jspc?
        return true;
    }

    public boolean getTrimSpaces() {
        return trimSpaces;
    }

    public void setTrimSpaces(boolean ts) {
        this.trimSpaces = ts;
    }

    public boolean isPoolingEnabled() {
        return poolingEnabled;
    }

    public void setPoolingEnabled(boolean poolingEnabled) {
        this.poolingEnabled = poolingEnabled;
    }

    public boolean isXpoweredBy() {
        return xpoweredBy;
    }

    public void setXpoweredBy(boolean xpoweredBy) {
        this.xpoweredBy = xpoweredBy;
    }

    public boolean getErrorOnUseBeanInvalidClassAttribute() {
        return errorOnUseBeanInvalidClassAttribute;
    }

    public void setErrorOnUseBeanInvalidClassAttribute(boolean b) {
        errorOnUseBeanInvalidClassAttribute = b;
    }

    public int getTagPoolSize() {
        return Constants.MAX_POOL_SIZE;
    }

    /**
     * Are we supporting HTML mapped servlets?
     */
    public boolean getMappedFile() {
        return mappedFile;
    }

    // Off-line compiler, no need for security manager
    public Object getProtectionDomain() {
        return null;
    }

    public boolean getSendErrorToClient() {
        // implied send to System.err
        return true;
    }

    public void setClassDebugInfo(boolean b) {
        classDebugInfo = b;
    }

    public boolean getClassDebugInfo() {
        // compile with debug info
        return classDebugInfo;
    }

    /**
     * @see Options#isCaching()
     */
    public boolean isCaching() {
        return caching;
    }

    /**
     * @see Options#isCaching()
     */
    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    /**
     * @see Options#getCache()
     */
    public Map getCache() {
        return cache;
    }

    /**
     * Background compilation check intervals in seconds
     */
    public int getCheckInterval() {
        return 0;
    }

    /**
     * Modification test interval.
     */
    public int getModificationTestInterval() {
        return 0;
    }

    /**
     * Is Jasper being used in development mode?
     */
    public boolean getDevelopment() {
        return false;
    }

    /**
     * Is the generation of SMAP info for JSR45 debuggin suppressed?
     */
    public boolean isSmapSuppressed() {
        return smapSuppressed;
    }

    /**
     * Set smapSuppressed flag.
     */
    public void setSmapSuppressed(boolean smapSuppressed) {
        this.smapSuppressed = smapSuppressed;
    }


    /**
     * Should SMAP info for JSR45 debugging be dumped to a file?
     */
    public boolean isSmapDumped() {
        return smapDumped;
    }

    /**
     * Set smapSuppressed flag.
     */
    public void setSmapDumped(boolean smapDumped) {
        this.smapDumped = smapDumped;
    }


    /**
     * Determines whether text strings are to be generated as char arrays,
     * which improves performance in some cases.
     *
     * @param genStringAsCharArray true if text strings are to be generated as
     *                             char arrays, false otherwise
     */
    public void setGenStringAsCharArray(boolean genStringAsCharArray) {
        this.genStringAsCharArray = genStringAsCharArray;
    }

    /**
     * Indicates whether text strings are to be generated as char arrays.
     *
     * @return true if text strings are to be generated as char arrays, false
     *         otherwise
     */
    public boolean genStringAsCharArray() {
        return genStringAsCharArray;
    }

    /**
     * Sets the class-id value to be sent to Internet Explorer when using
     * <jsp:plugin> tags.
     *
     * @param ieClassId Class-id value
     */
    public void setIeClassId(String ieClassId) {
        this.ieClassId = ieClassId;
    }

    /**
     * Gets the class-id value that is sent to Internet Explorer when using
     * <jsp:plugin> tags.
     *
     * @return Class-id value
     */
    public String getIeClassId() {
        return ieClassId;
    }

    public File getScratchDir() {
        return scratchDir;
    }

    public Class getJspCompilerPlugin() {
        // we don't compile, so this is meanlingless
        return null;
    }

    public String getJspCompilerPath() {
        // we don't compile, so this is meanlingless
        return null;
    }

    /**
     * Compiler to use.
     */
    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String c) {
        compiler = c;
    }

    /**
     * @see Options#getCompilerTargetVM
     */
    public String getCompilerTargetVM() {
        return compilerTargetVM;
    }

    public void setCompilerTargetVM(String vm) {
        compilerTargetVM = vm;
    }

    /**
     * @see Options#getCompilerSourceVM()
     */
    public String getCompilerSourceVM() {
        return compilerSourceVM;
    }

    /**
     * @see Options#getCompilerSourceVM()
     */
    public void setCompilerSourceVM(String vm) {
        compilerSourceVM = vm;
    }

    public TldLocationsCache getTldLocationsCache() {
        return tldLocationsCache;
    }

    /**
     * Returns the encoding to use for
     * java files.  The default is UTF-8.
     *
     * @return String The encoding
     */
    public String getJavaEncoding() {
        return javaEncoding;
    }

    /**
     * Sets the encoding to use for
     * java files.
     *
     * @param encodingName The name, e.g. "UTF-8"
     */
    public void setJavaEncoding(String encodingName) {
        javaEncoding = encodingName;
    }

    public boolean getFork() {
        return false;
    }

    public String getClassPath() {
        if (classPath != null)
            return classPath;
        return System.getProperty("java.class.path");
    }

    public void setClassPath(String s) {
        classPath = s;
    }

    /**
     * Returns the list of file extensions
     * that are treated as JSP files.
     *
     * @return The list of extensions
     */
    public List getExtensions() {
        return extensions;
    }

    /**
     * Adds the given file extension to the
     * list of extensions handled as JSP files.
     *
     * @param extension The extension to add, e.g. "myjsp"
     */
    protected void addExtension(final String extension) {
        if (extension != null) {
            if (extensions == null) {
                extensions = new Vector();
            }

            extensions.add(extension);
        }
    }

    /**
     * Parses comma-separated list of JSP files to be processed.  If the argument
     * is null, nothing is done.
     * <p/>
     * <p>Each file is interpreted relative to uriroot, unless it is absolute,
     * in which case it must start with uriroot.</p>
     *
     * @param jspFiles Comma-separated list of JSP files to be processed
     */
    public void setJspFiles(final String jspFiles) {
        if (jspFiles == null) {
            return;
        }

        StringTokenizer tok = new StringTokenizer(jspFiles, ",");
        while (tok.hasMoreTokens()) {
            pages.add(tok.nextToken());
        }
    }

    /**
     * Sets the compile flag.
     *
     * @param b Flag value
     */
    public void setCompile(final boolean b) {
        compile = b;
    }

    /**
     * Sets the verbosity level.  The actual number doesn't
     * matter: if it's greater than zero, the verbose flag will
     * be true.
     *
     * @param level Positive means verbose
     */
    public void setVerbose(final int level) {
        if (level > 0) {
            verbose = true;
            showSuccess = true;
            listErrors = true;
        }
    }

    public void setValidateXml(boolean b) {
        org.apache.struts2.jasper.xmlparser.ParserUtils.validating = b;
    }

    public void setListErrors(boolean b) {
        listErrors = b;
    }

    public void setOutputDir(String s) {
        if (s != null) {
            scratchDir = resolveFile(s).getAbsoluteFile();
        } else {
            scratchDir = null;
        }
    }

    public void setPackage(String p) {
        targetPackage = p;
    }

    /**
     * Class name of the generated file ( without package ).
     * Can only be used if a single file is converted.
     * XXX Do we need this feature ?
     */
    public void setClassName(String p) {
        targetClassName = p;
    }

    /**
     * File where we generate a web.xml fragment with the class definitions.
     */
    public void setWebXmlFragment(String s) {
        webxmlFile = resolveFile(s).getAbsolutePath();
        webxmlLevel = INC_WEBXML;
    }

    /**
     * File where we generate a complete web.xml with the class definitions.
     */
    public void setWebXml(String s) {
        webxmlFile = resolveFile(s).getAbsolutePath();
        webxmlLevel = ALL_WEBXML;
    }

    /**
     * Set the option that throws an exception in case of a compilation error.
     */
    public void setFailOnError(final boolean b) {
        failOnError = b;
    }

    public boolean getFailOnError() {
        return failOnError;
    }

    /**
     * Obtain JSP configuration informantion specified in web.xml.
     */
    public JspConfig getJspConfig() {
        return jspConfig;
    }

    public TagPluginManager getTagPluginManager() {
        return tagPluginManager;
    }

    public void generateWebMapping(String file, JspCompilationContext clctxt)
            throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Generating web mapping for file " + file
                    + " using compilation context " + clctxt);
        }

        String className = clctxt.getServletClassName();
        String packageName = clctxt.getServletPackageName();

        String thisServletName;
        if ("".equals(packageName)) {
            thisServletName = className;
        } else {
            thisServletName = packageName + '.' + className;
        }

        if (servletout != null) {
            servletout.write("\n    <servlet>\n        <servlet-name>");
            servletout.write(thisServletName);
            servletout.write("</servlet-name>\n        <servlet-class>");
            servletout.write(thisServletName);
            servletout.write("</servlet-class>\n    </servlet>\n");
        }
        if (mappingout != null) {
            mappingout.write("\n    <servlet-mapping>\n        <servlet-name>");
            mappingout.write(thisServletName);
            mappingout.write("</servlet-name>\n        <url-pattern>");
            mappingout.write(file.replace('\\', '/'));
            mappingout.write("</url-pattern>\n    </servlet-mapping>\n");

        }
    }

    private void processFile(String file)
            throws JasperException {
        if (log.isDebugEnabled()) {
            log.debug("Processing file: " + file);
        }

        ClassLoader originalClassLoader = null;

        try {
            // set up a scratch/output dir if none is provided
            if (scratchDir == null) {
                String temp = System.getProperty("java.io.tmpdir");
                if (temp == null) {
                    temp = "";
                }
                scratchDir = new File(new File(temp).getAbsolutePath());
            }

            String jspUri = file.replace('\\', '/');
            JspCompilationContext clctxt = new JspCompilationContext
                    (jspUri, false, this, context, null, rctxt, classLoaderInterface);

            /* Override the defaults */
            if ((targetClassName != null) && (targetClassName.length() > 0)) {
                clctxt.setServletClassName(targetClassName);
                targetClassName = null;
            }
            if (targetPackage != null) {
                clctxt.setServletPackageName(targetPackage);
            }

            originalClassLoader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                initClassLoader(clctxt);
            }
            Thread.currentThread().setContextClassLoader(loader);

            clctxt.setClassLoader(loader);
            clctxt.setClassPath(classPath);

            Compiler clc = clctxt.createCompiler();

            // If compile is set, generate both .java and .class, if
            // .jsp file is newer than .class file;
            // Otherwise only generate .java, if .jsp file is newer than
            // the .java file
            if (clc.isOutDated(compile)) {
                if (log.isDebugEnabled()) {
                    log.debug(jspUri + " is out dated, compiling...");
                }

                clc.compile(compile, true);
            }

            // Generate mapping
            generateWebMapping(file, clctxt);
            if (showSuccess) {
                log.info("Built File: " + file);
            }

            this.sourceCode = clctxt.getSourceCode();

        } catch (JasperException je) {
            Throwable rootCause = je;
            while (rootCause instanceof JasperException
                    && ((JasperException) rootCause).getRootCause() != null) {
                rootCause = ((JasperException) rootCause).getRootCause();
            }
            if (rootCause != je) {
                log.error(Localizer.getMessage("jspc.error.generalException",
                        file),
                        rootCause);
            }

            // Bugzilla 35114.
            if (getFailOnError()) {
                throw je;
            } else {
                log.error(je.getMessage(), je);
                ;
            }

        } catch (Exception e) {
            if ((e instanceof FileNotFoundException) && log.isWarnEnabled()) {
                log.warn(Localizer.getMessage("jspc.error.fileDoesNotExist",
                        e.getMessage()));
            }
            throw new JasperException(e);
        } finally {
            if (originalClassLoader != null) {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
    }

    public Set<String> getTldAbsolutePaths() {
        return tldLocationsCache.getAbsolutePathsOfLocations();
    }


    /**
     * Executes the compilation.
     *
     * @throws JasperException If an error occurs
     */
    public void execute() throws JasperException {
        if (log.isDebugEnabled()) {
            log.debug("execute() starting for " + pages.size() + " pages.");
        }

        try {
            if (context == null) {
                initServletContext();
            }

            initWebXml();

            Iterator iter = pages.iterator();
            while (iter.hasNext()) {
                String nextjsp = iter.next().toString();

                processFile(nextjsp);
            }

            completeWebXml();
        } catch (JasperException je) {
            Throwable rootCause = je;
            while (rootCause instanceof JasperException
                    && ((JasperException) rootCause).getRootCause() != null) {
                rootCause = ((JasperException) rootCause).getRootCause();
            }
            if (rootCause != je) {
                rootCause.printStackTrace();
            }
            throw je;
        } finally {
            if (loader != null) {
                LogFactory.release(loader);
            }
        }
    }

    // ==================== Private utility methods ====================

    private String nextArg() {
        if ((argPos >= args.length)
                || (fullstop = SWITCH_FULL_STOP.equals(args[argPos]))) {
            return null;
        } else {
            return args[argPos++];
        }
    }

    private String nextFile() {
        if (fullstop) argPos++;
        if (argPos >= args.length) {
            return null;
        } else {
            return args[argPos++];
        }
    }

    private void initWebXml() {
        try {
            if (webxmlLevel >= INC_WEBXML) {
                File fmapings = new File(webxmlFile);
                mapout = new FileWriter(fmapings);
                servletout = new CharArrayWriter();
                mappingout = new CharArrayWriter();
            } else {
                mapout = null;
                servletout = null;
                mappingout = null;
            }
            if (webxmlLevel >= ALL_WEBXML) {
                mapout.write(Localizer.getMessage("jspc.webxml.header"));
                mapout.flush();
            } else if ((webxmlLevel >= INC_WEBXML)) {
                mapout.write(Localizer.getMessage("jspc.webinc.header"));
                mapout.flush();
            }
        } catch (IOException ioe) {
            mapout = null;
            servletout = null;
            mappingout = null;
        }
    }

    private void completeWebXml() {
        if (mapout != null) {
            try {
                servletout.writeTo(mapout);
                mappingout.writeTo(mapout);
                if (webxmlLevel >= ALL_WEBXML) {
                    mapout.write(Localizer.getMessage("jspc.webxml.footer"));
                } else if ((webxmlLevel >= INC_WEBXML)) {
                    mapout.write(Localizer.getMessage("jspc.webinc.footer"));
                }
                mapout.close();
            } catch (IOException ioe) {
                // noting to do if it fails since we are done with it
            }
        }
    }

    private void initServletContext() {

        context = new JspCServletContext
                (new PrintWriter(System.out),
                        classLoaderInterface);
        tldLocationsCache = new TldLocationsCache(context, true);

        rctxt = new JspRuntimeContext(context, this);
        jspConfig = new JspConfig(context);
        tagPluginManager = new TagPluginManager(context);
    }

    /**
     * Initializes the classloader as/if needed for the given
     * compilation context.
     *
     * @param clctxt The compilation context
     * @throws IOException If an error occurs
     */
    private void initClassLoader(JspCompilationContext clctxt)
            throws IOException {

        classPath = getClassPath();

        ClassLoader jspcLoader = getClass().getClassLoader();
        // Turn the classPath into URLs
        ArrayList urls = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(classPath,
                File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();
            try {
                File libFile = new File(path);
                urls.add(libFile.toURL());
            } catch (IOException ioe) {
                // Failing a toCanonicalPath on a file that
                // exists() should be a JVM regression test,
                // therefore we have permission to freak uot
                throw new RuntimeException(ioe.toString());
            }
        }

        //TODO: add .tld files to the URLCLassLoader

        URL urlsA[] = new URL[urls.size()];
        urls.toArray(urlsA);
        loader = new URLClassLoader(urlsA, this.getClass().getClassLoader());

    }

    /**
     * Resolves the relative or absolute pathname correctly
     * in both Ant and command-line situations.  If Ant launched
     * us, we should use the basedir of the current project
     * to resolve relative paths.
     * <p/>
     * See Bugzilla 35571.
     *
     * @param s The file
     * @return The file resolved
     */
    protected File resolveFile(final String s) {
        //TODO: what to do with this
        return new File(s);
    }
}
