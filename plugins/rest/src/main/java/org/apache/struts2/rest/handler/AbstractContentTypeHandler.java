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

package org.apache.struts2.rest.handler;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

abstract public class AbstractContentTypeHandler implements ContentTypeHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractContentTypeHandler.class);

    public void toObject(Reader in, Object target) throws IOException {
        LOG.warn("This method is deprecated!");
    }

    public String fromObject(Object obj, String resultCode, Writer stream) throws IOException {
        LOG.warn("This method is deprecated!");
        return null;
    }

}
