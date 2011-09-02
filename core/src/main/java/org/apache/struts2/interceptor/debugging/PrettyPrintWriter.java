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

package org.apache.struts2.interceptor.debugging;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

public class PrettyPrintWriter {

    private final PrintWriter writer;
    private final Stack<String> elementStack = new Stack<String>();
    private final char[] lineIndenter;

    private boolean tagInProgress;
    private int depth;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    private String newLine;
    private boolean escape = true;

    private static final char[] NULL = "&#x0;".toCharArray();
    private static final char[] AMP = "&amp;".toCharArray();
    private static final char[] LT = "&lt;".toCharArray();
    private static final char[] GT = "&gt;".toCharArray();
    private static final char[] SLASH_R = "&#x0D;".toCharArray();
    private static final char[] QUOT = "&quot;".toCharArray();
    private static final char[] APOS = "&apos;".toCharArray();
    private static final char[] CLOSE = "</".toCharArray();

    public PrettyPrintWriter(Writer writer, char[] lineIndenter, String newLine) {
        this.writer = new PrintWriter(writer);
        this.lineIndenter = lineIndenter;
        this.newLine = newLine;
    }

    public PrettyPrintWriter(Writer writer, char[] lineIndenter) {
        this(writer, lineIndenter, "\n");
    }

    public PrettyPrintWriter(Writer writer, String lineIndenter, String newLine) {
        this(writer, lineIndenter.toCharArray(), newLine);
    }

    public PrettyPrintWriter(Writer writer, String lineIndenter) {
        this(writer, lineIndenter.toCharArray());
    }

    public PrettyPrintWriter(Writer writer) {
        this(writer, new char[]{' ', ' '});
    }

    public void startNode(String name) {
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(name);
        elementStack.push(name);
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    public void setValue(String text) {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();

        writeText(writer, text);
    }

    public void addAttribute(String key, String value) {
        writer.write(' ');
        writer.write(key);
        writer.write('=');
        writer.write('\"');
        writeAttributeValue(writer, value);
        writer.write('\"');
    }

    protected void writeAttributeValue(PrintWriter writer, String text) {
        writeText(text);
    }

    protected void writeText(PrintWriter writer, String text) {
        writeText(text);
    }

    private void writeText(String text) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\0':
                    this.writer.write(NULL);
                    break;
                case '&':
                    this.writer.write(AMP);
                    break;
                case '<':
                    this.writer.write(LT);
                    break;
                case '>':
                    this.writer.write(GT);
                    break;
                case '"':
                    this.writer.write(QUOT);
                    break;
                case '\'':
                    //for some reason IE just doesn't like this when we use it from ObjectToHtmlWriter
                    //it works on FF and Opera
                    if (escape)
                        this.writer.write(APOS);
                    else
                        this.writer.write(c);
                    break;
                case '\r':
                    this.writer.write(SLASH_R);
                    break;
                default:
                    this.writer.write(c);
            }
        }
    }

    public void endNode() {
        depth--;
        if (tagIsEmpty) {
            writer.write('/');
            readyForNewLine = false;
            finishTag();
            elementStack.pop();
        } else {
            finishTag();
            writer.write(CLOSE);
            writer.write((String)elementStack.pop());
            writer.write('>');
        }
        readyForNewLine = true;
        if (depth == 0 ) {
            writer.flush();
        }
    }

    private void finishTag() {
        if (tagInProgress) {
            writer.write('>');
        }
        tagInProgress = false;
        if (readyForNewLine) {
            endOfLine();
        }
        readyForNewLine = false;
        tagIsEmpty = false;
    }

    protected void endOfLine() {
        writer.write(newLine);
        for (int i = 0; i < depth; i++) {
            writer.write(lineIndenter);
        }
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }

    public boolean isEscape() {
        return escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}
