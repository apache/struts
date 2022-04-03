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
package org.apache.struts2.jasper.compiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.struts2.el.ExpressionFactoryImpl;
import org.apache.struts2.jasper.Constants;
import org.apache.struts2.jasper.JasperException;

import javax.el.ExpressionFactory;
import javax.servlet.jsp.tagext.TagLibraryInfo;

/**
 * A repository for various info about the translation unit under compilation.
 *
 * @author Kin-man Chung
 */

class PageInfo {

    private Vector imports;
    private Vector dependants;

    private BeanRepository beanRepository;
    private HashMap taglibsMap;
    private HashMap jspPrefixMapper;
    private HashMap xmlPrefixMapper;
    private HashMap nonCustomTagPrefixMap;
    private String jspFile;
    private String defaultLanguage = "java";
    private String language;
    private String defaultExtends = Constants.JSP_SERVLET_BASE;
    private String xtends;
    private String contentType = null;
    private String session;
    private boolean isSession = true;
    private String bufferValue;
    private int buffer = 8*1024;    // XXX confirm
    private String autoFlush;
    private boolean isAutoFlush = true;
    private String isThreadSafeValue;
    private boolean isThreadSafe = true;
    private String isErrorPageValue;
    private boolean isErrorPage = false;
    private String errorPage = null;
    private String info;

    private boolean scriptless = false;
    private boolean scriptingInvalid = false;
    
    private String isELIgnoredValue;
    private boolean isELIgnored = false;
    
    // JSP 2.1
    private String deferredSyntaxAllowedAsLiteralValue;
    private boolean deferredSyntaxAllowedAsLiteral = false;
    private ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
    private String trimDirectiveWhitespacesValue;
    private boolean trimDirectiveWhitespaces = false;
    
    private String omitXmlDecl = null;
    private String doctypeName = null;
    private String doctypePublic = null;
    private String doctypeSystem = null;

    private boolean isJspPrefixHijacked;

    // Set of all element and attribute prefixes used in this translation unit
    private HashSet prefixes;

    private boolean hasJspRoot = false;
    private Vector includePrelude;
    private Vector includeCoda;
    private Vector pluginDcls;      // Id's for tagplugin declarations


    PageInfo(BeanRepository beanRepository, String jspFile) {

        this.jspFile = jspFile;
        this.beanRepository = beanRepository;
        this.taglibsMap = new HashMap();
        this.jspPrefixMapper = new HashMap();
        this.xmlPrefixMapper = new HashMap();
        this.nonCustomTagPrefixMap = new HashMap();
        this.imports = new Vector();
        this.dependants = new Vector();
        this.includePrelude = new Vector();
        this.includeCoda = new Vector();
        this.pluginDcls = new Vector();
        this.prefixes = new HashSet();

        // Enter standard imports
        for(int i = 0; i < Constants.STANDARD_IMPORTS.length; i++)
            imports.add(Constants.STANDARD_IMPORTS[i]);
    }

    /**
     * Check if the plugin ID has been previously declared.  Make a not
     * that this Id is now declared.
     * @return true if Id has been declared.
     */
    public boolean isPluginDeclared(String id) {
        if (pluginDcls.contains(id))
            return true;
        pluginDcls.add(id);
        return false;
    }

    public void addImports(List imports) {
        this.imports.addAll(imports);
    }

    public void addImport(String imp) {
        this.imports.add(imp);
    }

    public List getImports() {
        return imports;
    }

    public String getJspFile() {
        return jspFile;
    }

    public void addDependant(String d) {
        if (!dependants.contains(d) && !jspFile.equals(d))
                dependants.add(d);
    }

    public List getDependants() {
        return dependants;
    }

    public BeanRepository getBeanRepository() {
        return beanRepository;
    }

    public void setScriptless(boolean s) {
        scriptless = s;
    }

    public boolean isScriptless() {
        return scriptless;
    }

    public void setScriptingInvalid(boolean s) {
        scriptingInvalid = s;
    }

    public boolean isScriptingInvalid() {
        return scriptingInvalid;
    }

    public List getIncludePrelude() {
        return includePrelude;
    }

    public void setIncludePrelude(Vector prelude) {
        includePrelude = prelude;
    }

    public List getIncludeCoda() {
        return includeCoda;
    }

    public void setIncludeCoda(Vector coda) {
        includeCoda = coda;
    }

