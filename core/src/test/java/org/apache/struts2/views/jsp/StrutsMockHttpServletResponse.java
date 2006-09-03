/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.jsp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import com.mockobjects.servlet.MockHttpServletResponse;


/**
 * StrutsMockHttpServletResponse
 *
 */
public class StrutsMockHttpServletResponse extends MockHttpServletResponse {
    private Locale locale;
    private PrintWriter writer;
    
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public PrintWriter getWriter() throws IOException {
    	if (writer == null)
    		return new PrintWriter(new ByteArrayOutputStream());
    	else 
    		return writer;
    }
    
    public void setWriter(PrintWriter writer) {
    	this.writer = writer;
    }

    public String encodeURL(String s) {
        return s;
    }

    public String encodeUrl(String s) {
        return s;
    }
}
