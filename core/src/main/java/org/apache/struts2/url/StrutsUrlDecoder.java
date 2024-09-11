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

import org.apache.struts2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.StrutsConstants;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;

public class StrutsUrlDecoder implements UrlDecoder {

    private static final Logger LOG = LogManager.getLogger(StrutsUrlDecoder.class);

    private static final Collection<Charset> AVAILABLE_CHARSETS = Charset.availableCharsets().values();

    private String encoding = "UTF-8";

    @Inject(value = StrutsConstants.STRUTS_I18N_ENCODING, required = false)
    public void setEncoding(String encoding) {
        LOG.debug("Using default encoding: {}", encoding);
        if (StringUtils.isNotEmpty(encoding)) {
            this.encoding = encoding;
        }
    }

    @Override
    public String decode(String input, String encoding, boolean isQueryString) {
        if (input == null) {
            return (null);
        }
        byte[] bytes = null;
        try {
            bytes = input.getBytes(getCharset(encoding));
        } catch (UnsupportedEncodingException uee) {
            LOG.debug(new ParameterizedMessage("Unable to URL decode the specified input since the encoding: {} is not supported.", encoding), uee);
        }

        return internalDecode(bytes, encoding, isQueryString);
    }

    @Override
    public String decode(String input, boolean isQueryString) {
        return this.decode(input, this.encoding, isQueryString);
    }

    @Override
    public String decode(String input) {
        return decode(input, false);
    }

    private String internalDecode(byte[] bytes, String encoding, boolean isQuery) {
        if (bytes == null) {
            return null;
        }

        int len = bytes.length;
        int ix = 0;
        int ox = 0;
        while (ix < len) {
            byte b = bytes[ix++];     // Get byte to test
            if (b == '+' && isQuery) {
                b = (byte) ' ';
            } else if (b == '%') {
                if (ix + 2 > len) {
                    throw new IllegalArgumentException("The % character must be followed by two hexadecimal digits");
                }
                b = (byte) ((((convertHexDigit(bytes[ix++]) << 4) & 0xff) + convertHexDigit(bytes[ix++]) & 0xff) & 0xff);
            }
            bytes[ox++] = b;
        }
        if (encoding != null) {
            try {
                return new String(bytes, 0, ox, getCharset(encoding));
            } catch (UnsupportedEncodingException uee) {
                LOG.debug(new ParameterizedMessage("Unable to URL decode the specified input since the encoding: {} is not supported.", encoding), uee);
                return null;
            }
        }
        return new String(bytes, 0, ox);

    }

    private byte convertHexDigit(byte b) {
        if ((b >= '0') && (b <= '9')) return (byte) (b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte) (b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte) (b - 'A' + 10);
        throw new IllegalArgumentException(((char) b) + " is not a hexadecimal digit");
    }

    private Charset getCharset(String encoding) throws UnsupportedEncodingException {
        for (Charset charset : AVAILABLE_CHARSETS) {
            if (encoding.equalsIgnoreCase(charset.name())) {
                return charset;
            }
        }
        throw new UnsupportedEncodingException("The character encoding " + encoding + " is not supported");
    }

}
