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
package org.apache.struts2.jasper;

import java.io.File;
import java.util.Map;

import org.apache.struts2.jasper.compiler.JspConfig;
import org.apache.struts2.jasper.compiler.TagPluginManager;
import org.apache.struts2.jasper.compiler.TldLocationsCache;

/**
 * A class to hold all init parameters specific to the JSP engine. 
 *
 * @author Anil K. Vijendran
 * @author Hans Bergsten
 * @author Pierre Delisle
 */
public interface Options {

    /**
     * @return  true if Jasper issues a compilation error instead of a runtime
     * Instantiation error if the class attribute specified in useBean action
     * is invalid.
     */
    public boolean getErrorOnUseBeanInvalidClassAttribute();

    /**
     *  @return Are we keeping generated code around?
     */
    public boolean getKeepGenerated();

    /**
     *  @return  true if tag handler pooling is enabled, false otherwise.
     */
    public boolean isPoolingEnabled();

    /**
     *  @return  Are we supporting HTML mapped servlets?
     */
    public boolean getMappedFile();

    /**
     *  @return  Should we include debug information in compiled class?
     */
    public boolean getClassDebugInfo();

    /**
     * @return Background compile thread check interval in seconds
     */
    public int getCheckInterval();

    /**
     *  @return Is Jasper being used in development mode?
     */
    public boolean getDevelopment();

    /**
     *  @return Should we include a source fragment in exception messages, which could be displayed
     * to the developer ?
     */
    public boolean getDisplaySourceFragment();

    /**
     *  @return Is the generation of SMAP info for JSR45 debugging suppressed?
     */
    public boolean isSmapSuppressed();

    /**
     *  @return Indicates whether SMAP info for JSR45 debugging should be dumped to a
     * file.
     * Ignored is suppressSmap() is true
     */
    public boolean isSmapDumped();

    /**
     *  @return Should white spaces between directives or actions be trimmed?
     */
    public boolean getTrimSpaces();

    /**
     *  @return Class ID for use in the plugin tag when the browser is IE.
     */
    public String getIeClassId();

    /**
     *  @return What is my scratch dir?
     */
    public File getScratchDir();

    /**
     *  @return What classpath should I use while compiling the servlets
     * generated from JSP files?
     */
    public String getClassPath();

    /**
     *  @return Compiler to use.
     */
    public String getCompiler();

    /**
     *  @return The compiler target VM, e.g. 1.1, 1.2, 1.3, 1.4, or 1.5.
     */
    public String getCompilerTargetVM();

    /**
     * @return  Compiler source VM, e.g. 1.3, 1.4, or 1.5.
     */
    public String getCompilerSourceVM();   

    /**
     *  @return Java compiler class to use.
     */
    public String getCompilerClassName();   

    /**
     * The cache for the location of the TLD's
     * for the various tag libraries 'exposed'
     * by the web application.
     * A tag library is 'exposed' either explicitely in 
     * web.xml or implicitely via the uri tag in the TLD 
     * of a taglib deployed in a jar file (WEB-INF/lib).
     *
     * @return the instance of the TldLocationsCache
     * for the web-application.
     */
    public TldLocationsCache getTldLocationsCache();

    /**
     *  @return Java platform encoding to generate the JSP
     * page servlet.
     */
    public String getJavaEncoding();

    /**
     *  @return boolean flag to tell Ant whether to fork JSP page compilations.
     */
    public boolean getFork();

    /**
     *  @return Obtain JSP configuration information specified in web.xml.
     */
    public JspConfig getJspConfig();

    /**
     *  @return Is generation of X-Powered-By response header enabled/disabled?
     */
    public boolean isXpoweredBy();

    /**
     *  @return Obtain a Tag Plugin Manager
     */
    public TagPluginManager getTagPluginManager();

    /**
     *  @return Are Text strings to be generated as char arrays?
     */
    public boolean genStringAsCharArray();
    
    /**
     *  @return  Modification test interval.
     */
    public int getModificationTestInterval();
    
    /**
     *  @return Is caching enabled (used for precompilation).
     */
    public boolean isCaching();
    
    /**
     * The web-application wide cache for the returned TreeNode
     * by parseXMLDocument in TagLibraryInfoImpl.parseTLD,
     * if isCaching returns true.
     * 
     * @return the Map(String uri, TreeNode tld) instance.
     */
    public Map getCache();
    
}
