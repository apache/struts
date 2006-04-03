/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp;

import com.mockobjects.servlet.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;


/**
 * StrutsMockHttpServletResponse
 *
 * @author Jason Carreira
 * @author tm_jee
 * @version $Date$ $Id$
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
