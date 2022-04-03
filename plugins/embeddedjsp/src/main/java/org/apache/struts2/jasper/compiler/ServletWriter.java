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
package org.apache.struts2.jasper.compiler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This is what is used to generate servlets.
 *
 * @author Anil K. Vijendran
 * @author Kin-man Chung
 */
public class ServletWriter {
    public static int TAB_WIDTH = 2;
    public static String SPACES = "                              ";

    // Current indent level:
    private int indent = 0;
    private int virtual_indent = 0;

    // The sink writer:
    PrintWriter writer;

    // servlet line numbers start from 1
    private int javaLine = 1;


    public ServletWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public void close() throws IOException {
        writer.close();
    }


    // -------------------- Access informations --------------------

    public int getJavaLine() {
        return javaLine;
    }


    // -------------------- Formatting --------------------

    public void pushIndent() {
        virtual_indent += TAB_WIDTH;
        if (virtual_indent >= 0 && virtual_indent <= SPACES.length())
            indent = virtual_indent;
    }

    public void popIndent() {
        virtual_indent -= TAB_WIDTH;
        if (virtual_indent >= 0 && virtual_indent <= SPACES.length())
            indent = virtual_indent;
    }

    /**
     * Print a standard comment for echo outputed chunk.
     *
     * @param start The starting position of the JSP chunk being processed.
     * @param stop  The ending position of the JSP chunk being processed.
     * @param chars characters as array
     */
    public void printComment(Mark start, Mark stop, char[] chars) {
        if (start != null && stop != null) {
            println("// from=" + start);
            println("//   to=" + stop);
        }

        if (chars != null)
            for (int i = 0; i < chars.length; ) {
                printin();
                print("// ");
                while (chars[i] != '\n' && i < chars.length)
                    writer.print(chars[i++]);
            }
    }

    /**
     * Prints the given string followed by '\n'
     *
     * @param s string to print
     */
    public void println(String s) {
        javaLine++;
        writer.println(s);
    }

    /**
     * Prints a '\n'
     */
    public void println() {
        javaLine++;
        writer.println("");
    }

    /**
     * Prints the current indention
     */
    public void printin() {
        writer.print(SPACES.substring(0, indent));
    }

    /**
     * Prints the current indention, followed by the given string
     *
     * @param s string to print
     */
    public void printin(String s) {
        writer.print(SPACES.substring(0, indent));
        writer.print(s);
    }

    /**
     * Prints the current indention, and then the string, and a '\n'.
     *
     * @param s string to print
     */
    public void printil(String s) {
        javaLine++;
        writer.print(SPACES.substring(0, indent));
        writer.println(s);
    }

    /**
     * <p>
     * Prints the given char.
     * </p>
     *
     * Use println() to print a '\n'.
     *
     * @param c char to print
     */
    public void print(char c) {
        writer.print(c);
    }

    /**
     * Prints the given int.
     *
     * @param i int to print
     */
    public void print(int i) {
        writer.print(i);
    }

    /**
     * <p>
     * Prints the given string.
     * </p>
     *
     * <p>
     * The string must not contain any '\n', otherwise the line count will be
     * off.
     * </p>
     *
     * @param s string to print
     */
    public void print(String s) {
        writer.print(s);
    }

    /**
     * <p>
     * Prints the given string.
     * </p>
     *
     * <p>
     * If the string spans multiple lines, the line count will be adjusted
     * accordingly.
     * </p>
     *
     * @param s string to print
     */
    public void printMultiLn(String s) {
        int index = 0;

        // look for hidden newlines inside strings
        while ((index = s.indexOf('\n', index)) > -1) {
            javaLine++;
            index++;
        }

        writer.print(s);
    }
}
