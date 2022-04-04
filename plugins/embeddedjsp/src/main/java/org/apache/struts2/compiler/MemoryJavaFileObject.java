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
package org.apache.struts2.compiler;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Captures the output of the java compiler in memory
 */
public class MemoryJavaFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream out;

    public MemoryJavaFileObject(String name, JavaFileObject.Kind kind) {
        super(toURI(name), kind);
    }

    public InputStream openInputStream() throws IOException,
            IllegalStateException, UnsupportedOperationException {
        return new ByteArrayInputStream(out.toByteArray());
    }

    public OutputStream openOutputStream() throws IOException,
            IllegalStateException, UnsupportedOperationException {
        return out = new ByteArrayOutputStream();
    }

    private static URI toURI(String name) {
        try {
            return new URI(name);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] toByteArray() {
        return out.toByteArray();
    }
}
