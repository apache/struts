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
package org.apache.struts2.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for test classes
 */
public class TestUtils {

    /**
     * A regex pattern for recognizing blocks of whitespace characters.
     */
    private static final Pattern WHITESPACE_BLOCK = Pattern.compile("\\s+");

    /**
     * normalizes a string so that strings generated on different platforms can
     * be compared. any group of one or more space, tab, \r, and \n characters
     * are converted to a single space character
     *
     * @param json         the JSON to be normalized. normalize will trim before starting
     * @param removeSpaces removes white spaces from the JSON or not
     * @return the normalized string
     */
    public static String normalize(String json, boolean removeSpaces) {
        Matcher matcher = WHITESPACE_BLOCK.matcher(StringUtils.trim(json));
        if (removeSpaces) {
            return matcher.replaceAll("").replaceAll(" ", "");
        }
        return matcher.replaceAll("");
    }

    public static String normalize(URL url) throws Exception {
        return normalize(readContent(url), true);
    }

    /**
     * Attempt to verify the contents of text against the contents of the URL
     * specified. Performs a trim on both ends
     *
     * @param url the HTML snippet that we want to validate against
     * @throws Exception if the validation failed
     */
    public static boolean compare(URL url, String text) throws Exception {
        /*
          compare the trimmed values of each buffer and make sure they're
          equivalent. however, let's make sure to normalize the strings first
          to account for line termination differences between platforms.
         */
        String writerString = TestUtils.normalize(text, true);
        String bufferString = TestUtils.normalize(readContent(url), true);

        return bufferString.equals(writerString);
    }

    public static void assertEquals(URL source, String text) throws Exception {
        String writerString = TestUtils.normalize(text, true);
        String bufferString = TestUtils.normalize(readContent(source), true);
        Assert.assertEquals(bufferString, writerString);
    }

    public static String readContent(URL url) throws Exception {
        return readContent(url, StandardCharsets.UTF_8);
    }

    public static String readContent(URL url, Charset encoding) throws Exception {
        if (url == null) {
            throw new IllegalArgumentException("Unable to verify a null URL");
        }

        if (encoding == null) {
            throw new IllegalArgumentException("Unable to verify the URL using a null Charset");
        }

        return IOUtils.toString(url.openStream(), encoding);
    }

}
