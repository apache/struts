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

package org.apache.struts2.sitegraph.model;

import java.io.IOException;
import java.io.Writer;

/**
 */
public class IndentWriter extends Writer {
    Writer writer;

    public IndentWriter(Writer writer) {
        this.writer = writer;
    }

    public void close() throws IOException {
        writer.close();
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void write(String str) throws IOException {
        write(str, false);
    }

    public void write(String str, boolean noIndent) throws IOException {
        if (!noIndent) {
            str = "    " + str;
        }

        if (writer instanceof IndentWriter) {
            ((IndentWriter) writer).write(str, false);
        } else {
            writer.write(str + "\n");
        }
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        writer.write(cbuf, off, len);
    }
}
