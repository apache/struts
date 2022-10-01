/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.autotag.core.runtime;

import java.io.IOException;
import java.io.Writer;

/**
 * Abstracts a tag/directive body.
 */
public interface ModelBody {

    /**
     * Evaluates a body and returns it as a string.
     *
     * @return The body, as a string.
     * @throws IOException If something goes wrong.
     */
    String evaluateAsString() throws IOException;

    /**
     * Evaluates a body, but discards result.
     *
     * @throws IOException If something goes wrong.
     */
    void evaluateWithoutWriting() throws IOException;

    /**
     * Evaluates the body and writes in the default writer.
     *
     * @throws IOException If something goes wrong.
     */
    void evaluate() throws IOException;

    /**
     * Evaluates the body and writes the result in the writer.
     *
     * @param writer The writer to write the result into.
     * @throws IOException If something goes wrong.
     */
    void evaluate(Writer writer) throws IOException;
}
