/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;


/**
 * WebWorkMockBodyContent
 *
 * @author Jason Carreira
 *         Date: Mar 15, 2004 10:50:56 PM
 */
public class WebWorkMockBodyContent extends BodyContent {

    private JspWriter jspWriter;
    private String body = null;


    public WebWorkMockBodyContent(JspWriter jspWriter) {
        super(jspWriter);
        this.jspWriter = jspWriter;
    }


    public Reader getReader() {
        return null;
    }

    public int getRemaining() {
        return jspWriter.getRemaining();
    }

    public void setString(String body) {
        this.body = body;
    }

    public String getString() {
        return body;
    }

    public void clear() throws IOException {
        jspWriter.clear();
    }

    public void clearBuffer() throws IOException {
        jspWriter.clearBuffer();
    }

    public void close() throws IOException {
        jspWriter.close();
    }

    public void newLine() throws IOException {
        jspWriter.newLine();
    }

    public void print(double v) throws IOException {
        jspWriter.print(v);
    }

    public void print(int i) throws IOException {
        jspWriter.print(i);
    }

    public void print(long l) throws IOException {
        jspWriter.print(l);
    }

    public void print(float v) throws IOException {
        jspWriter.print(v);
    }

    public void print(boolean b) throws IOException {
        jspWriter.print(b);
    }

    public void print(String s) throws IOException {
        jspWriter.print(s);
    }

    public void print(char c) throws IOException {
        jspWriter.print(c);
    }

    public void print(Object o) throws IOException {
        jspWriter.print(o);
    }

    public void print(char[] chars) throws IOException {
        jspWriter.print(chars);
    }

    public void println() throws IOException {
        jspWriter.println();
    }

    public void println(char c) throws IOException {
        jspWriter.println(c);
    }

    public void println(String s) throws IOException {
        jspWriter.println(s);
    }

    public void println(char[] chars) throws IOException {
        jspWriter.println(chars);
    }

    public void println(boolean b) throws IOException {
        jspWriter.println(b);
    }

    public void println(long l) throws IOException {
        jspWriter.println(l);
    }

    public void println(int i) throws IOException {
        jspWriter.println(i);
    }

    public void println(float v) throws IOException {
        jspWriter.println(v);
    }

    public void println(double v) throws IOException {
        jspWriter.println(v);
    }

    public void println(Object o) throws IOException {
        jspWriter.println(o);
    }

    public void write(char[] chars, int i, int i1) throws IOException {
        jspWriter.write(chars, i, i1);
    }

    public void writeOut(Writer writer) throws IOException {
        writer.write(body);
    }
}
