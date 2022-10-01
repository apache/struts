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
package org.apache.tiles.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is an utility class that perform wilcard-patterns matching and
 * isolation taken from Apache Struts that is taken, in turn, from Apache
 * Struts.
 *
 * @since 2.1.0
 */
public class WildcardHelper {
    /**
     * The int representing '*' in the pattern <code>int []</code>.
     *
     * @since 2.1.0
     */
    protected static final int MATCH_FILE = -1;

    /**
     * The int representing '**' in the pattern <code>int []</code>.
     *
     * @since 2.1.0
     */
    protected static final int MATCH_PATH = -2;

    /**
     * The int representing begin in the pattern <code>int []</code>.
     *
     * @since 2.1.0
     */
    protected static final int MATCH_BEGIN = -4;

    /**
     * The int representing end in pattern <code>int []</code>.
     *
     * @since 2.1.0
     */
    protected static final int MATCH_THEEND = -5;

    /**
     * The int value that terminates the pattern <code>int []</code>.
     *
     * @since 2.1.0
     */
    protected static final int MATCH_END = -3;

    /**
     * The length of the placeholder.
     *
     * @since 2.1.0
     */
    private static final int PLACEHOLDER_LENGTH = 3;

    /**
     * <p>
     * Translate the given <code>String</code> into a <code>int []</code>
     * representing the pattern matchable by this class. <br>
     * This function translates a <code>String</code> into an int array
     * converting the special '*' and '\' characters. <br>
     * Here is how the conversion algorithm works:
     * </p>
     *
     * <ul>
     *
     * <li>The '*' character is converted to MATCH_FILE, meaning that zero or
     * more characters (excluding the path separator '/') are to be matched.</li>
     *
     * <li>The '**' sequence is converted to MATCH_PATH, meaning that zero or
     * more characters (including the path separator '/') are to be matched.</li>
     *
     * <li>The '\' character is used as an escape sequence ('\*' is translated
     * in '*', not in MATCH_FILE). If an exact '\' character is to be matched
     * the source string must contain a '\\'. sequence.</li>
     *
     * </ul>
     *
     * <p>
     * When more than two '*' characters, not separated by another character,
     * are found their value is considered as '**' (MATCH_PATH). <br>
     * The array is always terminated by a special value (MATCH_END). <br>
     * All MATCH* values are less than zero, while normal characters are equal
     * or greater.
     * </p>
     *
     * @param data The string to translate.
     * @return The encoded string as an int array, terminated by the MATCH_END
     * value (don't consider the array length).
     * @throws NullPointerException If data is null.
     * @since 2.1.0
     */
    public int[] compilePattern(String data) {
        // Prepare the arrays
        int[] expr = new int[data.length() + 2];
        char[] buff = data.toCharArray();

        // Prepare variables for the translation loop
        int y = 0;
        boolean slash = false;

        // Must start from beginning
        expr[y++] = MATCH_BEGIN;

        if (buff.length > 0) {
            if (buff[0] == '\\') {
                slash = true;
            } else if (buff[0] == '*') {
                expr[y++] = MATCH_FILE;
            } else {
                expr[y++] = buff[0];
            }

            // Main translation loop
            for (int x = 1; x < buff.length; x++) {
                // If the previous char was '\' simply copy this char.
                if (slash) {
                    expr[y++] = buff[x];
                    slash = false;

                    // If the previous char was not '\' we have to do a bunch of
                    // checks
                } else {
                    // If this char is '\' declare that and continue
                    if (buff[x] == '\\') {
                        slash = true;

                        // If this char is '*' check the previous one
                    } else if (buff[x] == '*') {
                        // If the previous character als was '*' match a path
                        if (expr[y - 1] <= MATCH_FILE) {
                            expr[y - 1] = MATCH_PATH;
                        } else {
                            expr[y++] = MATCH_FILE;
                        }
                    } else {
                        expr[y++] = buff[x];
                    }
                }
            }
        }

        // Must match end at the end
        expr[y] = MATCH_THEEND;

        return expr;
    }