    public void setHasJspRoot(boolean s) {
        hasJspRoot = s;
    }

    public boolean hasJspRoot() {
        return hasJspRoot;
    }

    public String getOmitXmlDecl() {
        return omitXmlDecl;
    }

    public void setOmitXmlDecl(String omit) {
        omitXmlDecl = omit;
    }

    public String getDoctypeName() {
        return doctypeName;
    }

    public void setDoctypeName(String doctypeName) {
        this.doctypeName = doctypeName;
    }

    public String getDoctypeSystem() {
        return doctypeSystem;
    }

    public void setDoctypeSystem(String doctypeSystem) {
        this.doctypeSystem = doctypeSystem;
    }

    public String getDoctypePublic() {
        return doctypePublic;
    }

    public void setDoctypePublic(String doctypePublic) {
        this.doctypePublic = doctypePublic;
    }

    /* Tag library and XML namespace management methods */

    public void setIsJspPrefixHijacked(boolean isHijacked) {
        isJspPrefixHijacked = isHijacked;
    }

    public boolean isJspPrefixHijacked() {
        return isJspPrefixHijacked;
    }

    /*
     * Adds the given prefix to the set of prefixes of this translation unit.
     *
     * @param prefix The prefix to add
     */
    public void addPrefix(String prefix) {
        prefixes.add(prefix);
    }

    /*
     * Checks to see if this translation unit contains the given prefix.
     *
     * @param prefix The prefix to check
     *
     * @return true if this translation unit contains the given prefix, false
     * otherwise
     */
    public boolean containsPrefix(String prefix) {
        return prefixes.contains(prefix);
    }

    /*
     * Maps the given URI to the given tag library.
     *
     * @param uri The URI to map
     * @param info The tag library to be associated with the given URI
     */
    public void addTaglib(String uri, TagLibraryInfo info) {
        taglibsMap.put(uri, info);
    }

    /*
     * Gets the tag library corresponding to the given URI.
     *
     * @return Tag library corresponding to the given URI
     */
    public TagLibraryInfo getTaglib(String uri) {
        return (TagLibraryInfo) taglibsMap.get(uri);
    }

    /*
     * Gets the collection of tag libraries that are associated with a URI
     *
     * @return Collection of tag libraries that are associated with a URI
     */
    public Collection getTaglibs() {
        return taglibsMap.values();
    }

    /*
     * Checks to see if the given URI is mapped to a tag library.
     *
     * @param uri The URI to map
     *
     * @return true if the given URI is mapped to a tag library, false
     * otherwise
     */
    public boolean hasTaglib(String uri) {
        return taglibsMap.containsKey(uri);
    }

    /*
     * Maps the given prefix to the given URI.
     *
     * @param prefix The prefix to map
     * @param uri The URI to be associated with the given prefix
     */
    public void addPrefixMapping(String prefix, String uri) {
        jspPrefixMapper.put(prefix, uri);
    }

    /*
     * Pushes the given URI onto the stack of URIs to which the given prefix
     * is mapped.
     *
     * @param prefix The prefix whose stack of URIs is to be pushed
     * @param uri The URI to be pushed onto the stack
     */
    public void pushPrefixMapping(String prefix, String uri) {
        LinkedList stack = (LinkedList) xmlPrefixMapper.get(prefix);
        if (stack == null) {
            stack = new LinkedList();
            xmlPrefixMapper.put(prefix, stack);
        }
        stack.addFirst(uri);
    }

    /*
     * Removes the URI at the top of the stack of URIs to which the given
     * prefix is mapped.
     *
     * @param prefix The prefix whose stack of URIs is to be popped
     */
    public void popPrefixMapping(String prefix) {
        LinkedList stack = (LinkedList) xmlPrefixMapper.get(prefix);
        if (stack == null || stack.size() == 0) {
            // XXX throw new Exception("XXX");
        }
        stack.removeFirst();
    }

    /*
     * Returns the URI to which the given prefix maps.
     *
     * @param prefix The prefix whose URI is sought
     *
     * @return The URI to which the given prefix maps
     */
    public String getURI(String prefix) {

        String uri = null;

        LinkedList stack = (LinkedList) xmlPrefixMapper.get(prefix);
        if (stack == null || stack.size() == 0) {
            uri = (String) jspPrefixMapper.get(prefix);
        } else {
            uri = (String) stack.getFirst();
        }

        return uri;
    }


