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
 * URL Encoder used internally by Struts
 * @since Struts 6.1.0
 */
public interface UrlEncoder extends Serializable {

    /**
     * Encodes the input tb be used with URL using the provided encoding
     *
     * @param input String to encode
     * @param encoding encoding to use
     * @return encoded string
     */
    String encode(String input, String encoding);

    /**
     * Encodes the input to be used with URL using default encoding, e.g.: struts.i18n.encoding
     *
     * @param input String to encode
     * @return encoded string
     */
    String encode(String input);

}
