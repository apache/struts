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

import org.apache.tiles.api.TilesException;

/**
 * Exception thrown when an error occurs while the impl tries to
 * create a new instance mapper.
 */
public class DefinitionsFactoryException extends TilesException {

    /**
     * Constructor.
     */
    public DefinitionsFactoryException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param message The error or warning message.
     */
    public DefinitionsFactoryException(String message) {
        super(message);
    }


    /**
     * Create a new <code>DefinitionsFactoryException</code> wrapping an existing exception.
     * <p/>
     * <p>The existing exception will be embedded in the new
     * one and its message will become the default message for
     * the DefinitionsFactoryException.</p>
     *
     * @param e The exception to be wrapped.
     */
    public DefinitionsFactoryException(Throwable e) {
        super(e);
    }


    /**
     * Create a new <code>DefinitionsFactoryException</code> from an existing exception.
     * <p/>
     * <p>The existing exception will be embedded in the new
     * one, but the new exception will have its own message.</p>
     *
     * @param message The detail message.
     * @param e       The exception to be wrapped.
     */
    public DefinitionsFactoryException(String message, Throwable e) {
        super(message, e);
    }
}
