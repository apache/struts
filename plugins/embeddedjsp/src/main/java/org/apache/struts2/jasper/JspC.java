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

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.apache.struts2.jasper.compiler.Compiler;
import org.apache.struts2.jasper.compiler.JspConfig;
import org.apache.struts2.jasper.compiler.JspRuntimeContext;
import org.apache.struts2.jasper.compiler.Localizer;
import org.apache.struts2.jasper.compiler.TagPluginManager;
import org.apache.struts2.jasper.compiler.TldLocationsCache;
import org.apache.struts2.jasper.servlet.JspCServletContext;
import org.apache.struts2.jasper.xmlparser.ParserUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;

/**
 * Shell for the jspc compiler.  Handles all options associated with the
 * command line and creates compilation contexts which it then compiles
 * according to the specified options.
 *
 * This version can process files from a _single_ webapp at once, i.e.
 * a single docbase can be specified.
 *
 * It can be used as an Ant task using:
 * <pre>
 *   &lt;taskdef classname="org.apache.struts2.jasper.JspC" name="jasper2" &gt;
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
 *
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

    public static final String DEFAULT_IE_CLASS_ID =
            "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";

    // Logger
    protected static Log log = LogFactory.getLog(JspC.class);

    protected static final String SWITCH_VERBOSE = "-v";
    protected static final String SWITCH_HELP = "-help";
    protected static final String SWITCH_OUTPUT_DIR = "-d";
    protected static final String SWITCH_PACKAGE_NAME = "-p";
    protected static final String SWITCH_CACHE = "-cache";
    protected static final String SWITCH_CLASS_NAME = "-c";
    protected static final String SWITCH_FULL_STOP = "--";
    protected static final String SWITCH_COMPILE = "-compile";
    protected static final String SWITCH_SOURCE = "-source";
    protected static final String SWITCH_TARGET = "-target";
    protected static final String SWITCH_URI_BASE = "-uribase";
    protected static final String SWITCH_URI_ROOT = "-uriroot";
    protected static final String SWITCH_FILE_WEBAPP = "-webapp";
    protected static final String SWITCH_WEBAPP_INC = "-webinc";
    protected static final String SWITCH_WEBAPP_XML = "-webxml";
    protected static final String SWITCH_MAPPED = "-mapped";
    protected static final String SWITCH_XPOWERED_BY = "-xpoweredBy";
    protected static final String SWITCH_TRIM_SPACES = "-trimSpaces";
    protected static final String SWITCH_CLASSPATH = "-classpath";
    protected static final String SWITCH_DIE = "-die";
    protected static final String SWITCH_POOLING = "-poolingEnabled";
    protected static final String SWITCH_ENCODING = "-javaEncoding";
    protected static final String SWITCH_SMAP = "-smap";
    protected static final String SWITCH_DUMP_SMAP = "-dumpsmap";

    protected static final String SHOW_SUCCESS ="-s";
    protected static final String LIST_ERRORS = "-l";
    protected static final int INC_WEBXML = 10;
    protected static final int ALL_WEBXML = 20;
    protected static final int DEFAULT_DIE_LEVEL = 1;
    protected static final int NO_DIE_LEVEL = 0;

    protected static final String[] insertBefore =
    { "</web-app>", "<servlet-mapping>", "<session-config>",
      "<mime-mapping>", "<welcome-file-list>", "<error-page>", "<taglib>",
      "<resource-env-ref>", "<resource-ref>", "<security-constraint>",
      "<login-config>", "<security-role>", "<env-entry>", "<ejb-ref>",
      "<ejb-local-ref>" };

    protected static int die;
    protected String classPath = null;
    protected URLClassLoader loader = null;
    protected boolean trimSpaces = false;
    protected boolean genStringAsCharArray = false;
    protected boolean xpoweredBy;
    protected boolean mappedFile = false;
    protected boolean poolingEnabled = true;
    protected File scratchDir;
    protected String ieClassId = DEFAULT_IE_CLASS_ID;
    protected String targetPackage;
    protected String targetClassName;
    protected String uriBase;
    protected String uriRoot;
    protected int dieLevel;
    protected boolean helpNeeded = false;
    protected boolean compile = false;
    protected boolean smapSuppressed = true;
    protected boolean smapDumped = false;
    protected boolean caching = true;
    protected Map cache = new HashMap();

    protected String compiler = null;

    protected String compilerTargetVM = "1.4";
    protected String compilerSourceVM = "1.4";

    protected boolean classDebugInfo = true;

    /**
     * Throw an exception if there's a compilation error, or swallow it.
     * Default is true to preserve old behavior.
     */
    protected boolean failOnError = true;

    /**
     * The file extensions to be handled as JSP files.
     * Default list is .jsp and .jspx.
     */
    protected List extensions;

    /**
     * The pages.
     */
    protected List pages = new Vector();

    /**
     * Needs better documentation, this data member does.
     * True by default.
     */
    protected boolean errorOnUseBeanInvalidClassAttribute = true;

    /**
     * The java file encoding.  Default
     * is UTF-8.  Added per bugzilla 19622.
     */
    protected String javaEncoding = "UTF-8";

    // Generation of web.xml fragments
    protected String webxmlFile;
    protected int webxmlLevel;
    protected boolean addWebXmlMappings = false;

    protected Writer mapout;
    protected CharArrayWriter servletout;
    protected CharArrayWriter mappingout;

    /**
     * The servlet context.
     */
    protected JspCServletContext context;

    /**
     * The runtime context.
     * Maintain a dummy JspRuntimeContext for compiling tag files.
     */
    protected JspRuntimeContext rctxt;

    /**
     * Cache for the TLD locations
     */
    protected TldLocationsCache tldLocationsCache = null;

    protected JspConfig jspConfig = null;
    protected TagPluginManager tagPluginManager = null;

    protected boolean verbose = false;
    protected boolean listErrors = false;
    protected boolean showSuccess = false;
    protected int argPos;
    protected boolean fullstop = false;
    protected String args[];

    private String sourceCode;
    private ClassLoaderInterface classLoaderInterface;


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
                if (die != NO_DIE_LEVEL) {
                    System.exit(die);
                }
            }
        }
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
            } else if (tok.equals(SWITCH_PACKAGE_NAME)) {
                targetPackage = nextArg();
            } else if (tok.equals(SWITCH_COMPILE)) {
                compile=true;
            } else if (tok.equals(SWITCH_CLASS_NAME)) {
                targetClassName = nextArg();
            } else if (tok.equals(SWITCH_URI_BASE)) {
                uriBase=nextArg();
            } else if ( tok.equals( SHOW_SUCCESS ) ) {
                showSuccess = true;
            } else if ( tok.equals( LIST_ERRORS ) ) {
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
        while( true ) {
            String file = nextFile();
            if( file==null ) {
                break;
            }
            pages.add( file );
        }
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setClassLoaderInterface(ClassLoaderInterface classLoaderInterface) {
        this.classLoaderInterface = classLoaderInterface;
    }

    public Set<String> getTldAbsolutePaths() {
        return tldLocationsCache.getAbsolutePathsOfLocations();
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

    public boolean getDisplaySourceFragment() {
        return true;
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

    public void setClassDebugInfo( boolean b ) {
        classDebugInfo=b;
    }

    public boolean getClassDebugInfo() {
        // compile with debug info
        return classDebugInfo;
    }

     /*
      * @see Options#isCaching()
     */
    public boolean isCaching() {
        return caching;
    }

    /*
     * @see Options#isCaching()
     */
    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    /*
     * @see Options#getCache()
     */
    public Map getCache() {
        return cache;
    }

    /**
     * @return Background compilation check intervals in seconds
     */
    public int getCheckInterval() {
        return 0;
    }

    /**
     * @return Modification test interval.
     */
    public int getModificationTestInterval() {
        return 0;
    }

    /**
     * @return Is Jasper being used in development mode?
     */
    public boolean getDevelopment() {
        return false;
    }

    /**
     * @return Is the generation of SMAP info for JSR45 debuggin suppressed?
     */
    public boolean isSmapSuppressed() {
        return smapSuppressed;
    }

    /**
     * @param smapSuppressed Set smapSuppressed flag.
     */
    public void setSmapSuppressed(boolean smapSuppressed) {
        this.smapSuppressed = smapSuppressed;
    }

    
    /**
     * @return  Should SMAP info for JSR45 debugging be dumped to a file?
     */
    public boolean isSmapDumped() {
        return smapDumped;
    }

    /**
     * @param smapDumped  Set smapDumped flag.
     */
    public void setSmapDumped(boolean smapDumped) {
        this.smapDumped = smapDumped;
    }

    
    /**
     * Determines whether text strings are to be generated as char arrays,
     * which improves performance in some cases.
     *
     * @param genStringAsCharArray true if text strings are to be generated as
     * char arrays, false otherwise
     */
    public void setGenStringAsCharArray(boolean genStringAsCharArray) {
        this.genStringAsCharArray = genStringAsCharArray;
    }

    /**
     * Indicates whether text strings are to be generated as char arrays.
     *
     * @return true if text strings are to be generated as char arrays, false
     * otherwise
     */
    public boolean genStringAsCharArray() {
        return genStringAsCharArray;
    }

    /**
     * Sets the class-id value to be sent to Internet Explorer when using
     * &lt;jsp:plugin&gt; tags.
     *
     * @param ieClassId Class-id value
     */
    public void setIeClassId(String ieClassId) {
        this.ieClassId = ieClassId;
    }

    /**
     * Gets the class-id value that is sent to Internet Explorer when using
     * &lt;jsp:plugin&gt; tags.
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
     * @return Compiler to use.
     */
    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String c) {
        compiler=c;
    }

    /**
     * @return Compiler class name to use.
     */
    public String getCompilerClassName() {
        return null;
    }
    
    /*
     * @see Options#getCompilerTargetVM
     */
    public String getCompilerTargetVM() {
        return compilerTargetVM;
    }

    public void setCompilerTargetVM(String vm) {
        compilerTargetVM = vm;
    }

    /*
     * @see Options#getCompilerSourceVM()
     */
     public String getCompilerSourceVM() {
         return compilerSourceVM;
     }
        
    /*
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
        if( classPath != null )
            return classPath;
        return System.getProperty("java.class.path");
    }

    public void setClassPath(String s) {
        classPath=s;
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
        if(extension != null) {
            if(extensions == null) {
                extensions = new Vector();
            }

            extensions.add(extension);
        }
    }


    /**
     * Parses comma-separated list of JSP files to be processed.  If the argument
     * is null, nothing is done.
     *
     * <p>Each file is interpreted relative to uriroot, unless it is absolute,
     * in which case it must start with uriroot.</p>
     *
     * @param jspFiles Comma-separated list of JSP files to be processed
     */
    public void setJspFiles(final String jspFiles) {
        if(jspFiles == null) {
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
    public void setCompile( final boolean b ) {
        compile = b;
    }

    /**
     * Sets the verbosity level.  The actual number doesn't
     * matter: if it's greater than zero, the verbose flag will
     * be true.
     *
     * @param level Positive means verbose
     */
    public void setVerbose( final int level ) {
        if (level > 0) {
            verbose = true;
            showSuccess = true;
            listErrors = true;
        }
    }

    public void setValidateXml( boolean b ) {
       ParserUtils.validating=b;
    }

    public void setListErrors( boolean b ) {
        listErrors = b;
    }

    public void setPackage( String p ) {
        targetPackage=p;
    }

    /**
     * @param p Class name of the generated file ( without package ).
     * Can only be used if a single file is converted.
     * XXX Do we need this feature ?
     */
    public void setClassName( String p ) {
        targetClassName=p;
    }

    public void setAddWebXmlMappings(boolean b) {
        addWebXmlMappings = b;
    }

    /**
     * @param b Set the option that throws an exception in case of a compilation error.
     */
    public void setFailOnError(final boolean b) {
        failOnError = b;
    }

    public boolean getFailOnError() {
        return failOnError;
    }

    /**
     * @return Obtain JSP configuration information specified in web.xml.
     */
    public JspConfig getJspConfig() {
        return jspConfig;
    }

    public TagPluginManager getTagPluginManager() {
        return tagPluginManager;
    }

    public void generateWebMapping( String file, JspCompilationContext clctxt )
        throws IOException
    {
        if (log.isDebugEnabled()) {
            log.debug("Generating web mapping for file " + file
                      + " using compilation context " + clctxt);
        }

        String className = clctxt.getServletClassName();
        String packageName = clctxt.getServletPackageName();

        String thisServletName;
        if  ("".equals(packageName)) {
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

    /**
     * Include the generated web.xml inside the webapp's web.xml.
     *
     * @throws IOException in case of IO errors
     */
    protected void mergeIntoWebXml() throws IOException {

        File webappBase = new File(uriRoot);
        File webXml = new File(webappBase, "WEB-INF/web.xml");
        File webXml2 = new File(webappBase, "WEB-INF/web2.xml");
        String insertStartMarker =
            Localizer.getMessage("jspc.webinc.insertStart");
        String insertEndMarker =
            Localizer.getMessage("jspc.webinc.insertEnd");

        BufferedReader reader = new BufferedReader(new FileReader(webXml));
        BufferedReader fragmentReader =
            new BufferedReader(new FileReader(webxmlFile));
        PrintWriter writer = new PrintWriter(new FileWriter(webXml2));

        // Insert the <servlet> and <servlet-mapping> declarations
        int pos = -1;
        String line = null;
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            // Skip anything previously generated by JSPC
            if (line.indexOf(insertStartMarker) >= 0) {
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        return;
                    }
                    if (line.indexOf(insertEndMarker) >= 0) {
                        line = reader.readLine();
                        line = reader.readLine();
                        if (line == null) {
                            return;
                        }
                        break;
                    }
                }
            }
            for (int i = 0; i < insertBefore.length; i++) {
                pos = line.indexOf(insertBefore[i]);
                if (pos >= 0)
                    break;
            }
            if (pos >= 0) {
                writer.print(line.substring(0, pos));
                break;
            } else {
                writer.println(line);
            }
        }

        writer.println(insertStartMarker);
        while (true) {
            String line2 = fragmentReader.readLine();
            if (line2 == null) {
                writer.println();
                break;
            }
            writer.println(line2);
        }
        writer.println(insertEndMarker);
        writer.println();

        for (int i = 0; i < pos; i++) {
            writer.print(" ");
        }
        writer.println(line.substring(pos));

        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            writer.println(line);
        }
        writer.close();

        reader.close();
        fragmentReader.close();

        FileInputStream fis = new FileInputStream(webXml2);
        FileOutputStream fos = new FileOutputStream(webXml);

        byte buf[] = new byte[512];
        while (true) {
            int n = fis.read(buf);
            if (n < 0) {
                break;
            }
            fos.write(buf, 0, n);
        }

        fis.close();
        fos.close();

        webXml2.delete();
        (new File(webxmlFile)).delete();

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
    

    /**
     * Locate all jsp files in the webapp. Used if no explicit
     * jsps are specified.
     *
     * @param base base
     * @throws JasperException in case of Jasper errors
     */
    public void scanFiles( File base ) throws JasperException {
        Stack<String> dirs = new Stack<>();
        dirs.push(base.toString());

        // Make sure default extensions are always included
        if ((getExtensions() == null) || (getExtensions().size() < 2)) {
            addExtension("jsp");
            addExtension("jspx");
        }

        while (!dirs.isEmpty()) {
            String s = dirs.pop();
            File f = new File(s);
            if (f.exists() && f.isDirectory()) {
                String[] files = f.list();
                String ext;
                for (int i = 0; (files != null) && i < files.length; i++) {
                    File f2 = new File(s, files[i]);
                    if (f2.isDirectory()) {
                        dirs.push(f2.getPath());
                    } else {
                        String path = f2.getPath();
                        String uri = path.substring(uriRoot.length());
                        ext = files[i].substring(files[i].lastIndexOf('.') +1);
                        if (getExtensions().contains(ext) ||
                            jspConfig.isJspPage(uri)) {
                            pages.add(path);
                        }
                    }
                }
            }
        }
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
        }
    }
    // ==================== protected utility methods ====================

    protected String nextArg() {
        if ((argPos >= args.length)
            || (fullstop = SWITCH_FULL_STOP.equals(args[argPos]))) {
            return null;
        } else {
            return args[argPos++];
        }
    }

    protected String nextFile() {
        if (fullstop) argPos++;
        if (argPos >= args.length) {
            return null;
        } else {
            return args[argPos++];
        }
    }

    protected void initWebXml() {
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
            } else if ((webxmlLevel>= INC_WEBXML) && !addWebXmlMappings) {
                mapout.write(Localizer.getMessage("jspc.webinc.header"));
                mapout.flush();
            }
        } catch (IOException ioe) {
            mapout = null;
            servletout = null;
            mappingout = null;
        }
    }

    protected void completeWebXml() {
        if (mapout != null) {
            try {
                servletout.writeTo(mapout);
                mappingout.writeTo(mapout);
                if (webxmlLevel >= ALL_WEBXML) {
                    mapout.write(Localizer.getMessage("jspc.webxml.footer"));
                } else if ((webxmlLevel >= INC_WEBXML) && !addWebXmlMappings) {
                    mapout.write(Localizer.getMessage("jspc.webinc.footer"));
                }
                mapout.close();
            } catch (IOException ioe) {
                // noting to do if it fails since we are done with it
            }
        }
    }

    protected void initServletContext() {

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
     * Find the WEB-INF dir by looking up in the directory tree.
     * This is used if no explicit docbase is set, but only files.
     * XXX Maybe we should require the docbase.
     *
     * @param f start file for lookup
     */
    protected void locateUriRoot( File f ) {
        String tUriBase = uriBase;
        if (tUriBase == null) {
            tUriBase = "/";
        }
        try {
            if (f.exists()) {
                f = new File(f.getAbsolutePath());
                while (f != null) {
                    File g = new File(f, "WEB-INF");
                    if (g.exists() && g.isDirectory()) {
                        uriRoot = f.getCanonicalPath();
                        uriBase = tUriBase;
                        if (log.isInfoEnabled()) {
                            log.info(Localizer.getMessage(
                                        "jspc.implicit.uriRoot",
                                        uriRoot));
                        }
                        break;
                    }
                    if (f.exists() && f.isDirectory()) {
                        tUriBase = "/" + f.getName() + "/" + tUriBase;
                    }

                    String fParent = f.getParent();
                    if (fParent == null) {
                        break;
                    } else {
                        f = new File(fParent);
                    }

                    // If there is no acceptible candidate, uriRoot will
                    // remain null to indicate to the CompilerContext to
                    // use the current working/user dir.
                }

                if (uriRoot != null) {
                    File froot = new File(uriRoot);
                    uriRoot = froot.getCanonicalPath();
                }
            }
        } catch (IOException ioe) {
            // since this is an optional default and a null value
            // for uriRoot has a non-error meaning, we can just
            // pass straight through
        }
    }
}
