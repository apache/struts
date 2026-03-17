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
package org.apache.struts2.json;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Deserializes an object from a JSON string with configurable limits
 * to prevent denial-of-service attacks via malicious payloads.
 * </p>
 */
public class StrutsJSONReader implements JSONReader {
    private static final Object OBJECT_END = new Object();
    private static final Object ARRAY_END = new Object();
    private static final Object COLON = new Object();
    private static final Object COMMA = new Object();
    private static final Map<Character, Character> escapes = Map.of(
            '"', '"',
            '\\', '\\',
            '/', '/',
            'b', '\b',
            'f', '\f',
            'n', '\n',
            'r', '\r',
            't', '\t'
    );

    private CharacterIterator it;
    private char c;
    private Object token;
    private final StringBuilder buf = new StringBuilder();

    private int maxElements = DEFAULT_MAX_ELEMENTS;
    private int maxDepth = DEFAULT_MAX_DEPTH;
    private int maxStringLength = DEFAULT_MAX_STRING_LENGTH;
    private int maxKeyLength = DEFAULT_MAX_KEY_LENGTH;
    private int depth;

    @Override
    public void setMaxElements(int maxElements) {
        this.maxElements = maxElements;
    }

    @Override
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void setMaxStringLength(int maxStringLength) {
        this.maxStringLength = maxStringLength;
    }

    @Override
    public void setMaxKeyLength(int maxKeyLength) {
        this.maxKeyLength = maxKeyLength;
    }

    protected char next() {
        this.c = this.it.next();

        return this.c;
    }

    protected void skipWhiteSpace() {
        while (Character.isWhitespace(this.c)) {
            this.next();
        }
    }

    @Override
    public Object read(String string) throws JSONException {
        this.it = new StringCharacterIterator(string);
        this.c = this.it.first();
        this.depth = 0;

        return this.read();
    }

    protected Object read() throws JSONException {
        Object ret;

        this.skipWhiteSpace();

        if (this.c == '"') {
            this.next();
            ret = this.string('"');
        } else if (this.c == '\'') {
            this.next();
            ret = this.string('\'');
        } else if (this.c == '[') {
            this.next();
            ret = this.array();
        } else if (this.c == ']') {
            ret = ARRAY_END;
            this.next();
        } else if (this.c == ',') {
            ret = COMMA;
            this.next();
        } else if (this.c == '{') {
            this.next();
            ret = this.object();
        } else if (this.c == '}') {
            ret = OBJECT_END;
            this.next();
        } else if (this.c == ':') {
            ret = COLON;
            this.next();
        } else if ((this.c == 't') && (this.next() == 'r') && (this.next() == 'u') && (this.next() == 'e')) {
            ret = Boolean.TRUE;
            this.next();
        } else if ((this.c == 'f') && (this.next() == 'a') && (this.next() == 'l') && (this.next() == 's')
                && (this.next() == 'e')) {
            ret = Boolean.FALSE;
            this.next();
        } else if ((this.c == 'n') && (this.next() == 'u') && (this.next() == 'l') && (this.next() == 'l')) {
            ret = null;
            this.next();
        } else if (Character.isDigit(this.c) || (this.c == '-')) {
            ret = this.number();
        } else {
            throw buildInvalidInputException();
        }

        this.token = ret;

        return ret;
    }

    protected Map<String, Object> object() throws JSONException {
        if (this.depth >= this.maxDepth) {
            throw new JSONException("JSON object nesting exceeds maximum allowed depth ("
                    + this.maxDepth + "). Use " + JSONConstants.JSON_MAX_DEPTH + " to increase the limit.");
        }
        this.depth++;
        try {
            Map<String, Object> ret = new HashMap<>();
            Object next = this.read();
            if (next != OBJECT_END) {
                String key = (String) next;
                validateKeyLength(key);
                while (this.token != OBJECT_END) {
                    this.read(); // should be a colon

                    if (this.token != OBJECT_END) {
                        if (ret.size() >= this.maxElements) {
                            throw new JSONException("JSON object exceeds maximum allowed elements ("
                                    + this.maxElements + "). Use " + JSONConstants.JSON_MAX_ELEMENTS + " to increase the limit.");
                        }
                        ret.put(key, this.read());

                        if (this.read() == COMMA) {
                            Object name = this.read();

                            if (name instanceof String nextKey) {
                                key = nextKey;
                                validateKeyLength(key);
                            } else {
                                throw buildInvalidInputException();
                            }
                        }
                    }
                }
            }

            return ret;
        } finally {
            this.depth--;
        }
    }

