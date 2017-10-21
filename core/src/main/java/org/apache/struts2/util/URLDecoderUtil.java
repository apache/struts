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

import org.apache.struts2.util.tomcat.buf.UDecoder;

/**
 * URLDecoderUtil serves as a facade for a correct URL decoding implementation.
 * As of Struts 2.3.25 it uses Tomcat URLDecoder functionality rather than the one found in java.io.
 */
public class URLDecoderUtil {

    /**
     * Decodes a <code>x-www-form-urlencoded</code> string.
     * @param sequence the String to decode
     * @param charset The name of a supported character encoding.
     * @return the newly decoded <code>String</code>
     * @exception IllegalArgumentException If the encoding is not valid
     */
    public static String decode(String sequence, String charset) {
        return UDecoder.URLDecode(sequence, charset);
    }

    /**
     * Decodes a <code>x-www-form-urlencoded</code> string.
     * @param sequence the String to decode
     * @param charset The name of a supported character encoding.
     * @param isQueryString whether input is a query string. If <code>true</code> other decoding rules apply.
     * @return the newly decoded <code>String</code>
     * @exception IllegalArgumentException If the encoding is not valid
     */
    public static String decode(String sequence, String charset, boolean isQueryString) {
        return UDecoder.URLDecode(sequence, charset, isQueryString);
    }

}
