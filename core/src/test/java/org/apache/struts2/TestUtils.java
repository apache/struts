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

package org.apache.struts2;

import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Utility methods for test classes
 *
 */
public class TestUtils {
    /**
     * normalizes a string so that strings generated on different platforms can be compared.  any group of one or more
     * space, tab, \r, and \n characters are converted to a single space character
     *
     * @param obj the object to be normalized.  normalize will perform its operation on obj.toString().trim() ;
     * @param appendSpace
     * @return the normalized string
     */
    public static String normalize(Object obj, boolean appendSpace) {
        StringTokenizer st =
            new StringTokenizer(obj.toString().trim(), " \t\r\n");
        StringBuilder buffer = new StringBuilder(128);

        while(st.hasMoreTokens()) {
            buffer.append(st.nextToken());
        }

        return buffer.toString();
    }

    
    public static String normalize(URL url) throws Exception {
        return normalize(readContent(url), true);
    }
    /**
     * Attempt to verify the contents of text against the contents of the URL specified. Performs a
     * trim on both ends
     *
     * @param url the HTML snippet that we want to validate against
     * @throws Exception if the validation failed
     */
    public static boolean compare(URL url, String text)
        throws Exception {
        /**
         * compare the trimmed values of each buffer and make sure they're equivalent.  however, let's make sure to
         * normalize the strings first to account for line termination differences between platforms.
         */
        String writerString = TestUtils.normalize(text, true);
        String bufferString = TestUtils.normalize(readContent(url), true);

        return bufferString.equals(writerString);
    }
    
    

    public static String readContent(URL url)
        throws Exception {
        if(url == null) {
            throw new Exception("unable to verify a null URL");
        }

        StringBuilder buffer = new StringBuilder(128);
        InputStream in = url.openStream();
        byte[] buf = new byte[4096];
        int nbytes;

        while((nbytes = in.read(buf)) > 0) {
            buffer.append(new String(buf, 0, nbytes));
        }

        in.close();

        return buffer.toString();
    }
}
