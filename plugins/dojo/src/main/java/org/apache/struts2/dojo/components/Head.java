/*
 * $Id$
 *
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
package org.apache.struts2.dojo.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Renders parts of the HEAD section for an HTML file. This is useful as some themes require certain CSS and JavaScript
 * includes.<p/>
 *
 * If, for example, your page has ajax components integrated, without having the default theme set to ajax, you might
 * want to use the head tag with <b>theme="ajax"</b> so that the typical ajax header setup will be included in the
 * page.<p/>
 *
 * If you use the ajax theme you can turn a debug flag on by setting the debug parameter to <tt>true</tt>.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;head&gt;
 *   &lt;title&gt;My page&lt;/title&gt;
 *   &lt;s:head/&gt;
 * &lt;/head&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;head&gt;
 *   &lt;title&gt;My page&lt;/title&gt;
 *   &lt;s:head debug="true"/&gt;
 * &lt;/head&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 *
 */
@StrutsTag(name="head", tldBodyContent="empty", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.HeadTag",
    description="Render a chunk of HEAD for your HTML file")
@StrutsTagSkipInheritance
public class Head extends org.apache.struts2.components.Head {
    public static final String TEMPLATE = "head";

    private String debug;
    private String compressed;
    private String baseRelativePath;
    private String extraLocales;
    private String locale;
    
    public Head(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        super.evaluateParams();
        
        if (this.debug != null)
            addParameter("debug", findValue(this.debug, Boolean.class));
        if (this.compressed != null)
            addParameter("compressed", findValue(this.compressed, Boolean.class));
        if (this.baseRelativePath != null)
            addParameter("baseRelativePath", findString(this.baseRelativePath));
        if (this.extraLocales != null) {
            String locales = findString(this.extraLocales);
            addParameter("extraLocales", locales.split(","));
        }
        if (this.locale != null)
            addParameter("locale", findString(this.locale));
    }

    @Override
    @StrutsTagSkipInheritance
    public void setTheme(String theme) {
        super.setTheme(theme);
    }
    
    @Override
    public String getTheme() {
        return "ajax";
    }
    
    public boolean isDebug() {
        return debug != null && Boolean.parseBoolean(debug);
    }

    @StrutsTagAttribute(description="Set to true to enable Dojo debug messages", defaultValue="false")
    public void setDebug(String debug) {
        this.debug = debug;
    }

    @StrutsTagAttribute(description="Use compressed version of dojo.js", defaultValue="true")
    public void setCompressed(String compressed) {
        this.compressed = compressed;
    }

    @StrutsTagAttribute(description="Context relative path of Dojo distribution folder", defaultValue="/struts/dojo")
    public void setBaseRelativePath(String baseRelativePath) {
        this.baseRelativePath = baseRelativePath;
    }

    @StrutsTagAttribute(description="Comma separated list of locale names to be loaded by Dojo, locale names must be specified as in RFC3066")
    public void setExtraLocales(String extraLocales) {
        this.extraLocales = extraLocales;
    }

    @StrutsTagAttribute(description="Default locale to be used by Dojo, locale name must be specified as in RFC3066")
    public void setLocale(String locale) {
        this.locale = locale;
    }
}
