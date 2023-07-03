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
import com.mockobjects.util.AssertMo;
import jakarta.servlet.jsp.JspWriter;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MockJspWriter  extends JspWriter {
    private final ExpectationValue expectedData = new ExpectationValue("data");
    private StringWriter stringWriter = new StringWriter();
    private PrintWriter printWriter;

    public MockJspWriter() {
        super(0, true);
        this.printWriter = new PrintWriter(this.stringWriter);
    }

    public void setExpectedData(String data) {
        this.expectedData.setExpected(data);
    }

    private final void notImplemented() {
        AssertMo.notImplemented(this.getClass().getName());
    }

    public void newLine() {
        this.notImplemented();
    }

    public void flush() {
        this.notImplemented();
    }

    public void print(double d) {
        this.printWriter.print(String.valueOf(d));
    }

    public void println() {
        this.notImplemented();
    }

    public void close() {
        this.notImplemented();
    }

    public void print(int anInt) {
        this.printWriter.print(String.valueOf(anInt));
    }

    public void print(long aLong) {
        this.printWriter.print(String.valueOf(aLong));
    }

    public void print(float f) {
        this.notImplemented();
    }

    public void println(char c) {
        this.notImplemented();
    }

    public void clear() {
        this.notImplemented();
    }

    public void print(boolean b) {
        this.notImplemented();
    }

    public void print(String aString) {
        this.printWriter.print(aString);
    }

    public void println(String aString) {
        this.printWriter.print(aString);
    }

    public void print(char c) {
        this.notImplemented();
    }

    public void write(char[] buf, int off, int len) {
        this.printWriter.write(buf, off, len);
    }

    public void println(char[] c) {
        this.notImplemented();
    }

    public void println(boolean b) {
        this.notImplemented();
    }

    public void clearBuffer() {
        this.notImplemented();
    }

    public void print(Object anObject) {
        this.printWriter.print(anObject);
    }

    public void println(long l) {
        this.notImplemented();
    }

    public void println(int i) {
        this.notImplemented();
    }

    public void print(char[] c) {
        this.notImplemented();
    }

    public void println(float f) {
        this.notImplemented();
    }

    public void println(double d) {
        this.notImplemented();
    }

    public int getRemaining() {
        this.notImplemented();
        return -1;
    }

    public void println(Object anObject) {
        this.printWriter.print(anObject);
    }

    public void verify() {
        this.printWriter.flush();
        this.expectedData.setActual(this.stringWriter.toString());
        this.expectedData.verify();
        this.expectedData.setExpected("");
        this.stringWriter = new StringWriter();
        this.printWriter = new PrintWriter(this.stringWriter);
    }
}
