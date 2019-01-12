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

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.juneau.parser.ParseException;
import org.apache.juneau.serializer.SerializeException;
import org.apache.juneau.xml.XmlDocSerializer;
import org.apache.juneau.xml.XmlParser;
import org.apache.juneau.xml.XmlSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

/**
 * Handles XML content using Apache Juneau
 * http://juneau.apache.org/#marshall.html
 */
public class JuneauXmlHandler extends AbstractContentTypeHandler {

    private static final Logger LOG = LogManager.getLogger(JuneauXmlHandler.class);

    private static final String DEFAULT_CONTENT_TYPE = "application/xml";

    private final XmlParser parser = XmlParser.DEFAULT;
    private final XmlSerializer serializer = XmlDocSerializer.DEFAULT;

    public void toObject(ActionInvocation invocation, Reader in, Object target) throws IOException {
        LOG.debug("Converting input into an object of: {}", target.getClass().getName());
        try {
            Object result = parser.parse(in, target.getClass());
            BeanUtils.copyProperties(target, result);
        } catch (ParseException | IllegalAccessException | InvocationTargetException e) {
            throw new IOException(e);
        }
    }

    public String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer stream) throws IOException {
        LOG.debug("Converting an object of {} into string", obj.getClass().getName());
        try {
            serializer
                .builder()
                .locale(invocation.getInvocationContext().getLocale())
                .build()
                .serialize(obj, stream);
            return null;
        } catch (SerializeException e) {
            throw new IOException(e);
        }
    }

    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    public String getExtension() {
        return "xml";
    }

}
