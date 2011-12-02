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

package org.apache.struts2.dojo.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.dojo.components.Head;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see Head
 */
public class HeadTag extends AbstractUITag {

    private static final long serialVersionUID = 6876765769175246030L;

    private String debug;
    private String compressed;
    private String baseRelativePath;
    private String extraLocales;
    private String locale;
    private String cache;
    private String parseContent;
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Head(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();
        
        Head head = (Head) component;
        head.setDebug(debug);
        head.setCompressed(compressed);
        head.setBaseRelativePath(baseRelativePath);
        head.setExtraLocales(extraLocales);
        head.setLocale(locale);
        head.setCache(cache);
        head.setParseContent(parseContent);
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public void setBaseRelativePath(String baseRelativePath) {
        this.baseRelativePath = baseRelativePath;
    }

    public void setCompressed(String compressed) {
        this.compressed = compressed;
    }

    public void setExtraLocales(String extraLocales) {
        this.extraLocales = extraLocales;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public void setParseContent(String parseContent) {
        this.parseContent = parseContent;
    }
}
