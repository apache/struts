/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.mockobjects.servlet.MockJspWriter;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.io.StringWriter;


/**
 * Unforunately, the MockJspWriter throws a NotImplementedException when any of the Writer methods are invoked and
 * as you might guess, Velocity uses the Writer methods.  I'velocityEngine subclassed the MockJspWriter for the time being so
 * that we can do testing on the results until MockJspWriter gets fully implemented.
 * <p/>
 * todo replace this once MockJspWriter implements Writer correctly (i.e. doesn't throw NotImplementException)
 */
public class WebWorkMockJspWriter extends JspWriter {
    StringWriter writer;

    public WebWorkMockJspWriter(StringWriter writer) {
        super(1024, true);
        this.writer = writer;
    }

    public void newLine() throws IOException {
        writer.write("\n");
    }

    public void print(boolean b) throws IOException {
        writer.write(String.valueOf(b));
    }

    public void print(char c) throws IOException {
        writer.write(String.valueOf(c));
    }

    public void print(int i) throws IOException {
        writer.write(i);
    }

    public void print(long l) throws IOException {
        writer.write(String.valueOf(l));
    }

    public void print(float v) throws IOException {
        writer.write(String.valueOf(v));
    }

    public void print(double v) throws IOException {
        writer.write(String.valueOf(v));
    }

    public void print(char[] chars) throws IOException {
        writer.write(chars);
    }

    public void print(String s) throws IOException {
        writer.write(s);
    }

    public void print(Object o) throws IOException {
        writer.write(o.toString());
    }

    public void println() throws IOException {
        writer.write("\n");
    }

    public void println(boolean b) throws IOException {
        print(b);
        println();
    }

    public void println(char c) throws IOException {
        print(c);
        println();
    }

    public void println(int i) throws IOException {
        print(i);
        println();
    }

    public void println(long l) throws IOException {
        print(l);
        println();
    }

    public void println(float v) throws IOException {
        print(v);
        println();
    }

    public void println(double v) throws IOException {
        print(v);
        println();
    }

    public void println(char[] chars) throws IOException {
        print(chars);
        println();
    }

    public void println(String s) throws IOException {
        print(s);
        println();
    }

    public void println(Object o) throws IOException {
        print(o);
        println();
    }

    public void clear() throws IOException {
    }

    public void clearBuffer() throws IOException {
    }

    public void close() throws IOException {
        writer.close();
    }

    public int getRemaining() {
        return 0;
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        writer.write(cbuf, off, len);
    }

    public void write(String str) throws IOException {
        writer.write(str);
    }

    public void write(int c) throws IOException {
        writer.write(c);
    }

    public void write(char[] cbuf) throws IOException {
        writer.write(cbuf);
    }

    public void write(String str, int off, int len) throws IOException {
        writer.write(str, off, len);
    }

    public void flush() {
        writer.flush();
    }
}
