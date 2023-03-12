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
package org.apache.struts2.url;

import java.io.Serializable;

/**
 * URL Decoder used internally by Struts
 * @since Struts 6.1.0
 */
public interface UrlDecoder extends Serializable {

    /**
     * Decodes the input using default encoding, e.g.: struts.i18n.encoding
     *
     * @param input String to decode
     * @param encoding encoding used in decoding
     * @param isQueryString indicates if input is a query string
     * @return the decoded string
     */
    String decode(String input, String encoding, boolean isQueryString);

    /**
     * Decodes the input using default encoding, e.g.: struts.i18n.encoding
     *
     * @param input String to decode
     * @param isQueryString indicates if input is a query string
     * @return the decoded string
     */
    String decode(String input, boolean isQueryString);

    /**
     * Decodes the input using default encoding, e.g.: struts.i18n.encoding
     *
     * @param input String to decode
     * @return the decoded string
     */
    String decode(String input);

}
