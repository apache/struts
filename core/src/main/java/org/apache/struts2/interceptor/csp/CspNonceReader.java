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
package org.apache.struts2.interceptor.csp;

import org.apache.struts2.util.ValueStack;

/**
 * Reads the nonce value using the ValueStack, {@link StrutsCspNonceReader} is the default implementation
 * @since 6.8.0
 */
public interface CspNonceReader {

    NonceValue readNonceValue(ValueStack stack);

    class NonceValue {
        private final String nonceValue;
        private final CspNonceSource source;

        private NonceValue(String nonceValue, CspNonceSource source) {
            this.nonceValue = nonceValue;
            this.source = source;
        }

        public static NonceValue ofSession(String nonceValue) {
            return new NonceValue(nonceValue, CspNonceSource.SESSION);
        }

        public static NonceValue ofRequest(String nonceValue) {
            return new NonceValue(nonceValue, CspNonceSource.REQUEST);
        }

        public static NonceValue ofNullSession() {
            return new NonceValue(null, CspNonceSource.REQUEST);
        }

        public static NonceValue ofNullRequest() {
            return new NonceValue(null, CspNonceSource.REQUEST);
        }

        public boolean isNonceValueSet() {
            return nonceValue != null;
        }

        public String getNonceValue() {
            return nonceValue;
        }

        public CspNonceSource getSource() {
            return source;
        }

        @Override
        public String toString() {
            return "NonceValue{" +
                    String.format("nonceValue='%s**********'", nonceValue.substring(0, 4)) +
                    ", source=" + source +
                    '}';
        }
    }
}