    /* Page/Tag directive attributes */

    /*
     * language
     */
    public void setLanguage(String value, Node n, ErrorDispatcher err,
                boolean pagedir)
        throws JasperException {

        if (!"java".equalsIgnoreCase(value)) {
            if (pagedir)
                err.jspError(n, "jsp.error.page.language.nonjava");
            else
                err.jspError(n, "jsp.error.tag.language.nonjava");
        }

        language = value;
    }

    public String getLanguage(boolean useDefault) {
        return (language == null && useDefault ? defaultLanguage : language);
    }

    public String getLanguage() {
        return getLanguage(true);
    }


    /*
     * extends
     */
    public void setExtends(String value, Node.PageDirective n) {

        xtends = value;

        /*
         * If page superclass is top level class (i.e. not in a package)
         * explicitly import it. If this is not done, the compiler will assume
         * the extended class is in the same pkg as the generated servlet.
         */
        if (value.indexOf('.') < 0)
            n.addImport(value);
    }

    /**
     * Gets the value of the 'extends' page directive attribute.
     *
     * @param useDefault TRUE if the default
     * (org.apache.struts2.jasper.runtime.HttpJspBase) should be returned if this
     * attribute has not been set, FALSE otherwise
     *
     * @return The value of the 'extends' page directive attribute, or the
     * default (org.apache.struts2.jasper.runtime.HttpJspBase) if this attribute has
     * not been set and useDefault is TRUE
     */
    public String getExtends(boolean useDefault) {
        return (xtends == null && useDefault ? defaultExtends : xtends);
    }

    /**
     * Gets the value of the 'extends' page directive attribute.
     *
     * @return The value of the 'extends' page directive attribute, or the
     * default (org.apache.struts2.jasper.runtime.HttpJspBase) if this attribute has
     * not been set
     */
    public String getExtends() {
        return getExtends(true);
    }


    /*
     * contentType
     */
    public void setContentType(String value) {
        contentType = value;
    }

    public String getContentType() {
        return contentType;
    }


    /*
     * buffer
     */
    public void setBufferValue(String value, Node n, ErrorDispatcher err)
        throws JasperException {

        if ("none".equalsIgnoreCase(value))
            buffer = 0;
        else {
            if (value == null || !value.endsWith("kb"))
                err.jspError(n, "jsp.error.page.invalid.buffer");
            try {
                Integer k = new Integer(value.substring(0, value.length()-2));
                buffer = k.intValue() * 1024;
            } catch (NumberFormatException e) {
                err.jspError(n, "jsp.error.page.invalid.buffer");
            }
        }

        bufferValue = value;
    }

    public String getBufferValue() {
        return bufferValue;
    }

    public int getBuffer() {
        return buffer;
    }


    /*
     * session
     */
    public void setSession(String value, Node n, ErrorDispatcher err)
        throws JasperException {

        if ("true".equalsIgnoreCase(value))
            isSession = true;
        else if ("false".equalsIgnoreCase(value))
            isSession = false;
        else
            err.jspError(n, "jsp.error.page.invalid.session");

        session = value;
    }

    public String getSession() {
        return session;
    }

    public boolean isSession() {
        return isSession;
    }


    /*
     * autoFlush
     */
    public void setAutoFlush(String value, Node n, ErrorDispatcher err)
        throws JasperException {

        if ("true".equalsIgnoreCase(value))
            isAutoFlush = true;
        else if ("false".equalsIgnoreCase(value))
            isAutoFlush = false;
        else
            err.jspError(n, "jsp.error.autoFlush.invalid");

        autoFlush = value;
    }

    public String getAutoFlush() {
        return autoFlush;
    }

    public boolean isAutoFlush() {
        return isAutoFlush;
    }


    /*
     * isThreadSafe
     */
    public void setIsThreadSafe(String value, Node n, ErrorDispatcher err)
        throws JasperException {

        if ("true".equalsIgnoreCase(value))
            isThreadSafe = true;
        else if ("false".equalsIgnoreCase(value))
            isThreadSafe = false;
        else
            err.jspError(n, "jsp.error.page.invalid.isthreadsafe");

        isThreadSafeValue = value;
    }

    public String getIsThreadSafe() {
        return isThreadSafeValue;
    }

