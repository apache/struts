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

package org.apache.struts2.jasper.compiler;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

import org.apache.struts2.jasper.JasperException;
import org.apache.struts2.jasper.JspCompilationContext;
import org.apache.struts2.jasper.Options;
import org.apache.struts2.jasper.servlet.JspServletWrapper;

/**
 * Main JSP compiler class. This class uses Ant for compiling.
 * 
 * @author Anil K. Vijendran
 * @author Mandar Raje
 * @author Pierre Delisle
 * @author Kin-man Chung
 * @author Remy Maucherat
 * @author Mark Roth
 */
public abstract class Compiler {
    
    protected org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory
            .getLog(Compiler.class);

    // ----------------------------------------------------- Instance Variables

    protected JspCompilationContext ctxt;

    protected ErrorDispatcher errDispatcher;

    protected PageInfo pageInfo;

    protected JspServletWrapper jsw;

    protected TagFileProcessor tfp;

    protected Options options;

    protected Node.Nodes pageNodes;

    // ------------------------------------------------------------ Constructor

    public void init(JspCompilationContext ctxt, JspServletWrapper jsw) {
        this.jsw = jsw;
        this.ctxt = ctxt;
        this.options = ctxt.getOptions();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * <p>
     * Retrieves the parsed nodes of the JSP page, if they are available. May
     * return null. Used in development mode for generating detailed error
     * messages. http://issues.apache.org/bugzilla/show_bug.cgi?id=37062.
     * </p>
     *
     * @return page nodes
     */
    public Node.Nodes getPageNodes() {
        return this.pageNodes;
    }

    /**
     * Compile the jsp file into equivalent servlet in .java file
     * 
     * @return a smap for the current JSP page, if one is generated, null
     *         otherwise
     * @throws Exception in case of any errors
     */
    protected String[] generateJava() throws Exception {

        String[] smapStr = null;

        long t1, t2, t3, t4;

        t1 = t2 = t3 = t4 = 0;

        if (log.isDebugEnabled()) {
            t1 = System.currentTimeMillis();
        }

        // Setup page info area
        pageInfo = new PageInfo(new BeanRepository(ctxt.getClassLoader(),
                errDispatcher), ctxt.getJspFile());

        JspConfig jspConfig = options.getJspConfig();
        JspConfig.JspProperty jspProperty = jspConfig.findJspProperty(ctxt
                .getJspFile());

        /*
         * If the current uri is matched by a pattern specified in a
         * jsp-property-group in web.xml, initialize pageInfo with those
         * properties.
         */
        if (jspProperty.isELIgnored() != null) {
            pageInfo.setELIgnored(JspUtil.booleanValue(jspProperty
                    .isELIgnored()));
        }
        if (jspProperty.isScriptingInvalid() != null) {
            pageInfo.setScriptingInvalid(JspUtil.booleanValue(jspProperty
                    .isScriptingInvalid()));
        }
        if (jspProperty.getIncludePrelude() != null) {
            pageInfo.setIncludePrelude(jspProperty.getIncludePrelude());
        }
        if (jspProperty.getIncludeCoda() != null) {
            pageInfo.setIncludeCoda(jspProperty.getIncludeCoda());
        }
        if (jspProperty.isDeferedSyntaxAllowedAsLiteral() != null) {
            pageInfo.setDeferredSyntaxAllowedAsLiteral(JspUtil.booleanValue(jspProperty
                    .isDeferedSyntaxAllowedAsLiteral()));
        }
        if (jspProperty.isTrimDirectiveWhitespaces() != null) {
            pageInfo.setTrimDirectiveWhitespaces(JspUtil.booleanValue(jspProperty
                    .isTrimDirectiveWhitespaces()));
        }

        ctxt.checkOutputDir();
        String javaFileName = ctxt.getServletJavaFileName();

        ServletWriter writer = null;
        try {
            /*
             * The setting of isELIgnored changes the behaviour of the parser
             * in subtle ways. To add to the 'fun', isELIgnored can be set in
             * any file that forms part of the translation unit so setting it
             * in a file included towards the end of the translation unit can
             * change how the parser should have behaved when parsing content
             * up to the point where isELIgnored was set. Arghh!
             * Previous attempts to hack around this have only provided partial
             * solutions. We now use two passes to parse the translation unit.
             * The first just parses the directives and the second parses the
             * whole translation unit once we know how isELIgnored has been set.
             * TODO There are some possible optimisations of this process.  
             */ 
            // Parse the file
            ParserController parserCtl = new ParserController(ctxt, this);

            // Pass 1 - the directives
            Node.Nodes directives =
                parserCtl.parseDirectives(ctxt.getJspFile());
            Validator.validateDirectives(this, directives);

            // Pass 2 - the whole translation unit
            pageNodes = parserCtl.parse(ctxt.getJspFile());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);

            if (ctxt.isPrototypeMode()) {
                // generate prototype .java file for the tag file
                writer = setupContextWriter(byteArrayOutputStream);
                Generator.generate(writer, this, pageNodes);
                writer.close();
                writer = null;
                return null;
            }

            // Validate and process attributes - don't re-validate the
            // directives we validated in pass 1
            Validator.validateExDirectives(this, pageNodes);

            if (log.isDebugEnabled()) {
                t2 = System.currentTimeMillis();
            }

            // Collect page info
            Collector.collect(this, pageNodes);

            // Compile (if necessary) and load the tag files referenced in
            // this compilation unit.
            tfp = new TagFileProcessor();
            tfp.loadTagFiles(this, pageNodes);

            if (log.isDebugEnabled()) {
                t3 = System.currentTimeMillis();
            }

            // Determine which custom tag needs to declare which scripting vars
            ScriptingVariabler.set(pageNodes, errDispatcher);

            // Optimizations by Tag Plugins
            TagPluginManager tagPluginManager = options.getTagPluginManager();
            tagPluginManager.apply(pageNodes, errDispatcher, pageInfo);

            // Optimization: concatenate contiguous template texts.
            TextOptimizer.concatenate(this, pageNodes);

            // Generate static function mapper codes.
            ELFunctionMapper.map(this, pageNodes);

            // generate servlet .java file
            writer = setupContextWriter(byteArrayOutputStream);
            Generator.generate(writer, this, pageNodes);
            writer.close();
            writer = null;

            // The writer is only used during the compile, dereference
            // it in the JspCompilationContext when done to allow it
            // to be GC'd and save memory.
            ctxt.setWriter(null);
            ctxt.setSourceCode(byteArrayOutputStream.toString());

            if (log.isDebugEnabled()) {
                t4 = System.currentTimeMillis();
                log.debug("Generated " + javaFileName + " total=" + (t4 - t1)
                        + " generate=" + (t4 - t3) + " validate=" + (t2 - t1));
            }

        } catch (Exception e) {
            if (writer != null) {
                try {
                    writer.close();
                    writer = null;
                } catch (Exception e1) {
                    // do nothing
                }
            }
            // Remove the generated .java file
            new File(javaFileName).delete();
            throw e;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e2) {
                    // do nothing
                }
            }
        }

        // JSR45 Support
        if (!options.isSmapSuppressed()) {
            smapStr = SmapUtil.generateSmap(ctxt, pageNodes);
        }

        // If any proto type .java and .class files was generated,
        // the prototype .java may have been replaced by the current
        // compilation (if the tag file is self referencing), but the
        // .class file need to be removed, to make sure that javac would
        // generate .class again from the new .java file just generated.
        tfp.removeProtoTypeFiles(ctxt.getClassFileName());

        return smapStr;
    }

	private ServletWriter setupContextWriter(OutputStream os)
			throws FileNotFoundException, JasperException {
		ServletWriter writer;
		// Setup the ServletWriter
		String javaEncoding = ctxt.getOptions().getJavaEncoding();
		OutputStreamWriter osw = null;

		try {
		    osw = new OutputStreamWriter(os, javaEncoding);
		} catch (UnsupportedEncodingException ex) {
		    errDispatcher.jspError("jsp.error.needAlternateJavaEncoding",
		            javaEncoding);
		}

		writer = new ServletWriter(new PrintWriter(osw));
		ctxt.setWriter(writer);
		return writer;
	}

    /**
     * Compile the servlet from .java file to .class file
     *
     * @param smap string array
     * @throws FileNotFoundException is file was not found
     * @throws JasperException in case of jasper errors
     * @throws Exception in case of other errors
     */
    protected abstract void generateClass(String[] smap)
            throws FileNotFoundException, JasperException, Exception;

    /**
     * Compile the jsp file from the current engine context
     * @throws FileNotFoundException is file was not found
     * @throws JasperException in case of jasper errors
     * @throws Exception in case of other errors
     */
    public void compile() throws FileNotFoundException, JasperException,
            Exception {
        compile(true);
    }

    /**
     * Compile the jsp file from the current engine context. As an side- effect,
     * tag files that are referenced by this page are also compiled.
     * 
     * @param compileClass
     *            If true, generate both .java and .class file If false,
     *            generate only .java file
     * @throws FileNotFoundException is file was not found
     * @throws JasperException in case of jasper errors
     * @throws Exception in case of other errors
     */
    public void compile(boolean compileClass) throws FileNotFoundException,
            JasperException, Exception {
        compile(compileClass, false);
    }

    /**
     * Compile the jsp file from the current engine context. As an side- effect,
     * tag files that are referenced by this page are also compiled.
     * 
     * @param compileClass
     *            If true, generate both .java and .class file If false,
     *            generate only .java file
     * @param jspcMode
     *            true if invoked from JspC, false otherwise
     * @throws FileNotFoundException is file was not found
     * @throws JasperException in case of jasper errors
     * @throws Exception in case of other errors
     */
    public void compile(boolean compileClass, boolean jspcMode)
            throws FileNotFoundException, JasperException, Exception {
        if (errDispatcher == null) {
            this.errDispatcher = new ErrorDispatcher(jspcMode);
        }

        try {
            String[] smap = generateJava();
            if (compileClass) {
                generateClass(smap);
                // Fix for bugzilla 41606
                // Set JspServletWrapper.servletClassLastModifiedTime after successful compile
                String targetFileName = ctxt.getClassFileName();
                if (targetFileName != null) {
                    File targetFile = new File(targetFileName);
                    if (targetFile.exists() && jsw != null) {
                        jsw.setServletClassLastModifiedTime(targetFile.lastModified());
                    }
                }
            }
        } finally {
            if (tfp != null && ctxt.isPrototypeMode()) {
                tfp.removeProtoTypeFiles(null);
            }
            // Make sure these object which are only used during the
            // generation and compilation of the JSP page get
            // dereferenced so that they can be GC'd and reduce the
            // memory footprint.
            tfp = null;
            errDispatcher = null;
            pageInfo = null;

            // Only get rid of the pageNodes if in production.
            // In development mode, they are used for detailed
            // error messages.
            // http://issues.apache.org/bugzilla/show_bug.cgi?id=37062
            if (!this.options.getDevelopment()) {
                pageNodes = null;
            }

            if (ctxt.getWriter() != null) {
                ctxt.getWriter().close();
                ctxt.setWriter(null);
            }
        }
    }

    /**
     * This is a protected method intended to be overridden by subclasses of
     * Compiler. This is used by the compile method to do all the compilation.
     *
     * @return true if out dated
     */
    public boolean isOutDated() {
        return isOutDated(true);
    }

    /**
     * Determine if a compilation is necessary by checking the time stamp of the
     * JSP page with that of the corresponding .class or .java file. If the page
     * has dependencies, the check is also extended to its dependants, and so
     * on. This method can by overridden by a subclasses of Compiler.
     * 
     * @param checkClass
     *            If true, check against .class file, if false, check against
     *            .java file.
     *
     * @return true if out dated
     */
    public boolean isOutDated(boolean checkClass) {

        String jsp = ctxt.getJspFile();

        if (jsw != null
                && (ctxt.getOptions().getModificationTestInterval() > 0)) {

            if (jsw.getLastModificationTest()
                    + (ctxt.getOptions().getModificationTestInterval() * 1000) > System
                    .currentTimeMillis()) {
                return false;
            } else {
                jsw.setLastModificationTest(System.currentTimeMillis());
            }
        }

        long jspRealLastModified = 0;
        try {
            URL jspUrl = ctxt.getResource(jsp);
            if (jspUrl == null) {
                ctxt.incrementRemoved();
                return false;
            }
            URLConnection uc = jspUrl.openConnection();
            if (uc instanceof JarURLConnection) {
                jspRealLastModified =
                    ((JarURLConnection) uc).getJarEntry().getTime();
            } else {
                jspRealLastModified = uc.getLastModified();
            }
            uc.getInputStream().close();
        } catch (Exception e) {
            return true;
        }

        long targetLastModified = 0;
        File targetFile;

        if (checkClass) {
            targetFile = new File(ctxt.getClassFileName());
        } else {
            targetFile = new File(ctxt.getServletJavaFileName());
        }

        if (!targetFile.exists()) {
            return true;
        }

        targetLastModified = targetFile.lastModified();
        if (checkClass && jsw != null) {
            jsw.setServletClassLastModifiedTime(targetLastModified);
        }
        if (targetLastModified < jspRealLastModified) {
            if (log.isDebugEnabled()) {
                log.debug("Compiler: outdated: " + targetFile + " "
                        + targetLastModified);
            }
            return true;
        }

        // determine if source dependent files (e.g. includes using include
        // directives) have been changed.
        if (jsw == null) {
            return false;
        }

        List depends = jsw.getDependants();
        if (depends == null) {
            return false;
        }

        for (Object depend : depends) {
            String include = (String) depend;
            try {
                URL includeUrl = ctxt.getResource(include);
                if (includeUrl == null) {
                    return true;
                }

                URLConnection iuc = includeUrl.openConnection();
                long includeLastModified = 0;
                if (iuc instanceof JarURLConnection) {
                    includeLastModified =
                            ((JarURLConnection) iuc).getJarEntry().getTime();
                } else {
                    includeLastModified = iuc.getLastModified();
                }
                iuc.getInputStream().close();

                if (includeLastModified > targetLastModified) {
                    return true;
                }
            } catch (Exception e) {
                return true;
            }
        }

        return false;

    }

    /**
     * @return  the error dispatcher.
     */
    public ErrorDispatcher getErrorDispatcher() {
        return errDispatcher;
    }

    /**
     * @return  the info about the page under compilation
     */
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public JspCompilationContext getCompilationContext() {
        return ctxt;
    }

    /**
     * Remove generated files
     */
    public void removeGeneratedFiles() {
        try {
            String classFileName = ctxt.getClassFileName();
            if (classFileName != null) {
                File classFile = new File(classFileName);
                if (log.isDebugEnabled())
                    log.debug("Deleting " + classFile);
                classFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
        try {
            String javaFileName = ctxt.getServletJavaFileName();
            if (javaFileName != null) {
                File javaFile = new File(javaFileName);
                if (log.isDebugEnabled())
                    log.debug("Deleting " + javaFile);
                javaFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
    }

    public void removeGeneratedClassFiles() {
        try {
            String classFileName = ctxt.getClassFileName();
            if (classFileName != null) {
                File classFile = new File(classFileName);
                if (log.isDebugEnabled())
                    log.debug("Deleting " + classFile);
                classFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
    }
}
