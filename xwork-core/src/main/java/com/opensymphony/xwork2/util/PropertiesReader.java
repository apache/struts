/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to read properties lines. These lines do
 * not terminate with new-line chars but rather when there is no
 * backslash sign a the end of the line.  This is used to
 * concatenate multiple lines for readability.
 * 
 * This class was pulled out of Jakarta Commons Configuration and
 * Jakarta Commons Lang trunk revision 476093
 */
public class PropertiesReader extends LineNumberReader
{
    /** Stores the comment lines for the currently processed property.*/
    private List<String> commentLines;

    /** Stores the name of the last read property.*/
    private String propertyName;

    /** Stores the value of the last read property.*/
    private String propertyValue;

    /** Stores the list delimiter character.*/
    private char delimiter;
    
    /** Constant for the supported comment characters.*/
    static final String COMMENT_CHARS = "#!";
    
    /** Constant for the radix of hex numbers.*/
    private static final int HEX_RADIX = 16;

    /** Constant for the length of a unicode literal.*/
    private static final int UNICODE_LEN = 4;
    
    /** The list of possible key/value separators */
    private static final char[] SEPARATORS = new char[] {'=', ':'};

    /** The white space characters used as key/value separators. */
    private static final char[] WHITE_SPACE = new char[]{' ', '\t', '\f'};

    /**
     * Constructor.
     *
     * @param reader A Reader.
     */
    public PropertiesReader(Reader reader)
    {
        this(reader, ',');
    }

    /**
     * Creates a new instance of <code>PropertiesReader</code> and sets
     * the underlaying reader and the list delimiter.
     *
     * @param reader the reader
     * @param listDelimiter the list delimiter character
     * @since 1.3
     */
    public PropertiesReader(Reader reader, char listDelimiter)
    {
        super(reader);
        commentLines = new ArrayList<String>();
        delimiter = listDelimiter;
    }
    
    /**
     * Tests whether a line is a comment, i.e. whether it starts with a comment
     * character.
     *
     * @param line the line
     * @return a flag if this is a comment line
     * @since 1.3
     */
    boolean isCommentLine(String line)
    {
        String s = line.trim();
        // blanc lines are also treated as comment lines
        return s.length() < 1 || COMMENT_CHARS.indexOf(s.charAt(0)) >= 0;
    }

    /**
     * Reads a property line. Returns null if Stream is
     * at EOF. Concatenates lines ending with "\".
     * Skips lines beginning with "#" or "!" and empty lines.
     * The return value is a property definition (<code>&lt;name&gt;</code>
     * = <code>&lt;value&gt;</code>)
     *
     * @return A string containing a property value or null
     *
     * @throws IOException in case of an I/O error
     */
    public String readProperty() throws IOException
    {
        commentLines.clear();
        StringBuilder buffer = new StringBuilder();

        while (true)
        {
            String line = readLine();
            if (line == null)
            {
                // EOF
                return null;
            }

            if (isCommentLine(line))
            {
                commentLines.add(line);
                continue;
            }

            line = line.trim();

            if (checkCombineLines(line))
            {
                line = line.substring(0, line.length() - 1);
                buffer.append(line);
            }
            else
            {
                buffer.append(line);
                break;
            }
        }
        return buffer.toString();
    }

    /**
     * Parses the next property from the input stream and stores the found
     * name and value in internal fields. These fields can be obtained using
     * the provided getter methods. The return value indicates whether EOF
     * was reached (<b>false</b>) or whether further properties are
     * available (<b>true</b>).
     *
     * @return a flag if further properties are available
     * @throws IOException if an error occurs
     * @since 1.3
     */
    public boolean nextProperty() throws IOException
    {
        String line = readProperty();

        if (line == null)
        {
            return false; // EOF
        }

        // parse the line
        String[] property = parseProperty(line);
        propertyName = unescapeJava(property[0]);
        propertyValue = unescapeJava(property[1], delimiter);
        return true;
    }

    /**
     * Returns the comment lines that have been read for the last property.
     *
     * @return the comment lines for the last property returned by
     * <code>readProperty()</code>
     * @since 1.3
     */
    public List<String> getCommentLines()
    {
        return commentLines;
    }

    /**
     * Returns the name of the last read property. This method can be called
     * after <code>{@link #nextProperty()}</code> was invoked and its
     * return value was <b>true</b>.
     *
     * @return the name of the last read property
     * @since 1.3
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Returns the value of the last read property. This method can be
     * called after <code>{@link #nextProperty()}</code> was invoked and
     * its return value was <b>true</b>.
     *
     * @return the value of the last read property
     * @since 1.3
     */
    public String getPropertyValue()
    {
        return propertyValue;
    }

    /**
     * Checks if the passed in line should be combined with the following.
     * This is true, if the line ends with an odd number of backslashes.
     *
     * @param line the line
     * @return a flag if the lines should be combined
     */
    private boolean checkCombineLines(String line)
    {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; idx--)
        {
            bsCount++;
        }