    /**
     * Match a pattern agains a string and isolates wildcard replacement into a
     * <code>Stack</code>.
     *
     * @param data The string to match
     * @param expr The compiled wildcard expression
     * @return The list of matched variables, or <code>null</code> if it does not match.
     * @throws NullPointerException If any parameters are null
     * @since 2.2.0
     */
    public List<String> match(String data, int[] expr) {
        List<String> varsValues = null;

        if (data == null) {
            throw new NullPointerException("No data provided");
        }

        if (expr == null) {
            throw new NullPointerException("No pattern expression provided");
        }

        char[] buff = data.toCharArray();

        // Allocate the result buffer
        char[] rslt = new char[expr.length + buff.length];

        // The previous and current position of the expression character
        // (MATCH_*)
        int charpos = 0;

        // The position in the expression, input, translation and result arrays
        int exprpos = 0;
        int buffpos = 0;
        int rsltpos = 0;
        int offset = -1;

        // First check for MATCH_BEGIN
        boolean matchBegin = false;

        if (expr[charpos] == MATCH_BEGIN) {
            matchBegin = true;
            exprpos = ++charpos;
        }

        // Search the fist expression character (except MATCH_BEGIN - already
        // skipped)
        while (expr[charpos] >= 0) {
            charpos++;
        }

        // The expression charater (MATCH_*)
        int exprchr = expr[charpos];

        while (true) {
            // Check if the data in the expression array before the current
            // expression character matches the data in the input buffer
            if (matchBegin) {
                if (!matchArray(expr, exprpos, charpos, buff, buffpos)) {
                    return null;
                }

                matchBegin = false;
            } else {
                offset = indexOfArray(expr, exprpos, charpos, buff, buffpos);

                if (offset < 0) {
                    return null;
                }
            }

            // Check for MATCH_BEGIN
            if (matchBegin) {
                if (offset != 0) {
                    return null;
                }

                matchBegin = false;
            }

            // Advance buffpos
            buffpos += (charpos - exprpos);

            // Check for END's
            if (exprchr == MATCH_END) {
                if (rsltpos > 0) {
                    varsValues = addAndCreateList(varsValues, new String(rslt,
                            0, rsltpos));
                }

                // Don't care about rest of input buffer
                varsValues = addElementOnTop(varsValues, data);
                return varsValues;
            } else if (exprchr == MATCH_THEEND) {
                if (rsltpos > 0) {
                    varsValues = addAndCreateList(varsValues, new String(rslt,
                            0, rsltpos));
                }

                // Check that we reach buffer's end
                if (buffpos == buff.length) {
                    addElementOnTop(varsValues, data);
                    return varsValues;
                }
                return null;
            }

            // Search the next expression character
            exprpos = ++charpos;

            while (expr[charpos] >= 0) {
                charpos++;
            }

            int prevchr = exprchr;

            exprchr = expr[charpos];

            // We have here prevchr == * or **.
            offset = (prevchr == MATCH_FILE) ? indexOfArray(expr, exprpos,
                    charpos, buff, buffpos) : lastIndexOfArray(expr, exprpos,
                    charpos, buff, buffpos);

            if (offset < 0) {
                return null;
            }

            // Copy the data from the source buffer into the result buffer
            // to substitute the expression character
            if (prevchr == MATCH_PATH) {
                while (buffpos < offset) {
                    rslt[rsltpos++] = buff[buffpos++];
                }
            } else {
                // Matching file, don't copy '/'
                while (buffpos < offset) {
                    if (buff[buffpos] == '/') {
                        return null;
                    }

                    rslt[rsltpos++] = buff[buffpos++];
                }
            }

            varsValues = addAndCreateList(varsValues, new String(rslt, 0,
                    rsltpos));
            rsltpos = 0;
        }
    }

    /**
     * Get the offset of a part of an int array within a char array. <br>
     * This method return the index in d of the first occurrence after dpos of
     * that part of array specified by r, starting at rpos and terminating at
     * rend.
     *
     * @param r The array containing the data that need to be matched in d.
     * @param rpos The index of the first character in r to look for.
     * @param rend The index of the last character in r to look for plus 1.
     * @param d The array of char that should contain a part of r.
     * @param dpos The starting offset in d for the matching.
     * @return The offset in d of the part of r matched in d or -1 if that was
     * not found.
     * @since 2.1.0
     */
    protected int indexOfArray(int[] r, int rpos, int rend, char[] d, int dpos) {
        // Check if pos and len are legal
        if (rend < rpos) {
            throw new IllegalArgumentException("rend < rpos");
        }

        // If we need to match a zero length string return current dpos
        if (rend == rpos) {
            return (d.length); // ?? dpos?
        }

        // If we need to match a 1 char length string do it simply
        if ((rend - rpos) == 1) {
            // Search for the specified character
            for (int x = dpos; x < d.length; x++) {
                if (r[rpos] == d[x]) {
                    return (x);
                }
            }
        }

        // Main string matching loop. It gets executed if the characters to
        // match are less then the characters left in the d buffer
        while (((dpos + rend) - rpos) <= d.length) {
            // Set current startpoint in d
            int y = dpos;

            // Check every character in d for equity. If the string is matched
            // return dpos
            for (int x = rpos; x <= rend; x++) {
                if (x == rend) {
                    return (dpos);
                }

                if (r[x] != d[y++]) {
                    break;
                }
            }

            // Increase dpos to search for the same string at next offset
            dpos++;
        }

        // The remaining chars in d buffer were not enough or the string
        // wasn't matched
        return (-1);
    }

