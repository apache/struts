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
import org.apache.struts2.StrutsConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StrutsUrlEncoder implements UrlEncoder {

    private static final Logger LOG = LogManager.getLogger(StrutsUrlEncoder.class);

    private String encoding = "UTF-8";

    @Inject(value = StrutsConstants.STRUTS_I18N_ENCODING, required = false)
    public void setEncoding(String encoding) {
        LOG.debug("Using default encoding: {}", encoding);
        if (StringUtils.isNotEmpty(encoding)) {
            this.encoding = encoding;
        }
    }

    @Override
    public String encode(String input, String encoding) {
        try {
            return URLEncoder.encode(input, encoding);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Could not encode URL parameter '{}', returning value un-encoded", input);
            return input;
        }
    }

    @Override
    public String encode(String input) {
        return encode(input, encoding);
    }
}