        return bsCount % 2 == 1;
    }

    /**
     * Parse a property line and return the key and the value in an array.
     *
     * @param line the line to parse
     * @return an array with the property's key and value
     * @since 1.2
     */
    private String[] parseProperty(String line)
    {
        // sorry for this spaghetti code, please replace it as soon as
        // possible with a regexp when the Java 1.3 requirement is dropped

        String[] result = new String[2];
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        // state of the automaton:
        // 0: key parsing
        // 1: antislash found while parsing the key
        // 2: separator crossing
        // 3: value parsing
        int state = 0;

        for (int pos = 0; pos < line.length(); pos++)
        {
            char c = line.charAt(pos);

            switch (state)
            {
                case 0:
                    if (c == '\\')
                    {
                        state = 1;
                    }
                    else if (contains(WHITE_SPACE, c))
                    {
                        // switch to the separator crossing state
                        state = 2;
                    }
                    else if (contains(SEPARATORS, c))
                    {
                        // switch to the value parsing state
                        state = 3;
                    }
                    else
                    {
                        key.append(c);
                    }

                    break;

                case 1:
                    if (contains(SEPARATORS, c) || contains(WHITE_SPACE, c))
                    {
                        // this is an escaped separator or white space
                        key.append(c);
                    }
                    else
                    {
                        // another escaped character, the '\' is preserved
                        key.append('\\');
                        key.append(c);
                    }

                    // return to the key parsing state
                    state = 0;

                    break;

                case 2:
                    if (contains(WHITE_SPACE, c))
                    {
                        // do nothing, eat all white spaces
                        state = 2;
                    }
                    else if (contains(SEPARATORS, c))
                    {
                        // switch to the value parsing state
                        state = 3;
                    }
                    else
                    {
                        // any other character indicates we encoutered the beginning of the value
                        value.append(c);

                        // switch to the value parsing state
                        state = 3;
                    }

                    break;

                case 3:
                    value.append(c);
                    break;
            }
        }

        result[0] = key.toString().trim();
        result[1] = value.toString().trim();

        return result;
    }
    
    /**
     * <p>Unescapes any Java literals found in the <code>String</code> to a
     * <code>Writer</code>.</p> This is a slightly modified version of the
     * StringEscapeUtils.unescapeJava() function in commons-lang that doesn't
     * drop escaped separators (i.e '\,').
     *
     * @param str  the <code>String</code> to unescape, may be null
     * @param delimiter the delimiter for multi-valued properties
     * @return the processed string
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     */
    protected static String unescapeJava(String str, char delimiter)
    {
        if (str == null)
        {
            return null;
        }
        int sz = str.length();
        StringBuilder out = new StringBuilder(sz);
        StringBuffer unicode = new StringBuffer(UNICODE_LEN);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++)
        {
            char ch = str.charAt(i);
            if (inUnicode)
            {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == UNICODE_LEN)
                {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try
                    {
                        int value = Integer.parseInt(unicode.toString(), HEX_RADIX);
                        out.append((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    }
                    catch (NumberFormatException nfe)
                    {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }

            if (hadSlash)
            {
                // handle an escaped value
                hadSlash = false;

                if (ch == '\\')
                {
                    out.append('\\');
                }
                else if (ch == '\'')
                {
                    out.append('\'');
                }
                else if (ch == '\"')
                {
                    out.append('"');
                }
                else if (ch == 'r')
                {
                    out.append('\r');
                }
                else if (ch == 'f')
                {
                    out.append('\f');
                }
                else if (ch == 't')
                {
                    out.append('\t');
                }
                else if (ch == 'n')
                {
                    out.append('\n');
                }
                else if (ch == 'b')
                {
                    out.append('\b');
                }
                else if (ch == delimiter)
                {
                    out.append('\\');
                    out.append(delimiter);
                }
                else if (ch == 'u')
                {
                    // uh-oh, we're in unicode country....
                    inUnicode = true;
                }
                else
                {
                    out.append(ch);
                }

                continue;
            }
            else if (ch == '\\')
            {
                hadSlash = true;
                continue;
            }
            out.append(ch);
        }

        if (hadSlash)
        {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.append('\\');
        }

        return out.toString();
    }
    
    /**
     * <p>Checks if the object is in the given array.</p>
     *
     * <p>The method returns <code>false</code> if a <code>null</code> array is passed in.</p>
     * 
     * @param array  the array to search through
     * @param objectToFind  the object to find
     * @return <code>true</code> if the array contains the object
     */
    public boolean contains(char[] array, char objectToFind) {
        if (array == null) {
            return false;
        }
        for (char anArray : array) {
            if (objectToFind == anArray) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * <p>Unescapes any Java literals found in the <code>String</code>.
     * For example, it will turn a sequence of <code>'\'</code> and
     * <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.</p>
     * 
     * @param str  the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     */
    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * <p>Unescapes any Java literals found in the <code>String</code> to a
     * <code>Writer</code>.</p>
     *
     * <p>For example, it will turn a sequence of <code>'\'</code> and
     * <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.</p>
     * 
     * <p>A <code>null</code> string input has no effect.</p>
     * 
     * @param out  the <code>Writer</code> used to output unescaped characters
     * @param str  the <code>String</code> to unescape, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException if error occurs on underlying Writer
     */
    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        out.write('\\');
                        break;
                    case '\'':
                        out.write('\'');
                        break;
                    case '\"':
                        out.write('"');
                        break;
                    case 'r':
                        out.write('\r');
                        break;
                    case 'f':
                        out.write('\f');
                        break;
                    case 't':
                        out.write('\t');
                        break;
                    case 'n':
                        out.write('\n');
                        break;
                    case 'b':
                        out.write('\b');
                        break;
                    case 'u':
                        {
                            // uh-oh, we're in unicode country....
                            inUnicode = true;
                            break;
                        }
                    default :
                        out.write(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write('\\');
        }
    }
}