    /**
     * Get the offset of a last occurance of an int array within a char array.
     * <br>
     * This method return the index in d of the last occurrence after dpos of
     * that part of array specified by r, starting at rpos and terminating at
     * rend.
     *
     * @param r The array containing the data that need to be matched in d.
     * @param rpos The index of the first character in r to look for.
     * @param rend The index of the last character in r to look for plus 1.
     * @param d The array of char that should contain a part of r.
     * @param dpos The starting offset in d for the matching.
     * @return The offset in d of the last part of r matched in d or -1 if that
     * was not found.
     * @since 2.1.0
     */
    protected int lastIndexOfArray(int[] r, int rpos, int rend, char[] d,
            int dpos) {
        // Check if pos and len are legal
        if (rend < rpos) {
            throw new IllegalArgumentException("rend < rpos");
        }

        // If we need to match a zero length string return current dpos
        if (rend == rpos) {
            return (d.length); // ?? dpos?
        }

        // If we need to match a 1 char length string do it simply
        if ((rend - rpos) == 1) {
            // Search for the specified character
            for (int x = d.length - 1; x > dpos; x--) {
                if (r[rpos] == d[x]) {
                    return (x);
                }
            }
        }

        // Main string matching loop. It gets executed if the characters to
        // match are less then the characters left in the d buffer
        int l = d.length - (rend - rpos);

        while (l >= dpos) {
            // Set current startpoint in d
            int y = l;

            // Check every character in d for equity. If the string is matched
            // return dpos
            for (int x = rpos; x <= rend; x++) {
                if (x == rend) {
                    return (l);
                }

                if (r[x] != d[y++]) {
                    break;
                }
            }

            // Decrease l to search for the same string at next offset
            l--;
        }

        // The remaining chars in d buffer were not enough or the string
        // wasn't matched
        return (-1);
    }

    /**
     * Matches elements of array r from rpos to rend with array d, starting from
     * dpos. <br>
     * This method return true if elements of array r from rpos to rend equals
     * elements of array d starting from dpos to dpos+(rend-rpos).
     *
     * @param r The array containing the data that need to be matched in d.
     * @param rpos The index of the first character in r to look for.
     * @param rend The index of the last character in r to look for.
     * @param d The array of char that should start from a part of r.
     * @param dpos The starting offset in d for the matching.
     * @return true if array d starts from portion of array r.
     * @since 2.1.0
     */
    protected boolean matchArray(int[] r, int rpos, int rend, char[] d, int dpos) {
        if ((d.length - dpos) < (rend - rpos)) {
            return (false);
        }

        for (int i = rpos; i < rend; i++) {
            if (r[i] != d[dpos++]) {
                return (false);
            }
        }

        return (true);
    }

    /**
     * <p>
     * Inserts into a value wildcard-matched strings where specified.
     * </p>
     *
     * @param val The value to convert
     * @param vars A Map of wildcard-matched strings
     * @return The new value
     * @since 2.1.0
     */
    public static String convertParam(String val, Map<Integer, String> vars) {
        if (val == null) {
            return null;
        } else if (!val.contains("{")) {
            return val;
        }

        Map.Entry<Integer, String> entry;
        StringBuilder key = new StringBuilder("{0}");
        StringBuilder ret = new StringBuilder(val);
        String keyTmp;
        int x;

        for (Map.Entry<Integer, String> integerStringEntry : vars.entrySet()) {
            entry = integerStringEntry;
            key.setCharAt(1, entry.getKey().toString().charAt(0));
            keyTmp = key.toString();

            // Replace all instances of the placeholder
            while ((x = ret.toString().indexOf(keyTmp)) > -1) {
                ret.replace(x, x + PLACEHOLDER_LENGTH, entry.getValue());
            }
        }

        return ret.toString();
    }

    /**
     * Adds and object to a list. If the list is null, it creates it.
     *
     * @param <T> The type of the element.
     * @param list The list.
     * @param data The data to add.
     * @return The list itself, or a new one if it is <code>null</code>.
     */
    private <T> List<T> addAndCreateList(List<T> list, T data) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(data);
        return list;
    }

    /**
     * Adds and object on top of a list. If the list is null, it creates it.
     *
     * @param <T> The type of the element.
     * @param list The list.
     * @param data The data to add.
     * @return The list itself, or a new one if it is <code>null</code>.
     */
    private <T> List<T> addElementOnTop(List<T> list, T data) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(0, data);
        return list;
    }
}