    private void validateKeyLength(String key) throws JSONException {
        if (key.length() > this.maxKeyLength) {
            throw new JSONException("JSON object key exceeds maximum allowed length ("
                    + this.maxKeyLength + "). Use " + JSONConstants.JSON_MAX_KEY_LENGTH + " to increase the limit.");
        }
    }

    protected JSONException buildInvalidInputException() {
        return new JSONException("Input string is not well formed JSON (invalid char " + this.c + ")");
    }


    protected List<Object> array() throws JSONException {
        if (this.depth >= this.maxDepth) {
            throw new JSONException("JSON array nesting exceeds maximum allowed depth ("
                    + this.maxDepth + "). Use " + JSONConstants.JSON_MAX_DEPTH + " to increase the limit.");
        }
        this.depth++;
        try {
            List<Object> ret = new ArrayList<>();
            Object value = this.read();

            while (this.token != ARRAY_END) {
                if (ret.size() >= this.maxElements) {
                    throw new JSONException("JSON array exceeds maximum allowed elements ("
                            + this.maxElements + "). Use " + JSONConstants.JSON_MAX_ELEMENTS + " to increase the limit.");
                }
                ret.add(value);

                Object read = this.read();
                if (read == COMMA) {
                    value = this.read();
                } else if (read != ARRAY_END) {
                    throw buildInvalidInputException();
                }
            }

            return ret;
        } finally {
            this.depth--;
        }
    }

    protected Object number() throws JSONException {
        this.buf.setLength(0);
        boolean toDouble = false;

        if (this.c == '-') {
            this.add();
        }

        this.addDigits();

        if (this.c == '.') {
            toDouble = true;
            this.add();
            this.addDigits();
        }

        if ((this.c == 'e') || (this.c == 'E')) {
            toDouble = true;
            this.add();

            if ((this.c == '+') || (this.c == '-')) {
                this.add();
            }

            this.addDigits();
        }

        if (toDouble) {
            try {
                return Double.parseDouble(this.buf.toString());
            } catch (NumberFormatException e) {
                throw buildInvalidInputException();
            }
        } else {
            try {
                return Long.parseLong(this.buf.toString());
            } catch (NumberFormatException e) {
                throw buildInvalidInputException();
            }
        }
    }

    protected Object string(char quote) throws JSONException {
        this.buf.setLength(0);

        while ((this.c != quote) && (this.c != CharacterIterator.DONE)) {
            if (this.c == '\\') {
                this.next();

                if (this.c == 'u') {
                    this.add(this.unicode());
                } else {
                    Character value = escapes.get(this.c);

                    if (value != null) {
                        this.add(value);
                    }
                }
            } else {
                this.add();
            }
            if (this.buf.length() > this.maxStringLength) {
                throw new JSONException("JSON string exceeds maximum allowed length ("
                        + this.maxStringLength + "). Use " + JSONConstants.JSON_MAX_STRING_LENGTH + " to increase the limit.");
            }
        }

        this.next();

        return this.buf.toString();
    }

    protected void add(char cc) {
        this.buf.append(cc);
        this.next();
    }

    protected void add() {
        this.add(this.c);
    }

    protected void addDigits() {
        while (Character.isDigit(this.c)) {
            this.add();
        }
    }

    protected char unicode() {
        int value = 0;

        for (int i = 0; i < 4; ++i) {
            value = switch (this.next()) {
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> (value << 4) + (this.c - '0');
                case 'a', 'b', 'c', 'd', 'e', 'f' -> (value << 4) + (this.c - 'W');
                case 'A', 'B', 'C', 'D', 'E', 'F' -> (value << 4) + (this.c - '7');
                default -> value;
            };
        }

        return (char) value;
    }
}
