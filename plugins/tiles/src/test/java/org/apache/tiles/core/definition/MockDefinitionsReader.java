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
package org.apache.tiles.core.definition;

import org.apache.tiles.api.Definition;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mock Definitions Reader implementation.  Stubs out all functionality.
 */
public class MockDefinitionsReader implements DefinitionsReader {

    /**
     * Reads <code>{@link Definition}</code> objects from a source.
     * Implementations should publish what type of source object is expected.
     *
     * @param source The source from which definitions will be read.
     * @return a Map of <code>Definition</code> objects read from
     * the source.
     * @throws org.apache.tiles.core.definition.DefinitionsFactoryException if the source is invalid or
     *                                                                      an error occurs when reading definitions.
     */
    public Map<String, Definition> read(Object source) {
        return new LinkedHashMap<>();
    }
}