    public boolean isThreadSafe() {
        return isThreadSafe;
    }


    /*
     * info
     */
    public void setInfo(String value) {
        info = value;
    }

    public String getInfo() {
        return info;
    }


    /*
     * errorPage
     */
    public void setErrorPage(String value) {
        errorPage = value;
    }

    public String getErrorPage() {
        return errorPage;
    }


    /*
     * isErrorPage
     */
    public void setIsErrorPage(String value, Node n, ErrorDispatcher err)
        throws JasperException {

        if ("true".equalsIgnoreCase(value))
            isErrorPage = true;
        else if ("false".equalsIgnoreCase(value))
            isErrorPage = false;
        else
            err.jspError(n, "jsp.error.page.invalid.iserrorpage");

        isErrorPageValue = value;
    }

    public String getIsErrorPage() {
        return isErrorPageValue;
    }

    public boolean isErrorPage() {
        return isErrorPage;
    }


    /*
     * isELIgnored
     */
    public void setIsELIgnored(String value, Node n, ErrorDispatcher err,
                   boolean pagedir)
        throws JasperException {

        if ("true".equalsIgnoreCase(value))
            isELIgnored = true;
        else if ("false".equalsIgnoreCase(value))
            isELIgnored = false;
        else {
            if (pagedir)
                err.jspError(n, "jsp.error.page.invalid.iselignored");
            else
                err.jspError(n, "jsp.error.tag.invalid.iselignored");
        }

        isELIgnoredValue = value;
    }
    
    /*
     * deferredSyntaxAllowedAsLiteral
     */
    public void setDeferredSyntaxAllowedAsLiteral(String value, Node n, ErrorDispatcher err,
                   boolean pagedir)
        throws JasperException {

        if ("true".equalsIgnoreCase(value))
            deferredSyntaxAllowedAsLiteral = true;
        else if ("false".equalsIgnoreCase(value))
            deferredSyntaxAllowedAsLiteral = false;
        else {
            if (pagedir)
                err.jspError(n, "jsp.error.page.invalid.deferredsyntaxallowedasliteral");
            else
                err.jspError(n, "jsp.error.tag.invalid.deferredsyntaxallowedasliteral");
        }

        deferredSyntaxAllowedAsLiteralValue = value;
    }
    
    /*
     * trimDirectiveWhitespaces
     */
    public void setTrimDirectiveWhitespaces(String value, Node n, ErrorDispatcher err,
                   boolean pagedir)
        throws JasperException {

        if ("true".equalsIgnoreCase(value))
            trimDirectiveWhitespaces = true;
        else if ("false".equalsIgnoreCase(value))
            trimDirectiveWhitespaces = false;
        else {
            if (pagedir)
                err.jspError(n, "jsp.error.page.invalid.trimdirectivewhitespaces");
            else
                err.jspError(n, "jsp.error.tag.invalid.trimdirectivewhitespaces");
        }

        trimDirectiveWhitespacesValue = value;
    }

    public void setELIgnored(boolean s) {
        isELIgnored = s;
    }

    public String getIsELIgnored() {
        return isELIgnoredValue;
    }

    public boolean isELIgnored() {
        return isELIgnored;
    }

    public void putNonCustomTagPrefix(String prefix, Mark where) {
        nonCustomTagPrefixMap.put(prefix, where);
    }

    public Mark getNonCustomTagPrefix(String prefix) {
        return (Mark) nonCustomTagPrefixMap.get(prefix);
    }
    
    public String getDeferredSyntaxAllowedAsLiteral() {
        return deferredSyntaxAllowedAsLiteralValue;
    }

    public boolean isDeferredSyntaxAllowedAsLiteral() {
        return deferredSyntaxAllowedAsLiteral;
    }

    public void setDeferredSyntaxAllowedAsLiteral(boolean isELDeferred) {
        this.deferredSyntaxAllowedAsLiteral = isELDeferred;
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    public String getTrimDirectiveWhitespaces() {
        return trimDirectiveWhitespacesValue;
    }

    public boolean isTrimDirectiveWhitespaces() {
        return trimDirectiveWhitespaces;
    }

    public void setTrimDirectiveWhitespaces(boolean trimDirectiveWhitespaces) {
        this.trimDirectiveWhitespaces = trimDirectiveWhitespaces;
    }
}
