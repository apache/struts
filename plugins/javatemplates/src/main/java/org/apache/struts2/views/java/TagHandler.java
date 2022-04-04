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
package org.apache.struts2.views.java;

import org.apache.struts2.components.template.TemplateRenderingContext;

import java.io.IOException;

public interface TagHandler {

    void setNext(TagHandler next);

    void setup(TemplateRenderingContext context);

    /**
     * Write a tag openening, with its attributes
     * @param name name of the tag
     * @param attributes attributes of the tag
     * @throws IOException
     */
    void start(String name, Attributes attributes) throws IOException;

    /**
     * Writes a tag close
     * @param name name of the tag
     * @throws IOException
     */
    void end(String name) throws IOException;

    /**
     * Writes to the inner text of a tag. By default the body is html encoded
     * @param text tag body.
     * @throws IOException
     */
    void characters(String text) throws IOException;

    /**
     * Writes to the inner text of a tag
     * @param text tag body
     * @param encode html encode the body
     * @throws IOException
     */
    void characters(String text, boolean encode) throws IOException;
}
