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
package org.apache.tiles.freemarker.template;

public class TilesFMModelRepository {

    /**
     * The "putAttribute" directive.
     */
    private final PutAttributeFMModel putAttribute;

    /**
     * The "definition" directive.
     */
    private final DefinitionFMModel definition;

    /**
     * Constructor.
     */
    public TilesFMModelRepository() {
        putAttribute = new PutAttributeFMModel(new org.apache.tiles.template.PutAttributeModel());
        definition = new DefinitionFMModel(new org.apache.tiles.template.DefinitionModel());
    }

    /**
     * Returns the "putAttribute" directive.
     *
     * @return The "putAttribute" directive.
     */
    public PutAttributeFMModel getPutAttribute() {
        return putAttribute;
    }

    /**
     * Returns the "definition" directive.
     *
     * @return The "definition" directive.
     */
    public DefinitionFMModel getDefinition() {
        return definition;
    }

}