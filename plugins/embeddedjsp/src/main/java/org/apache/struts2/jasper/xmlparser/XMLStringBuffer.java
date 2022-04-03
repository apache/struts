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
package org.apache.struts2.jasper.xmlparser;

/**
 * <p>
 * XMLString is a structure used to pass character arrays. However,
 * XMLStringBuffer is a buffer in which characters can be appended
 * and extends XMLString so that it can be passed to methods
 * expecting an XMLString object. This is a safe operation because
 * it is assumed that any callee will <strong>not</strong> modify
 * the contents of the XMLString structure.
 * </p>
 *
 * <p>
 * The contents of the string are managed by the string buffer. As
 * characters are appended, the string buffer will grow as needed.
 * </p>
 *
 * <p>
 * <strong>Note:</strong> Never set the <code>ch</code>,
 * <code>offset</code>, and <code>length</code> fields directly.
 * These fields are managed by the string buffer. In order to reset
 * the buffer, call <code>clear()</code>.
 * </p>
 *
 * @author Andy Clark, IBM
 * @author Eric Ye, IBM
 * @version $Id: XMLStringBuffer.java 467222 2006-10-24 03:17:11Z markt $
 */
public class XMLStringBuffer
        extends XMLString {

    //
    // Constants
    //

    /**
     * Default buffer size (32).
     */
    public static final int DEFAULT_SIZE = 32;

    //
    // Constructors
    //

    /**
     *
     */
    public XMLStringBuffer() {
        this(DEFAULT_SIZE);
    } // <init>()

    /**
     * @param size size
     */
    public XMLStringBuffer(int size) {
        ch = new char[size];
    } // <init>(int)

    /**
     * Constructs a string buffer from a char.
     *
     * @param c character
     */
    public XMLStringBuffer(char c) {
        this(1);
        append(c);
    } // <init>(char)

    /**
     * Constructs a string buffer from a String.
     *
     * @param s string
     */
    public XMLStringBuffer(String s) {
        this(s.length());
        append(s);
    } // <init>(String)

    /**
     * Constructs a string buffer from the specified character array.
     *
     * @param ch     char array
     * @param offset offset
     * @param length length
     */
    public XMLStringBuffer(char[] ch, int offset, int length) {
        this(length);
        append(ch, offset, length);
    } // <init>(char[],int,int)

    /**
     * Constructs a string buffer from the specified XMLString.
     *
     * @param s xml string
     */
    public XMLStringBuffer(XMLString s) {
        this(s.length);
        append(s);
    } // <init>(XMLString)

    //
    // Public methods
    //

    /**
     * Clears the string buffer.
     */
    public void clear() {
        offset = 0;
        length = 0;
    }

    /**
     * append
     *
     * @param c char
     */
    public void append(char c) {
        if (this.length + 1 > this.ch.length) {
            int newLength = this.ch.length * 2;
            if (newLength < this.ch.length + DEFAULT_SIZE)
                newLength = this.ch.length + DEFAULT_SIZE;
            char[] newch = new char[newLength];
            System.arraycopy(this.ch, 0, newch, 0, this.length);
            this.ch = newch;
        }
        this.ch[this.length] = c;
        this.length++;
    } // append(char)

    /**
     * append
     *
     * @param s string
     */
    public void append(String s) {
        int length = s.length();
        if (this.length + length > this.ch.length) {
            int newLength = this.ch.length * 2;
            if (newLength < this.length + length + DEFAULT_SIZE)
                newLength = this.ch.length + length + DEFAULT_SIZE;
            char[] newch = new char[newLength];
            System.arraycopy(this.ch, 0, newch, 0, this.length);
            this.ch = newch;
        }
        s.getChars(0, length, this.ch, this.length);
        this.length += length;
    } // append(String)

    /**
     * append
     *
     * @param ch     char arry
     * @param offset offset
     * @param length length
     */
    public void append(char[] ch, int offset, int length) {
        if (this.length + length > this.ch.length) {
            char[] newch = new char[this.ch.length + length + DEFAULT_SIZE];
            System.arraycopy(this.ch, 0, newch, 0, this.length);
            this.ch = newch;
        }
        System.arraycopy(ch, offset, this.ch, this.length, length);
        this.length += length;
    } // append(char[],int,int)

    /**
     * append
     *
     * @param s xml string
     */
    public void append(XMLString s) {
        append(s.ch, s.offset, s.length);
    } // append(XMLString)

} // class XMLStringBuffer
