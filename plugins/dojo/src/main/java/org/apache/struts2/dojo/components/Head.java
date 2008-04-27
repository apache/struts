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
 * <!-- START SNIPPET: notice -->
 * The "head" tag renders required JavaScript code to configure Dojo and is required in order to use
 * any of the tags included in the Dojo plugin.</p>
 * <!-- END SNIPPET: notice -->
 * 
 * <!-- START SNIPPET: javadoc -->
 * <p></p>
 * 
 * <p>To debug javascript errors set the "debug" attribute to true, which will display Dojo 
 * (and Struts) warning and error messages at the bottom of the page. Core Dojo files are by default
 * compressed, to improve loading time, which makes them very hard to read. To debug Dojo and Struts
 * widgets, set the "compressed" attribute to true. Make sure to turn this option off before
 * moving your project into production, as uncompressed files will take longer to download.
 * </p>
 * <p>For troubleshooting javascript problems the following configuration is recommended:</p>
 * <pre>
 *   &lt;sx:head debug="true" cache="false" compressed="false" /&gt;
 * </pre>
 *
 * <p>Dojo files are loaded as required by the Dojo loading mechanism. The problem with this
 * approach is that the files are not cached by the browser, so reloading a page or navigating
 * to a different page that uses the same widgets will cause the files to be reloaded. To solve 
 * this problem a custom Dojo profile is distributed with the Dojo plugin. This profile contains
 * the files required by the tags in the Dojo plugin, all in one file (524Kb), which is cached 
 * by the browser. This file will take longer to load by the browser but it will be downloaded 
 * only once. By default the "cache" attribute is set to false.</p>
 * 
 * <p>Some tags like the "datetimepicker" can use different locales, to use a locale
 * that is different from the request locale, it must be specified on the "extraLocales" 
 * attribute. This attribute can contain a comma separated list of locale names. From
 * Dojo's documentation:</p>
 * 
 * <p>
 * The locale is a short string, defined by the host environment, which conforms to RFC 3066 
 * (http://www.ietf.org/rfc/rfc3066.txt) used in the HTML specification. 
 * It consists of short identifiers, typically two characters 
 * long which are case-insensitive. Note that Dojo uses dash separators, not underscores like 
 * Java (e.g. "en-us", not "en_US"). Typically country codes are used in the optional second 
 * identifier, and additional variants may be specified. For example, Japanese is "ja"; 
 * Japanese in Japan is "ja-jp". Notice that the lower case is intentional -- while Dojo 
 * will often convert all locales to lowercase to normalize them, it is the lowercase that 
 * must be used when defining your resources.
 * </p>
 * 
 * <p>The "locale" attribute configures Dojo's locale:</p>
 * 
 * <p>"The locale Dojo uses on a page may be overridden by setting djConfig.locale. This may be 
 * done to accomodate applications with a known user profile or server pages which do manual
 * assembly and assume a certain locale. You may also set djConfig.extraLocale to load 
 * localizations in addition to your own, in case you want to specify a particular 
 * translation or have multiple languages appear on your page."</p>
 * 
 * <p>To improve loading time, the property "parseContent" is set to false by default. This property will
 * instruct Dojo to only build widgets using specific element ids. If the property is set to true
 * Dojo will scan the whole document looking for widgets.</p>
 * 
 * <p>Dojo 0.4.3 is distributed with the Dojo plugin, to use a different Dojo version, the 
 * "baseRelativePath" attribute can be set to the URL of the Dojo root folder on your application.
 * </p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;%@ taglib prefix="sx" uri="/struts-dojo-tags" %&gt;
 * &lt;head&gt;
 *   &lt;title&gt;My page&lt;/title&gt;
 *   &lt;sx:head/&gt;
 * &lt;/head&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;%@ taglib prefix="sx" uri="/struts-dojo-tags" %&gt;
 * &lt;head&gt;
 *   &lt;title&gt;My page&lt;/title&gt;
 *   &lt;sx:head debug="true" extraLocales="en-us,nl-nl,de-de"/&gt;
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
    public static final String PARSE_CONTENT = "struts.dojo.head.parseContent";
    
    private String debug;
    private String compressed;
    private String baseRelativePath;
    private String extraLocales;
    private String locale;
    private String cache;
    private String parseContent;
    
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
        if (this.cache != null)
            addParameter("cache", findValue(this.cache, Boolean.class));
        if (this.parseContent != null) {
            Boolean shouldParseContent = (Boolean) findValue(this.parseContent, Boolean.class);
            addParameter("parseContent", shouldParseContent);
            stack.getContext().put(PARSE_CONTENT, shouldParseContent);
        } else {
            addParameter("parseContent", false);
            stack.getContext().put(PARSE_CONTENT, false);
        }
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

    @StrutsTagAttribute(description="Enable Dojo debug messages", defaultValue="false", type="Boolean")
    public void setDebug(String debug) {
        this.debug = debug;
    }

    @StrutsTagAttribute(description="Use compressed version of dojo.js", defaultValue="true", type="Boolean")
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

    @StrutsTagAttribute(description="Use Struts Dojo profile, which contains all Struts widgets in one file, making it possible to be chached by " +
                "the browser", defaultValue="true", type="Boolean")
    public void setCache(String cache) {
        this.cache = cache;
    }
    
    @StrutsTagAttribute(description="Parse the whole document for widgets", defaultValue="false", type="Boolean")
    public void setParseContent(String parseContent) {
        this.parseContent = parseContent;
    }
}
