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
package org.apache.struts2.views.jsp;

import com.mockobjects.ExpectationValue;
import com.mockobjects.ReturnValue;
import com.mockobjects.Verifiable;
import com.mockobjects.util.AssertMo;
import jakarta.servlet.jsp.JspWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class MockBodyContent  extends jakarta.servlet.jsp.tagext.BodyContent implements Verifiable {
    private final ReturnValue myEnclosingWriter = new ReturnValue("enclosing writer");
    private final ExpectationValue myWriter = new ExpectationValue("writer");

    public int getBufferSize() {
        this.notImplemented();
        return super.getBufferSize();
    }

    public MockBodyContent() {
        super((JspWriter)null);
    }

    public void write(int c) throws IOException {
        this.notImplemented();
        super.write(c);
    }

    public void flush() throws IOException {
        this.notImplemented();
        super.flush();
    }

    public boolean isAutoFlush() {
        this.notImplemented();
        return super.isAutoFlush();
    }

    public void write(char[] cbuf) throws IOException {
        this.notImplemented();
        super.write(cbuf);
    }

    public boolean equals(Object obj) {
        this.notImplemented();
        return super.equals(obj);
    }

    public void clearBody() {
        this.notImplemented();
        super.clearBody();
    }

    public void write(String str) throws IOException {
        this.notImplemented();
        super.write(str);
    }

    public void setupGetEnclosingWriter(JspWriter aJspWriter) {
        this.myEnclosingWriter.setValue(aJspWriter);
    }

    public JspWriter getEnclosingWriter() {
        return (JspWriter)this.myEnclosingWriter.getValue();
    }

    private void notImplemented() {
        AssertMo.notImplemented(null);
    }

    public Reader getReader() {
        this.notImplemented();
        return null;
    }

    public void newLine() throws IOException {
        this.notImplemented();
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        this.notImplemented();
    }

    public void print(boolean b) throws IOException {
        this.notImplemented();
    }

    public void setExpectedWriteOut(Writer aWriter) {
        this.myWriter.setExpected(aWriter);
    }

    public void writeOut(Writer aWriter) throws IOException {
        this.myWriter.setActual(aWriter);
    }

    public void print(char c) throws IOException {
        this.notImplemented();
    }

    public void print(int i) throws IOException {
        this.notImplemented();
    }

    public void print(long l) throws IOException {
        this.notImplemented();
    }

    public void print(float v) throws IOException {
        this.notImplemented();
    }

    public void print(double v) throws IOException {
        this.notImplemented();
    }

    public void print(char[] chars) throws IOException {
        this.notImplemented();
    }

    public void print(String s) throws IOException {
        this.notImplemented();
    }

    public void print(Object o) throws IOException {
        this.notImplemented();
    }

    public void println() throws IOException {
        this.notImplemented();
    }

    public void println(boolean b) throws IOException {
        this.notImplemented();
    }

    public void println(char c) throws IOException {
        this.notImplemented();
    }

    public void println(int i) throws IOException {
        this.notImplemented();
    }

    public void println(long l) throws IOException {
        this.notImplemented();
    }

    public void println(float v) throws IOException {
        this.notImplemented();
    }

    public void println(double v) throws IOException {
        this.notImplemented();
    }

    public void println(char[] chars) throws IOException {
        this.notImplemented();
    }

    public void println(String s) throws IOException {
        this.notImplemented();
    }

    public void println(Object o) throws IOException {
        this.notImplemented();
    }

    public void clear() throws IOException {
        this.notImplemented();
    }

    public void clearBuffer() throws IOException {
        this.notImplemented();
    }

    public void close() throws IOException {
        this.notImplemented();
    }

    public int getRemaining() {
        this.notImplemented();
        return 0;
    }

    public void write(String content, int off, int len) throws IOException {
        this.notImplemented();
        super.write(content, off, len);
    }

    public String getString() {
        this.notImplemented();
        return null;
    }

    public void verify() {
    }
}
